package yakasov.japaneselettergame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String englishPath = "english.txt";
    public static final String japanesePath = "japanese.txt";
    public static final String TAG = MainActivity.class.getSimpleName();

    public static List<String> englishLines;
    public static List<String> japaneseLines;
    private BackCode backCode;

    public static ArrayList<Button> buttons = new ArrayList<Button>();
    public static Button correctButton;

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
        Button buttonA = (Button)findViewById(R.id.buttonA);
        Button buttonB = (Button)findViewById(R.id.buttonB);
        Button buttonC = (Button)findViewById(R.id.buttonC);
        Button buttonD = (Button)findViewById(R.id.buttonD);
        buttons.add(buttonA);
        buttons.add(buttonB);
        buttons.add(buttonC);
        buttons.add(buttonD);
    }

    public void setAllCharacters() {
        int chosenIndex = backCode.getRandomIndex(104);
        correctButton = buttons.get(chosenIndex % 4);

        setMainJapaneseCharacter(chosenIndex);
        setAllEnglishCharacters(chosenIndex);
        setCorrectEnglishCharacter(chosenIndex);
    }

    public void setMainJapaneseCharacter(int chosenIndex) {
        TextView tv = findViewById(R.id.mainJapaneseCharacter);
        tv.setText(japaneseLines.get(chosenIndex));
    }

    public void setAllEnglishCharacters(int chosenIndex) {
        for (Button button : buttons) {
            button.setText(englishLines.get(backCode.getRandomIndexExclusionary(104, chosenIndex)));
        }
    }

    public void setCorrectEnglishCharacter(int chosenIndex) {
        correctButton.setText(englishLines.get(chosenIndex));
    }

    public void buttonPressed(View view) {
        if (findViewById(view.getId()) == correctButton) {
            setAllCharacters();
        }
    }
}