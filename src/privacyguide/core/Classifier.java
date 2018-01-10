//Copyright (C) 2017  Peter Hofmann

//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.

//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.

//You should have received a copy of the GNU General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>

package privacyguide.core;

import java.util.ArrayList;
import java.util.Random;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

/**
* Classifer. This class is a convenience class to handle multiple types of classification
* algorithms at once. Furthermore it is prepared for multithreading as it extends the Thread
* class. 
* 
* @author Peter Hofmann (peter_hofmann91@gmx.de)
* @version $Revision: 1 $
*/

public class Classifier extends Thread {

private AbstractClassifier classifier;
private String typeOfClassifier;
private Instances trainingSet;
private Instances testingSet;
private Instances rawData;
private ArrayList<Prediction> predictions = new ArrayList<Prediction>();
private PrivacyAspect privacyAspect;
private String evaluationResults;

public Classifier(String classifierType, Instances training, Instances testing, Instances raw,
		PrivacyAspect aspect) {

	// Forward variables

	setTypeOfClassifier(classifierType);
	setTrainingSet(training);
	setTestingSet(testing);
	setRawData(raw);
	setPrivacyAspect(aspect);

	// Select classification algorithm

	switch (getTypeOfClassifier()) {
	case "NaiveBayes": {
		classifier = new NaiveBayes();
	}
		break;
	case "Tree": {
		classifier = new J48();
	}
		break;
	case "SVM": {
		classifier = new SMO();
		((SMO) classifier).setBuildCalibrationModels(true);
	}
		break;
	case "NeuralNetwork": {
		classifier = new MultilayerPerceptron();
	}
		break;
	case "Ensemble": {
		classifier = new RandomForest();
	}
	}
}

public void run() {

	// Methods are called for multithreading
	trainClassifier(getTrainingSet());
    //evaluateClassifier(trainingSet, testingSet);
	predictClasses(getPrivacyAspect(), getTrainingSet(), getRawData());
}

/**
* Builds the classifier with the given trainingData
*
* @param trainingInstances Instances
*            the training set that is used to build the classifier.
*/

public void trainClassifier(Instances trainingInstances) {
	try {
		getClassifier().buildClassifier(trainingInstances);
	} catch (Exception e) {
		System.out.println("Build classifier failed");
		e.printStackTrace();
	}
}

/**
* Computes the performance of the classifier by printing out several performance metrics.
* The results are based on a 10-fold cross validation. 
*
* @param trainingInstances Instances
*            the training set that was used to train the classifier
* @param testingInstances Instances 
* 			  the testing set that is used to test the classifier
* 
*/

public void evaluateClassifier(Instances trainingInstances, Instances testingInstances) {

	Random seed = new Random();
	try {
		Evaluation eval = new Evaluation(trainingInstances);
		// Cross validate model with random seed
		eval.crossValidateModel(getClassifier(), trainingInstances, 10, seed);
		// Print out evaluation summary
		this.setEvaluationResults(eval.toSummaryString(
				"Evaluation results: " + this.getTypeOfClassifier() + " \n" + "\n" + "Aspect: " + privacyAspect.getName(),
				false));
		// Print out confusion matrix
		this.setEvaluationResults(this.getEvaluationResults() + " \n\n" + eval.toMatrixString("\n==== Overall Confusion Matrix ====\n"));
		// Print out advanced metrics
		this.setEvaluationResults(this.getEvaluationResults() + " \n\n" + eval.toClassDetailsString());
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

/**
* Returns the predicted class and probability and saves it in a "Prediction" instance for each unknown dataset.
*
* @param privacyAspect PrivacyAspect
*            the PrivacyAspect to which the unknown instance refers
* @param filteredData Instances
* 			  All datasets that have been prepared for classification by the StringToWordVector (Preprocessor)
* @param rawData Instances
* 			  All datasets in their primitive state (needed to connect original paragraph and predicted value)	
* 
*/

public void predictClasses(PrivacyAspect privacyAspect, Instances filteredData, Instances rawData) {

	// Predict classes
	try {
		for (int i = 0; i < filteredData.numInstances(); i++) {

			// Filter out the unknown datasets
			if (String.valueOf(filteredData.instance(i).value(0)) == "NaN") {

				// Make prediction
				double predictionValue;
				double[] distributionForInstance;
				predictionValue = getClassifier().classifyInstance(filteredData.instance(i));
				distributionForInstance = getClassifier().distributionForInstance(filteredData.instance(i));
				
				
				// Save prediction and data in a new Prediction Instance
				if (!(filteredData.classAttribute().value((int) predictionValue).contains("R"))){
					Prediction pred = new Prediction(privacyAspect, rawData.get(i).stringValue(0),
							filteredData.classAttribute().value((int) predictionValue), distributionForInstance[(int) predictionValue]);
					// Save to returning List
					predictions.add(pred);
				}

			}

		}
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	// In case of an empty prediction list, set default entry
	if (getPredictions().isEmpty()){
		// In case of data aggregation default is A
		if(getPrivacyAspect().getName().contains("data aggregation")){
		Prediction defaultPrediction = new Prediction(privacyAspect, "Not available", "A", 1.0);
		predictions.add(defaultPrediction);
		// In case of all other aspects default is C
		}else {
		Prediction defaultPrediction = new Prediction(privacyAspect, "Not available", "C", 1.0);
		predictions.add(defaultPrediction);
		}
	}
}

public String getTypeOfClassifier() {
	return typeOfClassifier;
}

public ArrayList<Prediction> getPredictions() {
	return predictions;
}

public Instances getTrainingSet() {
	return trainingSet;
}

public void setTrainingSet(Instances trainingSet) {
	this.trainingSet = trainingSet;
}

public Instances getTestingSet() {
	return testingSet;
}

public void setTestingSet(Instances testingSet) {
	this.testingSet = testingSet;
}

public AbstractClassifier getClassifier() {
	return classifier;
}

public void setClassifier(AbstractClassifier classifier) {
	this.classifier = classifier;
}

public Instances getRawData() {
	return rawData;
}

public void setRawData(Instances rawData) {
	this.rawData = rawData;
}

public PrivacyAspect getPrivacyAspect() {
	return privacyAspect;
}

public void setPrivacyAspect(PrivacyAspect privacyAspect) {
	this.privacyAspect = privacyAspect;
}

public void setTypeOfClassifier(String typeOfClassifier) {
	this.typeOfClassifier = typeOfClassifier;
}

public void setPredictions(ArrayList<Prediction> predictions) {
	this.predictions = predictions;
}

public String getEvaluationResults() {
	return evaluationResults;
}

public void setEvaluationResults(String evaluationResults) {
	this.evaluationResults = evaluationResults;
}

}
