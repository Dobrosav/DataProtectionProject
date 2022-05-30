package etf.openpgp.ma180126d.exceptions;

public class SignerKeyNotFoundException extends ReceiverException{
    public SignerKeyNotFoundException(long keyID) {
        super(keyID);
    }
}
