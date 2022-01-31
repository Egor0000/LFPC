package md.utm.isa.lab1;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class GraphBuilder {
    private String file;

    public GraphBuilder(String file){
        this.file = file;
    }
    public void drawGraph() throws Exception{
        Graph graph = new MultiGraph("Laboratory 1");

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

        ArrayList<MyPair> edges = getGraphNodes(file);
        graph.addNode("0");

        for(int i =0; i<edges.size(); i++){
            MyPair myPair  = edges.get(i);
            if(i==0){
                graph.addEdge("00", "0", myPair.key());
            }
            ArrayList<String> secondNodes = getEdgeVal(myPair.key(), myPair.value(), String.valueOf(i));
            addEdge(graph, "Q"+String.valueOf(i), myPair.key(), secondNodes.get(0), secondNodes.get(1));
            if(myPair.value().length()==1){
                graph.getNode(secondNodes.get(0)).setAttribute("ui.class", "endPoint");
            }
        }

        for (Node node : graph) {
            if(!node.getId().equals("0")) {
                node.setAttribute("ui.label", node.getId());
            }
        }

    }

    public static void addEdge(Graph graph, String id, String from, String to, String label){
        Edge e = graph.addEdge(id, from, to, true);

        SpriteManager sm = new SpriteManager(graph);

        Sprite s = sm.addSprite(id);
        s.setAttribute("ui.label", label);

        s.attachToEdge(id);
        s.setPosition(0.5);

    }

    private ArrayList<MyPair> getGraphNodes(String filePath) throws Exception{
        File file = new File(filePath);
        Scanner reader = new Scanner(file);

        LinkedHashMap<String, String> graphParts = new LinkedHashMap<>();
        ArrayList<MyPair> edges = new ArrayList<>();

        while (reader.hasNextLine()){
            String line = reader.nextLine();
            ArrayList<String> nodes = getNodesFromLine(line);
            if(nodes.size()==0){
                continue;
            }
            MyPair myPair = new MyPair(nodes.get(0), nodes.get(1));
            edges.add(myPair);
        }

        return edges;
    }

    private ArrayList<String> getNodesFromLine(String line){
        ArrayList<String> nodes = new ArrayList<>();
        if (line.contains("->")){
            String[] lineParts = line.split("->");
            String[] firstPart = lineParts[0].split("\\.");
            nodes.add(firstPart[1].replace(" ", ""));
            nodes.add(lineParts[1].replace(" ", ""));
        }
        return nodes;
    }

    private ArrayList<String> getEdgeVal(String firstNode, String edgeVal, String id) throws Exception {
        int edgeLength = edgeVal.length();
        ArrayList<String> secondNode = new ArrayList<>();
        if(edgeLength<1)
            throw new Exception("Empty strings are not allowed");
        if(edgeLength==1 && Character.isLowerCase(edgeVal.charAt(0))){
            secondNode.add("Q"+id);
            secondNode.add(edgeVal.substring(0, 1));
        }else if(edgeLength==1 && Character.isUpperCase(edgeVal.charAt(0))){
            secondNode.add(edgeVal.substring(0, 1));
            secondNode.add("");
        }

        if(edgeLength==2){
            secondNode.add(edgeVal.substring(1, 2));
            secondNode.add(edgeVal.substring(0, 1));
        }
        return secondNode;
    }

}
