package com.irahavoi.geofencingdemo;

import android.content.Context;
import android.content.res.Resources;

import com.google.android.gms.location.GeofenceStatusCodes;

/**
 * Created by irahavoi on 2016-01-09.
 */
public class GeofenceErrorMessages {
    private GeofenceErrorMessages(){}

    public static String getErrorString(Context context, int errorCode){
        Resources resources = context.getResources();

        switch (errorCode){
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return context.getString(R.string.goefence_service_not_available);
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return context.getString(R.string.geofence_too_many_geofences);
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return context.getString(R.string.geofence_too_many_pending_intents);
            default:
                return context.getString(R.string.unknown_error);
        }
    }
}
