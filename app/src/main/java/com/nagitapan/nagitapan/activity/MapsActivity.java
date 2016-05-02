package com.nagitapan.nagitapan.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nagitapan.nagitapan.R;
import com.nagitapan.nagitapan.service.GPSTracker;
import com.nagitapan.nagitapan.util.RootApplication;

public class MapsActivity extends FragmentActivity implements View.OnClickListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMyLocationChangeListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private TextView headingTxt1, headingTxt2;
    double currentLatitude, currentLongitude;
    double destinationLatitude, destinationLongitude;
    MarkerOptions currentMarker, destinationMarker;
    GPSTracker gps;
    private Circle mCircleDestination;
    private Button btnSetAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Initialize UI Components and Listeners
        initializeComponents();
        setFontFace();
        setHookListeners();

        gps = new GPSTracker(MapsActivity.this);
        // check if GPS enabled
        if (gps.canGetLocation()) {

            currentLatitude = gps.getLatitude();
            currentLongitude = gps.getLongitude();


            setUpMapIfNeeded();
            // \n is for new line
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + currentLatitude + "\nLong: " + currentLongitude, Toast.LENGTH_LONG).show();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
//        setUpMapIfNeeded();


    }

    /**
     * Initialize UI Components
     */
    private void initializeComponents() {
        headingTxt1 = (TextView) findViewById(R.id.app_name1);
        headingTxt2 = (TextView) findViewById(R.id.app_name2);
        btnSetAlarm = (Button) findViewById(R.id.btn_set_alarm);

        currentMarker = new MarkerOptions();
        destinationMarker = new MarkerOptions();
    }

    private void setFontFace() {
        headingTxt1.setTypeface(RootApplication.getInstance().getArkhip());
        headingTxt2.setTypeface(RootApplication.getInstance().getArkhip());
    }

    /**
     * UI Click Events Trigger
     */
    protected void setHookListeners() {
        btnSetAlarm.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            mMap.setOnMarkerDragListener(this);

            CameraPosition INIT = new CameraPosition.Builder().target(new LatLng(currentLatitude, currentLongitude)).zoom(12.5F) //.bearing(300F) // orientation
//                    .tilt(0F) // viewing angle
                    .build();

            // use map to move camera into position
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(INIT));

            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        double radiusInMeters = 1000.0;
        int strokeColor = 0xffff0000; //red outline
        int shadeColor = 0x44ff0000; //opaque red fill

        CircleOptions circleOptions = new CircleOptions().center(latLng).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
        mCircleDestination = mMap.addCircle(circleOptions);

        currentMarker.position(new LatLng(currentLatitude, currentLongitude))
                .draggable(false).title("You");
        destinationMarker.position(new LatLng(currentLatitude + 0.01, currentLongitude + 0.01))
                .draggable(true).title("Destionation")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mMap.addMarker(currentMarker);
        mMap.addMarker(destinationMarker);

    }


    private void drawMarkerWithCircle(LatLng position) {
        double radiusInMeters = 500.0;
        int strokeColor = 0xffff0000; //red outline
        int shadeColor = 0x44ff0000; //opaque red fill

//        CircleOptions circleOptions = new CircleOptions().center(position).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
//        mCircle = googleMap.addCircle(circleOptions);
//
//        MarkerOptions markerOptions = new MarkerOptions().position(position);
//        mMarker = googleMap.addMarker(markerOptions);
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        LatLng dragPosition = marker.getPosition();
        destinationLatitude = dragPosition.latitude;
        destinationLongitude = dragPosition.longitude;
        Log.i("info", "on drag end :" + destinationLatitude + " dragLong :" + destinationLongitude);
        Toast.makeText(getApplicationContext(), "Your Destination..!", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onMyLocationChange(Location location) {
        float[] distance = new float[5];
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                mCircleDestination.getCenter().latitude, mCircleDestination.getCenter().longitude, distance);

        if (distance[0] > mCircleDestination.getRadius()) {
            Toast.makeText(getBaseContext(), "Outside, distance from center: " + distance[0] + " radius: " + mCircleDestination.getRadius(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getBaseContext(), "Inside, distance from center: " + distance[0] + " radius: " + mCircleDestination.getRadius(), Toast.LENGTH_LONG).show();
        }


    }

    public void checkDistance() {
        float[] distance = new float[5];
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        Location.distanceBetween(destinationLatitude, destinationLongitude,
                mCircleDestination.getCenter().latitude, mCircleDestination.getCenter().longitude, distance);

        if (distance[0] > mCircleDestination.getRadius()) {
            Toast.makeText(getBaseContext(), "Outside, distance from center: " + distance[0] + " radius: " + mCircleDestination.getRadius(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getBaseContext(), "Inside, distance from center: " + distance[0] + " radius: " + mCircleDestination.getRadius(), Toast.LENGTH_LONG).show();
//            showNotification();
//            Notify();
            createNotification();
        }


    }

    private void Notify() {
        Intent notificationIntent = new Intent(this, NotificationView.class);
        Notification myNotication;
        PendingIntent pendingIntent = PendingIntent.getActivity(MapsActivity.this, 1, notificationIntent, 0);
//        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        NotificationManager manager;
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Notification.Builder builder = new Notification.Builder(MapsActivity.this);

        builder.setAutoCancel(false);
        builder.setTicker("this is ticker text");
        builder.setContentTitle("WhatsApp Notification");
        builder.setContentText("You have a new message");
        builder.setSmallIcon(R.drawable.icon_slp);

        //Vibration
        builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        //LED
        builder.setLights(Color.GREEN, 3000, 3000);
        //Ton
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        builder.setSound(uri);

        builder.setContentIntent(null);
        builder.setOngoing(true);
        builder.setSubText("This is subtext...");   //API level 16
        builder.setNumber(100);
        builder.build();

        myNotication = builder.getNotification();
        myNotication.flags = Notification.DEFAULT_LIGHTS & Notification.FLAG_AUTO_CANCEL;
        manager.notify(11, myNotication);
    }

    public void createNotification() {
        // Prepare intent which is triggered if the
        // notification is selected
//        Intent intent = new Intent(this, MapsActivity.class);
//        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        // Build notification
        // Actions are just fake
        Notification noti = new Notification.Builder(this)
                .setContentTitle("New mail from " + "test@gmail.com")
                .setContentText("Subject")
                .setSmallIcon(R.drawable.icon_slp)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setSound(uri)
                .setLights(Color.GREEN, 3000, 3000)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, noti);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_set_alarm:
                // Set Alarm button clicked
                checkDistance();
                break;

        }
    }
}
