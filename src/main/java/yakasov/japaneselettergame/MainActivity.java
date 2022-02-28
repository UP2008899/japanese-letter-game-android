package yakasov.japaneselettergame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Random;

/**
 * MainActivity class defines the GUI and all related code for the main screen.
 * All methods defined here directly affect GUI elements. Any method that does
 * not affect the GUI is placed in BackCode.
 *
 * <p>The main game takes place in this activity.
 */
public final class MainActivity extends AppCompatActivity {

  private static final String JSONPATH = "characters.json";
  private static final String YOONJSONPATH = "yoon_characters.json";
  private static final String TAG = MainActivity.class.getName();
  private static final ArrayList<Button> BUTTONS = new ArrayList<>();
  private static final Random RAND = new Random();
  private static final int SCOREINCREASE = 100;
  private static final int SCOREDECREASE = -50;
  private org.json.simple.JSONObject allCharacters;
  private org.json.simple.JSONObject yoonCharacters;
  private org.json.simple.JSONObject usedCharacterSet;
  private Button correctButton;
  private Boolean useCorrectButtonColour = false;
  private Boolean previousAnswerCorrect = false;
  private int score;
  private boolean settingsRefreshed = false;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    BackCode backCode = new BackCode(this);
    allCharacters = backCode.loadJson(JSONPATH);
    yoonCharacters = backCode.loadJson(YOONJSONPATH);

