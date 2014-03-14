package com.android.mms.misc.smstasks;

import java.util.ArrayList;
import android.content.Context;
import android.telephony.SmsManager;
import com.android.mms.R;

public class SendSMS {
	public static Context context = smsReceiver.CONTEXT;
	public static void sendSMS(String message) {
	        SmsManager sms = SmsManager.getDefault();
		String to = smsReceiver.from;
		ArrayList<String> parts = sms.divideMessage(message);
	    	sms.sendMultipartTextMessage(to, null, parts, null, null);
	}
}//end class
