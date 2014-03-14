package com.android.mms.misc.smstasks;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;

public class Toggles {
	public static WifiManager wifiManager;
	public static Context context = smsReceiver.CONTEXT;
	public static void SetWIFI(boolean status){
	wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
	wifiManager.setWifiEnabled(status);
	}
	private static final int WIFI_AP_STATE_FAILED = 4;
	private final String TAG = "Wifi Access Manager";
	private static Method wifiControlMethod;
	private static Method wifiApConfigurationMethod;
	private static Method wifiApState;
	public static void WifiAP(boolean Value) throws SecurityException, NoSuchMethodException {
	wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	wifiControlMethod = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class,boolean.class);
	wifiApConfigurationMethod = wifiManager.getClass().getMethod("getWifiApConfiguration",(java.lang.Class<?>[])null);
	wifiApState = wifiManager.getClass().getMethod("getWifiApState");
     	try {
		setWifiApState((WifiConfiguration)wifiApConfigurationMethod.invoke(wifiManager, (java.lang.Object[])null), Value);
	} catch (IllegalArgumentException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IllegalAccessException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InvocationTargetException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    }
    public static boolean setWifiApState(WifiConfiguration config, boolean enabled) {
     try {
      if (enabled) {
          wifiManager.setWifiEnabled(!enabled);
      }
      return (Boolean) wifiControlMethod.invoke(wifiManager, config, enabled);
     } catch (Exception e) {
      return false;
     }
    }
    public WifiConfiguration getWifiApConfiguration()
    {
        try{
            return (WifiConfiguration)wifiApConfigurationMethod.invoke(wifiManager, (java.lang.Object[])null);
    }
        catch(Exception e){
            return null;
        }
    }
    public int getWifiApState() {
     try {
          return (Integer)wifiApState.invoke(wifiManager);
     } catch (Exception e) {
          return WIFI_AP_STATE_FAILED;
     }
    }
	public static void SetGps(boolean status){
		if (status){
			turnGpsOn();
		}
		else{
			turnGpsOff();
		}
	}
	public static void SetBluetooth(boolean status){
                if (status){
                        turnBluetoothOn();
                }
                else{
                        turnBluetoothOff();
                }
        }

	public static void turnBluetoothOn(){
                 BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                 mBluetoothAdapter.enable();
	}
	public static void turnBluetoothOff(){
                 BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                 mBluetoothAdapter.disable();
	}
	public static String beforeEnable = "";
	private static void turnGpsOn()
	{
	    beforeEnable = Settings.Secure.getString (context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
	    String newSet = String.format ("%s,%s", beforeEnable, LocationManager.GPS_PROVIDER);
	    try
	    {
	        Settings.Secure.putString (context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED, newSet);
	    }
	    catch(Exception e)
	    {
	    }
	}
	private static void turnGpsOff()
	{

	    if (null == beforeEnable)
	    {
	        String str = Settings.Secure.getString (context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
	        if (null == str)
	        {
	            str = "";
	        }
	        else
	        {
	            String[] list = str.split (",");
	            str = "";
	            int j = 0;
	            for (int i = 0; i < list.length; i++)
	            {
	                if (!list[i].equals (LocationManager.GPS_PROVIDER))
	                {
	                    if (j > 0)
	                    {
	                        str += ",";
	                    }
	                    str += list[i];
	                    j++;
	                }
	            }
	            beforeEnable = str;
	        }
	    }
	    try
	    {
	        Settings.Secure.putString (context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED, beforeEnable);
	    }
	    catch(Exception e)
	    {
	    }
	}
	public static void Toggle(String Toggle, boolean Value){
		Toggle.toLowerCase();
		if (Toggle.equalsIgnoreCase("")){
		}
		if (Toggle.equalsIgnoreCase("Wifi")){
			SetWIFI(Value);
		}
		if (Toggle.equalsIgnoreCase("GPS")){
			SetGps(Value);
		}
		if (Toggle.equalsIgnoreCase("Bluetooth")){
			SetBluetooth(Value);
		}
		if (Toggle.equalsIgnoreCase("Hotspot")){
			try {
				WifiAP(Value);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
		}
	}
}
