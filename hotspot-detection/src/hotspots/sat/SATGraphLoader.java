package hotspots.sat;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

public class SATGraphLoader {
    public SATGraph loadJson(String path) {
        try {
            return loadJson(new FileReader(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return new SATGraph(new JSONArray(), new JSONArray());
    }

    private SATGraph loadJson(Reader reader) {
        JSONTokener parser = new JSONTokener(reader);
        JSONObject jsonObject = new JSONObject(parser);

        JSONArray verticesArray = (JSONArray)jsonObject.get("nodes");
        JSONArray edgesArray = (JSONArray)jsonObject.get("edges");

        return new SATGraph(verticesArray, edgesArray);
    }
}
