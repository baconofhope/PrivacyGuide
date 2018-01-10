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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.KeepEverythingExtractor;

/**
 * PrivacyPolicy. This class is meant for the import, preprocessing and
 * configuration of a privacy policy that is provided by the user. This includes
 * the download and split of a webpage that contains privacy related content.
 * 
 * @author Peter Hofmann (peter_hofmann91@gmx.de)
 * @version $Revision: 1.1 $
 */

public class PrivacyPolicy {

	private String url;
	private ArrayList<PolicySubset> policySubsets = new ArrayList<PolicySubset>();
	private PrivacyAspect[] privacyAspects;
	private String rawData;
	private ArrayList<String> sentences = new ArrayList<String>();


	/** Constructor */
	public PrivacyPolicy(String userUrl, PrivacyAspect[] pA) throws MalformedURLException {
		url = userUrl;
		privacyAspects = pA;
	}

	/**
	 * Extracts plain text from URL using the boilerpipe framework for
	 * additional information see: Kohlschütter, C.; Frankhauser, P.; Nejdl, W.
	 * (2010). Boilerplate Detection using Shallow Text Features Accessed on 10.
	 * April 2017 @
	 * http://www.l3s.de/~kohlschuetter/publications/wsdm187-kohlschuetter.pdf
	 *
	 * @param userURL
	 *            the URL which has been provided by the user
	 */
	
	public void loadPrivacyPolicy() throws MalformedURLException, BoilerpipeProcessingException {
		String data = "";
			URL policyURL = new URL(url);
			data = KeepEverythingExtractor.INSTANCE.getText(policyURL).toLowerCase();
		setRawData(data);
	}

	/**
	 * Splits the extracted plain text into sentences for further processing.
	 * Line breaks and double "." are replaced before splitting.
	 */

	public void splitPolicyInParagraphs() {
		
		String text = this.getRawData();
		// Delete all line breaks
		String withoutEndOfLineBreaks = text.replace(".\n", ". ");
		String withoutAdditionalBreaks = withoutEndOfLineBreaks.replace("\n", " ");
		// Split string in sentences
		String[] splittedText = withoutAdditionalBreaks.split("(?<=[a-z])\\.+\\s+");
		
		// Delete all sentences which are shorter than k chars
		ArrayList<String> finalSentences = new ArrayList<String>();
		Integer k = 50;
		for (String s:splittedText){
			if (s.length() > k){
				finalSentences.add(s);
			}
		}
		this.setSentences(finalSentences);
		}

	/**
	 * Adds the extracted sentences to the dataset of the multilabel Classifier.
	 * Start of classification phase 1 (Multilabel sentence classification)
	 * 
	 */

	public void addSubsets(PrivacyAspect aspect) {
		for (String sentence: this.getSentences()) {
			PolicySubset pS = new PolicySubset(sentence, aspect);
			this.getPolicySubsets().add(pS);
		}
	}
	
//	/**
//	 * Checks the separated sentences for keywords and pre-classifies them
//	 * according to the set of defined privacy aspects. Each candidate sentence 
//	 * is stored in a PolicySubset instance. 
//	 * 
//	 */
//
//	
//	
//	
// --> Old version that checks for keywords
//	public void splitPrivacyAspects() {
//		for (String sentence : this.getSentences()) {
//			for (PrivacyAspect aspect : this.getPrivacyAspects()) {
//				for (String keyword : aspect.getKeyWordList())
//					if (sentence.contains(keyword)) {
//						PolicySubset pS = new PolicySubset(sentence, aspect);
//						this.getPolicySubsets().add(pS);
//						break;
//					}
//			}
//		}
//	}

	private void setRawData(String data) {
		rawData = data;
	}

	public String getRawData() {
		return rawData;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public ArrayList<PolicySubset> getPolicySubsets() {
		return policySubsets;
	}

	public void setPolicySubsets(ArrayList<PolicySubset> policySubsets) {
		this.policySubsets = policySubsets;
	}


	public ArrayList<String> getSentences() {
		return sentences;
	}


	public void setSentences(ArrayList<String> sentences) {
		this.sentences = sentences;
	}


	public PrivacyAspect[] getPrivacyAspects() {
		return privacyAspects;
	}


	public void setPrivacyAspects(PrivacyAspect[] privacyAspects) {
		this.privacyAspects = privacyAspects;
	}
}
