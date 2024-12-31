import java.util.HashMap;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

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
        StringBuilder encryptedWord = new StringBuilder();
        int alphabetSize = this.alphabet.length();
        for (int i = 0; i < cleartext.length(); i++) {
            char letter = cleartext.charAt(i);
            int originalPosition = this.alphabet.indexOf(letter);
            if (originalPosition != -1) {
                int inc = i * this.increment;
                int newPosition = (originalPosition + this.rotationKey + inc) % alphabetSize;
                encryptedWord.append(this.alphabet.charAt(newPosition));
            } else {
                encryptedWord.append(letter);
            }
        }
        return encryptedWord.toString();
    }
    public String decrypt(String ciphertext) {
        StringBuilder originalWord = new StringBuilder();
        int alphabetSize = this.alphabet.length();
        for (int i = 0; i < ciphertext.length(); i++) {
            char letter = ciphertext.charAt(i);
            int letterPosition = this.alphabet.indexOf(letter);
            if (letterPosition != -1) {
                int inc = i * this.increment;
                int originalPosition = ((letterPosition - this.rotationKey - inc) % alphabetSize + alphabetSize) % alphabetSize; // Ex: (5 - 3 - 4) % 26 = -2 After normalization: ((-2 + 26) % 26) = 24
                originalWord.append(this.alphabet.charAt(originalPosition));
            } else {
                originalWord.append(letter);
            }
        }
        return originalWord.toString();
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
    private void setAlphabet(String alphabetConfig){
        Set<Character> uniqueChars = new HashSet<>();
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
        plugBoardInput = plugBoardInput.replaceAll("[{}']", "").trim(); //remove {}
        plugBoardInput = plugBoardInput.replaceAll("\\s+", "");// remove spaces
        String[] mappings = plugBoardInput.split(",");
        for (String mapping : mappings) {
            // split the "key:value" pair
            String[] pair = mapping.split(":");
            if (pair.length == 2 && pair[0].length() == 1 && pair[1].length() == 1) {
                char from = pair[0].charAt(0);
                char to = pair[1].charAt(0);
                if (plugBoardMap.containsKey(from)) {
                    System.out.println("Char" + from + " is already mapped");
                    this.plugBoardMap = null;
                }
                if (plugBoardMap.containsValue(to)) {
                    System.out.println("Char" + to + " was already found in another mapping");
                    this.plugBoardMap = null;
                }
                plugBoardMap.put(from, to);
            } else {
                this.plugBoardMap = null;
            }
        }
        this.plugBoardMap = plugBoardMap;
    }
}
