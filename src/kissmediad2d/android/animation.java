package kissmediad2d.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Enumeration;

import login.submit1.Login;
import login.submit1.locupdate;
import login.submit1.retrieve;
import tab.list.att_parameter;
import tab.list.fileContentProvider.UserSchema;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.VideoView;
import database.storage.messageProcess;

public class animation extends Activity {
	private VideoView v;
	String account =new String();
	String password =new String();
	String getip =new String();
	public String LoginrequestString = new String();
	public static String login_name;
	/*
	 * 這邊是用來做開場動畫用的
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // set no title
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // set


		setContentView(R.layout.animation);
		v = (VideoView) findViewById(R.id.videoView1);
		// 從專案中讀取檔案，開啟kissmedia1.3gp這個影音檔
		Uri uri = Uri.parse("android.resource://kissmediad2d.android/" + R.raw.mcaas_animation);
		v.setVideoURI(uri);
		// 取得videoview物件的視窗焦點
		v.requestFocus();
		v.start();
		// 製作監聽器，當影片完成時要做什麼事
		v.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer arg0) {

				Intent intent = new Intent();
				// 此意圖會呼叫LoginActivity.class這
				//intent.setClass(animation.this, LoginActivity.class);
				intent.setClass(animation.this, logininput.class);
				
				// 開啟這個活動
				startActivity(intent);
				animation.this.finish();
//				final String[] check_down = {UserSchema._ACCOUNT,UserSchema._PASSWORD};
//				Cursor info_cursor = getContentResolver().query(Uri.parse("content://tab.list.d2d/user_info"), check_down, null, null, null);
//				if(info_cursor.getCount()>0){
//					info_cursor.moveToFirst();
//					account=info_cursor.getString(0);
//					password=info_cursor.getString(1);
//					try {
//						// 取得網路服務的實體
//						ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//						NetworkInfo info = CM.getActiveNetworkInfo();
//
//						State InternetState = info.getState();
//						
//						getip = LoginActivity.Homeip;
//						// 判斷輸入的帳密是否有大於20
//
//						// 判斷是否有網路
//						if (info == null || !info.isAvailable()) {
//							Toast.makeText(animation.this, "無可用網路", Toast.LENGTH_LONG).show();
//						} else {
//							info_cursor.close();
//							// 執行登入的動作
//							new ConnectHttp().execute();
//
//						}
//					} catch (Exception e) {
//						Toast.makeText(animation.this, "無可用網路", Toast.LENGTH_LONG).show();
//					}	
//				}else{
//					info_cursor.close();
//					Intent intent = new Intent();
//					// 此意圖會呼叫LoginActivity.class這
//					//intent.setClass(animation.this, LoginActivity.class);
//					intent.setClass(animation.this, LoginActivity.class);
//					
//					// 開啟這個活動
//					startActivity(intent);
//					animation.this.finish();
//				}
//				

			}
		});
	}
	
	private class ConnectHttp extends AsyncTask<Void, Void, String[]> {

		AlertDialog.Builder Dialog = new AlertDialog.Builder(animation.this);
		ProgressDialog progressDialog = null;

		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(animation.this, "請稍候", "登入中", true);
			progressDialog.show();

		}

		// 在背景執行此method，執行完會跳到onPostExecute
		@Override
		public String[] doInBackground(Void... params) {
			
			String[] loginreturn = new String[7];
			// 將getid與getpw製作成登入資訊，做為日後連線傳入server的參數
			LoginrequestString = "username=" + account + "&password=" + password;
			login_name = LoginrequestString;
			/*
			 * 使用login中的login method，回傳值是一個陣列 loginreturn[0]是用來檢查有無成功
			 * loginreturn[1]是server回傳的訊息
			 */
			locupdate locup =new locupdate();
			loginreturn = locup.login(getip, LoginrequestString);
			//使用ssdp尋找port
			
            String ipp=getIPAddress(getApplicationContext());

            System.out.println(ipp);
            
            //有問題,3G無法使用
            try{
                att_parameter.port=5555;
                	
            }catch(Exception e){
            	att_parameter.port=9527;
            }
        
