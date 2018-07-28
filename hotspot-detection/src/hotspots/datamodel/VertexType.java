package hotspots.datamodel;

public enum VertexType {
    BaseClass("base"),
    DerivedClass("super"),
    HierarchyClientClass("client");

    private final String name;

    VertexType(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}
