package com.wear.streamer;

import android.provider.SyncStateContract;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class FileImportService extends WearableListenerService  {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Toast.makeText(this, messageEvent.getPath(), Toast.LENGTH_LONG).show();

    }
    /*
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Toast.makeText(this, "Success", Toast.LENGTH_LONG).show();
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED &&
                    event.getDataItem().getUri().getPath().equals("/wearstreamer")) {
                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                Asset profileAsset = dataMapItem.getDataMap().getAsset("opml");
            }
        }
    }
    */

}
