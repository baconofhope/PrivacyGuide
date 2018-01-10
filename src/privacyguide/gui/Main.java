//    Copyright (C) 2017  Peter Hofmann

//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.

//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.

//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>

package privacyguide.gui;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;

/**
 * Main. This class initially loads the MainView GUI.
 * All other methods are done within the MainViewController Class.
 * 
 * @author Peter Hofmann (peter_hofmann91@gmx.de)
 * @version $Revision: 1 $
 */

public class Main extends Application {
	// static Corpus corpus = new Corpus();
	MainViewController controller;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));

		controller = loader.getController();

		Parent root = loader.load();
		Scene scene = new Scene(root);

		primaryStage.setTitle("PrivacyGuide");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
