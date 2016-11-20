package snkt.co.hangman;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;

import static snkt.co.hangman.R.layout.letter;

/**
 * Created by Dado on 2015-09-24.
 */
public class LetterAdapter extends BaseAdapter {
    private String[] letters;
    private LayoutInflater letterInf;


    public LetterAdapter(Context c) {
        letters = new String[26];

        // for getting the characters in alphabetical order
        for (int a = 0; a < letters.length; a++) {
            letters[a] = "" + (char)(a + 'A');
        }

        letterInf = LayoutInflater.from(c);
    }


    @Override
    public int getCount() {
        return letters.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //create a button for the letter at this position in the alphabet
        Button letterBtn;
        if (convertView == null) {
            //inflate the button layout
            letterBtn = (Button)letterInf.inflate(letter, parent, false);
        } else {
            letterBtn = (Button) convertView;
        }
        //set the text to this letter
        letterBtn.setText(letters[position]);

        letterBtn.setTextColor(Color.BLACK);
        letterBtn.setGravity(Gravity.CENTER);

        return letterBtn;
    }
}
