package snkt.co.hangman;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import static android.R.id.message;

public class GameActivity extends AppCompatActivity {

    private final int TOTAL_MOVES = 6;

    /* for the GridView adapter */
    private LetterAdapter letterAdapter;
    private GridView letters;


    /* hangman image */
    private ImageView hangman;

    /* for the data */
    private SimpleDictionary simpleDictionary;
    private FileParser fileParser;

    private TextView moveCounterText;
    private TextView movesRemainingText;

    private Button hintButton;

    private String currentWord;
    private String currentWordHint;

    /* for the array of TextViews holding the currentWord */
    private LinearLayout wordLayout;
    private TextView[] wordView;

    private int moveCounter;
    private int movesRemaining;

    private int correctCounter;

    private int genre;

    private boolean oldSchool = false;

    private final static String TAG = "HANGMAN";

    /* for holding the already used hint indexes, so that they are not repeated.*/
    private ArrayList<Integer> usedHintLetters;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.restart:
                playGame();
                return true;
            case R.id.help:
                showHowToPlay();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Getting the genre passed through SplashScreenActivity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            genre = extras.getInt("genre");
        }

        // view declarations
        letters = (GridView) findViewById(R.id.letters);
        hangman = (ImageView) findViewById(R.id.hangman);
        moveCounterText = (TextView) findViewById(R.id.move_counter);
        movesRemainingText = (TextView) findViewById(R.id.moves_remaining);
        wordLayout = (LinearLayout) findViewById(R.id.the_word);
        hintButton = (Button) findViewById(R.id.hint_button);

        currentWord = "";

        AssetManager assetManager = getAssets();

        InputStream inputStream = null;

        Log.i(TAG, "genre = " + genre);

        if(genre == -1) {
            // This means that the user has selected the "OLD SCHOOL MODE" in the SplashScreen.
            oldSchool = true;
            setTitle("Hangman (Old School)");

            // open the words.txt file which contains all the words.
            try {
                inputStream = assetManager.open("words.txt");
                simpleDictionary = new SimpleDictionary(inputStream);
            }
            catch (IOException e) {
                Toast.makeText(this, "Could not load data.", Toast.LENGTH_LONG).show();
            }
        }
        else {
            /* The user has selected the Genre Mode. Based on the index of the genre selected,
               we load the appropriate dataset.
            */
            try {
                switch (genre) {
                    case 0:
                        inputStream = assetManager.open("normal.txt");
                        setTitle(getTitle() + " - Dictionary");
                        break;
                    case 1:
                        inputStream = assetManager.open("hollywood.txt");
                        setTitle(getTitle() + " - Hollywood");
                        break;
                    case 2:
                        inputStream = assetManager.open("bollywood.txt");
                        setTitle(getTitle() + " - Bollywood");
                        break;
                    case 3:
                        inputStream = assetManager.open("famous.txt");
                        setTitle(getTitle() + " - Famous");
                        break;
                    case 4:
                        inputStream = assetManager.open("riddles.txt");
                        setTitle(getTitle() + " - Riddles");
                        break;
                }
                fileParser = new FileParser(inputStream);
            } catch (IOException e) {
                Toast.makeText(this, "Could not load data.", Toast.LENGTH_LONG).show();
            }
        }

        playGame();

    }

    private void playGame() {

        // initialize hint letters array
        usedHintLetters =  new ArrayList<Integer>(currentWord.length());

        // Set default image
        hangman.setImageResource(R.drawable.error0);

        // Populate the gridview with the buttons that will act like the keyboard.
        letterAdapter=new LetterAdapter(this);
        letters.setAdapter(letterAdapter);
        letters.setGravity(Gravity.CENTER);

        // Initialise the TextViews.
        movesRemainingText.setText("Remaining: " + TOTAL_MOVES);
        moveCounterText.setText("Moves: 0");

        moveCounter = 0;
        movesRemaining = 6;
        correctCounter = 0;

        /* Below lines fetch the current word based on the mode selected by the user.
           For old school mode, the SimpleDictionary class is used to get a random word.

           For genre mode, a FileParser class is used to get a random word and its respective hint.
           The FileParser.getRandomWord() returns a two-element array containing the word and the
           hint.

           The hintButton handler for old school and genre mode are also set here, as they have
           different behaviours.

           For old school mode, one letter of the word is placed for the user to "acknowledge" that
           guess.

           For genre mode, a hint is shown as a sentence which could a movie quote or a riddle!
         */

        if(oldSchool == true) {
            String newWord = simpleDictionary.getRandomWord();
            while(newWord.toUpperCase().equals(currentWord)) newWord = simpleDictionary.getRandomWord();

            currentWord = newWord.toUpperCase();

            hintButton.setText("PLACE HINT");

            hintButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Random random = new Random();

                    int index = random.nextInt(currentWord.length());
                    while (usedHintLetters.contains(index)) index = random.nextInt(currentWord.length());

                    String letter = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

                    usedHintLetters.add(index);

                    Log.i("GAME", usedHintLetters.toString());

                    Log.i("GAME", "index = " + index);

                    wordView[index].setTextColor(Color.BLACK);

                    /* Get the key of the keyboard.
                       The color is set to yellow to differentiate it that is a hint.
                       The user has to click it again to acknowledge the hint.
                     */
                    View key = letters.getChildAt(letter.indexOf(currentWord.charAt(index)));
                    key.setBackgroundColor(Color.YELLOW);
                }
            });

        }
        else {

            hintButton.setText("SHOW HINT");

            String[] newWord = fileParser.getRandomWord();
            while(newWord[0].toUpperCase().equals(currentWord)) newWord = fileParser.getRandomWord();

            currentWord = newWord[0].toUpperCase();
            currentWordHint = newWord[1];

            hintButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(GameActivity.this);
                    alert.setTitle("Hint");
                    alert.setMessage(currentWordHint);
                    alert.setPositiveButton("OK",null);
                    alert.show();
                }
            });

        }



        Log.i("SanketDG", "newWord = " + currentWord);

        wordView = new TextView[currentWord.length()];

        wordLayout.removeAllViews();

        /* The current word fetched from the dataset is binded to the TextView array
           created earlier. Each TextView contains one letter from the current word
           and it is set to transparent color, so that it's invisible.
         */

        for(int i = 0; i < currentWord.length(); i++) {
            wordView[i] = new TextView(this);
            wordView[i].setText("" + currentWord.charAt(i));

            LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(2, 2, 2, 2);


            wordView[i].setGravity(Gravity.CENTER);
            wordView[i].setIncludeFontPadding(false);
            wordView[i].setTextSize(20);

            if(currentWord.charAt(i) != ' ')
                wordView[i].setBackgroundResource(R.drawable.textlines);
            else
                // if its a space, just set some extra margins so that it can be differentiated.
                layoutParams.setMargins(5,5,5,5);

            wordView[i].setLayoutParams(layoutParams);
            wordView[i].setTextColor(Color.TRANSPARENT);

            wordLayout.addView(wordView[i]);

        }
    }

    public void letterPressed(View view) {
        moveCounter++;

        moveCounterText.setText("Moves: " + moveCounter);


        String letterString = ((TextView) view).getText().toString();
        Log.i("Game", "letter =  " + letterString);

        char letter = letterString.charAt(0);

        boolean correct = false;

        /* This is where the main operation takes place after any key is pressed. */

        for (int k = 0; k < currentWord.length(); k++) {
            if (currentWord.charAt(k) == letter) {
                /* The user is correct in his guess, so we set the correct to true, increment the
                correct counter, add the letter to the used hint letters ( so that it is not shown
                again when the user clicks on hint button )

                The respective letter which was set to invisible in above code, is set to black
                for it to become visible.

                The button is disabled and the color of the button is changed to green.
                 */
                correct = true;
                correctCounter++;

                usedHintLetters.add(k);

                wordView[k].setTextColor(Color.BLACK);
                view.setEnabled(false);
                view.setBackgroundColor(Color.GREEN);
            }
        }

        if (!correct) {
            /* The user is incorrect in his guess */
            movesRemaining--;

            view.setEnabled(false);
            view.setBackgroundColor(Color.RED);

            /* The user has not depleted all its chances */
            if (movesRemaining >= 0) {
                /* decrease remaining moves */
                movesRemainingText.setText("Remaining: " + movesRemaining);

                /* instead of doing it manually every time,
                we use this method to construct a file name everytime. */
                String resName = "error" + String.valueOf(TOTAL_MOVES - movesRemaining);
                hangman.setImageResource(getResources().getIdentifier(resName, "drawable", getPackageName()));
            } else {

                /* The user has used up all its chances. He has lost */
                AlertDialog.Builder loseBuild = new AlertDialog.Builder(this);
                loseBuild.setCancelable(false);
                loseBuild.setTitle("NO!");
                loseBuild.setMessage("You lose!\n\nThe answer was:\n\n" + currentWord);
                loseBuild.setPositiveButton("Play Again",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                playGame();
                            }
                        });

                loseBuild.setNegativeButton("Main Menu",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                GameActivity.this.finish();
                            }
                        });

                loseBuild.show();
            }


        }

        /* The user has guessed everything correct. He has own. */

        if ((correct) && correctCounter == currentWord.replaceAll("\\s", "").length()) {
            AlertDialog.Builder winBuild = new AlertDialog.Builder(this);
            winBuild.setCancelable(false);
            winBuild.setTitle("YES!");
            winBuild.setMessage("You win!\n\n");
            winBuild.setPositiveButton("Play Again",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            playGame();
                        }
                    });

            winBuild.setNegativeButton("Main Menu",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            GameActivity.this.finish();
                        }
                    });

            winBuild.show();
        }

    }

    // onClick handler for "How to Play" menu item
    public void showHowToPlay() {
        Spanned helpMessage;
        if (oldSchool == true) {
            helpMessage = Html.fromHtml(getString(R.string.help) + getString(R.string.help_old_school));
        }
        else {
            helpMessage = Html.fromHtml(getString(R.string.help) + getString(R.string.help_genre));
        }
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("How To Play")
                .setMessage(helpMessage)
                .setPositiveButton("Ok", null)
                .create();

        dialog.show();
        ((TextView)dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
    }
}
