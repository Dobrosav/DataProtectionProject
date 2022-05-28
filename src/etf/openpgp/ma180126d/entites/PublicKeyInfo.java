package etf.openpgp.ma180126d.entites;

import org.bouncycastle.openpgp.PGPPublicKey;

public class PublicKeyInfo extends KeyInfo {

    public PublicKeyInfo(PGPPublicKey pgpPublicKey) {
        super(pgpPublicKey.getKeyID(), pgpPublicKey.getUserIDs().next());
    }

}
