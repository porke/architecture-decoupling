import org.jgrapht.graph.DirectedMultigraph;


public class Hotspot {
    DirectedMultigraph<FileVertex, DependencyEdge> graph;

    public Hotspot(DirectedMultigraph<FileVertex, DependencyEdge> graph) {
        this.graph = graph;
    }

    public DirectedMultigraph<FileVertex, DependencyEdge> getGraph() {return graph;}
}
