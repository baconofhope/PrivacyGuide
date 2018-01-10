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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.evaluation.ThresholdCurve;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

/**
 * MultilabelClassifer. This class is a convenience class to handle multiple
 * types of classification algorithms at once. It is a modified version from the
 * original "Classifier" class which handles classification tasks which only
 * assign one class to a candidate sentence
 * 
 * 
 * @author Peter Hofmann (peter_hofmann91@gmx.de)
 * @version $Revision: 1 $
 */

public class MultilabelClassifier extends Thread {

	private AbstractClassifier classifier;
	private String typeOfClassifier;
	private Instances trainingSet;
	private Instances testingSet;
	private Instances rawData;
	private ArrayList<PolicySubset> subsets = new ArrayList<PolicySubset>();
	private PrivacyAspect[] privacyAspects;
	private double threshold = 0.01;

	public MultilabelClassifier(String classifierType, Instances training, Instances testing, Instances raw,
			PrivacyAspect[] aspects) {

		// Forward variables

		setTypeOfClassifier(classifierType);
		setTrainingSet(training);
		setTestingSet(testing);
		setRawData(raw);
		setPrivacyAspects(aspects);

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
		predictClasses(getTrainingSet(), getRawData());
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
			System.out.println(eval.toSummaryString(
					"Evaluation results: " + this.getTypeOfClassifier() + " \n" + "\n" + "Aspect: " + this.getName(),
					false));
			// Print out confusion matrix
			System.out.println(eval.toMatrixString("\n==== Overall Confusion Matrix ====\n"));
			// Print out advanced metrics
			System.out.println(eval.toClassDetailsString());
			
			// Construct ROC Curve
			ThresholdCurve tc = new ThresholdCurve();
			for (int i = 0; i < trainingSet.numClasses(); i++) {
				Instances roc = tc.getCurve(eval.predictions(), i);
				ArffSaver saver = new ArffSaver();
				try {
					saver.setInstances(roc);
					saver.setFile(new File("roc-curves-1st-phase/" + trainingSet.classAttribute().value(i)  + ".arff"));
					saver.writeBatch();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the predicted class and probability and saves it in a "Prediction" instance for each unknown dataset. Generates more instances if 
	 * multiple classes are possible. (pass the threshold) 
	 *
	 * @param filteredData Instances
	 * 			  All datasets that have been prepared for classification by the StringToWordVector (Preprocessor)
	 * @param rawData Instances
	 * 			  All datasets in their primitive state (needed to connect original paragraph and predicted value)	
	 * 
	 */

	public void predictClasses(Instances filteredData, Instances rawData) {

		// Predict classes
		try {
			for (int i = 0; i < filteredData.numInstances(); i++) {

				// Filter out the unknown datasets
				if (String.valueOf(filteredData.instance(i).value(0)) == "NaN") {

					// Make prediction
					//double predictionValue;
					double[] distributionForInstance;
					//predictionValue = getClassifier().classifyInstance(filteredData.instance(i));
					distributionForInstance = getClassifier().distributionForInstance(filteredData.instance(i));

					// Filter relevant results and assign to individual privacy aspect
					for (int j = 0; j < distributionForInstance.length; j++) {
						if (Double.compare(distributionForInstance[j], this.getThreshold()) > 0) {
							switch (filteredData.classAttribute().value(j)) {

							case "1_data_collection": {
								PolicySubset sub = new PolicySubset(rawData.get(i).stringValue(0),getPrivacyAspects()[0]);
								subsets.add(sub);
							}
								break;
							case "2_protection_of_children": {
								PolicySubset sub = new PolicySubset(rawData.get(i).stringValue(0),getPrivacyAspects()[1]);
								subsets.add(sub);
							}
								break;
							case "3_third_party_sharing": {
								PolicySubset sub = new PolicySubset(rawData.get(i).stringValue(0),getPrivacyAspects()[2]);
								subsets.add(sub);
							}
								break;
							case "4_data_security": {
								PolicySubset sub = new PolicySubset(rawData.get(i).stringValue(0),getPrivacyAspects()[3]);
								subsets.add(sub);
							}
								break;
							case "5_data_retention": {
								PolicySubset sub = new PolicySubset(rawData.get(i).stringValue(0),getPrivacyAspects()[4]);
								subsets.add(sub);
							}
								break;
							case "6_data_aggregation": {
								PolicySubset sub = new PolicySubset(rawData.get(i).stringValue(0),getPrivacyAspects()[5]);
								subsets.add(sub);
							}
								break;
							case "7_control_of_data": {
								PolicySubset sub = new PolicySubset(rawData.get(i).stringValue(0),getPrivacyAspects()[6]);
								subsets.add(sub);
							}
								break;
							case "8_privacy_settings": {
								PolicySubset sub = new PolicySubset(rawData.get(i).stringValue(0),getPrivacyAspects()[7]);
								subsets.add(sub);
							}
								break;
							case "9_account_deletion": {
								PolicySubset sub = new PolicySubset(rawData.get(i).stringValue(0),getPrivacyAspects()[8]);
								subsets.add(sub);
							}
								break;
							case "11_policy_changes": {
								PolicySubset sub = new PolicySubset(rawData.get(i).stringValue(0),getPrivacyAspects()[9]);
								subsets.add(sub);
							}
							}
						}

					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		for (PolicySubset s: this.getSubsets()) {
//			System.out.println("Privacy aspect: " + s.getReferringAspect().getName() + " --- Raw text: " + s.getRawData());
//		}
	}

	public String getTypeOfClassifier() {
		return typeOfClassifier;
	}

	public ArrayList<PolicySubset> getSubsets() {
		return subsets;
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

	public PrivacyAspect[] getPrivacyAspects() {
		return privacyAspects;
	}

	public void setPrivacyAspects(PrivacyAspect[] aspects) {
		this.privacyAspects = aspects;
	}

	public void setTypeOfClassifier(String typeOfClassifier) {
		this.typeOfClassifier = typeOfClassifier;
	}

	public void setSubsets(ArrayList<PolicySubset> sub) {
		this.subsets = sub;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

}

