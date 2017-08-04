package kissmediad2d.android;

import java.text.SimpleDateFormat;
import java.util.Date;

import login.submit1.regFromServer;
import tab.list.att_parameter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import database.storage.messageProcess;

public class sms extends Activity implements OnClickListener{
	private static final int UPDATE_LIST_MES = 0;
	EditText recsms;
	String smscode;
	Boolean keeprun=true;
	Button Bvalidate;
	Thread aliveThread;
	public String sms = null;
	long time;
	String phone;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Validate");
		Intent data = getIntent();
		Bundle bundle = data.getExtras();
		// 取得傳入的contact,說是filename 其實是sender(寄件者)
		phone = bundle.getString("phone");
		// TODO Put your code here
		setContentView(R.layout.sms);
		recsms = (EditText) this.findViewById(R.id.recsms);
		Bvalidate=(Button) this.findViewById(R.id.Bvalidate);
		Bvalidate.setOnClickListener(this);
		aliveThread = new Thread(runnable);
		aliveThread.start();
	}
	
	private Handler mHandler = new Handler() {
		// 此方法在ui線程運行
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_LIST_MES:
				recsms.setText(smscode);
				
				break;
			}
		}
	};
	// 建立工作清單，每30秒檢查
	Runnable runnable = new Runnable() {
		String smsbody;
		String []splitbody;
		long smstime,timenow,min;
		@Override
		public void run() {
			while (keeprun) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						Log.i("update list thread", "Thread.currentThread().isInterrupted()");
					}
					
					Uri SMS_INBOX = Uri.parse("content://sms/inbox");
					Cursor new_sms_cursor = getContentResolver().query(SMS_INBOX, new String[] { "address", "date", "body" }, "address='0903410141' or address='+886903410141'", null, null);
					if(new_sms_cursor.getCount()>0){
						new_sms_cursor.moveToFirst();
						for (int i = 0; i < new_sms_cursor.getCount(); i++) {
							smsbody=sms = new_sms_cursor.getString(new_sms_cursor.getColumnIndex("body"));
							splitbody=smsbody.split("&");
							if(splitbody.length==1){
								//取得簡訊時間
								smstime=Long.parseLong(new_sms_cursor.getString(new_sms_cursor.getColumnIndex("date")));
								//Date mDate = new Date();
								//取得現在時間
								//timenow=mDate.getTime();
								//檢查時間是否在規定內
								
								Date mDate = new Date();
								time=mDate.getTime();
						
								min=(smstime-time)/(1000*60);
								if(min>=0){
									keeprun=false;
									//splitbody=smsbody.split(" ");
									smscode=smsbody.substring(smsbody.indexOf(':')+1,smsbody.indexOf(','));
									mHandler.obtainMessage(UPDATE_LIST_MES).sendToTarget(); // 傳送要求更新list的訊息給handler
									break;
								}else{
									splitbody=null;
									new_sms_cursor.moveToNext();
								}
							}else{
								splitbody=null;
								new_sms_cursor.moveToNext();
							}
							
						}	//for end
				}//if end
				new_sms_cursor.close();
			}//while end
		}
	};
	/*
	 * 當按下註冊紐時所要做的事
	 */
	private class validate extends AsyncTask<Void, Void, String> {
		AlertDialog.Builder Dialog = new AlertDialog.Builder(sms.this);
		ProgressDialog progressDialog = null;
		
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(sms.this, "請稍候", "驗證中", true);
		}
		@Override
		protected String doInBackground(Void... arg0) {
			
			regFromServer validte=new regFromServer();
			String resp=validte.validate(smscode);

			// TODO Auto-generated method stub
			return resp;
		}
		@Override
		protected void onPostExecute(String resp) {
			progressDialog.dismiss();
			String res=new String();
			Boolean result =att_parameter.chechsuccess(resp);
			if(result){
				res="true";
			}
			else{
				res="false";
			}
			if(res.equalsIgnoreCase("true")){
				AlertDialog.Builder Dialog = new AlertDialog.Builder(sms.this); // Dialog
				Dialog.setTitle("恭喜");
				Dialog.setMessage("註冊成功，請按確定鍵返回主畫面");
				Dialog.setIcon(android.R.drawable.ic_dialog_info);
				Dialog.setNegativeButton("確定", new DialogInterface.OnClickListener() { // 按下abort
							// 將thread結束
							// 隱藏progressbar
							// 設定按鈕當按下時結束，結束這個Dialog並中斷這個thread
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Intent intent = new Intent();
								intent.setClass(sms.this, logininput.class);
								// startActivityForResult(intent,0);
								startActivity(intent);
							}
						});
				Dialog.show();

			}else{
				String [] errmsg=resp.split("&msg=");
				if(errmsg[1].equalsIgnoreCase("verify code Error")){
					Dialog.setTitle("抱歉，使用到錯誤的簡訊驗證");
					Dialog.setMessage("請等待接收新的驗證簡訊");
					Dialog.setIcon(android.R.drawable.ic_dialog_info);
					Dialog.setNegativeButton("確定", new DialogInterface.OnClickListener() { // 按下abort
								@Override
								public void onClick(DialogInterface dialog, int which) {
									keeprun=true;
								}
							});
					Dialog.show();
				}else if(errmsg[1].equalsIgnoreCase("Verify Code time out")){
					Dialog.setTitle("抱歉，驗證碼逾時");
					Dialog.setMessage("請按確定重發驗證碼簡訊");
					Dialog.setIcon(android.R.drawable.ic_dialog_info);
					Dialog.setNegativeButton("確定", new DialogInterface.OnClickListener() { // 按下abort
								@Override
								public void onClick(DialogInterface dialog, int which) {
									keeprun=true;
								}
							});
					Dialog.show();
				}

			}
		}
			
		
	}
	//註冊紐所要執行的thread,這會呼叫自定義底層的class,與Server做溝通
	private class registAgain extends AsyncTask<Void, Void, String[]> // implement
																		// thread
	{
		String[] regreturn = new String[5];
		String[] conreturn = new String[4];
		String response;
		ProgressDialog pdialog;
		AlertDialog.Builder Dialog = new AlertDialog.Builder(sms.this);
		@Override
		protected void onPreExecute() {
			// 開啟資料傳送dialog
			pdialog = ProgressDialog.show(sms.this, "請稍候", "重新要求註冊碼中", true);
			pdialog.show();
		}
		@Override
		// 一呼叫就會執行的函式，在背景中執行的事
		protected String[] doInBackground(Void... params) {
			regFromServer regist = new regFromServer();
			String requestString = "user=" + "" + "&password=" + ""
					+ "&phone=" + phone+ "&sms=" + "";

			// regist是自定義的regfromserver
			// 會回傳regreturn[0]:verifycode [1]:有找到[2]:沒找到[3][4]:發生例外
			
			response = regist.regist(requestString);
			regist=null;
			boolean result =att_parameter.chechsuccess(response);
			if(result){
				regreturn[0]="true";
			}else{
				
				regreturn[0]="false";
			}
			return regreturn;
		}

		@Override
		// 背景工作完成後要做的事情，傳入的是verifycode
		protected void onPostExecute(String[] regreturn) {
			pdialog.dismiss();
			if (regreturn[0] == "true") {
				AlertDialog.Builder Dialog = new AlertDialog.Builder(sms.this); // Dialog
				Dialog.setTitle("要求成功!");
				Dialog.setMessage("請等待新的驗證碼");
				Dialog.setIcon(android.R.drawable.ic_dialog_info);
				Dialog.setNegativeButton("確定", new DialogInterface.OnClickListener() { // 按下abort
							// 將thread結束
							// 隱藏progressbar
							// 設定按鈕當按下時結束，結束這個Dialog並中斷這個thread
							@Override
							public void onClick(DialogInterface dialog, int which) {
							
							}
						});
				Dialog.show();
			}else{

			}

		}

		// 取消時要做的事
		protected void onCancelled() {
		}
	}
	
	public void checkSMS() {

		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); // 將date
		String tempdate;
		// uri是連結內建DB的一個方式，這裡要去抓簡訊資料庫中的收件夾
		Uri SMS_INBOX = Uri.parse("content://sms/inbox");
		Cursor new_sms_cursor = getContentResolver().query(SMS_INBOX, new String[] { "address", "date", "body" }, "address='0903410141' or address='+886903410141'", null, null);

		// 檢查是否有新的sms
		if (new_sms_cursor.getCount() > 0) {
			new_sms_cursor.moveToFirst();
			for (int i = 0; i < new_sms_cursor.getCount(); i++) {

				// decode到一般人看得懂得型式
				Date d = new Date(Long.parseLong(new_sms_cursor.getString(new_sms_cursor.getColumnIndex("date"))));
				tempdate = dateFormat.format(d);

				sms = new_sms_cursor.getString(new_sms_cursor.getColumnIndex("body"));
				messageProcess MsgSave = new messageProcess();
				MsgSave.insertdata(getContentResolver(), sms, tempdate);
				MsgSave = null;
				// 自定義的函式			
				new_sms_cursor.moveToNext();
			}// end for
		}// end if
		new_sms_cursor.close();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.Bvalidate:
			new validate().execute();
			break;
		}
		
	}
}
