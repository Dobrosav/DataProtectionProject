package etf.openpgp.vd180005d;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.bouncycastle.util.encoders.Base64;

public class Test {
	public void generate(int size) {
		try {
			KeyPairGenerator kpg= KeyPairGenerator.getInstance("DSA");
			kpg.initialize(size);
			KeyPair key=kpg.generateKeyPair();
			PrivateKey priv=key.getPrivate();
			PublicKey pub=key.getPublic();
			String privateKey = new String(Base64.encode(priv.getEncoded(), 0,priv.getEncoded().length));
			String publicKey1 = new String(Base64.encode(pub.getEncoded(), 0,pub.getEncoded().length));
			String publicKey = new String(Base64.encode(publicKey1.getBytes(),0, publicKey1.getBytes().length));
			System.out.println(privateKey);
			System.out.println(publicKey);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Test t= new Test();
		t.generate(1024);
	}

}
