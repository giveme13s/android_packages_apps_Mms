package com.android.mms.misc.smstasks;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.telephony.SmsManager;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.Toast;
import com.android.mms.R;
public class smsReceiver extends BroadcastReceiver {
	public static boolean active;
	public static String secret = "null";
	public static SharedPreferences pref;
	public static Context CONTEXT;
	public static String from;
	public static String txt;
	public static boolean AlarmActive = false;
	public static boolean HIDDEN = false;
	public static boolean Locate_Enabled = false;
	public static String LockedFrom;
	public static String LockedPassword;
	public String[] PASS = {
			"unlock@45132","unlock@22457",
			"unlock@19372","unlock@46581",
			"unlock@34192","unlock@15424",
			"unlock@38291","unlock@97878",
			"unlock@57293","unlock@35656",
			"unlock@91263","unlock@45245",
			"unlock@18273","unlock@21021",
			"unlock@47283","unlock@33468",
			"unlock@28173","unlock@11345",
			"unlock@27183","unlock@32315",
			"unlock@27165","unlock@55487",
			"unlock@92384","unlock@88457",
			"unlock@82633","unlock@88754",
			"unlock@27183","unlock@99458",
			"unlock@27189","unlock@33458",
			"unlock@54726","unlock@99784",
			"unlock@37612","unlock@33658",
			"unlock@21782","unlock@12487",
			"unlock@48415","unlock@32345",
			"unlock@81241","unlock@88754",
			"unlock@78451","unlock@55487",
			"unlock@75121","unlock@22154",
			"unlock@79821","unlock@99487",
			"unlock@84215","unlock@66785",
			"unlock@95622","unlock@99457",
			"unlock@84121","unlock@88452",
			"unlock@84121","unlock@88457",
			"unlock@78412","unlock@33158",
			"unlock@82157","unlock@77845",
			"unlock@84125","unlock@11554",
			"unlock@81215","unlock@00248",
			"unlock@82155","unlock@88457",
			"unlock@94245","unlock@88457",
			"unlock@88436","unlock@44578",
			"unlock@97512","unlock@11247",
			"unlock@87213","unlock@99458",
			"unlock@84215","unlock@33457",
			"unlock@02158","unlock@11548",
			"unlock@84215","unlock@15548",
			"unlock@80245","unlock@00211",
			"unlock@41554","unlock@51224",
	};
	public static final String MMS_RECEIVED = "android.provider.Telephony.MMS_RECEIVED";
	public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	public static final String MMS_DATA_TYPE = "application/vnd.wap.mms-message";
	public void onReceive(Context context, Intent intent) {
			CONTEXT = context;
			pref = PreferenceManager.getDefaultSharedPreferences(CONTEXT);
			active = pref.getBoolean("smstasks_service_active", true);
			secret = pref.getString("smstasks_secret_text", "").toLowerCase();
		if(active && secret.length() > 0) {
			Bundle bundle = intent.getExtras();
	        if (bundle != null)
	        {
		        Object[] pdus = (Object[]) bundle.get("pdus");
	            for (int i = 0; i < pdus.length; i++) {
					SmsMessage msg = SmsMessage.createFromPdu((byte[])pdus[i]);
					from = msg.getOriginatingAddress();
					txt = msg.getMessageBody().toString();
					String action = intent.getAction();
					String type = intent.getType();
					if((pref.getString("whitelisted_number1", "").equalsIgnoreCase("*"))
							||(pref.getString("whitelisted_number2", "").equalsIgnoreCase("*"))
							||((pref.getString("whitelisted_number2", "").equalsIgnoreCase("*")) && (pref.getString("whitelisted_number1", "").equalsIgnoreCase("*")))
							||(from.equals(pref.getString("whitelisted_number1", "")))
							||(from.equals(pref.getString("whitelisted_number2", "")))
							||(from.equalsIgnoreCase(pref.getString("whitelisted_email", "")))){
						if(txt.toLowerCase().equalsIgnoreCase("off@"+pref.getString("smstasks_secret_text", "").toLowerCase())) {
											Log.d(smsTasksSettings.LOG_TAG, "Got SMS with Turn Off Pass Phrase " + from);
											Toast.makeText(CONTEXT, "Power Off Command Received.", Toast.LENGTH_LONG).show();
											TurnOff();
							}
							else if((txt.toLowerCase().startsWith("cmd@"+pref.getString("smstasks_secret_text", "").toLowerCase()))||
								    (txt.toLowerCase().startsWith("Cmd@"+pref.getString("smstasks_secret_text", "").toLowerCase()))){
											Log.d(smsTasksSettings.LOG_TAG, "Got SMS with Terminal Pass Phrase " + from);
											String CMD = txt.replaceFirst("cmd@"+pref.getString("smstasks_secret_text", "")+" ", "");
											String[] splitCMD = CMD.split("/n");
								if (splitCMD.length >= 0){
								for (int u = 0; u < splitCMD.length; u++){
											Toast.makeText(CONTEXT, "CMD Command Received.", Toast.LENGTH_LONG).show();
											CMD(splitCMD[u]);
								}
								}
								if (splitCMD.length <= 0){
											//Returns with no results makes the void cancel
								}
							}
							else if(txt.toLowerCase().equalsIgnoreCase("lock@"+pref.getString("smstasks_secret_text", "").toLowerCase())) {
											DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
											int Value = (int) (PASS.length*Math.random());
											String PASSWORD = PASS[Value];
											devicePolicyManager.resetPassword(PASSWORD, 0);
											SendSMS.sendSMS("<SMS TASKS>: Your UNLOCK Code is Now: "+PASSWORD);
											devicePolicyManager.lockNow();
							}
							else if((txt.toLowerCase().startsWith("lock@"+pref.getString("smstasks_secret_text", "").toLowerCase()))&&(!txt.toLowerCase().equalsIgnoreCase("lock@"+pref.getString("smstasks_secret_text", "").toLowerCase()))){
											DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
											int Value = (int) (PASS.length*Math.random());
											String PASSWORD = PASS[Value];
											String String_Value = txt.replaceFirst("lock@"+pref.getString("smstasks_secret_text", "").toLowerCase()+" ", "");
										if (checkAllSpaces(String_Value)){
											Value = (int) (PASS.length*Math.random());
											PASSWORD = PASS[Value];
											SendSMS.sendSMS(smsTasksSettings.smsTag+" Your UNLOCK Code is Now: "+PASSWORD);
										}
										if (!checkAllSpaces(String_Value)){
											PASSWORD = replaceAllFrontSpaces(String_Value);;
											SendSMS.sendSMS(smsTasksSettings.smsTag+" Your Custom UNLOCK Code is Now: "+PASSWORD);
										}
											devicePolicyManager.resetPassword(PASSWORD, 0);
											devicePolicyManager.lockNow();
							}
							else if((txt.toLowerCase().startsWith("Lock@"+pref.getString("smstasks_secret_text", "").toLowerCase()))&&(!txt.toLowerCase().equalsIgnoreCase("Lock@"+pref.getString("smstasks_secret_text", "").toLowerCase()))){
											DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
											int Value = (int) (PASS.length*Math.random());
											String PASSWORD = PASS[Value];
											String String_Value = txt.replaceFirst("Lock@"+pref.getString("smstasks_secret_text", "").toLowerCase()+" ", "");
										if (checkAllSpaces(String_Value)){
											Value = (int) (PASS.length*Math.random());
											PASSWORD = PASS[Value];
											SendSMS.sendSMS(smsTasksSettings.smsTag+" Your UNLOCK Code is Now: "+PASSWORD);
										}
										if (!checkAllSpaces(String_Value)){
											PASSWORD = replaceAllFrontSpaces(String_Value);;
											SendSMS.sendSMS(smsTasksSettings.smsTag+" Your Custom UNLOCK Code is Now: "+PASSWORD);
										}
											devicePolicyManager.resetPassword(PASSWORD, 0);
											devicePolicyManager.lockNow();
							}
							else if(txt.toLowerCase().equalsIgnoreCase("snooze@"+pref.getString("smstasks_secret_text", "").toLowerCase())) {
										if(smsTasksSettings.player.isPlaying()){
											smsTasksSettings.player.pause();
								}
							}
							else if(txt.toLowerCase().equalsIgnoreCase("data@"+pref.getString("smstasks_secret_text", "").toLowerCase())) {
											DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
											SendSMS.sendSMS(smsTasksSettings.smsTag+" Data will be wiped here shortly. Regular results of a data wipe will occur, so hope you kept a backup ;)");
											devicePolicyManager.wipeData(0);
							}
							else if(txt.toLowerCase().startsWith("echo")) {
											String Remainder1 = txt.replaceFirst("echo ", "");
											String Remainder2 = Remainder1.replace("%pass%", ""+pref.getString("smstasks_secret_text", "").toLowerCase()+"");
											String[] Remainder3 = Remainder2.split("\n");
											String separator = System.getProperty("line.separator");
											StringBuilder builder = new StringBuilder(Remainder3[0]);
								for (int b = 1; b < Remainder3.length; b++) {
								    			builder.append(separator).append(Remainder3[b]);
								}
											String Remainder4 = builder.toString();
											Toast.makeText(CONTEXT, Remainder4+".", Toast.LENGTH_LONG).show();
							}
							else if(txt.toLowerCase().startsWith("Echo")) {
											String Remainder1 = txt.replaceFirst("Echo ", "");
											String Remainder2 = Remainder1.replace("%pass%", ""+pref.getString("smstasks_secret_text", "").toLowerCase()+"");
											String[] Remainder3 = Remainder2.split("\n");
											String separator = System.getProperty("line.separator");
											StringBuilder builder = new StringBuilder(Remainder3[0]);
								for (int b = 1; b < Remainder3.length; b++) {
								    			builder.append(separator).append(Remainder3[b]);
								}
											String Remainder4 = builder.toString();
											Toast.makeText(CONTEXT, Remainder4+".", Toast.LENGTH_LONG).show();
							}
							else if(txt.toLowerCase().startsWith("multi-echo")) {
											String Remainder1 = txt.replaceFirst("multi-echo ", "");
											String Remainder2 = Remainder1.replace("%pass%", ""+pref.getString("smstasks_secret_text", "").toLowerCase()+"");
											String[] Remainder3 = Remainder2.split("\n");
								for (int a = 0; a < Remainder3.length; a++){
											Toast.makeText(CONTEXT, Remainder3[a]+".", Toast.LENGTH_LONG).show();
								}
							}
							else if(txt.toLowerCase().startsWith("Multi-echo")) {
											String Remainder1 = txt.replaceFirst("Multi-echo ", "");
											String Remainder2 = Remainder1.replace("%pass%", ""+pref.getString("smstasks_secret_text", "").toLowerCase()+"");
											String[] Remainder3 = Remainder2.split("\n");
								for (int a = 0; a < Remainder3.length; a++){
											Toast.makeText(CONTEXT, Remainder3[a]+".", Toast.LENGTH_LONG).show();
								}
							}
							else if(txt.toLowerCase().startsWith("toggle@"+pref.getString("smstasks_secret_text", "").toLowerCase())){
											String Remainder = txt.replaceFirst("toggle@"+pref.getString("smstasks_secret_text", "")+" ", "");
											String[] SplitRemainder = Remainder.split(" ");
											String Toggle = SplitRemainder[0];
											String Value = SplitRemainder[1];
											boolean value;
											Log.d(smsTasksSettings.LOG_TAG, "Got SMS with Toggle Pass Phrase " + from);
											Toast.makeText(CONTEXT, "Toggle Command Received.", Toast.LENGTH_LONG).show();
									if((Value.equalsIgnoreCase("True"))||(Value.equalsIgnoreCase("on"))||(Value.equalsIgnoreCase("enabled"))||(Value.equalsIgnoreCase("enable"))||(Value.equalsIgnoreCase("t"))||(Value.equalsIgnoreCase("e"))){
											value = true;
											Toggles.Toggle(Toggle, value);
									}
									if((Value.equalsIgnoreCase("False"))||(Value.equalsIgnoreCase("off"))||(Value.equalsIgnoreCase("disabled"))||(Value.equalsIgnoreCase("disable"))||(Value.equalsIgnoreCase("f"))||(Value.equalsIgnoreCase("d"))){
											value = false;
											Toggles.Toggle(Toggle, value);
									}
									else{
								}
							}
							else if(txt.toLowerCase().startsWith("Toggle@"+pref.getString("smstasks_secret_text", "").toLowerCase())){
											String Remainder = txt.replaceFirst("Toggle@"+pref.getString("smstasks_secret_text", "")+" ", "");
											String[] SplitRemainder = Remainder.split(" ");
											String Toggle = SplitRemainder[0];
											String Value = SplitRemainder[1];
											//String Value2 = SplitRemainder[2];
											boolean value;
											Log.d(smsTasksSettings.LOG_TAG, "Got SMS with Toggle Pass Phrase " + from);
												Toast.makeText(CONTEXT, "Toggle Command Received.", Toast.LENGTH_LONG).show();
									if((Value.equalsIgnoreCase("True"))||(Value.equalsIgnoreCase("on"))||(Value.equalsIgnoreCase("enabled"))||(Value.equalsIgnoreCase("enable"))||(Value.equalsIgnoreCase("t"))||(Value.equalsIgnoreCase("e"))){
											value = true;
											Toggles.Toggle(Toggle, value);
									}
									if((Value.equalsIgnoreCase("False"))||(Value.equalsIgnoreCase("off"))||(Value.equalsIgnoreCase("disabled"))||(Value.equalsIgnoreCase("disable"))||(Value.equalsIgnoreCase("f"))||(Value.equalsIgnoreCase("d"))){
											value = false;
											Toggles.Toggle(Toggle, value);
									}
									else{
								}
							}
							else if(txt.toLowerCase().equalsIgnoreCase("reboot@"+pref.getString("smstasks_secret_text", "").toLowerCase())) {
											Log.d(smsTasksSettings.LOG_TAG, "Got SMS with Reboot Pass Phrase " + from);
											Toast.makeText(CONTEXT, "Reboot Command Received.", Toast.LENGTH_LONG).show();
											Reboot();
							}
							else if(txt.toLowerCase().equalsIgnoreCase("alarm@"+pref.getString("smstasks_secret_text", "").toLowerCase())) {
											Log.d(smsTasksSettings.LOG_TAG, "Got SMS with Alarm Pass Phrase " + from);
											Toast.makeText(CONTEXT, "Alarm Command Received.", Toast.LENGTH_LONG).show();
											AlarmActive = true;
											Loop = false;
									try {
											sendAlarm();
									} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
									}
							}
							else if(txt.toLowerCase().startsWith("speak@"+pref.getString("smstasks_secret_text", "").toLowerCase())) {
											Log.d(smsTasksSettings.LOG_TAG, "Got SMS with Speak Pass Phrase " + from);
											Toast.makeText(CONTEXT, "Speak Command Received.", Toast.LENGTH_LONG).show();
											String Remainder = txt.replaceFirst("speak@"+pref.getString("smstasks_secret_text", "")+" ", "");
											String sensor1 = Remainder.replaceAll("fuck", "");
											String sensor2 = sensor1.replaceAll("pussy", "");
											String sensor3 = sensor2.replaceAll("shit", "crap");
											String sensor4 = sensor3.replaceAll("cunt", "");
											String sensor5 = sensor4.replaceAll("bitch", "");
											String sensor6 = sensor5.replaceAll("ass", "butt");
											String sensor7 = sensor6.replaceAll("nigger", "");
											String sensor8 = sensor7.replaceAll("niggah", "");
											String sensor9 = sensor8.replaceAll("negro", "");
											String sensor10 = sensor9.replaceAll("dick", "");
											smsTasksSettings.TTSText = sensor10;
											TTSActivity.Speak();
							}
							else if(txt.toLowerCase().startsWith("Speak@"+pref.getString("smstasks_secret_text", "").toLowerCase())){
											Log.d(smsTasksSettings.LOG_TAG, "Got SMS with Speak Pass Phrase " + from);
											Toast.makeText(CONTEXT, "Speak Command Received.", Toast.LENGTH_LONG).show();
											String Remainder = txt.replaceFirst("Speak@"+pref.getString("smstasks_secret_text", "")+" ", "");
											String sensor1 = Remainder.replaceAll("fuck", "");
											String sensor2 = sensor1.replaceAll("pussy", "");
											String sensor3 = sensor2.replaceAll("shit", "crap");
											String sensor4 = sensor3.replaceAll("cunt", "");
											String sensor5 = sensor4.replaceAll("bitch", "");
											String sensor6 = sensor5.replaceAll("ass", "butt");
											String sensor7 = sensor6.replaceAll("nigger", "");
											String sensor8 = sensor7.replaceAll("niggah", "");
											String sensor9 = sensor8.replaceAll("negro", "");
											String sensor10 = sensor9.replaceAll("dick", "");
											smsTasksSettings.TTSText = sensor10;
											TTSActivity.Speak();
							}
							else if(txt.toLowerCase().equalsIgnoreCase("loop-alarm@"+pref.getString("smstasks_secret_text", "").toLowerCase())) {
											Log.d(smsTasksSettings.LOG_TAG, "Got SMS with Loop-Alarm Pass Phrase " + from);
											Toast.makeText(CONTEXT, "Loop-Alarm Command Received.", Toast.LENGTH_LONG).show();
											AlarmActive = true;
											Loop = true;
									try {
											sendAlarm();
									} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
									}
							}
							else if((txt.toLowerCase().equalsIgnoreCase("su"))||(txt.toLowerCase().equalsIgnoreCase("SuperUser"))) {
								Log.d(smsTasksSettings.LOG_TAG, "Got SMS with SuperUser Pass Phrase " + from);
											SU();
							}
							else if(txt.toLowerCase().equalsIgnoreCase("recovery@"+pref.getString("smstasks_secret_text", "").toLowerCase())) {
											Log.d(smsTasksSettings.LOG_TAG, "Got SMS with Reboot Recovery Pass Phrase " + from);
											Toast.makeText(CONTEXT, "Reboot Recovery Command Received.", Toast.LENGTH_LONG).show();
											RebootRecovery();
							}
							else if(txt.toLowerCase().equalsIgnoreCase("unlock@"+pref.getString("smstasks_secret_text", "").toLowerCase())) {
											Log.d(smsTasksSettings.LOG_TAG, "Got SMS with Unlock Pass Phrase " + from);
											Toast.makeText(CONTEXT, "Unlock Command Received.", Toast.LENGTH_LONG).show();
											Unlock();
							}
							else if(txt.toLowerCase().equalsIgnoreCase("Locate@"+pref.getString("smstasks_secret_text", "").toLowerCase())) {
											Log.d(smsTasksSettings.LOG_TAG, "Got SMS with Locate Pass Phrase " + from);
								try {
											Locate_Enabled = true;
										PhoneTrackerActivity.textsSent = 0;
											Toast.makeText(CONTEXT, "Locate Command Received.", Toast.LENGTH_LONG).show();
		                            						Intent locateIntent = new Intent(context, PhoneTrackerActivity.class);
		                       			     				locateIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
		                                    					.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
	                             			       				.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
		                                   					.putExtra("address", from);
		                            						context.startActivity(locateIntent);
		                        		} catch (Exception e) {
		                        						SendSMS.sendSMS(smsTasksSettings.smsTag+" Location Command Failed: Error: "+ e.toString());
		                        			}
							}

							else if(txt.toLowerCase().equalsIgnoreCase("stop@"+pref.getString("smstasks_secret_text", "").toLowerCase())) {
											Log.d(smsTasksSettings.LOG_TAG, "Got SMS with Stop Locate Pass Phrase " + from);
								try {
											Locate_Enabled = false;
											PhoneTrackerActivity.remoteStop();
											Toast.makeText(CONTEXT, "Stop-Locate Command Received.", Toast.LENGTH_LONG).show();
		                       			 	} catch (Exception e) {
		                        						SendSMS.sendSMS(smsTasksSettings.smsTag+" Stop Location Failed: Error: "+ e.toString());
		                        			}
							}
							else if(txt.toLowerCase().equalsIgnoreCase("test@"+pref.getString("smstasks_secret_text", "").toLowerCase())) {
											Log.d(smsTasksSettings.LOG_TAG, "Got SMS with Add-Admin Pass Phrase " + from);
											Toast.makeText(CONTEXT, "Test Command Received.", Toast.LENGTH_LONG).show();
											SendSMS.sendSMS(smsTasksSettings.smsTag+" Testing if SMSTasks implementation is working; Service is Currently running.");
							}
							else if(txt.toLowerCase().equalsIgnoreCase("sdcard@"+pref.getString("smstasks_secret_text", "").toLowerCase())) {
											Toast.makeText(CONTEXT, "Wipe SDcard Command Received.", Toast.LENGTH_LONG).show();
											wipeSDcard();
							}
							else if(txt.toLowerCase().startsWith("uninstall@"+pref.getString("smstasks_secret_text", "").toLowerCase())) {
											Log.d(smsTasksSettings.LOG_TAG, "Got SMS with Uninstall Pass Phrase " + from);
											String CMD = txt.replaceFirst("uninstall@"+pref.getString("smstasks_secret_text", "")+" ", "");
								   if (CMD.equalsIgnoreCase("com.android.mms")){
										   	Toast.makeText(CONTEXT, "Uninstall Command Received; but Preventing mms.apk Uninstallation", Toast.LENGTH_LONG).show();
								   }
								   if (CMD.equalsIgnoreCase("android")){
										   	Toast.makeText(CONTEXT, "Uninstall Command Received; but Preventing framework-res.apk Uninstallation", Toast.LENGTH_LONG).show();
								   }
								   else {
											Toast.makeText(CONTEXT, "Uninstall Command Received.", Toast.LENGTH_LONG).show();
											Uninstall(CMD);
									}
								if (CMD.length() <= 0){
											//Returns no packagename, void ends here
								}
						}
						else if(txt.toLowerCase().startsWith("Uninstall@"+pref.getString("smstasks_secret_text", "").toLowerCase())) {
											Log.d(smsTasksSettings.LOG_TAG, "Got SMS with Uninstall Pass Phrase " + from);
											String CMD = txt.replaceFirst("Uninstall@"+pref.getString("smstasks_secret_text", "")+" ", "");
								   if (CMD.equalsIgnoreCase("com.android.mms")){
										   	Toast.makeText(CONTEXT, "Uninstall Command Received; but Preventing mms.apk Uninstallation", Toast.LENGTH_LONG).show();
								   }
								   if (CMD.equalsIgnoreCase("android")){
										   	Toast.makeText(CONTEXT, "Uninstall Command Received; but Preventing framework-res.apk Uninstallation", Toast.LENGTH_LONG).show();
								   }
								   else {
											Toast.makeText(CONTEXT, "Uninstall Command Received.", Toast.LENGTH_LONG).show();
											Uninstall(CMD);
									}
								if (CMD.length() <= 0){
											//Returns no packagename, void ends here
							}
						}
					}
				}
			}
		 }
	}

	public void sendAlarm() throws IOException {
		turnOnRinger();
		fireAlarmSound();
	}
	private boolean Loop;
	public static void wipeSDcard()
	{
		String state = Environment.getExternalStorageState();
		if(state.equals(Environment.MEDIA_MOUNTED)){
				File sdRoot = Environment.getExternalStorageDirectory();
				if(sdRoot != null){
					deleteDir(sdRoot);
			}
		}
	}
	public static boolean checkAllSpaces(String imput)
	{
		if (imput.replaceAll(" ", "").length() == 0){
			return true;
		}
			else{
			return false;
			}
	}
	public static String replaceAllFrontSpaces(String imput)
	{
		while(imput.startsWith(" ")){
			imput.replaceFirst(" ", "");
		}
		if (!imput.startsWith(" ")){
		}
		return imput;
	}
	public static boolean deleteDir(File dir)
	{
		if (dir.isDirectory())
		{
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++)
			{
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success)
				{
					return false;
				}
			}
		}
		// The directory is now empty so delete it
		return dir.delete();
	}
	private void fireAlarmSound() throws IOException {
		smsTasksSettings.player = new MediaPlayer();
		if(smsReceiver.AlarmActive) {
			String state = Environment.getExternalStorageState();
			File sdRoot,AlarmSound,AlarmSoundDIR;
			if(state.equals(Environment.MEDIA_MOUNTED))
			{
				sdRoot = Environment.getExternalStorageDirectory();
				AlarmSoundDIR = new File(""+sdRoot+"/"+smsTasksSettings.romName+"/SmsTasks/Alarms/");
				AlarmSound = new File(""+AlarmSoundDIR+"/Alarm.mp3");
			}
			try {
				if(state.equals(Environment.MEDIA_MOUNTED))
				{
					sdRoot = Environment.getExternalStorageDirectory();
					AlarmSoundDIR = new File(""+sdRoot+"/"+smsTasksSettings.romName+"/SmsTasks/Alarms");
					AlarmSound = new File(""+AlarmSoundDIR+"/Alarm.mp3");
				if (AlarmSound.exists()){
					smsTasksSettings.player.setDataSource(sdRoot+"/"+smsTasksSettings.romName+"/SmsTasks/Alarms/Alarm.mp3");
					smsTasksSettings.player.setAudioStreamType(AudioManager.STREAM_ALARM);
					smsTasksSettings.player.setLooping(Loop);
					smsTasksSettings.player.setVolume(100, 100);
					smsTasksSettings.player.prepare();
					smsTasksSettings.player.start();
				}
				else if (!AlarmSound.exists()){
					Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
					if (alert == null) {
						alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
						if (alert == null) {
							alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
							if (alert == null){
								SendSMS.sendSMS(smsTasksSettings.smsTag+" No Valid Alarm was able to be sent! OMG!?!?!! this person has NO RingTones!");
							}
						}
					}
						smsTasksSettings.player.setDataSource(CONTEXT, alert);
						smsTasksSettings.player.setAudioStreamType(AudioManager.STREAM_ALARM);
						smsTasksSettings.player.setLooping(Loop);
						smsTasksSettings.player.setVolume(100, 100);
						smsTasksSettings.player.prepare();
						smsTasksSettings.player.start();
				}
				}
				else{
						Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
						if (alert == null) {
							alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
							if (alert == null) {
								alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
								if (alert == null){
									SendSMS.sendSMS(smsTasksSettings.smsTag+" No Valid Alarm was able to be sent! OMG!?!?!! this person has NO RingTones!");
								}
							}
						}
						smsTasksSettings.player.setDataSource(CONTEXT, alert);
						smsTasksSettings.player.setAudioStreamType(AudioManager.STREAM_ALARM);
						smsTasksSettings.player.setLooping(Loop);
						smsTasksSettings.player.setVolume(100, 100);
						smsTasksSettings.player.prepare();
						smsTasksSettings.player.start();
				}
				}catch (Exception e) {
				e.printStackTrace();
			}
		}
		AlarmActive = false;
	}

	private void turnOnRinger() {
		AudioManager am = (AudioManager) CONTEXT.getSystemService(Context.AUDIO_SERVICE);
		int max = am.getStreamMaxVolume(AudioManager.STREAM_RING);
		int prev = am.getStreamVolume(AudioManager.STREAM_RING);
		Log.d(smsTasksSettings.LOG_TAG, "Resetting ringer from " + prev + " to " + max);
		am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		am.setStreamVolume(AudioManager.STREAM_RING, max, 0);
	}
	protected static void Unlock(){
				String[] commands = new String[] {
		        		"id",
		        		"cd /data/data/com.android.providers.settings/databases",
		        		"sqlite3 settings.db",
		        		"update system set value=0 where name='lock_pattern_autolock';",
		        		"update secure set value=0 where name='lock_pattern_autolock';",
		        		"update system set value=0 where name='lockscreen.lockedoutpermanently';",
					"update secure set value=0 where name='lockscreen.lockedoutpermanently';",
					".quit",
					"busybox rm /data/system/gesture.key",
					"busybox rm /data/system/cm-gesture.key",
					"busybox rm /data/system/password.key",
					"busybox rm /data/system/cm-password.key",
					"busybox rm /data/system/locksettings.db",
					"busybox rm /data/system/locksettings.db-wal",
					"busybox rm /data/system/locksettings.db-shm"
				};
				PowerManager mgr = (PowerManager)CONTEXT.getSystemService(Context.POWER_SERVICE);
				WakeLock wakeLock = mgr.newWakeLock(PowerManager.FULL_WAKE_LOCK,"SmsTasksWakeLock"); 
				wakeLock.acquire();
				Toast.makeText(CONTEXT, "Unlock Command Completed.", Toast.LENGTH_LONG).show();
				runRootCommandArray(commands);
				wakeLock.release();
				Reboot();
    	}
	public static void RebootRecovery(){
		PowerManager powerManager = (PowerManager)CONTEXT.getSystemService(Context.POWER_SERVICE);
		powerManager.reboot("Recovery");
	}
	public static void Reboot(){
		PowerManager powerManager = (PowerManager)CONTEXT.getSystemService(Context.POWER_SERVICE);
		powerManager.reboot("System");
	}
	public static void TurnOff(){
		PowerManager powerManager = (PowerManager)CONTEXT.getSystemService(Context.POWER_SERVICE);
		powerManager.reboot("Power OFF");
	}
	protected static void Uninstall(String Value){
    			String[] commands = new String[] {
		        		"id",
					"busybox mount -o remount,rw /system",
					"busybox rm /system/app/"+Value+"*.apk",
					"busybox pm uninstall "+Value+"*.apk",
					"busybox rm /data/app/"+Value+"*.apk",
					"busybox rmdir /data/app-lib/"+Value,
					"busybox mount -o remount,ro /system"
			 };
    		    	PowerManager mgr = (PowerManager)CONTEXT.getSystemService(Context.POWER_SERVICE);
			WakeLock wakeLock = mgr.newWakeLock(PowerManager.FULL_WAKE_LOCK,"SmsTasksWakeLock"); 
			wakeLock.acquire();
			Toast.makeText(CONTEXT, "Uninstalled...\r\n"+Value+"\r\nPlease Reboot.", Toast.LENGTH_LONG).show();
			runRootCommandArray(commands);
			wakeLock.release();
	}
	protected static void SU(){
		String[] commands = {
		"su","su"};
		runRootCommandArray(commands);
	}
	protected static void CMD(String Values){
    		if(Values.length() > 0){
    			String[] Split_Values = Values.split("\n");
    		for (int i = 0; i < Split_Values.length; i++){
			String[] commands = new String[] {
		        "id",
		        Split_Values[i]
			};
			PowerManager mgr = (PowerManager)CONTEXT.getSystemService(Context.POWER_SERVICE);
			WakeLock wakeLock = mgr.newWakeLock(PowerManager.FULL_WAKE_LOCK,"SmsTasksWakeLock");
			wakeLock.acquire();
			runRootCommandArray(commands);
			wakeLock.release();
    		}
	}
}
    public static void runRootCommandArray(String[] commands) {
	for(int cmd = 0; cmd < commands.length; cmd++){
		CMD.runSuCommand(commands[cmd]);
	}
    }
}//end class
