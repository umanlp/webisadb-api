// Web Data Commons - WebIsA Database
// http://webdatacommons.org/isadb/
// Christian Bizer
// Kai Eckert
// Stefano Faralli
// Robert Meusel
// Heiko Paulheim
// Simone Paolo Ponzetto
// WebIsADb is a publicly available database containing more than 400 million hypernymy relations we extracted from the CommonCrawl web corpus. This collection of relations represents a rich source of knowledge and may be useful for many researchers. We offer the tuple dataset for public download and an application programming interface to help other researchers programmatically query the database.
// This software is meant to be the Jave API to query a WebIsADb instance.
// The WebIsADb and the API are licensed under a Creative Commons Attribution-Non Commercial-Share Alike 3.0 License: 
// http://creativecommons.org/licenses/by-nc-sa/3.0/.
// Acknowledgements
// This work was partially funded by the Deutsche Forschungsgemeinschaft within the JOIN-T project (research grant PO 1900/1-1). Part of the computational resources used for this work were provide by an Amazon AWS in Education Grant award.
package de.unima.webtuples.queries;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;

import de.unima.webtuples.datatypes.Tuple;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to iterate through tuples.
 * @author Julian Seitener, Nacho Vidal and Stefano Faralli
 *
 */
public class TupleQueryResultIterator extends CoreTupleQueryResultIterator {

    String iPreModifier;
    String iPostModifier;
    String cPreModifier;
    String cPostModifier;

    /**
     * 
     * @param instance : Refers to the value of the instance of a tuple
     * 		Classes or instances consist of exactly one word, the so called core noun of a noun phrase.
     * 		Examples: "apple", "peach"
     * @param clazz : Refers to the value of the class of the tuple 
     * 		specified by the field parameter above - are selected as resuilt candidates
     * 		Examples: "company", "fruit"
     * @param db : Refers to the mongoDB database that is queried
     * @param iPreModifier : Refers to the premodifier, that has to be present for the core noun. 
     * 		If every premodifier should be retrieved use the wildcard "*".
     * 		Examples: "red", "*", ""
     * @param iPostModifier : Refers to the postmodifier, that has to be present for the core noun.
     * 		If every postmodifier should be retrieved use the wildcard "*".
     * 		Examples: "from portugal", "*", ""
     * @param cPreModifier : Refers to the premodifier, that has to be present for the core class. 
     * 		If every premodifier should be retrieved use the wildcard "*".
     * 		Examples: "red", "*", ""
     * @param cPostModifier : Refers to the postmodifier, that has to be present for the core class.
     * 		If every postmodifier should be retrieved use the wildcard "*".
     * 		Examples: "from portugal", "*", ""
     * @param pids : Refers to list of pattern IDs. Only if one of the pattern IDs is found in an tuple, it can be returned as result.
     * 		If every pattern ID should be allowed, pass an null value pids.
     * 		Example: ["p1","p2"], null
     * @param plds : Refers to list of pay level domains IDs. Only if one of the pay level domains is found in an tuple, it can be returned as result.
     * 		If every pay level domain should be allowed, pass an null value plds.
     * 		Example: ["google.com", "example.org"]
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
     */
    public TupleQueryResultIterator(String instance,String clazz, DB db, 
    		String iPreModifier, String iPostModifier,
    		String cPreModifier, String cPostModifier,
    		String[] pids, String[] plds, 
    		double minFrequency, double maxFrequency,
    		int minPidSpread, int maxPidSpread,
    		int minPldSpread, int maxPldSpread, boolean strict) 
    {
    	super(instance, clazz, db, pids, plds, minFrequency, maxFrequency, minPidSpread, maxPidSpread, minPldSpread, maxPldSpread, strict);
        this.iPreModifier = iPreModifier;
        this.iPostModifier = iPostModifier;
        this.cPreModifier = cPreModifier;
        this.cPostModifier = cPostModifier;
    }
    
    @Override
    public List<Tuple> next() {
    	boolean pldCheck = false;
    	boolean pidCheck = false;
        List<Tuple> results = new ArrayList<>();
        
        DBObject current = cursor.next();
        String inst = current.get("instance").toString();
        String claz = current.get("class").toString();
        
        BasicDBList modificationDBList = (BasicDBList) current.get("modifications");
        BasicDBObject[] modificationDBArr = modificationDBList.toArray(new BasicDBObject[0]);
        for (DBObject singleModification : modificationDBArr) 
        {
        	pldCheck=false;
        	pidCheck=false;
        	if (!singleModification.get("ipremod").equals(this.iPreModifier) && !this.iPreModifier.equals("*"))
        	{
        		continue;
        	}
        	if (!singleModification.get("ipostmod").equals(this.iPostModifier) && !this.iPostModifier.equals("*"))
        	{
        		continue;
        	}
        	if (!singleModification.get("cpremod").equals(this.cPreModifier) && !this.cPreModifier.equals("*"))
        	{
        		continue;
        	}
        	if (!singleModification.get("cpostmod").equals(this.cPostModifier) && !this.cPostModifier.equals("*"))
        	{
        		continue;
        	}
        	if(Double.parseDouble(singleModification.get("frequency").toString())<this.minFrequency && !(this.minFrequency==0))
        	{
        		continue;
        	}
        	if(Double.parseDouble(singleModification.get("frequency").toString())>this.maxFrequency && !(this.maxFrequency==0))
        	{
        		continue;
        	}
        	if(Integer.parseInt(singleModification.get("pidspread").toString())<this.minPidSpread && !(this.minPidSpread==0))
        	{
        		continue;
        	}
        	if(Integer.parseInt(singleModification.get("pidspread").toString())>this.maxPidSpread && !(this.maxPidSpread==0))
        	{
        		continue;
        	}
        	if(Integer.parseInt(singleModification.get("pldspread").toString())<this.minPldSpread && !(this.minPldSpread==0))
        	{
        		continue;
        	}
        	if(Integer.parseInt(singleModification.get("pldspread").toString())>this.maxPldSpread && !(this.maxPldSpread==0))
        	{
        		continue;
        	}
        	if (pids!=null)
        	{
	        	for (String pid : pids)
	        	{
	        		if (singleModification.get("pids").toString().contains(pid+";"))
	        		{
	        			pidCheck=true;
	        		}
	        		if (!singleModification.get("pids").toString().contains(pid+";") && strict)
	        		{
	        			pidCheck=false;
	        			break;
	        		}
	        	}
	        	if (!pidCheck)
	        	{
	        		continue;
	        	}
        	}
        	if (plds!=null)
        	{
	        	for (String pld : plds)
	        	{
	        		if (singleModification.get("plds").toString().contains(pld+";"))
	        		{
	        			pldCheck=true;
	        		}
	        		if (!singleModification.get("plds").toString().contains(pld+";") && strict)
	        		{
	        			pldCheck=false;
	        			break;
	        		}
	        	}
	        	if (!pldCheck)
	        	{
	        		continue;
	        	}
        	}
            results.add(new Tuple(inst, claz,
                    Double.parseDouble(singleModification.get("frequency").toString()),
                    Integer.parseInt(singleModification.get("pidspread").toString()),
                    Integer.parseInt(singleModification.get("pldspread").toString()),
                    singleModification.get("ipremod").toString(), singleModification.get("ipostmod").toString(),
                    singleModification.get("cpremod").toString(), singleModification.get("cpostmod").toString(),
                    singleModification.get("pids").toString(), singleModification.get("plds").toString(), 
                    singleModification.get("provids").toString())
            );
        }
        return results;
    }
}


