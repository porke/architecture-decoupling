package hotspots.datamodel;

public class FileVertex {
    private String path;

    public FileVertex(String path) {
        this.path = path;
    }

    public String getPath() {return path;}

        @Override
    public String toString() {return path;}
}
