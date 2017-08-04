package kissmediad2d.android;

import java.io.File;

import kissmediad2d.android.R;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

public class videoView extends Activity {
	private VideoView v;
	private String[] playList;
	private int playingIndex = 0;
	public String sdcardPath = Environment.getExternalStorageDirectory().toString() + File.separator + "KM" + "/";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // set no title
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // set
																														// fullscreen
		setContentView(R.layout.animation);
		Intent data = getIntent(); // 接收從上一activity傳來的參數
		Bundle bundle = data.getExtras();
		String temp = bundle.getString("playList");
		temp=temp.substring(temp.indexOf("&")+1, temp.length());
		System.out.println("看清單拉  "+temp);
		playList = temp.split("&");
		
		v = (VideoView) findViewById(R.id.videoView1);

		v.setMediaController(new MediaController(videoView.this));

		v.setVideoURI(Uri.parse(sdcardPath + playList[playingIndex]));
		v.setSoundEffectsEnabled(v.isSoundEffectsEnabled());
		v.requestFocus();
		v.start();



		// ------------------------------------
		/*
		 * v.setVideoURI(Uri.parse("/sdcard/"+playList[playingIndex]));
		 * v.requestFocus(); v.start();
		 */
		v.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
			}
		});
		v.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer arg0) {
				playingIndex++;
				if (playingIndex < playList.length)  {

					v.setVideoPath(sdcardPath + playList[playingIndex]);
					v.requestFocus();
					v.start();
				}else{
					
					//finish();
				}
				/*
				 * else arg0.release(); //finish();
				 */
			}
		});

	}

	public void onConfigurationChanged(Configuration newConfig) {

		super.onConfigurationChanged(newConfig);

	}

}
