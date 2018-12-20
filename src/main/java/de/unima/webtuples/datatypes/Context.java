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



package de.unima.webtuples.datatypes;

/**
 * This class defines the context of a tuple exctraction 
 * 
 * @author Julian Seitener, Nacho Vidal and Stefano Faralli
 *
 */
public class Context {

    private String plds;
    private String sentence;
    public Context(String plds, String sentence) 
    {
     this.plds=plds;
     this.sentence=sentence;
    }
    public String getSentence()
    {
        return sentence;
    }  
    public String getPlds()
    {
        return plds;
    }  
}
