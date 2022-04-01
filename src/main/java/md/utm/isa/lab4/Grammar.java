package md.utm.isa.lab4;

import lombok.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
public class Grammar {
    private Set<String> terminals = new LinkedHashSet<>();
    private Set<String> nonTerminals = new LinkedHashSet<>();
    private String initialSymbol;
    private Set<Production> productions = new LinkedHashSet<>();
    private Set<Production> emptyProductions = new HashSet<>();
    private Set<Production> renamings = new HashSet<>();
    private Set<State> accessibleStates = new HashSet<>();
    private Set<State> productiveStates = new HashSet<>();
    private Set<String> finalNonTerminals= new LinkedHashSet<>();
    private Set<State> finalTerminals= new LinkedHashSet<>();
    private Set<Production> finalChomsky = new LinkedHashSet<>();

//    private LinkedHashMap<Left, Right> productions = new LinkedHashMap<>();

    public void readDefinition(String line) {
        List<String> definition = Arrays.asList(line.split("VN"));
        List<String> splitDef = Arrays.asList(definition.get(2).split("VT"));
        List<String> nonTerminals = Arrays.asList(splitDef.get(0).split("[^A-Za-z]"));
        List<String> terminals = Arrays.asList(splitDef.get(1).split("[^A-Za-z]"));
        nonTerminals.forEach(nt -> {
            if (!nt.equals("")){
                this.nonTerminals.add(nt);
            }
        });

        terminals.forEach(t -> {
            if (!t.equals("")){
                this.terminals.add(t);
            }
        });

        initialSymbol = Arrays.asList(definition.get(1)
                .split(",")).get(3)
                .replaceAll("[^A-Za-z]", "");
    }

    public void readProduction(String line){
        String production = line.split("[.]")[1];
        List<String> parts = Arrays.asList(production.split("[?]", 2));

        Left leftPart = new Left(parts.get(0).trim());
        Right rightPart = new Right(parts.get(1).trim());

        Production prod = new Production(leftPart, rightPart);

        productions.add(prod);
    }

    public boolean removeEmptyProductions(){
        emptyProductions = productions.stream()
                .filter(Production::hasEmptyState)
                .collect(Collectors.toSet());

        return productions.removeIf(production -> emptyProductions.contains(production));
    }

    public void addMissingProductions(){
        // todo continue here
        LinkedHashSet<Production> tempProduction = new LinkedHashSet<>();
        // Iterate through each element in right part list
        emptyProductions.forEach(emptyProd -> {
            String emptyString = emptyProd.getLeftPart().getState().getValue();
            productions.forEach(prod -> {
                List<Integer> emptyIndexes = prod.getRightPart().allEmptyIndexes(emptyString);

                List<HashSet<Integer>> emptyIndexesList = new ArrayList<>();

                generateEmptyIndexes(emptyIndexes, emptyIndexesList, 0, 0);


                // add production with removed empty generating states
                emptyIndexesList.forEach(list -> {
                    Production modifedProduction = addNonEmptyProductions(prod, list);

                    //add empty state if appeared
                    if (modifedProduction.getRightPart().getStates().size() == 0){
                        modifedProduction.getRightPart().getStates().add(new State("?", true, false, true));
                    }
                    tempProduction.add(modifedProduction);
                });
            });
        });
        this.productions.addAll(tempProduction);
        // Determine if it is in the empty states list
        // If yes, remove all occurences and its combinations
    }

    // get all combinations of empty state indexes array that should replaced
    public void generateEmptyIndexes(List<Integer> emptyIndexes, List<HashSet<Integer>> emptyIndexesList, int start, int end){
        // Stop if we have reached the end of the array
        if (end == emptyIndexes.size()) {
            return;
        } else if (start > end) {
            generateEmptyIndexes( emptyIndexes, emptyIndexesList, 0, end + 1);
        } else {
            HashSet<Integer> tempList = new HashSet<>();
            for (int i = start; i < end; i++){
                tempList.add(emptyIndexes.get(i));
            }
            tempList.add(emptyIndexes.get(end));
            emptyIndexesList.add(tempList);

            generateEmptyIndexes( emptyIndexes, emptyIndexesList, start+1, end);
        }
        return;
    }

