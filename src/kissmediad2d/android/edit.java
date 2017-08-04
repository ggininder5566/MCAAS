package kissmediad2d.android;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.support.igd.PortMappingListener;
import org.teleal.cling.support.model.PortMapping;

import login.submit1.EditAccount;
import login.submit1.user;
import net.sbbi.upnp.messages.UPNPResponseException;
import softwareinclude.ro.portforwardandroid.asyncTasks.WebServerPluginInfo;
import softwareinclude.ro.portforwardandroid.asyncTasks.openserver;
import softwareinclude.ro.portforwardandroid.network.UPnPPortMapper;
import softwareinclude.ro.portforwardandroid.util.ApplicationConstants;
import tab.list.att_parameter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.github.kevinsawicki.http.HttpRequest;
import com.iangclifton.android.floatlabel.FloatLabel;

public class edit extends Activity implements OnClickListener{
	/**
	 * �o��O�Ψӽs��ϥΪ̪���T�A�i�H�ק�K�X��q�ܸ��X
	 */
	private TextView name;
	private FloatLabel newpw, newpwagain, phone;
	private ToggleButton smsbutton;
	private NumberPicker timePicker;
	public static Switch open_server;
    String H="1";
    String M;

	ArrayAdapter<String> adapter;
	String getNotifyMethod = new String(), smsstate = new String(), getNewPw = new String(), getComfirm = new String(), getphone = new String();

	EditAccount editAccount = new EditAccount();

