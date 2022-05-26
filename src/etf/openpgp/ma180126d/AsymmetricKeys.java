package etf.openpgp.ma180126d;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.Key;
import java.security.KeyPair;
import java.security.Security;
import java.sql.Date;
import java.util.ArrayList;

import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.jcajce.provider.symmetric.ARC4.Base;
import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPKeyConverter;

import etf.openpgp.vd180005d.DSA;
import etf.openpgp.vd180005d.KeySaveStructure;
import etf.openpgp.vd180005d.PemFile;

public abstract class AsymmetricKeys {
	private static final String descDSA = "PGP PUBLIC KEY BLOCK";
	private static final String descDSAP = "PGP PRIVATE KEY BLOCK";
	public static ArrayList<KeySaveStructure> keys = new ArrayList<KeySaveStructure>();

	public int delete(int id, String password) {
		KeySaveStructure kss = keys.get(id);

		if (kss.getPassword().equals(password)) {
			keys.remove(id);
			return 0;
		}

		return 1;
	}

	public abstract KeyPair generate(int size, String mail, String name, String password);
	
	public static final void createPGPKeyRingGenerator(KeyPair dsaKeyPair, KeyPair egKeyPair, String identity, char[] passphrase) throws Exception
    {
		JcaPGPKeyConverter pgpConverter = new JcaPGPKeyConverter();
		
		PGPPublicKey dsaPublicKey = pgpConverter.getPGPPublicKey(PGPPublicKey.DSA, dsaKeyPair.getPublic(), new Date(0));
		PGPPublicKey egPublicKey = pgpConverter.getPGPPublicKey(PGPPublicKey.ELGAMAL_ENCRYPT, egKeyPair.getPublic(), new Date(0));
		PGPPrivateKey dsaPrivateKey = pgpConverter.getPGPPrivateKey(dsaPublicKey, dsaKeyPair.getPrivate());
		PGPPrivateKey egPrivateKey = pgpConverter.getPGPPrivateKey(egPublicKey, egKeyPair.getPrivate());
		byte[] s=dsaPublicKey.getEncoded();
		String st=java.util.Base64.getEncoder().encodeToString(s);
		System.out.println("dsaPublicKey : " + st);
		s=egPublicKey.getEncoded();
	    st=java.util.Base64.getEncoder().encodeToString(s);
		System.out.println("egPublicKey : " + st);
		System.out.println("dsaPrivateKey : " + dsaPrivateKey);
		System.out.println("egPrivateKey : " + egPrivateKey);
    }
	
	public static void test(){	
		DSA dsa = new DSA();
		ElGamal eg = new ElGamal();
		KeyPair dsaKeyPair = dsa.generate(1024, "aleksandra.milovic9@gmail.com","aleksandra","123");
        KeyPair egKeyPair = eg.generate(1024, "aleksandra.milovic9@gmail.com","aleksandra","123");
        try {
			createPGPKeyRingGenerator(dsaKeyPair, egKeyPair, "aleksandra.milovic9@gmail.com", "123".toCharArray());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//PGPKeyRingGenerator pgpKeyRingGen = PGPTools.createPGPKeyRingGenerator(dsaKeyPair, elGamalKeyPair, "test@gmail.com", "TestPass12345!".toCharArray());
	}

	public void exportPublicKey(int id, String filenameurl) {
		Key key = keys.get(id).getKeyPair().getPublic();
		PemFile pemFile = new PemFile(key, descDSA);
		try {
			pemFile.write(filenameurl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void exportPrivateKey(int id, String filenameurl) {
		Key key = keys.get(id).getKeyPair().getPrivate();
		PemFile pemFile = new PemFile(key, descDSAP);
		try {
			pemFile.write(filenameurl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void importPublicKey(File file) throws IOException {
	    String key = new String(Files.readAllBytes(file.toPath()), Charset.defaultCharset());

	    String publicKeyPEM = key
	      .replace("-----BEGIN PGP PUBLIC KEY BLOCK-----", "")
	      .replaceAll(System.lineSeparator(), "")
	      .replace("-----END PGP PUBLIC KEY BLOCK-----", "");

	    byte[] encoded = Base64.decode(publicKeyPEM);
	    System.out.println(encoded.toString());
	}
	
	public static void main(String[] args) {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		test();
	}
}
