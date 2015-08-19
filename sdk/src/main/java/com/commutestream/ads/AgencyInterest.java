package com.commutestream.ads;

/**
 * Agency Interest holds information about transit agency requests made.
 */
class AgencyInterest {
    static final String TRACKING_DISPLAYED = "TRACKING_DISPLAYED";
    static final String ALERT_DISPLAYED = "ALERT_DISPLAYED";
    static final String MAP_DISPLAYED = "MAP_DISPLAYED";
    static final String FAVORITE_ADDED = "FAVORITE_ADDED";
    static final String TRIP_PLANNING_POINT_A = "TRIP_PLANNING_POINT_A";
    static final String TRIP_PLANNING_POINT_B = "TRIP_PLANNING_POINT_B";

    String interest;
    String agencyID;
    String routeID;
    String stopID;

    AgencyInterest(String interest, String agencyID, String routeID, String stopID) {
        this.interest = interest;
        this.agencyID = agencyID;
        this.routeID = routeID;
        this.stopID = stopID;
    }
}
