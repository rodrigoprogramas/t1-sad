import javax.print.DocFlavor;
import java.net.*;
import java.io.*;
import java.util.Scanner;

public class SocketClient {
    private Socket socket;
    private Scanner inputScanner;

    private PrintWriter output;
    private BufferedReader input;

    private EncryptionAlgorithm encryptionAlgorithm;

    // server IP address
    final String SERVER_IP = "127.0.0.1";
    final String CLOSE_COMMAND = "BYE";

    // location in the command line arguments' array where the path is provided
    static int COMMAND_LINE_ARGUMENT_FILE_PATH = 0;

    /**
     * connect the client to the server at `SERVER_IP` on the given port
     * @param port - port to connect the client to
     */
    public void startConnection(int port) {
        try {
            this.socket = new Socket(SERVER_IP, port);
            this.output = new PrintWriter(socket.getOutputStream(), true); // Auto flush ativado
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Connected to the server at " + SERVER_IP + ":" + port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle the client's communications
     */
    public void handleClientCommunications(String clientInput) {
        try {
            String encryptedMessage = encrypt(clientInput);
            output.println(encryptedMessage);
            String serverResponse = input.readLine();
            if (serverResponse != null) {
                String decryptedMessage = decrypt(serverResponse);
                System.out.println("Server response: " + decryptedMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * notify the server to close the session
     */
    private void sendCloseCommand() {
        try {
            output.println(CLOSE_COMMAND);
            stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Close ALL client I/O
     */
    public void stop() throws IOException {
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null) socket.close();
            System.out.println("Connection closed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * read the file on the given `filePath`, create, configure and return a new instance of the EncryptionAlgorithm
     * @param filePath - path where the configuration file is located
     */
    private void configureEncryptionAlgorithm(String filePath) {
        this.encryptionAlgorithm = new CaesarEnigma();
        try{
            this.encryptionAlgorithm.configure(filePath);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Decrypts the given message using the instance's encryption algorithm
     * @param message - message to be decrypted
     * @return the decrypted (original) message
     */
    private String decrypt(String message) { return this.encryptionAlgorithm.decrypt(message); }

    /**
     * Encrypts the given message using the instance's encryption algorithm
     * @param message - Message to be encrypted
     * @return the encrypted message
     */
    private String encrypt(String message) { return this.encryptionAlgorithm.encrypt(message); }

    /**
    * Mostly useful for debug purposes
    */
    public String toString() {
        return "Client Configuration{" +
                "socket Address=" + socket.getInetAddress() +
                ", clientSocket=" + socket.getPort() +
                '}' +
                "EncryptionAlgorithm: " + encryptionAlgorithm;
    }

    /**
    * __Reminders:__
    * - validate inputs
    * - connect to the server & configure the encryption algorithm
    * - handle errors
    *
    * @param args - command line arguments. args[0] SHOULD contain the absolute path for the configuration file
    */
    public static void main(String[] args) {
        if (args.length == 1){
            SocketClient socketClient = new SocketClient();
            socketClient.configureEncryptionAlgorithm(args[0]);
            socketClient.startConnection(433);
            Scanner scanner = new Scanner(System.in);
            System.out.println("SocketClient started. Start typing your messages...");
            while (true) {
                String userInput = scanner.nextLine();
                if (userInput.equalsIgnoreCase(socketClient.CLOSE_COMMAND)) {
                    socketClient.sendCloseCommand();
                    break;
                }
                socketClient.handleClientCommunications(userInput);
            }

            scanner.close();
        }else{
            System.out.println("Invalid arguments.");
        }
    }
}
