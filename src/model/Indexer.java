package model;

import cc.mallet.util.*;
import cc.mallet.types.*;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import java.util.*;
import java.util.regex.*;
import java.io.*;
import java.nio.charset.Charset;

public class Indexer {

	private Pipe pipe;
	private Config options;
	private InstanceList instances;
	
	public Indexer(Config c) {
		options = c;
		if (options.getLoadIndex()) {
			System.out.println("Reading index from " + options.getIndexPath());
        	instances = (InstanceList) FileUtils.readObject(new File(options.getIndexPath()));
		}
		else {
    		System.out.println("Building index from " + Integer.toString(getNumFiles(options.getWorkingDir())) + " volumes in " + options.getWorkingDir());
        	instances = readDirectory(new File(options.getWorkingDir()));
        	if (options.getWriteIndex()) {
            	// Save index to disk
        		System.out.println("Saving copy of index to disk as " + options.getSetName() + ".mallet");
        		FileUtils.writeObject(new File(options.getIndexPath() + options.getSetName() + ".mallet"),instances);
        	}
		}
	}
	
	public InstanceList getIndex() {
		return instances;
	}
	
    private int getNumFiles(String path) {
    	File check = new File(path);
    	if (check.isDirectory()) {
    		return check.list().length;
    	}
    	return 0;
    }
	
	private InstanceList readDirectory(File directory) {
	    return readDirectories(new File[] {directory});
	}
	
    private InstanceList readDirectories(File[] directories) {
        
        // Construct a file iterator, starting with the 
        //  specified directories, and recursing through subdirectories.
        // The second argument specifies a FileFilter to use to select
        //  files within a directory.
        // The third argument is a Pattern that is applied to the 
        //   filename to produce a class label. In this case, I've 
        //   asked it to use the last directory name in the path.
        FileIterator iterator = new FileIterator(directories,new TxtFilter(),FileIterator.LAST_DIRECTORY);

        // Construct a new instance list, passing it the pipe
        //  we want to use to process instances.
        InstanceList instances = new InstanceList(buildPipe());

        // Now process each instance provided by the iterator.
        instances.addThruPipe(iterator);

        return instances;
    }
    
    
    class TxtFilter implements FileFilter {

        /** Test whether the string representation of the file 
         *   ends with the correct extension. Note that {@ref FileIterator}
         *   will only call this filter if the file is not a directory,
         *   so we do not need to test that it is a file.
         */
        public boolean accept(File file) {
            return file.toString().endsWith(".txt");
        }
    }
    
    private Pipe buildPipe() {
    	
        // Begin by importing documents from text to feature sequences
        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

        // Pipes: lowercase, tokenize, remove stopwords, map to features
        pipeList.add(new Input2CharSequence("UTF-8"));
        pipeList.add( new CharSequence2TokenSequence(Pattern.compile("[a-zA-Z]{3,}")) );
        pipeList.add( new TokenSequenceLowercase() );
        pipeList.add( new TokenSequenceRemoveStopwords(new File("stop-en.txt"), "UTF-8", false, false, false) );
        pipeList.add( new TokenSequence2FeatureSequence() );
        
        return new SerialPipes(pipeList);
    	
    }

	
}
