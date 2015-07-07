package com.commutestream.ads;

/**
 * AdResponse contains the response data about an ad request
 */
class AdResponse {
    String html;
    String url;

    /**
     * Get the Ad URL
     * @return url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Get the Ad HTML to Embed in a WebView
     * @return html
     */
    public String getHtml() {
        return html;
    }
}
