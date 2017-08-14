package com.wear.streamer;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class PhoneMainActivity extends Activity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private GoogleApiClient mGoogleApiClient = null;
    private static final int READ_REQUEST_CODE = 42;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();

        findViewById(R.id.btn_main_import).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(intent, READ_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent resultData) {

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            if(mGoogleApiClient.isConnected()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Uri uri = null;
                        if (resultData != null) {
                            uri = resultData.getData();

                            Asset asset = createAssetFromFile(uri);

                            PutDataMapRequest dataMap = PutDataMapRequest.create("/wearstreamer");
                            dataMap.getDataMap().putAsset("opml", asset);
                            dataMap.getDataMap().putLong("time", new Date().getTime());
                            PutDataRequest request = dataMap.asPutDataRequest();
                            Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                                    .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                                        @Override
                                        public void onResult(DataApi.DataItemResult dataItemResult) {
                                            Log.i(getPackageName(), "Sending image was successful: " + dataItemResult.getStatus().isSuccess());
                                        }
                                    });


                            /*

                        CapabilityApi.GetCapabilityResult result = Wearable.CapabilityApi.getCapability(mGoogleApiClient, "wear_streamer", CapabilityApi.FILTER_REACHABLE).await();

                        Set<Node> connectedNodes = result.getCapability().getNodes();

                        Wearable.MessageApi.sendMessage(mGoogleApiClient, pickBestNodeId(connectedNodes), "/wearstreamer", opml).setResultCallback(
                                new ResultCallback() {
                                    @Override
                                    public void onResult(Result result) {
                                        MessageApi.SendMessageResult sendMessageResult = (MessageApi.SendMessageResult) result;
                                        if (!sendMessageResult.getStatus().isSuccess()) {
                                            // Failed to send message
                                        }
                                    }
                                }
                        );

                    }
                */
                }}
                }).start();
            } else {
                Log.e(getPackageName(), "not connected");
            }
        }
    }

    private String pickBestNodeId(Set<Node> nodes) {
        String bestNodeId = null;
        // Find a nearby node or pick one arbitrarily
        for (Node node : nodes) {
            if (node.isNearby()) {
                return node.getId();
            }
            bestNodeId = node.getId();
        }
        return bestNodeId;
    }

    private Asset createAssetFromFile(Uri uri) {

        InputStream iStream = null;
        try {
            iStream = getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        byte[] inputData = new byte[0];
        try {
            inputData = getBytes(iStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Asset.createFromBytes(inputData);
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}