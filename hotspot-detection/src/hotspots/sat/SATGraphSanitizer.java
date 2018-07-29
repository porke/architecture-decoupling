package hotspots.sat;

import hotspots.datamodel.Relationship;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SATGraphSanitizer {

    private HashMap<String, Predicate<String>> classFilterFunctions = new HashMap<>();
    private HashMap<String, Predicate<String>> edgeFilterFunctions = new HashMap<>();

    public SATGraphSanitizer() {
        classFilterFunctions.put("java", p -> p.startsWith("java.class"));
        classFilterFunctions.put("csharp", p -> p.startsWith("csharp.type")
                || p.startsWith("csharp.type_partial"));
        edgeFilterFunctions.put("csharp", p -> p.startsWith("csharp.call")
                || p.startsWith("csharp.extends"));
        edgeFilterFunctions.put("java", p -> p.startsWith("java.call")
                || p.startsWith("java.extends")
                || p.startsWith("java.implements"));
    }

    public SATGraph sanitize(SATGraph graph, String language) {
        System.out.print("Initial graph stats: ");
        System.out.println("nodes: " + graph.getVertices().length() + ", edges: " + graph.getEdges().length());

        Set<JSONObject> languageFilteredVertices = filterLanguageVertices(graph.getVertices(), language);
        Set<JSONObject> languageFilteredEdges = filterLanguageEdges(graph.getEdges(), language);

        Set<JSONObject> fileFilteredVertices = filterFileVertices(new JSONArray(languageFilteredVertices), language);
        Set<JSONObject> fileFilteredEdges = filterFileEdges(new JSONArray(languageFilteredEdges), language);

        return new SATGraph(new JSONArray(fileFilteredVertices), new JSONArray(fileFilteredEdges));
    }

// TODO: Import from SAT libs
//    public WeightedPseudograph<FileVertex, DependencyEdge> importFromSatGraph(Graph satGraph) {
//        WeightedPseudograph<FileVertex, DependencyEdge> retGraph = new WeightedPseudograph<>(DependencyEdge.class);
//        String language = "csharp";
//
//        Map<String, FileVertex> verticesMap = new HashMap<>();
//        System.out.println("Extracting vertices from the SAT graph");
//        satGraph.getNodes().stream().filter(n -> classFilterFunctions.get(language).test(n.getType()))
//                .forEach(n -> {
//                    FileVertex vertex = new FileVertex(n.getName());
//                    retGraph.addVertex(vertex);
//                    verticesMap.put(vertex.getPath(), vertex);
//
//                    System.out.println("Node: " + n + ", node name: " + n.getName());
//                });
//
//        System.out.println("Extracting edges from the SAT graph");
//        satGraph.getEdges().stream().filter(e -> edgeFilterFunctions.get(language).test(e.getType()))
//                .forEach(e -> {
//                    System.out.println(e);
//
//                    Relationship relationship = relationshipFromString(e.getType());
//                    FileVertex fromVertex = verticesMap.get(e.getFromNode().getName());
//                    FileVertex toVertex = verticesMap.get(e.getToNode().getName());
//
//                    if (!verticesMap.containsKey(fromVertex.getPath())) {
//                        System.out.println("Vertex missing: " + e.getFromNode().toString());
//                    }
//                    else if (!verticesMap.containsKey(toVertex.getPath())) {
//                        System.out.println("Vertex missing: " + e.getToNode().toString());
//                    }
//                    else {
//                        DependencyEdge edge = new DependencyEdge(fromVertex, toVertex, relationship);
//                        retGraph.addEdge(fromVertex, toVertex, edge);
//                    }
//                });
//
//        return retGraph;
//    }

    private Set<JSONObject> filterLanguageVertices(JSONArray vertices, String language) {
        Set<JSONObject> filteredVertices = vertices.toList()
                .stream()
                .filter(e -> ((HashMap<String, String>)e).get("type").startsWith(language + "."))
                .map(e -> new JSONObject((HashMap<String, String>)e))
                .collect(Collectors.toSet());
        System.out.println("Sanitized vertices to java: " + filteredVertices.size());
        return filteredVertices;
    }

    private Set<JSONObject> filterLanguageEdges(JSONArray edges, String language) {
        Set<JSONObject> filteredEdges = edges.toList()
                .stream()
                .filter(e -> ((HashMap<String, String>)e).get("type").startsWith(language + "."))
                .map(e -> new JSONObject((HashMap<String, String>)e))
                .collect(Collectors.toSet());
        System.out.println("Sanitized edges to java: " + filteredEdges.size());
        return filteredEdges;
    }

    private Set<JSONObject> filterFileVertices(JSONArray vertices, String language) {
        Set<JSONObject> filteredVertices = vertices.toList()
                .stream()
                .filter(e -> classFilterFunctions.get(language).test(((HashMap<String, String>)e).get("qualifiedName")))
                .map(e -> new JSONObject((HashMap<String, String>)e))
                .collect(Collectors.toSet());
        System.out.println("Sanitized vertices to class level: " + filteredVertices.size());
        return filteredVertices;
    }

    private Set<JSONObject> filterFileEdges(JSONArray edges, String language) {
        Set<JSONObject> filteredEdges = edges.toList()
                .stream()
                .filter(e -> classFilterFunctions.get(language).test(((HashMap<String, String>)e).get("fromNodeQualifiedName")))
                .filter(e -> classFilterFunctions.get(language).test(((HashMap<String, String>)e).get("toNodeQualifiedName")))
                .map(e -> new JSONObject((HashMap<String, String>)e))
                .collect(Collectors.toSet());
        System.out.println("Sanitized edges to class level: " + filteredEdges.size());
        return filteredEdges;
    }

    private Relationship relationshipFromString(String relationship) {
        switch (relationship) {
            case "java.call":
            case "csharp.call":
                return Relationship.Call;
            case "java.implements":
            case "java.extends":
            case "csharp.extends":
                return Relationship.Inheritance;
        }

        return Relationship.Unknown;
    }
}
