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

import java.util.Arrays;
import java.util.HashSet;

/**
 * This class defines a core tuple. A Core tuple is an aggregation of all
 * regular tuples using the core noun of the instance and the core noun of a
 * class as aggregation criteria.
 *
 * @author Julian Seitener, Nacho Vidal and Stefano Faralli
 *
 */
public class CoreTuple {

    private String instanceLemma;
    private String classLemma;
    private Double adjustedFrequency;
    private int pidSpread;
    private int pldSpread;
    private String provids;

    private HashSet<String> pids;
    private HashSet<String> plds;

    /**
     *
     * @param ilem : Is the lemmatized core noun of the instance.
     * @param clem : Is the lemmatized core noun of the class
     * @param adjFreq : Is the frequency of the tuple
     * @param pidSpread : Is pattern id spread, which describes how many
     * distinct patterns found this tuple
     * @param pldSpread : Is the pay level domain spread, which describes on how
     * many different top level domains the tuple was found
     * @param pids : Contains a set of pattern IDs separated by a semicolon
     * Example: "p1;p2;p3a"
     * @param plds: Contains a set of pay level domains separated by a semicolon
     * Example: "google.com;wikipedia.org"
     * @param provids: Contains a set of provenance IDs separated by a semicolon
     * Example: "378458;9783456;934875"
     */
    public CoreTuple(
            String ilem, String clem,
            Double adjFreq, int pidSpread, int pldSpread,
            String pids, String plds,
            String provids
    ) {
        this.instanceLemma = ilem;
        this.classLemma = clem;
        this.pidSpread = pidSpread;
        this.pldSpread = pldSpread;
        this.adjustedFrequency = adjFreq;
        this.provids = provids;
        this.pids = new HashSet<>();
        this.plds = new HashSet<>();
        this.pids.addAll(Arrays.asList(pids.split(";")));
        this.plds.addAll(Arrays.asList(plds.split(";")));
    }

    public String getInstanceLemma() {
        return instanceLemma;
    }

    public String getClassLemma() {
        return classLemma;
    }

    public Double getAdjustedFrequency() {
        return adjustedFrequency;
    }

    public int getPidSpread() {
        return pidSpread;
    }

    public int getPldSpread() {
        return pldSpread;
    }

    public void setAdjustedFrequency(double adjustedFrequency) {
        this.adjustedFrequency = adjustedFrequency;
    }

    @Override
    public String toString() {
        String tuple = String.format("%-50s %s", getInstanceLemma(), getClassLemma());
        String stats = String.format("%-8s %-3s %-5s %s %s %s", getAdjustedFrequency(), getPidSpread(), getPldSpread(), getPidsString(), getPldsString(), getProvids());
        return String.format("%-100s %s", tuple, stats);
    }

    public HashSet<String> getPids() {
        return pids;
    }

    public HashSet<String> getPlds() {
        return plds;
    }

    public String getProvids() {
        return provids;
    }

    public String getPidsString() {
        StringBuilder sb = new StringBuilder();
        for (String pid : pids) {
            sb.append(pid).append(";");
        }
        return sb.toString();
    }

    public String getPldsString() {
        StringBuilder sb = new StringBuilder();
        for (String pld : plds) {
            sb.append(pld).append(";");
        }
        return sb.toString();
    }
}
