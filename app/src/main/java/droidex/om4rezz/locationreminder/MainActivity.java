package droidex.om4rezz.locationreminder;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

    Button btnShowLocation, btnSetLocation;
    SharedPreferences prefs;
    EditText etLat, etLong;

    Thread thCheckLocation;

    // GPSTracker class
    GPSTracker gps;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = this.getSharedPreferences(
                "droidex.om4rezz.locationreminder", Context.MODE_PRIVATE);

        btnShowLocation = (Button) findViewById(R.id.btnShowLocation);
        btnSetLocation = (Button) findViewById(R.id.btn_set_location);
        etLat = (EditText) findViewById(R.id.et_lat);
        etLong = (EditText) findViewById(R.id.et_lng);

        btnSetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs.edit().putFloat("lat", Float.parseFloat(etLat.getText().toString())).apply();
                prefs.edit().putFloat("lng", Float.parseFloat(etLong.getText().toString())).apply();
                Toast.makeText(getApplicationContext(), "New Location Saved", Toast.LENGTH_LONG).show();
                thCheckLocation.start();
                btnSetLocation.setEnabled(false);


            }
        });

        // show location button click event
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // create class object
                gps = new GPSTracker(MainActivity.this);

                // check if GPS enabled
                if (gps.canGetLocation()) {

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    // \n is for new line
                    Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                } else {
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }
            }
        });

        checkLocation();

    }

    private void checkLocation() {
        // create class object
        gps = new GPSTracker(MainActivity.this);

        thCheckLocation = new Thread(new Runnable() {
            @Override
            public void run() {
                while (1 == 1) {
                    double roundLat = 0;
                    double roundLng = 0;
                    double latitude = 0;
                    double longitude = 0;
                    double prefLat = 0;
                    double prefLng = 0;
                    double roundPrefLat = 0;
                    double roundPrefLng = 0;


                    if (gps.canGetLocation()) {

                        latitude = gps.getLatitude();
                        longitude = gps.getLongitude();

                        roundLat = ((int) (latitude * 1000)) / 1000.0;
                        roundLng = ((int) (longitude * 1000)) / 1000.0;

                        prefLat = prefs.getFloat("lat", 0);
                        prefLng = prefs.getFloat("lng", 0);

                        roundPrefLat = ((int) (prefLat * 1000)) / 1000.0;
                        roundPrefLng = ((int) (prefLng * 1000)) / 1000.0;



                        // \n is for new line
//                        Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                    } else {
                        // can't get location
                        // GPS or Network is not enabled
                        // Ask user to enable GPS/network in settings
                        gps.showSettingsAlert();
                    }
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // LOG JUST TO TEST
                    if(roundLat == roundPrefLat && roundLng == roundPrefLng){
                        Log.w("KEY","WOOOOOOOOOOOOOOOOOW..........!!!!!");
                        Notify("You've received new message", "عرض جديد");

                    }

                    Log.w("xxxxxxxxxxx", roundLat + " - " + roundLng);
                    Log.w("yyyyyyyyyyy", roundPrefLat+ " - " + roundPrefLng);
                    Log.w("zzzzzzzzzzz", latitude + " - " + longitude);
                }
            }
        });

    }


    private void Notify(String notificationTitle, String notificationMessage){
//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        @SuppressWarnings("deprecation")
//        Notification notification = new Notification(R.drawable.sat,"عرض خاص من كارفور المعادي خصومات 40%", System.currentTimeMillis());
//        Intent notificationIntent = new Intent(this,NotificationView.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,notificationIntent, 0);
//
//        notification.setLatestEventInfo(MainActivity.this, notificationTitle,notificationMessage, pendingIntent);
//        notificationManager.notify(9999, notification);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent("com.rj.notitfications.SECACTIVITY");

        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 1, intent, 0);

        Notification.Builder builder = new Notification.Builder(MainActivity.this);

        builder.setAutoCancel(false);
        builder.setTicker("عرض حصري من كارفور");
        builder.setContentTitle("خصومات 40%");
        builder.setContentText("خصومات كارفور المعادي");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentIntent(pendingIntent);
        builder.setOngoing(true);
        builder.setSubText("عرض حصري من كارفور...");   //API level 16
        builder.setNumber(100);
        builder.build();

        Notification myNotication = builder.getNotification();
        manager.notify(11, myNotication);


    }
}