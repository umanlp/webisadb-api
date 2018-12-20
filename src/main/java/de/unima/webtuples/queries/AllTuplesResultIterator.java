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
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import de.unima.webtuples.datatypes.Tuple;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * This class is an iterator, to iterate thorugh all the tuple 
 * The sorting prefix can either be "i" for instances or "c" for classes.
 * Both will return same results, however sorted in different order.
 * 
 * @author Julian Seitener, Nacho Vidal and Stefano Faralli
 *
 */
public class AllTuplesResultIterator implements Iterator {

    List<String> tablenames;
    int currenttable = -1;
    DBCursor cursor;
    DBCollection table;
    DB db;

    
    /**
     * 
     * @param db
     * @param sortingprefix: "i" for instances and "c" for classes
     */
    public AllTuplesResultIterator(DB db, String sortingprefix) {
        this.db = db;
        Set<String> tablenamesapp = db.getCollectionNames();
        tablenames = new ArrayList<>();
        for (String s : tablenamesapp) {
            if (s.startsWith(sortingprefix)) {
                tablenames.add(s);
            }
        }
        if (tablenames.size() > 0) {
            currenttable = 0;
            openTable(tablenames.get(currenttable));
        }
    }

    private void openTable(String tablename) {

        table = db.getCollection(tablename);
        cursor = table.find();
    }

    @Override
    public boolean hasNext() {
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
        // do we have another result
        return true;
    }

    @Override
    public List<Tuple> next() {
        List<Tuple> results = new ArrayList<>();
        
        DBObject current = cursor.next();
        String instance = current.get("instance").toString();

        String clazz = current.get("class").toString();
        
        BasicDBList modificationDBList = (BasicDBList) current.get("modifications");
        BasicDBObject[] modificationDBArr = modificationDBList.toArray(new BasicDBObject[0]);
        for (DBObject singleModification : modificationDBArr) 
        {
            results.add(new Tuple(instance, clazz,
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

