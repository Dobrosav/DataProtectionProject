# Data Protection Project

A Java-based OpenPGP implementation for secure data encryption, decryption, and digital signatures using the Bouncy Castle library.

## Features

- **Key Management**: Generate, import, and manage PGP key pairs
- **Encryption**: Encrypt files and messages using public key cryptography
- **Decryption**: Decrypt files using private keys with passphrase protection
- **Digital Signatures**: Sign and verify data integrity
- **Graphical User Interface**: JavaFX-based GUI for easy interaction

## Requirements

- Java 21 or higher
- Maven 3.6+

## Building

This project uses Maven. To build and run:

```bash
# Compile
mvn compile

# Run
mvn javafx:run

# Package as JAR
mvn package
```

## Project Structure

```
src/main/java/etf/openpgp/
├── ma180126d/          # Sender/Receiver implementation
│   ├── Sender.java
│   ├── Receiver.java
│   ├── ReceiverStatus.java
│   ├── exceptions/     # Custom exceptions
│   └── keymaterial/   # Key material classes
├── vd180005d/         # GUI implementation
│   ├── KeyManager.java
│   ├── Test.java
│   ├── gui/           # JavaFX GUI components
│   └── gui/controllers/
src/main/resources/     # GUI resources (FXML, CSS, images)
lib/                   # (deprecated - now managed by Maven)
documentation/         # Project documentation
```

## Usage

1. Launch the application
2. Generate a key pair or import existing keys
3. Use the encryption page to encrypt files
4. Use the decryption page to decrypt files
5. Manage your keys in the key management section

## License

Academic project -, University of Belgrade Faculty of Electrical Engineering
