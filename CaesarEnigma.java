import java.util.*;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;

public class CaesarEnigma implements  EncryptionAlgorithm{
    private String plugBoard;
    private int increment;
    private String alphabet;
    private int rotationKey;
    HashMap<Character, Character> plugBoardMap;
    public CaesarEnigma() {
        this.plugBoard = null;
        this.increment = 0;
        this.alphabet = null;
        this.rotationKey = 0;
    }
    public String encrypt(String cleartext) {
        String wordPlugged = plugBoard(cleartext);
        StringBuilder encryptedWord = new StringBuilder();
        int alphabetSize = this.alphabet.length();
        for (int i = 0; i < wordPlugged.length(); i++) {
            char letter = wordPlugged.charAt(i);
            int originalPosition = this.alphabet.indexOf(letter);
            if (originalPosition != -1) {
                int inc = i * this.increment;
                int newPosition = (originalPosition + this.rotationKey + inc) % alphabetSize;
                encryptedWord.append(this.alphabet.charAt(newPosition));
            } else {
                encryptedWord.append(letter);
            }
        }
        return plugBoard(encryptedWord.toString());
    }
    private String plugBoard(String word){
        StringBuilder newWord = new StringBuilder();
        for (char letter : word.toCharArray()) {
            if (this.plugBoardMap.containsKey(letter)) {
                newWord.append(this.plugBoardMap.get(letter));
            } else {
                newWord.append(letter);
            }
        }
        return newWord.toString();
    }
    public String decrypt(String ciphertext) {
        String wordPlugged = plugBoard(ciphertext);
        StringBuilder originalWord = new StringBuilder();
        int alphabetSize = this.alphabet.length();
        for (int i = 0; i < wordPlugged.length(); i++) {
            char letter = wordPlugged.charAt(i);
            int letterPosition = this.alphabet.indexOf(letter);
            if (letterPosition != -1) {
                int inc = i * this.increment;
                int originalPosition = ((letterPosition - this.rotationKey - inc) % alphabetSize + alphabetSize) % alphabetSize;
                originalWord.append(this.alphabet.charAt(originalPosition));
            } else {
                originalWord.append(letter);
            }
        }
        return plugBoard(originalWord.toString());
    }
    public void configure(String configurationFilePath) throws Exception {
        try{
            File xmlFile = new File(configurationFilePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newDefaultInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document xmlDoc = builder.parse(xmlFile);
            xmlDoc.getDocumentElement().normalize();
            String alphabetConfig = xmlDoc.getElementsByTagName("alphabet").item(0).getTextContent().trim();
            String rotation = xmlDoc.getElementsByTagName("encryption-key").item(0).getTextContent().trim();
            String increment = xmlDoc.getElementsByTagName("increment-factor").item(0).getTextContent().trim();
            String plugBoard = xmlDoc.getElementsByTagName("plugboard").item(0).getTextContent().trim();
            this.plugBoard = plugBoard;
            this.rotationKey = Integer.valueOf(rotation);
            this.increment = Integer.valueOf(increment);
            setAlphabet(alphabetConfig);
            setPlugBoardMap(plugBoard);
        }catch (Exception e){
            e.printStackTrace();
        }
        if (this.plugBoard == null || this.increment <= 0  || this.alphabet == null || this.rotationKey <= 0 ||this.plugBoardMap == null ){
            System.out.println("There is an error during the configuration, check configuration file and try again");
            System.exit(1);
        }
    }
    private void setAlphabet(String alphabetConfig) {
        Set<Character> uniqueChars = new LinkedHashSet<>();
        String[] alphabetSetter = alphabetConfig.split("\\+");
        for (String config : alphabetSetter) {
            String toAdd = "";
            switch (config) {
                case "UPPER":
                    toAdd = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                    break;
                case "DIGITS":
                    toAdd = "0123456789";
                    break;
                case "PUNCTUATION":
                    toAdd = "!@#$%^&*()-_=+[]{};:'\",.<>?/|\\";
                    break;
                default:
                    System.out.println("Unknown config: " + config);
                    continue;
            }
            for (char ch : toAdd.toCharArray()) {
                uniqueChars.add(ch);
            }
        }
        StringBuilder alphabetBuilder = new StringBuilder();
        for (char ch : uniqueChars) {
            alphabetBuilder.append(ch);
        }
        this.alphabet = alphabetBuilder.toString();
    }
    private void setPlugBoardMap(String plugBoardInput) {
        HashMap<Character, Character> plugBoardMap = new HashMap<>();
        plugBoardInput = plugBoardInput.replaceAll("[{}']", "").trim(); // Remove braces and quotes
        plugBoardInput = plugBoardInput.replaceAll("\\s+", ""); // Remove spaces
        String[] mappings = plugBoardInput.split(",");
        for (String mapping : mappings) {
            String[] pair = mapping.split(":");
            if (pair.length == 2 && pair[0].length() == 1 && pair[1].length() == 1) {
                char from = pair[0].charAt(0);
                char to = pair[1].charAt(0);
                if (from == to){
                    System.out.println("Warning: Character " + from + " cannot map to itself. Skipping.");
                    continue;
                }
                if (plugBoardMap.containsKey(from)) {
                    System.out.println("Warning: Character " + from + " is already mapped to " + plugBoardMap.get(from) + ". Skipping.");
                    continue;
                }
                if (plugBoardMap.containsValue(to)) {
                    System.out.println("Warning: Character " + to + " is already mapped to another key. Skipping.");
                    continue;
                }
                plugBoardMap.put(from, to);
                plugBoardMap.put(to, from);
            } else {
                System.out.println("Invalid input format: " + mapping);
                return; // Exit with error
            }
        }
        // Print mappings
        for (Map.Entry<Character, Character> plug : plugBoardMap.entrySet()) {
            System.out.println("Key: " + plug.getKey() + " -> Value: " + plug.getValue());
        }
        // Assign validated map
        this.plugBoardMap = plugBoardMap;
    }

}
