package etf.openpgp.ma180126d.exceptions;

public class InvalidPassphraseException extends ReceiverException {

    public InvalidPassphraseException(long keyID) {
        super(keyID);
    }
}
