package hotspots.datamodel;

public class FileVertex {
    private String path;
    private VertexType vertexType;

    public FileVertex(String path) {
        this.path = path;
    }

    public void setVertexType(VertexType vertexType) {this.vertexType = vertexType;}

    public String getPath() {return path;}

    public VertexType getVertexType() {return this.vertexType;}

    @Override
    public String toString() {return path;}
}
