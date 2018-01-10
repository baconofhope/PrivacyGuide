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

/**
 * Corpus. This class is needed as a global management class.
 * It contains and initializes all privacy aspects, loads the related data sets bei using
 * the data loader class, and manages the classification and filtering of the final results.  
 * 
 * @author Peter Hofmann (peter_hofmann91@gmx.de)
 * @version $Revision: 1.1 $
 */

public class Corpus {

	private String location;
	private ArrayList<PrivacyAspect> privacyAspects = new ArrayList<PrivacyAspect>();
	private ArrayList<ArrayList<Prediction>> predictions = new ArrayList<ArrayList<Prediction>>();
	private ArrayList<Prediction> multilabelPredictions = new ArrayList<Prediction>();
	private PrivacyAspect allAspects;
	private ArrayList<Prediction> finalPredictions = new ArrayList<Prediction>();
	private String detailedResults;

	public Corpus() {
		
		
	//// MultiLabel aspect "allAspects"
		// Multi Aspects (for privacy policy segmentation)	
		allAspects = new PrivacyAspect("multiAspects", "training-data/0_all_aspects", "", new String[] {""});
		
		// Individual Classifier
		allAspects.setClassifier("NaiveBayes");
		
		// Individual Preprocessing
		allAspects.setPreprocessing(new String[] {"stemming", "ngram"});
		
	//// Data Collection
		privacyAspects.add(new PrivacyAspect("data collection", "training-data/1_data_collection",
					"What type of data is collected by the company?", new String[] { "IP", "personal", "health", "sensi", "devic", "name", "location"}));

		// Individual Classifier
		privacyAspects.get(privacyAspects.size()-1).setClassifier("NaiveBayes");
		
		// Individual Preprocessing //stopwords
		privacyAspects.get(privacyAspects.size()-1).setPreprocessing(new String[] {""});

		// Individual Risk Classes
		ArrayList<RiskClass> riskClassesDataCollection = new ArrayList<RiskClass>();
		riskClassesDataCollection
				.add(new RiskClass("0", 0, "No risk class assigned", this.getClass().getResource("/data_collection.png").toString()));
	
		riskClassesDataCollection.add(
				new RiskClass("A", 1, "Collection of nonpersonal information", this.getClass().getResource("/data_collection_A.png").toString()));
		riskClassesDataCollection.add(
				new RiskClass("B", 2, "Collection of personal information", this.getClass().getResource("/data_collection_B.png").toString()));
		riskClassesDataCollection.add(
				new RiskClass("C", 3, "Collection of sensitive information", this.getClass().getResource("/data_collection_C.png").toString()));
		privacyAspects.get(privacyAspects.size() - 1).setRiskClasses(riskClassesDataCollection);

	//// Protection of Children
		privacyAspects
				.add(new PrivacyAspect("protection of children", "training-data/2_protection_of_children",
						"Does the company knowingly collect data of children?",
						new String[] { "child", "age", "18", "16", "13", "guard", "parent" }));
		
		// Individual Classifier
		privacyAspects.get(privacyAspects.size()-1).setClassifier("SVM");
		
		// Individual Preprocessing // stopwords
		privacyAspects.get(privacyAspects.size()-1).setPreprocessing(new String[] {""});

		// Individual Risk Classes
		ArrayList<RiskClass> riskClassesProtectionOfChildren = new ArrayList<RiskClass>();
		riskClassesProtectionOfChildren
				.add(new RiskClass("0", 0, "No risk class assigned", this.getClass().getResource("/protection_of_children.png").toString()));
		riskClassesProtectionOfChildren.add(new RiskClass("A", 3, "Not knowingly collecting information of children",
				this.getClass().getResource("/protection_of_children_A.png").toString()));
		riskClassesProtectionOfChildren
				.add(new RiskClass("B", 2, "Not available", this.getClass().getResource("/protection_of_children_B.png").toString()));
		riskClassesProtectionOfChildren
				.add(new RiskClass("C", 1, "Not mentioned", this.getClass().getResource("/protection_of_children_C.png").toString()));
		privacyAspects.get(privacyAspects.size() - 1).setRiskClasses(riskClassesProtectionOfChildren);

	//// Third-Party Sharing
		privacyAspects.add(new PrivacyAspect("third-party sharing", "training-data/3_third_party_sharing",
				"Does the company disclose the data to third parties?", new String[] { "share", "sell", "third", "partner", "provide" }));
		
		// Individual Classifier
		privacyAspects.get(privacyAspects.size()-1).setClassifier("SVM");
		
		// Individual Preprocessing //tfidf
		privacyAspects.get(privacyAspects.size()-1).setPreprocessing(new String[] {""});

		// Individual Risk Classes
		ArrayList<RiskClass> riskClassesThirdPartySharing = new ArrayList<RiskClass>();
		riskClassesThirdPartySharing
				.add(new RiskClass("0", 0, "No risk class assigned", this.getClass().getResource("/third_party_sharing.png").toString()));
		riskClassesThirdPartySharing
				.add(new RiskClass("A", 1, "No third-party sharing", this.getClass().getResource("/third_party_sharing_A.png").toString()));
		riskClassesThirdPartySharing.add(new RiskClass("B", 2, "Third-party sharing only for the intended purpose",
				this.getClass().getResource("/third_party_sharing_B.png").toString()));
		riskClassesThirdPartySharing.add(new RiskClass("C", 3, "Third-party sharing with no further explanation",
				this.getClass().getResource("/third_party_sharing_C.png").toString()));
		privacyAspects.get(privacyAspects.size() - 1).setRiskClasses(riskClassesThirdPartySharing);

	//// Data security
		privacyAspects.add(new PrivacyAspect("data security", "training-data/4_data_security",
				"Does the company mention any kind of safeguarding mechanisms?",
				new String[] { "secur", "measure", "administrat", "safe", "tech", "SSL", "proce" }));
		
		// Individual Classifier
		privacyAspects.get(privacyAspects.size()-1).setClassifier("SVM");
		
		// Individual Preprocessing //tfidf
		privacyAspects.get(privacyAspects.size()-1).setPreprocessing(new String[] {""});

		// Individual Risk Classes
		ArrayList<RiskClass> riskClassesDataSecurity = new ArrayList<RiskClass>();
		riskClassesDataSecurity
				.add(new RiskClass("0", 0, "No risk class assigned", this.getClass().getResource("/data_security.png").toString()));
		riskClassesDataSecurity
				.add(new RiskClass("A", 3, "Security measures mentioned", this.getClass().getResource("/data_security_A.png").toString()));
		riskClassesDataSecurity.add(new RiskClass("B", 2, "Not available", this.getClass().getResource("/data_security_B.png").toString()));
		riskClassesDataSecurity
				.add(new RiskClass("C", 1, "No security measures mentioned", this.getClass().getResource("/data_security_C.png").toString()));
		privacyAspects.get(privacyAspects.size() - 1).setRiskClasses(riskClassesDataSecurity);

	//// Data retention
		privacyAspects.add(new PrivacyAspect("data retention", "training-data/5_data_retention",
				"How long does the company store the collected data?", new String[] { "retent", "stor", "durat", "long", "period" }));
		
		// Individual Classifier
		privacyAspects.get(privacyAspects.size()-1).setClassifier("NaiveBayes");
		
		// Individual Preprocessing // tfidf
		privacyAspects.get(privacyAspects.size()-1).setPreprocessing(new String[] {""});

		// Individual Risk Classes
		ArrayList<RiskClass> riskClassesDataRetention = new ArrayList<RiskClass>();
		riskClassesDataRetention
				.add(new RiskClass("0", 0, "No risk class assigned", this.getClass().getResource("/data_retention.png").toString()));
		riskClassesDataRetention.add(new RiskClass("A", 3, "Data storing limited to a specific period of time",
				this.getClass().getResource("/data_retention_A.png").toString()));
		riskClassesDataRetention
				.add(new RiskClass("B", 2, "Data is kept as long as it is neccessary for the intended purpose",
						this.getClass().getResource("/data_retention_B.png").toString()));
		riskClassesDataRetention
				.add(new RiskClass("C", 1, "No expiration date mentioned", this.getClass().getResource("/data_retention_C.png").toString()));
		privacyAspects.get(privacyAspects.size() - 1).setRiskClasses(riskClassesDataRetention);

	//// Data aggregation
		privacyAspects.add(new PrivacyAspect("data aggregation", "training-data/6_data_aggregation",
				"Does the company aggregate the collected information?", new String[] { "aggregat", "combin", "source", "multi", "similar", "other"  }));
		
		// Individual Classifier
		privacyAspects.get(privacyAspects.size()-1).setClassifier("SVM");
		
		// Individual Preprocessing //stopwords
		privacyAspects.get(privacyAspects.size()-1).setPreprocessing(new String[] {""});

		// Individual Risk Classes
		ArrayList<RiskClass> riskClassesDataAggregation = new ArrayList<RiskClass>();
		riskClassesDataAggregation
				.add(new RiskClass("0", 0, "No risk class assigned", this.getClass().getResource("/data_aggregation.png").toString()));
		riskClassesDataAggregation.add(new RiskClass("A", 1, "No data aggregation practices mentioned",
				this.getClass().getResource("/data_aggregation_A.png").toString()));
		riskClassesDataAggregation.add(new RiskClass("B", 2, "Data aggregation only for the intended purpose",
				this.getClass().getResource("/data_aggregation_B.png").toString()));
		riskClassesDataAggregation.add(new RiskClass("C", 3, "Sharing of aggregated information with third-parties",
				this.getClass().getResource("/data_aggregation_C.png").toString()));
		privacyAspects.get(privacyAspects.size() - 1).setRiskClasses(riskClassesDataAggregation);

	//// Control of data
		privacyAspects.add(new PrivacyAspect("control of data", "training-data/7_control_of_data",
				"Does the company offer the possibility to review personal information?",
				new String[] { "access", "update", "review", "delete", "control", "add", "corr" }));
		
		// Individual Classifier
		privacyAspects.get(privacyAspects.size()-1).setClassifier("NaiveBayes");
		
		// Individual Preprocessing // tfidf
		privacyAspects.get(privacyAspects.size()-1).setPreprocessing(new String[] {""});

		// Individual Risk Classes
		ArrayList<RiskClass> riskClassesControlOfData = new ArrayList<RiskClass>();
		riskClassesControlOfData
				.add(new RiskClass("0", 0, "No risk class assigned", this.getClass().getResource("/control_of_data.png").toString()));
		riskClassesControlOfData.add(new RiskClass("A", 3, "Full control of personal data (review, edit and deletion)",
				this.getClass().getResource("/control_of_data_A.png").toString()));
		riskClassesControlOfData
				.add(new RiskClass("B", 2, "Limited access and control of personal data (review and editing)",
						this.getClass().getResource("/control_of_data_B.png").toString()));
		riskClassesControlOfData
				.add(new RiskClass("C", 1, "Collected data can not be reviewed, edited or deleted by the user.",
						this.getClass().getResource("/control_of_data_C.png").toString()));
		privacyAspects.get(privacyAspects.size() - 1).setRiskClasses(riskClassesControlOfData);

	//// Privacy Settings
		privacyAspects.add(new PrivacyAspect("privacy settings", "training-data/8_privacy_settings",
				"Is it possible to choose which privacy related practices will be applied?",
				new String[] { "opt-in", "opt-out", "choice", "select", "prefer", "object", "choose" }));
		
		// Individual Classifier
		privacyAspects.get(privacyAspects.size()-1).setClassifier("NaiveBayes");
		
		// Individual Preprocessing // tfidf
		privacyAspects.get(privacyAspects.size()-1).setPreprocessing(new String[] {""});

		// Individual Risk Classes
		ArrayList<RiskClass> riskClassesPrivacySettings = new ArrayList<RiskClass>();
		riskClassesPrivacySettings
				.add(new RiskClass("0", 0, "No risk class assigned", this.getClass().getResource("/privacy_settings.png").toString()));
		riskClassesPrivacySettings
				.add(new RiskClass("A", 3, "User has the option to opt-in for privacy related practices",
						this.getClass().getResource("/privacy_settings_A.png").toString()));
		riskClassesPrivacySettings
				.add(new RiskClass("B", 2, "User has the option to opt-out for privacy related practices",
						this.getClass().getResource("/privacy_settings_B.png").toString()));
		riskClassesPrivacySettings
				.add(new RiskClass("C", 1, "User has no choice to opt-in/opt-out from privacy related practices",
						this.getClass().getResource("/privacy_settings_C.png").toString()));
		privacyAspects.get(privacyAspects.size() - 1).setRiskClasses(riskClassesPrivacySettings);

	//// Account deletion
		privacyAspects.add(new PrivacyAspect("account deletion", "training-data/9_account_deletion",
				"Is it possible to delete an account?", new String[] { "account", "withdraw"}));
		
		// Individual Classifier
		privacyAspects.get(privacyAspects.size()-1).setClassifier("SVM");
		
		// Individual Preprocessing //tfidf
		privacyAspects.get(privacyAspects.size()-1).setPreprocessing(new String[] {""});

		// Individual Risk Classes
		ArrayList<RiskClass> riskClassesAccountDeletion = new ArrayList<RiskClass>();
		riskClassesAccountDeletion
				.add(new RiskClass("0", 0, "No risk class assigned", this.getClass().getResource("/account_deletion.png").toString()));
		riskClassesAccountDeletion.add(new RiskClass("A", 3, "Full deletion (no remaining data) possible",
				this.getClass().getResource("/account_deletion_A.png").toString()));
		riskClassesAccountDeletion.add(new RiskClass("B", 2, "Partial deletion (some data will remain)",
				this.getClass().getResource("/account_deletion_B.png").toString()));
		riskClassesAccountDeletion
				.add(new RiskClass("C", 1, "No account deletion possible", this.getClass().getResource("/account_deletion_C.png").toString()));
		privacyAspects.get(privacyAspects.size() - 1).setRiskClasses(riskClassesAccountDeletion);

		// // Data breach notification
		// privacyAspects.add(new PrivacyAspect("data breach notification",
		// dir + "\\training-data\\10_data_breach_notification", "Does the
		// company inform their
		// customers in case of a data breach?", new String[] { "breach,
		// secur, 72h, noti" }));

	//// Policy changes 
		privacyAspects.add(new PrivacyAspect("policy changes", "training-data/11_policy_changes",
				"Does the company inform their customers in case of a policy change?",
				new String[] { "change", "noti" ,"policy", "mail" }));
		
		// Indiviual Classifier
		privacyAspects.get(privacyAspects.size()-1).setClassifier("SVM");
		
		// Individual Preprocessing //stemming
		privacyAspects.get(privacyAspects.size()-1).setPreprocessing(new String[] {""});

		// Individual Risk Classes
		ArrayList<RiskClass> riskClassesPolicyChanges = new ArrayList<RiskClass>();
		riskClassesPolicyChanges
				.add(new RiskClass("0", 0, "No risk class assigned", this.getClass().getResource("/policy_changes.png").toString()));
		riskClassesPolicyChanges.add(new RiskClass("A", 3, "Individual notification in case of policy changes",
				this.getClass().getResource("/policy_changes_A.png").toString()));
		riskClassesPolicyChanges.add(new RiskClass("B", 2, "Changes are only visible within the policy",
				this.getClass().getResource("/policy_changes_B.png").toString()));
		riskClassesPolicyChanges
				.add(new RiskClass("C", 1, "Changes are not mentioned", this.getClass().getResource("/policy_changes_C.png").toString()));
		privacyAspects.get(privacyAspects.size() - 1).setRiskClasses(riskClassesPolicyChanges);
		
		// Load data as part of the initialization process
		initializeData();
	}
	
	
	/**
	 * Controls the data initialization by starting parallel threads that load
	 * the training data sets for all 11 privacy aspects.
	 *
	 */

