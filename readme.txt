 Web Data Commons - WebIsA Database
 http://webdatacommons.org/isadb/

 Christian Bizer
 Kai Eckert
 Stefano Faralli
 Robert Meusel
 Heiko Paulheim
 Simone Paolo Ponzetto


WebIsADb is a publicly available database containing more than 400 million hypernymy relations we extracted from the CommonCrawl web corpus. This collection of relations represents a rich source of knowledge and may be useful for many researchers. We offer the tuple dataset for public download and an application programming interface to help other researchers programmatically query the database.

This software is meant to be the Jave API to query a WebIsADb instance.*,**,***

The WebIsADb and the API are licensed under a Creative Commons Attribution-Non Commercial-Share Alike 3.0 License: 
http://creativecommons.org/licenses/by-nc-sa/3.0/.

Acknowledgements
This work was partially funded by the Deutsche Forschungsgemeinschaft within the JOIN-T project (research grant PO 1900/1-1). Part of the computational resources used for this work were provide by an Amazon AWS in Education Grant award.

======================================================================
* this package includes also a JavaDoc folder.
** example of usage of the API can be viewed in the main method of file: "src/de/unima/webtuples/WebIsADb.java" 
*** Please rember to configure your MongoDb instance connections 

File "src/de/unima/webtuples/WebIsADb.java":
 47   ..... 
 48   // the host of the MongoDB WebIsADb tuples instance: (e.g. localhost)
 49   public final static String tuplesDbUrl="<your host here>";      
 50   // the MongoDB name for the WebIsADb tuples instance: 
 51   public final static String tuplesDbName="tuplesdb";
 52   // the port where the MongoDB WebIsADb tuples instance listen: (e.g. 27017 is the defualt MongoDB server port)
 53   public final static int dbPort_tuples_instance=27017;    
 54   
 55   // the host of the MongoDB WebIsADb contexts instance: (e.g. localhost)
 56   public final static String contextsDbUrl="wifo5-31.informatik.uni-mannheim.de";
 57   // the MongoDB name for the WebIsADb contexts instance: 
 58   public final static String contextsDbName="sentencesdb";
 59   // the port where the MongoDB WebIsADb contexts instance listen: (e.g. 27017 is the defualt MongoDB server port)
 60   public final static int dbPort_contexts_instance=27017;   
 61   ....
