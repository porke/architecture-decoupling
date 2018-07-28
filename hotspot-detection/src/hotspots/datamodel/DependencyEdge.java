package hotspots.datamodel;

public class DependencyEdge {
    private Relationship dependencyType;
    private FileVertex from;
    private FileVertex to;

    public DependencyEdge(FileVertex from, FileVertex to, Relationship dependencyType) {
        this.from = from;
        this.to = to;
        this.dependencyType = dependencyType;
    }

    public FileVertex getFrom() {return from;}
    public FileVertex getTo() {return to;}
    public Relationship getDependencyType() {return dependencyType;}

    @Override
    public String toString() {
        return from + " -> " + to + "::" + dependencyType;
    }
}
