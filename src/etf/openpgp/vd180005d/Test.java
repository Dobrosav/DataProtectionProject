package etf.openpgp.vd180005d;


import org.bouncycastle.openpgp.PGPException;

import etf.openpgp.ma180126d.entites.User;
import etf.openpgp.ma180126d.keymaterial.KeyMaterial;
import etf.openpgp.ma180126d.keymaterial.SubkeyMaterial;

public class Test {
	public static void main(String args[]) {
		try {
			KeyManager.generateKeys(new User("dodo","dodo@gmail.com","123"),KeyMaterial.DSA_1024 , SubkeyMaterial.EL_GAMAL_2048);
			
		} catch (PGPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
