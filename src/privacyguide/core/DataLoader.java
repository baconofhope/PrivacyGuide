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

import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.converters.TextDirectoryLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.Randomize;

/**
 * DataLoader. This class is meant for the loading of necessary datasets from a
 * specific directory. For this purpose the TextDirectoryLoader from WEKA Framework is
 * used. The class is needed for performance optimization by using
 * multithreading.
 * 
 * @author Peter Hofmann (peter_hofmann91@gmx.de)
 * @version $Revision: 1 $
 */

public class DataLoader extends Thread {

	private TextDirectoryLoader tdLoader = new TextDirectoryLoader();
	private Instances loadedData;
	private String location;

	public DataLoader(String loc) {
		location = loc;
	}

	public void run() {
		loadData(location);
	}

	public void loadData(String location) {
		try {
			File testFile = new File("training-data-prepared" + location.substring(location.indexOf("/")) + ".arff");
			if (!testFile.exists()) {
				File testDir = new File(location);
				// Testing file if location is a directory (running in IDE)
				if (testDir.isDirectory()) {
					tdLoader.setDirectory(new File(location));
					// Import as Instances
					loadedData = tdLoader.getDataSet();
					// Randomize to avoid problems with evaluation measures
					Randomize randomizer = new Randomize();
					// Prepare filter for data
					randomizer.setInputFormat(loadedData);
					// Apply randomization filter
					Instances randomizedInstances = Filter.useFilter(loadedData, randomizer);
					// Save in variable
					setLoadedData(randomizedInstances);
					// Save as ARFF
					ArffSaver saver = new ArffSaver();
					try {
						saver.setInstances(randomizedInstances);
						saver.setFile(new File("training-data-prepared" + location.substring(location.indexOf("/")) + ".arff"));
						saver.writeBatch();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					// Need to load from classpath (running in JAR)
					ArffLoader loader = new ArffLoader();
					loader.setFile(new File(this.getClass().getResource(location.substring(location.indexOf("/")) + ".arff").toString()));
					setLoadedData(loader.getDataSet());
				}
			} else {
				// Need to load from directory (running in IDE)
				ArffLoader loader = new ArffLoader();
				loader.setFile(new File("training-data-prepared" + location.substring(location.indexOf("/")) + ".arff"));
				setLoadedData(loader.getDataSet());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			System.out.println(this.getClass().getResource(location.substring(location.indexOf("/")) + ".arff").toString());
			e.printStackTrace();
		}
	}
	
	public Instances getLoadedData(){
		return loadedData;
	}

	public void setLoadedData(Instances loadedData) {
		this.loadedData = loadedData;
	}
}
