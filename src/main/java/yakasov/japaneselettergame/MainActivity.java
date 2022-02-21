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

public class MainActivity extends AppCompatActivity {

  private static final String JSONPATH = "characters.json";
  private static final String YOONJSONPATH = "yoon_characters.json";
  private static final String TAG = MainActivity.class.getName();
  private static final ArrayList<Button> buttons = new ArrayList<>();
  private final Random rand = new Random();
  private org.json.simple.JSONObject allCharacters;
  private org.json.simple.JSONObject yoonCharacters;
  private org.json.simple.JSONObject usedCharacterSet;
  private Button correctButton;
  private Boolean previousAnswerCorrect = false;
  private int score;
  private boolean settingsRefreshed = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    BackCode backCode = new BackCode(this);
    allCharacters = backCode.loadJson(JSONPATH);
    yoonCharacters = backCode.loadJson(YOONJSONPATH);

    populateButtonsArrayList();
    setAllCharacters();
  }

  @Override
  public void onResume(){
    super.onResume();
    setAllCharacters();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main_menu, menu);
    return true;
  }

  public void populateButtonsArrayList() {
    Button buttonA = findViewById(R.id.buttonA);
    Button buttonB = findViewById(R.id.buttonB);
    Button buttonC = findViewById(R.id.buttonC);
    Button buttonD = findViewById(R.id.buttonD);
    buttons.add(buttonA);
    buttons.add(buttonB);
    buttons.add(buttonC);
    buttons.add(buttonD);
  }

  public void setAllCharacters() {
    if (!settingsRefreshed) {
      loadPrefs();
      settingsRefreshed = true;
    }

    correctButton = buttons.get(rand.nextInt(4));
    setScoreText();

    BackCode.setPreviousCharacters();
    if (!"".equals(BackCode.returnCorrectEnglishCharacter())) {
      setFeedbackText();
    }

    BackCode.getCorrectCharacterObj(usedCharacterSet); // Creates the correct object
    setMainJapaneseCharacter();
    setAllEnglishCharacters();
    setCorrectEnglishCharacter();
  }

  public void setMainJapaneseCharacter() {
    TextView tv = findViewById(R.id.mainJapaneseCharacter);
    tv.setText(BackCode.returnCorrectJapaneseCharacter());
  }

  public void setAllEnglishCharacters() {
    ArrayList<String> repeatedCharacters = new ArrayList<>();
    repeatedCharacters.add(BackCode.returnCorrectEnglishCharacter());
    for (Button button : buttons) {
      String letter = BackCode.getRandomEnglishCharacter(usedCharacterSet, repeatedCharacters);
      repeatedCharacters.add(letter);
      button.setText(letter);
    }
  }

  public void setCorrectEnglishCharacter() {
    correctButton.setText(BackCode.returnCorrectEnglishCharacter());
  }

  public void setScoreText() {
    TextView tv = findViewById(R.id.scoreText);
    tv.setText(String.valueOf(score));
  }

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

  public void buttonPressed(View view) {
    if (findViewById(view.getId()) == correctButton) {
      score += 100;
      previousAnswerCorrect = true;
    } else {
      score -= 50;
      previousAnswerCorrect = false;
    }
    setAllCharacters();
  }

  public void settingsPressed(MenuItem item) {
    settingsRefreshed = false;
    Intent intent = new Intent(this, SettingsActivity.class);
    startActivity(intent);
  }

  public void resetPressed(MenuItem item) {
    score = 0;
    setAllCharacters();
    TextView tv = findViewById(R.id.feedbackText);
    tv.setVisibility(View.INVISIBLE);
  }

  public void loadPrefs() {
    Log.d(TAG, "Getting prefs");
    SharedPreferences prefs = getSharedPreferences("yakasov.japaneselettergame_preferences", 0);
    boolean yoonPreference = prefs.getBoolean("hiragana_yoon_preference", false);
    if (yoonPreference) {
      usedCharacterSet = yoonCharacters;
    } else {
      usedCharacterSet = allCharacters;
    }

    Log.d(TAG, BackCode.compareRows(prefs));
  }
}
