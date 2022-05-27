package etf.openpgp.vd180005d.entities;

import org.bouncycastle.openpgp.PGPSecretKey;

public class SecretKeyInfo extends KeyInfo {

    public SecretKeyInfo(PGPSecretKey pgpSecretKey) {
        super(pgpSecretKey.getKeyID(), pgpSecretKey.getUserIDs().next());
    }


}
