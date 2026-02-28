package etf.openpgp.ma180126d;

import etf.openpgp.ma180126d.entites.PublicKeyInfo;
import etf.openpgp.ma180126d.entites.SecretKeyInfo;
import etf.openpgp.ma180126d.entites.User;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SenderTest {

    @TempDir
    Path tempDir;

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

    @Test
    void testEncryptWithAes128() throws Exception {
        File inputFile = new File(tempDir.toFile(), "test.txt");
        java.nio.file.Files.writeString(inputFile.toPath(), "Hello World");

        Sender sender = new Sender(
                inputFile, outputDir,
                false, false,
                true, SymmetricKeyAlgorithmTags.AES_128, keyPair.getKey(),
                false, null, null
        );
        sender.send();

        File outputFile = new File(outputDir, "test.txt.gpg");
        assertThat(outputFile).exists();
        assertThat(outputFile.length()).isGreaterThan(0);
    }

    @Test
    void testEncryptWithTripleDes() throws Exception {
        File inputFile = new File(tempDir.toFile(), "test.txt");
        java.nio.file.Files.writeString(inputFile.toPath(), "Hello World");

        Sender sender = new Sender(
                inputFile, outputDir,
                false, false,
                true, SymmetricKeyAlgorithmTags.TRIPLE_DES, keyPair.getKey(),
                false, null, null
        );
        sender.send();

        File outputFile = new File(outputDir, "test.txt.gpg");
        assertThat(outputFile).exists();
        assertThat(outputFile.length()).isGreaterThan(0);
    }

    @Test
    void testEncryptWithCompression() throws Exception {
        File inputFile = new File(tempDir.toFile(), "test.txt");
        java.nio.file.Files.writeString(inputFile.toPath(), "Hello World");

        Sender sender = new Sender(
                inputFile, outputDir,
                true, false,
                true, SymmetricKeyAlgorithmTags.AES_128, keyPair.getKey(),
                false, null, null
        );
        sender.send();

        File outputFile = new File(outputDir, "test.txt.gpg");
        assertThat(outputFile).exists();
    }

    @Test
    void testEncryptWithRadix64() throws Exception {
        File inputFile = new File(tempDir.toFile(), "test.txt");
        java.nio.file.Files.writeString(inputFile.toPath(), "Hello World");

        Sender sender = new Sender(
                inputFile, outputDir,
                false, true,
                true, SymmetricKeyAlgorithmTags.AES_128, keyPair.getKey(),
                false, null, null
        );
        sender.send();

        File outputFile = new File(outputDir, "test.txt.gpg");
        assertThat(outputFile).exists();
        
        String content = java.nio.file.Files.readString(outputFile.toPath());
        assertThat(content).startsWith("-----BEGIN PGP MESSAGE-----");
    }

    @Test
    void testSignWithoutEncryption() throws Exception {
        File inputFile = new File(tempDir.toFile(), "test.txt");
        java.nio.file.Files.writeString(inputFile.toPath(), "Hello World");

        Sender sender = new Sender(
                inputFile, outputDir,
                false, false,
                false, 0, null,
                true, "testpass", keyPair.getValue()
        );
        sender.send();

        File outputFile = new File(outputDir, "test.txt.sig");
        assertThat(outputFile).exists();
    }

    @Test
    void testEncryptAndSign() throws Exception {
        File inputFile = new File(tempDir.toFile(), "test.txt");
        java.nio.file.Files.writeString(inputFile.toPath(), "Hello World");

        Sender sender = new Sender(
                inputFile, outputDir,
                false, false,
                true, SymmetricKeyAlgorithmTags.AES_128, keyPair.getKey(),
                true, "testpass", keyPair.getValue()
        );
        sender.send();

        File outputFile = new File(outputDir, "test.txt.gpg");
        assertThat(outputFile).exists();
    }

    @Test
    void testEncryptWithCompressionAndRadix64AndSign() throws Exception {
        File inputFile = new File(tempDir.toFile(), "test.txt");
        java.nio.file.Files.writeString(inputFile.toPath(), "Hello World");

        Sender sender = new Sender(
                inputFile, outputDir,
                true, true,
                true, SymmetricKeyAlgorithmTags.AES_128, keyPair.getKey(),
                true, "testpass", keyPair.getValue()
        );
        sender.send();

        File outputFile = new File(outputDir, "test.txt.gpg");
        assertThat(outputFile).exists();
    }

    @Test
    void testEncryptWithNullPublicKeyThrowsException() throws Exception {
        File inputFile = new File(tempDir.toFile(), "test.txt");
        java.nio.file.Files.writeString(inputFile.toPath(), "Hello World");

        Sender sender = new Sender(
                inputFile, outputDir,
                false, false,
                true, SymmetricKeyAlgorithmTags.AES_128, null,
                false, null, null
        );

        assertThatThrownBy(() -> sender.send())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Public key not provided");
    }

    @Test
    void testSignWithNullSecretKeyThrowsException() throws Exception {
        File inputFile = new File(tempDir.toFile(), "test.txt");
        java.nio.file.Files.writeString(inputFile.toPath(), "Hello World");

        Sender sender = new Sender(
                inputFile, outputDir,
                false, false,
                false, 0, null,
                true, "testpass", null
        );

        assertThatThrownBy(() -> sender.send())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Secret key not provided");
    }

    @Test
    void testSignWithInvalidPassphraseThrowsException() throws Exception {
        File inputFile = new File(tempDir.toFile(), "test.txt");
        java.nio.file.Files.writeString(inputFile.toPath(), "Hello World");

        Sender sender = new Sender(
                inputFile, outputDir,
                false, false,
                false, 0, null,
                true, "wrongpass", keyPair.getValue()
        );

        assertThatThrownBy(() -> sender.send())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid passphrase");
    }

    @Test
    void testEncryptEmptyFile() throws Exception {
        File inputFile = new File(tempDir.toFile(), "empty.txt");
        java.nio.file.Files.writeString(inputFile.toPath(), "");

        Sender sender = new Sender(
                inputFile, outputDir,
                false, false,
                true, SymmetricKeyAlgorithmTags.AES_128, keyPair.getKey(),
                false, null, null
        );
        sender.send();

        File outputFile = new File(outputDir, "empty.txt.gpg");
        assertThat(outputFile).exists();
    }

    @Test
    void testEncryptLargeContent() throws Exception {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            sb.append("Lorem ipsum dolor sit amet. ");
        }
        File inputFile = new File(tempDir.toFile(), "large.txt");
        java.nio.file.Files.writeString(inputFile.toPath(), sb.toString());

        Sender sender = new Sender(
                inputFile, outputDir,
                false, false,
                true, SymmetricKeyAlgorithmTags.AES_128, keyPair.getKey(),
                false, null, null
        );
        sender.send();

        File outputFile = new File(outputDir, "large.txt.gpg");
        assertThat(outputFile).exists();
        assertThat(outputFile.length()).isGreaterThan(0);
    }
}
