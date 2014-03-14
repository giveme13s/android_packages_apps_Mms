package com.android.mms.misc.smstasks;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import com.android.mms.R;
public class PhoneTrackerActivity extends Activity {  
 private static LocationManager locationManager=null;  
 private static LocationListener locationListener=null;   
 private static final String TAG = "";
 private Boolean flag = false;
public int MAX_SMS = 2;
public static int textsSent = 0;
public static Context context;
 @Override
 public void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
    setContentView(R.layout.smstasks_tracker_menu);
    context = smsReceiver.CONTEXT;
	TextView welcome = (TextView) findViewById(R.id.smstasks_tracker_welcome);
	welcome.setText("Welcome to SMS Tasks Phone Tracking Menu, This menu was Initiated by: "+smsReceiver.from + " To stop this command send the 'Stop' Command to this phone, if you are White-Listed, or contact that number listed");
   //if you want to lock screen for always Portrait mode
   setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
   GpsStatusON();
   locationManager = (LocationManager)  getSystemService(Context.LOCATION_SERVICE);  
   locationListener = new MyLocationListener();
   locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10,locationListener);  
 }
public static void remoteStop(){
locationManager.removeUpdates(locationListener);
GpsStatusOFF();
}
 public void GpsStatusON() {
  ContentResolver contentResolver = getBaseContext().getContentResolver();
  boolean gpsStatus = Settings.Secure.isLocationProviderEnabled(contentResolver, LocationManager.GPS_PROVIDER);  
  if (gpsStatus) {
  //already on
  } else {
   Toggles.Toggle("gps", true);
  }
 }
  public static void GpsStatusOFF() {
  ContentResolver contentResolver = context.getContentResolver();
  boolean gpsStatus = Settings.Secure.isLocationProviderEnabled(contentResolver, LocationManager.GPS_PROVIDER);  
  if (gpsStatus) {
  Toggles.Toggle("gps", false);
  } else {
  //already off
  }
 }
 /*----------Listener class to get coordinates ------------- */
 private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
		String txt2send = locationFormatForSite(location);
		while (textsSent < MAX_SMS){
		SendSMS.sendSMS(txt2send);
		textsSent++;
		}
		if (textsSent >= MAX_SMS){
        	remoteStop();//stops after it sends location set amount of times=)
		}
	}
        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }
        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {  
            // TODO Auto-generated method stub
        }
	public String locationFormatForSite(Location location) {
    	String sep = System.getProperty("line.separator");
        return ""+smsTasksSettings.smsTag+"Your phone is here: "+sep+"https://maps.google.com/maps?q="+location.getLatitude()+",+"+location.getLongitude()+sep+sep+"With accuracy of: "+location.getAccuracy()+" meters"+sep+"Provider: "+location.getProvider();
    	}
    }
}
