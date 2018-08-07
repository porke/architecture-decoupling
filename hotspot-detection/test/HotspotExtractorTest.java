import hotspots.datamodel.DependencyEdge;
import hotspots.datamodel.FileVertex;
import hotspots.datamodel.Hotspot;
import hotspots.datamodel.Relationship;
import hotspots.extraction.HotspotExtractor;
import hotspots.extraction.HotspotFileSerializer;
import hotspots.sat.GraphBuilder;
import hotspots.sat.SATGraph;
import hotspots.sat.SATGraphLoader;
import hotspots.sat.SATGraphSanitizer;
import org.jgrapht.graph.Pseudograph;

import java.util.HashMap;
import java.util.Set;

public class HotspotExtractorTest {
    @org.junit.Test
    public void extractInternalHotspotTestWithSingleHierarchy() {
        Pseudograph<FileVertex, DependencyEdge> depGraph = createInternalHotspotSingleHierarchy();
        HotspotExtractor he = new HotspotExtractor();

        System.out.println(he.extractHotspots(depGraph));
    }

    @org.junit.Test
    public void extractExternalHotspotTest() {
        Pseudograph<FileVertex, DependencyEdge> depGraph = createExternalHotspot();
        HotspotExtractor he = new HotspotExtractor();

        Set<Hotspot> extractedHotspots = he.extractHotspots(depGraph);
        System.out.println(extractedHotspots);
        new HotspotFileSerializer().outputHotspotFiles(extractedHotspots, "extractExternalHotspotTest-hotspots.json");
    }

    @org.junit.Test
    public void jenkinsHotspotTest() {
        SATGraph graph = new SATGraphLoader().loadJson("jenkins.json");
        SATGraph sanitizedGraph = new SATGraphSanitizer().sanitize(graph, "java");

        Pseudograph<FileVertex, DependencyEdge> hotspotGraph = new GraphBuilder().buildGraph(sanitizedGraph);
        Set<Hotspot> extractedHotspots = new HotspotExtractor().extractHotspots(hotspotGraph);
        System.out.println(extractedHotspots);
        new HotspotFileSerializer().outputHotspotFiles(extractedHotspots, "jenkinsHotspotTest-hotspots.json");
    }

    @org.junit.Test
    public void nunitHotspotTest() {
        SATGraph graph = new SATGraphLoader().loadJson("nunit.json");
        SATGraph sanitizedGraph = new SATGraphSanitizer().sanitize(graph, "csharp");

        Pseudograph<FileVertex, DependencyEdge> hotspotGraph = new GraphBuilder().buildGraph(sanitizedGraph);
        System.out.println(new HotspotExtractor().extractHotspots(hotspotGraph));
    }

    @org.junit.Test
    public void dotnet_wcfHotspotTest() {
        SATGraph graph = new SATGraphLoader().loadJson("dotnet_wcf.json");
        SATGraph sanitizedGraph = new SATGraphSanitizer().sanitize(graph, "csharp");

        Pseudograph<FileVertex, DependencyEdge> hotspotGraph = new GraphBuilder().buildGraph(sanitizedGraph);
        System.out.println(new HotspotExtractor().extractHotspots(hotspotGraph));
    }

    private Pseudograph<FileVertex, DependencyEdge> createExternalHotspot() {
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

        Pseudograph<FileVertex, DependencyEdge> depGraph = new Pseudograph<>(DependencyEdge.class);
        files.values().forEach(depGraph::addVertex);

        addEdgeToGraph(depGraph, files, "SubmitAction.java", "AbstractActionManager.java", Relationship.Inheritance);
        addEdgeToGraph(depGraph, files, "UriAction.java", "AbstractActionManager.java", Relationship.Inheritance);
        addEdgeToGraph(depGraph, files, "NamedAction.java", "AbstractActionManager.java", Relationship.Inheritance);
        addEdgeToGraph(depGraph, files, "HideAction.java", "AbstractActionManager.java", Relationship.Inheritance);
        addEdgeToGraph(depGraph, files, "ThreadAction.java", "AbstractActionManager.java", Relationship.Inheritance);
        addEdgeToGraph(depGraph, files, "GoToAction.java", "AbstractActionManager.java", Relationship.Inheritance);
        addEdgeToGraph(depGraph, files, "InvalidAction.java", "AbstractActionManager.java", Relationship.Inheritance);
        addEdgeToGraph(depGraph, files, "UndefAction.java", "AbstractActionManager.java", Relationship.Inheritance);

        addEdgeToGraph(depGraph, files, "ActionManagerFactory.java", "AbstractActionManager.java", Relationship.Call);
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

    private Pseudograph<FileVertex, DependencyEdge> createInternalHotspotSingleHierarchy() {
        HashMap<String, FileVertex> files = new HashMap<>();
        Pseudograph<FileVertex, DependencyEdge> depGraph = new Pseudograph<>(DependencyEdge.class);

        files.put("LabelVisitor.java", new FileVertex("LabelVisitor.java"));
        files.put("Label.java", new FileVertex("Label.java"));
        files.put("LabelExpression.java", new FileVertex("LabelExpression.java"));
        files.put("LabelAtom.java", new FileVertex("LabelAtom.java"));
        files.values().forEach(depGraph::addVertex);

        addEdgeToGraph(depGraph, files, "Label.java", "LabelVisitor.java", Relationship.Call);
        addEdgeToGraph(depGraph, files,"LabelExpression.java", "LabelVisitor.java", Relationship.Call);
        addEdgeToGraph(depGraph, files,"LabelAtom.java", "LabelVisitor.java", Relationship.Call);
        addEdgeToGraph(depGraph, files,"LabelExpression.java", "Label.java", Relationship.Inheritance);
        addEdgeToGraph(depGraph, files,"LabelAtom.java", "Label.java", Relationship.Inheritance);
        addEdgeToGraph(depGraph, files,"Label.java", "LabelExpression.java", Relationship.Call);
        addEdgeToGraph(depGraph, files,"Label.java", "LabelAtom.java", Relationship.Call);
        addEdgeToGraph(depGraph, files,"LabelVisitor.java", "LabelExpression.java", Relationship.Call);
        addEdgeToGraph(depGraph, files,"LabelVisitor.java", "LabelAtom.java", Relationship.Call);

        return depGraph;
    }

    private void addEdgeToGraph(Pseudograph<FileVertex, DependencyEdge> depGraph, HashMap<String, FileVertex> files, String from, String to, Relationship rel) {
        depGraph.addEdge(
                files.get(from),
                files.get(to),
                new DependencyEdge(files.get(from),  files.get(to),  rel));
    }
}
