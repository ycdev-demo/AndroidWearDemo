package me.ycdev.android.demo.androidwear;

import android.util.Log;

import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;

public class AppWearableListenerService extends WearableListenerService {
    private static final String TAG = "AppWLService";

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        Log.d(TAG, "onDataChanged");
        for (DataEvent dataEvent : dataEventBuffer) {
            Log.d(TAG, "dataEvent, type=" + dataEvent.getType()
                    + ", uri=" + dataEvent.getDataItem().getUri());
        }
    }

    @Override
    public void onMessageReceived(MessageEvent msg) {
        Log.d(TAG, "onMessageReceived, sourceNode=" + msg.getSourceNodeId()
                + ", path=" + msg.getPath());
    }

    @Override
    public void onCapabilityChanged(CapabilityInfo info) {
        Log.d(TAG, "onCapabilityChanged: " + info);
    }

    @Override
    public void onPeerConnected(Node node) {
        Log.d(TAG, "onPeerConnected: " + node);
    }

    @Override
    public void onPeerDisconnected(Node node) {
        Log.d(TAG, "onPeerDisconnected: " + node);
    }

    @Override
    public void onConnectedNodes(List<Node> list) {
        Log.d(TAG, "onConnectedNodes: " + list);
    }

}
