package hotspots.datamodel;

import org.jgrapht.graph.Pseudograph;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class Hotspot {
    Pseudograph<FileVertex, DependencyEdge> graph;
    Set<DependencyEdge> violatingEdges;
    boolean isInternal;

    public Hotspot(Pseudograph<FileVertex, DependencyEdge> graph, Set<DependencyEdge> violatingEdges, boolean isInternal, Set<FileVertex> classHierarchy) {
        this.violatingEdges = violatingEdges;
        this.isInternal = isInternal;

        this.graph = (Pseudograph<FileVertex, DependencyEdge>)graph.clone();
        markVertexTypes(classHierarchy);

        // TODO: remove
        System.out.println(serializeToJson());
    }

    public Pseudograph<FileVertex, DependencyEdge> getGraph() {return graph;}

    public Set<DependencyEdge> getViolatingEdges() {return violatingEdges;}

    public boolean isInternal() {return isInternal;}

    public Set<FileVertex> getViolatingFiles() {return this.graph.vertexSet(); }

    private void markVertexTypes(Set<FileVertex> classHierarchy) {
        graph.vertexSet().forEach(v -> {
            // Mark all vertices as derived classes
            v.setVertexType(VertexType.DerivedClass);

            // Those which are not in the hierarchy are hierarchy clients
            if (!classHierarchy.stream().anyMatch(c -> c.getPath().equals(v.getPath()))) {
                v.setVertexType(VertexType.HierarchyClientClass);
            }

            // Those which have at least one derived relationship are base classes
            if (graph.edgesOf(v).stream().anyMatch(e -> e.getDependencyType() == Relationship.Inheritance
                                                && e.getFrom().getPath().equals(v.getPath()))) {
                v.setVertexType(VertexType.BaseClass);
            }
        });
    }

    private String serializeToJson() {
        Set<DependencyEdge> edges = graph.edgeSet();
        Map<String, Integer> vertexIds = new HashMap<>();
        int id = 0;
        for (DependencyEdge e : edges) {
            if (!vertexIds.containsKey(e.getFrom().getPath())) {
                vertexIds.put(e.getFrom().getPath(), id);
                ++id;
            }
            if (!vertexIds.containsKey(e.getTo().getPath())) {
                vertexIds.put(e.getTo().getPath(), id);
                ++id;
            }
        }

        JSONObject rootObject = new JSONObject();
        JSONArray edgesArray = new JSONArray();
        for (DependencyEdge e : edges) {
            JSONObject edgeObject = new JSONObject();
            edgeObject.put("value", e.getDependencyType());
            edgeObject.put("violation", violatingEdges.contains(e));

            JSONObject fromVertexObject = new JSONObject();
            fromVertexObject.put("id", vertexIds.get(e.getFrom().getPath()));
            fromVertexObject.put("label", e.getFrom().getPath());
            fromVertexObject.put("type", e.getFrom().getVertexType().toString());
            edgeObject.put("source", fromVertexObject);

            JSONObject toVertexObject = new JSONObject();
            toVertexObject.put("id", vertexIds.get(e.getTo().getPath()));
            toVertexObject.put("label", e.getTo().getPath());
            toVertexObject.put("type", e.getTo().getVertexType().toString());
            edgeObject.put("target", toVertexObject);

            edgesArray.put(edgeObject);
        }

        rootObject.put("edges", edgesArray);
        return rootObject.toString();
    }

    @Override
    public String toString() {
        return graph.toString();
    }
}
