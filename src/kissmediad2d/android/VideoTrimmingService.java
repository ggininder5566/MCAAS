package kissmediad2d.android;
import java.io.File;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import kissmediad2d.android.VideoTrimmer;

public class VideoTrimmingService extends IntentService {
public static final String ACTION_MyIntentService = "kissmediad2d.android.RESPONSE";
public static final String EXTRA_KEY_OUT = "EXTRA_OUT";	
String extraOut;
int i=0;
private VideoTrimmer trimmer;
public static boolean isTrimming = false;
LoginActivity id1 =new LoginActivity();
public VideoTrimmingService() {
	super("VideoTrimmingService");
	setVideoTrimmer(new VideoTrimmer());	
}

@Override
public void onCreate() {
	super.onCreate();
	System.loadLibrary("ffmpeg");
	System.loadLibrary("video-trimmer");
	
}


@Override
protected void onHandleIntent(Intent intent) {
	isTrimming = true;
	extraOut = "ok";
	Log.i("VideoTrimmerService", "On bind of service");
	Bundle extras = intent.getExtras();
	String inputFileName = extras.getString("inputFileName");
	String outFileName = extras.getString("outFileName");
	int index =extras.getInt("index");
	int split_seq =extras.getInt("split_seq");
	int start = extras.getInt("start");
	int duration = extras.getInt("duration");
	Float total_time = extras.getFloat("total_time");
	String selfid = extras.getString("selfid");
	Messenger messenger = (Messenger) extras.get("messenger");
	extraOut=inputFileName+"&"+outFileName+"&"+index+"&"+split_seq+"&"+start+"&"+duration+"&"+total_time+"&"+selfid;
	//--------------------------------
	Log.i("VideoTrimmerService", "Starting trimming");
	Log.i("VideoTrimmerService Inputfile","NAME¦b³o¸Ì°Õ "+extraOut);
	//Log.i("VideoTrimmerService outfilename",outFileName);
	System.out.println("start = "+start);
	System.out.println("duration = "+duration);
	//---------------------------------
	System.gc();
	//------------------------
	boolean error = false;
	
	try{
		int returnStatus = trimmer.trim_(inputFileName, outFileName, start, duration);
		error = returnStatus != 0;
	} catch (Exception e) {
		error = true;
	}
	System.gc();
	
	String messageText = error ?"Unable to trim the video. Check the error logs.": "Trimmed video succesfully to "+outFileName;
	Log.i("VideoTrimmerService", "Sending message: "+messageText);
	System.out.println("kill_process"+android.os.Process.myTid());
	/*try {
		
		Message message = new Message();
		message.getData().putString("text", messageText);
		messenger.send(message);
	} catch (RemoteException e) {
		Log.i("VideoTrimmerService", "Exception while sending message");
	}*/
	isTrimming = false;

	//return result




}



void setVideoTrimmer(VideoTrimmer t){
	this.trimmer = t;
	
}
public void onDestroy()
{
    super.onDestroy();
    Intent intentResponse = new Intent();
	intentResponse.setAction(ACTION_MyIntentService);
	intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
	intentResponse.putExtra(EXTRA_KEY_OUT, extraOut);
	sendBroadcast(intentResponse);
   android.os.Process.killProcess(android.os.Process.myPid());

	
}
}
