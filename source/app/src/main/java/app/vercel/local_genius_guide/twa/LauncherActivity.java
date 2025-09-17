/*
 * Copyright 2020 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.vercel.local_genius_guide.twa;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.webkit.WebView;
import androidx.webkit.WebViewClient;
import androidx.webkit.WebSettings;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private InsetHelper insetHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Enable edge-to-edge display for Android 15+
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize inset helper
        insetHelper = new InsetHelper(this);
        
        // Setup WebView
        setupWebView();
        
        // Handle window insets for edge-to-edge display
        setupWindowInsets();
        
        // Configure status bar and navigation bar for modern Android
        configureSystemBars();
    }

    private void setupWebView() {
        webView = findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        
        // Enable JavaScript and modern web features
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        
        // Enable responsive design support
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        
        // Modern WebView settings for Android 15+
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setOffscreenPreRaster(true);
        
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Inject safe area CSS variables after page load
                insetHelper.injectSafeAreaVariables(webView);
            }
        });
        
        // Load your PWA
        webView.loadUrl("https://1d33d164-f4dc-4616-bb00-790b9869a384.lovableproject.com?forceHideBadge=true");
    }

    private void setupWindowInsets() {
        View rootView = findViewById(R.id.root_container);
        
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            // Get system bar insets for edge-to-edge display
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            androidx.core.graphics.Insets displayCutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout());
            androidx.core.graphics.Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            
            // Apply insets to WebView with proper safe areas
            insetHelper.applyInsets(webView, systemBars, displayCutout, ime);
            
            return insets;
        });
    }

    private void configureSystemBars() {
        // Modern approach for Android 15+ - replace deprecated SYSTEM_UI_FLAG_*
        WindowInsetsControllerCompat windowInsetsController = 
            ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        
        if (windowInsetsController != null) {
            // Configure transparent status bar and navigation bar
            getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
            getWindow().setNavigationBarColor(android.graphics.Color.TRANSPARENT);
            
            // Enable edge-to-edge content
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            );
            
            // Set system bar appearance based on app theme
            windowInsetsController.setAppearanceLightStatusBars(false);
            windowInsetsController.setAppearanceLightNavigationBars(false);
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }
}