	public void initializeData() {
		
		ArrayList<DataLoader> runningLoaders = new ArrayList<DataLoader>();

		// Load data of privacy aspects
		for (PrivacyAspect pA : this.getPrivacyAspects()) {
			DataLoader dLoader = new DataLoader(pA.getLocation());
			dLoader.start();
			runningLoaders.add(dLoader);
		}
		
		// Loader for multilabel approach
		DataLoader multilabeldLoader = new DataLoader(this.getAllAspects().getLocation());
		multilabeldLoader.start();
		runningLoaders.add(multilabeldLoader);

		// Wait for all threads to finish
		for (DataLoader d : runningLoaders) {
			try {
				d.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Save loaded data to privacy Aspects
		for (int i = 0; i < (this.getPrivacyAspects().size() + 1); i++) {
			if (i == 10) {
				this.getAllAspects().setRawData(runningLoaders.get(i).getLoadedData());
			}
			else {
			this.getPrivacyAspects().get(i).setRawData(runningLoaders.get(i).getLoadedData());
			}
		}
		
	}
	
	/**
	 * Prepares the data for the classification by applying parallel StringToWordVector filters
	 * on all loaded data sets. 
	 *
	 */

	public void filterData() {

		ArrayList<Preprocessor> runningPreprocessors = new ArrayList<Preprocessor>();

		// Apply filter
		for (PrivacyAspect pA : getPrivacyAspects()) {
			Preprocessor prePro = new Preprocessor(pA.getRawData(), pA.getPreprocessing());
			prePro.start();
			runningPreprocessors.add(prePro);
		}
		// Wait for all threads to finish
		for (Preprocessor p : runningPreprocessors) {
			try {
				p.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Save data to privacy Aspects
		for (int i = 0; i < getPrivacyAspects().size(); i++) {
			getPrivacyAspects().get(i).setFilteredData(runningPreprocessors.get(i).getFilteredData());
			
		}

	}
	
	/**
	 * Prepares the data for the classification by applying StringToWordVector
	 * filter ((especially designed for the multilabel pre-classification)
	 *
	 */

	public void filterData(PrivacyAspect aspect) {

			Preprocessor prePro = new Preprocessor(aspect.getRawData(), aspect.getPreprocessing());
			prePro.start();
			try {
				prePro.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			aspect.setFilteredData(prePro.getFilteredData());
	}
	
	/**
	 * Initialises the use of parallel classifiers based on the previously prepared, 
	 * filtered, data sets.
	 * 
	 */

	public void buildClassifiers() {

		ArrayList<ArrayList<Prediction>> results = new ArrayList<ArrayList<Prediction>>();
		ArrayList<Classifier> runningClassifiers = new ArrayList<Classifier>();

		// Classify data
		for (PrivacyAspect pA : getPrivacyAspects()) {
			Classifier threadedClassifier = new Classifier(pA.getClassifier(), pA.getFilteredData(),
					pA.getFilteredData(), pA.getRawData(), pA);
			threadedClassifier.start();
			runningClassifiers.add(threadedClassifier);
		}
		// Wait for all threads to finish
		for (Classifier c : runningClassifiers) {
			try {
				c.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for (Classifier classifier : runningClassifiers) {
			results.add(classifier.getPredictions());
			// System.out.println(classifier.getEvaluationResults());
		}
		this.setPredictions(results);
	}
	

	/**
	 * For using a single Classifier (especially created for using the multilabel
	 * classifier)
	 * 
	 */
	
	public void buildMultilabelClassifier(PrivacyAspect aspect, PrivacyAspect[] aspectList) {
		MultilabelClassifier aspectClassifier = new MultilabelClassifier(aspect.getClassifier(), aspect.getFilteredData(),
				aspect.getFilteredData(), aspect.getRawData(), aspectList);
		aspectClassifier.start();
		try {
			aspectClassifier.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	
	/**
	 * Filters the final prediction results by choosing the worst (or in case of data aggregation the best) classification
	 * results that should be shown to the user.
	 */
	
	public void filterFinalResults() throws Exception {
		
		
		String fullResults = "";
		Integer id = 0;
		
		ArrayList<Prediction> finalResults = new ArrayList<Prediction>();
		for (ArrayList<Prediction> alAspect : this.getPredictions()) {
				id = 0;
				// Save first instance for new aspect and overwrite later
				finalResults.add(alAspect.get(0));

				for (Prediction alPrediction : alAspect) {
						fullResults = fullResults + "\n\n" + "Privacy Aspect: " + alAspect.get(0).getReferringAspect().getName() + "\n" + "ID: " + (id++) + " Class: " + alPrediction.getPredictedClass() + ", Prob.: " + alPrediction.getPredictionLikelihood() + "\n" + "Text: " + alPrediction.getRawData(); 

						// Overwrite with worst predictions per aspect
						if (alPrediction.getReferringAspect().getName().equals(finalResults.get(finalResults.size() - 1).getReferringAspect().getName())
							&& alPrediction.getNumericalClass() > finalResults.get(finalResults.size() - 1).getNumericalClass()) {

							// Delete last prediction if risk level is worse
							finalResults.remove(finalResults.size() - 1);
							// Add new prediction with higher risk level
							finalResults.add(alPrediction);
						}
						
						// Replace with prediction instance with higher probability
						if (alPrediction.getReferringAspect().getName().equals(finalResults.get(finalResults.size() - 1).getReferringAspect().getName())
							&& alPrediction.getNumericalClass().equals(finalResults.get(finalResults.size() - 1).getNumericalClass())	
							&& alPrediction.getPredictionLikelihood() > finalResults.get(finalResults.size()-1).getPredictionLikelihood()) {

							// Delete last prediction if probability is higher
							finalResults.remove(finalResults.size() - 1);
							// Add new prediction with higher probability
							finalResults.add(alPrediction);
						}
					} 
		}
		this.setFinalPredictions(finalResults);
		this.setDetailedResults(fullResults);
	}
	
	
	// Getter & Setter Section

	public ArrayList<PrivacyAspect> getPrivacyAspects() {
		return privacyAspects;
	}

	public void setPrivacyAspects(ArrayList<PrivacyAspect> privacyAspects) {
		this.privacyAspects = privacyAspects;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public ArrayList<ArrayList<Prediction>> getPredictions() {
		return predictions;
	}

	public void setPredictions(ArrayList<ArrayList<Prediction>> predictions) {
		this.predictions.clear();
		this.predictions = predictions;
	}

	public ArrayList<Prediction> getFinalPredictions() {
		return finalPredictions;
	}

	public void setFinalPredictions(ArrayList<Prediction> finalPredictions) {
		this.finalPredictions.clear();
		this.finalPredictions = finalPredictions;
	}

	public String getDetailedResults() {
		return detailedResults;
	}

	public void setDetailedResults(String detailedResults) {
		this.detailedResults = detailedResults;
	}


	public ArrayList<Prediction> getMultilabelPredictions() {
		return multilabelPredictions;
	}


	public void setMultilabelPredictions(ArrayList<Prediction> multilabelPredictions) {
		this.multilabelPredictions = multilabelPredictions;
	}


	public PrivacyAspect getAllAspects() {
		return allAspects;
	}


	public void setAllAspects(PrivacyAspect allAspects) {
		this.allAspects = allAspects;
	}
}
