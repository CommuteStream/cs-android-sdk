package com.commutestream.sdk;

/**
 * Agency Interest holds information about transit agency requests made.
 */
public class AgencyInterest {
    public static final String TRACKING_DISPLAYED = "TRACKING_DISPLAYED";
    public static final String ALERT_DISPLAYED = "ALERT_DISPLAYED";
    public static final String MAP_DISPLAYED = "MAP_DISPLAYED";
    public static final String FAVORITE_ADDED = "FAVORITE_ADDED";
    public static final String TRIP_PLANNING_POINT_A = "TRIP_PLANNING_POINT_A";
    public static final String TRIP_PLANNING_POINT_B = "TRIP_PLANNING_POINT_B";

    private String interest = "";
    private String agencyID = "";
    private String routeID = "";
    private String stopID = "";

    /**
     * Create an agency interest
     * @param interest Interest kind
     * @param agencyID transit agency identifier
     * @param routeID transit route identifier
     * @param stopID transit stop identifier
     */
    public AgencyInterest(String interest, String agencyID, String routeID, String stopID) {
        this.interest = interest;
        this.agencyID = agencyID;
        this.routeID = routeID;
        this.stopID = stopID;
    }

    /**
     * Get agency interest
     * @return interest Interest kind identifier
     */
    public String getInterest() {
        return interest;
    }

    /**
     * Get agency id
     * @return agencyID transit agency identifier
     */
    public String getAgencyID() {
        return agencyID;
    }

    /**
     * Get route id
     * @return routeID transit route identifier
     */
    public String getRouteID() {
        return routeID;
    }

    /**
     * Get stop id
     * @return stopID transit stop identifier
     */
    public String getStopID() {
        return stopID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AgencyInterest that = (AgencyInterest) o;

        if (interest != null ? !interest.equals(that.interest) : that.interest != null)
            return false;
        if (agencyID != null ? !agencyID.equals(that.agencyID) : that.agencyID != null)
            return false;
        if (routeID != null ? !routeID.equals(that.routeID) : that.routeID != null) return false;
        return !(stopID != null ? !stopID.equals(that.stopID) : that.stopID != null);

    }

    @Override
    public int hashCode() {
        int result = interest != null ? interest.hashCode() : 0;
        result = 31 * result + (agencyID != null ? agencyID.hashCode() : 0);
        result = 31 * result + (routeID != null ? routeID.hashCode() : 0);
        result = 31 * result + (stopID != null ? stopID.hashCode() : 0);
        return result;
    }
}