    public void eliminateRenaming() throws Exception {
        renamings = new HashSet<>();

        List<Production> modifedRenamings = new ArrayList<>();
        productions.forEach(production -> {
            if (production.getRightPart().getStates().size() == 1
                    && production.getRightPart().getStates().get(0).isNonTerminal()){
                State renaming = production.getRightPart().getStates().get(0);

                // catch loops
                if (production.getLeftPart().getState().equals(renaming)){
                    try {
                        throw new Exception(String.format("Entered loop %s -> %s", production.getLeftPart(), renaming));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // add to renaming set
                this.renamings.add(production);
//                this.accessibleStates.removeIf(state -> {
//                    return state.equals(production.getLeftPart().getState());
//                });

                // remove renaming
                productions.forEach(childProd -> {
                    if (childProd.getLeftPart().getState().getValue()
                            .equals((production.getRightPart().getStates().get(0).getValue()))){
                        modifedRenamings.add(new Production(production.getLeftPart(), childProd.getRightPart()));
                    }
//                    accessibleStates.addAll(childProd.getRightPart().getStates());
                });

            }
        });

        productions.addAll(modifedRenamings);
        productions.removeAll(renamings);
    }

    public void eliminateNonProductive() {
        Set<State> tempProductive;
        do {
            tempProductive = new HashSet<>();

            for (Production production: productions){
                if (productiveStates.contains(production.getLeftPart().getState())) {
                    continue;
                }
                boolean productive = true;
                for (State state: production.getRightPart().getStates()){

                    if ((state.isNonTerminal() && !productiveStates.contains(state)) || state.isEmpty()){
                        productive = false;
                        break;
                    }
                }
                if (productive){
                    tempProductive.add(production.getLeftPart().getState());
                    break;
                }
            }

            productiveStates.addAll(tempProductive);
        } while (!tempProductive.isEmpty());

        // do until no more production can be visited
        productions.removeIf(production -> !productiveStates.contains(production.getLeftPart().getState())
                ||isNonProductiveRight(production)
        );
    }

    public void convertToChomskyGrammar() {

        // filter productions that satisfy Chomsky Form
         Set<Production> tempProductions = productions.stream().filter(production -> {
             List<State> rightStates =  production.getRightPart().getStates();
             return (rightStates.size()==2 && rightStates.stream().filter(State::isNonTerminal).count() == 2)
                     || (rightStates.size() == 1 && rightStates.get(0).isTerminal());
         }).collect(Collectors.toSet());

         // add them to final grammar
         finalChomsky.addAll(tempProductions);

         Set<Production> targetProductions = productions.stream().filter(prod -> !tempProductions.contains(prod)).collect(Collectors.toSet());

//         // count all possible state combinations
//        Map<String, Integer> stateCountTable = countSubstrings();
//        addTerminalsToGrammar();


        Set<Production> newProds;
        Map<String, Integer> stateCountTable = countSubstrings(productions);

        // remove all terminals
        newProds = addTerminalsToGrammar();
        newProds.forEach(production -> {
            replaceAllLeftSymbols(targetProductions,
                    production.getRightPart().getStates().get(0).getValue(), production.getLeftPart().getState().getValue());
            Left finalLeft = new Left(production.getLeftPart().getState().getValue());
            finalNonTerminals.add(production.getLeftPart().getState().getValue());
            // add new production of form Y -> a
            tempProductions.add(new Production(finalLeft,
                    new Right(production.getRightPart().getStates().get(0).getValue())));
        });

        // count all non-terminals
        // final non terminal
        int fnt = 0;
        do {
            stateCountTable = countSubstrings(targetProductions);
            Optional<Map.Entry<String, Integer>> maxKeyOpt;

            do {
                 maxKeyOpt = stateCountTable.entrySet().stream()
                        .max(Comparator.comparing(x -> x.getValue()));
                 if (maxKeyOpt.isEmpty()) {
                     break;
                 }

                int largest = stateCountTable.values().stream()
                        .mapToInt(i -> i)
                        .max()
                        .orElse(-1);
                List<String> largestList = stateCountTable.entrySet().stream()
                        .filter(s -> s.getValue() == largest)
                        .map(x -> x.getKey())
                        .collect(Collectors.toList());

                Optional<String> longest = largestList.stream().max(Comparator.comparing(String::length));

//                String maxKey = maxKeyOpt.get().getKey();
                if (longest.isEmpty()) {
                    break;
                }

                String maxKey = longest.get();


//                 if (maxKey.length() == 1) {
//                     break;
//                 }

                boolean isGrammarChanged = replaceAllRightSymbols(targetProductions, maxKey, "X"+fnt);
                if (isGrammarChanged){
                    // add new production of form X->YZ
                    String leftString = "X"+fnt;
                    tempProductions.add(new Production(new Left(leftString),
                            new Right(maxKey)));
                    finalNonTerminals.add(leftString);

                    fnt +=1;
                    break;
                }
                stateCountTable.remove(maxKey);
            } while (maxKeyOpt.isPresent());


            // todo bring all productions to form X -> YZ

        } while (!stateCountTable.isEmpty());

        finalChomsky = new HashSet<>();
        finalNonTerminals.addAll(accessibleStates.stream().map(x -> x.getValue()).collect(Collectors.toList()));
        finalChomsky.addAll(tempProductions);
        finalChomsky.addAll(targetProductions);
    }

    private boolean isNonProductiveRight(Production production){
        return production.getRightPart().getStates().stream()
                .anyMatch(state -> (state.isNonTerminal() && !productiveStates.contains(state)) || state.isEmpty());
    }


    public void visitStates(){
        this.accessibleStates = new HashSet<>();
        visitRecursively(new State(initialSymbol, false, true, false));
        this.productions.removeIf(production -> !accessibleStates.contains(production.getLeftPart().getState()));
    }

    private void visitRecursively(State current){
        this.accessibleStates.add(current);
        for (Production prod: productions){
            for (State state: prod.getRightPart().getStates()){
                if (state.isNonTerminal() && !accessibleStates.contains(state)){
                    visitRecursively(state);
                }
            }
        }
    }

    private Production addNonEmptyProductions(Production targetProduction, HashSet<Integer> indexToRemove){
        Production production = new Production();
        production.setLeftPart(targetProduction.getLeftPart());

        List<State> tempStates = IntStream.range(0, targetProduction.getRightPart().getStates().size())
                .filter(i -> !indexToRemove.contains(i))
                .mapToObj(i->targetProduction.getRightPart().getStates().get(i))
                .collect(Collectors.toList());

        production.setRightPart(new Right(tempStates));

        return production;
    }

    public List<String> getAllRights(Set<Production> productions){
        List<String> stringList = new ArrayList<>();

        for(Production production: productions) {
            List<State> states = production.getRightPart().getStates();
            String right = states.stream().map(State::getValue).collect(Collectors.joining());
            stringList.add(right);
        }

        return stringList;
    }

    // for debugging
    public String printAllRights(Set<Production> productions){
        String rights = "";

        for(Production production: productions) {
            List<State> states = production.getRightPart().getStates();
            String right = states.stream().map(State::getValue).collect(Collectors.joining());
            rights = rights.concat(right);
            rights += "\n";
        }

        return rights;
    }

    // count all combinations
    public HashMap<String, Integer> countSubstrings(Set<Production> productions){
        List<String> stringList = getAllRights(productions);
        HashMap<String, Integer> substringCounter = new HashMap<>();

        for (String s: stringList) {
            // if there is any number in right
            if (s.matches("(.*)[0-9](.*)") && !s.matches("(.*)[a-z](.*)")){
                String[] ss = s.split("((?<=(A-Za-z))|(?=[A-Za-z]))");;
                for (int i = 0; i < ss.length-1; i++) {
                    for (int j = i + 1; j < ss.length; j++) {
//                        if (j-i<=1){
//                            continue;
//                        }

                        String inner = "";
                        for (int k = i; k<=j; k++){
                            inner = inner.concat(ss[k]);
                        }

                        Integer count = substringCounter.get(inner);
                        count = (count==null)?1:(count+1);
                        substringCounter.put(inner, count);

                    }
                }
            } else {
                for (int i = 0; i < s.length()-1; i++) {
                    for (int j = i+1; j < s.length(); j++) {
                        String sstring = s.substring(i,j);
                        if ((sstring.length() ==1 && sstring.matches("[A-Za-z]"))
                                ){
                            continue;
                        }

                        // add final terminal states
                        // WOW! O(n^4)
                        addFinalTerminals(sstring);

                        Integer count = substringCounter.get(sstring);
                        count = (count==null)?1:(count+1);
                        substringCounter.put(s.substring(i,j), count);
                    }
                }
            }
        }
        return substringCounter;
    }

    public String printChomskyNormalForm(){
        String prods = "";
        for (Production production: finalChomsky){
            String prod = "";
            prod = String.format("%-2s -> %s", production.getLeftPart().getState().getValue(),
                    production.getRightPart().getStates().stream()
                            .map(State::getValue)
                            .collect(Collectors.joining()));
            prods = prods.concat(prod+"\n");
        }

        String result = String.format("NT = {%s}, \n P={\n%s}", finalNonTerminals.toString(), prods);
        return result;
    }

    private void addFinalTerminals(String string){
        string.chars().forEach(ch -> {
            String chVal = Character.toString(ch);
            if (chVal.matches("[a-z]")){
                finalTerminals.add(new State(chVal, true, false, false));
            }
        });
    }

    private Set<Production> addTerminalsToGrammar(){
        Set<String> presentNonTerminals = accessibleStates.stream()
                .map(State::getValue)
                .collect(Collectors.toSet());
        Set<Production> newProductions = new HashSet<>();

        finalTerminals.forEach(t -> {
            for (int i = 65; i<91; i++){
                String possibleSymbol = Character.toString(i);
                if (!presentNonTerminals.contains(possibleSymbol)){
                    presentNonTerminals.add(possibleSymbol);
                    Left left = new Left(possibleSymbol);
                    Right right = new Right(t.getValue());
                    Production newProduction = new Production(left, right);
                    finalChomsky.add(newProduction);
                    newProductions.add(newProduction);
                    break;
                }
            }
        });

        return newProductions;
    }

    private boolean replaceAllRightSymbols(Set<Production> targetProds, String target, String newSymbol){
        boolean isGrammarChanged = false;
        for (Production production: targetProds){
            String rightString = production.getRightPart().getStates().stream().map(State::getValue).collect(Collectors.joining());
            if (!rightString.contains(target) ||rightString.equals(target)) {
                continue;
            }
            rightString = rightString.replace(target, "-");
            if (rightString.split("((?<=(A-Za-z))|(?=[A-Za-z]))").length>1) {
                rightString = rightString.replace("-", newSymbol);
                production.setRightPart(new Right(rightString));
                isGrammarChanged = true;
            }
        }
        return isGrammarChanged;
    }

    // Replace all left symbols
    private void replaceAllLeftSymbols(Set<Production> targetProds, String target, String newSymbol){
        Set<Production> newProd = new HashSet<>();
        for (Production production: targetProds){
            String rightString = production.getRightPart().getStates().stream().map(State::getValue).collect(Collectors.joining());
            rightString = rightString.replace(target, newSymbol);
            production.setRightPart(new Right(rightString));
            newProd.add(production);
        }
    }

    //todo for refactoring: copy to a new object final chomsky


}
