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
	 * ��animimation������A�I�s��LoginActivity�A �o�̬O�B�z�n�J����T�A�P�ɤ]�i�H�޾ɦܵ��U����
	 * ���s���S�Ψ�
	 */
	private ImageView login, register, exit; // �ŧi�ΨӦs�����ܼ�
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
		// �������D
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// �]�w���ù�
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // set
																														// fullscreen
		// �]�w��activity�ҥΪ�layout
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
	 * ��q�t�@��activity�����즹Loginactivity��A�����|���ӳo
	 */
	@Override
	protected void onResume() {
		super.onResume();
		et.setText("ll");
		et2.setText("ll");
		etip.setText("163.20.52.40:8100");
	}

	/**
	 * ��ť�blayouy�W���Ҧ�buttonn�A�ݬO���@�ӫ��s�Q���U�A�N�h���۹������u�@
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {// ��ť�O����button�Q���U
		case R.id.Logip:
			etip.setText("163.20.52.40:8100");
			etip.setSelection(0, homeIp.length());
			break;
		case R.id.submit:
			try {
				// ���o�����A�Ȫ�����
				ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo info = CM.getActiveNetworkInfo();

				State InternetState = info.getState();
				System.out.println("InternetState = " + InternetState);
				getid = et.getText().toString();
				getpw = et2.getText().toString();
				getip = etip.getText().toString();
				// �P�_��J���b�K�O�_���j��20
				if (getid.length() > 20 || getpw.length() > 20)
					Toast.makeText(this, "ID or Password can not over 20 characters", Toast.LENGTH_LONG).show();
				// �P�_�O�_������
				else if (info == null || !info.isAvailable()) {
					Toast.makeText(this, "�L�i�κ���", Toast.LENGTH_LONG).show();
				} else {
					// ����n�J���ʧ@
					new ConnectHttp().execute();

				}
			} catch (Exception e) {
				Toast.makeText(this, "�L�i�κ���", Toast.LENGTH_LONG).show();
			}
			// GoToTab();
			break;
		case R.id.register:
			// �۩w�q��function�A�޾ɦܵ��U����
			GoToRegister();
			break;
		case R.id.Exit:
			finish();
			break;
		}
	}

	// �إ�intent�A�ϥ�intent����k�A�i�H�qcalss A ����calss B�A�Y����������
	private void GoToRegister() {
		Intent intent = new Intent();
		// �qLoginActivity ���� register
		intent.setClass(LoginActivity.this, register.class);
		startActivity(intent);
	}

	/*
	 * �ϥ�AsyncTask����k�A�i��O�ɪ��u�@���I���h����A
	 * AsyncTask���T�Ӥ�k�i�ϥΡA���O�OonPreExecute�BdoInBackground�BonPostExecute
	 * �h�]�w������e�B���椤�B����� �U�n������Ʊ�
	 */
	private class ConnectHttp extends AsyncTask<Void, Void, String[]> {

		AlertDialog.Builder Dialog = new AlertDialog.Builder(LoginActivity.this);
		ProgressDialog progressDialog = null;

		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(LoginActivity.this, "�еy��", "�n�J��", true);
			progressDialog.show();

		}

		// �b�I�����榹method�A���槹�|����onPostExecute
		@Override
		public String[] doInBackground(Void... params) {
			
			String[] loginreturn = new String[7];
			// �Ngetid�Pgetpw�s�@���n�J��T�A�������s�u�ǤJserver���Ѽ�
			LoginrequestString = "username=" + getid + "&password=" + getpw;
			login_name = LoginrequestString;
			/*
			 * �ϥ�login����login method�A�^�ǭȬO�@�Ӱ}�C loginreturn[0]�O�Ψ��ˬd���L���\
			 * loginreturn[1]�Oserver�^�Ǫ��T��
			 */
			locupdate locup =new locupdate();
			loginreturn = locup.login(getip, LoginrequestString);
			//�ϥ�ssdp�M��port
			
            String ipp=getIPAddress(getApplicationContext());

            System.out.println(ipp);
            
            //�����D,3G�L�k�ϥ�
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

		// onPostExecute �|���� doInBackground ��return
		@Override
		protected void onPostExecute(String loginreturn[]) {
			progressDialog.dismiss();
			if (loginreturn[0] == "true") // �p�G��server���^��
			{	
				Boolean result =att_parameter.chechsuccess(loginreturn[1]);
				if (result) // �Y�b���K�X���T ��U�@��
				{
					ContentValues values = new ContentValues();
					values.put(UserSchema._ACCOUNT, getid);
					values.put(UserSchema._PASSWORD, getpw);

					getContentResolver().insert(Uri.parse("content://tab.list.d2d/user_info"), values);
					
					// ����Dialog���T��
					
					Intent intent = new Intent();
					// ���N�Ϸ|�I�sLoginActivity.class�o
					//intent.setClass(LoginActivity.this, LoginActivity.class);
					intent.setClass(LoginActivity.this, MainFragment.class);
					
					// �}�ҳo�Ӭ���
					startActivity(intent);
					LoginActivity.this.finish();
				}
			} else {
				if (loginreturn[4] == "false") {
					
					System.out.println("Test LoginSKE");
					AlertDialog.Builder Dialog = new AlertDialog.Builder(LoginActivity.this); // Dialog
					Dialog.setTitle("ĵ�i");
					Dialog.setMessage("�L�Ī����A����}�A�нT�{IP��}");
					Dialog.setIcon(android.R.drawable.ic_dialog_info);
					Dialog.setNegativeButton("�T�w", new DialogInterface.OnClickListener() { // ���Uabort
								// �Nthread����
								// ����progressbar
								// �]�w���s����U�ɵ����A�����o��Dialog�ä��_�o��thread
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
					Dialog.setTitle("ĵ�i");
					// Dialog.setMessage("�b���αK�X���~").setCancelable(false);
					Dialog.setIcon(android.R.drawable.ic_dialog_info);
					Dialog.setMessage("�b���αK�X���~").setCancelable(true).setNegativeButton("�T�w", new DialogInterface.OnClickListener() { // ���Uabort
								// �Nthread����
								// ����progressbar
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
					Dialog.setTitle("ĵ�i");
					Dialog.setMessage("�������`�ɭP�s�u�O��");
					Dialog.setIcon(android.R.drawable.ic_dialog_info);
					Dialog.setNeutralButton("����", new DialogInterface.OnClickListener() { // ���Uretry
								// �Nthread����
								// �A�]�@�ӷs��thread
								@Override
								public void onClick(DialogInterface dialog, int which) {
									Thread.currentThread().interrupt();
									// ���s����
									new ConnectHttp().execute();
								}
							});
					Dialog.setNegativeButton("�פ�", new DialogInterface.OnClickListener() { // ���Uabort
								// �Nthread����
								// ����progressbar
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
					Dialog.setTitle("ĵ�i");
					Dialog.setMessage("�n�J����");
					Dialog.setIcon(android.R.drawable.ic_dialog_info);
					Dialog.setNeutralButton("����", new DialogInterface.OnClickListener() { // ���Uretry
								// �Nthread����
								// �A�]�@�ӷs��thread
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
			// ����ϥ�task
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
	// ��s��Ʈw���A
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