package me.ycdev.android.demo.androidwear;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import me.ycdev.android.demo.androidwear.capabilityapi.CapabilityApiActivity;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.capability_api).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.capability_api) {
            Intent intent = new Intent(this, CapabilityApiActivity.class);
            startActivity(intent);
        }
    }
}
