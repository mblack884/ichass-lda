ICHASS-LDA
======
I wrote this interface for MALLET to minimize the post-processing and formating that MALLET performs prior to write out and add a "configuration file" that can associated with topic model output as a kind of receipt. 

When using MALLET's command-line output settings, there are two ways to get counts of tokens assigned to each topic in documents: parse the "output-state" file or count tokens in documents and multiply their sums by the membership (percentage) vectors in the "output-doc-topics." Either is problematic at scale, and even in smaller projects can require significant memory/CPU time. This interface forgoes the need to do either.

The "configuration file" provides access to the same options available through the command line interface. When doing exploratory work, it can also serve as a kind of receipt that can be stored with the output of a topic model. This proved easier to keep track of than copying and pasting command line entries. It is also much easier to read!

## Setup and Usage
Dependencies: [MALLET](http://mallet.cs.umass.edu/)

Can be compiled and run as a JAR. Configuration file must be specified in the command line (e.g., "java -jar ichass-lda.jar moz50.config").

Configuration files are list of variable and definitions separated by an equals sign. Variable names are no case sensitive but stored values are (esp. pathnames). Variables marked in bold are required:
* working=Directory containing corpus (to be indexed)
* writeindex=Output path for index file
* loadindex=Input path if using index from a previous session
* onlyindex=If "true", halt after indexing before beginning model
* **numtopics**=Number of topics to be modeled (if generating topic model)
* **setname**=Root name for all output files
* **iterations**=Number of model iterations performed before write-out
* threads=Defaults to 2. Set higher to use MALLET's parallelization features (if running on HPC hardware)
* pathpath=Path to pagefile (if used)
* jobtype=Set to 1 for topic inferencing
* savemodel=Save complete model file to disk at write-out (for use with inferencer)
* loadmodel=Load model from a previous session (for use with inferencer)
* saveinfer=Save inferencing tool at write-out
* loadinfer=Load inferencing tool from previous session

Either a working directory or a completed index must be specified. If neither is listed, the configuration will be rejected. If both are specified, the interface will prioritize building a new one from the corpus in the working directory.