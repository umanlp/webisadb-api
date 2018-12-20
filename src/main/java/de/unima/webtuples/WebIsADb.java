// Web Data Commons - WebIsA Database
// http://webdatacommons.org/isadb/

// Christian Bizer
// Kai Eckert
// Stefano Faralli
// Robert Meusel
// Heiko Paulheim
// Simone Paolo Ponzetto


//WebIsADb is a publicly available database containing more than 400 million hypernymy relations we extracted from the CommonCrawl web corpus. This collection of relations represents a rich source of knowledge and may be useful for many researchers. We offer the tuple dataset for public download and an application programming interface to help other researchers programmatically query the database.

//This software is meant to be the Jave API to query a WebIsADb instance.

// The WebIsADb and the API are licensed under a Creative Commons Attribution-Non Commercial-Share Alike 3.0 License: 
// http://creativecommons.org/licenses/by-nc-sa/3.0/.

// Acknowledgements
// This work was partially funded by the Deutsche Forschungsgemeinschaft within the JOIN-T project (research grant PO 1900/1-1). Part of the computational resources used for this work were provide by an Amazon AWS in Education Grant award.



package de.unima.webtuples;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import de.unima.webtuples.datatypes.Context;
import de.unima.webtuples.datatypes.Tuple;
import de.unima.webtuples.queries.CoreTupleQueryResultIterator;
import de.unima.webtuples.queries.TupleQueryResultIterator;
import de.unima.webtuples.queries.AllTuplesResultIterator;
import de.unima.webtuples.queries.ContextResultIterator;
import de.unima.webtuples.queries.MultipleContextsResultIterator;
import java.util.HashSet;
import java.util.Set;

/**
 * This class sets the connection to the mongodb instances. 
 * The class follows the singleton pattern. Therefore only one instance of this class can be instantiated.
 * Additionally this class provides methods to query the isa database.
 * 
 * @author Julian Seitener, Nacho Vidal and Stefano Faralli
 *
 */
public class WebIsADb
{
    // the host of the MongoDB WebIsADb tuples instance: (e.g. localhost)
    public final static String tuplesDbUrl="<your host here>";
    // the MongoDB name for the WebIsADb tuples instance: 
    public final static String tuplesDbName="tuplesdb";
    // the port where the MongoDB WebIsADb tuples instance listen: (e.g. 27017 is the defualt MongoDB server port)
    public final static int dbPort_tuples_instance=27017;    
    
    // the host of the MongoDB WebIsADb contexts instance: (e.g. localhost)
    public final static String contextsDbUrl="wifo5-31.informatik.uni-mannheim.de";
    // the MongoDB name for the WebIsADb contexts instance: 
    public final static String contextsDbName="sentencesdb";
    // the port where the MongoDB WebIsADb contexts instance listen: (e.g. 27017 is the defualt MongoDB server port)
    public final static int dbPort_contexts_instance=27017;   
    
    // following the singelton pattern this is the reference to the unique static instance of the class
    public static WebIsADb instance=null;
    
    // reference to the MongoDB API to connect to the tuples instance:
    private static DB mongoDb_tuples_instance = null;
    private static MongoClient mongoClient_tuples_instance = null;
    
    // reference to the MongoDB API to connect to the contexts instance:
    private static DB mongoDb_contexts_instance = null;
    private static MongoClient mongoClient_contexts_instance = null;
    
    
    
    /* singleton pattern for contructor*/
    private WebIsADb()
    {
        // open the connection to the MongoDb instance for the tuples 
        mongoClient_tuples_instance = new MongoClient(tuplesDbUrl, dbPort_tuples_instance);
    	mongoDb_tuples_instance = mongoClient_tuples_instance.getDB(tuplesDbName);
        
        // open the connection to the MongoDb instance for the contexts 
        mongoClient_contexts_instance = new MongoClient(contextsDbUrl, dbPort_contexts_instance);
    	mongoDb_contexts_instance = mongoClient_contexts_instance.getDB(contextsDbName);
    }
    
    /**
     * This method will return an Iterator, to iterate through all the tuples.
     * For this purpose every single entry of all the instance tables, which start with i, is returned.
     * @return an instance of AllTuplesResultIterator, to iterate through all the tuples
     * @throws java.lang.Exception
     */
    public AllTuplesResultIterator getAllTuples() throws Exception
    {

    	AllTuplesResultIterator qri=new AllTuplesResultIterator(mongoDb_tuples_instance,"i");
    	return qri;
    }
    
