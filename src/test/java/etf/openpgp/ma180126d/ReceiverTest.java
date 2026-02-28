package etf.openpgp.ma180126d;

import etf.openpgp.ma180126d.entites.PublicKeyInfo;
import etf.openpgp.ma180126d.entites.SecretKeyInfo;
import etf.openpgp.ma180126d.entites.User;
import etf.openpgp.ma180126d.exceptions.InvalidPassphraseException;
import etf.openpgp.ma180126d.exceptions.KeyNotFoundException;
import etf.openpgp.ma180126d.exceptions.PassphraseRequiredException;
import etf.openpgp.ma180126d.keymaterial.KeyMaterial;
import etf.openpgp.ma180126d.keymaterial.SubkeyMaterial;
import etf.openpgp.vd180005d.KeyManager;
import javafx.util.Pair;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReceiverTest {

    @TempDir
    java.nio.file.Path tempDir;

    private Pair<PublicKeyInfo, SecretKeyInfo> keyPair;
    private File outputDir;

    @BeforeEach
    void setUp() throws Exception {
        deleteKeyRingFiles();
        KeyManager.reloadKeyRings();
        
        User user = new User("Test User", "test@example.com", "testpass");
        keyPair = KeyManager.generateKeys(user, KeyMaterial.DSA_1024, SubkeyMaterial.EL_GAMAL_1024);
        outputDir = tempDir.toFile();
    }

    @AfterEach
    void tearDown() throws Exception {
        deleteKeyRingFiles();
        KeyManager.reloadKeyRings();
    }

    private void deleteKeyRingFiles() {
        new File("public_key_ring.gpg").delete();
        new File("secret_key_ring.gpg").delete();
    }

    private File createEncryptedFile(String content) throws Exception {
        File inputFile = new File(tempDir.toFile(), "test.txt");
        java.nio.file.Files.writeString(inputFile.toPath(), content);

        Sender sender = new Sender(
                inputFile, outputDir,
                false, false,
                true, SymmetricKeyAlgorithmTags.AES_128, keyPair.getKey(),
                false, null, null
        );
        sender.send();

        return new File(outputDir, "test.txt.gpg");
    }

    private File createEncryptedSignedFile(String content) throws Exception {
        File inputFile = new File(tempDir.toFile(), "test.txt");
        java.nio.file.Files.writeString(inputFile.toPath(), content);

        Sender sender = new Sender(
                inputFile, outputDir,
                false, false,
                true, SymmetricKeyAlgorithmTags.AES_128, keyPair.getKey(),
                true, "testpass", keyPair.getValue()
        );
        sender.send();

        return new File(outputDir, "test.txt.gpg");
    }

    @Test
    void testDecryptWithCorrectPassphrase() throws Exception {
        String originalContent = "Hello World";
        File encryptedFile = createEncryptedFile(originalContent);

        Receiver receiver = new Receiver(encryptedFile);
        receiver.setPassphrase("testpass");
        receiver.receive();

        ReceiverStatus status = receiver.getReceiverStatus();
        assertThat(status.isDecryptionSucceeded()).isTrue();
        assertThat(status.getMessage()).isEqualTo(originalContent);
    }

    @Test
    void testDecryptWithIncorrectPassphrase() throws Exception {
        String originalContent = "Hello World";
        File encryptedFile = createEncryptedFile(originalContent);

        Receiver receiver = new Receiver(encryptedFile);
        receiver.setPassphrase("wrongpass");

        assertThatThrownBy(() -> receiver.receive())
                .isInstanceOf(InvalidPassphraseException.class);
    }

    @Test
    void testDecryptWithEmptyPassphrase() throws Exception {
        User userNoPass = new User("NoPass User", "nopass@example.com", "");
        Pair<PublicKeyInfo, SecretKeyInfo> keyPairNoPass = KeyManager.generateKeys(
                userNoPass, KeyMaterial.DSA_1024, SubkeyMaterial.EL_GAMAL_1024);

        File inputFile = new File(tempDir.toFile(), "test.txt");
        java.nio.file.Files.writeString(inputFile.toPath(), "Hello World");

        Sender sender = new Sender(
                inputFile, outputDir,
                false, false,
                true, SymmetricKeyAlgorithmTags.AES_128, keyPairNoPass.getKey(),
                false, null, null
        );
        sender.send();

        File encryptedFile = new File(outputDir, "test.txt.gpg");

        Receiver receiver = new Receiver(encryptedFile);
        receiver.setPassphrase("");
        receiver.receive();

        ReceiverStatus status = receiver.getReceiverStatus();
        assertThat(status.isDecryptionSucceeded()).isTrue();
    }

    @Test
    void testDecryptCompressedData() throws Exception {
        String originalContent = "Hello World";
        
        File inputFile = new File(tempDir.toFile(), "test.txt");
        java.nio.file.Files.writeString(inputFile.toPath(), originalContent);

        Sender sender = new Sender(
                inputFile, outputDir,
                true, false,
                true, SymmetricKeyAlgorithmTags.AES_128, keyPair.getKey(),
                false, null, null
        );
        sender.send();

        File encryptedFile = new File(outputDir, "test.txt.gpg");

        Receiver receiver = new Receiver(encryptedFile);
        receiver.setPassphrase("testpass");
        receiver.receive();

        ReceiverStatus status = receiver.getReceiverStatus();
        assertThat(status.isDecryptionSucceeded()).isTrue();
        assertThat(status.getMessage()).isEqualTo(originalContent);
    }

    @Test
    void testDecryptRadix64Data() throws Exception {
        String originalContent = "Hello World";
        
        File inputFile = new File(tempDir.toFile(), "test.txt");
        java.nio.file.Files.writeString(inputFile.toPath(), originalContent);

        Sender sender = new Sender(
                inputFile, outputDir,
                false, true,
                true, SymmetricKeyAlgorithmTags.AES_128, keyPair.getKey(),
                false, null, null
        );
        sender.send();

        File encryptedFile = new File(outputDir, "test.txt.gpg");

        Receiver receiver = new Receiver(encryptedFile);
        receiver.setPassphrase("testpass");
        receiver.receive();

        ReceiverStatus status = receiver.getReceiverStatus();
        assertThat(status.isDecryptionSucceeded()).isTrue();
        assertThat(status.getMessage()).isEqualTo(originalContent);
    }

    @Test
    void testVerifySignature() throws Exception {
        String originalContent = "Signed Message";
        File encryptedSignedFile = createEncryptedSignedFile(originalContent);

        Receiver receiver = new Receiver(encryptedSignedFile);
        receiver.setPassphrase("testpass");
        receiver.receive();

        ReceiverStatus status = receiver.getReceiverStatus();
        assertThat(status.isDecryptionSucceeded()).isTrue();
        assertThat(status.isVerificationApplied()).isTrue();
        assertThat(status.isVerificationSucceeded()).isTrue();
        assertThat(status.getMessage()).isEqualTo(originalContent);
        assertThat(status.getSignerKeyInfo()).isNotNull();
    }

    @Test
    void testDecryptWithoutPassphraseThrowsException() throws Exception {
        String originalContent = "Hello World";
        File encryptedFile = createEncryptedFile(originalContent);

        Receiver receiver = new Receiver(encryptedFile);
        receiver.setPassphrase(null);

        assertThatThrownBy(() -> receiver.receive())
                .isInstanceOf(PassphraseRequiredException.class);
    }

    @Test
    void testDecryptWithWrongKeyThrowsException() throws Exception {
        User user2 = new User("User 2", "user2@example.com", "pass2");
        KeyManager.generateKeys(user2, KeyMaterial.DSA_1024, SubkeyMaterial.EL_GAMAL_1024);

        String originalContent = "Hello World";
        File encryptedFile = createEncryptedFile(originalContent);

        Receiver receiver = new Receiver(encryptedFile);
        receiver.setPassphrase("pass2");

        assertThatThrownBy(() -> receiver.receive())
                .isInstanceOf(InvalidPassphraseException.class);
    }

    @Test
    void testDecryptLargeContent() throws Exception {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            sb.append("Lorem ipsum dolor sit amet. ");
        }
        String originalContent = sb.toString();
        
        File encryptedFile = createEncryptedFile(originalContent);

        Receiver receiver = new Receiver(encryptedFile);
        receiver.setPassphrase("testpass");
        receiver.receive();

        ReceiverStatus status = receiver.getReceiverStatus();
        assertThat(status.isDecryptionSucceeded()).isTrue();
        assertThat(status.getMessage()).isEqualTo(originalContent);
    }

    @Test
    void testDecryptEmptyContent() throws Exception {
        String originalContent = "";
        File encryptedFile = createEncryptedFile(originalContent);

        Receiver receiver = new Receiver(encryptedFile);
        receiver.setPassphrase("testpass");
        receiver.receive();

        ReceiverStatus status = receiver.getReceiverStatus();
        assertThat(status.isDecryptionSucceeded()).isTrue();
    }
}
