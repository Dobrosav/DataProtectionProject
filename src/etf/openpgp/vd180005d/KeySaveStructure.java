package etf.openpgp.vd180005d;

import java.security.KeyPair;

public class KeySaveStructure {
	private KeyPair keyPair;
	private String ime;
	private String password, mail;
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
	
}
