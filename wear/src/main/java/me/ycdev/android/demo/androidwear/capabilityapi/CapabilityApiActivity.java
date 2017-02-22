package me.ycdev.android.demo.androidwear.capabilityapi;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import me.ycdev.android.demo.androidwear.R;

public class CapabilityApiActivity extends WearableActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        CapabilityApi.CapabilityListener  {
    private static final String TAG = "CapabilityApiActivity";

    private static final String WELCOME_MESSAGE = "Welcome to our Wear app!\n\n";

    private static final String CHECKING_MESSAGE =
            WELCOME_MESSAGE + "Checking for Mobile app...\n";

    private static final String MISSING_MESSAGE =
            WELCOME_MESSAGE
                    + "You are missing the required phone app, please click on the button below to "
                    + "install it on your phone.\n";

    private static final String INSTALLED_MESSAGE =
            WELCOME_MESSAGE
                    + "Mobile app installed on your %s!\n\nYou can now use MessageApi, "
                    + "DataApi, etc.";

    // Name of capability listed in Phone app's wear.xml.
    // IMPORTANT NOTE: This should be named differently than your Wear app's capability.
    private static final String CAPABILITY_PHONE_APP = "verify_remote_example_phone_app";

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private BoxInsetLayout mContainerView;
    private TextView mInfoView;
    private TextView mClockView;

    private GoogleApiClient mGoogleApiClient;
    private Node mAndroidPhoneNodeWithApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_capability_api);
        setAmbientEnabled();

        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        mInfoView = (TextView) findViewById(R.id.info);
        mClockView = (TextView) findViewById(R.id.clock);

        mInfoView.setText(CHECKING_MESSAGE);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        int blackColor = ResourcesCompat.getColor(getResources(), android.R.color.black, null);
        int whiteColor = ResourcesCompat.getColor(getResources(), android.R.color.white, null);
        if (isAmbient()) {
            mContainerView.setBackgroundColor(blackColor);
            mInfoView.setTextColor(whiteColor);
            mClockView.setVisibility(View.VISIBLE);
            mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
        } else {
            mContainerView.setBackgroundColor(whiteColor);
            mInfoView.setTextColor(blackColor);
            mClockView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()");
        super.onPause();

        if ((mGoogleApiClient != null) && mGoogleApiClient.isConnected()) {
            Wearable.CapabilityApi.removeCapabilityListener(mGoogleApiClient, this,
                    CAPABILITY_PHONE_APP);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle connectionHint) {
        Log.d(TAG, "onConnected()");

        // Set up listeners for capability changes (install/uninstall of remote app).
        Wearable.CapabilityApi.addCapabilityListener(mGoogleApiClient, this, CAPABILITY_PHONE_APP);

        checkIfPhoneHasApp();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "onConnectionSuspended: " + cause);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.d(TAG, "onConnectionFailed: " + result);
    }

    @Override
    public void onCapabilityChanged(CapabilityInfo capabilityInfo) {
        Log.d(TAG, "onCapabilityChanged(): " + capabilityInfo);
        mAndroidPhoneNodeWithApp = pickBestNodeId(capabilityInfo.getNodes());
        verifyNodeAndUpdateUI();
    }

    private void checkIfPhoneHasApp() {
        Log.d(TAG, "checkIfPhoneHasApp()");

        PendingResult<CapabilityApi.GetCapabilityResult> pendingResult =
                Wearable.CapabilityApi.getCapability(mGoogleApiClient,
                        CAPABILITY_PHONE_APP, CapabilityApi.FILTER_ALL);

        pendingResult.setResultCallback(new ResultCallback<CapabilityApi.GetCapabilityResult>() {
            @Override
            public void onResult(@NonNull CapabilityApi.GetCapabilityResult result) {
                Log.d(TAG, "getCapability onResult: " + result.getStatus());
                if (result.getStatus().isSuccess()) {
                    CapabilityInfo capabilityInfo = result.getCapability();
                    Log.d(TAG, "capability: " + capabilityInfo.getName() + ", nodes: " + capabilityInfo.getNodes());
                    mAndroidPhoneNodeWithApp = pickBestNodeId(capabilityInfo.getNodes());
                    verifyNodeAndUpdateUI();
                } else {
                    Log.d(TAG, "Failed CapabilityApi: " + result.getStatus());
                }
            }
        });
    }

    private void verifyNodeAndUpdateUI() {
        if (mAndroidPhoneNodeWithApp != null) {
            String installMessage =
                    String.format(INSTALLED_MESSAGE, mAndroidPhoneNodeWithApp.getDisplayName());
            Log.d(TAG, installMessage);
            mInfoView.setText(installMessage);

        } else {
            Log.d(TAG, MISSING_MESSAGE);
            mInfoView.setText(MISSING_MESSAGE);
        }
    }

    private Node pickBestNodeId(Set<Node> nodes) {
        Node bestNode = null;
        // Find a nearby node or pick one arbitrarily
        for (Node node : nodes) {
            bestNode = node;
            if (node.isNearby()) {
                break;
            }
        }
        return bestNode;
    }
}
