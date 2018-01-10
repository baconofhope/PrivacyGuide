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

package privacyguide.core;

/**
 * RiskClass. This class is a generic class for storing the risk classes that refer to the privacy aspects.
 * All attributes like, name, description and location of the icon are mandatory as they are used for displaying
 * the classification results to the user.
 * 
 * @author Peter Hofmann (peter_hofmann91@gmx.de)
 * @version $Revision: 1 $
 */

public class RiskClass {
	private String name;
	private Integer numericClass;
	private String description;
	private String iconLocation;
	
	
	public RiskClass (String name, Integer numericClass, String description, String iconLocation){
		this.setName(name);
		this.setNumericClass(numericClass);
		this.setDescription(description);
		this.setIconLocation(iconLocation);
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getIconLocation() {
		return iconLocation;
	}


	public void setIconLocation(String iconLocation) {
		this.iconLocation = iconLocation;
	}


	public Integer getNumericClass() {
		return numericClass;
	}


	public void setNumericClass(Integer numericClass) {
		this.numericClass = numericClass;
	}

	
}
