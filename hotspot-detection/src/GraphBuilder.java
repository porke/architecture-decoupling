import org.jgrapht.graph.WeightedPseudograph;
import org.json.JSONObject;

import java.util.HashMap;

public class GraphBuilder {
    public WeightedPseudograph<FileVertex, DependencyEdge> buildGraph(SATGraph input) {
        WeightedPseudograph<FileVertex, DependencyEdge> ret = new WeightedPseudograph<>(DependencyEdge.class);

        HashMap<String, FileVertex> vertices = new HashMap<>();
        input.getVertices().forEach(v -> {
            String classPath = ((JSONObject)v).get("qualifiedName").toString();
            FileVertex newVertex = new FileVertex(classPath);
            vertices.put(classPath, newVertex);
            ret.addVertex(newVertex);
        });

        input.getEdges().forEach(e ->
        {
            JSONObject edge = (JSONObject)e;
            String fromVertexName = edge.get("fromNodeQualifiedName").toString();
            String toVertexName = edge.get("toNodeQualifiedName").toString();
            String depType = edge.get("type").toString();
            String weight = edge.get("weight").toString();

            if (!vertices.containsKey(fromVertexName)) {
                System.out.println("Vertex missing: " + fromVertexName);
            }
            else if (!vertices.containsKey(toVertexName)) {
                System.out.println("Vertex missing: " + fromVertexName);
            }
            else if (weight == null) {
                System.out.println("Weight is null");
            }
            else {
                DependencyEdge newEdge = new DependencyEdge(vertices.get(fromVertexName), vertices.get(toVertexName), relationshipFromString(depType));
                ret.addEdge(vertices.get(fromVertexName), vertices.get(toVertexName), newEdge);
                ret.setEdgeWeight(newEdge, Integer.valueOf(weight));
            }
        });

        System.out.println("Constructed a graph with " + ret.vertexSet().size() + " nodes and " + ret.edgeSet().size() + " edges");

        return ret;
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
