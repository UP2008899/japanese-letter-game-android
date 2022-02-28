package yakasov.japaneselettergame;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

/**
 * BackCode class handles all character management. This includes loading
 * characters from JSON files (stored in assets), as well as figuring out what
 * rows should be used. It also randomly chooses the characters and finds the
 * correct translations.
 */
public class BackCode {
  private static final String[] ALLROWS = {
      "a-row", "ka-row", "sa-row", "ta-row", "na-row", "ha-row", "ma-row",
      "ya-row", "ra-row", "wa-row", "ga-row", "za-row", "da-row",
      "ba-row", "pa-row"
  };
  private static final Random RAND = new Random();
  private static ArrayList<String> chosenRows;
  private static String randomRow;
  private static String correctEnglishCharacter = "";
  private static String correctJapaneseCharacter = "";
  private static String previousEnglishCharacter = "";
  private static String previousJapaneseCharacter = "";
  private final Context context;

  public BackCode(final Context context) {
    this.context = context;
  }

  /**
   * Compares the rows selected by the user in settings (stored in prefs) to the
   * JSON loaded rows, and sets them as active if they match.
   *
   * @param prefs SharedPreferences object using getSharedPreferences method
   * @return String of the chosenRows ArrayList (for use with Log.d)
   */
  public static String compareRows(final SharedPreferences prefs) {
    chosenRows = new ArrayList<>();
    chosenRows.add("a-row");
    Set<String> gojuon = prefs.getStringSet("hiragana_gojuon_list", null);
    Set<String> dakuon = prefs.getStringSet("hiragana_dakuon_list", null);
    for (String row : ALLROWS) {
      if (gojuon.contains(row) || dakuon.contains(row)) {
        chosenRows.add(row);
      }
    }
    return String.valueOf(chosenRows);
  }

  public static String returnCorrectEnglishCharacter() {
    return correctEnglishCharacter;
  }

  public static String returnCorrectJapaneseCharacter() {
    return correctJapaneseCharacter;
  }

  public static void setPreviousCharacters() {
    previousEnglishCharacter = correctEnglishCharacter;
    previousJapaneseCharacter = correctJapaneseCharacter;
  }

  public static String returnPreviousEnglishCharacter() {
    return previousEnglishCharacter;
  }

  public static String returnPreviousJapaneseCharacter() {
    return previousJapaneseCharacter;
  }

  /**
   * Randomly chooses the correct English and Japanese characters for the next
   * guess, utilising getRandomJSONObj to get a pair of characters.
   *
   * @param allCharacters loaded JSON from assets/characters.json
   */
  public static void getCorrectCharacterObj(
      final org.json.simple.JSONObject allCharacters) {
    org.json.simple.JSONObject correctCharacterObject;
    correctJapaneseCharacter = previousJapaneseCharacter;
    while (Objects.equals(
        correctJapaneseCharacter, previousJapaneseCharacter)) {
      correctCharacterObject = getRandomJSONObj(allCharacters);
      correctEnglishCharacter = (String) correctCharacterObject.keySet()
          .iterator().next();
      correctJapaneseCharacter = (String) correctCharacterObject.get(
          correctEnglishCharacter);
    }
  }

  /**
   * Randomly chooses a pair of characters to use from allCharacters. The format
   * of the resulting JSONObject will be {"x": "y"}, where x is the English
   * character and y is the Japanese Hiragana equivalent. eg {"n": "ん"}
   *
   * @param allCharacters loaded JSON from assets/characters.json
   * @return randomly chosen row from allCharacters as JSONObject
   */
  public static org.json.simple.JSONObject getRandomJSONObj(
      final org.json.simple.JSONObject allCharacters) {
    randomRow = chosenRows.get(RAND.nextInt(chosenRows.size())); // String
    org.json.simple.JSONArray correctRowArray =
        (org.json.simple.JSONArray) allCharacters.get(randomRow);
    return (org.json.simple.JSONObject)
        Objects.requireNonNull(correctRowArray).get(
            RAND.nextInt(correctRowArray.size()));
  }

  /**
   * Randomly chooses the correct English and Japanese characters for the next
   * guess, utilising getRandomJSONObj to get a pair of characters. These
   * characters are unique and not the same as the correct character. The
   * correct character button is set separately.
   *
   * @param allCharacters      loaded JSON from assets/characters.json
   * @param repeatedCharacters List of already selected characters. Includes the
   *                           correct English character in pos 0
   * @return String of the random English character chosen
   */
  public static String getRandomEnglishCharacter(
      final org.json.simple.JSONObject allCharacters,
      final List<String> repeatedCharacters) {
    org.json.simple.JSONObject randomCharacterObject;
    String randomEnglishCharacter = repeatedCharacters.get(0);
    while (repeatedCharacters.contains(randomEnglishCharacter)) {
      randomCharacterObject = getRandomJSONObj(allCharacters);
      randomEnglishCharacter = (String) randomCharacterObject.keySet()
          .iterator().next();
    }
    return randomEnglishCharacter;
  }

  /**
   * Loads JSON given a path, and returns a JSON object. JSON object uses
   * com.googlecode.json-simple:json-simple:1.1.1.
   *
   * @param path path to JSON file (from assets/.)
   * @return loaded JSON object
   */
  public org.json.simple.JSONObject loadJson(final String path) {
    AssetManager am = context.getAssets();
    org.json.simple.JSONObject jsonObject = new org.json.simple.JSONObject();

    try (InputStream is = am.open(path)) {

      JSONParser jsonParser = new JSONParser();
      jsonObject =
          (org.json.simple.JSONObject)
              jsonParser.parse(new InputStreamReader(
                  is, StandardCharsets.UTF_8));
    } catch (IOException | ParseException e) {
      e.printStackTrace();
    }

    return jsonObject;
  }
}
