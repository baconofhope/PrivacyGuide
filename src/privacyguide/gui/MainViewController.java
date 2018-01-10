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
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import privacyguide.core.Corpus;
import privacyguide.core.Prediction;
import privacyguide.core.PrivacyAspect;
import privacyguide.core.PrivacyPolicy;

/**
 * MainViewController. This class is needed as main GUI Controller class.
 * It coordinates the main program flow and interacts with the user.
 * 
 * 
 * @author Peter Hofmann (peter_hofmann91@gmx.de)
 * @version $Revision: 1 $
 */

public class MainViewController implements Initializable {
	private Corpus corpus;

	@FXML
	private ImageView ivDataCollection;

	@FXML
	private Tooltip ttDataCollection;

	@FXML
	private Label lblClassDataCollection;

	@FXML
	private ImageView ivProtectionOfChildren;

	@FXML
	private Label lblClassProtectionOfChildren;

	@FXML
	private Tooltip ttProtectionOfChildren;

	@FXML
	private ImageView ivThirdPartySharing;

	@FXML
	private Label lblClassThirdPartySharing;

	@FXML
	private Tooltip ttThirdPartySharing;

	@FXML
	private ImageView ivDataSecurity;

	@FXML
	private Label lblClassDataSecurity;

	@FXML
	private Tooltip ttDataSecurity;

	@FXML
	private ImageView ivDataRetention;

	@FXML
	private Label lblClassDataRetention;

	@FXML
	private Tooltip ttDataRetention;

	@FXML
	private ImageView ivDataAggregation;

	@FXML
	private Label lblClassDataAggregation;

	@FXML
	private Tooltip ttDataAggregation;

	@FXML
	private ImageView ivControlOfData;

	@FXML
	private Label lblClassControlOfData;

	@FXML
	private Tooltip ttControlOfData;

	@FXML
	private ImageView ivPrivacySetting;

	@FXML
	private Label lblClassPrivacySettings;

	@FXML
	private Tooltip ttPrivacySettings;

	@FXML
	private ImageView ivAccountDeletion;

	@FXML
	private Label lblClassAccountDeletion;

	@FXML
	private Tooltip ttAccountDeletion;

	@FXML
	private ImageView ivPolicyChanges;

	@FXML
	private Label lblClassPolicyChanges;

	@FXML
	private Tooltip ttPolicyChanges;
	
    @FXML
    private ImageView ivPrivacyBreach;

    @FXML
    private Label lblClassPrivacyBreach;

    @FXML
    private Tooltip ttPrivacyBreach;

	@FXML
	private Button btnStart;

	@FXML
	private TextField tfURL;

	@FXML
	private ProgressBar progressBar;

	@FXML
	private Label lblProgressTask;

	@FXML
	private Button btnAbout;

	@FXML
	private Label lblAdvice;
	
	
	/**
	 * Initializes the main view, initially creates the corpus and resets all icons
	 * 
	 */

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		corpus = new Corpus();
		updateView(true, null);
		
