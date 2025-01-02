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
    public void start(int port) throws IOException {
        System.out.println("Starting server...");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started. Waiting for connections...");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected!");
            this.clientSocket = clientSocket;
        }
    }
    /**
     * Handle messages received on the server and log them with the respective timestamp
     */
    public void processServerCommunications(String message) {
        String decryptedMessage = decrypt(message);
        LocalDateTime timestamp = LocalDateTime.now();
        System.out.println("Message received: (" + message + ")");
        System.out.println("Decrypted message: " + decryptedMessage);
        System.out.println("Received on: " + timestamp);
        output.println(encrypt("MESSAGE RECEIVED ON " + timestamp));
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
    public Socket getClientSocket() {
        return clientSocket;
    }
    public PrintWriter getOutput() {
        return output;
    }
    public void setOutput(PrintWriter output) {
        this.output = output;
    }
    public BufferedReader getInput() {
        return input;
    }
    public void setInput(BufferedReader input) {
        this.input = input;
    }

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
        if (args.length != 1) {
            System.out.println("Usage: java SocketServer <encryption-config-file-path>");
            System.exit(1);
        }
        SocketServer socketServer = new SocketServer();
        String configFilePath = args[COMMAND_LINE_ARGUMENT_FILE_PATH];
        socketServer.configureEncryptionAlgorithm(configFilePath);
        try {
            socketServer.start(12345);
            Socket clientSocket = socketServer.getClientSocket();
            socketServer.setInput(new BufferedReader(new InputStreamReader(clientSocket.getInputStream())));
            socketServer.setOutput(new PrintWriter(clientSocket.getOutputStream(), true));
            try{
                String clientMessage;
                while ((clientMessage = socketServer.getInput().readLine()) != null) {
                    if ("BYE".equalsIgnoreCase(clientMessage)) {
                        System.out.println("Client requested to close the connection.");
                        break;
                    }
                    socketServer.getOutput().flush();
                    socketServer.processServerCommunications(clientMessage);
                }
            } catch (IOException e) {
                System.err.println("Error processing client communication: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}