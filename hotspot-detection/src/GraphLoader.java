import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class GraphLoader {
    public String loadJson(String path) {
        try {
            JSONTokener parser = new JSONTokener(new FileReader(path));
            JSONObject jsonObject = new JSONObject(parser);

            JSONArray nodesArray = (JSONArray)jsonObject.get("nodes");
            JSONArray edgesArray = (JSONArray)jsonObject.get("edges");

            // TODO: from node I need the type & qualifiedName
            System.out.println(nodesArray.get(0).toString());
            // TODO: from edge I need the fromNodeQualifiedName, toNodeQualifiedName and type
            System.out.println(edgesArray.get(0).toString());

            System.out.println("nodes: " + nodesArray.length() + ", edges: " + edgesArray.length());

            return jsonObject.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
}
