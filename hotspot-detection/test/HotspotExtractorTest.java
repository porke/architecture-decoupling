import org.jgrapht.graph.DirectedMultigraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class HotspotExtractorTest {
    @org.junit.Test
    public void extractInternalHotspotTestWithSingleHierarchy() {
        DirectedMultigraph<FileVertex, DependencyEdge> depGraph = createInternalHotspotSingleHierarchy();
        HotspotExtractor he = new HotspotExtractor();

        Set<Hotspot> hotspots = he.extractHotspots(depGraph);
    }

    @org.junit.Test
    public void extractInternalHotspotTestWithMultipleHierarchies() {
        DirectedMultigraph<FileVertex, DependencyEdge> depGraph = createInternalHotspotDoubleHierarchy();
        HotspotExtractor he = new HotspotExtractor();

        Set<Hotspot> hotspots = he.extractHotspots(depGraph);
    }

    @org.junit.Test
    public void extractExternalHotspotTest() {
        DirectedMultigraph<FileVertex, DependencyEdge> depGraph = createType1Hotspot();
        HotspotExtractor he = new HotspotExtractor();

        Set<Hotspot> hotspots = he.extractHotspots(depGraph);
    }

    private DirectedMultigraph<FileVertex, DependencyEdge> createType1Hotspot() {
        HashMap<String, FileVertex> files = new HashMap<>();
        files.put("AbstractActionManager.java", new FileVertex("AbstractActionManager.java"));
        files.put("SubmitAction.java", new FileVertex("SubmitAction.java"));
        files.put("UriAction.java", new FileVertex("UriAction.java"));
        files.put("NamedAction.java", new FileVertex("NamedAction.java"));
        files.put("HideAction.java", new FileVertex("HideAction.java"));
        files.put("ThreadAction.java", new FileVertex("ThreadAction.java"));
        files.put("UndefAction.java", new FileVertex("UndefAction.java"));
        files.put("GoToAction.java", new FileVertex("GoToAction.java"));
        files.put("InvalidAction.java", new FileVertex("InvalidAction.java"));
        files.put("ActionManagerFactory.java", new FileVertex("ActionManagerFactory.java"));
        DirectedMultigraph<FileVertex, DependencyEdge> depGraph = new DirectedMultigraph<>(DependencyEdge.class);
        files.values().forEach(depGraph::addVertex);

        addEdgeToGraph(depGraph, files, "SubmitAction.java", "AbstractActionManager.java", Relationship.Inheritance);
        addEdgeToGraph(depGraph, files, "UriAction.java", "AbstractActionManager.java", Relationship.Inheritance);
        addEdgeToGraph(depGraph, files, "NamedAction.java", "AbstractActionManager.java", Relationship.Inheritance);
        addEdgeToGraph(depGraph, files, "HideAction.java", "AbstractActionManager.java", Relationship.Inheritance);
        addEdgeToGraph(depGraph, files, "ThreadAction.java", "AbstractActionManager.java", Relationship.Inheritance);
        addEdgeToGraph(depGraph, files, "GoToAction.java", "AbstractActionManager.java", Relationship.Inheritance);
        addEdgeToGraph(depGraph, files, "InvalidAction.java", "AbstractActionManager.java", Relationship.Inheritance);
        addEdgeToGraph(depGraph, files, "UndefAction.java", "AbstractActionManager.java", Relationship.Inheritance);

        addEdgeToGraph(depGraph, files, "ActionManagerFactory.java", "AbstractActionManager.java", Relationship.Type);
        addEdgeToGraph(depGraph, files, "ActionManagerFactory.java", "SubmitAction.java", Relationship.Call);
        addEdgeToGraph(depGraph, files, "ActionManagerFactory.java", "UriAction.java", Relationship.Call);
        addEdgeToGraph(depGraph, files, "ActionManagerFactory.java", "NamedAction.java", Relationship.Call);
        addEdgeToGraph(depGraph, files, "ActionManagerFactory.java", "HideAction.java", Relationship.Call);
        addEdgeToGraph(depGraph, files, "ActionManagerFactory.java", "ThreadAction.java", Relationship.Call);
        addEdgeToGraph(depGraph, files, "ActionManagerFactory.java", "GoToAction.java", Relationship.Call);
        addEdgeToGraph(depGraph, files, "ActionManagerFactory.java", "InvalidAction.java", Relationship.Call);
        addEdgeToGraph(depGraph, files, "ActionManagerFactory.java", "UndefAction.java", Relationship.Call);

        return depGraph;
    }

    private DirectedMultigraph<FileVertex, DependencyEdge> createInternalHotspotSingleHierarchy() {
        HashMap<String, FileVertex> files = new HashMap<>();
        DirectedMultigraph<FileVertex, DependencyEdge> depGraph = new DirectedMultigraph<>(DependencyEdge.class);
        files.put("LabelVisitor.java", new FileVertex("LabelVisitor.java"));
        files.put("Label.java", new FileVertex("Label.java"));
        files.put("LabelExpression.java", new FileVertex("LabelExpression.java"));
        files.put("LabelAtom.java", new FileVertex("LabelAtom.java"));
        files.values().forEach(depGraph::addVertex);

        addEdgeToGraph(depGraph, files, "Label.java", "LabelVisitor.java", Relationship.Type);
        addEdgeToGraph(depGraph, files,"LabelExpression.java", "LabelVisitor.java", Relationship.Type);
        addEdgeToGraph(depGraph, files,"LabelAtom.java", "LabelVisitor.java", Relationship.Type);
        addEdgeToGraph(depGraph, files,"LabelExpression.java", "Label.java", Relationship.Inheritance);
        addEdgeToGraph(depGraph, files,"LabelAtom.java", "Label.java", Relationship.Inheritance);
        addEdgeToGraph(depGraph, files,"Label.java", "LabelExpression.java", Relationship.Type);
        addEdgeToGraph(depGraph, files,"Label.java", "LabelAtom.java", Relationship.Type);
        addEdgeToGraph(depGraph, files,"LabelVisitor.java", "LabelExpression.java", Relationship.Type);
        addEdgeToGraph(depGraph, files,"LabelVisitor.java", "LabelAtom.java", Relationship.Type);

        return depGraph;
    }

    private DirectedMultigraph<FileVertex, DependencyEdge> createInternalHotspotDoubleHierarchy() {
        HashMap<String, FileVertex> files = new HashMap<>();
        DirectedMultigraph<FileVertex, DependencyEdge> depGraph = new DirectedMultigraph<>(DependencyEdge.class);
        files.put("A.java", new FileVertex("A.java"));
        files.put("B.java", new FileVertex("B.java"));
        files.put("C.java", new FileVertex("C.java"));
        files.put("D.java", new FileVertex("D.java"));
        files.values().forEach(depGraph::addVertex);

        addEdgeToGraph(depGraph, files, "A.java", "B.java", Relationship.Inheritance);
        addEdgeToGraph(depGraph, files,"C.java", "D.java", Relationship.Inheritance);
        return depGraph;
    }

    private void addEdgeToGraph(DirectedMultigraph<FileVertex, DependencyEdge> depGraph, HashMap<String, FileVertex> files, String from, String to, Relationship rel) {
        depGraph.addEdge(
                files.get(from),
                files.get(to),
                new DependencyEdge(files.get(from),  files.get(to),  rel, 1));
    }
}
