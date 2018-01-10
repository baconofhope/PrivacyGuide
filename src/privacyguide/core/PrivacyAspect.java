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

import java.util.ArrayList;

import weka.core.Instances;

/**
 * PrivacyAspect. This class is a generic class for storing the different privacy aspects.
 * Please note that every class needs to have two attributes: name and location. Whereas
 * location specifies the directory where the data of that specific category can be found.
 * 
 * @author Peter Hofmann (peter_hofmann91@gmx.de)
 * @version $Revision: 1 $
 */

public class PrivacyAspect {
	
	private String name;
	private String location;
	private String description;
	private String[] keywords;
	private String classifier;
	private String[] preprocessing;
	private Instances rawData;
	private Instances filteredData;
	private ArrayList<RiskClass> riskClasses = new ArrayList<RiskClass>();
	
	
	/** Constructor */
	public PrivacyAspect(String definedName, String definedLocation, String definedDescription, String[] keywords){
		setName(definedName);
		setLocation(definedLocation);
		setDescription(definedDescription);
		setKeyWordList(keywords);
	}
	
	public void setRiskClasses(ArrayList<RiskClass> classes){
		riskClasses = classes;
	}
	
	public ArrayList<RiskClass> getRiskClasses(){
		return riskClasses;
	}

	public String getClassifier() {
		return classifier;
	}

	public void setClassifier(String classifier) {
		this.classifier = classifier;
	}

	public String getName(){
		return name;
	}
	
	public void setName(String definedName){
		name = definedName;
	}
	
	public String getLocation(){
		return location;
	}
	
	public void setLocation(String definedLocation){
		location = definedLocation;
	}
	
	public String getDescription(){
		return description;
	}
	
	public void setDescription(String definedDescription){
		description = definedDescription;
	}
	
	public String[] getKeyWordList(){
		return keywords;
	}
	
	public void setKeyWordList(String[] newKeyWordList){
		keywords = newKeyWordList;
	}

	public Instances getRawData() {
		return rawData;
	}

	public void setRawData(Instances rawData) {
		this.rawData = rawData;
	}

	public Instances getFilteredData() {
		return filteredData;
	}

	public void setFilteredData(Instances filteredData) {
		this.filteredData = filteredData;
	}

	public String[] getPreprocessing() {
		return preprocessing;
	}

	public void setPreprocessing(String[] preprocessing) {
		this.preprocessing = preprocessing;
	}
	
}
