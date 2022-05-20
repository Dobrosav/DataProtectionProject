package etf.openpgp.vd180005d;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;  

import org.bouncycastle.util.encoders.Base64;

import etf.openpgp.ma180126d.AsymmetricKeys;

public class DSA  extends AsymmetricKeys {
	public void generate(int size, String mail, String name, String password) {
		try {
			LocalDateTime t=java.time.LocalDateTime.now();
			KeyPairGenerator kpg= KeyPairGenerator.getInstance("DSA");
			kpg.initialize(size);
			KeyPair key=kpg.generateKeyPair();
			PrivateKey priv=key.getPrivate();
			PublicKey pub=key.getPublic();
			KeySaveStructure kss=new KeySaveStructure();
			kss.setIme(name);
			kss.setKeyPair(key);
			kss.setMail(mail);
			kss.setPassword(password);
			kss.setTime(t);
			kss.setAlgo("DSA");
			keys.add(kss);
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
		DSA t= new DSA();
		t.generate(1024,"vlaskovic.dodo@gmail.com","dobrosav","1234");
		t.exportPublicKey(0, "pub.asc");
		t.exportPrivateKey(0, "priv.asc");
		
	}

}