	user userdata = new user();
	ProgressDialog pdialog = null;
	boolean getRes = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.edit);
		

        
		setTitle("Edit");
		TextView ok = (Button) findViewById(R.id.okbutton);
		name = (TextView) findViewById(R.id.username);
		phone = (FloatLabel) findViewById(R.id.user_phone);
		newpw = (FloatLabel) findViewById(R.id.NewPassword);
		newpwagain = (FloatLabel) findViewById(R.id.NewPasswordAgain);
		open_server = (Switch) findViewById(R.id.switch1);
		open_server.setChecked(att_parameter.nat);
		ok.setOnClickListener(this);
		open_server.setOnCheckedChangeListener(LisServer);
		new getuser().execute();

	}

	@Override
	public void onClick(View v) {
		getNewPw = newpw.getEditText().getText().toString();
		getComfirm = newpwagain.getEditText().getText().toString();
		getphone = phone.getEditText().getText().toString();
		//�P�_���O�_����
		if (getNewPw.equals("") | (getNewPw.equals("") & getComfirm.equals(""))) {
			Toast.makeText(getApplicationContext(), "�K�X�B���i���šA�Э��s��g", Toast.LENGTH_SHORT).show();
		} else if (getComfirm.equals("")) {
			Toast.makeText(getApplicationContext(), "���ƱK�X�B���i���šA�Э��s��g", Toast.LENGTH_SHORT).show();

		} else {
			switch (v.getId()) {
			case R.id.okbutton:
				//�ˬd�⦸�K�X�O�_�ۦP
				if (getNewPw.equalsIgnoreCase(getComfirm)) {
					pdialog = ProgressDialog.show(edit.this, "�еy��", "�ק襤", true);
					new Thread() {
						public void run() {
							try {
								while (!getRes) {
									Thread.sleep(1000);
								}

							} catch (Exception e) {
								Log.i("Thread", "dialog error");
							} finally {
								pdialog.dismiss();
							}
						}
					}.start();

					new edit_profile().execute();
					new WaitForResponse().execute();
				} else {
					Toast.makeText(getApplicationContext(), "�⦸�K�X��J���P�A�Э��s��g", Toast.LENGTH_SHORT).show();

				}

				break;

			}
		}

	}
	//���ѥثe��ܬOsms�٬Owlan
	public OnCheckedChangeListener LisServer = new OnCheckedChangeListener() {

		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			
			if (isChecked) {
			    

				LayoutInflater factory = LayoutInflater.from(edit.this);
				final View v1 = factory.inflate(R.layout.servertime, null);
				timePicker = (NumberPicker) v1.findViewById(R.id.timePicker);
				timePicker.setMaxValue(24);  
				timePicker.setMinValue(1);    
				timePicker.setValue(1);AlertDialog.Builder dialog = new AlertDialog.Builder(edit.this);
				dialog.setTitle("�п�ܤ����~�}��ɶ�");
				dialog.setView(v1);
				timePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener (){
		             public void onValueChange(NumberPicker view, int oldValue, int newValue) {
		                 
		            	 H = String.valueOf(newValue);
			                System.out.println("�o�̬OH="+H);
		                 
		               
		             }
		        });
				dialog.setPositiveButton("�T�w", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {


						
						if(att_parameter.out_ip.equals("0.0.0.0")){
							open_server.setChecked(false);
							Toast.makeText(getApplicationContext(), "�|���}��wifi�A�L�k�}��d2d�\��", Toast.LENGTH_LONG).show();					
						}
//						else if(att_parameter.in_ip.equals(att_parameter.out_ip)&& att_parameter.out_ip != "0.0.0.0"){
//							open_server.setChecked(false);
//							Toast.makeText(getApplicationContext(), "��p�A�ثe���Ҥ��A�X�}��d2d�\��", Toast.LENGTH_LONG).show();					
//						
//						}
						else {
				    		ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				    		final NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
							String name=mNetworkInfo.getTypeName();
							if(mNetworkInfo.getTypeName().equalsIgnoreCase("MOBILE")){
								Toast.makeText(getApplicationContext(), "��p�A��ʺ������Ҥ��A�X�}��D2D�\��", Toast.LENGTH_LONG).show();
								open_server.setChecked(false);
							}else{
								att_parameter.nat = true;

								new preserver().execute();
							}
							//������ �n��nat

}
					}

				});
				
				dialog.setNegativeButton("����", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						open_server.setChecked(false);
					}
				});
				dialog.show();

			} else {
				if(att_parameter.nat){
					att_parameter.nat=false;
					new posserver(att_parameter.out_ip,att_parameter.port,"").execute();
					logininput.server.stop();
					
					 System.out.println("Server stopped.\n");
//				     Intent intent = new Intent(edit.this,CopyOfAddPortAsync.class);
//				     stopService(intent);
				     }
				else{
					att_parameter.nat=false;
				//�o����P���q

				}

			}

		}
	};
    static String TimeFix(int c){
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }
	 public class posserver extends AsyncTask<Void, Void, String> {
		 String state=new String();
		    private UPnPPortMapper uPnPPortMapper;
		    private String externalIP;
		    private int externalPort;
		    private String message;
			ProgressDialog senddialog;
		    public posserver(String externalIP,int externalPort,String msg) {
		    this.message = msg;
		    this.externalIP = externalIP;
			this.externalPort = externalPort;
		}
			@Override
		    protected void onPreExecute() {
		        super.onPreExecute();
		        uPnPPortMapper = new UPnPPortMapper();
		        if(message.equalsIgnoreCase("")){
		        	senddialog = ProgressDialog.show(edit.this, "�еy��", "server������", true);
					
		        }else{
		        	 senddialog = ProgressDialog.show(edit.this, "�еy��", message, true);
		        }
		       
				
				senddialog.show();
		    }
			@Override
			protected String doInBackground(Void... params) {
		        if(uPnPPortMapper != null){
		            try {	        
		            	user user = new user();
		            	String res =user.setservicetime("H=-"+H+"&M=0");
		            	user=null;

		            	uPnPPortMapper.removePort(externalIP, externalPort);		  					
		            	logininput.server.stop();
		            }
		            catch (IOException e) {
		            	state="no";
		                e.printStackTrace();
		            } catch (UPNPResponseException e) {
		            	state="no";
		                e.printStackTrace();
		            }
		            state="yes";
		        }
				return state;
			}
			protected void onPostExecute(String state) {
				senddialog.dismiss();
				open_server.setChecked(att_parameter.nat);
				if(state.equalsIgnoreCase("no")){
					Toast.makeText(edit.this, "server��������", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(edit.this, "server�������\", Toast.LENGTH_SHORT).show();
				}
			}
		}

	class preserver extends AsyncTask<Void, Void, String> // implement thread
	{	 
		private UPnPPortMapper uPnPPortMapper;
		String state=new String();
		ProgressDialog senddialog;
		@Override
		protected void onPreExecute() {
			
			uPnPPortMapper = new UPnPPortMapper();
			senddialog = ProgressDialog.show(edit.this, "�еy��", "server�}�Ҥ�", true);
			
			senddialog.show();
		// �}�Ҹ�ƶǰedialog

	}
		@Override
		protected String doInBackground(Void... params)  { // �@�I�s�N�|���檺�禡
            if(uPnPPortMapper != null){
    			// ���response�r��A��0�}�l��A��";"�e������
                                   try {
									uPnPPortMapper.openRouterPort(att_parameter.out_ip, att_parameter.port,att_parameter.in_ip,att_parameter.port, ApplicationConstants.ADD_PORT_DESCRIPTION);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (UPNPResponseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
//            PortMapping desiredMapping =
//                    new PortMapping(
//                    		att_parameter.port,
//                            att_parameter.in_ip,
//                            PortMapping.Protocol.TCP,
//                            "My Port Mapping"
//                    );
//            final WifiManager wifiManager =(WifiManager) getSystemService(Context.WIFI_SERVICE);
//            UpnpService upnpService =
//                    new UpnpServiceImpl(
//                            new PortMappingListener(desiredMapping)
//                    );
//
//            upnpService.getControlPoint().search();
                
			// Defaults
	        int port = att_parameter.port;
//	        int port=33102;
//	        att_parameter.port=port;
	        String host = att_parameter.in_ip; // bind to all interfaces by default
	        //String host = att_parameter.out_ip;
	        List<File> rootDirs = new ArrayList<File>();
	        boolean quiet = false;
	        Map<String, String> options = new HashMap<String, String>();

	        if (rootDirs.isEmpty()) {
	            rootDirs.add(new File(Environment.getExternalStorageDirectory().getAbsolutePath()));
	        }

	        options.put("host", host);
	        options.put("port", "" + port);
	        options.put("quiet", String.valueOf(quiet));
	        StringBuilder sb = new StringBuilder();
	        for (File dir : rootDirs) {
	            if (sb.length() > 0) {
	                sb.append(":");
	            }
	            try {
	                sb.append(dir.getCanonicalPath());
	            } catch (IOException ignored) {
	            }
	        }
	        options.put("home", sb.toString());
	        ServiceLoader<WebServerPluginInfo> serviceLoader = ServiceLoader.load(WebServerPluginInfo.class);

	        for (WebServerPluginInfo info : serviceLoader) {
	            String[] mimeTypes = info.getMimeTypes();
	            for (String mime : mimeTypes) {
	                String[] indexFiles = info.getIndexFilesForMimeType(mime);
	                if (!quiet) {
	                    System.out.print("# Found plugin for Mime type: \"" + mime + "\"");
	                    if (indexFiles != null) {
	                        System.out.print(" (serving index files: ");
	                        for (String indexFile : indexFiles) {
	                            System.out.print(indexFile + " ");
	                        }
	                    }
	                    System.out.println(").");
	                }
	                //registerPluginForMimeType(indexFiles, mime, info.getWebServerPlugin(mime), options);
	            }
	        }
	        logininput.server =new openserver(host, port, rootDirs, quiet);
	        logininput.server.setContent(getContentResolver());
	        try {
	        	logininput.server.start();
	            
	        } catch (IOException ioe) {
	            System.err.println("Couldn't start server:\n" + ioe);
	            System.exit(-1);
	             state="no";
	        }
	        state="ok";
	        System.out.println("Server started, Hit Enter to stop.\n");
	        user user = new user();
	        Calendar c = Calendar.getInstance(); 
	    	 String res =user.setservicetime("H="+H+"&M="+c.get(Calendar.MINUTE));
	    	 user=null;
	    	 if(!att_parameter.chechsuccess(res)){
	    		 state="time_error";
	    	 }

		}
			return state;
		}
		protected void onPostExecute(String state) {
			senddialog.dismiss();
			if(state.equalsIgnoreCase("no")){
				att_parameter.nat=false;
				Toast.makeText(edit.this, "server�}�ҥ���", Toast.LENGTH_SHORT).show();
			}else if(state.equalsIgnoreCase("time_error")){
				att_parameter.nat=false;
				new posserver(att_parameter.out_ip,att_parameter.port,"�}��ɶ��]�w����,server�N�۰�����").execute();
				logininput.server.stop();
			}
			else{
				Toast.makeText(edit.this, "server�}�Ҧ��\", Toast.LENGTH_SHORT).show();
			}
				
			
			
		}
	}
	private class getuser extends AsyncTask<Void, Void, String[]> {

		@Override
		protected void onPreExecute() {
			// �}�Ҹ�ƶǰedialog
			pdialog = ProgressDialog.show(edit.this, "�еy��", "���Ū����", true);
			pdialog.show();
		}

		@Override
		protected String[] doInBackground(Void... params) {
			String[] userinfo = new String[4];
			userinfo = userdata.getuser();
			return userinfo;
		}

		protected void onPostExecute(String[] userinfo) {
			pdialog.dismiss();
			//userinfo�q1�}�l���O�O user_name user_phone user_sms
			name.setText(userinfo[1]);
			phone.getEditText().setText(userinfo[2]);
			//toLowerCase()�ק�r�����g�p


		}
	}

	private class edit_profile extends AsyncTask<Void, Void, Void> // implement thread
	{
		@Override
		protected Void doInBackground(Void... params) { // �@�I�s�N�|���檺�禡
			String requsest = "new_password=" + getNewPw + "&new_phone=" + getphone + "&new_sms=" + smsstate;
			editAccount.setrequsest(requsest);
			editAccount.edituser();
			getRes = editAccount.getResponse();

			return null;
		}

	}

	private class editsms extends AsyncTask<Void, Void, Void> // implement
																// thread
	{
		@Override
		protected Void doInBackground(Void... params) { // �@�I�s�N�|���檺�禡
			editAccount.editnotify(smsstate);
			return null;
		}

	}

	private class WaitForResponse extends AsyncTask<Void, Void, Void> // implement
																		// thread
	{

		@Override
		protected Void doInBackground(Void... params) { // �@�I�s�N�|���檺�禡
			while (!getRes)
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					e.printStackTrace();
				}

			return null;
		}

		protected void onPostExecute(Void noUse) // ��u�@������ �|�I�s���禡
		{
			String getLine = editAccount.getRegReadLine();
			Pattern pattern = Pattern.compile("ret=0"); // check correct
			Matcher matcher = pattern.matcher(getLine);

			if (matcher.find()) {
				AlertDialog.Builder Dialog = new AlertDialog.Builder(edit.this); // Dialog
				Dialog.setTitle("");
				Dialog.setMessage("�ק令�\");
				Dialog.setIcon(android.R.drawable.ic_dialog_info);
				Dialog.setNeutralButton("�T�w", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						newpw.getEditText().setText("");
						newpwagain.getEditText().setText("");

					}
				});
				Dialog.show();
			} else {
				AlertDialog.Builder Dialog = new AlertDialog.Builder(edit.this); // Dialog
				Dialog.setTitle("");
				Dialog.setMessage("�ק異��");
				Dialog.setIcon(android.R.drawable.ic_dialog_info);
				Dialog.setNeutralButton("�T�w", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
				Dialog.show();
			}

		}

	}
}
