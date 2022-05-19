package etf.openpgp.vd180005d;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

import org.bouncycastle.util.encoders.Base64;

public class Test {
	public static ArrayList<KeySaveStructure> keys= new ArrayList<KeySaveStructure>();
	public void generate(int size, String mail, String name, String password) {
		try {
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
	public int delete(int id, String password) {
		KeySaveStructure kss=keys.get(id);
		if(kss.getPassword().equals(password)) {
			keys.remove(id);
			return 0;
		}
		return 1;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Test t= new Test();
		t.generate(1024,"vlaskovic.dodo@gmail.com","dobrosav","1234");
	}

}
