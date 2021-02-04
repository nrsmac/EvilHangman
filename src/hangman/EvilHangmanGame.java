package hangman;

import com.sun.source.tree.Tree;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EvilHangmanGame implements IEvilHangmanGame {

    int wordLength;
    int guessesLeft;
    boolean isOver;
    SortedSet<Character> guessedLetters;
    TreeSet<String> wordSet;
    char previousGuess;
    private String minCharSetStr;
    public String currentPattern;


    public EvilHangmanGame() throws IOException, EmptyDictionaryException {
        startGame(new File("dictionary.txt"), 5);
    }


    @Override
    public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {
        for (int i = 0; i < wordLength; i++){
            currentPattern = currentPattern+"-";
        }
        this.guessesLeft = 26; //Can you call me back on slack?
        this.guessedLetters = new TreeSet<>();
        this.isOver = false;
        this.wordLength = wordLength;
        this.wordSet = new TreeSet<>();
        Scanner scanner = new Scanner(dictionary);
        if (!scanner.hasNext()){
            throw new EmptyDictionaryException();
        }
        while (scanner.hasNext()) { //TODO: Handle dictionary exceptions by spec
            String line = scanner.next();
            if (line.length() == wordLength) {
                this.wordSet.add(line.toLowerCase());
            }

        }
        if (wordSet.size() == 0){
            throw new EmptyDictionaryException();
        }
    }

    public void checkWin() {
        if (this.wordSet.size() == 1) { //Checks for unlikely win
            boolean containsAllLetters = true;
            for (char c : this.guessedLetters){
                String s = "" + c;
                if(!this.wordSet.iterator().next().contains(s)){
                    containsAllLetters = false;
                }
            }
            if (containsAllLetters) {
                System.out.println("You win! The word was: " + this.wordSet.iterator().next());
                this.isOver = true;
            }
        }
    }

    @Override
    public Set<String> makeGuess(char g) throws GuessAlreadyMadeException {
        TreeMap<String, TreeSet<String>> partitions = new TreeMap<>();

        String s = g + "";
        s = s.toLowerCase();
        char guess = s.charAt(0);

        //Check for guess already made
        if(this.guessedLetters.contains(guess) || this.guessedLetters.contains(g)){
            throw new GuessAlreadyMadeException();
        }

        for (String word : this.wordSet) {
            String key = getSubsetKey(word, guess);
            TreeSet<String> currentSubset = new TreeSet<>();
                if (partitions.get(key) == null) {
                    currentSubset.add(word);
                    partitions.put(key, currentSubset);
                } else {
                    //Update, add word to the set
                    partitions.get(key).add(word);
                }
        }


        guessedLetters.add(guess);

        this.wordSet = getMaxPartition(partitions, guess);
        return wordSet;
    }

    private TreeSet<String> getMaxPartition(TreeMap<String, TreeSet<String>> partitions, char guess) {
        Map<String, TreeSet<String>> subPartitions = new TreeMap<>();
        int maxSize = 0;
        for (Map.Entry<String, TreeSet<String>> entry : partitions.entrySet()) {
            if(entry.getValue().size() > maxSize) {
                subPartitions.clear();
                subPartitions.put(entry.getKey(), entry.getValue());
                maxSize = entry.getValue().size();
                currentPattern = entry.getKey();
            } else if (entry.getValue().size() == maxSize) {
                subPartitions.put(entry.getKey(), entry.getValue());
            }
        }

        if (subPartitions.size() > 1) {
            subPartitions = getPartitionWithFewestInstances(partitions);
        }

        if (subPartitions.size() > 1) {
            subPartitions = getRightmostPartition(partitions);
        }

        return subPartitions.get(currentPattern);
    }

    private TreeMap<String, TreeSet<String>> getPartitionWithFewestInstances(TreeMap<String, TreeSet<String>> partitions) {
        TreeMap<String, TreeSet<String>> partitionWithFewestInstances = new TreeMap<>();
        int maxCount = 0;
        for (Map.Entry<String, TreeSet<String>> entry : partitions.entrySet()) {
            int dashCount = 0;
            for (int i = 0; i < entry.getKey().length(); i++) {
                if (entry.getKey().charAt(i) == '-') {
                    dashCount++;
                }
            }
            if (dashCount > maxCount) {
                partitionWithFewestInstances.clear();
                partitionWithFewestInstances.put(entry.getKey(), entry.getValue());
                maxCount = dashCount;
                currentPattern = entry.getKey();

            } else if (dashCount == maxCount){
                partitionWithFewestInstances.put(entry.getKey(), entry.getValue());
            }
        }

        return partitionWithFewestInstances;
    }

    private TreeMap<String, TreeSet<String>> getRightmostPartition(TreeMap<String, TreeSet<String>> partitions) {
        TreeMap<String, TreeSet<String>> rightmostPartition = new TreeMap<>();
        int maxDashCount = 0;
        for (Map.Entry<String, TreeSet<String>> entry : partitions.entrySet()) {
            int dashCount = 0;
            for (int i = 0; i < entry.getKey().length(); i++) {
                if (entry.getKey().charAt(i) == '-') {
                    dashCount++;
                } else {
                    break;
                }
            }

            if (dashCount > maxDashCount) {
                rightmostPartition.clear();
                rightmostPartition.put(entry.getKey(), entry.getValue());
                maxDashCount = dashCount;
                currentPattern = entry.getKey();
            }

            else if (dashCount == maxDashCount){
//                rightmostPartition.clear();
                rightmostPartition.put(entry.getKey(), entry.getValue());
            }
        }
        return rightmostPartition;
    }

    @Override
    public SortedSet<Character> getGuessedLetters() {
        return guessedLetters;
    }

    private String getSubsetKey(String word, char guessedLetter) {
        char[] key = word.toCharArray();
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) != guessedLetter && !guessedLetters.contains(guessedLetter)) {
                key[i] = '-';
            }
        }
        return new String(key);
    }


    public String getStringRep() {

        String word = currentPattern;
        char[] key = word.toCharArray();

        //populate out with dashes to avoid index errors
        for (char c : guessedLetters) {
            for (int i = 0; i < word.length(); i++) {
                if (word.charAt(i) == c) {
                    key[i] = c;
                }
            }
        }
        return Arrays.toString(key).replace("]","").replace("[","");
    }

}
