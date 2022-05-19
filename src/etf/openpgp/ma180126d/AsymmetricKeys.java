package etf.openpgp.ma180126d;

import java.util.ArrayList;

import etf.openpgp.vd180005d.KeySaveStructure;

public abstract class AsymmetricKeys {
	
	public static ArrayList<KeySaveStructure> keys= new ArrayList<KeySaveStructure>();
	
	public int delete(int id, String password) {
		KeySaveStructure kss = keys.get(id);
		
		if(kss.getPassword().equals(password)) {
			keys.remove(id);
			return 0;
		}
		
		return 1;
	}
	
	abstract void generate (int size, String mail, String name, String password);
}
