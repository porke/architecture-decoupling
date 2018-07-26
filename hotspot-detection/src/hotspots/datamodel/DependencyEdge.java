package hotspots.datamodel;

public class DependencyEdge {
    private Hotspot.Relationship dependencyType;
    private FileVertex from;
    private FileVertex to;

    public DependencyEdge(FileVertex from, FileVertex to, Hotspot.Relationship dependencyType) {
        this.from = from;
        this.to = to;
        this.dependencyType = dependencyType;
    }

    public FileVertex getFrom() {return from;}
    public FileVertex getTo() {return to;}
    public Hotspot.Relationship getDependencyType() {return dependencyType;}

    @Override
    public String toString() {
        return from + " -> " + to + "::" + dependencyType;
    }
}
