public class MyActivity extends AppCompatActivity {

    private int layoutId;
    private NativeAd globalNativeAd;
    private ScrollView adScrollView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

      	........
      	........

        adScrollView = findViewById(R.id.scroll_view_ad);
        loadAd(getAdId());


		........
      	........
        
    }

    ........
    ........

    @Override
    public void onBackPressed() {

        if (isTaskRoot()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }else {
            finish();
        }

    }

    ///////NATIVE Ads
    private VideoOperator.VideoLifecycleListener videoLifecycleListener = new VideoOperator.VideoLifecycleListener() {
        @Override
        public void onVideoStart() {
            updateStatus(getString(R.string.status_play_start), false);
        }

        @Override
        public void onVideoPlay() {
            updateStatus(getString(R.string.status_playing), false);
        }

        @Override
        public void onVideoEnd() {
            // If a video exists, load a new native ad only after video playback is complete.
            updateStatus(getString(R.string.status_play_end), true);
        }
    };

    /**
     * Register and populate a native ad asset view.
     *
     * @param nativeAd  Native ad object that contains ad assets.
     * @param nativeView Native ad view to be populated.
     */
    private void initNativeAdView(NativeAd nativeAd, NativeView nativeView) {
        // Register a native ad asset view.
        nativeView.setTitleView(nativeView.findViewById(R.id.ad_title));
        nativeView.setMediaView((MediaView) nativeView.findViewById(R.id.ad_media));
        nativeView.setAdSourceView(nativeView.findViewById(R.id.ad_source));
        nativeView.setCallToActionView(nativeView.findViewById(R.id.ad_call_to_action));

        // Populate the native ad asset view. The native ad must contain the title and media assets.
        ((TextView) nativeView.getTitleView()).setText(nativeAd.getTitle());
        nativeView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        if (null != nativeAd.getAdSource()) {
            ((TextView) nativeView.getAdSourceView()).setText(nativeAd.getAdSource());
        }
        nativeView.getAdSourceView().setVisibility(null != nativeAd.getAdSource() ? View.VISIBLE : View.INVISIBLE);

        if (null != nativeAd.getCallToAction()) {
            ((Button) nativeView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }
        nativeView.getCallToActionView().setVisibility(null != nativeAd.getCallToAction() ? View.VISIBLE : View.INVISIBLE);

        // Obtain a video controller.
        VideoOperator videoOperator = nativeAd.getVideoOperator();

        // Check whether a native ad contains video assets.
        if (videoOperator.hasVideo()) {
            // Add a video lifecycle event listener.
            videoOperator.setVideoLifecycleListener(videoLifecycleListener);
        }

        // Register a native ad object.
        nativeView.setNativeAd(nativeAd);
    }

    /**
     * Update the message and obtain the button status.
     *
     * @param text             Message.
     * @param loadBtnEnabled Obtain the button status.
     */
    private void updateStatus(String text, boolean loadBtnEnabled) {

        // Meedi MSG for start and finish loading
        if (null != text) {
            //    Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
        }

    }


    private String getAdId() {
        String adId;

        adId = getString(R.string.imgad_native);
        layoutId = R.layout.native_small_template;


        return adId;
    }

    /**
     * Load a native ad.
     *
     * @param adId Ad unit ID.
     */
    private void loadAd(String adId) {

        updateStatus(null, false);

        NativeAdLoader.Builder builder = new NativeAdLoader.Builder(this, adId);

        builder.setNativeAdLoadedListener(new NativeAd.NativeAdLoadedListener() {
            @Override
            public void onNativeAdLoaded(NativeAd nativeAd) {
                // Called when an ad is successfully loaded.
                updateStatus(getString(R.string.status_load_ad_success), true);

                // Display a native ad.
                showNativeAd(nativeAd);

                nativeAd.setDislikeAdListener(new DislikeAdListener() {
                    @Override
                    public void onAdDisliked() {
                        // Called when an ad is closed.
                        updateStatus(getString(R.string.ad_is_closed), true);
                    }
                });
            }
        }).setAdListener(new AdListener() {
            @Override
            public void onAdFailed(int errorCode) {
                // Called when an ad fails to be loaded.
                updateStatus(getString(R.string.status_load_ad_fail) + errorCode, true);
            }
        });

        NativeAdConfiguration adConfiguration = new NativeAdConfiguration.Builder()
                // Set custom attributes.
                .setChoicesPosition(NativeAdConfiguration.ChoicesPosition.BOTTOM_RIGHT)
                .build();

        NativeAdLoader nativeAdLoader = builder.setNativeAdOptions(adConfiguration).build();

        nativeAdLoader.loadAd(new AdParam.Builder().build());

        updateStatus(getString(R.string.status_ad_loading), false);
    }

    /**
     * Display the native ad.
     *
     * @param nativeAd Native ad object that contains ad assets.
     */
    private void showNativeAd(NativeAd nativeAd) {
        // Destroy the original native ad.
        if (null != globalNativeAd) {
            globalNativeAd.destroy();
        }
        globalNativeAd = nativeAd;

        // Create NativeView.
        NativeView nativeView = (NativeView) getLayoutInflater().inflate(layoutId, null);

        // Populate NativeView.
        initNativeAdView(globalNativeAd, nativeView);

        // Add NativeView to the app UI.
        adScrollView.removeAllViews();
        adScrollView.addView(nativeView);
    }

    @Override
    public void onDestroy() {

        if (null != globalNativeAd) {
            globalNativeAd.destroy();
        }

        super.onDestroy();

    }
}
