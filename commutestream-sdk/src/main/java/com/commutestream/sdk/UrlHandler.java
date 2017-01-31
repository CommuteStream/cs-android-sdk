package com.commutestream.sdk;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Url Handler may be overridden and given to the WebClient to handle url's differently
 * than the WebViewClient normally handles them.
 *
 * By default we let android handle the url as if it were a normal link if there has been
 * interactivity. We then let a given listener also take action when that occurs.
 */
public class UrlHandler {
    private boolean mHandleClicks = false;
    private Context mContext;

    public UrlHandler(Context context) {
        mContext = context;
    }

    public boolean handleUrl(WebViewClient client, WebView view, String url) {
        if(mHandleClicks) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(i);
        } else {
            Log.w("CS_SDK", "Attempted to open intent without interaction from Ad, Ignoring");
        }
        return true;
    }

    /**
     * Enable url handling by starting an activity given the url. true if newly enabled. false otherwise.
     * @return
     */
    public boolean enable() {
        if(mHandleClicks) {
            return false;
        }
        mHandleClicks = true;
        return true;
    }
}