    /**
     * This method will return an Iterator, to iterate through all the contexts corresponding to the context ids in provids.
     * For this purpose every single entry of all the instance tables, which start with i, is returned.
     * @param provids a set of context id
     * @param limit a parameter to limit the number of results. 0 means no limits.
     * @return an instance of MultipleContextsResultIterator, to iterate through all the requested contexts
     * @throws java.lang.Exception
     */
    
    public MultipleContextsResultIterator getMultipleContextsWithProvid(Set<String>  provids, int limit)  throws Exception
    {
    	MultipleContextsResultIterator sri = new MultipleContextsResultIterator(provids, mongoDb_contexts_instance,limit);
    	return sri;
    
    }
      /**
     * This method will return an Iterator, to iterate through all the contexts corresponding to the context ids in provids.
     * For this purpose every single entry of all the instance tables, which start with i, is returned.
     * @param provids a set of context id
     * @return an instance of MultipleContextsResultIterator, to iterate through all the requested contexts
     * @throws java.lang.Exception
     */
    public MultipleContextsResultIterator getMultipleSentenceWithProvid(Set<String>  provids)  throws Exception
    {
    	MultipleContextsResultIterator sri = new MultipleContextsResultIterator(provids, mongoDb_contexts_instance,0);
    	return sri;
    }
    /**
     * This method will return an Iterator, to iterate through all the contexts corresponding to the context id provid.
     * @param provid : Is the id of a context. 	The context ID is used to keep the relation between a tuple and the context of the extraction.      
     * @return a ContextResultIterator instance to iterate through all the contexts corresponding to the context id provid.
     * @throws java.lang.Exception
     */
    public ContextResultIterator getContextWithProvid(String provid)  throws Exception
    {
    	ContextResultIterator sri = new ContextResultIterator(provid, mongoDb_contexts_instance);
    	return sri;
    }
    /**
     * This method return TupleQueryResultIterator, to iterate through all the tuple that match the specified parameters.
     * Additionally parameters are passed, which further limit the result.
     * @param instanceHead : Refers to the core noun of the instance of a tuple. It consists of exactly one word.
     * 		Examples: "apple", "germany"
     * @param clazzHead : Refers to the core noun of the class of a tuple. It consists of exactly one word.
     * 		Examples: "fruit", "country"
     * @param iPreModifier : Refers to the premodifier of the instance, that has to be present for the core noun. 
     * 		If every premodifier should be retrieved use the wildcard "*".
     * 		Examples: "red", "*", ""
     * @param iPostModifier : Refers to the postmodifier of the instance, that has to be present for the core noun.
     * 		If every postmodifier should be retrieved use the wildcard "*".
     * 		Examples: "from portugal", "*", ""
     * @param cPreModifier : Refers to the premodifier of the class, that has to be present for the core noun. 
     * 		If every premodifier should be retrieved use the wildcard "*".
     * 		Examples: "red", "*", ""
     * @param cPostModifier : Refers to the postmodifier of the class, that has to be present for the core noun.
     * 		If every postmodifier should be retrieved use the wildcard "*".
     * 		Examples: "from portugal", "*", ""
     * @param pids : Refers to list of pattern IDs. Only if one of the pattern IDs is found in an tuple, it can be returned as result.
     * 		If every pattern ID should be allowed, pass an null value pids.
     * 		Example: ["p1","p2"], null
     * @param plds : Refers to list of pay level domains IDs. Only if one of the pay level domains is found in an tuple, it can be returned as result.
     * 		If every pay level domain should be allowed, pass an null value plds.
     * 		Example: ["google.com", "example.org"], null
     * @param minFrequency : This value specifies the minimum allowed frequency of a result entry. 
     * 		If no minimum frequency threshold is needed, pass the value 0;
     * 		Examples: "5", "0"
     * @param maxFrequency : This value specifies the maximum allowed frequency of a result entry. 
     * 		If no maximum frequency threshold is needed, pass the value 0;
     * 		Examples: "5", "0"
     * @param minPidSpread : This value specifies the minimum allowed pattern id spread of a result entry. 
     * 		The pattern id spread describes the amount of distinct patterns, that identified this tuple.
     * 		If no minimum pattern id spread threshold is needed, pass the value 0;
     * 		Examples: "5", "0"
     * @param maxPidSpread : This value specifies the maximum allowed pattern id spread of a result entry. 
     * 		The pattern id spread describes the amount of distinct patterns, that identified this tuple.
     * 		If no maximum pattern id spread threshold is needed, pass the value 0;
     * 		Examples: "5", "0"
     * @param minPldSpread : This value specifies the minimum allowed pay level domain spread of a result entry. 
     * 		The pay level domain spread describes the amount of distinct pay level domains, that identified this tuple.
     * 		If no minimum pay level domain spread threshold is needed, pass the value 0;
     * 		Examples: "5", "0"
     * @param maxPldSpread : This value specifies the maxumum allowed pay level domain spread of a result entry. 
     * 		The pay level domain spread describes the amount of distinct pay level domains, that identified this tuple.
     * 		If no maximum pay level domain spread threshold is needed, pass the value 0;
     * 		Examples: "5", "0"
     * @param strict : This parameter is used to process the pids and plds lists. 
     * 		Value true: Each single pid in pids and each single pld in plds has to be present in a Tuple
     * 		Value false: Only one pid of the pids and only one pld of the plds has to be present in a Tuple
     * @return a TupleQueryResultIterator, to iterate through all the tuple that match the specified parameters
     * @throws java.lang.Exception
     */
    public TupleQueryResultIterator getTuplesWhere(String instanceHead, String clazzHead, String iPreModifier, String iPostModifier, String cPreModifier, String cPostModifier,
    		String[] pids, String[] plds, double minFrequency, double maxFrequency, int minPidSpread, int maxPidSpread, 
    		int minPldSpread, int maxPldSpread, boolean strict) throws Exception
    {

       TupleQueryResultIterator fuqri=new TupleQueryResultIterator(instanceHead,clazzHead,mongoDb_tuples_instance, iPreModifier, iPostModifier, cPreModifier, cPostModifier, pids, plds, minFrequency,
    		   maxFrequency, minPidSpread, maxPidSpread, minPldSpread, maxPldSpread, strict);
       return fuqri;
    }
    
