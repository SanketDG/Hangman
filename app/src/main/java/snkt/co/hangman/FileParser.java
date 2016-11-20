package snkt.co.hangman;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static android.R.attr.x;

/**
 * Created by SanketDG on 19-11-2016.
 */

public class FileParser {

    private HashMap<String, String> hashMap;

    public FileParser(InputStream fileStream) throws IOException {
        if (fileStream != null) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileStream));
            hashMap = new HashMap<String, String>();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                String word = line.trim();
                String[] words = word.split("#");

                hashMap.put(words[0], words[1]);
                Log.i("GAME", words[0] + words[1]);
            }
        }
    }

    public String[] getRandomWord() {
        String[] result = new String[2];



        Random random = new Random();
        List<String> keys = new ArrayList<String>(hashMap.keySet());

        result[0] = keys.get( random.nextInt(keys.size()) );
        result[1] = hashMap.get(result[0]);

        return result;


    }
}
