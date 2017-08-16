package com.wear.streamer;

import android.content.ContentValues;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ImportService extends WearableListenerService {
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
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED && event.getDataItem().getUri().getPath().equals("/podcastimport")) {
                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());

                ContentValues cv = new ContentValues();
                cv.put("title", dataMapItem.getDataMap().getString("title"));
                cv.put("url", dataMapItem.getDataMap().getString("link"));
                cv.put("dateAdded", Utilities.GetDate());

                new DBPodcasts(getApplicationContext()).insert(cv);
            }

            if (event.getType() == DataEvent.TYPE_CHANGED && event.getDataItem().getUri().getPath().equals("/opmlimport")) {
                new DBPodcasts(getApplicationContext()).deleteAll();
                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                Asset asset = dataMapItem.getDataMap().getAsset("opml");

                InputStream in = Wearable.DataApi.getFdForAsset(mGoogleApiClient, asset).await().getInputStream();
                mGoogleApiClient.disconnect();

                new DBPodcasts(getApplicationContext()).insert(OPMLParser.parse(in));
            }
        }
    }


}
