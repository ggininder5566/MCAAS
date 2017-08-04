package kissmediad2d.android;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.conn.util.InetAddressUtils;
import org.teleal.cling.android.AndroidUpnpService;
import org.teleal.cling.binding.LocalServiceBindingException;
import org.teleal.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.teleal.cling.model.DefaultServiceManager;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.meta.DeviceDetails;
import org.teleal.cling.model.meta.DeviceIdentity;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.LocalService;
import org.teleal.cling.model.meta.ManufacturerDetails;
import org.teleal.cling.model.meta.ModelDetails;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.UDADeviceType;
import org.teleal.cling.model.types.UDAServiceType;
import org.teleal.cling.model.types.UDN;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.Toast;
import database.storage.messageProcess;
import ffmpeg.process.ffmpeg_service;
import login.submit1.Login;
import login.submit1.Logout;
import login.submit1.locupdate;
import login.submit1.retrieve;
import login.submit1.user;
import softwareinclude.ro.portforwardandroid.asyncTasks.openserver;
import tab.list.att_parameter;
import tab.list.fileContentProvider;
import tab.list.fileContentProvider.UserSchema;
import upnp.service.BrowserUpnpService;
import upnp.service.SwitchPower;
import upnp.service.UPnPDeviceFinder;
public class logininput extends Activity implements OnClickListener, PropertyChangeListener {
	// �s�����n�J�e��
	private MyBroadcastReceiver myBroadcastReceiver;
	private EditText et, et2;
	private CheckedTextView ct;
	private String getid, getpw, getip;
	private boolean LoginNPE = true;
	private int cs = 0,ss=0;
	private static final int UPDATE_LIST_MES = 0,UPDATE_FFMPEG=1,FFMPEG=2,timeout=3,change_content=4,SHOW_NOTIFY=5;
	private boolean finishUpdateList = false;
	public boolean retrieving = true;
	public String sms = null;
	public String LoginrequestString = new String();
	public static String login_name;
	public static String Homeip = "163.20.52.40:8100";
	public static String method;
	static openserver server;
	public static String connect_ip,connect_port;
	Boolean idle=true;
	String[] aliveIp;
	String notify_msg=new String();
	ContentResolver conten;
	private NotificationManager gNotMgr = null;
	public String[] rep, req;
	Button no_bn, in_bn, send_bn, ed_bn, log_bn, reg_bn;
	locupdate locup =new locupdate();
	ProgressDialog pdialog = null,re_ffmpeg_dialog=null,Dialog_for_login = null;
	int tolen,hasRead;
	String content;
	Logout logout = new Logout();
	Thread aliveThread;
	Thread listThread;
	fileContentProvider KM_DB = new fileContentProvider();
	int i = 0;
	List<String> fileList = new ArrayList<String>();
	String filesize = null;
	String bdtoken =new String();
	String bdsender =new String();
	String bdtype =new String();
	String bdreretrieve = new String();
	String butoken =new String();
	String busender =new String();
	ffmpeg_service cc ;
	Activity qq;
	String upid=new String(),downid=new String();
	public String sdcardPath = Environment.getExternalStorageDirectory().toString() + File.separator + "KM" + "/";
	
	

	
	public String getLoginrequestString() {
		return LoginrequestString;
	}

