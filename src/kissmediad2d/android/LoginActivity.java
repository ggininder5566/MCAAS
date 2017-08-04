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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import database.storage.messageProcess;

public class LoginActivity extends Activity implements OnClickListener {
	/*
	 * 當animimation結束後，呼叫此LoginActivity， 這裡是處理登入的資訊，同時也可以引導至註冊頁面
	 * 但新版沒用到
	 */
	private ImageView login, register, exit; // 宣告用來存取的變數
	private EditText et, et2, etip;
	private String getid, getpw, getip, homeIp = "";
	private boolean LoginNPE = true;
	public String LoginrequestString = new String();
	public int id;
	public int i = 0;
	public static String login_name_old;
	public static String Homeip="163.20.52.40:8100";
	public static String login_name;
	ProgressDialog dialog;


	public String getLoginrequestString() {
		return LoginrequestString;
	}

	public LoginActivity() {
	}

	public int getid() {

		return id;
	}

	ProgressDialog pdialog = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 取消標題
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 設定全螢幕
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // set
																														// fullscreen
		// 設定該activity所用的layout
		setContentView(R.layout.main_old);
		login = (ImageView) findViewById(R.id.submit);
		register = (ImageView) findViewById(R.id.register);
		exit = (ImageView) findViewById(R.id.Exit);
		et = (EditText) findViewById(R.id.ID);
		et2 = (EditText) findViewById(R.id.Password);
		etip = (EditText) findViewById(R.id.Logip);
		etip.setClickable(true);
		login.setOnClickListener(this);
		register.setOnClickListener(this);
		exit.setOnClickListener(this);
		etip.setOnClickListener(this);

	}

	/*
	 * 當從另一個activity切換到此Loginactivity後，首先會跳來這
	 */
	@Override
	protected void onResume() {
		super.onResume();
		et.setText("ll");
		et2.setText("ll");
		etip.setText("163.20.52.40:8100");
	}

	/**
	 * 監聽在layouy上的所有buttonn，看是哪一個按鈕被按下，就去做相對應的工作
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {// 監聽是哪個button被按下
		case R.id.Logip:
			etip.setText("163.20.52.40:8100");
			etip.setSelection(0, homeIp.length());
			break;
		case R.id.submit:
			try {
				// 取得網路服務的實體
				ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo info = CM.getActiveNetworkInfo();

				State InternetState = info.getState();
				System.out.println("InternetState = " + InternetState);
				getid = et.getText().toString();
				getpw = et2.getText().toString();
				getip = etip.getText().toString();
				// 判斷輸入的帳密是否有大於20
				if (getid.length() > 20 || getpw.length() > 20)
					Toast.makeText(this, "ID or Password can not over 20 characters", Toast.LENGTH_LONG).show();
				// 判斷是否有網路
				else if (info == null || !info.isAvailable()) {
					Toast.makeText(this, "無可用網路", Toast.LENGTH_LONG).show();
				} else {
					// 執行登入的動作
					new ConnectHttp().execute();

				}
			} catch (Exception e) {
				Toast.makeText(this, "無可用網路", Toast.LENGTH_LONG).show();
			}
			// GoToTab();
			break;
		case R.id.register:
			// 自定義的function，引導至註冊頁面
			GoToRegister();
			break;
		case R.id.Exit:
			finish();
			break;
		}
	}

	// 建立intent，使用intent的方法，可以從calss A 跳至calss B，即頁面的切換
	private void GoToRegister() {
		Intent intent = new Intent();
		// 從LoginActivity 跳到 register
		intent.setClass(LoginActivity.this, register.class);
		startActivity(intent);
	}

	/*
	 * 使用AsyncTask的方法，可把費時的工作丟到背景去執行，
	 * AsyncTask有三個方法可使用，分別是onPreExecute、doInBackground、onPostExecute
	 * 去設定說執行前、執行中、執行後 各要做什麼事情
	 */
	private class ConnectHttp extends AsyncTask<Void, Void, String[]> {

		AlertDialog.Builder Dialog = new AlertDialog.Builder(LoginActivity.this);
		ProgressDialog progressDialog = null;

		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(LoginActivity.this, "請稍候", "登入中", true);
			progressDialog.show();

		}

		// 在背景執行此method，執行完會跳到onPostExecute
		@Override
		public String[] doInBackground(Void... params) {
			
			String[] loginreturn = new String[7];
			// 將getid與getpw製作成登入資訊，做為日後連線傳入server的參數
			LoginrequestString = "username=" + getid + "&password=" + getpw;
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
			progressDialog.dismiss();
			if (loginreturn[0] == "true") // 如果有server有回應
			{	
				Boolean result =att_parameter.chechsuccess(loginreturn[1]);
				if (result) // 若帳號密碼正確 到下一頁
				{
					ContentValues values = new ContentValues();
					values.put(UserSchema._ACCOUNT, getid);
					values.put(UserSchema._PASSWORD, getpw);

					getContentResolver().insert(Uri.parse("content://tab.list.d2d/user_info"), values);
					
					// 關掉Dialog的訊息
					
					Intent intent = new Intent();
					// 此意圖會呼叫LoginActivity.class這
					//intent.setClass(LoginActivity.this, LoginActivity.class);
					intent.setClass(LoginActivity.this, MainFragment.class);
					
					// 開啟這個活動
					startActivity(intent);
					LoginActivity.this.finish();
				}
			} else {
				if (loginreturn[4] == "false") {
					
					System.out.println("Test LoginSKE");
					AlertDialog.Builder Dialog = new AlertDialog.Builder(LoginActivity.this); // Dialog
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
					

					AlertDialog.Builder Dialog = new AlertDialog.Builder(LoginActivity.this); // Dialog
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
					
					AlertDialog.Builder Dialog = new AlertDialog.Builder(LoginActivity.this); // Dialog
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
					
					AlertDialog.Builder Dialog = new AlertDialog.Builder(LoginActivity.this); // Dialog
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
	// 更新資料庫狀態
	public void updateContent(Uri location, String mod,String condition,String ip) {
		int id_this;
		String where, content, token, tittle;
		String[] Form = { UserSchema._ID, UserSchema._MESSAGETOKEN };
		String[] reretrieve = new String[7];

		Cursor up_content = getContentResolver().query(location, Form, condition, null, null);
		if (up_content.getCount() > 0) {
			up_content.moveToFirst();
			retrieve retreive =new retrieve();
			for (int i = 0; i < up_content.getCount(); i++) {
				token = up_content.getString(1);
				reretrieve = retreive.retrieve_req(token, mod);
				if (reretrieve[1].equals("true")) {
					if(retreive.retrieveFileCount.length > 3){
						content = reretrieve[0].substring(reretrieve[0].indexOf("content=") + 8, reretrieve[0].indexOf("&file"));
					}else{
						content = reretrieve[0].substring(reretrieve[0].indexOf("content=") + 8, reretrieve[0].length()-1);							
					}
					id_this = Integer.valueOf(up_content.getString(0));
					ContentValues values = new ContentValues();									
					if (mod.equals("retrievable")) {
						tittle = reretrieve[0].substring(reretrieve[0].indexOf("subject=") + 8, reretrieve[0].indexOf("&content="));
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