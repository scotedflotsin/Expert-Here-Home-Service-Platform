package www.experthere.in.helper;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class AdManager {
    private static final String TAG = "AdManager";
    private InterstitialAd mInterstitialAd;
    private final String adUnitId;

    public AdManager(String adUnitId) {
        this.adUnitId = adUnitId;
    }

    public void loadAd(Context context) {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(context, adUnitId, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                mInterstitialAd = interstitialAd;
                Log.i(TAG, "onAdLoaded");
                // Ad successfully loaded, can show immediately if needed
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.d(TAG, "onAdFailedToLoad: " + loadAdError);
                mInterstitialAd = null;
            }
        });
    }

    public void showAd(Activity activity) {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(activity);
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    Log.d(TAG, "The ad was dismissed.");
                    // Reload the ad after it is dismissed
                    loadAd(activity);
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    Log.d(TAG, "The ad failed to show.");
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    Log.d(TAG, "The ad was shown.");
                    mInterstitialAd = null; // Set the ad to null to ensure it doesn't show again
                }
            });
        } else {
            Log.d(TAG, "The interstitial ad wasn't ready yet.");
        }
    }

    public boolean isAdLoaded() {
        return mInterstitialAd != null;
    }
}
