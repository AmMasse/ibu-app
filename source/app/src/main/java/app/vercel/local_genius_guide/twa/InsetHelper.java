package app.vercel.local_genius_guide.twa;;

import android.content.Context;
import android.webkit.WebView;
import androidx.core.graphics.Insets;

public class InsetHelper {
    private Context context;
    private int lastTopInset = 0;
    private int lastBottomInset = 0;
    private int lastLeftInset = 0;
    private int lastRightInset = 0;

    public InsetHelper(Context context) {
        this.context = context;
    }

    public void applyInsets(WebView webView, Insets systemBars, Insets displayCutout, Insets ime) {
        // Calculate safe area insets combining system bars, display cutout, and IME
        int topInset = Math.max(systemBars.top, displayCutout.top);
        int bottomInset = Math.max(Math.max(systemBars.bottom, displayCutout.bottom), ime.bottom);
        int leftInset = Math.max(systemBars.left, displayCutout.left);
        int rightInset = Math.max(systemBars.right, displayCutout.right);

        // Only update if insets have changed to avoid unnecessary WebView updates
        if (topInset != lastTopInset || bottomInset != lastBottomInset || 
            leftInset != lastLeftInset || rightInset != lastRightInset) {
            
            lastTopInset = topInset;
            lastBottomInset = bottomInset;
            lastLeftInset = leftInset;
            lastRightInset = rightInset;

            // Apply padding to WebView for safe areas
            webView.setPadding(leftInset, topInset, rightInset, bottomInset);
            
            // Inject CSS variables for safe areas
            injectSafeAreaVariables(webView);
        }
    }

    public void injectSafeAreaVariables(WebView webView) {
        // Convert pixels to CSS units (assuming density of 160dpi as baseline)
        float density = context.getResources().getDisplayMetrics().density;
        
        String topPx = (lastTopInset / density) + "px";
        String bottomPx = (lastBottomInset / density) + "px";
        String leftPx = (lastLeftInset / density) + "px";
        String rightPx = (lastRightInset / density) + "px";

        // JavaScript to inject CSS custom properties for safe areas
        String javascript = String.format(
            "(function() {" +
            "  var style = document.createElement('style');" +
            "  style.textContent = ':root {' +" +
            "    '--safe-area-inset-top: %s;' +" +
            "    '--safe-area-inset-bottom: %s;' +" +
            "    '--safe-area-inset-left: %s;' +" +
            "    '--safe-area-inset-right: %s;' +" +
            "    '--android-safe-top: %s;' +" +
            "    '--android-safe-bottom: %s;' +" +
            "    '--android-safe-left: %s;' +" +
            "    '--android-safe-right: %s;' +" +
            "  }';" +
            "  var existingStyle = document.querySelector('style[data-android-insets]');" +
            "  if (existingStyle) {" +
            "    existingStyle.remove();" +
            "  }" +
            "  style.setAttribute('data-android-insets', 'true');" +
            "  document.head.appendChild(style);" +
            "  window.dispatchEvent(new CustomEvent('android-insets-updated', {" +
            "    detail: { top: %d, bottom: %d, left: %d, right: %d }" +
            "  }));" +
            "})();",
            topPx, bottomPx, leftPx, rightPx,
            topPx, bottomPx, leftPx, rightPx,
            lastTopInset, lastBottomInset, lastLeftInset, lastRightInset
        );

        webView.evaluateJavascript(javascript, null);
    }
}