package com.commutestream.sdk;

/**
 * AdResponse contains the response data about an ad request
 */
public class AdResponse {

    private String banner_request_uuid;
    private boolean item_returned;
    private String error;
    private String html;
    private String url;

    public String getBannerRequestUuid() {
        return banner_request_uuid;
    }

    public boolean isItemReturned() {
        return item_returned;
    }

    /**
     * Get the Ad URL
     *
     * @return url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Get the Ad HTML to Embed in a WebView
     *
     * @return html
     */
    public String getHtml() {
        return html;
    }

    /**
     * Get the Ad Error if any
     *
     * @return error
     */
    public String getError() { return error; }

    @Override
    public String toString() {
        return "AdResponse{" +
                "banner_request_uuid='" + banner_request_uuid + '\'' +
                ", item_returned=" + item_returned +
                ", error='" + error + '\'' +
                ", html='" + html + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
