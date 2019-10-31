package model;

import cc.mallet.util.*;
import cc.mallet.types.*;
import cc.mallet.topics.*;
import java.util.*;
import java.io.*;
import java.nio.charset.Charset;

public class LDA {
	
	private Config options;
	private Indexer index;
        
    private ParallelTopicModel doModel() {
    	
    	InstanceList instances = index.getIndex();
    	
        // Create model 
        ParallelTopicModel model = new ParallelTopicModel(options.getNumTopics());

        // Add documents to model
        model.addInstances(instances);
        
        // Set model hyper-parameters according to Wallach's recommendations
        model.alphaSum = 50.0 / options.getNumTopics();
        model.beta = 200.0 / model.totalTokens;
        
        // Use two parallel samplers, which each look at one half the corpus and combine
        //  statistics after every iteration.
        model.setNumThreads(options.getThreads());

        // Run the model for X iterations
        model.setNumIterations(options.getIters());
        
        System.out.println("Beginning topic model.");
        try {
			model.estimate();
		} catch (IOException e) {
			System.out.println("Topic model failed to initialize at " + new Date().toString());
		}
    	
    	return model;
    }
    
    private void writeKeys(ParallelTopicModel model, String setName, int numTopics) {
    	String outFile = setName + "-" + Integer.toString(numTopics) + "-keys.txt";
    	
		Alphabet tokenDictionary = model.getAlphabet();
		ArrayList<TreeSet<IDSorter>> results = model.getSortedWords();
    	
    	try {
			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile),"UTF-8"));
			
			for(int i=0;i<results.size();i++) {
				if (i>0){
					output.write("\n");
				}
				output.write("topic" + Integer.toString(i) + "\n");
				Iterator<IDSorter> iterator = results.get(i).iterator();
				int rank = 0;
				while (iterator.hasNext() && rank < 25) {
					IDSorter countPair = iterator.next();
					String token = (String)tokenDictionary.lookupObject(countPair.getID());
					String total = Integer.toString((int)countPair.getWeight());
					output.write(token + "\t" + total + "\n");
					rank++;
				}
			}
			
			output.close();
		} catch (Exception e) {
			System.out.println("Error: Could not write file to disk.");
		} 
    	
    	return;
    }
    
    private void writeVectors(ParallelTopicModel model, String setName, int numTopics) {
    	String outFile = setName + "-" + Integer.toString(numTopics) + "-docvectors.csv";

		ArrayList<TopicAssignment> results = model.getData();
		String[] fileNames = new String[results.size()];
		int[][] counts = new int[results.size()][numTopics];
    	for (int i=0;i<results.size();i++){
    		String rawName = new String(); 
    		rawName += results.get(i).instance.getName();
    		String[] parts = rawName.split("/");
    		fileNames[i] = parts[parts.length-1];
    		
    		LabelSequence tokenAssignments = results.get(i).topicSequence;
    		for (int j=0;j<tokenAssignments.size();j++) {
    			counts[i][tokenAssignments.getIndexAtPosition(j)] += 1;
    		}
    		
    	}
    	
    	try {
			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile),"UTF-8"));
	    	
	    	for (int i=0;i<fileNames.length;i++) {
	    		output.write(fileNames[i]);
	    		for (int j=0;j<counts[i].length;j++) {
	    			output.write("," + Integer.toString(counts[i][j]));
	    		}
	    		output.write("\n");
	    	}
			
			output.close();
		} catch (Exception e) {
			System.out.println("Error: Could not write file to disk.");
		} 
    	
    }
    
    private void writeModel(ParallelTopicModel model, String path) {
    	model.write(new File(path));
    }
    
    private void writeInfer(ParallelTopicModel model, String path) {
    	FileUtils.writeObject(new File(path), model.getInferencer());
    }
    
    public LDA (Config c, Indexer i) {
    	options = c;
    	index = i;
    }
    
    public void begin() throws Exception {
    	
		ParallelTopicModel model;
		if(options.getLoadModel()) {
			System.out.println("Loading model from disk");
			model = ParallelTopicModel.read(new File(options.getModelPath()));
			System.out.println("Discovered model with " + Integer.toString(model.getNumTopics()) + " topics");
		}
		else{
			// Perform topic model according to options set prior to runtime
			System.out.println("Beginning topic model at " + new Date().toString());
    			model = doModel();
    		}
 
	        // Write out using custom methods that better support post-processing
        System.out.println("Writing results to disk.");     
        writeKeys(model,options.getSetName(),options.getNumTopics());
        writeVectors(model,options.getSetName(),options.getNumTopics());
        
        if (options.getSaveModel()) {
        	System.out.println("Writing model to disk.");
        	writeModel(model,options.getModelPath());
        }
        
        if (options.getSaveInfer()) {
        	System.out.print("Writing inference tool to disk.");
        	writeInfer(model,options.getInferPath());
        }
	        
        
    }

}