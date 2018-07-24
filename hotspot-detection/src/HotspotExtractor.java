import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HotspotExtractor {

    public Set<Hotspot> extractHotspots(DirectedMultigraph<FileVertex, DependencyEdge> dependencyGraph) {
        List<Set<FileVertex>> classHierarchies = extractClassHierarchies(dependencyGraph);

        Set<Hotspot> allHotspots = new HashSet<>();
        allHotspots.addAll(detectInternalHotspots(dependencyGraph, classHierarchies));
        allHotspots.addAll(detectExternalHotspots(dependencyGraph, classHierarchies));
        return allHotspots;
    }

    private List<Set<FileVertex>> extractClassHierarchies(DirectedMultigraph<FileVertex, DependencyEdge> dependencyGraph) {
        Set<DependencyEdge> inheritanceEdges = dependencyGraph.edgeSet().stream()
                                                              .filter(e -> e.getDependencyType() == Relationship.Inheritance)
                                                              .collect(Collectors.toSet());
        Set<FileVertex> baseClasses = inheritanceEdges.stream().map(e -> e.getTo()).collect(Collectors.toSet());
        Set<FileVertex> derivedClasses = inheritanceEdges.stream().map(e -> e.getFrom()).collect(Collectors.toSet());

        DirectedMultigraph<FileVertex, DependencyEdge> inheritanceGraph = new DirectedMultigraph<>(DependencyEdge.class);
        baseClasses.forEach(inheritanceGraph::addVertex);
        derivedClasses.forEach(inheritanceGraph::addVertex);
        inheritanceEdges.forEach(e -> inheritanceGraph.addEdge(e.getFrom(), e.getTo(), e));

        ConnectivityInspector<FileVertex, DependencyEdge> inspector = new ConnectivityInspector<>(inheritanceGraph);
        return inspector.connectedSets();
    }

    private Set<Hotspot> detectInternalHotspots(DirectedMultigraph<FileVertex, DependencyEdge> dependencyGraph, List<Set<FileVertex>> classHierarchies) {
        return new HashSet<>();
    }

    private Set<Hotspot> detectExternalHotspots(DirectedMultigraph<FileVertex, DependencyEdge> dependencyGraph, List<Set<FileVertex>> classHierarchies) {
        return new HashSet<>();
    }
}
