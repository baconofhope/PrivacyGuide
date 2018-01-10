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

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


/**
 * AboutController. This class is loads and controls the about view.
 * 
 * 
 * @author Peter Hofmann (peter_hofmann91@gmx.de)
 * @version $Revision: 1 $
 */

public class AboutController {

	@FXML
	private ImageView ivGU;

	@FXML
	void initialize() {
		ivGU.setImage(new Image(this.getClass().getResource("/GU-Logo-weiss.png").toString()));
	}
}
