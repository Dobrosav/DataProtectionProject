package etf.openpgp.ma180126d.exceptions;

public class KeyNotFoundException extends ReceiverException {

    public KeyNotFoundException(long keyID) {
        super(keyID);
    }
}
