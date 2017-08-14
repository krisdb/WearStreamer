package com.wear.streamer;

import android.os.Environment;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class FileImportService extends WearableListenerService {
    private GoogleApiClient mGoogleApiClient = null;

    @Override
    public void onCreate()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();

        super.onCreate();
    }

    @Override
    public void onMessageReceived(final MessageEvent message) {

        try {
            //byte[] decodestring = Base64.decode(message.getData(), Base64.DEFAULT);
            File file = Environment.getExternalStorageDirectory();
            File dir = new File(file.getAbsolutePath() + "/");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File document = new File(dir, "podcasts.opml");

            if (document.exists()) {
                document.delete();
            }

            FileOutputStream fos = new FileOutputStream(document.getPath());
            fos.write(message.getData());
            fos.close();
        }
        catch(Exception ex)
        {
            Log.e(getPackageName(), ex.getMessage());
        }

        /*
        Handler mHandler = new Handler(getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                byte[] test = messageEvent.getData();
                Toast.makeText(getApplicationContext(), messageEvent.getData().toString(), Toast.LENGTH_LONG).show();
            }
        });
        Log.i(getPackageName(), "success on wear");
        */
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED && event.getDataItem().getUri().getPath().equals("/wearstreamer")) {
                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                Asset asset = dataMapItem.getDataMap().getAsset("opml");

                InputStream in = Wearable.DataApi.getFdForAsset(mGoogleApiClient, asset).await().getInputStream();
                mGoogleApiClient.disconnect();

                List<PodcastItem> podcasts = OPMLParser.parse(in);
            }
        }
    }

}
