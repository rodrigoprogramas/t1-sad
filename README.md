Application and data security 

The project was divided into two parts.

The first part involved the development in Java of the famous encryption machine, the Enigma.

The machine featured a configurable plugboard and rotors. The rotors followed a specific algorithm called Enhanced Caesar, which, in addition to the rotation factor R, included an increment factor F, where ```inc[i] = i x F``` (with i being the index position of the character). This process generated a SHA-256 hash.

After building the machine's functionality, the professor from the subject provided us with a list of words. These words had salt characters added at the beginning and end, along with a specific plugboard configuration and the corresponding hash. The goal was to perform a brute-force attack on our own machine to discover the original hash.

By inputting the plugboard configuration from the list of words into the terminal, the attack began, ultimately revealing the original hash. This exercise demonstrated the importance of robust encryption and the vulnerabilities that can arise from predictable algorithms or configurations.

The second part focused on creating a secure client-server communication channel by integrating the Enigma of Caesar encryption algorithm from the first sprint into a WebSocket-based setup.

In this phase, students were required to implement three main classes: SocketClient, SocketServer, and CaesarEnigma as the first part but with adptations. The latter served as the encryption algorithm and had to implement the provided EncryptionAlgorithm interface. Both the client and server were designed to receive the path to a valid XML configuration file as an execution argument. This file contained key cryptographic settings such as the rotation value for the encryption (encryption-key), the increment factor to apply (increment-factor), and the plugboard configuration (plugboard).Once launched, the client awaited user input through standard input, while the server printed received messages along with their respective timestamps to the standard output. The communication ended when the client sent the string "BYE." These interactions demonstrated the practical application of encryption in network security, highlighting the integration of cryptographic mechanisms into a secure communication setup. The second part of the project reinforced the importance of properly configured encryption settings for real-world scenarios.

The client is launched with the command:
```java SocketClient CONFIG_FILE.xml ```
The server is launched similarly:
```java SocketServer CONFIG_FILE.xml```

