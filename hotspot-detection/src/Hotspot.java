import org.jgrapht.graph.Pseudograph;


public class Hotspot {
    Pseudograph<FileVertex, DependencyEdge> graph;

    public Hotspot(Pseudograph<FileVertex, DependencyEdge> graph) {
        this.graph = graph;
    }

    public Pseudograph<FileVertex, DependencyEdge> getGraph() {return graph;}

    @Override
    public String toString() {
        return graph.toString();
    }
}
