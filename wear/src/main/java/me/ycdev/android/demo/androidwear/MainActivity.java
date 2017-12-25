package me.ycdev.android.demo.androidwear;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.phone.PhoneDeviceType;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.ycdev.android.demo.androidwear.capabilityapi.CapabilityApiActivity;

public class MainActivity extends WearableActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private BoxInsetLayout mContainerView;
    private TextView mClockView;
    private Button mCapabilityApiBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        mContainerView = findViewById(R.id.container);
        mClockView = findViewById(R.id.clock);
        mCapabilityApiBtn = findViewById(R.id.capability_api);
        mCapabilityApiBtn.setOnClickListener(this);

        Log.i(TAG, "paired type: " + getPhoneDevieType());
    }

    private String getPhoneDevieType() {
        int type = PhoneDeviceType.getPhoneDeviceType(this);
        if (type == PhoneDeviceType.DEVICE_TYPE_ANDROID) {
            return "Android";
        } else if (type == PhoneDeviceType.DEVICE_TYPE_IOS) {
            return "iOS";
        } else {
            return "unknown";
        }
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
        if (isAmbient()) {
            mContainerView.setBackgroundColor(blackColor);
            mCapabilityApiBtn.setVisibility(View.GONE);
            mClockView.setVisibility(View.VISIBLE);
            mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
        } else {
            mContainerView.setBackground(null);
            mCapabilityApiBtn.setVisibility(View.VISIBLE);
            mClockView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == mCapabilityApiBtn) {
            Intent intent = new Intent(this, CapabilityApiActivity.class);
            startActivity(intent);
        }
    }
}
