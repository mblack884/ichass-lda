package model;

import java.io.File;

public class setup {

    public static void main(String[] args) throws Exception {
    	
    	if(args.length == 0){
    		System.out.println("Error: No configuration file specified.");
    		return;
    	}
    	
    	// Load a topic model options file and check to make sure it contains a valid job definition
    	Config options = new Config(new File(args[0]));
    	if(!options.checkConfig()) {
    		System.out.println("Error: Configuration file does not contain a complete job definition.");
    		return;
    	}
    	
    	Indexer index = new Indexer(options);
    	
    	if (options.getJobType() == 0) {
    		System.out.println("Found configuration for LDA topic model");
    		LDA model = new LDA(options,index);
    		model.begin();
    	}
    	else if (options.getJobType() == 1) {
    		System.out.println("Found configuration for topic inferencing");
    		Inference model = new Inference(options,index);
    		model.begin();
    	}
    	
        
    }

}
