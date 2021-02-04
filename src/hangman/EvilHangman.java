package hangman;

import java.io.IOException;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;

public class EvilHangman {

    public static void main(String[] args) throws IOException, EmptyDictionaryException, GuessAlreadyMadeException {
        try {
            EvilHangmanGame game = new EvilHangmanGame();
            while (!game.isOver) {
                //Checks to make sure we have sufficient guesses left
                if (game.guessesLeft == 0) {
                    System.out.println("You lose! The word was: " + game.wordSet.iterator().next());
                    game.isOver = true;
                    break;
                }

                try {
                    char userChoice = displayAndPrompt(game);
                    SortedSet guess = (TreeSet) game.makeGuess(userChoice);

                    if (guess.size() > 0) {
                        game.wordSet = (TreeSet<String>) guess;
                        System.out.println("Yes, there are " + userChoice); //TODO: show how many of the guessed there are
                        game.guessesLeft--;
                    }
                    if (guess.size() <= 0) {
                        System.out.println("No, there is no " + userChoice);
                        game.guessesLeft--;
                    }

                    game.checkWin();

                } catch (IOException e){
                    System.out.println("Invalid input type");
                }

                System.out.println("------------");
            }
        } catch (IOException | EmptyDictionaryException e) {
            e.printStackTrace();
        }
    }

    private static char displayAndPrompt(EvilHangmanGame game) throws IOException, GuessAlreadyMadeException{
        Scanner scanner = new Scanner(System.in);  // Create a Scanner object
        char userGuess = '-';
        System.out.println("You have " + game.guessesLeft + " guesses left");// Enter username and press Enter

        if (game.getGuessedLetters() != null) {
            System.out.print("Used letters:");
            for (char c : game.getGuessedLetters()){
                System.out.print(c + " ");
            }
            System.out.println("");
        } else {
            System.out.println("Used letters:");
        }
        System.out.println("Word: " + game.getStringRep());
        System.out.print("Enter guess: ");
        userGuess = scanner.nextLine().toLowerCase().charAt(0);
        if (!Character.isLetter(userGuess)){
            throw new IOException("Invalid input type");
        }
        game.previousGuess = userGuess;
        return userGuess;
    }
}
