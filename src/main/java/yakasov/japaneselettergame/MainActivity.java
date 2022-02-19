package yakasov.japaneselettergame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public static final String jsonPath = "characters.json";
    private static final String TAG = MainActivity.class.getName();

    public static org.json.simple.JSONObject allCharacters;

    public static ArrayList<Button> buttons = new ArrayList<>();
    public Button correctButton;

    public static int score;
    public static boolean settingsRefreshed = false;
    public static int maxLetters = 104;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BackCode backCode = new BackCode(this);
        allCharacters = backCode.loadJson(jsonPath);

        populateButtonsArrayList();
        setAllCharacters();
    }

    /*
    TODO:
    - Add developer toggle so developer options actually get applied
    - Prevent the same character from appearing twice
    - Add feedback / previous translation back
    - General aesthetic fixes
     */

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

        Random rand = new Random();
        correctButton = buttons.get(rand.nextInt(4));
        setScoreText();
//        if (previousIndex != -1) {
//            setFeedbackText();
//        }

        BackCode.getCorrectCharacterObj(allCharacters);  // Creates the correct object
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
            String letter = BackCode.getRandomEnglishCharacter(allCharacters, repeatedCharacters);
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
//        tv.setText(getResources().getString(R.string.feedback, japaneseLines.get(previousIndex),
//                englishLines.get(previousIndex)));

    }

    public void buttonPressed(View view) {
        if (findViewById(view.getId()) == correctButton) {
            score += 100;
        } else {
            score -= 50;
        }
        setAllCharacters();
    }

    public void settingsPressed(View view) {
        settingsRefreshed = false;
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void loadPrefs() {
        Log.d(TAG, "Getting prefs");
        SharedPreferences prefs = getSharedPreferences("yakasov.japaneselettergame_preferences", 0);
        try {
            int dev_letter_count = Integer.parseInt(prefs.getString("dev_letter_count", ""));
            if (6 < dev_letter_count && dev_letter_count <= 104) {
                maxLetters = dev_letter_count;
            }
        } catch (java.lang.NumberFormatException e) {
            Log.d(TAG, "dev_letter_count invalid ie not integer");
        }

        Log.d(TAG, BackCode.compareRows(prefs));
    }
}