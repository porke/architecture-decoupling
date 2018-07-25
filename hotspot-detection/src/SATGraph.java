import org.json.JSONArray;

public class SATGraph {
    private JSONArray edges;
    private JSONArray vertices;

    public SATGraph(JSONArray vertices, JSONArray edges) {
        this.edges = edges;
        this.vertices = vertices;
    }

    public JSONArray getEdges() { return this.edges;}
    public JSONArray getVertices() {return this.vertices;}
}
