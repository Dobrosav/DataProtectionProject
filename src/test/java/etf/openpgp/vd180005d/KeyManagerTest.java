package etf.openpgp.vd180005d;

import etf.openpgp.ma180126d.entites.KeyInfo;
import etf.openpgp.ma180126d.entites.PublicKeyInfo;
import etf.openpgp.ma180126d.entites.SecretKeyInfo;
import etf.openpgp.ma180126d.entites.User;
import etf.openpgp.ma180126d.keymaterial.KeyMaterial;
import etf.openpgp.ma180126d.keymaterial.SubkeyMaterial;
import javafx.util.Pair;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class KeyManagerTest {

    private static final String PUBLIC_KEY_RING = "public_key_ring.gpg";
    private static final String SECRET_KEY_RING = "secret_key_ring.gpg";

    @BeforeEach
    void setUp() throws Exception {
        deleteKeyRingFiles();
        KeyManager.reloadKeyRings();
    }

    @AfterEach
    void tearDown() throws Exception {
        deleteKeyRingFiles();
        KeyManager.reloadKeyRings();
    }

    private void deleteKeyRingFiles() {
        new File(PUBLIC_KEY_RING).delete();
        new File(SECRET_KEY_RING).delete();
    }

    @Test
    void testGenerateKeysDsa1024ElGamal1024() throws Exception {
        User user = new User("Test User", "test@example.com", "testpass");
        
        Pair<PublicKeyInfo, SecretKeyInfo> keyPair = KeyManager.generateKeys(
                user, KeyMaterial.DSA_1024, SubkeyMaterial.EL_GAMAL_1024);

        assertThat(keyPair).isNotNull();
        assertThat(keyPair.getKey()).isNotNull();
        assertThat(keyPair.getValue()).isNotNull();
        assertThat(keyPair.getKey().getKeyIdLong()).isNotNull();
    }

    @Test
    void testGenerateKeysDsa2048ElGamal2048() throws Exception {
        User user = new User("Test User 2048", "test2048@example.com", "testpass");
        
        Pair<PublicKeyInfo, SecretKeyInfo> keyPair = KeyManager.generateKeys(
                user, KeyMaterial.DSA_2048, SubkeyMaterial.EL_GAMAL_2048);

        assertThat(keyPair).isNotNull();
        assertThat(keyPair.getKey().getKeyIdLong()).isNotNull();
    }

    @Test
    void testGenerateKeysNoPassphrase() throws Exception {
        User user = new User("NoPass User", "nopass@example.com", "");
        
        Pair<PublicKeyInfo, SecretKeyInfo> keyPair = KeyManager.generateKeys(
                user, KeyMaterial.DSA_1024, SubkeyMaterial.EL_GAMAL_1024);

        assertThat(keyPair).isNotNull();
    }

    @Test
    void testGetPublicKeyInfoCollection() throws Exception {
        User user = new User("Test User", "test@example.com", "testpass");
        KeyManager.generateKeys(user, KeyMaterial.DSA_1024, SubkeyMaterial.EL_GAMAL_1024);

        Collection<PublicKeyInfo> publicKeys = KeyManager.getPublicKeyInfoCollection();

        assertThat(publicKeys).hasSize(1);
        PublicKeyInfo keyInfo = publicKeys.iterator().next();
        assertThat(keyInfo.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void testGetSecretKeyInfoCollection() throws Exception {
        User user = new User("Test User", "test@example.com", "testpass");
        KeyManager.generateKeys(user, KeyMaterial.DSA_1024, SubkeyMaterial.EL_GAMAL_1024);

        Collection<SecretKeyInfo> secretKeys = KeyManager.getSecretKeyInfoCollection();

        assertThat(secretKeys).hasSize(1);
    }

    @Test
    void testExportAndImportPublicKey() throws Exception {
        User user = new User("Test User", "test@example.com", "testpass");
        Pair<PublicKeyInfo, SecretKeyInfo> originalKeyPair = KeyManager.generateKeys(
                user, KeyMaterial.DSA_1024, SubkeyMaterial.EL_GAMAL_1024);

        File tempFile = new File("test_public_key_" + UUID.randomUUID() + ".gpg");
        KeyManager.exportKey(originalKeyPair.getKey(), tempFile);

        deleteKeyRingFiles();
        KeyManager.reloadKeyRings();

        KeyManager.importKeyRings(tempFile);

        Collection<PublicKeyInfo> publicKeys = KeyManager.getPublicKeyInfoCollection();
        assertThat(publicKeys).hasSize(1);
        assertThat(publicKeys.iterator().next().getKeyIdLong())
                .isEqualTo(originalKeyPair.getKey().getKeyIdLong());

        tempFile.delete();
    }

    @Test
    void testExportAndImportSecretKey() throws Exception {
        User user = new User("Test User", "test@example.com", "testpass");
        Pair<PublicKeyInfo, SecretKeyInfo> originalKeyPair = KeyManager.generateKeys(
                user, KeyMaterial.DSA_1024, SubkeyMaterial.EL_GAMAL_1024);

        File tempFile = new File("test_secret_key_" + UUID.randomUUID() + ".gpg");
        KeyManager.exportKey(originalKeyPair.getValue(), tempFile);

        deleteKeyRingFiles();
        KeyManager.reloadKeyRings();

        KeyManager.importKeyRings(tempFile);

        Collection<SecretKeyInfo> secretKeys = KeyManager.getSecretKeyInfoCollection();
        assertThat(secretKeys).hasSize(1);
        assertThat(secretKeys.iterator().next().getKeyIdLong())
                .isEqualTo(originalKeyPair.getValue().getKeyIdLong());

        tempFile.delete();
    }

    @Test
    void testGetPublicKey() throws Exception {
        User user = new User("Test User", "test@example.com", "testpass");
        Pair<PublicKeyInfo, SecretKeyInfo> keyPair = KeyManager.generateKeys(
                user, KeyMaterial.DSA_1024, SubkeyMaterial.EL_GAMAL_1024);

        PGPPublicKey publicKey = KeyManager.getPublicKey(keyPair.getKey().getKeyIdLong());

        assertThat(publicKey).isNotNull();
    }

    @Test
    void testGetSecretKey() throws Exception {
        User user = new User("Test User", "test@example.com", "testpass");
        Pair<PublicKeyInfo, SecretKeyInfo> keyPair = KeyManager.generateKeys(
                user, KeyMaterial.DSA_1024, SubkeyMaterial.EL_GAMAL_1024);

        PGPSecretKey secretKey = KeyManager.getSecretKey(keyPair.getValue().getKeyIdLong());

        assertThat(secretKey).isNotNull();
    }

    @Test
    void testIsEncryptedWithPassphrase() throws Exception {
        User user = new User("Test User", "test@example.com", "testpass");
        Pair<PublicKeyInfo, SecretKeyInfo> keyPair = KeyManager.generateKeys(
                user, KeyMaterial.DSA_1024, SubkeyMaterial.EL_GAMAL_1024);

        boolean isEncrypted = KeyManager.isEncrypted(keyPair.getValue());

        assertThat(isEncrypted).isTrue();
    }

    @Test
    void testIsEncryptedWithoutPassphrase() throws Exception {
        User user = new User("Test User", "test@example.com", "");
        Pair<PublicKeyInfo, SecretKeyInfo> keyPair = KeyManager.generateKeys(
                user, KeyMaterial.DSA_1024, SubkeyMaterial.EL_GAMAL_1024);

        boolean isEncrypted = KeyManager.isEncrypted(keyPair.getValue());

        assertThat(isEncrypted).isFalse();
    }

    @Test
    void testDeletePublicKey() throws Exception {
        User user = new User("Test User", "test@example.com", "testpass");
        Pair<PublicKeyInfo, SecretKeyInfo> keyPair = KeyManager.generateKeys(
                user, KeyMaterial.DSA_1024, SubkeyMaterial.EL_GAMAL_1024);

        KeyManager.deletePublicKey(keyPair.getKey());

        Collection<PublicKeyInfo> publicKeys = KeyManager.getPublicKeyInfoCollection();
        assertThat(publicKeys).isEmpty();
    }

    @Test
    void testMultipleKeyPairsHaveDifferentIds() throws Exception {
        User user1 = new User("User 1", "user1@example.com", "pass1");
        User user2 = new User("User 2", "user2@example.com", "pass2");

        Pair<PublicKeyInfo, SecretKeyInfo> keyPair1 = KeyManager.generateKeys(
                user1, KeyMaterial.DSA_1024, SubkeyMaterial.EL_GAMAL_1024);
        Pair<PublicKeyInfo, SecretKeyInfo> keyPair2 = KeyManager.generateKeys(
                user2, KeyMaterial.DSA_1024, SubkeyMaterial.EL_GAMAL_1024);

        assertThat(keyPair1.getKey().getKeyIdLong())
                .isNotEqualTo(keyPair2.getKey().getKeyIdLong());
    }
}
