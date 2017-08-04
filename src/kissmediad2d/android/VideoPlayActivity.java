package kissmediad2d.android;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

public class VideoPlayActivity extends Activity implements  SurfaceHolder.Callback {
	
    private MediaPlayer firstPlayer,    
                        nextMediaPlayer, 
                        cachePlayer,      
                        currentPlayer;   

    private SurfaceView surface;  
    private SurfaceHolder surfaceHolder;  
    private LinearLayout bottom_bar_layout;  
    private FrameLayout video_layout;   
    private String[] playList;
    private ArrayList<String> VideoListQueue = new ArrayList<String>();  

    private HashMap<String, MediaPlayer> playersCache = new HashMap<String, MediaPlayer>();  

    private int currentVideoIndex; 
	    @Override  
	    public void onCreate(Bundle savedInstanceState) {  
	        super.onCreate(savedInstanceState);  
	        
			requestWindowFeature(Window.FEATURE_NO_TITLE); // set no title
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // set
			setContentView(R.layout.upvideo);
			Intent data = getIntent(); // 接收從上一activity傳來的參數
			Bundle bundle = data.getExtras();
			String temp = bundle.getString("playList");
			temp=temp.substring(temp.indexOf("&")+1, temp.length());
			System.out.println("看清單拉  "+temp);
			playList = temp.split("&");
	        //this.setRequestedOrientation(ActivityInfo.SCREEN_);  
	        initView();  
	    } 
	    @Override  
	    protected void onDestroy() {  
	        super.onDestroy();  
	        if (firstPlayer != null) {  
	            if (firstPlayer.isPlaying()) {  
	                firstPlayer.stop();  
	            }  
	            firstPlayer.release();  
	        }  
	        if (nextMediaPlayer != null) {  
	            if (nextMediaPlayer.isPlaying()) {  
	                nextMediaPlayer.stop();  
	            }  
	            nextMediaPlayer.release();  
	        }  
	  
	        if (currentPlayer != null) {  
	            if (currentPlayer.isPlaying()) {  
	                currentPlayer.stop();  
	            }  
	            currentPlayer.release();  
	        }  
	        currentPlayer = null;  
	    } 
	    private void initView() {  
	        surface = (SurfaceView) findViewById(R.id.surface);  
	        surfaceHolder = surface.getHolder(); 
	        surfaceHolder.addCallback(this); 
	        bottom_bar_layout = (LinearLayout) findViewById(R.id.live_buttom_bar);
	        video_layout = (FrameLayout) findViewById(R.id.videoLayout);  
	        video_layout.setOnClickListener(new View.OnClickListener() {  
	  
	            @Override  
	            public void onClick(View arg0) {  
	                if (bottom_bar_layout.getVisibility() == View.VISIBLE) {  
	                    bottom_bar_layout.setVisibility(View.GONE);  
	                } else {  
	                    bottom_bar_layout.setVisibility(View.VISIBLE);  
	                }  
	  
	            }  
	        });
	    }  
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		getVideoUrls();  
        
        initFirstPlayer(); 
	}
    private void initFirstPlayer() {  
        firstPlayer = new MediaPlayer();  
        firstPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);  
        firstPlayer.setDisplay(surfaceHolder);  
  
        firstPlayer  
                .setOnCompletionListener(new MediaPlayer.OnCompletionListener() {  
                    @Override  
                    public void onCompletion(MediaPlayer mp) {  
                        onVideoPlayCompleted(mp);  
                    }  
                });  
  

        cachePlayer = firstPlayer;  
        initNexttPlayer();  
  

        startPlayFirstVideo();  
    } 
    private void startPlayFirstVideo() {  
        try {  
            firstPlayer.setDataSource(VideoListQueue.get(currentVideoIndex));  
            firstPlayer.prepare();  
            firstPlayer.start();  
        } catch (IOException e) {  

            e.printStackTrace();  
        }  
    }  
    private void initNexttPlayer() {  
        new Thread(new Runnable() {  
  
            @Override  
            public void run() {  
  
                for (int i = 1; i < VideoListQueue.size(); i++) {  
                    nextMediaPlayer = new MediaPlayer();  
                    nextMediaPlayer  
                            .setAudioStreamType(AudioManager.STREAM_MUSIC);  
  
                    nextMediaPlayer  
                            .setOnCompletionListener(new MediaPlayer.OnCompletionListener() {  
                                @Override  
                                public void onCompletion(MediaPlayer mp) {  
                                    onVideoPlayCompleted(mp);  
                                }  
                            });  
  
                    try {  
                        nextMediaPlayer.setDataSource(VideoListQueue.get(i));  
                        nextMediaPlayer.prepare();  
                    } catch (IOException e) {  
                       
                        e.printStackTrace();  
                    }  
                    try { 
                    //set next mediaplayer  
                    cachePlayer.setNextMediaPlayer(nextMediaPlayer);  
                    //set new cachePlayer  
                    cachePlayer = nextMediaPlayer;  
                    //put nextMediaPlayer in cache  
                    playersCache.put(String.valueOf(i), nextMediaPlayer);  
                    } catch (Exception e) {  
  
                    } 
                }  
  
            }  
        }).start();  
    }  
    private void onVideoPlayCompleted(MediaPlayer mp) {  
        mp.setDisplay(null);  
        //get next player  
        currentPlayer = playersCache.get(String.valueOf(++currentVideoIndex));  
        if (currentPlayer != null) {
            try {  
            	 currentPlayer.setDisplay(surfaceHolder);
            } catch (Exception e) {  
            } 
             
        } else {  
            Toast.makeText(VideoPlayActivity.this, "撥放完畢..", Toast.LENGTH_SHORT)  
                    .show();  
        }  
    }
    private void getVideoUrls() {  
        for (int i = 0; i < playList.length; i++) {  
            String url = getURI(i);  
            VideoListQueue.add(url);  
        }  
    } 
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}
	private String getURI(int index) {
        return Environment.getExternalStorageDirectory().toString() + File.separator + "KM" + "/"+playList[index];  
    } 
}