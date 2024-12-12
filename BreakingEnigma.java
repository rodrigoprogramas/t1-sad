import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class BreakingEnigma {
    public static boolean breakingEnigmaAlg(String desiredHash, String recievedPlugBoard, String wordList) {
        boolean found = false;
        List<String> words = new ArrayList<>();
        try {
            File file = new File(wordList);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                words.add(scanner.next());
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + wordList);
            return false;
        }
        for (String word : words) {
            List<String> saltedWords = saltGen(word);
            HashMap<String, String> hashAndFinalWord = new HashMap<>();
            for (String saltedWord : saltedWords) {
                hashAndFinalWord = genHashesToCompare(recievedPlugBoard, saltedWord);
                for (Map.Entry<String, String> entry : hashAndFinalWord.entrySet()) {
                    if (entry.getKey().equals(desiredHash)){
                        System.out.println(entry.getValue());
                        found = true;
                        break;
                    }
                }
                if (found) break;
            }
            if (found) break;
        }
        return found;
    }
    public static HashMap<String,String>  genHashesToCompare(String plugBoard, String saltedWord){
        String word1plug = plugBoard(plugBoard,saltedWord);
        HashMap<String,String> hashAndFinalWord = new HashMap<>();
        List <String> allWordRotations = new ArrayList<>();
        List <String> allWordsRotationsAfter2plug = new ArrayList<>();
        for (int r = 0 ; r < 25 ; r++){
            for(int i = 0 ; i < 25; i++){
                allWordRotations.add(enhancedCaesar(word1plug,r,i));
            }
        }
        for(String rotation: allWordRotations){
            allWordsRotationsAfter2plug.add(plugBoard(plugBoard,rotation));
        }
        for(String finalWord : allWordsRotationsAfter2plug){
            hashAndFinalWord.put(genSHA256(finalWord),finalWord);
        }
        return hashAndFinalWord;
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
                int originalPosition = letter - 'A';
                int inc = i * increment;
                int newPosition = (originalPosition + rotation + inc) % alphabetSize;
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
        plugBoardInput = plugBoardInput.replaceAll("[{}']", "").trim(); //remove {}
        plugBoardInput = plugBoardInput.replaceAll("\\s+", "");// remove spaces 
        String[] mappings = plugBoardInput.split(",");
        for (String mapping : mappings) {
            // split the "key:value" pair
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
                breakingEnigmaAlg(hash,plugBoard,wordList);
            }else{
                System.out.println("Hash: " + validateHash(hash));
                System.out.println("PlugBoard: " + validatePlugBoard(plugBoard));
                System.out.println("File: " + validateFile(wordList));
            }
        } else {
            System.out.println("Invalid arguments.");
        }
    }
}