	/**
	 * �o�̷|�@���b�I���{������A�]���n�D�ʥh��s��ơA�ˬd�O�_���s���q��
	 */
	private Handler mHandler = new Handler() {
		// ����k�bui�u�{�B��
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_LIST_MES:
				checkSMS();
				break;
			case UPDATE_FFMPEG:
			if(hasRead<=tolen){
				re_ffmpeg_dialog.setProgress(hasRead);
			}else{
				re_ffmpeg_dialog.dismiss();
			}
			break;
			case FFMPEG:
				cc= new ffmpeg_service();
				final String[] temp_ffmpeg = {UserSchema._ID,UserSchema._FILEPATH,UserSchema._TOTAL_TIME,UserSchema._SEQ_ID,UserSchema._DURATION,UserSchema._SELFID};
				Cursor check_ffmpeg = getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_ffmpeg"), temp_ffmpeg, null, null, null);
				if(check_ffmpeg.getCount()>0){
					check_ffmpeg.moveToFirst();
					String id=check_ffmpeg.getString(0);
					String path=check_ffmpeg.getString(1);
					float total_time=Float.valueOf(check_ffmpeg.getString(2));
					int split_seq=Integer.valueOf(check_ffmpeg.getString(3));
					int duration=Integer.valueOf(check_ffmpeg.getString(4));
					String selfid=check_ffmpeg.getString(5);				
					
					//String req=selfid+"&"+
					if(split_seq==0){
						cc.ffmpeg(logininput.this,getContentResolver(),path, split_seq, duration,selfid,total_time,split_seq);
					}else{
						cc.ffmpeg(logininput.this,getContentResolver(),path, (split_seq * duration)-1, duration,selfid,total_time,split_seq);
					}
					//cc=null;
				}else{
					att_parameter.ffmpeg_now=false;
				}
				check_ffmpeg.close();
				 break;
			case timeout: 
					AlertDialog.Builder Dialog0 = new AlertDialog.Builder(logininput.this); // Dialog
					Dialog0.setTitle("�s�u�O��");
					Dialog0.setMessage("�еy�Ԧb��");
					Dialog0.setIcon(android.R.drawable.ic_dialog_info);
					Dialog0.setNeutralButton("�T�w", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						}
					});
					Dialog0.show();
				break;
			case change_content:
				
				Dialog_for_login.setMessage(content);
				break;
			case SHOW_NOTIFY:
				Toast.makeText(getApplicationContext(),notify_msg, ss).show();
				notify_msg="";
				ss=0;
				break;
			}
		}
	};
	// �إߤu�@�M��A�C30���ˬd
	Runnable runnable = new Runnable() {
		final String[] check_down = {UserSchema._MESSAGETOKEN,UserSchema._SENDER,UserSchema._TYPE};
		
		@Override
		public void run() {
			while (!finishUpdateList) {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						//Thread.currentThread().interrupt();
						Log.i("update list thread", "Thread.currentThread().isInterrupted()");
					}
//					if (!retrieving){
//						mHandler.obtainMessage(UPDATE_LIST_MES).sendToTarget(); // �ǰe�n�D��slist���T����handler
//					}

					ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
					NetworkInfo info = CM.getActiveNetworkInfo();
					if (info == null || !info.isAvailable()) {
						System.out.println("�ثe�S��������");
					}else{
						mHandler.obtainMessage(UPDATE_LIST_MES).sendToTarget(); // �ǰe�n�D��slist���T����handler
						String [] aliveIp = locup.locationupdate(Login.latest_cookie,logininput.getIPAddress(getApplicationContext()),att_parameter.port);
						if (aliveIp.length>0&&aliveIp[1].equalsIgnoreCase("true")){
								messageProcess MsgSave = new messageProcess();
								MsgSave.checkwlan(getContentResolver(), aliveIp[4]);
								updateContent(Uri.parse("content://tab.list.d2d/user_data"), "retrievable","content is null");
								updateContent(Uri.parse("content://tab.list.d2d/user_reply"), "reply","filename is null");
								MsgSave=null;
								if( aliveIp[4].length()>20){
									Pattern pattern_content = Pattern.compile("content");
									Matcher matcher_content = pattern_content.matcher(aliveIp[4]);
									Pattern pattern_d2d = Pattern.compile("d2d");
									Matcher matcher_d2d = pattern_d2d.matcher(aliveIp[4]);
									if(matcher_content.find()){
										final int notifyID = 2;
										final int requestCode = notifyID;
										
										
										//final int flags = PendingIntent.FLAG_CANCEL_CURRENT;
										Bundle bundle=new Bundle();
										 bundle.putString("location","0" );
										// intent.setClass(writepage.this, browse.class);
										
										Intent notificationIntent = new Intent(getApplicationContext(), MainFragment.class);
										notificationIntent.putExtras(bundle);
										notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
										PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), requestCode, notificationIntent, 0);
										//final Notification notification = new Notification.Builder(getApplicationContext()).setSmallIcon(R.drawable.icon).setContentTitle("���e���D").setContentText("���e��r").setContentIntent(contentIntent).setAutoCancel(true).build(); // �إ߳q��
										gNotMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
										final Notification notification = new Notification.Builder(getApplicationContext()).setSmallIcon(R.drawable.kmicon).setContentTitle("���s�T��").setContentText("���H�o�G���ɸ�T���A").setDefaults(Notification.DEFAULT_SOUND).setContentIntent(contentIntent).setAutoCancel(true).build(); // �إ߳q��
										gNotMgr.notify(notifyID, notification); // �o�e�q��
										notify_msg="���s�T���A�֬�!";
										ss=1;
										mHandler.obtainMessage(SHOW_NOTIFY).sendToTarget();
									}
									else if(matcher_d2d.find()){
										final int notifyID = 2;
										final int requestCode = notifyID;	
										//final int flags = PendingIntent.FLAG_CANCEL_CURRENT;
										Bundle bundle=new Bundle();
										 bundle.putString("location","0" );
										// intent.setClass(writepage.this, browse.class);
										
										Intent notificationIntent = new Intent(getApplicationContext(), MainFragment.class);
										notificationIntent.putExtras(bundle);
										notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
										PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), requestCode, notificationIntent, 0);
										//final Notification notification = new Notification.Builder(getApplicationContext()).setSmallIcon(R.drawable.icon).setContentTitle("���e���D").setContentText("���e��r").setContentIntent(contentIntent).setAutoCancel(true).build(); // �إ߳q��
										gNotMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
										final Notification notification = new Notification.Builder(getApplicationContext()).setSmallIcon(R.drawable.kmicon).setContentTitle("�K!���H��A�o�G�����ɷP�즳����").setContentText("�ЧA����M2M�s�u�A��").setDefaults(Notification.DEFAULT_SOUND).setContentIntent(contentIntent).setAutoCancel(true).build(); // �إ߳q��
										gNotMgr.notify(notifyID, notification); // �o�e�q��
										notify_msg="���s�T���A�֬�!";
										ss=2;
										mHandler.obtainMessage(SHOW_NOTIFY).sendToTarget();
									
									}

								}
								aliveIp=null;
						}
					}
					info=null;
					CM=null;
				}
		}
	};

	// �إߤu�@�M��A�C10���ˬd
	Runnable alive_run = new Runnable() {
		@Override
		public void run() {
			String self_id,file_where,token=new String(),d2d_id;
			final String[] check_down = {UserSchema._MESSAGETOKEN,UserSchema._SENDER,UserSchema._TYPE};
			final String[] check_up = {UserSchema._MESSAGETOKEN,UserSchema._SENDER,UserSchema._ID,UserSchema._SENDER,UserSchema._SELFID};
			final String[] id_up = {UserSchema._MESSAGETOKEN,UserSchema._SELFID};
			final String[] temp_up = {UserSchema._ID,UserSchema._SELFID};
			final String[] temp_ffmpeg = {UserSchema._ID,UserSchema._FILEPATH,UserSchema._TOTAL_TIME,UserSchema._SEQ_ID,UserSchema._DURATION,UserSchema._SELFID};
			while (!finishUpdateList) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					//Thread.currentThread().interrupt();
					
				}
				ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo info = CM.getActiveNetworkInfo();
				if (info == null || !info.isAvailable()) {
					System.out.println("�ثe�S��������");
				}else{
					Cursor down = getContentResolver().query(Uri.parse("content://tab.list.d2d/user_data"), new String[]{UserSchema._NOTIFICATION,UserSchema._MESSAGETOKEN,UserSchema._USESTATUS}, null, null, null);
					if(down.getCount()>0){
						down.moveToFirst();
						for(int i=0;i<down.getCount();i++){
							System.out.println("TEST_1 = "+down.getString(0));
							System.out.println("TEST_12 = "+down.getString(1));
							System.out.println("TEST_13 =" +down.getString(2));
							down.moveToNext();
						}
					}down.close();
					Cursor down_cursor = getContentResolver().query(Uri.parse("content://tab.list.d2d/user_data"), check_down, "(notification = '1' or notification = '2') and userstatus=''", null, null);//201608
					if(down_cursor.getCount()>0){
						down_cursor.moveToFirst();
						for (int i = 0; i < down_cursor.getCount(); i++) {
							while(!idle){
								try {
									Thread.sleep(2000);
								} catch (InterruptedException e) {
									//Thread.currentThread().interrupt();
									
								}
							}
							if(bdtoken.equalsIgnoreCase(down_cursor.getString(0))){
								//do noting
							}
							else{
								idle=false;
								bdtoken =down_cursor.getString(0);
								bdsender =down_cursor.getString(1);
								bdtype =down_cursor.getString(2);							
								if(bdtype.equalsIgnoreCase("wlan")||bdtype.equalsIgnoreCase("sms")){
									retrieve retrieve = new retrieve();
									bdreretrieve = retrieve.check_file(bdtoken);
									Boolean res=att_parameter.chechsuccess(bdreretrieve);
									if (res) {
										updateNotification("3", bdtype,bdtoken);
										new startretrieve().execute();
									}
									else{
										idle=true;
										bdtoken="";
									}
									retrieve =null;
								}else if(bdtype.equalsIgnoreCase("d2d")){
									user user = new user();
									String[] res =user.getservicetime(bdsender);
									
									if(res[1].equalsIgnoreCase("true")){
										String[] socketport = res[2].replace("socket=", "").split(":");
										//���Xip ��port
										//�ˬd�ؼЬO�_�����~��
										if(att_parameter.out_ip.equals(socketport[0])){
											//�~��ip�ۦP�A������ip
											logininput.connect_ip=socketport[1];
											System.out.println("�o��O d2d socketport[1]="+socketport[1]);
										}else{
											//�~��ip���P�A���~��
											logininput.connect_ip=socketport[0];
											System.out.println("�o��O d2d socketport[0]="+socketport[0]);
										}
										logininput.connect_port=socketport[2];
										updateNotification("3", "d2d",bdtoken);
										new startretrieve().execute();
										
									}else{
										idle=true;
										bdtoken="";
									}								
									user = null;
								}
								down_cursor.moveToNext();
								
							}

						}
						
					}
					down_cursor.close();
				
					Cursor check_ffmpeg = getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_ffmpeg"), temp_ffmpeg, null, null, null);
					if(check_ffmpeg.getCount()>0){
						Log.i("ffmpeg_login", "�o�̭n�}�l��ffmpeg");
						if(!att_parameter.ffmpeg_now){					
							check_ffmpeg.moveToFirst();
							String selfid=check_ffmpeg.getString(5);
							if(selfid.equalsIgnoreCase(att_parameter.selfid)){
								//do nothing
							}else{
								att_parameter.ffmpeg_now=true;
								mHandler.obtainMessage(FFMPEG).sendToTarget();
							}
						}else{
							//�ثe���H�٦b��
							//do noting
						}		
					}else{
						//do nothing
					}
					check_ffmpeg.close();

					//d2d�M��
					Cursor get_d2d_id = getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_content"), id_up,"finish ='yes'", null, null);
					//�ˬd���ɮ׬O�_���Χ���
					if(get_d2d_id.getCount()>0){
						get_d2d_id.moveToFirst();
						for(int i=0;i<get_d2d_id.getCount();i++){
							token=get_d2d_id.getString(0);
							d2d_id=get_d2d_id.getString(1);
							//��覬�T�T���n�D�ɮסA���o�{�o�S��token�A�N��token�S����A���s�o�e�n�D���^token
							if(token == null){
								//����republish�A�n�Dtoken,����ϥΫD�P�B
							}else{
								//token�s�b�A�]���Χ���
								Cursor file_token = getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_file"), temp_up, "selfid='"+d2d_id +"' and messagetoken is null", null, null);
								//�ˬdtemp_file�����ɮ׬O�_�]��s�Wtoken
								if(file_token.getCount()>0){
									//�i��token��s
									ContentValues values = new ContentValues();
									file_token.moveToFirst();
									values.put(UserSchema._MESSAGETOKEN, token);
									for (int a = 0; a < file_token.getCount(); a++) {
										int id_this = Integer.parseInt(file_token.getString(0));
										file_where = UserSchema._ID + " = " + id_this;
										getContentResolver().update(Uri.parse("content://tab.list.d2d/temp_file"), values, file_where, null);
										file_token.moveToNext();
									}
									
								}else{
									//temp_file����token�w�g������s
								}
								file_token.close();
								//��sreply��ready
								
							}
							get_d2d_id.moveToNext();
						}

						
						
					
					}else{
						//�ɮ��٨S���Φn�A���ݭI���۰ʤ���
					}
					get_d2d_id.close();
					Cursor ch_id = getContentResolver().query(Uri.parse("content://tab.list.d2d/user_reply"), check_up, "ready is null", null, null);
					//�o�{���٨SREADY����ơA�N��ӵ��٤���W�ǡA�}�l�v�@�ˬd��]
					if(ch_id.getCount()>0){
						Log.i("LOGININPUT", "�}�l�P�_");
						ch_id.moveToFirst();
						for(int i =0;i<ch_id.getCount();i++){
							self_id=ch_id.getString(4);
							Cursor get_id = getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_content"), id_up, "selfid='"+self_id +"' and finish ='yes'", null, null);
							//�ˬd���ɮ׬O�_���Χ���
							if(get_id.getCount()>0){
								get_id.moveToFirst();
								token=get_id.getString(0);
								//��覬�T�T���n�D�ɮסA���o�{�o�S��token�A�N��token�S����A���s�o�e�n�D���^token
								if(token.equals("")){
									//����republish�A�n�Dtoken,����ϥΫD�P�B
								}else{
									//token�s�b�A�]���Χ���
									Cursor file_token = getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_file"), temp_up, "selfid='"+self_id +"' and messagetoken is null", null, null);
									//�ˬdtemp_file�����ɮ׬O�_�]��s�Wtoken
									if(file_token.getCount()>0){
										//�i��token��s
										ContentValues values = new ContentValues();
										file_token.moveToFirst();
										values.put(UserSchema._MESSAGETOKEN, token);
										for (int a = 0; a < file_token.getCount(); a++) {
											int id_this = Integer.parseInt(file_token.getString(0));
											file_where = UserSchema._ID + " = " + id_this;
											getContentResolver().update(Uri.parse("content://tab.list.d2d/temp_file"), values, file_where, null);
											file_token.moveToNext();
										}
										
									}else{
										//temp_file����token�w�g������s
									}
									file_token.close();
									//��sreply��ready
									ContentValues values = new ContentValues();
									values.put(UserSchema._READY, "yes");
									int id_this = Integer.parseInt(ch_id.getString(2));
									file_where = UserSchema._ID + " = " + id_this;
									getContentResolver().update(Uri.parse("content://tab.list.d2d/user_reply"), values, file_where, null);
								}
								
								
							
							}else{
								//�ɮ��٨S���Φn�A���ݭI���۰ʤ���
							}
							get_id.close();
							
							ch_id.moveToNext();
						}
						Log.i("LOGININPUT", "�����P�_");
					}else{
						//�S���o�{ready���Ū��A�N��reply���ˬd�L�F
					}
					ch_id.close();
				}
				info=null;
				CM=null;

			}
		}
	};
    
	private static final Logger log = Logger.getLogger(logininput.class.getName());

    private AndroidUpnpService upnpService;

    private UDN udn = UDN.uniqueSystemIdentifier("Demo Binary Light");

    private ServiceConnection serviceConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            upnpService = (AndroidUpnpService) service;

            LocalService<SwitchPower> switchPowerService = getSwitchPowerService();

            // Register the device when this activity binds to the service for the first time
            if (switchPowerService == null) {
                try {
                    LocalDevice binaryLightDevice = createDevice();

                    Toast.makeText(logininput.this, R.string.registering_demo_device, Toast.LENGTH_SHORT).show();
                    upnpService.getRegistry().addDevice(binaryLightDevice);

                    switchPowerService = getSwitchPowerService();

                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Creating demo device failed", ex);
                    Toast.makeText(logininput.this, R.string.create_demo_failed, Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Obtain the state of the power switch and update the UI
            setLightbulb(switchPowerService.getManager().getImplementation().getStatus());

            // Start monitoring the power switch
            switchPowerService.getManager().getImplementation().getPropertyChangeSupport()
                    .addPropertyChangeListener(logininput.this);

        }

        public void onServiceDisconnected(ComponentName className) {
            upnpService = null;
        }
    };
    
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getPropertyName().equals("status")) {
            log.info("Turning light: " + event.getNewValue());
            setLightbulb((Boolean) event.getNewValue());
        }
    }

    protected void setLightbulb(final boolean on) {
        runOnUiThread(new Runnable() {
            public void run() {
 }
        });
    }

    protected LocalService<SwitchPower> getSwitchPowerService() {
        if (upnpService == null)
            return null;

        LocalDevice binaryLightDevice;
        if ((binaryLightDevice = upnpService.getRegistry().getLocalDevice(udn, true)) == null)
            return null;

        return (LocalService<SwitchPower>)
                binaryLightDevice.findService(new UDAServiceType("MASP", 1));
    }

    protected LocalDevice createDevice()
            throws ValidationException, LocalServiceBindingException {

        DeviceType type =
                new UDADeviceType("BinaryLight", 1);

        DeviceDetails details =
                new DeviceDetails(
                        "MASP",
                        new ManufacturerDetails("ACME"),
                        new ModelDetails("1705A_MASP", "TEST UPNP", "v1")
                );

        LocalService service =
                new AnnotationLocalServiceBinder().read(SwitchPower.class);

        service.setManager(
                new DefaultServiceManager<SwitchPower>(service, SwitchPower.class)
        );

        return new LocalDevice(
                new DeviceIdentity(udn),
                type,
                details,
                service
        );
    }
	
	private void updateState(String state, String conditional) {
		Cursor change_state = getContentResolver().query(Uri.parse("content://tab.list.d2d/user_data"), new String[] { UserSchema._ID, UserSchema._RECEIVEID }, conditional, null, null);
		if (change_state.getCount() > 0) {
			change_state.moveToFirst();
			for (int i = 0; i < change_state.getCount(); i++) {
				int id_this = 0;
				id_this = Integer.valueOf(change_state.getString(0));
				ContentValues values = new ContentValues();
				values.put(UserSchema._USESTATUS, state);
				String where = UserSchema._ID + " = " + id_this;
				getContentResolver().update(Uri.parse("content://tab.list.d2d/user_data"), values, where, null);
				change_state.moveToNext();
			}

		}
		change_state.close();
	}
	public void replaceSeq(String conditional, String mod,String name) {
		int id_this;
		String where;
		Cursor check_fileid_cursor = getContentResolver().query(Uri.parse("content://tab.list.d2d/user_data"), new String[] { UserSchema._ID, UserSchema._RECEIVEID, UserSchema._FILEPATH }, conditional, null, null);

		if (check_fileid_cursor.getCount() > 0) {
			check_fileid_cursor.moveToFirst();
			ContentValues values = new ContentValues();
			// ���]split �����e�O &10
			String[] split = (name).split("_-");
			// ���]check_id �����e�O&10&11&12&13
			String check_id = check_fileid_cursor.getString(1);
			// split_id[0~4]�����e���O�O"",10,11,12,13
			String[] split_id = check_id.split("&");
			// ���]split_id�j��2
			if (split_id.length != 2) {
				// &10& ������ &
				// �ҥH&10&11&12&13 �|�ܦ� &11&12&13
				check_id = check_id.replace("&" + split[0] + "&", "&");
			} else {// ��split_id���׵���2
				// split_id[0~1]�����e�� "",13
				// &13 ������ ""
				// �ҥH&13 �|����
				check_id = check_id.replace("&" + split[0], "");
			}
			if (mod.equals("again")) {
				String check_path = check_fileid_cursor.getString(2) + "&" + name;
				values.put(UserSchema._FILEPATH, check_path);
			}
			System.out.println(check_id);
			values.put(UserSchema._RECEIVEID, check_id);
			id_this = Integer.parseInt(check_fileid_cursor.getString(0));
			where = UserSchema._ID + " = " + id_this;
			getContentResolver().update(Uri.parse("content://tab.list.d2d/user_data"), values, where, null);
		}
		check_fileid_cursor.close();
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.newlogin);
		mDevfinder=null;
		if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        getApplicationContext().bindService(
                new Intent(this, BrowserUpnpService.class),
                serviceConnection,
                Context.BIND_AUTO_CREATE
        );
        File file = new File(sdcardPath);
        if(!file.exists()){
        	file.mkdir();
        }
		no_bn = (Button) this.findViewById(R.id.no_bn);
		in_bn = (Button) this.findViewById(R.id.in_bn);
		send_bn = (Button) this.findViewById(R.id.send_bn);
		ed_bn = (Button) this.findViewById(R.id.ed_bn);
		log_bn = (Button) this.findViewById(R.id.log_bn);
		reg_bn = (Button) this.findViewById(R.id.reg_bn);
		no_bn.setEnabled(false);
		in_bn.setEnabled(false);
		send_bn.setEnabled(false);
		ed_bn.setEnabled(false);

		no_bn.setOnClickListener(this);
		in_bn.setOnClickListener(this);
		send_bn.setOnClickListener(this);
		ed_bn.setOnClickListener(this);
		log_bn.setOnClickListener(this);
		reg_bn.setOnClickListener(this);

		// ����sqlite��DB�������A�s�W�@��table�A�s��Ҧ�"�H��̪�²�T"
		KM_DB.new_table(Uri.parse("content://tab.list.d2d/user_data"));
		// ����"�H���"
		KM_DB.new_table(Uri.parse("content://tab.list.d2d/user_group"));
		// �������W���ɮ�
		KM_DB.new_table(Uri.parse("content://tab.list.d2d/user_reply"));
		KM_DB.new_table(Uri.parse("content://tab.list.d2d/user_info"));
		KM_DB.new_table(Uri.parse("content://tab.list.d2d/temp_content"));
		KM_DB.new_table(Uri.parse("content://tab.list.d2d/temp_ffmpeg"));
		String[] TestForm = { UserSchema._ID, UserSchema._SENDER, UserSchema._TITTLE, UserSchema._CONTENT, UserSchema._MESSAGETOKEN, UserSchema._FILESIZE, UserSchema._DATE, UserSchema._FILEPATH, UserSchema._RECEIVEID, UserSchema._USESTATUS, UserSchema._FILEID };
		updateState("", TestForm, "userstatus!='delete'");
		conten=getContentResolver();
		qq=logininput.this;
		Cursor info_cursor = getContentResolver().query(Uri.parse("content://tab.list.d2d/user_info"), null, null, null, null);
		if(info_cursor.getCount()==0){
			ContentValues values = new ContentValues();
			values.put(UserSchema._REMEMBER, "false");
			getContentResolver().insert(Uri.parse("content://tab.list.d2d/user_info"), values);
			
			values = null;
		}
		info_cursor.close();
		Cursor cursor = getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_file"), new String[] {UserSchema._SELFID,UserSchema._MESSAGETOKEN }, null, null, null);
		   if(cursor.getCount()>0){
			   cursor.moveToFirst();
			   for(int a=0;a<cursor.getCount();a++){
				   String aa=cursor.getString(0);
				   String bb=cursor.getString(1);
				   String cc="";
				   cursor.moveToNext();
			   }
		   }
		   cursor.close();
			myBroadcastReceiver = new MyBroadcastReceiver();
			IntentFilter intentFilter = new IntentFilter(VideoTrimmingService.ACTION_MyIntentService);
			intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

			registerReceiver(myBroadcastReceiver, intentFilter);


	}



	private class startretrieve extends AsyncTask<Void, Void, String[]> // implement
	// thread
	{

		public String[] aliveIp;
		public String checkfile = new String();
		public String getFilename = null;
		public String[] getSplit;
		public String token;
		public int id_this;
		public String where;
		String[] reretrieve = new String[5];
		String[] reretrieve_file = new String[5];
		String[] reretrieve_arg = new String[11];
		String[] Form = { UserSchema._ID, UserSchema._RECEIVEID,UserSchema._SENDER};
		retrieve retrieve = new retrieve();
		protected void onPreExecute() {
			final int notifyID = 0;
			
				
			//final Notification notification = new Notification.Builder(getApplicationContext()).setSmallIcon(R.drawable.icon).setContentTitle("���e���D").setContentText("���e��r").setContentIntent(contentIntent).setAutoCancel(true).build(); // �إ߳q��
			gNotMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			final Notification notification = new Notification.Builder(getApplicationContext()).setSmallIcon(android.R.drawable.stat_sys_download).setContentTitle("�ɮפU����").setAutoCancel(false).setProgress(0, 50, false).build(); // �إ߳q��
			gNotMgr.notify(notifyID, notification); // �o�e�q��
			

		}
		@Override
		protected String[] doInBackground(Void... params) {
			idle=false;
			// String[] refile = new String[4];

			aliveIp = locup.locationupdate(Login.latest_cookie,logininput.getIPAddress(getApplicationContext()),att_parameter.port);
			messageProcess MsgSave = new messageProcess();
			MsgSave.checkwlan(getContentResolver(), aliveIp[4]);
			MsgSave = null;
			// ���o�̪�IP
			token = bdtoken;

				retrieve.viewmod = "startview";
				// �o�䪺DB�A�O�n��s�ثe�buser_data����²�T�����A�A�令�U���������A
				// cursor(��ƪ�ӷ�,����������W��,����)
				// ��������W�١Guser_data��ƪ�ID�ȡA����Gitemclick�I���ɩұo��token
				updateState("download", "messagetoken='" + token + "'");
				// ����request���ʧ@�A�ǤJtoken�hserver�A���o��token�ҫ������ɮ�id
				reretrieve = retrieve.retrieve_req_for_d2d(logininput.connect_ip,logininput.connect_port,bdtoken);
				reretrieve_arg[0]=reretrieve[0];
				reretrieve_arg[1]=reretrieve[1];
				reretrieve_arg[2]=reretrieve[2];
				reretrieve_arg[3]=reretrieve[3];
				reretrieve_arg[4]=reretrieve[4];

				
				//reretrieve = retrieve.retrieve_req(bdtoken, "");
				System.out.println("�^�_�����D���B "+reretrieve);
				if (reretrieve[0] == "true") // retrieve_req=ret=0
				{
					notify_msg="���b�����Ӧ۩�: "+logininput.connect_ip+"��"+retrieve.fileid[i].substring(retrieve.fileid[i].indexOf("/KM/")+4,retrieve.fileid[i].length());
					ss=10;
					mHandler.obtainMessage(SHOW_NOTIFY).sendToTarget();
					fileList.clear(); // ���NfileList�M�� �H�KŪ��W������
					getSplit = reretrieve[1].split("&");

						/*
						 * �]���ɮץi��|�U�����ѡA���O���@�ӥ��Ѥ����D�A�ҥH�o��DB���ʧ@���A�������n�U�����ɮ�ID�A
						 * �ҥHRECEIVEID�O���ɮפU�����\�ɡA�ݭ��@�����\�N�hRECEIVEID���R��
						 * ��FILEID�O�Ψӭ��s�U�����γ~
						 */
						Cursor up_fileid = getContentResolver().query(Uri.parse("content://tab.list.d2d/user_data"), Form, "messagetoken='" + token + "'", null, null);
						if (up_fileid.getCount() > 0) {
							up_fileid.moveToFirst();
							ContentValues values = new ContentValues();
							values.put(UserSchema._RECEIVEID, "&"+reretrieve[1]);
							values.put(UserSchema._FILEID, "&"+reretrieve[1]);
							id_this = Integer.parseInt(up_fileid.getString(0));
							where = UserSchema._ID + " = " + id_this;
							getContentResolver().update(Uri.parse("content://tab.list.d2d/user_data"), values, where, null);
							values = null;
							
						}
						up_fileid.close();
						/*
						 * �o��O���ɮת�ID�A����N��������۩w�q��saveBinaryFile�U���ɮסA�����i��
						 * saveBinaryFile�ثe�̪�ip,�H�β{�b�n�U�����@���ɮ�
						 */
						for (int j = 0; j < retrieve.fileid.length; j++) { // ���ɮ�ID
							try {
								
								String DLfilename=new String();
								reretrieve_file =retrieve.saveBinaryFile_for_d2d(token,j,getContentResolver()); // �����ln���ɮ�
								
								reretrieve_arg[5]=reretrieve_file[0];
								reretrieve_arg[6]=reretrieve_file[1];
								reretrieve_arg[7]=reretrieve_file[2];
								reretrieve_arg[8]=reretrieve_file[3];
								reretrieve_arg[9]=reretrieve_file[4];
								if(reretrieve_file[0].equalsIgnoreCase("true")){
									notify_msg="�ثe�w�U������"+(j+1)+"/"+retrieve.fileid.length;
									ss=3;
									mHandler.obtainMessage(SHOW_NOTIFY).sendToTarget();
									DLfilename = retrieve.filename;
									// �z��^�Ǫ�filename�APattern�O�A�n�z�諸���e�AMatcher�O�A�n�d���@�Ӧr�갵�z��
									boolean[] checktype = new boolean[att_parameter.filetype];
									checktype = att_parameter.checktype(DLfilename);
									System.out.println("�^�_�����D���B3 "+DLfilename);
									// �o��DB���ʧ@���A�����w���쪺file id�A�|���_�a�⦨�\��id�A�qDB���R��
									replaceSeq("messagetoken='" + token + "'", "retrive",reretrieve_file[1]);
									// �p�G�o���U�����O�v��
									if (checktype[att_parameter.video]||checktype[att_parameter.photo]) {//201608
										// �N�ɦW�[�J��arraylist
										fileList.add(DLfilename);

									}
								}


							} catch (IOException ex) {
								reretrieve_arg[5]="server error";
								reretrieve_arg[10]="true";
							}catch(Exception ex){
								reretrieve_arg[5]="server error";
								reretrieve_arg[9]="true";
							}

						}
				}
			

			return reretrieve;
		}

		protected void onPostExecute(String[] notuse) {
			String name=new String(),filename=new String();
			String []type;
			updateState("", "messagetoken='" + token + "'");
			if (reretrieve_arg[0] == "true") {
				//retreive���\
				if(reretrieve_arg[5]=="true"){
						/*
						 * cursor(��ƪ�ӷ�,����������W��,����)//��������W�١Guser_data��ƪ�ID�ȡA����
						 * �Gitemclick�I���ɩұo��token
						 * 
						 * �]���w�g�U�����F�A�ҥHuser_state��download����
						 */
						if (fileList.size() == 0) // �Y�ɮפ��Ovideo fileList=0
						{
							getFilename = retrieve.filename;
							type =getFilename.split("\\.");
							filename=getFilename;
						} else // �Y�ɮ׬Ovideo �h��&���ꦨ�@��A�ت��O�n���v���M��(�o�̪��M�椺�e�O�O�ɦW��_�Ӫ�)
						{
							getFilename = "";
							filename=fileList.get(0);
							type=filename.split("\\.");
							for (int i = 0; i < fileList.size(); i++)
								getFilename = getFilename + "&" + token+"_"+fileList.get(i).replace(".", "-_" + String.valueOf(i) + ".");
						
						}
						// �]�����U�����F�A�ҥH��sfilepath
						Cursor up_filepath = getContentResolver().query(Uri.parse("content://tab.list.d2d/user_data"), Form, "messagetoken='" + token + "'", null, null);
						ContentValues values = new ContentValues();
						if (up_filepath.getCount() > 0) {
							up_filepath.moveToFirst();
							values.put(UserSchema._FILEPATH, getFilename);
							int id_this = Integer.parseInt(up_filepath.getString(0));
							name = up_filepath.getString(2);
							//filename=up_filepath.getString(3);
							String where = UserSchema._ID + " = " + id_this;
							getContentResolver().update(Uri.parse("content://tab.list.d2d/user_data"), values, where, null);
						}
						up_filepath.close();
						updateNotification("0", "wlan",bdtoken);
						bdtoken="";
						idle=true;
						//�����D
					
						
						//����Notification����A�ó]�w���ݩ�
						final int notifyID = 0;
						final int requestCode = notifyID;
						final Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); // �q�����Ī�URI�A�b�o�̨ϥΨt�Τ��ت��q������
						//final int flags = PendingIntent.FLAG_CANCEL_CURRENT;
						Intent notificationIntent = new Intent(getApplicationContext(), receivelist.class);
						Bundle bundle = new Bundle();
			    		bundle.putString("contact", name);
			    		notificationIntent.putExtras(bundle);
						notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
						PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), requestCode, notificationIntent, 0);
						//final Notification notification = new Notification.Builder(getApplicationContext()).setSmallIcon(R.drawable.icon).setContentTitle("���e���D").setContentText("���e��r").setContentIntent(contentIntent).setAutoCancel(true).build(); // �إ߳q��
						gNotMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
						final Notification notification = new Notification.Builder(getApplicationContext()).setSmallIcon(android.R.drawable.stat_sys_download_done).setContentTitle( filename+"."+type[1]).setContentText("���\�����Ӧ�"+name+"���ɮ�").setDefaults(Notification.DEFAULT_SOUND).setContentIntent(contentIntent).setAutoCancel(true).build(); // �إ߳q��
						gNotMgr.notify(notifyID, notification); // �o�e�q��
						
						notify_msg="���\�U���Ӧ�"+name+"��"+filename+"."+type[1];
						ss=5;
						mHandler.obtainMessage(SHOW_NOTIFY).sendToTarget();
					}
				else if(reretrieve_arg[5]=="false"){
					//retreive file ���� (�o��200�H�~��code)
					//�nŪbody					
					Pattern pattern_msg = Pattern.compile("message does not exist"); // check file type
					Matcher matcher_msg = pattern_msg.matcher(reretrieve_arg[6]);
					Pattern pattern_file = Pattern.compile("file is not ready"); // check file type
					Matcher matcher_file = pattern_file.matcher(reretrieve_arg[6]);
					Pattern pattern_file_II = Pattern.compile("file not found"); // check file type
					Matcher matcher_file_II = pattern_file_II.matcher(reretrieve_arg[6]);
					if(matcher_msg.find()){
						notify_msg="�T���ä��s�bserver�W�A�ЧR�����T��";
					}else if(matcher_file.find()){
						notify_msg="����ɮת��o�Ϳ��~�A�ЧR�����T��";
					}else if(matcher_file_II.find()){
						notify_msg="���ɮקǸ����s�b�A�ЧR�����T��";
					}
					final int notifyID = 0;
					gNotMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
					final Notification notification = new Notification.Builder(getApplicationContext()).setSmallIcon(android.R.drawable.stat_sys_download).setContentTitle("���~").setContentText(notify_msg).setAutoCancel(false).setProgress(0, 50, false).build(); // �إ߳q��
					gNotMgr.notify(notifyID, notification); // �o�e�q��
					
					ss=3;
					mHandler.obtainMessage(SHOW_NOTIFY).sendToTarget();
				}else{

					//server error
					if(reretrieve_arg[7]!=null && reretrieve_arg[7].equalsIgnoreCase("true")){
						notify_msg="ĵ�i�A���A�������o�Ϳ��~�A�мȰ��ϥ�MD2MD";
					}else if(reretrieve_arg[8]!=null && reretrieve_arg[8].equalsIgnoreCase("true")){
						notify_msg="ĵ�i�A�s�����ѡA�мȰ��ϥ�MD2MD";
					}else if (reretrieve_arg[9]!=null && reretrieve_arg[9].equalsIgnoreCase("true")){
						notify_msg="ĵ�i�A���������~�A�мȰ��ϥ�MD2MD";
					}else if (reretrieve_arg[10]!=null && reretrieve_arg[10].equalsIgnoreCase("true")){
						notify_msg="ĵ�i�A�����x�s�Ŷ��o�Ϳ��~�A�мȰ��ϥ�MD2MD";
					}
					final int notifyID = 0;
					gNotMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
					final Notification notification = new Notification.Builder(getApplicationContext()).setSmallIcon(android.R.drawable.stat_notify_error).setContentTitle("ĵ�i").setContentText(notify_msg).setAutoCancel(false).setProgress(0, 50, false).build(); // �إ߳q��
					gNotMgr.notify(notifyID, notification); // �o�e�q��					
					ss=3;
					mHandler.obtainMessage(SHOW_NOTIFY).sendToTarget();
				}
			}else if(reretrieve_arg[0]=="false"){
				//retreive���� (�o��200�H�~��code)
				//�nŪbody					
				Pattern pattern_msg = Pattern.compile("message does not exist"); // check file type
				Matcher matcher_msg = pattern_msg.matcher(reretrieve_arg[1]);
				Pattern pattern_msg_II = Pattern.compile("file is not ready"); // check file type
				Matcher matcher_msg_II = pattern_msg_II.matcher(reretrieve_arg[1]);
				if(matcher_msg.find()){
					notify_msg="�T���ä��s�bserver�W�A�ЧR�����T��";

				}else if(matcher_msg_II.find()){
					notify_msg="���~���T���s���A�ЧR�����T��";
				}
				final int notifyID = 0;
				gNotMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				final Notification notification = new Notification.Builder(getApplicationContext()).setSmallIcon(android.R.drawable.stat_sys_download).setContentTitle("���~").setContentText(notify_msg).setAutoCancel(false).setProgress(0, 50, false).build(); // �إ߳q��
				gNotMgr.notify(notifyID, notification); // �o�e�q��
				ss=3;
			
				mHandler.obtainMessage(SHOW_NOTIFY).sendToTarget();
			}else{
				//server error
				if(reretrieve_arg[2]!=null && reretrieve_arg[2].equalsIgnoreCase("true")){
					notify_msg="ĵ�i�A���A�������o�Ϳ��~�A�мȰ��ϥ�MD2MD";
					
				}else if(reretrieve_arg[3]!=null && reretrieve_arg[3].equalsIgnoreCase("true")){
					notify_msg="ĵ�i�A�s�����ѡA�мȰ��ϥ�MD2MD";

				}else if (reretrieve_arg[4]!=null && reretrieve_arg[4].equalsIgnoreCase("true")){
					notify_msg="ĵ�i�A���������~�A�мȰ��ϥ�MD2MD";

				}
				final int notifyID = 0;
				gNotMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				final Notification notification = new Notification.Builder(getApplicationContext()).setSmallIcon(android.R.drawable.stat_sys_download).setContentTitle("ĵ�i").setContentText(notify_msg).setAutoCancel(false).setProgress(0, 50, false).build(); // �إ߳q��
				gNotMgr.notify(notifyID, notification); // �o�e�q��
				
				ss=3;
				mHandler.obtainMessage(SHOW_NOTIFY).sendToTarget();
			}			
			
				}// check file else
		} // reretrieve[1]=="true"

	private void updateNotification(String state,String type,String token) {
		Cursor noti_cursor = getContentResolver().query(Uri.parse("content://tab.list.d2d/user_data"), new String[] { UserSchema._ID, UserSchema._NOTIFICATION }, "messagetoken='" + token + "'", null, null);
		if (noti_cursor.getCount() > 0) {
			noti_cursor.moveToFirst();
			ContentValues values = new ContentValues();
			values.put(UserSchema._NOTIFICATION, state);
			values.put(UserSchema._TYPE, type);
			int id_this = Integer.parseInt(noti_cursor.getString(0));
			String where = UserSchema._ID + " = " + id_this;
			getContentResolver().update(Uri.parse("content://tab.list.d2d/user_data"), values, where, null);

		}
		noti_cursor.close();
	}


	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = CM.getActiveNetworkInfo();
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.no_bn:
    		
    		if (info == null || !info.isAvailable()) {
    			Toast.makeText(logininput.this, "�ثe�S��������!�ҥH�L�k�i�J�q��", Toast.LENGTH_LONG).show();
    		}else{
    			new getcontent().execute();
    		}

			

			//new getcontent().execute();
			break;
		case R.id.in_bn:
			 bundle.putString("location","1" );
			// intent.setClass(writepage.this, browse.class);
			  intent.putExtras(bundle);
			// intent.setClass(writepage.this, browse.class);
			intent.setClass(logininput.this, MainFragment.class);
			// startActivityForResult(intent,0);
			startActivity(intent);
			break;
		case R.id.send_bn:
			 bundle.putString("location","2" );
			// intent.setClass(writepage.this, browse.class);
			  intent.putExtras(bundle);
			// intent.setClass(writepage.this, browse.class);
			fileContentProvider test = new fileContentProvider();
			test.del_table(Uri.parse("content://tab.list.d2d/file_choice"));
			intent.setClass(logininput.this, MainFragment.class);
			// startActivityForResult(intent,0);
			startActivity(intent);
			break;
		case R.id.ed_bn:
			// intent.setClass(writepage.this, browse.class);
			intent.setClass(logininput.this, edit.class);
			// startActivityForResult(intent,0);
			startActivity(intent);
			break;
		case R.id.log_bn:
			// �P�_�O�_��0�ت��b���ˬd�O�_�B�b�n�J���\�A�Y�n�J���\�N�n���Ϥ�
			if (cs == 0) {

				Login();

			} else {
				att_parameter.ffmpeg_now=false;
				att_parameter.selfid="";
				listThread.interrupt();
				aliveThread.interrupt();
				aliveThread=null;
				listThread=null;
				mDevfinder=null;
				cs = 0;
				retrieving = true;
				finishUpdateList = true;
				
				no_bn.setEnabled(false);
				in_bn.setEnabled(false);
				send_bn.setEnabled(false);
				ed_bn.setEnabled(false);
				//reg_bn.setBackgroundResource(R.drawable.reg_custom_btn);
				reg_bn.setVisibility(View.VISIBLE);
				log_bn.setBackgroundResource(R.drawable.log_custom_btn);
				if (info == null || !info.isAvailable()) {
					System.out.println("�ثe�S��������");
				}else{
					logout.logout_start();
				}
				
			}
			break;
		case R.id.reg_bn:
			if(cs==0){
				intent.setClass(logininput.this, register.class);
				// startActivityForResult(intent,0);
				startActivity(intent);
			}else{
				 bundle.putString("location","3" );
					// intent.setClass(writepage.this, browse.class);
					  intent.putExtras(bundle);
					// intent.setClass(writepage.this, browse.class);
					intent.setClass(logininput.this, MainFragment.class);
					// startActivityForResult(intent,0);
					startActivity(intent);		
			}

			break;
		}
		info=null;
		CM=null;
	}

	public void Login() {
		LayoutInflater factory = LayoutInflater.from(logininput.this);
		final View v1 = factory.inflate(R.layout.logininput, null);

		et = (EditText) v1.findViewById(R.id.usr);
		et2 = (EditText) v1.findViewById(R.id.pwd);
		ct = (CheckedTextView) v1.findViewById(R.id.remember);
		ct.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                ((CheckedTextView) v).toggle();
            }
        });
		Cursor info_cursor = getContentResolver().query(Uri.parse("content://tab.list.d2d/user_info"), new String[] { UserSchema._ACCOUNT,UserSchema._PASSWORD,UserSchema._REMEMBER }, null, null, null);
		if(info_cursor.getCount()>0){
			info_cursor.moveToFirst();
			String remember = info_cursor.getString(2);
			if(remember.equalsIgnoreCase("false")){
				ct.setChecked(false);
			}
			else{
				et.setText(info_cursor.getString(0));
				et2.setText(info_cursor.getString(1));
				ct.setChecked(true);
			}
		}
		info_cursor.close();
		AlertDialog.Builder dialog = new AlertDialog.Builder(logininput.this);
		dialog.setTitle("�Τ�n�J");
		dialog.setView(v1);
		dialog.setPositiveButton("�n�J", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				try {
					// ���o�����A�Ȫ�����
					ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
					NetworkInfo info = CM.getActiveNetworkInfo();

					State InternetState = info.getState();
					// System.out.println("InternetState = " + InternetState);

					getid = et.getText().toString();
					getpw = et2.getText().toString();
					getip = LoginActivity.Homeip;
					// �P�_��J���b�K�O�_���j��20
					if (getid.length() > 20 || getpw.length() > 20) {
						Toast.makeText(logininput.this, "ID or Password can not over 20 characters", Toast.LENGTH_LONG).show();

					}else if(getid.length() == 0 || getpw.length() == 0){
						Toast.makeText(logininput.this, "�b���αK�X���šA�Э��s��J", Toast.LENGTH_LONG).show();
					}
					// �P�_�O�_������
					else if (info == null || !info.isAvailable()) {
						Toast.makeText(logininput.this, "�L�i�κ���", Toast.LENGTH_LONG).show();
					} else {
						// ����n�J���ʧ@
						new ConnectHttp().execute();

					}
				} catch (Exception e) {
					Toast.makeText(logininput.this, "���~!���ˬd����", Toast.LENGTH_LONG).show();
				}
			}

			private void showToast() {
				// TODO Auto-generated method stub
				Toast.makeText(logininput.this, "ID or Password can not over 20 characters", Toast.LENGTH_LONG).show();

			}
		});
		dialog.setNegativeButton("����", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		});
		dialog.show();
	}
	private class reload extends AsyncTask<Void, Void, String[]> {
		AlertDialog.Builder Dialog = new AlertDialog.Builder(logininput.this);
		ProgressDialog progressDialog = null;

		@Override
		protected String[] doInBackground(Void... arg0) {

			aliveIp = locup.locationupdate(Login.latest_cookie,logininput.getIPAddress(getApplicationContext()),att_parameter.port);
			messageProcess MsgSave = new messageProcess();
			MsgSave.checkwlan(getContentResolver(), aliveIp[4]);
			MsgSave = null;
			// TODO Auto-generated method stub
			return null;
		}

		
	}
	private UPnPDeviceFinder mDevfinder  = null;
	private class ConnectHttp extends AsyncTask<Void, Void, String[]> {

		AlertDialog.Builder Dialog = new AlertDialog.Builder(logininput.this);

		protected void onPreExecute() {
			Dialog_for_login = ProgressDialog.show(logininput.this, "�еy��", "�n�J��", true);
			Dialog_for_login.show();

		}

		// �b�I�����榹method�A���槹�|����onPostExecute
		@Override
		public String[] doInBackground(Void... params) {
			
			String[] loginreturn = new String[3];
			// �Ngetid�Pgetpw�s�@���n�J��T�A�������s�u�ǤJserver���Ѽ�
			LoginrequestString = "username=" + getid + "&password=" + getpw;
			login_name = LoginrequestString;
			/*
			 * �ϥ�login����login method�A�^�ǭȬO�@�Ӱ}�C loginreturn[0]�O�Ψ��ˬd���L���\
			 * loginreturn[1]�Oserver�^�Ǫ��T��
			 */
			loginreturn = locup.login(getip, LoginrequestString);
			if(loginreturn[0] == "true"&&att_parameter.chechsuccess(loginreturn[1])){
				//�ϥ�ssdp�M��port
				mHandler.obtainMessage(change_content).sendToTarget();
		        if(mDevfinder == null){
		            mDevfinder = new UPnPDeviceFinder(true);
		        }
		        content="�ˬd���ҬO�_�i��UPNP";
		        mHandler.obtainMessage(change_content).sendToTarget();
				ArrayList<String> devList=mDevfinder.getUPnPDevicesList();
				//�^��IP
	            Map<String, String> map = new HashMap<String, String>();
	            for(int i =0;i<devList.size();i++){
	            	String ss= devList.get(i);
	            	String index ="";
	            	String []socket;
	            	if (ss.indexOf("Location: http://")!= -1){
	            		index = ss.substring(ss.indexOf("Location: http://")+17,ss.indexOf("Location: http://")+40);
	            		index =index.substring(0,index.indexOf("/"));
	            		socket=index.split(":");
	            		map.put(socket[0],socket[1]);
	            		
	            	}
	            	else if(ss.indexOf("Location:http://")!= -1){
	            		index = ss.substring(ss.indexOf("Location:http://")+16,ss.indexOf("Location:http://")+40);	
	            		index =index.substring(0,index.indexOf("/"));
	            		socket=index.split(":");
	            		map.put(socket[0],socket[1]);
	            	}

	            	//String index = ss.substring(ss.indexOf("Location: http://"),ss.indexOf(ss.indexOf('/'),ss.indexOf("Location: http://")));
	            	System.out.println(index);
	            }
	            String ipp=getIPAddress(getApplicationContext());

	            System.out.println(ipp);
	            content="���ݱ���IP�γs����";
	            mHandler.obtainMessage(change_content).sendToTarget();
	            //�����D,3G�L�k�ϥ�
	            try{
	            	att_parameter.port=Integer.valueOf(map.get(ipp.substring(ipp.indexOf("in_ip=")+6,ipp.indexOf("&"))))+1;
	                //att_parameter.port=5555;
	                	
	            }catch(Exception e){
	            	att_parameter.port=9527;
	            }
	        		aliveIp = locup.locationupdate(Login.latest_cookie, ipp , att_parameter.port);
					messageProcess MsgSave = new messageProcess();
					MsgSave.checkwlan(getContentResolver(), aliveIp[4]);
					updateContent(Uri.parse("content://tab.list.d2d/user_data"), "retrievable","content is null");
					MsgSave = null;
				//��l�e�ɶ�

		        user user = new user();
		    	 String res =user.setservicetime("H=0&M=0");
		    	 user=null;
			}
			
			return loginreturn;
		}

		// onPostExecute �|���� doInBackground ��return
		@Override
		protected void onPostExecute(String loginreturn[]) {
			Dialog_for_login.dismiss();
			mDevfinder = null;
			if (loginreturn[0] == "true") // �p�G��server���^��
			{	
				Boolean result =att_parameter.chechsuccess(loginreturn[1]);
				if (result) // �Y�b���K�X���T ��U�@��
				{	listThread = new Thread(runnable);
					aliveThread = new Thread(alive_run);
					listThread.start();
					aliveThread.start();

					finishUpdateList=false;
					cs = 1;
					// ���n�X���Ϥ�
					log_bn.setBackgroundResource(R.drawable.logout);
					// �]�w���s���i�ϥ�
					no_bn.setEnabled(true);
					in_bn.setEnabled(true);
					send_bn.setEnabled(true);
					ed_bn.setEnabled(true);
					// ���õ��U��
					//reg_bn.setBackgroundResource(R.drawable.dra_custom_btn);
					reg_bn.setVisibility(View.INVISIBLE);
					retrieving = false;
					Cursor change_remember = getContentResolver().query(Uri.parse("content://tab.list.d2d/user_info"), new String[] { UserSchema._ID }, null, null, null);
					if (change_remember.getCount() > 0) {
						change_remember.moveToFirst();
							int id_this = 0;
							id_this = Integer.valueOf(change_remember.getString(0));
							ContentValues values = new ContentValues();
							if(ct.isChecked()){
								values.put(UserSchema._REMEMBER, "true");
								values.put(UserSchema._ACCOUNT, et.getText().toString());
								values.put(UserSchema._PASSWORD, et2.getText().toString());
							}else{
								values.put(UserSchema._REMEMBER, "false");
								values.put(UserSchema._ACCOUNT, "");
								values.put(UserSchema._PASSWORD, "");
							}																	
							String where = UserSchema._ID + " = " + id_this;
							getContentResolver().update(Uri.parse("content://tab.list.d2d/user_info"), values, where, null);
						
					}
					change_remember.close();
//					Cursor check_ffmpeg = getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_ffmpeg"), null, null, null, null);
//					if(check_ffmpeg.getCount()>0){
//						tolen=check_ffmpeg.getCount();
//						
//					}
//					check_ffmpeg.close();
				}else{
					//server���^���A���Oret=1;
					Pattern pattern_user = Pattern.compile("User name or Password is not correct");
					Matcher error_user = pattern_user.matcher(loginreturn[1]);
					Pattern pattern_activity = Pattern.compile("No Permission");
					Matcher error_activity = pattern_activity.matcher(loginreturn[1]);
					if(error_user.find()){
						final AlertDialog.Builder Dialog = new AlertDialog.Builder(logininput.this); // Dialog
						Dialog.setTitle("��p!!");
						Dialog.setMessage("�z��J���b���αK�X���~�A�Э��s�T�{��A�i��n�J");
						Dialog.setIcon(android.R.drawable.ic_dialog_info);
						Dialog.setNegativeButton("�T�w", new DialogInterface.OnClickListener() { // ���Uabort
									// �Nthread����
									// ����progressbar
									// �]�w���s����U�ɵ����A�����o��Dialog�ä��_�o��thread
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// ConnectHttp.cancel(ConnectCancel);
										
										// dialog.cancel();
									}
								});
						Dialog.show();
					}else if(error_activity.find()){
						final AlertDialog.Builder Dialog = new AlertDialog.Builder(logininput.this); // Dialog
						Dialog.setTitle("��p!!");
						Dialog.setMessage("�z���b���|���i�����ҡA�Э��s����");
						Dialog.setIcon(android.R.drawable.ic_dialog_info);
						Dialog.setNegativeButton("�T�w", new DialogInterface.OnClickListener() { // ���Uabort
									// �Nthread����
									// ����progressbar
									// �]�w���s����U�ɵ����A�����o��Dialog�ä��_�o��thread
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// ConnectHttp.cancel(ConnectCancel);
										
										// dialog.cancel();
									}
								});
						Dialog.show();
					}
					
				}
			} else {
				
				if (loginreturn[2] == "false")
				// no response from http,ask user to wait or retry
				{

					AlertDialog.Builder Dialog = new AlertDialog.Builder(logininput.this); // Dialog
					Dialog.setTitle("ĵ�i");
					Dialog.setMessage("�������`,�Э��s�A��");
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
					Dialog.setNegativeButton("����", new DialogInterface.OnClickListener() { // ���Uabort
								// �Nthread����
								// ����progressbar
								@Override
								public void onClick(DialogInterface dialog, int which) {
								}
							});
					Dialog.show();
				}

				else {
					AlertDialog.Builder Dialog = new AlertDialog.Builder(logininput.this); // Dialog
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		att_parameter.ffmpeg_now=false;
		att_parameter.selfid="";
		finishUpdateList = true;
		unregisterReceiver(myBroadcastReceiver);
		
        LocalService<SwitchPower> switchPowerService = getSwitchPowerService();
        if (switchPowerService != null)
            switchPowerService.getManager().getImplementation().getPropertyChangeSupport()
                    .removePropertyChangeListener(this);

        getApplicationContext().unbindService(serviceConnection);

	}

	public void checkSMS() {

		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); // �Ndate
		String tempdate;
		// uri�O�s������DB���@�Ӥ覡�A�o�̭n�h��²�T��Ʈw��������
		Uri SMS_INBOX = Uri.parse("content://sms/inbox");
		Cursor new_sms_cursor = getContentResolver().query(SMS_INBOX, new String[] { "address", "date", "body" }, "address='0903410141' or address='+886903410141'", null, null);

		// �ˬd�O�_���s��sms
		if (new_sms_cursor.getCount() > 0) {
			new_sms_cursor.moveToFirst();
			for (int i = 0; i < new_sms_cursor.getCount(); i++) {
				// decode��@��H�ݱo���o����
				Date d = new Date(Long.parseLong(new_sms_cursor.getString(new_sms_cursor.getColumnIndex("date"))));
				tempdate = dateFormat.format(d);

				sms = new_sms_cursor.getString(new_sms_cursor.getColumnIndex("body"));
				messageProcess MsgSave = new messageProcess();
				MsgSave.insertdata(getContentResolver(), sms, tempdate);
				MsgSave = null;
				// �۩w�q���禡			
				new_sms_cursor.moveToNext();
			}// end for
		}// end if
		new_sms_cursor.close();
	}

	private class getcontent extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			// �}�Ҹ�ƶǰedialog
			pdialog = ProgressDialog.show(logininput.this, "�еy��", "���Ū����", true);
			pdialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			aliveIp = locup.locationupdate(Login.latest_cookie,logininput.getIPAddress(getApplicationContext()),att_parameter.port);
			
			if(aliveIp[0]!=null){
				
				String ip =aliveIp[0];
				messageProcess MsgSave = new messageProcess();
				MsgSave.checkwlan(getContentResolver(), aliveIp[4]);
				updateContent(Uri.parse("content://tab.list.d2d/user_data"), "retrievable","content is null");
				updateContent(Uri.parse("content://tab.list.d2d/user_reply"), "reply","filename is null");
				MsgSave=null;
				pdialog.dismiss();
				// �n�[�W���~���P�_
				Bundle bundle=new Bundle();
				Intent intent = new Intent();
				 bundle.putString("location","0" );
				// intent.setClass(writepage.this, browse.class);
				  intent.putExtras(bundle);
				 intent.setClass(logininput.this, MainFragment.class);
				// startActivityForResult(intent,0);
				startActivity(intent);
				
			}else{
				pdialog.dismiss();
				mHandler.obtainMessage(timeout).sendToTarget(); // �ǰe�n�D��slist���T����handler
			}

			return null;
		}
	}

	// ��s��Ʈw���A
	public void updateContent(Uri location, String mod,String condition) {
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
	
	// ��s��Ʈw���A
	private void updateState(String state, String[] Form, String conditional) {
		Cursor change_state = getContentResolver().query(Uri.parse("content://tab.list.d2d/user_data"), Form, conditional, null, null);
		if (change_state.getCount() > 0) {
			change_state.moveToFirst();
			for (int i = 0; i < change_state.getCount(); i++) {
				int id_this = 0;
				id_this = Integer.valueOf(change_state.getString(0));
				ContentValues values = new ContentValues();
				values.put(UserSchema._USESTATUS, state);
				String where = UserSchema._ID + " = " + id_this;
				getContentResolver().update(Uri.parse("content://tab.list.d2d/user_data"), values, where, null);
				change_state.moveToNext();
			}
		}
		change_state.close();
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {//������^��
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {   
            ConfirmExit();//����^��A�h����h�X�T�{
            return true;   
        }   
        return super.onKeyDown(keyCode, event);   
    }
    public void ConfirmExit(){//�h�X�T�{
    	AlertDialog.Builder DialogPreDl = new AlertDialog.Builder(this); // Dialog
		DialogPreDl.setTitle("");					
		DialogPreDl.setMessage("�T�w�n�h�X?");
		DialogPreDl.setIcon(android.R.drawable.ic_dialog_info);

		DialogPreDl.setPositiveButton("�O", new DialogInterface.OnClickListener() { // �������ɮ�
			@Override
			public void onClick(DialogInterface dialog, int which) {
				idle=true;
				ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo info = CM.getActiveNetworkInfo();
				if (info == null || !info.isAvailable()) {
					System.out.println("�ثe�S��������");
				}else{
					Logout logout = new Logout();
					logout.logout_start();
					logout=null;
				}

				logininput.this.finish();
					}//���ݤU�� end
				});			
	
	DialogPreDl.setNeutralButton("�_", new DialogInterface.OnClickListener() { // �w���ɮ�
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
	
DialogPreDl.show();
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
			//�������~�A�ϥΤ������覡�h�d
			return in_out_ip=att_parameter.getip();
			//return in_out_ip="in_ip=0.0.0.0&out_ip=0.0.0.0";
		}
    	in_out_ip ="in_ip="+in_ip+"&out_ip="+out_ip;
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
	                            if (!inetAddress.isLoopbackAddress()&& InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {
	                                   return inetAddress.getHostAddress().toString();
	                            }
	                     }
         }
	       } catch (Exception e) {
	        }
	       return "0.0.0.0";
	}
	public class MyBroadcastReceiver extends BroadcastReceiver {
		int starttime;
		int split_seq=0,index,duration;
		float total_time;
		String path,selfid;
		@Override
		public void onReceive(Context context, Intent intent) {
			

			String result = intent.getStringExtra(VideoTrimmingService.EXTRA_KEY_OUT);
			// �p�G�ثe���Ϊ�INDEX�p��ҭn���Ϊ��`size�A�h�~�����
			// �Ҧp�ɮ׬�50M,�C10M�@���A�`size��5��
			String[] medadata =result.split("&");
			path=medadata[0];
			File file = new File(path);
			String outFileName= medadata[1];		
			index=Integer.valueOf(medadata[2]);
			split_seq=Integer.valueOf(medadata[3]);
			duration=Integer.valueOf(medadata[5]);
			total_time=Float.valueOf(medadata[6]);
			selfid=medadata[7];
			Cursor file_exit = getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_file"), null, "selfid='"+selfid +"' and filerecord ='"+"file"+index+"_"+split_seq+"'", null, null);
			if(file_exit.getCount()>0){
				//do nothing
			}else{
				ContentValues values = new ContentValues();
				values = new ContentValues();
				values.put(UserSchema._FILEPATH, outFileName);
				values.put(UserSchema._FILERECORD, "file"+index+"_"+split_seq);
				values.put(UserSchema._FILECHECK, 0);
				values.put(UserSchema._SELFID, selfid);
				values.put(UserSchema._FILENAME, file.getName());
				//�٭n�[�ۭqID(��SERVER)
				getContentResolver().insert(Uri.parse("content://tab.list.d2d/temp_file"), values);
			
			}
			file_exit.close();
			split_seq++;
			if(split_seq==1){
				
				att_parameter.first=true;
			}else{
				
			}
			if (split_seq < (total_time / duration)) {
				ffmpeg_service aa= new ffmpeg_service();
				//String req=selfid+"&"+
				aa.ffmpeg(logininput.this,getContentResolver(),path, (split_seq * duration)-1, duration,selfid,total_time,split_seq);
				aa=null;
			}
			else {
				Log.i("COPYofWIRTE", "���Ȧ��o���(�����g���L)");
				//�R�����ά���(��ID��)
				split_seq = 0;
				String token,content_where,file_where,ffmpeg_where = null;
				String[] Form = { UserSchema._ID,UserSchema._MESSAGETOKEN ,};
				Cursor token_cursor = getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_content"), Form, "selfid='"+selfid+"'", null, null);
				if (token_cursor.getCount() > 0) {
					int id_this1 = 0;
					token_cursor.moveToFirst();
					id_this1 = Integer.valueOf(token_cursor.getString(0));
					token=token_cursor.getString(1);
					ContentValues values1 = new ContentValues();
					values1.put(UserSchema._FINISH, "yes");
					String where1 = UserSchema._ID + " = " + id_this1;
					getContentResolver().update(Uri.parse("content://tab.list.d2d/temp_content"), values1, where1, null);
					//�Ĥ@���W�ǧ�  ��sFIRST��TRUE

					Cursor uptoken_cursor = getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_file"), Form, "selfid='"+selfid+"'", null, null);
					if (uptoken_cursor.getCount() > 0) {
						ContentValues values = new ContentValues();
						values = new ContentValues();
						uptoken_cursor.moveToFirst();
						values.put(UserSchema._MESSAGETOKEN, token);
						for (int i = 0; i < uptoken_cursor.getCount(); i++) {
							int id_this = Integer.parseInt(uptoken_cursor.getString(0));
							file_where = UserSchema._ID + " = " + id_this;
							getContentResolver().update(Uri.parse("content://tab.list.d2d/temp_file"), values, file_where, null);
							uptoken_cursor.moveToNext();
						}
					}
					uptoken_cursor.close();
					
					Cursor ffmpeg_id = getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_ffmpeg"), new String[] { UserSchema._ID } , "selfid='"+selfid+"'", null, null);
					if (ffmpeg_id.getCount() > 0) {
						ffmpeg_id.moveToFirst();
						ffmpeg_where =ffmpeg_id.getString(0);						
					}
					ffmpeg_id.close();
					
					int id_this =Integer.parseInt(ffmpeg_where);
					String where = UserSchema._ID + " = " +  id_this;
					getContentResolver().delete(Uri.parse("content://tab.list.d2d/temp_ffmpeg"), where, null);
				}
				att_parameter.selfid="";
				att_parameter.ffmpeg_now=false;
				token_cursor.close();
				Toast.makeText(getApplicationContext(), "YA!�����F", Toast.LENGTH_LONG).show();
			}

		}
	}
}