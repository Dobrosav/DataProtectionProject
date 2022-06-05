package etf.openpgp.vd180005d;

import etf.openpgp.ma180126d.entites.KeyInfo;
import etf.openpgp.ma180126d.entites.PublicKeyInfo;
import etf.openpgp.ma180126d.entites.SecretKeyInfo;
import etf.openpgp.ma180126d.entites.User;
import etf.openpgp.ma180126d.keymaterial.KeyMaterial;
import etf.openpgp.ma180126d.keymaterial.SubkeyMaterial;
import javafx.util.Pair;

import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.bcpg.sig.KeyFlags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.bc.BcPGPObjectFactory;

import org.bouncycastle.openpgp.operator.PBESecretKeyEncryptor;
import org.bouncycastle.openpgp.operator.PGPDigestCalculator;
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.*;

import java.io.*;
import java.security.*;
import java.util.*;

public class KeyManager {
	public static String message;
	private static final String PUBLIC_KEY_RING_FILE_PATH = "public_key_ring.gpg",
			SECRET_KEY_RING_FILE_PATH = "secret_key_ring.gpg";
	private static PGPPublicKeyRingCollection PUBLIC_KEY_RINGS;
	private static PGPSecretKeyRingCollection SECRET_KEY_RINGS;
	private static final PGPSignatureSubpacketVector KEY_SIGNATURE_SUBPACKET_VECTOR = generateKeySubpacketVector();
	private static final PGPSignatureSubpacketVector SUBKEY_SIGNATURE_SUBPACKET_VECTOR = generateSubkeySubpacketVector();
	private static BcKeyFingerprintCalculator KEY_FINGERPRINT_CALCULATOR = new BcKeyFingerprintCalculator();

	static {
		Security.addProvider(new BouncyCastleProvider());
		loadKeyRings();
	}

