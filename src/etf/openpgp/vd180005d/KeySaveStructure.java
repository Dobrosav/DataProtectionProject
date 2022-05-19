package etf.openpgp.vd180005d;

import java.security.KeyPair;
import java.sql.Timestamp;

public class KeySaveStructure {
	private KeyPair keyPair;
	private String ime;
	private String password, mail;
	private Timestamp time;
	private long id;
	private static long next=0;
	private String algo;
	public KeySaveStructure() {
		id=next++;
	}
	public Timestamp getTime() {
		return time;
	}
	public void setTime(Timestamp time) {
		this.time = time;
	}
	public KeyPair getKeyPair() {
		return keyPair;
	}
	public void setKeyPair(KeyPair keyPair) {
		this.keyPair = keyPair;
	}
	public String getIme() {
		return ime;
	}
	public void setIme(String ime) {
		this.ime = ime;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	
	public String getAlgo() {
		return algo;
	}
	public void setAlgo(String algo) {
		this.algo = algo;
	}
	public long getId() {
		return id;
	}
	public String toString() {
		return "";
	}
}
