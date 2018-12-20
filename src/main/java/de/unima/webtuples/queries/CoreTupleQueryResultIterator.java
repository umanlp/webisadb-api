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

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import de.unima.webtuples.datatypes.CoreTuple;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * This class is used to iterate through core tuples.
 * @author Julian Seitener, Nacho Vidal and Stefano Faralli
 *
 */
public class CoreTupleQueryResultIterator implements Iterator {

    List<String> tablenames;
    int currenttable = -1;
    DBCursor cursor;
    DBCollection table;
    DB db;
    String instance;
    String clazz;
    String[] pids;
    String[] plds;
    double minFrequency;
    double maxFrequency;
    int minPidSpread;
    int maxPidSpread;
    int minPldSpread;
    int maxPldSpread;
    String sortingprefix;
    boolean strict;

    /**
     * 
     * @param instance : Refers to the value of the instance of a tuple
     * 		Classes or instances consist of exactly one word, the so called core noun of a noun phrase.
     * 		Examples: "apple", "peach"
     * @param clazz : Refers to the value of the class of the tuple 
     * 		specified by the field parameter above - are selected as resuilt candidates
     * 		Examples: "company", "fruit"
     * @param db : Refers to the mongoDB database that is queried
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
     * 		Value false: Only one pid of the pids an
     * d only one pld of the plds has to be present in a Tuple
     */
    public CoreTupleQueryResultIterator(String instance,String clazz, DB db,
    		String[] pids, String[] plds, 
    		double minFrequency, double maxFrequency,
    		int minPidSpread, int maxPidSpread,
    		int minPldSpread, int maxPldSpread, boolean strict) 
    {
        this.db = db;
        String tag="";
        if (instance.equals("*"))
        {
        	this.sortingprefix="c";
        	tag=clazz.replaceAll("[^a-z]", "");
        }
        if (clazz.equals("*"))
        {
        	this.sortingprefix="i";
        	tag=instance.replaceAll("[^a-z]", "");
        }
        if (!clazz.equals("*") && !instance.equals("*"))
        {
        	this.sortingprefix="c";
        	tag=clazz.replaceAll("[^a-z]", "");
        }
        this.clazz=clazz;
        this.instance=instance;
        this.minFrequency = minFrequency;
        this.maxFrequency = maxFrequency;
        this.minPidSpread = minPidSpread;
        this.maxPidSpread = maxPidSpread;
        this.minPldSpread = minPldSpread;
        this.maxPldSpread = maxPldSpread;
        this.plds = plds;
        this.pids = pids;
        this.strict = strict;
        
        tablenames = new ArrayList<>();
       
         if (tag.length()==0)
         {
        	 tablenames.add(sortingprefix+"00");
         }
         else if (tag.length()==1)
         {
        	 tablenames.add(sortingprefix+"0"+tag.substring(0,1));
         }
         else
         {
             tablenames.add(sortingprefix+tag.substring(0,2));
         }
        
         
         if (instance.equals("*") && clazz.equals("*"))
         {
        	tablenames = new ArrayList<>();
         	for (String tn:db.getCollectionNames())
         	{
         		if (tn.startsWith("i"))
         		{
         			tablenames.add(tn);
         		}
         	}
         }
         
        if (tablenames.size() > 0) {
            currenttable = 0;
            openTable(tablenames.get(currenttable));
        }
    }

    private void openTable(String tablename) {

        BasicDBObject query = new BasicDBObject();
        if (!clazz.equals("*"))
        {
        	query.append("class", clazz);
        }
        if (!instance.equals("*"))
        {
        	query.append("instance", instance);
        }
        table = db.getCollection(tablename);
        cursor = table.find(query);/*.sort(new BasicDBObject("frequency", -1));*/
        cursor.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
        //cursor.sort();
    }

    @Override
    public boolean hasNext() {
       
        if (tablenames.isEmpty()) {
            return false;
        }
        if (cursor == null) {
            return false;
        }
        
        
        if (cursor.hasNext()) 
        {
            return true;
        } 
        else 
        {
            if (currenttable < tablenames.size() - 1)
            {
                currenttable += 1;
                openTable(tablenames.get(currenttable));
            } 
            else {
                    return false;
                }
        }
        
        // do we have another result
        return true;
    }
    
    @Override
    public List<? extends CoreTuple> next() {
    	boolean pldCheck = false;
    	boolean pidCheck = false;
        boolean valid = true;
        DBObject current = cursor.next();
        String inst = current.get("instance").toString();
        String claz = current.get("class").toString();
        List<CoreTuple> result = new ArrayList<>();
        
    	if(Double.parseDouble(current.get("frequency").toString())<this.minFrequency && !(this.minFrequency==0))
    	{
    		valid = false;
    	}
    	if(Double.parseDouble(current.get("frequency").toString())>this.maxFrequency && !(this.maxFrequency==0))
    	{
    		valid = false;
    	}
    	if(Integer.parseInt(current.get("pidspread").toString())<this.minPidSpread && !(this.minPidSpread==0))
    	{
    		valid = false;
    	}
    	if(Integer.parseInt(current.get("pidspread").toString())>this.maxPidSpread && !(this.maxPidSpread==0))
    	{
    		valid = false;
    	}
    	if(Integer.parseInt(current.get("pldspread").toString())<this.minPldSpread && !(this.minPldSpread==0))
    	{
    		valid = false;
    	}
    	if(Integer.parseInt(current.get("pldspread").toString())>this.maxPldSpread && !(this.maxPldSpread==0))
    	{
    		valid = false;
    	}
    	if (pids!=null)
    	{
        	for (String pid : pids)
        	{
        		if (current.get("pids").toString().contains(pid+";"))
        		{
        			pidCheck=true;
        		}
        		if (!current.get("pids").toString().contains(pid+";") && strict)
        		{
        			valid=false;
        			break;
        		}
        	}
        	if (!pidCheck)
        	{
        		valid = false;
        	}
    	}
    	if (plds!=null)
    	{
        	for (String pld : plds)
        	{
        		if (current.get("plds").toString().contains(pld+";"))
        		{
        			pldCheck=true;
        		}
        		if (!current.get("plds").toString().contains(pld+";") && strict)
        		{
        			valid=false;
        			break;
        		}
        	}
        	if (!pldCheck)
        	{
        		valid = false;
        	}
    	}
    	
    	if (valid)
    	{
    		result.add(new CoreTuple(inst, claz,
                    Double.parseDouble(current.get("frequency").toString()),
                    Integer.parseInt(current.get("pidspread").toString()),
                    Integer.parseInt(current.get("pldspread").toString()),
                    "", "",
                    ""));
    	}
        return result;
    }
    public void close()
    {
        if (cursor!=null)
        {
            cursor.close();
        }
    
    }
}


