import java.net.*;
import java.io.*;
import java.time.LocalDateTime;

public class SocketServer {

    private ServerSocket serverSocket;
    private Socket clientSocket;

    private PrintWriter output;
    private BufferedReader input;

    private EncryptionAlgorithm encryptionAlgorithm;

    /**
     * start the server and wait for connections on the given port

     * @param port - port to connect the client to
     */
    public void start(int port) {
        System.out.println("Starting server...");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started. Waiting for a connection...");
            try (Socket clientSocket = serverSocket.accept();
                PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                System.out.println("Client connected!");
                String clientMessage;
                while ((clientMessage = input.readLine()) != null) {
                    if ("BYE".equalsIgnoreCase(clientMessage)) {
                        System.out.println("Client requested to close the connection.");
                        break;
                    } else {
                        processServerCommunications(clientMessage, output);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error in server operation: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("Server stopped.");
            stop();
        }
    }

    /**
     * Handle messages received on the server and log them with the respective timestamp
     */
    public void processServerCommunications(String message, PrintWriter output) {
        try {
            String decryptedMessage = decrypt(message);
            LocalDateTime timestamp = LocalDateTime.now();
            System.out.println("Message received: (" + message + ")");
            System.out.println("Decrypted message: " + decryptedMessage);
            System.out.println("Received on: " + timestamp);
        } catch (Exception e) {
            System.err.println("Error processing client message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Terminate the service - close ALL I/O
     */
    public void stop() {
        try {
            if (clientSocket != null) clientSocket.close();
            if (serverSocket != null) serverSocket.close();
            System.out.println("Server stopped.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read the file at `filePath`, xreate, configure and return a new instance of the EncryptionAlgorithm

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
    @Override
    public String toString() {
        return "Server Configuration{" +
                "server Address=" + serverSocket.getInetAddress() +
                ", clientSocket=" + serverSocket.getLocalPort() +
                '}' +
                "EncryptionAlgorithm: " + encryptionAlgorithm;
    }

    // location in the command line arguments' array where the path is provided
    static int COMMAND_LINE_ARGUMENT_FILE_PATH = 0;

    /**
    * __Reminders:__
    * - validate inputs
    * - Start the server service & configure the encryption algorithm
    * - handle errors
    * - terminate the serviceon demand
    *
    * @param args - command line arguments. args[0] SHOULD contain the absolute path for the configuration file
    */
    public static void main(String[] args) {
        SocketServer socketServer = new SocketServer();
        if (args.length == 1){
            socketServer.configureEncryptionAlgorithm(args[0]);
        }else{
            System.out.println("Invalid arguments.");
            System.exit(1);
        }
        socketServer.start(433);
        // start the server

        // keep processing messages
      
        // stop the server
        
    }
}