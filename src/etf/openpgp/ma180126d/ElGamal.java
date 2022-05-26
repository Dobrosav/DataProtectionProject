package etf.openpgp.ma180126d;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.time.LocalDateTime;  

import org.bouncycastle.util.encoders.Base64;
import etf.openpgp.vd180005d.KeySaveStructure;

public class ElGamal extends AsymmetricKeys {
	
	public KeyPair generate (int size, String mail, String name, String password) {
		
		try {
			LocalDateTime time = java.time.LocalDateTime.now();
			KeyPairGenerator kpg= KeyPairGenerator.getInstance("ELGAMAL", "BC");
			kpg.initialize(size);
			KeyPair keyPair = kpg.generateKeyPair();
			
			PrivateKey priv = keyPair.getPrivate();
			PublicKey pub = keyPair.getPublic();
			
			KeySaveStructure kss = new KeySaveStructure();
			kss.setIme(name);
			kss.setKeyPair(keyPair);
			kss.setMail(mail);
			kss.setPassword(password);
//			kss.setTime(time);
			kss.setAlgo("Elgamal");
			keys.add(kss);
			
			String privateKey = new String(Base64.encode(priv.getEncoded(), 0,priv.getEncoded().length));
			String publicKey1 = new String(Base64.encode(pub.getEncoded(), 0,pub.getEncoded().length));
			String publicKey = new String(Base64.encode(publicKey1.getBytes(),0, publicKey1.getBytes().length));
			
			System.out.println(privateKey);
			System.out.println(publicKey);

			return keyPair;
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static void main(String[] args) {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		ElGamal e = new ElGamal();
		e.generate(1024,"aleksandra.milovic9@gmail.com","aleksandra","123");
	}

}
