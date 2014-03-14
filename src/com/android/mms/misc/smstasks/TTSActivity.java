package com.android.mms.misc.smstasks;

import android.app.Activity;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.widget.Toast; 
/**
 * This class demonstrates checking for a TTS engine, and if one is
 * available it will spit out some speak.
 */
public abstract class TTSActivity extends Activity implements TextToSpeech.OnInitListener
{
    public static String Text;
    public static Context context = smsReceiver.CONTEXT;
    public static TextToSpeech tts;
    public static OnInitListener CONTEXT;
	public static void Speak() {
		// TODO Auto-generated method stub
	Text = smsTasksSettings.TTSText;
    	Toast.makeText(context, "Speaking: "+Text, Toast.LENGTH_LONG).show();
    	smsTasksSettings.Speak(Text);
	}
}

