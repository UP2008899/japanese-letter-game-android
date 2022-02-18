package yakasov.japaneselettergame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String englishPath = "english.txt";
    public static final String japanesePath = "japanese.txt";
    public static final String jsonPath = "characters.json";
    private static final String TAG = MainActivity.class.getName();

    private BackCode backCode;
    public static List<String> englishLines;
    public static List<String> japaneseLines;
    public static JSONObject allCharacters;

    public static ArrayList<Button> buttons = new ArrayList<>();
    public Button correctButton;

    public static int score;
    public static int maxLetters = 104;
    public static int previousIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        backCode = new BackCode(this);
        allCharacters = backCode.loadJson(jsonPath);
        Log.d(TAG, String.valueOf(allCharacters));
        englishLines = backCode.readLine(englishPath);
        japaneseLines = backCode.readLine(japanesePath);

        populateButtonsArrayList();
        setAllCharacters();
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
        loadPrefs();

        int chosenIndex = backCode.getRandomIndex(maxLetters, previousIndex);
        correctButton = buttons.get(chosenIndex % 4);

        setScoreText();
        if (previousIndex != -1) {
            setFeedbackText();
        }

        previousIndex = chosenIndex;

        setMainJapaneseCharacter(chosenIndex);
        setAllEnglishCharacters(chosenIndex);
        setCorrectEnglishCharacter(chosenIndex);
    }

    public void setMainJapaneseCharacter(int chosenIndex) {
        TextView tv = findViewById(R.id.mainJapaneseCharacter);
        tv.setText(japaneseLines.get(chosenIndex));
    }

    public void setAllEnglishCharacters(int chosenIndex) {
        ArrayList<Integer> chosenIndexes = new ArrayList<>();
        chosenIndexes.add(chosenIndex);  // Prevent correct character from being chosen
                                         // This is for the correct button only
        chosenIndexes.add(previousIndex);  // Prevent previous character from being chosen
        int i;
        for (Button button : buttons) {
            i = backCode.getRandomIndexExclusionary(maxLetters, chosenIndexes);
            chosenIndexes.add(i);
            button.setText(englishLines.get(i));
        }
    }

    public void setCorrectEnglishCharacter(int chosenIndex) {
        correctButton.setText(englishLines.get(chosenIndex));
    }

    public void setScoreText() {
        TextView tv = findViewById(R.id.scoreText);
        tv.setText(String.valueOf(score));
    }

    public void setFeedbackText() {
        TextView tv = findViewById(R.id.feedbackText);
        tv.setVisibility(View.VISIBLE);
        tv.setText(getResources().getString(R.string.feedback, japaneseLines.get(previousIndex),
                englishLines.get(previousIndex)));

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
    }
}