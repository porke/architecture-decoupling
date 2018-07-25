import org.jgrapht.graph.Pseudograph;
import org.junit.Assert;

public class GraphBuilderTest {
    @org.junit.Test
    public void buildGraphTest() {
        SATGraph graph = new SATGraphLoader().loadJson("jenkins.json");
        SATGraph sanitizedGraph = new SATGraphSanitizer().sanitize(graph, "java");

        GraphBuilder gb = new GraphBuilder();
        Pseudograph<FileVertex, DependencyEdge> hotspotGraph = gb.buildGraph(sanitizedGraph);
        Assert.assertEquals(sanitizedGraph.getEdges().length(), hotspotGraph.edgeSet().size());
        Assert.assertEquals(sanitizedGraph.getVertices().length(), hotspotGraph.vertexSet().size());
    }
}
