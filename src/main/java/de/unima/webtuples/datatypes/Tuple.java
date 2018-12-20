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
package de.unima.webtuples.datatypes;

/**
 * This class define a tuple.
 * A tuple is the combination of an instance noun phrase decomposition and its class noun phrase decomposition, as well as 
 * its attributes, such as the frequency. 
 * @author Julian Seitener, Nacho Vidal and Stefano Faralli
 *
 */
public class Tuple extends CoreTuple {

    private String instancePreModifiers;
    private String instancePostModifiers;
    private String classPreModifiers;
    private String classPostModifiers;
    
    /**
     * 
     * @param ilem : Is the lemmatized core noun of the instance.
     * @param clem : Is the lemmatized core noun of the class
     * @param adjFreq : Is the frequency of the tuple
     * @param pidSpread : Is pattern id spread, which describes how many distinct patterns found this tuple
     * @param pldSpread : Is the pay level domain spread, which describes on how many different top level domains the tuple was found
     * @param iPreMod : Is the premodifier of the instance
     * @param iPostMod : Is the postmodifier of the instance
     * @param cPreMod : Is the premodifier of the class
     * @param cPostMod : Is the postmodifier of the class
     * @param pids : Contains a set of pattern IDs separated by a semicolon
     * 		Example: "p1;p2;p3a"
     * @param plds: Contains a set of pay level domains separated by a semicolon
     * 		Example: "google.com;wikipedia.org"
     * @param provids: Contains a set of provenance IDs separated by a semicolon
     * 		Example: "378458;9783456;934875"
     */
    public Tuple(
            String ilem, String clem,
            Double adjFreq, int pidSpread,int pldSpread,
            String iPreMod, String iPostMod,
            String cPreMod, String cPostMod,
            String pids, String plds,
            String provids
    ) 
    {
    	super(ilem, clem, adjFreq, pidSpread, pldSpread, pids, plds, provids);
        this.instancePreModifiers = iPreMod;
        this.instancePostModifiers = iPostMod;
        this.classPreModifiers = cPreMod;
        this.classPostModifiers = cPostMod;
    }
    
    public String getInstancePreModifiers() {
        return instancePreModifiers;
    }

    public String getInstancePostModifiers() {
        return instancePostModifiers;
    }

    public String getClassPreModifiers() {
        return classPreModifiers;
    }

    public String getClassPostModifiers() {
        return classPostModifiers;
    }

    @Override
    public String toString() {
        String tuple = String.format("%25s %s %-25s %25s %s %-25s",getInstancePreModifiers(), getInstanceLemma(), 
        		getInstancePostModifiers(),getClassPreModifiers(),getClassLemma(), getClassPostModifiers());
        String stats = String.format("%-8s %-3s %-5s %s %s %s", getAdjustedFrequency(), getPidSpread(), getPldSpread(), getPidsString(), getPldsString(), getProvids());       
        return String.format("%-175s %s", tuple, stats);
    }

}

