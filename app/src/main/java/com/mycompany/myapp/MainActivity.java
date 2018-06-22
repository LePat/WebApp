package com.mycompany.myapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.*;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.*;
import android.webkit.*;
import android.view.*;

public class MainActivity extends Activity 
{
    
    final static String BASE_URL = "file:///storage/0000-0000/www/hello_react.html";
    
    WebView browser = null;
    
    Map<String, List<String>> menus = new HashMap<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle("WebApp");
        JavaScriptInterface javaScriptInterface = new JavaScriptInterface(getActionBar());
        WebView.setWebContentsDebuggingEnabled(true);
        browser = new WebView(this);
		browser.loadUrl(BASE_URL);
		setContentView(browser);
        browser.addJavascriptInterface(javaScriptInterface, "JSI");
		browser.getSettings().setJavaScriptEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        for(String menuJavascript: menus.keySet()) {
            menu.add(Menu.NONE, 0, Menu.NONE, menus.get(menuJavascript).get(0));
        }
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        System.out.println("menu item = " + menus.get(item.getTitle()).get(2));
        browser.loadUrl("javascript:" + menus.get(item.getTitle()).get(2) + "('" + this.toString() + "')");
        return true;
    }
    
    @Override
    public void onDestroy() {
        browser.removeJavascriptInterface("JSI");
        super.onDestroy();
    }
    
    private class JavaScriptInterface {
        ActionBar actionBar;
        public JavaScriptInterface(ActionBar actionBar) {
            this.actionBar = actionBar;
            System.out.println("actionBar: " + this.actionBar);
        }
        @JavascriptInterface
        public void setToolbarTitle(String title) {
            this.actionBar.setTitle(title);
        }
        @JavascriptInterface
        public void addToolbarMenu(String text, String iconUrl, String callback) {
            menus.put(text, Arrays.asList(new String[] {text, iconUrl, callback}));
            invalidateOptionsMenu();
        }
        @JavascriptInterface
        public void analyzePhoto(String imagePath) {
            Uri imageUri = Uri.parse("/storage/0000-0000/www/IMG_3766.jpg");
            imagePath = imageUri.getPath();
            try {
                final ExifInterface exifInterface = new ExifInterface(imagePath);
                float[] latLong = new float[2];
                if (exifInterface.getLatLong(latLong)) {
                    browser.loadUrl("javascript:" + "updateFromAndroid('" + "lat= " + latLong[0] + "lon= " + latLong[1] + "')");
                }
            } catch (Exception e) {
                System.out.println("Couldn't read exif info: " + e.getLocalizedMessage());
            }
        }
    }
}
