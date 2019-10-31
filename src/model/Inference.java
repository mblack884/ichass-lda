package model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import cc.mallet.topics.*;
import cc.mallet.types.*;

public class Inference {

	private Config options;
	private Indexer index;
	private ArrayList<double[]> docMatrix = new ArrayList<double[]>();
	
	public Inference(Config c, Indexer i) {
		options = c;
		index = i;
	}
	
	public void begin() throws Exception {
		TopicInferencer infer;
		
		if (options.getLoadInfer()) {
			System.out.println("Loading infer tool from disk");
			infer = TopicInferencer.read(new File(options.getInferPath()));

		}
		else {
			System.out.println("Building infer tool from previous model");
			ParallelTopicModel model = ParallelTopicModel.read(new File(options.getModelPath()));
			System.out.println("Discovered model with " + Integer.toString(model.getNumTopics()) + " topics");
			infer = model.getInferencer();
			model = null;
		}
		
		System.out.println("Beginning inferencing at " + new Date().toString());
		System.out.println("Attempting to infer topics...");
		doInfer(infer);
		System.out.println("Completed inferencing at " + new Date().toString());
		/*
		for (int i=0;i<docMatrix.size();i++) {
			System.out.println("Document " + Integer.toString(i));
			double[] vector = docMatrix.get(i);
			String line = new String();
			for (int j=0;j<vector.length;j++) {
				line = line + Double.toString(vector[j]) + " ";
			}
			System.out.println(line);
		}
		*/
		
	}
	
	private void doInfer(TopicInferencer inferTool) {
		InstanceList instances = index.getIndex();
		
		System.out.println("Found instances: " + Integer.toString(instances.size()));
		/*
		Iterator<Instance> it = instances.iterator();
		while (it.hasNext()) {
			System.out.println("NEXT!");
			Instance document = it.next();
			double [] vector = inferTool.getSampledDistribution(document, 1000, 1, 5);
			docMatrix.add(vector);
		}
		*/
		for(int i=0;i<instances.size();i++) {
			docMatrix.add(inferTool.getSampledDistribution(instances.get(i), 10, 1, 5));
		}
		writeInferences(instances);
	}
		
	private void writeInferences(InstanceList instances) {
		System.out.println("Writing inferences to disk.");
		File outFile = new File(options.getSetName() + "-infervectors.csv");
		DecimalFormat rounder = new DecimalFormat("0.000000000000");
    	try {
			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile),"UTF-8"));
	    	
	    	for (int i=0;i<instances.size();i++) {
	    		output.write(instances.get(i).getName().toString());
	    		for (int j=0;j<docMatrix.get(i).length;j++) {
	    			output.write("," + rounder.format(docMatrix.get(i)[j]));
	    		}
	    		output.write("\n");
	    	}
			
			output.close();
		} catch (Exception e) {
			System.out.println("Error: Could not write file to disk.");
		} 
	}
	
}
