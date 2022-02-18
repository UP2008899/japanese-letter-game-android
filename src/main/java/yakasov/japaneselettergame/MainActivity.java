package yakasov.japaneselettergame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String englishPath = "english.txt";
    public static final String japanesePath = "japanese.txt";

    public static List<String> englishLines;
    public static List<String> japaneseLines;
    private BackCode backCode;

    public static ArrayList<Button> buttons = new ArrayList<>();
    public Button correctButton;

    public static int score;
    public static int maxLetters = 10;
    public static int previousIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        backCode = new BackCode(this);
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
        //tv.setText(getResources().getString(R.string.score, score));
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
}