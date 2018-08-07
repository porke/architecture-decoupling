package hotspots.extraction;

import hotspots.datamodel.Hotspot;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class HotspotFileSerializer {

    public void outputHotspotFiles(Set<Hotspot> hotspots, String outputFile) {
        JSONObject rootObject = new JSONObject();
        rootObject.put("violatingFiles", aggregateFiles(hotspots));

        try {
            PrintWriter outFile = new PrintWriter(outputFile, "UTF-8");
            outFile.write(rootObject.toString());
            outFile.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private Set<String> aggregateFiles(Set<Hotspot> hotspots) {
        Set<String> hotspotFiles = new HashSet<>();
        for(Hotspot h : hotspots) {
            hotspotFiles.addAll(h.getViolatingFiles().stream()
                                                     .map(f -> f.getPath())
                                                     .collect(Collectors.toSet()));
        }
        return hotspotFiles;
    }
}
