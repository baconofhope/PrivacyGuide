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

import weka.core.Instances;
import weka.core.Instance;
import weka.core.DenseInstance;

/**
 * PolicySubset. This class is a generic class for storing different paragraphs
 * of a privacy policy that belong to a certain kind of privacy aspects.
 * Instances of this class are used to enable the classification process.
 * 
 * @author Peter Hofmann (peter_hofmann91@gmx.de)
 * @version $Revision: 1 $
 */

public class PolicySubset {

	private String rawData;
	private PrivacyAspect referringAspect;

	public PolicySubset(String data, PrivacyAspect referringAspect) {
		setRawData(data);
		setReferringAspect(referringAspect);
		addInstances();
	}

	/**
	 * Adds an unknown instance (a paragraph from the privacy policy which
	 * should be analysed) to the training set of the PrivacyAspect. This is a
	 * necessary step because of the way WEKA works. The training and unknown
	 * data instances need to have exactly the same attributes.
	 * 
	 * For further information please see:
	 * http://weka.wikispaces.com/Why+do+I+get+the+error+message+%27training+and+test+set+are+not+compatible%27%3F
	 */

	private void addInstances() {

		// Load original data
		Instances original = referringAspect.getRawData();
		// Add new unknown dataset
		Instance additional = new DenseInstance(2);
		additional.setValue(original.attribute(0), getRawData());
		original.add(additional);
		// Reset original data
		referringAspect.setRawData(original);
	}

	public String getRawData() {
		return rawData;
	}

	public void setRawData(String rawData) {
		this.rawData = rawData;
	}

	public PrivacyAspect getReferringAspect() {
		return referringAspect;
	}

	public void setReferringAspect(PrivacyAspect privacyAspect) {
		this.referringAspect = privacyAspect;
	}
}
