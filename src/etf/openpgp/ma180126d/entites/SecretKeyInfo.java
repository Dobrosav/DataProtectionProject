package etf.openpgp.ma180126d.entites;

import org.bouncycastle.openpgp.PGPSecretKey;

public class SecretKeyInfo extends KeyInfo {

    public SecretKeyInfo(PGPSecretKey pgpSecretKey) {
        super(pgpSecretKey.getKeyID(), pgpSecretKey.getUserIDs().next());
    }


}
