package model;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/*
 * TODO: Proper job checker
 * TODO: Some job definition examples
 */

public class Config {
	
	private String workingDir, setName, indexPath, pagePath, modelPath, inferPath;
	private int numTopics, numThreads=2, numIters=1000, jobType=0;
	private boolean loadIndex, writeIndex, onlyIndex=false, usePaging=false, saveModel=false, loadModel=false, saveInfer=false, loadInfer=false;
	
	public Config (File configFile) {
		readConfig(configFile);
	}
	
    private String getTimeStamp() {
    	String day,month,year;
    	
    	Calendar start = new GregorianCalendar();
    	
    	day = Integer.toString(start.get(Calendar.DAY_OF_MONTH));
    	month = Integer.toString(start.get(Calendar.MONTH)+1);
    	year = Integer.toString(start.get(Calendar.YEAR));
    	
    	if (month.length() < 2) {
    		month = "0" + month;
    	}
    	
    	if (day.length() < 2) {
    		day = "0" + day;
    	}
    	
    	return  year.substring(2) + month + day;
    }
	
	private void readConfig(File configFile) {
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(configFile),Charset.forName("UTF-8")));
			ArrayList<String> options = new ArrayList<String>();
			String line;
			
			while((line = input.readLine()) != null) {
				options.add(line);
			}
			
			for(int i=0;i<options.size();i++) {
				if (options.get(i).startsWith("#")) {
					continue;
				} else if (options.get(i).trim().isEmpty()) {
					continue;
				}
				
				String[] parts = options.get(i).trim().split("=");
				
				if (parts[0].toLowerCase().equals("working")) {
					workingDir = parts[1];
				}
				else if (parts[0].toLowerCase().equals("topics")) {
					numTopics = Integer.parseInt(parts[1]);
				}
				else if (parts[0].toLowerCase().equals("setname")) {
					setName = parts[1].toLowerCase() + "-" + getTimeStamp();
				}
				else if (parts[0].toLowerCase().equals("writeindex")) {
					indexPath = parts[1];
					writeIndex = true;
				}
				else if (parts[0].toLowerCase().equals("loadindex")) {
					indexPath = parts[1];
					loadIndex = true;
				}
				else if (parts[0].toLowerCase().equals("onlyindex")) {
					if (parts[1].toLowerCase().equals("true")) {
						onlyIndex = true;
					}
				}
				else if (parts[0].toLowerCase().equals("pagepath")) {
					pagePath = parts[1];
					usePaging = true;
				}
				else if (parts[0].toLowerCase().equals("threads")) {
					numThreads = Integer.parseInt(parts[1]);
				}
				else if (parts[0].toLowerCase().equals("iterations")) {
					numIters = Integer.parseInt(parts[1]);
				}
				else if (parts[0].toLowerCase().equals("savemodel")) {
					saveModel = true;
					modelPath = parts[1];
				}
				else if (parts[0].toLowerCase().equals("loadmodel")) {
					loadModel = true;
					modelPath = parts[1];
				}
				else if (parts[0].toLowerCase().equals("jobtype")) {
					if (parts[1].toLowerCase().equals("1")) {
						jobType=1;
					}
				}
				else if (parts[0].toLowerCase().equals("saveinfer")) {
					saveInfer = true;
					inferPath = parts[1];
				}
				else if (parts[0].toLowerCase().equals("loadinfer")) {
					loadInfer = true;
					inferPath = parts[1];
				}

			}
			
			input.close();
		} catch (Exception e) {
			System.out.println("Error: Config file " + configFile.getName() + " does not exist or cannot be read.");
		} 
		
	}
	
	public boolean checkConfig () {
		if ((workingDir == null) && (indexPath == null)) {
			return false;
		}
		else if ((saveModel == true) && (loadModel == true)) {
			return false;
		}
		else if ((numTopics > 0) && (setName != null)) {
			return true;
		}
		else if (onlyIndex) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void printJobReport () {
		if(loadIndex) {
			System.out.println("Load index from " + indexPath);
		}
		else if (workingDir != null){
			System.out.println("Build index from " + workingDir);
		}
		else {
			System.out.println("Bad job definition: no data set specified");
		}
		
		if (onlyIndex){
			System.out.println("Stop after indexing.");
		}
	}
	
	public String getWorkingDir () {
		return workingDir;
	}
	
	public int getNumTopics() {
		return numTopics;
	}
	
	public String getSetName() {
		return setName;
	}
	
	public boolean getOnlyIndex() {
		return onlyIndex;
	}
	
	public boolean getLoadIndex() {
		return loadIndex;
	}
	
	public boolean getWriteIndex() {
		return writeIndex;
	}
	
	public String getIndexPath() {
		return indexPath;
	}
	
	public String getPagePath() {
		return pagePath;
	}
	
	public boolean getUsePaging() {
		return usePaging;
	}
	
	public int getThreads() {
		return numThreads;
	}
	
	public int getIters() {
		return numIters;
	}
	
	public boolean getSaveModel() {
		return saveModel;
	}
	
	public String getModelPath() {
		return modelPath;
	}
	
	public boolean getLoadModel() {
		return loadModel;
	}
	
	public int getJobType() {
		return jobType;
	}
	
	public boolean getSaveInfer() {
		return saveInfer;
	}
	
	public boolean getLoadInfer() {
		return loadInfer;
	}
	
	public String getInferPath() {
		return inferPath;
	}

}
