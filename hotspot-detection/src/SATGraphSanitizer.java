import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

public class SATGraphSanitizer {

    public SATGraph sanitize(SATGraph graph) {
        System.out.print("Initial graph stats: ");
        System.out.println("nodes: " + graph.getVertices().length() + ", edges: " + graph.getEdges().length());

        Set<JSONObject> languageFilteredVertices = filterLanguageVertices(graph.getVertices());
        Set<JSONObject> languageFilteredEdges = filterLanguageEdges(graph.getEdges());

        Set<JSONObject> fileFilteredVertices = filterFileVertices(new JSONArray(languageFilteredVertices));
        Set<JSONObject> fileFilteredEdges = filterFileEdges(new JSONArray(languageFilteredEdges));

        return new SATGraph(new JSONArray(fileFilteredVertices), new JSONArray(fileFilteredEdges));
    }

    private Set<JSONObject> filterLanguageVertices(JSONArray vertices) {
        Set<JSONObject> filteredVertices = vertices.toList()
                .stream()
                .filter(e -> ((HashMap<String, String>)e).get("type").startsWith("java."))
                .map(e -> new JSONObject((HashMap<String, String>)e))
                .collect(Collectors.toSet());
        System.out.println("Sanitized vertices to java: " + filteredVertices.size());
        return filteredVertices;
    }

    private Set<JSONObject> filterLanguageEdges(JSONArray edges) {
        Set<JSONObject> filteredEdges = edges.toList()
                .stream()
                .filter(e -> ((HashMap<String, String>)e).get("type").startsWith("java."))
                .map(e -> new JSONObject((HashMap<String, String>)e))
                .collect(Collectors.toSet());
        System.out.println("Sanitized edges to java: " + filteredEdges.size());
        return filteredEdges;
    }

    private Set<JSONObject> filterFileVertices(JSONArray vertices) {
        Set<JSONObject> filteredVertices = vertices.toList()
                .stream()
                .filter(e -> ((HashMap<String, String>)e).get("qualifiedName").startsWith("java.class"))
                .map(e -> new JSONObject((HashMap<String, String>)e))
                .collect(Collectors.toSet());
        System.out.println("Sanitized vertices to class level: " + filteredVertices.size());
        return filteredVertices;
    }

    private Set<JSONObject> filterFileEdges(JSONArray edges) {
        Set<JSONObject> filteredEdges = edges.toList()
                .stream()
                .filter(e -> ((HashMap<String, String>)e).get("fromNodeQualifiedName").startsWith("java.class"))
                .filter(e -> ((HashMap<String, String>)e).get("toNodeQualifiedName").startsWith("java.class"))
                .map(e -> new JSONObject((HashMap<String, String>)e))
                .collect(Collectors.toSet());
        System.out.println("Sanitized edges to class level: " + filteredEdges.size());
        return filteredEdges;
    }
}
