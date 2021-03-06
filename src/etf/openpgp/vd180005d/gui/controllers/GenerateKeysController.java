package etf.openpgp.vd180005d.gui.controllers;


import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;

import java.security.Key;

import etf.openpgp.ma180126d.keymaterial.KeyMaterial;
import etf.openpgp.ma180126d.keymaterial.SubkeyMaterial;

public class GenerateKeysController {
    @FXML
    private TextField usernameTextField;

    @FXML
    private TextField emailTextField;

    @FXML
    private PasswordField passphraseTextField;

    @FXML
    private ListView<KeyMaterial> singatureListView;

    @FXML
    private ListView<SubkeyMaterial> encryptionListView;

    @FXML
    private void initialize() {
        //singatureListView.getItems().addAll(KeyMaterial.values());
    	//singatureListView = new ListView<KeyMaterial>();
        singatureListView.getItems().add(KeyMaterial.DSA_1024);
        singatureListView.getItems().add(KeyMaterial.DSA_2048);
        singatureListView.getSelectionModel().select(KeyMaterial.DSA_1024);
        encryptionListView.getItems().addAll(SubkeyMaterial.values());
        encryptionListView.getSelectionModel().select(SubkeyMaterial.EL_GAMAL_1024);

    }

    public String getEmail() {
        return emailTextField.getText();
    }

    public String getUsername() {
        return usernameTextField.getText();
    }

    public String getPassphrase() {
        return passphraseTextField.getText();
    }

    public KeyMaterial getKeyMaterial() {
        return this.singatureListView.getSelectionModel().getSelectedItem();
    }
    public SubkeyMaterial getSubkeyMaterial() {
        return this.encryptionListView.getSelectionModel().getSelectedItem();
    }

}
