import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class SATGraphLoader {
    public SATGraph loadJson(String path) {
        try {
            JSONTokener parser = new JSONTokener(new FileReader(path));
            JSONObject jsonObject = new JSONObject(parser);

            JSONArray verticesArray = (JSONArray)jsonObject.get("nodes");
            JSONArray edgesArray = (JSONArray)jsonObject.get("edges");

            return new SATGraph(verticesArray, edgesArray);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return new SATGraph(new JSONArray(), new JSONArray());
    }
}