			if(loginreturn[0] == "true"){
				String []aliveIp = locup.locationupdate(Login.latest_cookie, ipp , att_parameter.port);
				messageProcess MsgSave = new messageProcess();
				MsgSave.checkwlan(getContentResolver(), aliveIp[4]);
				updateContent(Uri.parse("content://tab.list.d2d/user_data"), "retrievable","content is null",aliveIp[0]);
				updateContent(Uri.parse("content://tab.list.d2d/user_reply"), "reply","filename is null",aliveIp[0]);

				MsgSave = null;
			}
			locup=null;
			return loginreturn;
		}

		// onPostExecute 會接收 doInBackground 的return
		@Override
		protected void onPostExecute(String loginreturn[]) {
			if (loginreturn[0] == "true") // 如果有server有回應
			{	
				Boolean result =att_parameter.chechsuccess(loginreturn[1]);
				if (result) // 若帳號密碼正確 到下一頁
				{	
					// 關掉Dialog的訊息
					progressDialog.dismiss();
					Intent intent = new Intent();
					// 此意圖會呼叫LoginActivity.class這
					//intent.setClass(animation.this, LoginActivity.class);
					intent.setClass(animation.this, MainFragment.class);
					
					// 開啟這個活動
					startActivity(intent);
					animation.this.finish();
				}
			} else {
				if (loginreturn[4] == "false") {
					progressDialog.dismiss();
					System.out.println("Test LoginSKE");
					AlertDialog.Builder Dialog = new AlertDialog.Builder(animation.this); // Dialog
					Dialog.setTitle("警告");
					Dialog.setMessage("無效的伺服器位址，請確認IP位址");
					Dialog.setIcon(android.R.drawable.ic_dialog_info);
					Dialog.setNegativeButton("確定", new DialogInterface.OnClickListener() { // 按下abort
								// 將thread結束
								// 隱藏progressbar
								// 設定按鈕當按下時結束，結束這個Dialog並中斷這個thread
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
									Thread.currentThread().interrupt();
									// ConnectHttp.cancel(ConnectCancel);
									dialog.dismiss();
									// dialog.cancel();
								}
							});
					Dialog.show();
				} else if (loginreturn[5] == "false") {
					progressDialog.dismiss();

					AlertDialog.Builder Dialog = new AlertDialog.Builder(animation.this); // Dialog
					Dialog.setTitle("警告");
					// Dialog.setMessage("帳號或密碼錯誤").setCancelable(false);
					Dialog.setIcon(android.R.drawable.ic_dialog_info);
					Dialog.setMessage("帳號或密碼錯誤").setCancelable(true).setNegativeButton("確定", new DialogInterface.OnClickListener() { // 按下abort
								// 將thread結束
								// 隱藏progressbar
								@Override
								public void onClick(DialogInterface dialog, int which) {

									// Thread.currentThread().interrupt();
									
									dialog.dismiss();
									// dialog.cancel();
								}
							});
					Dialog.show();
					// Dialog.create().dismiss();
				} else if (loginreturn[2] == "false" || loginreturn[3] == "false")
				// no response from http,ask user to wait or retry
				{
					progressDialog.dismiss();
					AlertDialog.Builder Dialog = new AlertDialog.Builder(animation.this); // Dialog
					Dialog.setTitle("警告");
					Dialog.setMessage("網路異常導致連線逾時");
					Dialog.setIcon(android.R.drawable.ic_dialog_info);
					Dialog.setNeutralButton("重試", new DialogInterface.OnClickListener() { // 按下retry
								// 將thread結束
								// 再跑一個新的thread
								@Override
								public void onClick(DialogInterface dialog, int which) {
									Thread.currentThread().interrupt();
									// 重新執行
									new ConnectHttp().execute();
								}
							});
					Dialog.setNegativeButton("終止", new DialogInterface.OnClickListener() { // 按下abort
								// 將thread結束
								// 隱藏progressbar
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
									Thread.currentThread().interrupt();
									// ConnectHttp.cancel(ConnectCancel);
									dialog.dismiss();
									// dialog.cancel();
								}
							});
					Dialog.show();
				}

				else {
					progressDialog.dismiss();
					AlertDialog.Builder Dialog = new AlertDialog.Builder(animation.this); // Dialog
					Dialog.setTitle("警告");
					Dialog.setMessage("登入失敗");
					Dialog.setIcon(android.R.drawable.ic_dialog_info);
					Dialog.setNeutralButton("重試", new DialogInterface.OnClickListener() { // 按下retry
								// 將thread結束
								// 再跑一個新的thread
								@Override
								public void onClick(DialogInterface dialog, int which) {
									Thread.currentThread().interrupt();
									new ConnectHttp().execute();
								}
							});
				}
			}
		}

		@Override
		public void onCancelled() {
			// 停止使用task
			new ConnectHttp().cancel(true);
			Dialog.create().dismiss();
		}

	}
    public static String getIPAddress(Context c) {
    	String in_ip="" ,out_ip="",in_out_ip="";
    	in_ip=getIPV4(c);
    	att_parameter.in_ip=in_ip;
    	try {
        	URL whatismyip = new URL("http://checkip.amazonaws.com");
    		BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
			out_ip = in.readLine();
			att_parameter.out_ip=out_ip;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return in_out_ip="in_ip=0.0.0.0&out_ip=0.0.0.0";
		}
    	in_out_ip ="in_ip=0.0.0.0&out_ip=0.0.0.0";
    	return in_out_ip;
		
    }
	public static String getIPV4(Context c) {
	       WifiManager wifiMan = (WifiManager)c.getSystemService(WIFI_SERVICE);
	       WifiInfo wifiInf = wifiMan.getConnectionInfo();
	       long ip = wifiInf.getIpAddress();
	       if( ip != 0 )
	              return String.format( "%d.%d.%d.%d",
	                     (ip & 0xff),
	                     (ip >> 8 & 0xff),
	                     (ip >> 16 & 0xff),
	                     (ip >> 24 & 0xff));
	       try {
	              for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	                     NetworkInterface intf = en.nextElement();
	                     for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                            InetAddress inetAddress = enumIpAddr.nextElement();
	                            if (!inetAddress.isLoopbackAddress()) {
	                                   return inetAddress.getHostAddress().toString();
	                            }
	                     }
         }
	       } catch (Exception e) {
	        }
	       return "0.0.0.0";
	}
	public void updateContent(Uri location, String mod,String condition,String ip) {
		int id_this;
		String where, content, token, tittle;
		String[] Form = { UserSchema._ID, UserSchema._MESSAGETOKEN };
		String[] reretrieve = new String[5];

		Cursor up_content = getContentResolver().query(location, Form, condition, null, null);
		if (up_content.getCount() > 0) {
			up_content.moveToFirst();
			retrieve retreive =new retrieve();
			for (int i = 0; i < up_content.getCount(); i++) {
				token = up_content.getString(1);
				reretrieve = retreive.retrieve_req(token, mod);
				if (reretrieve[0].equals("true")) {
					if(retreive.retrieveFileCount.length > 3){
						content = reretrieve[1].substring(reretrieve[1].indexOf("content=") + 8, reretrieve[1].indexOf("&file"));
					}else{
						content = reretrieve[1].substring(reretrieve[1].indexOf("content=") + 8, reretrieve[1].length()-1);							
					}
					id_this = Integer.valueOf(up_content.getString(0));
					ContentValues values = new ContentValues();									
					if (mod.equals("retrievable")) {
						tittle = reretrieve[1].substring(reretrieve[1].indexOf("subject=") + 8, reretrieve[1].indexOf("&content="));
						values.put(UserSchema._CONTENT, content);
						values.put(UserSchema._TITTLE, tittle);
						values.put(UserSchema._USESTATUS, "");
						values.put(UserSchema._FILEPATH, "");
					}else{
						values.put(UserSchema._FILENAME, content);
					}
					
					where = UserSchema._ID + " = " + id_this;
					getContentResolver().update(location, values, where, null);

				}
				up_content.moveToNext();
			}
			
		}
		up_content.close();
	}
}