package snkt.co.hangman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class SimpleDictionary {
    private int MIN_WORD_LENGTH = 6;
    private int MAX_WORD_LENGTH = 10;
    private ArrayList<String> words;

    public SimpleDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        words = new ArrayList<>();
        String line;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() >= MIN_WORD_LENGTH && word.length() <= MAX_WORD_LENGTH)
              words.add(line.trim());
        }
    }

    public boolean isWord(String word) {
        return words.contains(word);
    }

    public String getRandomWord()
    {
        Random random = new Random();
        return words.get(random.nextInt(words.size()));
    }


}
