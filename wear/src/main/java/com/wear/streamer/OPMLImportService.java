package com.wear.streamer;

import android.content.ContentValues;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class OPMLImportService extends WearableListenerService {
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
    public void onDataChanged(DataEventBuffer dataEvents) {
        new DBPodcasts(getApplicationContext()).deleteAll();
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED && event.getDataItem().getUri().getPath().equals("/opmlimport")) {
                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                Asset asset = dataMapItem.getDataMap().getAsset("opml");

                InputStream in = Wearable.DataApi.getFdForAsset(mGoogleApiClient, asset).await().getInputStream();
                mGoogleApiClient.disconnect();

                List<PodcastItem> podcasts = OPMLParser.parse(in);

                for(PodcastItem podcast : podcasts) {
                    ContentValues cv = new ContentValues();
                    cv.put("title", podcast.getTitle());
                    cv.put("link", podcast.getLink().toString());
                    cv.put("dateAdded", GetDate());

                    new DBPodcasts(getApplicationContext()).insert(cv);
                }
            }
        }
    }

    private String GetDate()
    {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        return df.format(c.getTime());
    }

}