    /**
     * This method return TupleQueryResultIterator, to iterate through all the tuple's modifications that match the instance string value.
     * @param instanceHead : Refers to the core noun of the instance of a tuple. It consists of exactly one word.
     * 		Examples: "apple", "germany"
     * @param strict
     * @return TupleQueryResultIterator, to iterate through all the tuple that match the instance string value.
     * @throws Exception
     */
    public TupleQueryResultIterator getTuplesWhereInstanceLemma(String instanceHead, boolean strict) throws Exception
    {
        TupleQueryResultIterator fuqri=new TupleQueryResultIterator(instanceHead,"*",mongoDb_tuples_instance, "*", "*", "*", "*", null, null, 0,
    		 0, 0, 0, 0, 0, strict);
       return fuqri;
    }
    
    
    
    /**
     * Similar to getTuplesWhere this method return a CoreTupleQueryResultIterator iterate through result of a query on the "core" index of the tuples.
     * the core index is meant a groped collection of tuples which share the same instance value and class value.
     * 
     * @param instance : Refers to the core noun of the instance of a tuple. It consists of exactly one word.
     * 		Examples: "apple", "germany"
     * @param clazz : Refers to the core noun of the class of a tuple. It consists of exactly one word.
     * 		Examples: "fruit", "country"
     * @param pids : Refers to list of pattern IDs. Only if one of the pattern IDs is found in an tuple, it can be returned as result.
     * 		If every pattern ID should be allowed, pass an null value pids.
     * 		Example: ["p1","p2"], null
     * @param plds : Refers to list of pay level domains IDs. Only if one of the pay level domains is found in an tuple, it can be returned as result.
     * 		If every pay level domain should be allowed, pass an null value plds.
     * 		Example: ["google.com", "example.org"], null
     * @param minFrequency : This value specifies the minimum allowed frequency of a result entry. 
     * 		If no minimum frequency threshold is needed, pass the value 0;
     * 		Examples: "5", "0"
     * @param maxFrequency : This value specifies the maximum allowed frequency of a result entry. 
     * 		If no maximum frequency threshold is needed, pass the value 0;
     * 		Examples: "5", "0"
     * @param minPidSpread : This value specifies the minimum allowed pattern id spread of a result entry. 
     * 		The pattern id spread describes the amount of distinct patterns, that identified this tuple.
     * 		If no minimum pattern id spread threshold is needed, pass the value 0;
     * 		Examples: "5", "0"
     * @param maxPidSpread : This value specifies the maximum allowed pattern id spread of a result entry. 
     * 		The pattern id spread describes the amount of distinct patterns, that identified this tuple.
     * 		If no maximum pattern id spread threshold is needed, pass the value 0;
     * 		Examples: "5", "0"
     * @param minPldSpread : This value specifies the minimum allowed pay level domain spread of a result entry. 
     * 		The pay level domain spread describes the amount of distinct pay level domains, that identified this tuple.
     * 		If no minimum pay level domain spread threshold is needed, pass the value 0;
     * 		Examples: "5", "0"
     * @param maxPldSpread : This value specifies the maximum allowed pay level domain spread of a result entry. 
     * 		The pay level domain spread describes the amount of distinct pay level domains, that identified this tuple.
     * 		If no maximum pay level domain spread threshold is needed, pass the value 0;
     * 		Examples: "5", "0"
     * @param strict : This parameter is used to process the pids and plds lists. 
     * 		Value true: Each single pid in pids and each single pld in plds has to be present in a Tuple
     * 		Value false: Only one pid of the pids and only one pld of the plds has to be present in a Tuple
     * @return a Tuple Iterator, which contains tuples, that match the specified parameters
     * @throws java.lang.Exception
     */
    public CoreTupleQueryResultIterator getCoreTuplesWhere(String instance, String clazz,
    		String[] pids, String[] plds, double minFrequency, double maxFrequency, int minPidSpread, int maxPidSpread, 
    		int minPldSpread, int maxPldSpread, boolean strict) throws Exception
    {
    	
    	CoreTupleQueryResultIterator cqri =new CoreTupleQueryResultIterator(instance,clazz,mongoDb_tuples_instance, pids, plds, minFrequency,
    		   maxFrequency, minPidSpread, maxPidSpread, minPldSpread, maxPldSpread, strict);
       return cqri;
    }
    
