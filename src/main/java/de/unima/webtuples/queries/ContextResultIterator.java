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
import de.unima.webtuples.datatypes.Context;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * This class is used to find the context of the extraction of a tuple.
 * @author Julian Seitener, Nacho Vidal and Stefano Faralli
 *
 */
public class ContextResultIterator implements Iterator {

    List<String> tablenames;
    int currenttable = -1;
    String provid;
    DBCursor cursor;
    DBCollection table;
    DB db;   
    int counter=0;
    int limit=0;
    private Set<String> tablenamesapp;
    /**
     * 
     * @param db : The database, which contains the tables
     * @param provid : The provenance ID of tuple or sentence.
     */
    
    public ContextResultIterator(String provid, DB db)
    {
        this(provid,db,0);
    }
    
    public ContextResultIterator(String provid, DB db,int limit) {
        this.db = db;
        tablenamesapp = db.getCollectionNames();
        tablenames = new ArrayList<>();
       // System.out.println("Query with provid:"+provid);
        this.provid = provid;
        this.limit=limit;
        if (provid==null||provid.equals(""))
        {
            for (String s : tablenamesapp) {
                if (s.startsWith("s")) {
                    tablenames.add(s);
                }
            }
        }
        else
        {
            Double tableId = Math.ceil(Long.parseLong(provid)/1000000);           
            for (String s : tablenamesapp) {
                if (s.equals("s"+tableId.intValue())) 
                {
                    tablenames.add(s);
                    //System.out.println("Added Table:"+s);
                }
            }
        }
        
        if (tablenames.size() > 0) {
            currenttable = 0;
            openTable(tablenames.get(currenttable));
        }
    }

    private void openTable(String tablename) {
    	BasicDBObject query;
    	if (provid==null|| provid.equals("") )
    	{
    		query = new BasicDBObject();
    	}
    	else
    	{
            query = new BasicDBObject().append("provid", provid);
    	}
        if (cursor!=null) 
        {
            cursor.close();        
            cursor=null;
        }
        table = db.getCollection(tablename);
        cursor = table.find(query);
        cursor.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
    }

    @Override
    public boolean hasNext() {
        if (limit>0&&counter>limit) 
        {
            if (cursor!=null) 
            {
                cursor.close();
                cursor=null;
               /* System.out.println("Found record closing the query process!");*/
            }
            return false;
        }
        if (tablenames.isEmpty()) {
            return false;
        }
        if (cursor == null) {
            return false;
        }
        if (cursor.hasNext()) {
            return true;
        } else {
            if (currenttable < tablenames.size() - 1) {
                currenttable += 1;
                openTable(tablenames.get(currenttable));
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public Context next() 
    {       
        
        DBObject current = cursor.next();
        //String result = current.get("provid").toString()+" "+current.get("sentence").toString();      
        Context result= new Context(current.get("pld").toString(),current.get("sentence").toString());
        /*System.out.println("Found record!");
        System.out.println();*/
        counter++;
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