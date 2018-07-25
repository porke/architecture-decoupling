import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SATGraphSanitizer {

    private HashMap<String, Predicate<String>> classFilterFunctions = new HashMap<>();

    public SATGraphSanitizer() {
        classFilterFunctions.put("java", p -> p.startsWith("java.class"));
        classFilterFunctions.put("csharp", p -> p.startsWith("csharp.type") || p.startsWith("csharp.type_partial"));
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
}
