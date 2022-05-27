package etf.openpgp.vd180005d;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import org.bouncycastle.openpgp.PGPException;

import etf.openpgp.vd180005d.entities.User;
import etf.openpgp.vd180005d.keymaterial.KeyMaterial;
import etf.openpgp.vd180005d.keymaterial.SubkeyMaterial;

public class Test {
	public static void main(String args[]) {
		try {
			KeyManager.generateKeys(new User("dodo","dodo@gmail.com","123"),KeyMaterial.DSA_1024 , SubkeyMaterial.EL_GAMAL_2048);
		} catch (NoSuchAlgorithmException | NoSuchProviderException | PGPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
