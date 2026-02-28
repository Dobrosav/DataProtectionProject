package etf.openpgp.vd180005d.gui.controllers;

import etf.openpgp.vd180005d.gui.utils.UIUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;


public class HomeController {

    @FXML
    private ImageView imageViewLocked;
    @FXML
    private ImageView imageViewUnlocked;
    @FXML
    private ImageView imageViewKeys;

    @FXML
    private void initialize() {
    	this.welcome.setVisible(false);
        this.home.setVisible(true);
        imageViewLocked.setImage(new Image(this.getClass().getClassLoader().getResource("first.png").toExternalForm()));
        imageViewKeys.setImage(new Image(this.getClass().getClassLoader().getResource("second.png").toExternalForm()));
        imageViewUnlocked.setImage(new Image(this.getClass().getClassLoader().getResource("third.png").toExternalForm()));
    }

    @FXML
    private StackPane root;

    @FXML
    private GridPane home;

    @FXML
    private AnchorPane welcome;

    @FXML
    private void hide(MouseEvent event) {
        this.welcome.setVisible(false);
        this.home.setVisible(true);
    }

    @FXML
    private void showDecryptPage(ActionEvent event) {
        UIUtils.getInstance().switchPages("decryptPage.fxml");
    }

    @FXML
    private void showEncryptPage(ActionEvent event) {
        UIUtils.getInstance().switchPages("encryptPage.fxml");

    }

    @FXML
    private void showKeyManagementPage(ActionEvent event) {
        UIUtils.getInstance().switchPages("keyManagementPage.fxml");

    }

}
