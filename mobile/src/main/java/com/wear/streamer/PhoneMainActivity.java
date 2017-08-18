package com.wear.streamer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        ((TextView)findViewById(R.id.tv_version)).setText(BuildConfig.VERSION_NAME);

        findViewById(R.id.btn_import_opml).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alert = new AlertDialog.Builder(PhoneMainActivity.this);
                alert.setTitle("Warning");
                alert.setMessage("Importing podcasts will overwrite any existing podcasts.");
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("*/*");
                        startActivityForResult(intent, READ_REQUEST_CODE);
                    }
                });
                alert.setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

                alert.show();
            }
        });

        findViewById(R.id.btn_import_podcast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final TextView tvTitle = ((TextView) findViewById(R.id.tv_import_podcast_title));
                final TextView tvLink = ((TextView) findViewById(R.id.tv_import_podcast_link));

                String title = tvTitle.getText().toString();
                String link = tvLink.getText().toString();

                if (title.length() > 0 && link.length() > 0) {
                    link = link.startsWith("http") == false ? "http://" + link.toLowerCase() : link.toLowerCase();
                    if (isValidUrl(link)) {
                        PutDataMapRequest dataMap = PutDataMapRequest.create("/podcastimport");
                        dataMap.getDataMap().putString("title", title);
                        dataMap.getDataMap().putString("link", link);
                        dataMap.getDataMap().putLong("time", new Date().getTime());

                        PutDataRequest request = dataMap.asPutDataRequest();
                        Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                                    @Override
                                    public void onResult(DataApi.DataItemResult dataItemResult) {
                                        Toast.makeText(getApplicationContext(), "Podcast added successfully. Restart watch app.", Toast.LENGTH_LONG).show();
                                        tvTitle.setText(null);
                                        tvLink.setText(null);
                                    }
                                });
                    }
                    else
                        Toast.makeText(getApplicationContext(), "Enter a valid url", Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(getApplicationContext(), "Enter a title and url", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent resultData) {

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (mGoogleApiClient.isConnected()) new Thread(new Runnable() {
                @Override
                public void run() {
                    if (resultData != null) {
                        Uri uri = resultData.getData();

                        Asset asset = createAssetFromFile(uri);

                        PutDataMapRequest dataMap = PutDataMapRequest.create("/opmlimport");
                        dataMap.getDataMap().putAsset("opml", asset);
                        dataMap.getDataMap().putLong("time", new Date().getTime());

                        PutDataRequest request = dataMap.asPutDataRequest();
                        Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                                    @Override
                                    public void onResult(DataApi.DataItemResult dataItemResult) {
                                        Toast.makeText(getApplicationContext(), "Podcasts imported successfully. Restart watch app.", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                }
            }).start();
            else {
                Log.e(getPackageName(), "not connected");
            }
        }
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

        int len;
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

    private static boolean isValidUrl(String url) {
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url.toLowerCase());

        return m.matches();
    }

}