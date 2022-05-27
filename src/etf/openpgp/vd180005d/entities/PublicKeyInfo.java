package etf.openpgp.vd180005d.entities;

import org.bouncycastle.openpgp.PGPPublicKey;

public class PublicKeyInfo extends KeyInfo {

    public PublicKeyInfo(PGPPublicKey pgpPublicKey) {
        super(pgpPublicKey.getKeyID(), pgpPublicKey.getUserIDs().next());
    }

}
