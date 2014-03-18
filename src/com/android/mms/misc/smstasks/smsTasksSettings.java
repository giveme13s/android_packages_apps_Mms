package com.android.mms.misc.smstasks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.net.Uri;
import com.android.mms.R;
public class smsTasksSettings extends Activity implements OnClickListener, TextToSpeech.OnInitListener {
	public boolean firstRun = true;
	public static Context context = smsReceiver.CONTEXT;
    	public static TextToSpeech tts;
    	public static EditText op,np,cp,w1,w2,we;
    	static Button b,b2,b3;
    	public static SharedPreferences sp;
    	public static DevicePolicyManager devicePolicyManager;
	public static String HIDDEN = "false";
	public static MediaPlayer player;
	public static AudioManager manager;
	public static DevicePolicyManager mDPM;
	public static ComponentName mAdminName;
	public static String smsTag = "<SmsTasks>:";
	public static String romName = "";
	public static boolean warned;
	public static String TTSText = "";
	public static String LOG_TAG = "SmsTasks";
	public static void StartActivity(Intent intent){
    	context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smstasks_settings);
			//CMD.SU();
			romName = replaceAllFrontSpaces(getResources().getString(R.string.smstasks_implemented_rom_name));
			String temp = "";
		if (romName.equals("")){
			romName = "KittleApps"; //defaulted phrase just in case the element is empty, null,  non-existent, or error causing no value to be set occured, can be changed, but it will change the directory for file placements
		}
		if (romName.contains("/")){
			temp = romName.replaceAll("/", "");
			romName = temp;
			temp = "";
		}
		if (romName.contains("\"")){
			romName.replaceAll("\"", "");
			romName = temp;
			temp = "";
		}
		if (romName.contains("+")){
			romName.replaceAll("+", "");
			romName = temp;
			temp = "";
		}
		if (romName.contains("?")){
			romName.replaceAll("?", "");
			romName = temp;
			temp = "";
		}
		if (romName.contains("%")){
			romName.replaceAll("%", "");
			romName = temp;
			temp = "";
		}
		if (romName.contains("*")){
			romName.replaceAll("*", "");
			romName = temp;
			temp = "";
		}
		if (romName.contains(":")){
			romName.replaceAll(":", "");
			romName = temp;
			temp = "";
		}
		if (romName.contains("|")){
			romName.replaceAll("|", "");
			romName = temp;
			temp = "";
		}
		if (romName.contains(">")){
			romName.replaceAll(">", "");
			romName = temp;
			temp = "";
		}
		if (romName.contains("<")){
			romName.replaceAll("<", "");
			romName = temp;
			temp = "";
		}
		if (romName.contains(".")){
			romName.replaceAll(".", "");
			romName = temp;
			temp = "";
		}
		if (romName.contains(" ")){
			romName.replaceAll(" ", "");
			romName = temp;
			temp = "";
		}
    		player = new MediaPlayer();
    		manager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        	op = (EditText) findViewById(R.id.smstasks_old_pass_phrase_edittext);
        	np = (EditText) findViewById(R.id.smstasks_new_pass_phrase_edittext);
        	cp = (EditText) findViewById(R.id.smstasks_confirm_pass_phrase_edittext);
		w1 = (EditText) findViewById(R.id.smstasks_whitelist1_edittext);
		w2 = (EditText) findViewById(R.id.smstasks_whitelist2_edittext);
		we = (EditText) findViewById(R.id.smstasks_whitelist_email_edittext);
        	b = (Button) findViewById(R.id.smstasks_change_pass_phrase_button);
        	b2 = (Button) findViewById(R.id.smstasks_change_whitelist_button);
		b3 = (Button) findViewById(R.id.how_to_use_smstasks_button);
        	tts = new TextToSpeech(this, this);
        	sp = PreferenceManager.getDefaultSharedPreferences(this);
		warned = sp.getBoolean("smstasks_warned", true);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	if (op.getText().toString().equals(sp.getString("smstasks_secret_text", ""))){
            		if((cp.getText().toString().equals("")) && (np.getText().toString().equals(cp.getText().toString()))){
                		Toast.makeText(getApplicationContext(), "New Pass Phrases Cannot Be Blank.", Toast.LENGTH_LONG).show();
                	}
            		if (checkAllSpaces(np.getText().toString())){
         	           	Toast.makeText(getApplicationContext(), "New Pass Phrases Cannot be Only Spaces!!", Toast.LENGTH_LONG).show();
                    }
            		if((!checkAllSpaces(np.getText().toString()))&&(!np.getText().toString().equals("")) && (cp.getText().toString().equals(np.getText().toString()))){
            		if (np.getText().toString().startsWith(" ")){
            			savePrefs("smstasks_secret_text", replaceAllFrontSpaces(np.getText().toString()));
            			Toast.makeText(getApplicationContext(), "Deleted Beginning Spaces in Pass Phrase by Default; Settings are Saved.", Toast.LENGTH_LONG).show();
            		}
			if ((!np.getText().toString().startsWith(" ")) && (warned)){
				savePrefs("smstasks_secret_text", np.getText().toString());
        			Toast.makeText(getApplicationContext(), "Settings Saved.", Toast.LENGTH_LONG).show();
            		}
            		if ((!np.getText().toString().startsWith(" ")) && (!warned)){
				Toast.makeText(getApplicationContext(), "Are You Sure you can Remember?; tap one more time to apply", Toast.LENGTH_LONG).show();
				savePrefs("smstasks_warned", true);
				warned = sp.getBoolean("smstasks_warned", true);
            		}
            	}
            	else {
                		Toast.makeText(getApplicationContext(), "New+Confirm Pass Phrases Do Not Match.", Toast.LENGTH_LONG).show();
                     }
            	}
            	else {
        			Toast.makeText(getApplicationContext(), "Old Pass Phrase Incorrect; Remember First Time Setting there is NO Password", Toast.LENGTH_LONG).show();
 	 	     }
            }
        });
		b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
		if (cp.getText().toString().equals(smsTasksSettings.sp.getString("smstasks_secret_text", ""))){
				smsTasksSettings.savePrefs("whitelisted_number1", w1.getText().toString());
				smsTasksSettings.savePrefs("whitelisted_number2", w2.getText().toString());
				smsTasksSettings.savePrefs("whitelisted_email", we.getText().toString());
				Toast.makeText(getApplicationContext(), "White-List Settings Saved.", Toast.LENGTH_LONG).show();
			}
			else if (!cp.getText().toString().equals(smsTasksSettings.sp.getString("smstasks_secret_text", ""))){
				Toast.makeText(getApplicationContext(), "Confirm Pass Phrase Incorrect.", Toast.LENGTH_LONG).show();
			}
			else if (cp.getText().toString().equals("")){
				Toast.makeText(getApplicationContext(), "Confirm Pass Phrase Blank.", Toast.LENGTH_LONG).show();
			}
			else {
				Toast.makeText(getApplicationContext(), "Error in Reading Confirm Pass Phrase.", Toast.LENGTH_LONG).show();
			}
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            			Intent intent = new Intent(smsTasksSettings.this, howTo.class);
				startActivity(intent);
				}
        });
    }
    public void onClick(View v) {}
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
    public static void savePrefs(String key, String value) {
       // SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        Editor edit = sp.edit();
        edit.putString(key, value);
        edit.commit();
    }
	public static void savePrefs(String key, boolean value) {
		Editor edit = sp.edit();
		edit.putBoolean(key, value);
		edit.commit();
	}
	@Override
	public void onInit(int status) {
		 if (status == TextToSpeech.SUCCESS) {
	            int result = tts.setLanguage(Locale.US);
	            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
	                Log.e("TTS", "This Language is not supported");
	            } else {
	            }
	        } else {
	            Log.e("TTS", "Initialization Failed!");
	        }
	}
	public static void Speak(String Text) {
        tts.speak(Text, TextToSpeech.QUEUE_FLUSH, null);
        if (!tts.isSpeaking()){
            tts.stop();
            tts.shutdown();
        	}
	}
}
