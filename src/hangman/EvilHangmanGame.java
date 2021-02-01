package hangman;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class EvilHangmanGame implements IEvilHangmanGame {

    int wordLength;
    int guessesLeft;
    boolean isOver;
    SortedSet<Character> guessedLetters;
    TreeSet<String> wordSet;

    public EvilHangmanGame() throws IOException, EmptyDictionaryException {
        startGame(new File("small.txt"),5);
    }

    @Override
    public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {
        this.guessesLeft = 10;
        this.isOver = false;
        this.wordLength = wordLength;
        this.wordSet = new TreeSet<>();
        Scanner scanner = new Scanner(dictionary);
        while (scanner.hasNext()) {
            String line = scanner.next();
            if(line.length() == wordLength){
                wordSet.add(line.toLowerCase());
            }
        }

        while(!this.isOver){
            //Checks to make sure we have sufficient guesses left
            if(this.guessesLeft < 1){
                isOver = true;
            }

            Scanner in = new Scanner(System.in);
            String userChoice = in.next();
            if(userChoice.length() == 1){
                    try {
                        this.wordSet = (TreeSet<String>) makeGuess(userChoice.charAt(0));
                    } catch (GuessAlreadyMadeException e) {
                        throw new IOException("Guess already made");
                    }
            } else {
                throw new IOException("Incorrect input length");
            }
        }
    }

    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        //TODO: Implement partitioning
        //TODO: Implement Evil Algorithm
        return null;
    }

    @Override
    public SortedSet<Character> getGuessedLetters() {
        return guessedLetters;
    }
}
