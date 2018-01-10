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
import weka.core.stemmers.SnowballStemmer;
import weka.core.stopwords.Rainbow;
import weka.core.tokenizers.NGramTokenizer;
import weka.core.tokenizers.WordTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

/**
 * Preprocessor. This class is meant for the preprocessing of the available data
 * corpus. The avilable data can be sanitized, stemmed, lowercased, tokenized and rated by
 * their importance of words (TF-IDF).
 * 
 * @author Peter Hofmann (peter_hofmann91@gmx.de)
 * @version $Revision: 1 $
 */

public class Preprocessor extends Thread {

	private StringToWordVector stWV = new StringToWordVector();
	private Instances filteredData;
	private Instances rawData;

	public Preprocessor(Instances loadedData, String[] methods) {
		setRawData(loadedData);
		if (!(methods.length == 0)) {
			for (String s : methods) {
				switch (s) {
					case "stopwords": {
						sanitize();
					}
						break;
					case "stemming": {
						stem();
					}
						break;
					case "tfidf": {
						applyTFIDF();
					}
						break;
					case "ngram": {
						tokenizeNGram();
					}
				}
			}
		}
	}
	public void run() {
		filter(getRawData());
	}

	/**
	 * Deletes stopwords based on the Rainbow Stopwordslist For more information
	 * please see: http://www.cs.cmu.edu/~mccallum/bow/rainbow/
	 * 
	 */
	
	public void sanitize() {
		Rainbow stopWordHandler = new Rainbow();
		getStWV().setStopwordsHandler(stopWordHandler);
	}


	/**
	 * Stems attributes based on the SnowballStemmer. It is based on the
	 * PorterStemmer and supports more then 10 different languages. For more
	 * information please see: http://snowballstem.org/
	 * 
	 */
	
	public void stem() {
		SnowballStemmer snowStem = new SnowballStemmer();
		getStWV().setStemmer(snowStem);
	}


	/**
	 * Splits a string into an n-gram and rebuilds combinations of attributes
	 * based on the min & max attribute. For more information please see:
	 * http://weka.sourceforge.net/doc.dev/weka/core/tokenizers/NGramTokenizer.html
	 * 
	 */
	
	public void tokenizeNGram() {
		// Min number of tokens
		Integer min = 1;
		// Max number of tokens
		Integer max = 4;
		// Delimiting signs
		String delimiters = "\\r\\t\\n.,;:\'\" ";
		
		NGramTokenizer ngTokenizer = new NGramTokenizer();
		ngTokenizer.setNGramMaxSize(max);
		ngTokenizer.setNGramMinSize(min);
		ngTokenizer.setDelimiters(delimiters);
		getStWV().setTokenizer(ngTokenizer);
	}

	/**
	 * Default tokenizer that splits up the words based on the list of
	 * delimiters.
	 * 
	 */
	
	public void tokenizeWord(String delimiters) {
		WordTokenizer wordTokenizer = new WordTokenizer();
		wordTokenizer.setDelimiters(delimiters);
		getStWV().setTokenizer(wordTokenizer);
	}
	
	/**
	 * Applies tf-idf which replaces the mere occurrence counting with the
	 * tf-idf (term frequency - inverse document frequency) metric.
	 */

	public void applyTFIDF() {
		// Enable term frequency
		getStWV().setTFTransform(true);
		// Enable inverse document frequency
		getStWV().setIDFTransform(true);
		// Enable word count
		getStWV().setOutputWordCounts(true);
	}
	
	/**
	 * Applies the StringToWordVector with all previously set preprocessing
	 * methods
	 * 
	 * @param rawData
	 *            the data of type Instances that should be preprocessed
	 * 
	 */

	public void filter(Instances rawData) {
			try {
			// Set format
			getStWV().setInputFormat(rawData);
			// Apply filter
			setFilteredData(Filter.useFilter(rawData, stWV));
			getFilteredData().setClassIndex(0);
		} catch (Exception e) {
			System.out.println("Filtering failed");
			e.printStackTrace();
		}

	}

	public Instances getFilteredData() {
		return filteredData;
	}

	public Instances getRawData() {
		return rawData;
	}

	public void setRawData(Instances rawData) {
		this.rawData = rawData;
	}

	public void setFilteredData(Instances filteredData) {
		this.filteredData = filteredData;
	}

	public StringToWordVector getStWV() {
		return stWV;
	}

	public void setStWV(StringToWordVector stWV) {
		this.stWV = stWV;
	}
}
