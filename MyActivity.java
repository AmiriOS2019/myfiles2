/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.huawei.nativeadexample;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.VideoOperator;
import com.huawei.hms.ads.nativead.DislikeAdListener;
import com.huawei.hms.ads.nativead.MediaView;
import com.huawei.hms.ads.nativead.NativeAd;
import com.huawei.hms.ads.nativead.NativeAdConfiguration;
import com.huawei.hms.ads.nativead.NativeAdLoader;
import com.huawei.hms.ads.nativead.NativeView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the HUAWEI Ads SDK.
        HwAds.init(this);

        ////////////////////// 1 : Build a NativeAdLoader
        NativeAdLoader.Builder builder = new NativeAdLoader.Builder(this, "testb65czjivt9");
        builder.setNativeAdLoadedListener(new NativeAd.NativeAdLoadedListener() {

            @Override
            public void onNativeAdLoaded(NativeAd nativeAd) {

                // Called when an ad is loaded successfully.
                Toast.makeText(MainActivity.this, "Ad loading state: loaded successfully.", Toast.LENGTH_SHORT).show();

                // Obtain NativeView.
                NativeView nativeView = (NativeView) getLayoutInflater().inflate(R.layout.native_small_template, null);

                // Register and populate the native ad asset views.
                initNativeAdView(nativeAd, nativeView);

                // Add NativeView to the UI.
                ScrollView adScrollView = findViewById(R.id.scroll_view_ad);
                adScrollView.removeAllViews();
                adScrollView.addView(nativeView);
               
               // Destroy the original native ad.
               nativeAd.destroy();

            }
        }).setAdListener(new AdListener() {
            @Override
            public void onAdFailed(int errorCode) {
                // Called when an ad fails to be loaded.
                Toast.makeText(MainActivity.this, "Ad loading state: failed to be loaded. Error code: "+errorCode, Toast.LENGTH_SHORT).show();
            }

        });
        NativeAdLoader nativeAdLoader = builder.build();

        /////////////////// 2 : Load an Ad
        nativeAdLoader.loadAd(new AdParam.Builder().build());

    }

    ////////////////// 3 : Register and populate the assetView

    private void initNativeAdView(NativeAd nativeAd, NativeView nativeView) {

        // Register and populate the title view.
        nativeView.setTitleView(nativeView.findViewById(R.id.ad_title));
        ((TextView) nativeView.getTitleView()).setText(nativeAd.getTitle());
        // Register and populate the multimedia view.
        nativeView.setMediaView((MediaView) nativeView.findViewById(R.id.ad_media));
        nativeView.getMediaView().setMediaContent(nativeAd.getMediaContent());
        // Register and populate other asset views.
        nativeView.setAdSourceView(nativeView.findViewById(R.id.ad_source));
        nativeView.setCallToActionView(nativeView.findViewById(R.id.ad_call_to_action));
        if (null != nativeAd.getAdSource()) {
            ((TextView) nativeView.getAdSourceView()).setText(nativeAd.getAdSource());
        }
        nativeView.getAdSourceView()
                .setVisibility(null != nativeAd.getAdSource() ? View.VISIBLE : View.INVISIBLE);
        if (null != nativeAd.getCallToAction()) {
            ((Button) nativeView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }
        nativeView.getCallToActionView()
                .setVisibility(null != nativeAd.getCallToAction() ? View.VISIBLE : View.INVISIBLE);

        // Register the native ad object.
        nativeView.setNativeAd(nativeAd);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
