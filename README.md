
DistributedRepresentations and ConceptFinder
============================================

Version 1.0

Included
--------

Projects:
* DistributedRepresentationsProject - The parent pom for the other projects
* OwlTools - Classes for accessing the OpenCyc OWL export
* CycMapDBTools - Some configuration defaults for the other other projects
* DistributedRepresentations - Project to access Word2Vec sources
* ConceptFinder - Methods to find nearby concepts in the Word2Vec space

Other Files:
* GoogleNews-vectors-negative300.bin.gz - The GoogleNews Word2Vec Space
* BioASQ - The Word2Vec space developed by BioASQ and trained on Pubmed sources
* owl-export-unversioned.owl - The OpenCyc export
* This README file

Requirements
------------

* These projects require Java 1.8.
* **_This code has not yet been tested on Windows._**

Description and Usage
---------------------

The projects in this repository constitute a library for accessing Word2Vec content and searching in that space.
The OwlTools project provides access to OpenCyc concepts that can be
mapped into the space.  These mapped OpenCyc concepts can be viewed using the Taxonomy Viewer, located in the KBTaxonomy repository, which uses the Distributed Representations libraries to allow users to find OpenCyc concepts by way of nearest term search in the Word2Vec space.

At present, the library supports two sources:

1. The word2vec space produced by Google by training on 10^11 words of news. - (https://code.google.com/p/word2vec/)
2. The word2vec space produced by BioASQ by training on pubmed. - (http://bioasq.org/news/bioasq-releases-continuous-space-word-vectors-obtained-applying-word2vec-pubmed-abstracts)

To use these libraries, you will need to update some file paths to your local system as follows:

In DistributedRepresentations:
1. `GoogleNewsW2VSpace.java`
  * Modify the `w2vfile` variable so that it points to where you save the GoogleNews-vectors archive included in this repository
2. `BiologyW2VSpace.java`
  * Modify the `filebase` variable to where you save the BioASQ directory word2vecTools subdirectory
3. `Config.java`
  * Modify the `fallBackLocation` variable to a directory in your file system, ideally, where you saved the GoogleNews archive
  
In ConceptFinder:  
1. `ConceptFinderConfig.java`
  * Modify the `fallBackLocation` variable to a directory in your file system, ideally, where you saved the GoogleNews archive
  * Modify the `w2vVectorFile` variable accordingly
  
In OwlTools:
1. `OwlToolsConfig.java`
  * Modify the `ocyclocation` variable to match the location of where you save the OpenCyc export file, `owl-export-unversioned.owl`
  * Modify the `fallBackLocation` variable to match the location you gave in `ConceptFinderConfig.java`
  
To install the libraries to your local Maven repository, simply install the DistributedRepresentationsParent project.  This will install all four of its children to your local Maven repository.  To confirm that everything is working properly, run the integration tests in each of the projects.  Note that some tests may take a long time (on the order of several hours) to run the first time, but should be faster in subsequent runs.  The `Word2VecSpaceIT.java` test, in particular, will be setting up the Google News space on your local system, so it needs to run through all of the concepts in the space.  This is a one-time operation though, so you should not have to perform this set up step again.

IMPORTANT: If something goes wrong during the MapDB set-up operations, which get kicked off by running the integration tests in these projects, you may need to remove the MapDB directory and start again.  This sometimes happens if the set-up process is interrupted before it has completed.
=======
# DistributedRepresentations
DistributedRepresentations
