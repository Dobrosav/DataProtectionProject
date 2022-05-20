package etf.openpgp.ma180126d;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Timestamp;

import org.bouncycastle.util.encoders.Base64;

public class PrivateKeyRing {
	private Timestamp time;
	private String keyID;
	private KeyPair keyPair;
	private PublicKey publicKey;
	private PrivateKey privateKey;
	private String userID;
	
	public PrivateKeyRing(Timestamp time, String userID, KeyPair keyPair) {
		this.time = time;
		this.userID = userID;
		this.keyPair = keyPair;
	}

	public Timestamp getTime() {
		return time;
	}

//	public void setTime(Timestamp time) {
//		this.time = time;
//	}
	
	public String getKeyID() {
		return keyID;
	}
	
	public void setKeyPair(KeyPair keyPair) {
		this.keyPair = keyPair;
		this.publicKey = this.keyPair.getPublic();
		this.privateKey = this.keyPair.getPrivate();
		
		// set keyID
		//this.keyID=this.publicKey & ((1 << 64) - 1);
	}
	
	public String getPublicKey() {
		if(this.publicKey == null) return "";
		String pk = new String(Base64.encode(this.publicKey.getEncoded(), 0, this.publicKey.getEncoded().length));
		return new String(Base64.encode(pk.getBytes(),0, pk.getBytes().length));
	}
	
	public String getEncryptedPrivateKey() {
		if(this.privateKey == null) return "";
		return this.privateKey.toString();
		//return new String(Base64.encode(this.privateKey.getEncoded(), 0, this.privateKey.getEncoded().length));
	}
	
	public String getUserID() {
		return userID;
	}
	
//	public void setUserID(String userID) {
//		this.userID = userID;
//	}
	
	public static void main(String[] args) {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		ElGamal e = new ElGamal();
		e.generate(1024,"aleksandra.milovic9@gmail.com","aleksandra","123");
		PrivateKeyRing pkr = new PrivateKeyRing(e.keys.get(0).getTime(), e.keys.get(0).getMail(), e.keys.get(0).getKeyPair());
		System.out.println(pkr.getEncryptedPrivateKey());
	}
	
	
}
