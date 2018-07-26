package hotspots.sat;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

public class GraphUnzipper {
    public String loadGraph(String zipPath) {
        try {
            GZIPInputStream stream = new GZIPInputStream(new FileInputStream(zipPath));
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