	/*
	 * Load Key Rings from the Files
	 */
	private static void loadKeyRings() {
		try {
			File publicKeyRingFile = new File(PUBLIC_KEY_RING_FILE_PATH);
			if (publicKeyRingFile.exists()) {
				ArmoredInputStream ais = new ArmoredInputStream(new FileInputStream(publicKeyRingFile));
				PUBLIC_KEY_RINGS = new PGPPublicKeyRingCollection(ais, KEY_FINGERPRINT_CALCULATOR);
				ais.close();
			} else
				PUBLIC_KEY_RINGS = new PGPPublicKeyRingCollection(Collections.emptyList());

		} catch (IOException e) {
			e.printStackTrace();
		} catch (PGPException e) {
			e.printStackTrace();
		}

		try {
			File secretKeyRingfile = new File(SECRET_KEY_RING_FILE_PATH);
			if (secretKeyRingfile.exists()) {
				ArmoredInputStream ais = new ArmoredInputStream(new FileInputStream(secretKeyRingfile));
				SECRET_KEY_RINGS = new PGPSecretKeyRingCollection(ais, KEY_FINGERPRINT_CALCULATOR);
				ais.close();
			} else
				SECRET_KEY_RINGS = new PGPSecretKeyRingCollection(Collections.emptyList());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (PGPException e) {
			e.printStackTrace();
		}

	}

	// More utilities

	private static PGPSignatureSubpacketVector generateKeySubpacketVector() {
		PGPSignatureSubpacketGenerator signatureSubPacketGenerator = new PGPSignatureSubpacketGenerator();
		// Signature purpose
		signatureSubPacketGenerator.setKeyFlags(false, KeyFlags.SIGN_DATA | KeyFlags.CERTIFY_OTHER); // isCritical,
																										// functionalities
		// Set preferences for secondary crypto algorithms to use
		// when sending messages to this key.
		signatureSubPacketGenerator.setPreferredSymmetricAlgorithms(false,
				new int[] { SymmetricKeyAlgorithmTags.TRIPLE_DES, SymmetricKeyAlgorithmTags.AES_128 });
		signatureSubPacketGenerator.setPreferredHashAlgorithms(false, new int[] { HashAlgorithmTags.SHA1 });

		return signatureSubPacketGenerator.generate();
	}

	private static PGPSignatureSubpacketVector generateSubkeySubpacketVector() {
		PGPSignatureSubpacketGenerator signatureSubPacketGenerator = new PGPSignatureSubpacketGenerator();
		// Signature purpose
		signatureSubPacketGenerator.setKeyFlags(false, KeyFlags.ENCRYPT_COMMS | KeyFlags.ENCRYPT_STORAGE); // isCritical,
																											// functionalities
		return signatureSubPacketGenerator.generate();
	}

	public static final Pair<PublicKeyInfo, SecretKeyInfo> generateKeys(User user, KeyMaterial keyMaterial, SubkeyMaterial subkeyMaterial) throws NoSuchAlgorithmException, NoSuchProviderException, PGPException {
        KeyPair keyPair = generateKeyPair(keyMaterial);
        KeyPair subkeyPair = generateSubkeyPair(subkeyMaterial);
        PGPKeyRingGenerator pgpKeyRingGenerator = createKeyRingGenerator(user, keyPair, subkeyPair);

        PGPPublicKeyRing publicKeyRing = pgpKeyRingGenerator.generatePublicKeyRing();
        PUBLIC_KEY_RINGS = PGPPublicKeyRingCollection.addPublicKeyRing(PUBLIC_KEY_RINGS, publicKeyRing);
        exportPublicKeyRings();

        PGPSecretKeyRing secretKeyRing = pgpKeyRingGenerator.generateSecretKeyRing();
        SECRET_KEY_RINGS = PGPSecretKeyRingCollection.addSecretKeyRing(SECRET_KEY_RINGS, secretKeyRing);
        exportSecretKeyRings();

        return new Pair<>(new PublicKeyInfo(publicKeyRing.getPublicKey()), new SecretKeyInfo(secretKeyRing.getSecretKey()));

    }

	private static final KeyPair generateSubkeyPair(SubkeyMaterial subkeyMaterial) {
		KeyPairGenerator keyPairGenerator;
		KeyPair keyPair=null;
		try {
			keyPairGenerator = KeyPairGenerator.getInstance(subkeyMaterial.getName(), "BC");
		
		keyPairGenerator.initialize(subkeyMaterial.getBitSize(), new SecureRandom());
		keyPair = keyPairGenerator.generateKeyPair();
		
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			// TODO Auto-generated catch block
			message=e.getMessage();
		}
		return keyPair;
	}

	private static final KeyPair generateKeyPair(KeyMaterial keyMaterial) {
		KeyPairGenerator keyPairGenerator;
		KeyPair keyPair=null;
		try {
			keyPairGenerator = KeyPairGenerator.getInstance(keyMaterial.getName(), "BC");
		
		keyPairGenerator.initialize(keyMaterial.getBitSize(), new SecureRandom());
		keyPair = keyPairGenerator.generateKeyPair();
		
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			// TODO Auto-generated catch block
			
		}
		return keyPair;
	}

	private static PGPKeyRingGenerator createKeyRingGenerator(User user, KeyPair keyPair, KeyPair subkeyPair) {
		Date date = new Date();
		PGPKeyPair dsaKeyPair;
		PGPKeyRingGenerator keyRingGenerator=null;
		try {
			dsaKeyPair = new JcaPGPKeyPair(PGPPublicKey.DSA, keyPair, date);
		
		PGPKeyPair elGamalKeyPair = new JcaPGPKeyPair(PGPPublicKey.ELGAMAL_ENCRYPT, subkeyPair, date); 
		PGPDigestCalculator sha1DigestCalculator = new JcaPGPDigestCalculatorProviderBuilder().build()
				.get(HashAlgorithmTags.SHA1);
		PBESecretKeyEncryptor secretKeyEncryptor = (user.getPassphrase().length() == 0 ? null
				: new JcePBESecretKeyEncryptorBuilder(
						PGPEncryptedData.AES_128, sha1DigestCalculator).setProvider("BC")
						.build(user.getPassphrase().toCharArray()));
		keyRingGenerator = new PGPKeyRingGenerator(PGPSignature.POSITIVE_CERTIFICATION, dsaKeyPair,
				user.getId(), sha1DigestCalculator, KEY_SIGNATURE_SUBPACKET_VECTOR, null,
				new JcaPGPContentSignerBuilder(dsaKeyPair.getPublicKey().getAlgorithm(), HashAlgorithmTags.SHA1),
				secretKeyEncryptor);
		keyRingGenerator.addSubKey(elGamalKeyPair, SUBKEY_SIGNATURE_SUBPACKET_VECTOR, null);
		} catch (PGPException e) {
			// TODO Auto-generated catch block
			message=e.getMessage();
		}
		return keyRingGenerator;

	}

	public static Collection<PublicKeyInfo> getPublicKeyInfoCollection() {
		Collection<PublicKeyInfo> publicKeyInfoCollection = new ArrayList<>(PUBLIC_KEY_RINGS.size());
		PUBLIC_KEY_RINGS.forEach(keyRing -> publicKeyInfoCollection.add(new PublicKeyInfo(keyRing.getPublicKey())));
		return publicKeyInfoCollection;
	}

	public static Collection<SecretKeyInfo> getSecretKeyInfoCollection() {
		Collection<SecretKeyInfo> secretKeyInfoCollection = new ArrayList<>(SECRET_KEY_RINGS.size());
		SECRET_KEY_RINGS.forEach(keyRing -> secretKeyInfoCollection.add(new SecretKeyInfo(keyRing.getSecretKey())));
		return secretKeyInfoCollection;
	}

	private static void exportSecretKeyRings() {
		try {
			ArmoredOutputStream aos = new ArmoredOutputStream(new FileOutputStream(SECRET_KEY_RING_FILE_PATH));
			for (PGPSecretKeyRing secretKeyRing : SECRET_KEY_RINGS)
				secretKeyRing.encode(aos);
			aos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void exportPublicKeyRings() {
		try {
			ArmoredOutputStream aos = new ArmoredOutputStream(new FileOutputStream(PUBLIC_KEY_RING_FILE_PATH));
			for (PGPPublicKeyRing publicKeyRing : PUBLIC_KEY_RINGS)
				publicKeyRing.encode(aos);
			aos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void exportKey(KeyInfo keyInfo, File file) throws IOException, PGPException {

		ArmoredOutputStream aos = new ArmoredOutputStream(new FileOutputStream(file));
		if (keyInfo instanceof PublicKeyInfo)
			PUBLIC_KEY_RINGS.getPublicKeyRing(keyInfo.getKeyIdLong()).encode(aos);
		else
			SECRET_KEY_RINGS.getSecretKeyRing(keyInfo.getKeyIdLong()).encode(aos);

		aos.close();

	}
	public static List<KeyInfo> importKeyRings(File file) throws Exception {
		List<KeyInfo> keyInfoList = new ArrayList<>();

		PGPObjectFactory objectFactory = new BcPGPObjectFactory(PGPUtil.getDecoderStream(new FileInputStream(file)));
		Iterator iterator = objectFactory.iterator();
		if (!iterator.hasNext())
			throw new IOException("Invalid file format");

		Object object = iterator.next();
		// If file contains public key rings
		if (object instanceof PGPPublicKeyRing) {
			try {
				PGPPublicKeyRing keyRing = (PGPPublicKeyRing) object;
				PUBLIC_KEY_RINGS = PGPPublicKeyRingCollection.addPublicKeyRing(PUBLIC_KEY_RINGS,
						(PGPPublicKeyRing) keyRing);
				keyInfoList.add(new PublicKeyInfo(keyRing.getPublicKey()));
			} catch (IllegalArgumentException e) {
				System.out.println("Collection already contains a key with a keyID for the passed in ring.");
			}

			iterator.forEachRemaining(remainingObject -> {
				try {
					PGPPublicKeyRing keyRing = (PGPPublicKeyRing) remainingObject;
					PUBLIC_KEY_RINGS = PGPPublicKeyRingCollection.addPublicKeyRing(PUBLIC_KEY_RINGS,
							(PGPPublicKeyRing) keyRing);
					keyInfoList.add(new PublicKeyInfo(keyRing.getPublicKey()));
				} catch (IllegalArgumentException e) {
					System.out.println("Collection already contains a key with a keyID for the passed in ring.");
				}
			});

			exportPublicKeyRings();
			// If file contains secret key rings
		} else if (object instanceof PGPSecretKeyRing) {
			try {
				PGPSecretKeyRing keyRing = (PGPSecretKeyRing) object;
				SECRET_KEY_RINGS = PGPSecretKeyRingCollection.addSecretKeyRing(SECRET_KEY_RINGS,
						(PGPSecretKeyRing) keyRing);
				keyInfoList.add(new SecretKeyInfo(keyRing.getSecretKey()));
			} catch (IllegalArgumentException e) {
				System.out.println("Collection already contains a key with a keyID for the passed in ring.");
			}

			iterator.forEachRemaining(remainingObject -> {
				try {
					PGPSecretKeyRing keyRing = (PGPSecretKeyRing) remainingObject;
					SECRET_KEY_RINGS = PGPSecretKeyRingCollection.addSecretKeyRing(SECRET_KEY_RINGS,
							(PGPSecretKeyRing) keyRing);
					keyInfoList.add(new SecretKeyInfo(keyRing.getSecretKey()));
				} catch (IllegalArgumentException e) {
					System.out.println("Public key ring is already in collection.");
				}
			});
			exportSecretKeyRings();
		} else
			throw new IOException("Invalid file format");

		return keyInfoList;
	}

	public static void deletePublicKey(KeyInfo keyInfo) throws PGPException {
		PGPPublicKeyRing publicKeyRing = PUBLIC_KEY_RINGS.getPublicKeyRing(keyInfo.getKeyIdLong());
		PUBLIC_KEY_RINGS = PGPPublicKeyRingCollection.removePublicKeyRing(PUBLIC_KEY_RINGS, publicKeyRing);
		exportPublicKeyRings();
	}

	public static void deleteSecretKey(KeyInfo keyInfo, String passphrase) throws PGPException {
		PGPSecretKeyRing secretKeyRing = SECRET_KEY_RINGS.getSecretKeyRing(keyInfo.getKeyIdLong());
		secretKeyRing.getSecretKey().extractPrivateKey(new JcePBESecretKeyDecryptorBuilder().setProvider("BC")
				.build(passphrase.toCharArray()));
			SECRET_KEY_RINGS = PGPSecretKeyRingCollection.removeSecretKeyRing(SECRET_KEY_RINGS, secretKeyRing);
		exportSecretKeyRings();
	}

	public static boolean isEncrypted(KeyInfo keyInfo) throws PGPException {
		return SECRET_KEY_RINGS.getSecretKey(keyInfo.getKeyIdLong()).getS2K() != null;
	}

	public static boolean isEncrypted(PGPSecretKey secretKey) {
		return secretKey.getS2K() != null;
	}

	public static PGPSecretKeyRing getSecretKeyRing(long keyID) throws PGPException {
		return SECRET_KEY_RINGS.getSecretKeyRing(keyID);
		// private?
	}

	public static PGPSecretKey getSecretKey(long keyId) throws PGPException {
		return SECRET_KEY_RINGS.getSecretKey(keyId);
		// private?
	}

	public static PGPPublicKey getPublicKey(long keyId) throws PGPException {
		return PUBLIC_KEY_RINGS.getPublicKey(keyId);
	}

	public static PGPPublicKey getPublicSubkey(long keyIdLong) throws PGPException {
		PGPPublicKeyRing pkr = PUBLIC_KEY_RINGS.getPublicKeyRing(keyIdLong);
		Iterator<PGPPublicKey> it = pkr.getPublicKeys();
		it.next();
		return it.next();

	}

	// Extract Private key
	public static PGPPrivateKey extractPrivateKey(PGPSecretKey secretKey, String passphrase) throws PGPException {
		return secretKey.extractPrivateKey(new JcePBESecretKeyDecryptorBuilder().setProvider("BC")
				.build(/* passphrase == null ? null : */passphrase.toCharArray()));
	}
}