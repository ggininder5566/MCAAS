package kissmediad2d.android;

import java.util.Date;

import login.submit1.regFromServer;
import tab.list.att_parameter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.iangclifton.android.floatlabel.FloatLabel;

public class register extends Activity implements OnClickListener {
	/**
	 * 此葉面是來自於loginActivity的intent,
	 * 這裡是用來做註冊帳號用的，註冊完後需要用email驗證，驗證成功才能登入
	 * 	 */
	private Button reg, back;
	private FloatLabel regid, regpw, regpwagain, regphone;
	private String getId, getPw, getRetype, getPhone,getsms="False";
	regFromServer regist;
	ProgressDialog pdialog = null;
	boolean getRes = false;
	private ToggleButton regsb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO Put your code here
		setTitle("Register");
		regist = new regFromServer();
		setContentView(R.layout.register);
		reg = (Button) findViewById(R.id.reg);
		back = (Button) findViewById(R.id.back);
		regid = (FloatLabel) findViewById(R.id.RegID);
		regpw = (FloatLabel) findViewById(R.id.RegPassword);
		regpwagain = (FloatLabel) findViewById(R.id.RegPasswordAgain);
		regphone = (FloatLabel) findViewById(R.id.RegPhone);
		//regmail = (FloatLabel) findViewById(R.id.mail);

		reg.setOnClickListener(this);
		back.setOnClickListener(this);
	}
	/*
	 * 當按下註冊紐時所要做的事
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.reg:
			//取得畫面上所有editext的資訊
			getId = regid.getEditText().getText().toString();
			getPw = regpw.getEditText().getText().toString();
			getRetype = regpwagain.getEditText().getText().toString();
			getPhone = regphone.getEditText().getText().toString();
			//getmail = regmail.getEditText().getText().toString();
			/*
			 * 判斷各個欄位是否為空
			 * */
			ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = CM.getActiveNetworkInfo();

			if (getId.equals("") | getPw.equals("") | getRetype.equals("")
					| getPhone.equals("")  ) {
				Toast.makeText(getApplicationContext(), "尚有欄位可為空，請繼續填寫",
						Toast.LENGTH_SHORT).show();

			} else if (!(Linkify.addLinks(regphone.getEditText().getText(),
					Linkify.PHONE_NUMBERS))) {//檢查號碼是否符合格適用的method
				Toast.makeText(getApplicationContext(),
						"電話錯誤，必須是由0-9數字組成的10位數，請重新填寫", Toast.LENGTH_SHORT)
						.show();
			}

			else if (!(getPw.equalsIgnoreCase(getRetype))) // 如果密碼與確認密碼相同
			{
				Toast.makeText(getApplicationContext(), "兩次輸入的密碼不同，請重新填寫",
						Toast.LENGTH_SHORT).show();
			} 
			else if(info == null || !info.isAvailable()){
				Toast.makeText(register.this, "無可用網路", Toast.LENGTH_LONG).show();
			}else {

				new registThread().execute();
			}
			break;
		case R.id.back:
			finish();
			break;
		}
	}
	
	//辨識目前選擇是sms還是wlan
	public OnCheckedChangeListener sbcheck = new OnCheckedChangeListener() {
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked) {
				getsms = "True";
			} else {
				getsms = "False";
			}
			
		}
	};
	
	//註冊紐所要執行的thread,這會呼叫自定義底層的class,與Server做溝通
	private class registThread extends AsyncTask<Void, Void, String[]> // implement
																		// thread
	{
		String[] regreturn = new String[5];
		String[] conreturn = new String[4];
		String response;
		AlertDialog.Builder Dialog = new AlertDialog.Builder(register.this);
		@Override
		protected void onPreExecute() {
			// 開啟資料傳送dialog
			pdialog = ProgressDialog.show(register.this, "請稍候", "註冊中", true);
			pdialog.show();
		}
		@Override
		// 一呼叫就會執行的函式，在背景中執行的事
		protected String[] doInBackground(Void... params) {

			String requestString = "user=" + getId + "&password=" + getPw
					+ "&phone=" + getPhone+ "&sms=" + getsms;

			// regist是自定義的regfromserver
			// 會回傳regreturn[0]:verifycode [1]:有找到[2]:沒找到[3][4]:發生例外
			
			response = regist.regist(requestString);
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
				Intent intent = new Intent();
				Bundle content =new Bundle();
				content.putString("phone",getPhone);
	
				intent.putExtras(content);
				intent.setClass(register.this, sms.class);
				// startActivityForResult(intent,0);
				startActivity(intent);
			}else{
				AlertDialog.Builder Dialog = new AlertDialog.Builder(register.this); // Dialog
				Dialog.setTitle("註冊失敗");
				Dialog.setMessage("抱歉，該用戶名稱已被使用，請輸入其他用戶名稱");
				Dialog.setIcon(android.R.drawable.ic_dialog_info);
				Dialog.setNegativeButton("確定", new DialogInterface.OnClickListener() { // 按下abort
							// 將thread結束
							// 隱藏progressbar
							// 設定按鈕當按下時結束，結束這個Dialog並中斷這個thread
							@Override
							public void onClick(DialogInterface dialog, int which) {
								regid.getEditText().setText("");
							}
						});
				Dialog.show();
			}

		}

		// 取消時要做的事
		protected void onCancelled() {
			new registThread().cancel(true);
			Dialog.create().dismiss();
		}
	}

}
