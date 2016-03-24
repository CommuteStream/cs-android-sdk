package com.commutestream.sdk;

import java.util.HashMap;
import java.util.Map;

/**
 * AdRequestQueryMap takes an AdRequest and converts it to a simple string mapping
 */
class AdRequestQueryMap {
    /**
     * Map converts an ad request object into a simple Map<String, String>
     * which can then be used to build a url encoded query string.
     * @param request
     * @return
     */
    static Map<String, String> Map(AdRequest request) {
        Map<String, String> params = new HashMap<>();
        if(request.isSkipFetch()) {
            params.put("skip_fetch", Boolean.toString(request.isSkipFetch()));
        }
        params.put("app_name", request.getAppName());
        params.put("app_ver", request.getAppVersion());
        maybe_put(params, "sdk_name", request.getSdkName());
        params.put("sdk_ver", request.getSdkVersion());
        params.put("aid_sha", request.getAidSha());
        params.put("aid_md5", request.getAidMd5());
        params.put("banner_height", Integer.toString(request.getBannerHeight()));
        params.put("banner_width", Integer.toString(request.getBannerWidth()));
        params.put("ad_unit_uuid", request.getAdUnitUuid());
        if (request.getLocation() != null) {
            params.put("lat", Double.toString(request.getLocation().getLatitude()));
            params.put("lon", Double.toString(request.getLocation().getLongitude()));
            params.put("acc", Float.toString(request.getLocation().getAccuracy()));
            params.put("fix_time", Long.toString(request.getLocation().getTime()));
        }
        if (request.isTesting()) {
            params.put("testing", Boolean.toString(request.isTesting()));
        }
        if(request.getAgencyInterests().size() > 0) {
            params.put("agency_interest", AgencyInterestCSVEncoder.Encode(request.getAgencyInterests()));
        }
        return params;
    }

    static void maybe_put(Map<String, String> params, String key, String value) {
        if(value != null) {
            params.put(key, value);
        }
    }
}
