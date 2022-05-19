package etf.openpgp.ma180126d;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.Key;
import java.util.ArrayList;

import org.bouncycastle.util.encoders.Base64;

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

	public abstract void generate(int size, String mail, String name, String password);

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
}