		// Exception for aspect: "PrivacyBreach"
		ivPrivacyBreach.setImage(new Image(this.getClass().getResource("/privacy_breach.png").toString()));
	}

	
	/**
	 * Mainthread is the main controller class that coordinates the program flow of PrivacyGuide and 
	 * connects GUI with backend processing.
	 * 
	 */

	class Mainthread extends Thread {
		private String url;

		public Mainthread(String policyURL) {
			url = policyURL;
		}

		public void run() {
			// Time measurement
			long startTime = System.nanoTime();
			PrivacyPolicy userPolicy;
			try {
				userPolicy = new PrivacyPolicy(url, corpus.getPrivacyAspects().toArray(new PrivacyAspect[corpus.getPrivacyAspects().size()]));
				
				// Update ProgressBar
				Platform.runLater(() -> {
					updateProgress("Loading Privacy Policy", 0.1);
				});

				userPolicy.loadPrivacyPolicy();
				// Time measurement
				//long policyLoading = System.nanoTime();
				
				// Update ProgressBar
				Platform.runLater(() -> {
					updateProgress("Extract essential paragraphs", 0.3);
				});
				
				// Extract privacy policy
				userPolicy.splitPolicyInParagraphs();
				userPolicy.addSubsets(corpus.getAllAspects());
				
				// Classify sentences and assign privacy aspects
				corpus.filterData(corpus.getAllAspects());
				corpus.buildMultilabelClassifier(corpus.getAllAspects(), corpus.getPrivacyAspects().toArray(new PrivacyAspect[corpus.getPrivacyAspects().size()]));

				// Time measurement
				//long policyPreprocessing = System.nanoTime();
				
				// Update ProgressBar
				Platform.runLater(() -> {
					updateProgress("Prepare classifiers", 0.6);
				});

				corpus.filterData();
				// Time measurement
				//long dataPreprocessing = System.nanoTime();

				// Update ProgressBar
				Platform.runLater(() -> {
					updateProgress("Classify unknown datasets", 0.8);
				});

				corpus.buildClassifiers();
				
				// Time measurement
				//long classification = System.nanoTime();

				// Update ProgressBar
				Platform.runLater(() -> {
					updateProgress("Filter predictions", 0.9);
				});
				

				corpus.filterFinalResults();
				// Time measurement
				//long resultFiltering = System.nanoTime();
				
				// Time measurement
				long endTime = System.nanoTime();

				// Update ProgressBar
				Platform.runLater (() ->  {
					updateProgress("Execution time: " + (endTime - startTime) / 1000000 + " ms", 1.0);
					updateView(false, corpus.getFinalPredictions());
				});
				
				//Print all results to console
				//System.out.println(corpus.getDetailedResults());
				
				//Print time measurements to console
				//System.out.println("Total execution time: " + (endTime - startTime) / 1000000 + " ms \n");
				//System.out.println("Policy loading: " + (policyLoading - startTime) / 1000000 + " ms");
				//System.out.println("Policy preprocessing: " + (policyPreprocessing - policyLoading) / 1000000 + " ms");
				//System.out.println("Data preprocessing: " + (dataPreprocessing - policyPreprocessing) / 1000000 + " ms");
				//System.out.println("Classification: " + (classification - dataPreprocessing) / 1000000 + " ms");
				//System.out.println("Result filtering: " + (resultFiltering - classification) / 1000000 + " ms");
				
			} catch (Exception e) {

				Platform.runLater(() -> {
					FXMLLoader errorLoader = new FXMLLoader(getClass().getResource("Error.fxml"));
					Parent errorRoot;
					try {
						errorRoot = errorLoader.load();
						Scene errorScene = new Scene(errorRoot);
						Stage errorStage = new Stage();
						errorStage.setTitle("Error");
						errorStage.setScene(errorScene);
						errorStage.show();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				});
			}
			// Preload original dataset for the next privacy policy
			corpus.initializeData();

			// Enable Start and DetailedResults Buttons
			Platform.runLater(() -> {
				btnStart.setDisable(false);
				progressBar.setProgress(0.0);
			});
		}
	}
	
	/**
	 * Loads and displays the "About" Window onClick.
	 * 
	 */

	@FXML
	void showAboutText(ActionEvent event) {
		FXMLLoader aboutLoader = new FXMLLoader(getClass().getResource("About.fxml"));
		Parent aboutRoot;
		try {
			aboutRoot = aboutLoader.load();
			Scene aboutScene = new Scene(aboutRoot);

			Stage aboutStage = new Stage();

			aboutStage.setTitle("PrivacyGuide");
			aboutStage.setScene(aboutScene);
			aboutStage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Creates an object of the Mainthread class onClick.
	 * Buttons are disabled and the main GUI is reseted.
	 * 
	 * 
	 */
	
	@FXML
	void startProcessing() {
		btnStart.setDisable(true);
		updateView(true, null);
		Mainthread mT = new Mainthread(tfURL.getText());
		mT.start();
	}
	
	/**
	 * Updates / resets the progress bar.
	 * 
	 * @param text
	 *            String to display in progress label
	 * @param progress
	 *            progress value as Double
	 * 
	 */

	void updateProgress(String text, Double progress) {
		progressBar.setProgress(progress);
		lblProgressTask.setText(text);
	}

	/**
	 * Updates / resets the icons of the eleven different aspects in MainView.
	 * 
	 * @param reset
	 *            Boolean variable to determine if the icons should be updated
	 *            or reseted
	 * @param finalPredictions
	 *            List of predictions to present in MainView
	 * 
	 */
	void updateView(Boolean reset, ArrayList<Prediction> finalPredictions) {
		// In case of new iteration, reset to default
		try {
			if (reset == true) {
				// Reset Icon Data Collection

				Image iconDataCollectionDefault = new Image(
						corpus.getPrivacyAspects().get(0).getRiskClasses().get(0).getIconLocation());
				ivDataCollection.setImage(iconDataCollectionDefault);

				// Reset Class Label Data Collection
				lblClassDataCollection.setText("?");

				// Reset ToolTip Data Collection
				ttDataCollection.setText(corpus.getPrivacyAspects().get(0).getDescription());

				// Reset Icon Protection of Children
				Image iconProtectionOfChildren = new Image(
						corpus.getPrivacyAspects().get(1).getRiskClasses().get(0).getIconLocation());
				ivProtectionOfChildren.setImage(iconProtectionOfChildren);

				// Reset Class Label ProtectionOfChildren
				lblClassProtectionOfChildren.setText("?");

				// Reset ToolTip ProtectionOfChildren
				ttProtectionOfChildren.setText(corpus.getPrivacyAspects().get(1).getDescription());

				// Reset Icon Third-PartySharing
				Image iconThirdPartySharing = new Image(
						corpus.getPrivacyAspects().get(2).getRiskClasses().get(0).getIconLocation());
				ivThirdPartySharing.setImage(iconThirdPartySharing);

				// Reset Class Label ThirdPartySharing
				lblClassThirdPartySharing.setText("?");

				// Reset ToolTip ThirdPartySharing
				ttThirdPartySharing.setText(corpus.getPrivacyAspects().get(2).getDescription());

				// Reset Icon Data Security
				Image iconDataSecurity = new Image(
						corpus.getPrivacyAspects().get(3).getRiskClasses().get(0).getIconLocation());
				ivDataSecurity.setImage(iconDataSecurity);

				// Reset Class Label DataSecurity
				lblClassDataSecurity.setText("?");

				// Reset ToolTip ThirdPartySharing
				ttDataSecurity.setText(corpus.getPrivacyAspects().get(3).getDescription());

				// Reset Icon Data Retention
				Image iconDataRetention = new Image(
						corpus.getPrivacyAspects().get(4).getRiskClasses().get(0).getIconLocation());
				ivDataRetention.setImage(iconDataRetention);

				// Reset Class Label DataRetention
				lblClassDataRetention.setText("?");

				// Reset ToolTip DataRetention
				ttDataRetention.setText(corpus.getPrivacyAspects().get(4).getDescription());

				// Reset Icon Data Aggregation
				Image iconDataAggregation = new Image(
						corpus.getPrivacyAspects().get(5).getRiskClasses().get(0).getIconLocation());
				ivDataAggregation.setImage(iconDataAggregation);

				// Reset Class Label DataAggregation
				lblClassDataAggregation.setText("?");

				// Reset ToolTip DataAggregation
				ttDataAggregation.setText(corpus.getPrivacyAspects().get(5).getDescription());

				// Reset Icon Control of Data
				Image iconControlOfData = new Image(
						corpus.getPrivacyAspects().get(6).getRiskClasses().get(0).getIconLocation());
				ivControlOfData.setImage(iconControlOfData);

				// Reset Class Label ControlOfData
				lblClassControlOfData.setText("?");

				// Reset ToolTip ControlOfData
				ttControlOfData.setText(corpus.getPrivacyAspects().get(6).getDescription());

				// Reset Icon Privacy Settings
				Image iconPrivacySettings = new Image(
						corpus.getPrivacyAspects().get(7).getRiskClasses().get(0).getIconLocation());
				ivPrivacySetting.setImage(iconPrivacySettings);

				// Reset Class Label PrivacySettings
				lblClassPrivacySettings.setText("?");

				// Reset ToolTip PrivacySettings
				ttPrivacySettings.setText(corpus.getPrivacyAspects().get(7).getDescription());

				// Reset Icon Account Deletion
				Image iconAccountDeletion = new Image(
						corpus.getPrivacyAspects().get(8).getRiskClasses().get(0).getIconLocation());
				ivAccountDeletion.setImage(iconAccountDeletion);

				// Reset Class Label AccountDeletion
				lblClassAccountDeletion.setText("?");

				// Reset ToolTip AccountDeletion
				ttAccountDeletion.setText(corpus.getPrivacyAspects().get(8).getDescription());

				// Reset Icon Privacy Breach
				// Image iconPrivacyBreach = new Image
				// (corpus.getPrivacyAspects().get(9).getRiskClasses().get(0).getIconLocation());
				//
				// // Reset Class Label PrivacyBreach
				// lblClassPrivacyBreach.setText("?");
				//
				// // Reset ToolTip PrivacyBreach
				// ttPrivacyBreach.setText(corpus.getPrivacyAspects().get(9).getDescription());

				// Reset Icon Policy Changes
				Image iconPolicyChanges = new Image(
						corpus.getPrivacyAspects().get(9).getRiskClasses().get(0).getIconLocation());
				ivPolicyChanges.setImage(iconPolicyChanges);

				// Reset Class Label PolicyChanges
				lblClassPolicyChanges.setText("?");

				// Reset ToolTip PolicyChanges
				ttPolicyChanges.setText(corpus.getPrivacyAspects().get(9).getDescription());

			} else {
				// If classification has been done

				// Set Icon Data Collection
				Image iconDataCollectionDefault = new Image(finalPredictions.get(0).getIconLocation());
				ivDataCollection.setImage(iconDataCollectionDefault);

				// Set Class Label Data Collection
				lblClassDataCollection.setText(finalPredictions.get(0).getPredictedClass());

				// Set ToolTip Data Collection
				ttDataCollection.setText(finalPredictions.get(0).getToolTipText());

				// Set Icon Protection of Children
				Image iconProtectionOfChildren = new Image(finalPredictions.get(1).getIconLocation());
				ivProtectionOfChildren.setImage(iconProtectionOfChildren);

				// Set Class Label ProtectionOfChildren
				lblClassProtectionOfChildren.setText(finalPredictions.get(1).getPredictedClass());

				// Set ToolTip ProtectionOfChildren
				ttProtectionOfChildren.setText(finalPredictions.get(1).getToolTipText());

				// Set Icon Third-PartySharing
				Image iconThirdPartySharing = new Image(finalPredictions.get(2).getIconLocation());
				ivThirdPartySharing.setImage(iconThirdPartySharing);

				// Set Class Label ThirdPartySharing
				lblClassThirdPartySharing.setText(finalPredictions.get(2).getPredictedClass());

				// Set ToolTip ThirdPartySharing
				ttThirdPartySharing.setText(finalPredictions.get(2).getToolTipText());

				// Set Icon Data Security
				Image iconDataSecurity = new Image(finalPredictions.get(3).getIconLocation());
				ivDataSecurity.setImage(iconDataSecurity);

				// Set Class Label DataSecurity
				lblClassDataSecurity.setText(finalPredictions.get(3).getPredictedClass());

				// Set ToolTip ThirdPartySharing
				ttDataSecurity.setText(finalPredictions.get(3).getToolTipText());

				// Set Icon Data Retention
				Image iconDataRetention = new Image(finalPredictions.get(4).getIconLocation());
				ivDataRetention.setImage(iconDataRetention);

				// Set Class Label DataRetention
				lblClassDataRetention.setText(finalPredictions.get(4).getPredictedClass());

				// Set ToolTip DataRetention
				ttDataRetention.setText(finalPredictions.get(4).getToolTipText());

				// Set Icon Data Aggregation
				Image iconDataAggregation = new Image(finalPredictions.get(5).getIconLocation());
				ivDataAggregation.setImage(iconDataAggregation);

				// Set Class Label DataAggregation
				lblClassDataAggregation.setText(finalPredictions.get(5).getPredictedClass());

				// Set ToolTip DataAggregation
				ttDataAggregation.setText(finalPredictions.get(5).getToolTipText());

				// Set Icon Control of Data
				Image iconControlOfData = new Image(finalPredictions.get(6).getIconLocation());
				ivControlOfData.setImage(iconControlOfData);

				// Set Class Label ControlOfData
				lblClassControlOfData.setText(finalPredictions.get(6).getPredictedClass());

				// Set ToolTip ControlOfData
				ttControlOfData.setText(finalPredictions.get(6).getToolTipText());

				// Set Icon Privacy Settings
				Image iconPrivacySettings = new Image(finalPredictions.get(7).getIconLocation());
				ivPrivacySetting.setImage(iconPrivacySettings);

				// Set Class Label PrivacySettings
				lblClassPrivacySettings.setText(finalPredictions.get(7).getPredictedClass());

				// Set ToolTip PrivacySettings
				ttPrivacySettings.setText(finalPredictions.get(7).getToolTipText());

				// Set Icon Account Deletion
				Image iconAccountDeletion = new Image(finalPredictions.get(8).getIconLocation());
				ivAccountDeletion.setImage(iconAccountDeletion);

				// Set Class Label AccountDeletion
				lblClassAccountDeletion.setText(finalPredictions.get(8).getPredictedClass());

				// Set ToolTip AccountDeletion
				ttAccountDeletion.setText(finalPredictions.get(8).getToolTipText());

				// Set Icon Policy Changes
				Image iconPolicyChanges = new Image(finalPredictions.get(9).getIconLocation());
				ivPolicyChanges.setImage(iconPolicyChanges);

				// Set Class Label PolicyChanges
				lblClassPolicyChanges.setText(finalPredictions.get(9).getPredictedClass());

				// Set ToolTip PolicyChanges
				ttPolicyChanges.setText(finalPredictions.get(9).getToolTipText());

				// // Set Icon Privacy Breach
				// Image iconPrivacyBreach = new
				// Image(finalPredictions.get(10).getIconLocation());
				// ivPrivacyBreach.setImage(iconPrivacyBreach);
				//
				// // Set Class Label PrivacyBreach
				// lblClassPrivacyBreach.setText(finalPredictions.get(10).getPredictedClass());
				//
				// // Set ToolTip PrivacyBreach
				// ttPrivacyBreach.setText(finalPredictions.get(9).getToolTipText());
			}
		} catch (IndexOutOfBoundsException e) {
			FXMLLoader errorLoader = new FXMLLoader(getClass().getResource("Error.fxml"));
			Parent errorRoot;
			try {
				errorRoot = errorLoader.load();
				Scene errorScene = new Scene(errorRoot);
				Stage errorStage = new Stage();
				errorStage.setTitle("Error");
				errorStage.setScene(errorScene);
				errorStage.show();
				updateView(true, null);
			} catch (IOException e1) {
			}
		}
	}
}