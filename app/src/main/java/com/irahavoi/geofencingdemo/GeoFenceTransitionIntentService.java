package com.irahavoi.geofencingdemo;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by irahavoi on 2016-01-08.
 */
public class GeoFenceTransitionIntentService  extends IntentService{
    private static final String LOG_TAG = "geoFencingService";

    public GeoFenceTransitionIntentService(){
        super(LOG_TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if(geofencingEvent.hasError()){
            String errorMsg = GeofenceErrorMessages.getErrorString(this, geofencingEvent.getErrorCode());

            Log.e(LOG_TAG, errorMsg);
            return;
        }

        //Get the transition type:
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        //Only interested in enter and exit events.
        if(geofenceTransition  == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT){
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            String geofenceTransitionDetails = getGeofenceTransitionDetails(this, geofenceTransition, triggeringGeofences);

            sendNotification(geofenceTransitionDetails);
        } else{
            Log.e(LOG_TAG, getString(R.string.unknown_event_type, geofenceTransition));
        }

    }

    private String getGeofenceTransitionDetails(Context context, int transitionEventCode, List<Geofence> geofences){
        String geofenceTransitionString = getTransitionString(transitionEventCode);
        List<String> triggeringGeofencesIdsList = new ArrayList();

        for(Geofence geofence : geofences){
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }

        String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;

    }

    private String getTransitionString(int transitionCode){
        return transitionCode == Geofence.GEOFENCE_TRANSITION_ENTER ? "Enter" :
                transitionCode == Geofence.GEOFENCE_TRANSITION_EXIT ? "Exit" : "Unknown";
    }

    private void sendNotification(String notificationDetails){
        //Create an explicit content Intent that starts the main Activity:
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

        //Construct a task stack:
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        //Add the main activity to the stack as the parent
        stackBuilder.addParentStack(MainActivity.class);

        //Push the content intent into the stack
        stackBuilder.addNextIntent(notificationIntent);

        //Get a pending intent containing the entire back stack
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        //define the notification settings:
        builder.setSmallIcon(R.drawable.ic_place_black_24dp)
            //TODO: use library (volley) to decode resources
            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_place_black_24dp))
            .setColor(Color.RED)
            .setContentTitle(notificationDetails)
            .setContentText(getString(R.string.click_notification_to_return))
            .setContentIntent(notificationPendingIntent);


        builder.setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, builder.build());
    }
}
