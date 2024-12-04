import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class BreakingEnigma {
    public static boolean encryptAlg(String desiredHash, String recievedPlugBoard, String wordList){
        try {
            File file = new File(wordList);
            Scanner scanner = new Scanner(file);
            String word = scanner.next();
            List <String> saltedWord = saltGen(word);
            List <String> possibleHashs = new ArrayList<>();
            for(int i = 0 ; i < saltedWord.size(); i++){
               //possibleHashs = plugBoard(recievedPlugBoard);
            }
            /*while (scanner.hasNext()) {
                String word = scanner.next();
                String saltedWord = saltGen(word);
            }*/
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + wordList);
        }
        return false;
    }
    public List<String> plugBoard(String recievedPlugBoard){
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        HashMap<Character, Character> plugBoardMap = parsePlugBoard(recievedPlugBoard);
        
        return null;
    }
    public static List<String> saltGen(String word){
        List<String> saltedCombinations = new ArrayList<>();
        String alphabetSalt = "!#$%&*+-:;<=>?@";
        for (int i = 0; i < alphabetSalt.length(); i++) {
            for (int j = 0; j < alphabetSalt.length(); j++) {
                saltedCombinations.add(alphabetSalt.charAt(i) + "" + alphabetSalt.charAt(j) + word);
                saltedCombinations.add(word + alphabetSalt.charAt(i) + "" + alphabetSalt.charAt(j));
            }
        }
        return saltedCombinations;
    }
    public String Enhanced_Caesar(String plugBoardWord){
        return null;
    }
    public static boolean validateHash(String hash){
        String hashPattern = "^[a-fA-F0-9]{64}$";
        if (hash == null || hash.trim().isEmpty()) {
            return false;
        }
        return hash.trim().matches(hashPattern);
    }
    public static boolean validatePlugBoard(String plugBoardInput){
        HashMap<Character, Character> plugBoardMap = parsePlugBoard(plugBoardInput);
        if (plugBoardMap == null) {
           return false;
        }
        return true;
    }
    public static boolean validateFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.isFile()) {
                return true;
            } else {
                System.out.println("The path exists but is not a file.");
            }
        } else {
            System.out.println("File does not exist.");
        }
        return false;
    }
    private static HashMap<Character, Character> parsePlugBoard(String plugBoardInput) {
        HashMap<Character, Character> plugBoardMap = new HashMap<>();
        plugBoardInput = plugBoardInput.replaceAll("[{}']", "").trim();
        plugBoardInput = plugBoardInput.replaceAll("\\s+", "");
        String[] mappings = plugBoardInput.split(",");
        for (String mapping : mappings) {
            // Split the "key:value" pair
            String[] pair = mapping.split(":");

            // Ensure the pair consists of two characters (a valid plug board mapping)
            if (pair.length == 2 && pair[0].length() == 1 && pair[1].length() == 1) {
                char from = pair[0].charAt(0);
                char to = pair[1].charAt(0);
                plugBoardMap.put(from, to);  // Put the mapping in the HashMap
            } else {
                return null;  // Return null if the format is incorrect
            }
        }
        return plugBoardMap;  // Return the valid plugBoardMap
    }
    public static void main(String[] args) {
        if (args.length == 3) {
            String hash = args[0];
            String plugBoard = args[1];
            String wordList = args[2];
            if(validateHash(hash) && validatePlugBoard(plugBoard) && validateFile(wordList)){
                System.out.println("Começar Cifra do vladir");
            }else{
                System.out.println("Hash: " + validateHash(hash));
                System.out.println("PlugBoard: " + validatePlugBoard(plugBoard));
                System.out.println("File: " + validateFile(wordList));
            }
        } else {
            String hash = "20a64aac202ac6822346e3c73317bad8774e7fdce2f578174da78f6e6c73e434";
            String plugBoard ="{'K': 'G', 'Y': 'O', 'V': 'I', 'H': 'D', 'Z': 'T', 'M': 'P', 'U': 'C', 'J': 'L', 'N': 'A', 'S': 'W'}";
            String wordList = "src/wordlist.txt";
            System.out.println("Hash: " + validateHash(hash));
            System.out.println("PlugBoard: " + validatePlugBoard(plugBoard));
            System.out.println("File: " + validateFile(wordList));
            if(validateHash(hash) && validatePlugBoard(plugBoard) && validateFile(wordList)) {
                System.out.println("g");
                encryptAlg(hash,plugBoard,wordList);
            }
        }
    }
}
