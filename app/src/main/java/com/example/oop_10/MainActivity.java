package com.example.oop_10;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> prevPages = new ArrayList<String>();
    ArrayList<String> nextPages = new ArrayList<String>();
    boolean goingToPrev = false, goingToNext = false;
    Button prevButton, nextButton;
    EditText addressBar;
    String prevCurrentUrl = "";
    WebView wv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addressBar = findViewById(R.id.addressBar);
        addressBar.setImeOptions(EditorInfo.IME_ACTION_GO);
        addressBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN)
                    || actionId == EditorInfo.IME_ACTION_GO) {
                    openPage("");
                }
                return true;
            }
        });
        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);
        wv = findViewById(R.id.webView);
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap bitmap) {
                // if user clicks link, clear previous next pages
                if (!goingToPrev && !goingToNext) {
                    if (!prevCurrentUrl.equals(url)) {
                        nextPages.clear();
                    }
                }

                // don't add the same page more than once to previous pages
                if (!goingToPrev && (prevPages.size() == 0 || !prevPages.get(prevPages.size()-1).equals(url))) {
                    prevPages.add(url);
                }

                prevCurrentUrl = url;
                goingToPrev = false;
                goingToNext = false;
                prevButton.setEnabled(true);
                nextButton.setEnabled(true);
            }
        });
        wv.getSettings().setJavaScriptEnabled(true);
    }

    public void shoutOut(View v) {
        wv.evaluateJavascript("javascript:shoutOut()", null);
    }

    public void initialize(View v) {
        wv.evaluateJavascript("javascript:initialize()", null);
    }

    public void refresh(View v) {
        wv.reload();
    }

    public void openPage(String url) {
        if (url.equals("")) {
            url = addressBar.getText().toString();
            nextPages.clear(); // if user inputs new url, clear "next" pages
        }

        if (url.equals("index.html")) {
            url = "file:///android_asset/index.html";
        } else if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }

        wv.loadUrl(url);
    }

    public void openPrev(View v) {
        int idx = prevPages.size() - 1; // last item is current page

        if (idx >= 1) {
            // If user presses buttons too fast, bugs can happen. Buttons are enabled after code
            // in onPageStarted is ready
            prevButton.setEnabled(false);
            nextButton.setEnabled(false);
            goingToPrev = true;
            nextPages.add(prevPages.get(idx)); // add current page to next pages
            openPage(prevPages.get(idx - 1));
            prevPages.remove(idx); // remove the previously current page
        }
    }

    public void openNext(View v) {
        int idx = nextPages.size() - 1;

        if (idx >= 0) {
            // If user presses buttons too fast, bugs can happen. Buttons are enabled after code
            // in onPageStarted is ready
            prevButton.setEnabled(false);
            nextButton.setEnabled(false);
            goingToNext = true;
            openPage(nextPages.get(idx));
            nextPages.remove(idx);
        }
    }
}