    /**
     * This method return the singelton WebIsADb instance. 
     * @return a WebIsADb instance
     */
    public static WebIsADb getInstance()
    {
        if (instance==null) instance=new WebIsADb();
        return instance;
    }              
    /**
     * Close all the connection to MongoDBs
     */
    public static void close()
    {
        if (mongoClient_tuples_instance!=null) mongoClient_tuples_instance.close();
        if (mongoClient_contexts_instance!=null) mongoClient_contexts_instance.close();
        mongoClient_tuples_instance=null;
        mongoClient_contexts_instance=null;
        instance=null;
    }
    /**
     * Example of the class usage.
     * @param args
     */
    public static void main(String[] args) 
    {
       WebIsADb webisadb=WebIsADb.getInstance();
       try 
       {
        // Example of iterator of all the tuples
        int counts=0;   
        AllTuplesResultIterator ti=webisadb.getAllTuples();
        while (ti.hasNext())
        {
            for(Tuple t:ti.next())
        	{
                    counts++;
                    System.out.println(t);
                }
            if (counts==10) break;
        }
           
           
           
        //example of ContextResultIterator
        Set<String> keys=new HashSet<String>();
        keys.add("1");
        keys.add("2");
        MultipleContextsResultIterator sri = webisadb.getMultipleSentenceWithProvid(keys);
        while (sri.hasNext())
        {
            Context provenance=sri.next();
            System.out.println(provenance.getPlds()+"\t"+provenance.getSentence());
        }
        sri.close(); 
        
        
        //example of TupleQueryResultIterator
        TupleQueryResultIterator tuqri = webisadb.getTuplesWhere("gaga", "", "", "", "", "",
        		null, null, 0, 0, 0, 0, 0, 0, false);
        double sumOfFrequencies = 0;   
        while (tuqri.hasNext())
        {
        	for(Tuple t:tuqri.next())
        	{
        		sumOfFrequencies += t.getAdjustedFrequency();
        		System.out.println(t);
        	}
        }
        
        System.out.println("Overall count: " + sumOfFrequencies + " times.");
        
        // be careful and always close your database connections
        WebIsADb.close(); 
        
       }catch (Exception e)
       {
       }
    }
    
}

