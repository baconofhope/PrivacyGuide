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

import org.apache.commons.lang3.text.WordUtils;

/**
 * Prediction. This class is meant for storing the different prediction values
 * including the raw data and the referred PrivacyAspect.
 * 
 * @author Peter Hofmann (peter_hofmann91@gmx.de)
 * @version $Revision: 1 $
 */

@SuppressWarnings("deprecation")
public class Prediction {

	private String rawData;
	private String predictedClass;
	private Integer numericalClass;
	private String iconLocation;
	private String toolTipText;
	private PrivacyAspect referringAspect;
	private double predictionLikelihood;

	public Prediction(PrivacyAspect referringAspect, String rawData, String predictedClass, double distributionForInstance) {
		setRawData(rawData);
		setPredictedClass(predictedClass);
		setReferringAspect(referringAspect);
		defineLabelAndIcon();
		setPredictionLikelihood(distributionForInstance);
		
	}

	private void defineLabelAndIcon() {
		switch (predictedClass) {
		case "A": {
			setIconLocation(referringAspect.getRiskClasses().get(1).getIconLocation());
			setToolTipText("Predicted Class: " + predictedClass + "\n\n" + "Description: "
					+ referringAspect.getRiskClasses().get(1).getDescription() + "\n\n" + "Original paragraph: \n"
					+ "\"" + WordUtils.wrap(getRawData(), 75) + "\"");
			setNumericalClass(referringAspect.getRiskClasses().get(1).getNumericClass());
		}
			break;
		case "B": {
			setIconLocation(referringAspect.getRiskClasses().get(2).getIconLocation());
			setToolTipText("Predicted Class: " + predictedClass + "\n\n" + "Description: "
					+ referringAspect.getRiskClasses().get(2).getDescription() + "\n\n" + "Original paragraph: \n"
					+ "\"" + WordUtils.wrap(getRawData(), 75) + "\"");
			setNumericalClass(referringAspect.getRiskClasses().get(2).getNumericClass());
		}
			break;
		case "C": {
			setIconLocation(referringAspect.getRiskClasses().get(3).getIconLocation());
			setToolTipText("Predicted Class: " + predictedClass + "\n\n" + "Description: "
					+ referringAspect.getRiskClasses().get(3).getDescription() + "\n\n" + "Original paragraph: \n"
					+ "\"" + WordUtils.wrap(getRawData(), 75) + "\"");
			setNumericalClass(referringAspect.getRiskClasses().get(3).getNumericClass());
		}
			break;
		default: {
			setIconLocation(referringAspect.getRiskClasses().get(0).getIconLocation());
			setToolTipText("Predicted Class: " + predictedClass + "\n\n" + "Description: "
					+ referringAspect.getRiskClasses().get(0).getDescription() + "\n\n" + "Original paragraph: \n"
					+ "\"" + WordUtils.wrap(getRawData(), 75) + "\"");
			setNumericalClass(referringAspect.getRiskClasses().get(0).getNumericClass());
		}
			break;
		}
	}

	public String getRawData() {
		return rawData;
	}

	public void setRawData(String rawData) {
		this.rawData = rawData;
	}

	public String getPredictedClass() {
		return predictedClass;
	}

	public void setPredictedClass(String predictedClass) {
		this.predictedClass = predictedClass;
	}

	public PrivacyAspect getReferringAspect() {
		return referringAspect;
	}

	public void setReferringAspect(PrivacyAspect referringAspect) {
		this.referringAspect = referringAspect;
	}

	public String getToolTipText() {
		return toolTipText;
	}

	public void setToolTipText(String toolTipText) {
		this.toolTipText = toolTipText;
	}

	public String getIconLocation() {
		return iconLocation;
	}

	public void setIconLocation(String iconLocation) {
		this.iconLocation = iconLocation;
	}

	public Integer getNumericalClass() {
		return numericalClass;
	}

	public void setNumericalClass(Integer numericalClass) {
		this.numericalClass = numericalClass;
	}

	public double getPredictionLikelihood() {
		return predictionLikelihood;
	}

	public void setPredictionLikelihood(double predictionLikelihood) {
		this.predictionLikelihood = predictionLikelihood;
	}

}
