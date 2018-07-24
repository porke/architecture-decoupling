import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import static org.junit.Assert.*;

public class GraphBuilderTest {

    @org.junit.Before
    public void setUp() throws Exception {
    }

    @org.junit.Test
    public void buildGraphTest() {
        String json = new GraphLoader().loadJson("jenkins.json");

        GraphBuilder gb = new GraphBuilder();

        Graph<String, DefaultEdge> stringDefaultEdgeGraph = gb.buildGraph("nanana!");
        assertEquals(4, stringDefaultEdgeGraph.vertexSet().size());
    }
}
