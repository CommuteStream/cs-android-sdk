package com.commutestream.ads;

import com.loopj.android.http.RequestParams;

/**
 * AdRequestEncoder takes an AdRequest and encodes it into URL parameters
 */
class AdRequestURLEncoder {
    static RequestParams Encode(AdRequest request) {
        RequestParams params = new RequestParams();
        params.put("skip_fetch", Boolean.toString(request.skipFetch));
        params.put("app_name", request.appName);
        params.put("app_ver", request.appVersion);
        params.put("sdk_name", request.sdkName);
        params.put("sdk_ver", request.sdkVersion);
        params.put("aid_sha", request.aidSha);
        params.put("aid_md5", request.aidMd5);
        params.put("banner_height", request.bannerHeight);
        params.put("banner_width", request.bannerWidth);
        params.put("ad_unit_uuid", request.adUnitUuid);
        if(request.location != null) {
            params.put("lat", request.location.getLatitude());
            params.put("lon", request.location.getLongitude());
            params.put("acc", request.location.getAccuracy());
            params.put("fix_time", request.location.getTime());
        }
        params.put("testing", request.testing);
        params.put("agency_interest", AgencyInterestCSVEncoder.Encode(request.agencyInterests));
        return params;
    }
}
