package etf.openpgp.ma180126d.exceptions;

public class PassphraseRequiredException extends ReceiverException {


    public PassphraseRequiredException(long keyID) {
        super(keyID);
    }
}
