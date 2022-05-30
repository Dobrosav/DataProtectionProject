package etf.openpgp.vd180005d.gui.utils;

import etf.openpgp.ma180126d.entites.*;
import etf.openpgp.vd180005d.KeyManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class KeyInfoObservableLists {
    private static final ObservableList<KeyInfo> publicKeyObservableList = FXCollections.observableArrayList();
    private static final ObservableList<KeyInfo> secretKeyObservableList = FXCollections.observableArrayList();

    static {
        publicKeyObservableList.addAll(KeyManager.getPublicKeyInfoCollection());
        secretKeyObservableList.addAll(KeyManager.getSecretKeyInfoCollection());

    }

    public static ObservableList<KeyInfo> getPublicKeyObservableList() {
        return publicKeyObservableList;
    }

    public static ObservableList<KeyInfo> getSecretKeyObservableList() {
        return secretKeyObservableList;
    }
}
