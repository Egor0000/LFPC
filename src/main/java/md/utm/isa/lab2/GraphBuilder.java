package md.utm.isa.lab2;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

public class GraphBuilder {

    public void buildGraph(FiniteAutomaton fa, String name){
        MultiGraph faGraph = initGraph(name);
        setGraph(faGraph, fa);
    }

    private MultiGraph initGraph(String graphId){
        MultiGraph graph = new MultiGraph(graphId);
        graph.setAutoCreate(true);
        graph.setStrict(false);

        System.setProperty("org.graphstream.ui", "swing");
        graph.setAttribute("ui.quality");
        graph.setAttribute("ui.antialias");
        graph.setAttribute("ui.stylesheet", "node { size:15px; text-padding: 30px; text-style: bold; text-offset: -10px, -10px;}" +
                " node:clicked {text-size:30px; text-color:red; } node#0{size:1px;}" +
                " edge {text-size:15px; text-style:bold; text-offset: 30px, 00px;}" +
                "sprite{fill-mode:none; text-style:bold; text-size:15px;} node.endPoint{stroke-mode: plain; stroke-width: 3px; stroke-color: red;}");

        graph.display();

        return graph;
    }

    private void setGraph(Graph graph, FiniteAutomaton fa){
        boolean concatInnerState = fa.concatInnerState();

        for (State state: fa.keySet()){
            Transition transition = fa.get(state);
            String outerStateString = fa.stateToString(state);

            for (String letter: transition.keySet()){
                State innerState = transition.get(letter);
                if(innerState.isEmpty()){
                    continue;
                }

                if(!concatInnerState){
                    for(String s: innerState){
                        buildEdge(graph, outerStateString, letter, s);
                    }
                } else {
                    String innerStateString = fa.stateToString(innerState);
                    buildEdge(graph, outerStateString, letter, innerStateString);
                }
            }
        }
        for (Node node : graph) {
            if(!node.getId().equals("0")) {
                node.setAttribute("ui.label", node.getId());
            }
        }

        for (State finalState: fa.getFinalStates()){
            graph.getNode(fa.stateToString(finalState)).setAttribute("ui.class", "endPoint");
        }

    }

    private void addEdgeLabel(Graph graph, String edgeId, String label){
        SpriteManager sm = new SpriteManager(graph);

        Sprite s = sm.addSprite(edgeId);
        s.setAttribute("ui.label", label);

        s.attachToEdge(edgeId);
        s.setPosition(0.5);
    }

    private String generateEdgeId(String from, String label, String to){
        return String.format("%s_%s_%s", from, label, to);
    }

    private void buildEdge(Graph graph, String from, String label, String to){
        String edgeId = generateEdgeId(from, label, to);
        graph.addEdge(edgeId, from, to, true);

        addEdgeLabel(graph, edgeId, label);
    }
}
