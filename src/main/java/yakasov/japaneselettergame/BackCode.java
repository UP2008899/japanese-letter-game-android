package yakasov.japaneselettergame;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class BackCode {
    private final Context context;

    public static String[] allRows = {"a-row", "ka-row", "sa-row", "ta-row", "na-row", "ma-row",
                                      "ya-row", "ra-row", "wa-row", "ga-row", "za-row", "da-row",
                                      "ba-row", "pa-row"};
    public static ArrayList<String> chosenRows;

    public BackCode(Context context) {
        this.context = context;
    }

    public List<String> readLine(String path) {
        List<String> lines = new ArrayList<>();

        AssetManager am = context.getAssets();

        try {
            InputStream is = am.open(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;

            while ((line = reader.readLine()) != null)
                lines.add(line);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }

    public org.json.simple.JSONObject loadJson(String path) {
        AssetManager am = context.getAssets();
        org.json.simple.JSONObject jsonObject = new org.json.simple.JSONObject();

        try {
            InputStream is = am.open(path);

            JSONParser jsonParser = new JSONParser();
            jsonObject = (org.json.simple.JSONObject)jsonParser.parse(
                    new InputStreamReader(is, "UTF-8"));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static String compareRows(SharedPreferences prefs) {
        chosenRows = new ArrayList<>();
        chosenRows.add("a-row");
        Set<String> gojuon = prefs.getStringSet("hiragana_gojuon_list", null);
        Set<String> dakuon = prefs.getStringSet("hiragana_dakuon_list", null);
        for (String row : allRows) {
            if (gojuon.contains(row) || dakuon.contains(row)) {
                chosenRows.add(row);
            }
        }
        return String.valueOf(chosenRows);
    }

    public int getRandomIndex(int max, int previousIndex) {
        Random random = new Random();
        int chosenIndex = previousIndex;
        while (chosenIndex == previousIndex) {
            chosenIndex = random.nextInt(max);
        }
        return chosenIndex;
    }

    public int getRandomIndexExclusionary(int max, ArrayList<Integer> chosenIndexes) {
        int chosenValue;
        boolean pickedAlready;
        while (true) {
            Random random = new Random();
            chosenValue = random.nextInt(max);

            pickedAlready = false;
            for (int i : chosenIndexes) {
                if (chosenValue == i) {
                    pickedAlready = true;
                }
            }
            if (!pickedAlready) {
                return chosenValue;
            }
        }
    }
}
