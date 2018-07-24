import org.jgrapht.graph.DefaultWeightedEdge;

public class DependencyEdge {
    private Relationship dependencyType;
    private int weight;
    private FileVertex from;
    private FileVertex to;

    public DependencyEdge(FileVertex from, FileVertex to, Relationship dependencyType, int weight) {
        this.from = from;
        this.to = to;
        this.dependencyType = dependencyType;
        this.weight = weight;
    }

    public FileVertex getFrom() {return from;}
    public FileVertex getTo() {return to;}
    public Relationship getDependencyType() {return dependencyType;}
    public int getWeight() {return weight;}
}
