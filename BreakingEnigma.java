import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class BreakingEnigma {
    public static boolean breakingEnigmaAlg(String desiredHash, String recievedPlugBoard, String wordList){
        try {
            File file = new File(wordList);
            Scanner scanner = new Scanner(file);
            String word = scanner.next();
            List <String> saltedWords = saltGen(word);
            List <String> hashes = new ArrayList<>();
            for(int i = 0 ; i < saltedWords.size(); i++){
                hashes= genHashesToCompare(recievedPlugBoard, saltedWords.get(i));
                for(String hashToCompare: hashes){
                    if(hashToCompare.equals(desiredHash)){
                        System.out.println(hashToCompare);
                    }
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + wordList);
        }
        return false;
    }
    public static List<String> genHashesToCompare(String plugBoard, String saltedWord){
        String word1plug = plugBoard(plugBoard,saltedWord);
        List <String> allWordRotations = new ArrayList<>();
        List <String> allWordsRotationsAfter2plug = new ArrayList<>();
        List<String> generatedHashes = new ArrayList<>();
        for (int r = 0 ; r < 25 ; r++){
            for(int i = 0 ; i < 5; i++){
                allWordRotations.add(enhancedCaesar(word1plug,r,i));
            }
        }
        for(String rotation: allWordRotations){
            for (int r = 0 ; r < 25 ; r++){
                for(int i = 0 ; i < 5; i++){
                    allWordsRotationsAfter2plug.add(enhancedCaesar(rotation,r,i));
                }
            }
        }
        for(String finalWord : allWordsRotationsAfter2plug){
            generatedHashes.add(genSHA256(finalWord));
        }
        return generatedHashes;
    }
    public static String genSHA256(String word){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(word.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    public static String plugBoard(String recievedPlugBoard, String word){
        HashMap<Character, Character> plugBoardMap = parsePlugBoard(recievedPlugBoard);
        StringBuilder newWord = new StringBuilder();
        for (char letter : word.toCharArray()) {
            if (plugBoardMap.containsKey(letter)) {
                newWord.append(plugBoardMap.get(letter));
            } else {
                newWord.append(letter);
            }
        }
        return newWord.toString();
    }
    public static String enhancedCaesar(String word, int rotation, int increment) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder newWord = new StringBuilder();
        int alphabetSize = alphabet.length();
        for (int i = 0; i < word.length(); i++) {
            char letter = word.charAt(i);
            if (alphabet.contains(Character.toString(letter))) {
                // Find the position in the alphabet AKA Unicode of the letter
                int originalPosition = letter - 'A';
                int newPosition = (originalPosition + rotation + increment) % alphabetSize;
                newWord.append(alphabet.charAt(newPosition));
            } else {
                newWord.append(letter);
            }
        }
        return newWord.toString();
    }
    public static List<String> saltGen(String word){
        List<String> saltedCombinations = new ArrayList<>();
        String alphabetSalt = "!#$%&*+-:;<=>?@";
        for (int i = 0; i < alphabetSalt.length(); i++) {
            for (int j = 0; j < alphabetSalt.length(); j++) {
                //"" to convert char to string
                saltedCombinations.add(alphabetSalt.charAt(i) + "" + alphabetSalt.charAt(j) + word);
                saltedCombinations.add(word + alphabetSalt.charAt(i) + "" + alphabetSalt.charAt(j));
            }
        }
        return saltedCombinations;
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
            if (pair.length == 2 && pair[0].length() == 1 && pair[1].length() == 1) {
                char from = pair[0].charAt(0);
                char to = pair[1].charAt(0);
                plugBoardMap.put(from, to);
            } else {
                return null;
            }
        }
        return plugBoardMap;
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
            String hashRodas = "20a64aac202ac6822346e3c73317bad8774e7fdce2f578174da78f6e6c73e434";
            String hash = "97f30f1470a9cb882bd1dd84f741f31d842ff332f935bce9425875bcd1e278bf";
            String plugBoardRodas ="{'K': 'G', 'Y': 'O', 'V': 'I', 'H': 'D', 'Z': 'T', 'M': 'P', 'U': 'C', 'J': 'L', 'N': 'A', 'S': 'W'}";
            String plugBoard = "{'A': 'D', 'E': 'K', 'Y': 'U', 'H': 'E', 'C': 'G', 'L': 'S', 'T': 'Y', 'Q': 'C', 'S': 'I', 'F': 'L', 'V': 'F', 'M': 'R', 'K': 'B', 'I': 'V', 'N': 'M', 'Z': 'A', 'W': 'J', 'D': 'W', 'B': 'X', 'J': 'Z'}";
            String wordList = "src/wordlist.txt";
            System.out.println("Hash: " + validateHash(hash));
            System.out.println("PlugBoard: " + validatePlugBoard(plugBoard));
            System.out.println("File: " + validateFile(wordList));
            if(validateHash(hash) && validatePlugBoard(plugBoard) && validateFile(wordList)) {
                breakingEnigmaAlg(hash,plugBoard,wordList);
            }
        }
    }
}