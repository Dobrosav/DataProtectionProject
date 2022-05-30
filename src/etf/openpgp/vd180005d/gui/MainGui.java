package etf.openpgp.vd180005d.gui;

import etf.openpgp.vd180005d.gui.utils.UIUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainGui extends Application {
	
	public static void main(String arg[]) {
		launch();
	}

	@Override
	public void start(Stage arg0) throws Exception {
		// TODO Auto-generated method stub
		Parent root = FXMLLoader.load(this.getClass().getClassLoader().getResource("home.fxml"));
        arg0.setTitle("Alex&Dobri Crypto");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(this.getClass().getClassLoader().getResource("style.css").toExternalForm());
        UIUtils.setStage(arg0);
        arg0.setScene(scene);
        arg0.show();
	}
}
