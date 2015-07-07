package com.commutestream.ads;

import java.util.Collection;
import java.util.Iterator;

/**
 * Encode individual and collections of AgencyInterest entries into CSV
 */
class AgencyInterestCSVEncoder {
    static String Encode(AgencyInterest agencyInterest) {
        return agencyInterest.interest + "," + agencyInterest.agencyID + "," + agencyInterest.routeID + "," + agencyInterest.stopID;
    }

    static String Encode(Collection<AgencyInterest> agencyInterests) {
        StringBuilder builder = new StringBuilder();
        for(Iterator<AgencyInterest> iter = agencyInterests.iterator(); iter.hasNext();) {
            AgencyInterest agencyInterest = iter.next();
            builder.append(Encode(agencyInterest));
            if(iter.hasNext()) {
                builder.append(",");
            }
        }
        return builder.toString();
    }
}
