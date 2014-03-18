package com.android.mms.misc.smstasks;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.*;
import com.android.mms.R;
/*
*       This following class is NOT TO CHANGE! it is needed for 
*       the how-to button to propperly sync with the github webserver
*       and display the how-to guide to use this implementation proppperly!
*       This is displayed information is the only required thing in this
*       I DO NOT WANT CHANGED! 
*	only exceptions to change in this is className(s), packageName,
*	and what id the TextView+WebView is, other then that have free will =)
*/

public class howTo extends Activity {
    private class MyWebViewClient extends WebViewClient {
          public boolean shouldOverrideUrlLoading(WebView view, String url) {
              view.loadUrl(url);
              return true;
          }
    }
    WebView webview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smstasks_webviewlayout);
        webview=(WebView)findViewById(R.id.smsTasks_webview);
        webview.setWebViewClient(new MyWebViewClient());
        openURL();
    }
     /** Openss Github Server in a custom browser for a synced how-to file */
    private void openURL() {
        webview.loadUrl("https://raw.github.com/kittleapps/how-to/master/how-to-use-smstasks.txt");
        webview.requestFocus();
    }
}
/*
*		END of Required CLASS placement
*
*
*/