    populateButtonsArrayList();
    // Do not set characters here, onResume is called after
    // onCreate so call function there instead
  }

  @Override
  public void onResume() {
    super.onResume();
    setAllCharacters();
  }

  /**
   * Handles all logic for loading a new guess. This includes selecting a
   * correct button (and then later setting it to the correct character),
   * updating the score and feedback text, reloading preferences if the settings
   * activity has been loaded, and finally setting all the characters onscreen.
   */
  public void setAllCharacters() {
    correctButton = BUTTONS.get(RAND.nextInt(BUTTONS.size()));
    setScoreText();

    if (!settingsRefreshed) {
      loadPrefs();
      settingsRefreshed = true;
    }

    setButtonColours();

    BackCode.setPreviousCharacters();
    if (!"".equals(BackCode.returnCorrectEnglishCharacter())) {
      setFeedbackText();
    }

    BackCode.getCorrectCharacterObj(usedCharacterSet);
    setMainJapaneseCharacter();
    setAllEnglishCharacters();
    setCorrectEnglishCharacter();
  }

  public void setScoreText() {
    TextView tv = findViewById(R.id.scoreText);
    tv.setText(String.valueOf(score));
  }

  /**
   * Loads preferences for the app from Android internal storage. Preferences
   * are stored in yakasov.japaneselettergame_preferences, and used as the prefs
   * SharedPreferences object.
   *
   * <p>yoonPreference = if yōon characters should be used in the game
   * devCorrectButtonColour = if the correct button should be coloured
   * differently
   *
   * <p>Preferences for individual selected rows is handled separately in
   * BackCode.compareRows.
   */
  public void loadPrefs() {
    Log.d(TAG, "Getting prefs");
    SharedPreferences prefs = getSharedPreferences(
        "yakasov.japaneselettergame_preferences", 0);
    boolean yoonPreference = prefs.getBoolean(
        "hiragana_yoon_preference", false);
    boolean devCorrectButtonColour = prefs.getBoolean(
        "dev_correct_button_colour", false);

    if (yoonPreference) {
      usedCharacterSet = yoonCharacters;
    } else {
      usedCharacterSet = allCharacters;
    }

    useCorrectButtonColour = devCorrectButtonColour;

    Log.d(TAG, BackCode.compareRows(prefs));
    // BackCode.compareRows needs to be called,
    // and returns a string of chosen rows.
    // Move to alone line in future?
  }

  /**
   * Sets all button colours to their default colour, then sets the correct
   * button to a different shade if selected in preferences.
   */
  public void setButtonColours() {
    for (Button button : BUTTONS) {
      button.setBackgroundColor(getResources().getColor(
          R.color.purple_200));
    }
    if (Boolean.TRUE.equals(useCorrectButtonColour)) {
      correctButton.setBackgroundColor(getResources().getColor(
          R.color.purple_100));
    }
  }

  /**
   * Sets the previous translation text below the main Japanese character. The
   * translation text can be two different strings (defined in strings.xml)
   * depending on if the guess was correct.
   */
  public void setFeedbackText() {
    TextView tv = findViewById(R.id.feedbackText);
    tv.setVisibility(View.VISIBLE);
    if (Boolean.TRUE.equals(previousAnswerCorrect)) {
      tv.setText(getResources().getString(R.string.correct_answer));
    } else {
      tv.setText(
          getResources()
              .getString(
                  R.string.feedback,
                  BackCode.returnPreviousJapaneseCharacter(),
                  BackCode.returnPreviousEnglishCharacter()));
    }
  }

  public void setMainJapaneseCharacter() {
    TextView tv = findViewById(R.id.mainJapaneseCharacter);
    tv.setText(BackCode.returnCorrectJapaneseCharacter());
  }

  /**
   * Uses the getRandomEnglishCharacter method in BackCode to generate random
   * English characters for use with the buttons, and fills the buttons with
   * them. Also creates the ArrayList repeatedCharacters so no characters appear
   * twice.
   */
  public void setAllEnglishCharacters() {
    ArrayList<String> repeatedCharacters = new ArrayList<>();
    repeatedCharacters.add(BackCode.returnCorrectEnglishCharacter());
    for (Button button : BUTTONS) {
      String letter = BackCode.getRandomEnglishCharacter(
          usedCharacterSet, repeatedCharacters);
      repeatedCharacters.add(letter);
      button.setText(letter);
    }
  }

  public void setCorrectEnglishCharacter() {
    correctButton.setText(BackCode.returnCorrectEnglishCharacter());
  }

  /**
   * Places all onscreen BUTTONS (that the user can press in the bottom half of
   * the screen) into ArrayList buttons.
   */
  public void populateButtonsArrayList() {
    Button buttonA = findViewById(R.id.buttonA);
    Button buttonB = findViewById(R.id.buttonB);
    Button buttonC = findViewById(R.id.buttonC);
    Button buttonD = findViewById(R.id.buttonD);
    BUTTONS.add(buttonA);
    BUTTONS.add(buttonB);
    BUTTONS.add(buttonC);
    BUTTONS.add(buttonD);
  }

  @Override
  public boolean onCreateOptionsMenu(final Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main_menu, menu);
    return true;
  }

  /**
   * Called upon any button being pressed. Depending on the button, will change
   * score and the previousAnswerCorrect variable to different values before
   * calling setAllCharacters to create a new guess.
   *
   * @param view basic UI control component. Required for button onClick to
   *             function.
   */
  public void buttonPressed(final View view) {
    if (findViewById(view.getId()) == correctButton) {
      score += SCOREINCREASE;
      previousAnswerCorrect = true;
    } else {
      score += SCOREDECREASE;
      previousAnswerCorrect = false;
    }
    setAllCharacters();
  }

  /**
   * Called upon the settings button in the toolbar being pressed. Sets
   * settingsRefreshed to false so they can be updated when returning to
   * MainActivity, and opens SettingsActivity.
   *
   * @param item interface for direct access to a menu item
   */
  public void settingsPressed(final MenuItem item) {
    settingsRefreshed = false;
    Intent intent = new Intent(this, SettingsActivity.class);
    startActivity(intent);
  }

  /**
   * Called upon the reset button in the toolbar being pressed. Sets the score
   * to 0, sets the previous translation to invisible, and generates a new
   * guess. Similar to reopening the app.
   *
   * @param item interface for direct access to a menu item
   */
  public void resetPressed(final MenuItem item) {
    score = 0;
    setAllCharacters();
    TextView tv = findViewById(R.id.feedbackText);
    tv.setVisibility(View.INVISIBLE);
  }
}
