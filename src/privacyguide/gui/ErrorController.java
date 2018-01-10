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

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * ErrorController. This class loads and controls the error view.
 * 
 * @author Peter Hofmann (peter_hofmann91@gmx.de)
 * @version $Revision: 1 $
 */

public class ErrorController {

    @FXML
    private ResourceBundle resources;
    
    @FXML
    private ImageView ivThumbsDown;

    @FXML
    private URL location;

    @FXML
    private Button btnClose;
    
    @FXML Label lblErrorText;

    @FXML
    void hide(ActionEvent event) {
    	((Node)(event.getSource())).getScene().getWindow().hide();
    }

    @FXML
    void initialize() {
    	ivThumbsDown.setImage(new Image(this.getClass().getResource("/dislike.png").toString()));
    	
    }
}