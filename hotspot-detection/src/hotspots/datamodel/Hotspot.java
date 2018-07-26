package hotspots.datamodel;

import org.jgrapht.graph.Pseudograph;

import java.util.Set;


public class Hotspot {
    Pseudograph<FileVertex, DependencyEdge> graph;
    Set<DependencyEdge> violatingEdges;
    boolean isInternal;

    public Hotspot(Pseudograph<FileVertex, DependencyEdge> graph, Set<DependencyEdge> violatingEdges, boolean isInternal) {
        this.graph = graph;
        this.violatingEdges = violatingEdges;
        this.isInternal = isInternal;
    }

    public Pseudograph<FileVertex, DependencyEdge> getGraph() {return graph;}

    public Set<DependencyEdge> getViolatingEdges() {return violatingEdges;}

    public boolean isInternal() {return isInternal;}

    @Override
    public String toString() {
        return graph.toString();
    }

    public enum Relationship {
        Inheritance,
        Type,
        Call,
        Unknown
    }
}
