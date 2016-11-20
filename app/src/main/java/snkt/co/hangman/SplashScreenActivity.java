package snkt.co.hangman;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import static snkt.co.hangman.R.id.about;
import static snkt.co.hangman.R.id.twoPlayerMode;

public class SplashScreenActivity extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;

    private Button normalMode, streakMode, twoPlayerMode, aboutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        normalMode = (Button) findViewById(R.id.normalMode);
        streakMode = (Button) findViewById(R.id.genreMode);
        twoPlayerMode = (Button) findViewById(R.id.twoPlayerMode);
        aboutButton = (Button) findViewById(R.id.about);


        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                normalMode.setVisibility(View.VISIBLE);
                streakMode.setVisibility(View.VISIBLE);
                twoPlayerMode.setVisibility(View.VISIBLE);
                aboutButton.setVisibility(View.VISIBLE);
                // close this activity
            }
        }, SPLASH_TIME_OUT);
    }

    // Button handler for "Normal Mode" button
    public void goToGame(View view) {
        Intent intent = new Intent(SplashScreenActivity.this, GameActivity.class);
        intent.putExtra("genre", -1);
        startActivity(intent);
    }

    public void goToAdvancedGame(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose genre.")
                .setItems(R.array.genre, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(SplashScreenActivity.this, GameActivity.class);
                        intent.putExtra("genre", which);
                        startActivity(intent);
                    }
                });

        builder.show();

    }

    public void showAbout(View v) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("About")
                .setMessage(Html.fromHtml(getString(R.string.about)))
                .setPositiveButton("Ok", null)
                .create();

        dialog.show();
        ((TextView)dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());

    }


}