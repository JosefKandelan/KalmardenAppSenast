package com.example.kolmardenapp;

        import android.app.IntentService;
        import android.content.Intent;
        import android.util.Log;

        import com.google.android.gms.location.Geofence;
        import com.google.android.gms.location.GeofencingEvent;

        import java.util.List;

/**
 * Created by andresdavid on 13/09/16.
 */
public class GeofenceService extends IntentService {

    protected static final String Tag = "GeofenceService";

    public GeofenceService() {

        super(Tag);

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if(geofencingEvent.hasError())
        {
            Log.e(Tag,"geofencingEvent hasError");
        }
        else{

            int transition = geofencingEvent.getGeofenceTransition();

            List<Geofence> geofences = geofencingEvent.getTriggeringGeofences();

            Geofence geofence = geofences.get(0);

            String requestId = geofence.getRequestId();

            if(transition == Geofence.GEOFENCE_TRANSITION_ENTER){

                Log.d(Tag,"Välkommen hem - " + requestId);

            }

            else if(transition == Geofence.GEOFENCE_TRANSITION_EXIT){

                Log.d(Tag, "Herrå - " + requestId);

            }
        }

    }
}