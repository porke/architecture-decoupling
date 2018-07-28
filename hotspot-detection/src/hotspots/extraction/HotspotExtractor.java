package hotspots.extraction;

import hotspots.datamodel.DependencyEdge;
import hotspots.datamodel.FileVertex;
import hotspots.datamodel.Hotspot;
import hotspots.datamodel.Relationship;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.graph.Pseudograph;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HotspotExtractor {

    public Set<Hotspot> extractHotspots(Pseudograph<FileVertex, DependencyEdge> dependencyGraph) {
        List<Set<FileVertex>> classHierarchies = extractClassHierarchies(dependencyGraph);

        Set<Hotspot> allHotspots = new HashSet<>();
        allHotspots.addAll(detectInternalHotspots(dependencyGraph, classHierarchies));
        allHotspots.addAll(detectExternalHotspots(dependencyGraph, classHierarchies));
        return allHotspots;
    }

    private List<Set<FileVertex>> extractClassHierarchies(Pseudograph<FileVertex, DependencyEdge> dependencyGraph) {
        Set<DependencyEdge> inheritanceEdges = dependencyGraph.edgeSet().stream()
                                                              .filter(e -> e.getDependencyType() == Relationship.Inheritance)
                                                              .filter(e -> e.getTo() != e.getFrom())        // csharp allows inheriting outer classes and sat represents them with the same name
                                                              .collect(Collectors.toSet());
        Set<FileVertex> baseClasses = inheritanceEdges.stream().map(e -> e.getTo()).collect(Collectors.toSet());
        Set<FileVertex> derivedClasses = inheritanceEdges.stream().map(e -> e.getFrom()).collect(Collectors.toSet());

        DirectedMultigraph<FileVertex, DependencyEdge> inheritanceGraph = new DirectedMultigraph<>(DependencyEdge.class);
        baseClasses.forEach(inheritanceGraph::addVertex);
        derivedClasses.forEach(inheritanceGraph::addVertex);
        inheritanceEdges.forEach(e -> inheritanceGraph.addEdge(e.getFrom(), e.getTo(), e));

        ConnectivityInspector<FileVertex, DependencyEdge> inspector = new ConnectivityInspector<>(inheritanceGraph);
        List<Set<FileVertex>> hierarchies = inspector.connectedSets();
        System.out.println("Extracted a total of " + hierarchies.size() + " class hierarchies");

        return hierarchies;
    }

    private Set<Hotspot> detectInternalHotspots(Pseudograph<FileVertex, DependencyEdge> dependencyGraph, List<Set<FileVertex>> classHierarchies) {
        Set<Hotspot> ret = new HashSet<>();
        for (Set<FileVertex> hierarchy : classHierarchies) {
            Hotspot hotspot = detectInternalHotspotInHierarchy(dependencyGraph, hierarchy);
            if (hotspot != null) {
                ret.add(hotspot);
            }
        }

        System.out.println("Detected " + ret.size() + " internal hotspots");
        return ret;
    }

    private Hotspot detectInternalHotspotInHierarchy(Pseudograph<FileVertex, DependencyEdge> dependencyGraph, Set<FileVertex> classes) {
        Pseudograph<FileVertex, DependencyEdge> internalHierarchyGraph = constructHierarchy(dependencyGraph, classes, true);

        Set<DependencyEdge> violatingEdges = new HashSet<>();
        for (FileVertex c : classes) {
            Set<DependencyEdge> edges = internalHierarchyGraph.edgesOf(c);
            Set<FileVertex> childClasses = edges.stream()
                                                .filter(e -> e.getFrom() == c && e.getDependencyType() == Relationship.Inheritance)
                                                .map(e -> e.getFrom())
                                                .collect(Collectors.toSet());
            violatingEdges.addAll(edges.stream().filter(e ->  e.getDependencyType() != Relationship.Inheritance
                                                                        && e.getFrom() != e.getTo()
                                                                        && childClasses.contains(e.getTo()))
                                                                .collect(Collectors.toSet()));
        }

        if (!violatingEdges.isEmpty()) {
            return new Hotspot(internalHierarchyGraph, violatingEdges, true, classes);
        }

        return null;
    }

    private Set<Hotspot> detectExternalHotspots(Pseudograph<FileVertex, DependencyEdge> dependencyGraph, List<Set<FileVertex>> classHierarchies) {
        Set<Hotspot> ret = new HashSet<>();
        for (Set<FileVertex> hierarchy : classHierarchies) {
            Hotspot hotspot = detectExternalHotspotInHierarchy(dependencyGraph, hierarchy);
            if (hotspot != null) {
                ret.add(hotspot);
            }
        }

        System.out.println("Detected " + ret.size() + " external hotspots");

        return ret;
    }

    private Hotspot detectExternalHotspotInHierarchy(Pseudograph<FileVertex, DependencyEdge> dependencyGraph, Set<FileVertex> hierarchyClasses) {
        Pseudograph<FileVertex, DependencyEdge> hierarchyGraphWithClients = constructHierarchy(dependencyGraph, hierarchyClasses, false);
        Set<FileVertex> clients = new HashSet<>(hierarchyGraphWithClients.vertexSet());
        clients.removeAll(hierarchyClasses);

        Set<DependencyEdge> violatingEdges = new HashSet<>();
        for (FileVertex client : clients) {
            Set<DependencyEdge> clientToHierarchyConnections = hierarchyGraphWithClients.edgesOf(client);
            Set<FileVertex> clientToConnections = clientToHierarchyConnections.stream().map(e -> e.getTo()).collect(Collectors.toSet());
            if (clientToConnections.equals(hierarchyClasses)) {
                violatingEdges.addAll(clientToHierarchyConnections);
            }
        }

        if (!violatingEdges.isEmpty()) {
            return new Hotspot(hierarchyGraphWithClients, violatingEdges, false, hierarchyClasses);
        }

        return null;
    }

    private Pseudograph<FileVertex, DependencyEdge> constructHierarchy(Pseudograph<FileVertex, DependencyEdge> dependencyGraph, Set<FileVertex> classes, boolean isInternal) {
        Pseudograph<FileVertex, DependencyEdge> hierarchyGraph = new Pseudograph<>(DependencyEdge.class);
        classes.forEach(hierarchyGraph::addVertex);
        classes.forEach(c -> {
            Set<DependencyEdge> allEdges = new HashSet<>();
            allEdges.addAll(dependencyGraph.incomingEdgesOf(c));
            allEdges.addAll(dependencyGraph.outgoingEdgesOf(c));
            allEdges.forEach(e ->  {
                if (isInternal) {
                    if (classes.contains(e.getFrom()) && classes.contains(e.getTo())) {
                        hierarchyGraph.addEdge(e.getFrom(), e.getTo(), e);
                    }
                }
                else {
                    hierarchyGraph.addVertex(e.getFrom());
                    hierarchyGraph.addVertex(e.getTo());
                    hierarchyGraph.addEdge(e.getFrom(), e.getTo(), e);
                }
            });
        });

        return hierarchyGraph;
    }
}
