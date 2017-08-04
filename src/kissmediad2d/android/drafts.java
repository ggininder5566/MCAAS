package kissmediad2d.android;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import login.submit1.Login;
import login.submit1.Logout;
import login.submit1.locupdate;
import login.submit1.retrieve;
import login.submit1.submit;
import tab.list.FileUtils;
import tab.list.att_parameter;
import tab.list.fileContentProvider;
import tab.list.fileContentProvider.UserSchema;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import ffmpeg.process.ffmpeg_service;

class draftsinfo // ��class�ΨӰO��list�������
{
	public String msg, receiver, tittle, filepath,selfid;
	int duration;
	float totaltime;
	draftsinfo() {
		
		msg = new String();
		receiver = new String();
		tittle = new String();
		filepath = new String();
		selfid = new String();
		duration =0;
	}

	public void setmsg(String arg) {
		msg = arg;
	}

	public void setreceiver(String arg) {
		receiver = arg;
	}

	public void settittle(String arg) {
		tittle = arg;
	}

	public void setfilepath(String arg) {
		filepath = arg;
	}
	public void setselfid(String arg) {
		selfid = arg;
	}
	public void setduration(int arg) {
		duration = arg;
	}
	public void settotaltime(float arg) {
		totaltime = arg;
	}
	public String getmsg() {
		return msg;
	}

	public String getreceiver() {
		return receiver;
	}

	public String gettittle() {
		return tittle;
	}

	public String getfilepath() {
		return filepath;
	}
	public String getselfid() {
		return selfid;
	}
	public int getduration() {
		return duration;
	}
	public float gettotaltime() {
		return totaltime;
	}
}

public class drafts extends Fragment implements OnItemClickListener{
	
	public String msg, receiver, tittle, filepath,selfid;
	int duration;
	String attachment = null, content, state;
	String filetype[];
	TextView tvName,tvR, tvT, tvC;
	Button delete;
	ToggleButton sms;
	ImageView previewImg;
	float TWOMB = 5 * 1024 * 1024, ONEMB;
	int file_amount, split_seq = 0;
	String nack = "", ffmpeg_path = new String();
	public String submit_file_readline;
	String str1;
	public Boolean finish =false;
	public BufferedInputStream inputmasge ;
	public DataOutputStream dataOut ;
	private final int closedialog = 0;
	private final int timeout = 1;
	private final int ok = 2;
	private final int error = 3;
	private final int flash = 4;
	int index, urgent = 0;
	
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy�~MM��dd��HH:mm:ss");

	retrieve retreive = new retrieve();

	int ftimes;
	 String file_name = new String(), postFile;
	ArrayList<String> file_path;
	 Float total_time;
	 int file_size, Algocount;
	ProgressDialog senddialog = null;
	 VideoTrimmer trimmer;
	 String alive_cookie;
	submit Submit;
	ProgressDialog dialog = null;
	boolean finishsubmit = false, first = true;
	FileUtils fileutils = new FileUtils();
	FileUtils oname = new FileUtils();
	String outFileName;
	File file;
	locupdate locup =new locupdate();
	public String token ,filename;

	/*
	 * �o��O��ܦ�����"�ϥΪ�"�H²�T���A�A�P�ɷ|��̫ܳ�@���U�������
	 */
	List<draftsinfo> getdraftsinfo;
	View view;
	ListView Audiolist;
	String[] listid;
	boolean finishRetreive = false, newfile = false;
	//int filesize ;
	String temp, getTitle, getContent, getSender = null, getFilename;
	
	SimpleAdapter adapter;
	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

	private static final String ARG_SECTION_NUMBER = "section_number";
	public static drafts newInstance(int sectionNumber) {
		drafts fragment = new drafts();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}
	private Handler mHandler = new Handler() {
		
		// ����k�bui�u�{�B��
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case closedialog:
				senddialog = ProgressDialog.show(getActivity(), "�еy��", "��ƤW�Ǥ�", true);
				senddialog.show();
				break;
			case timeout:
				AlertDialog.Builder Dialog0 = new AlertDialog.Builder(getActivity()); // Dialog
				Dialog0.setTitle("�s�u�O��");
				Dialog0.setMessage("�аݬO�_�n���e?");
				Dialog0.setIcon(android.R.drawable.ic_dialog_info);
				Dialog0.setNeutralButton("�T�w", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						new SendandAttach().execute();
						
					}
				});
				Dialog0.setNegativeButton("����", new DialogInterface.OnClickListener() { // ���Uabort
					// �Nthread����
					// ����progressbar
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
				Dialog0.show();
				break;
			case error:
				AlertDialog.Builder Dialog = new AlertDialog.Builder(getActivity()); // Dialog
				Dialog.setTitle("");
				Dialog.setMessage("�W�ǥ��ѡA�d�L�������");
				Dialog.setIcon(android.R.drawable.ic_dialog_info);
				Dialog.setNeutralButton("�T�w", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						onResume();
					}
				});
				Dialog.show();
				break;
			case ok:
				// �ǰe���\����ܦ��\����
				AlertDialog.Builder Dialog1 = new AlertDialog.Builder(getActivity()); // Dialog
				Dialog1.setTitle("");
				Dialog1.setMessage("�ǰe���\");
				Dialog1.setIcon(android.R.drawable.ic_dialog_info);
				Dialog1.setNeutralButton("�T�w", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						onResume();
					}
				});
				Dialog1.show();
				break;
			case flash:
				onResume();
				break;
			}
		}
	};
	public drafts() {
	}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.blist, container,
				false);
		adapter = new SimpleAdapter(getActivity(), getData(), R.layout.drafts, new String[] { "receiver", "tittle", "msg" }, new int[] { R.id.temp_receiver, R.id.temp_tittle, R.id.temp_content});
		Audiolist = (ListView) rootView.findViewById(R.id.PhoneVideoList);
		Audiolist.setTextFilterEnabled(true);
		Audiolist.setBackgroundResource(R.drawable.background);
		Audiolist.setCacheColorHint(Color.TRANSPARENT);

		Audiolist.setAdapter(adapter);
		Audiolist.setOnItemClickListener(this);
		
		return rootView;
	}

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
    	menu.clear();
    	inflater.inflate(R.menu.reload, menu);
        }
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent();
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
        switch (id){
        case R.id.action_settings:
			// intent.setClass(writepage.this, browse.class);
			intent.setClass(getActivity(), edit.class);
			// startActivityForResult(intent,0);
			startActivity(intent);
			break;
        case R.id.logout:
        	fileContentProvider test = new fileContentProvider();
			test.del_table(Uri.parse("content://tab.list.d2d/user_info"));

			Logout logout = new Logout();
			logout.logout_start();
			
			// intent.setClass(writepage.this, browse.class);
			intent.setClass(getActivity(), logininput.class);
			// startActivityForResult(intent,0);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); 
			startActivity(intent);
			getActivity().finish();
			break;
        case android.R.id.home:

        	getActivity().finish();
        	break;
        case R.id.reload:
        	mHandler.obtainMessage(flash).sendToTarget();
        	break;
        
        }
        
		return true;
	}
	@Override
	public void onResume() {// ���B��,�C�����ح�����,�ߧY������s
		super.onResume();
		list.clear();
		list = getData();
		adapter.notifyDataSetChanged();
	}

	private List<Map<String, Object>> getData() {
		int count = 0;
		draftsinfo tempcontactinfo;

		getdraftsinfo = new ArrayList<draftsinfo>();
		String[] Form1 = { UserSchema._RECEIVER,UserSchema._TITTLE,UserSchema._CONTENT,UserSchema._FILEPATH, UserSchema._SELFID ,UserSchema._DURATION,UserSchema._TOTAL_TIME};
		//�ˬd�O�_���|���������T��
		Cursor temp_content_cursor = getActivity().getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_content"), Form1, "messagetoken is null or first is null", null, null);
		if (temp_content_cursor.getCount() > 0) {
			temp_content_cursor.moveToFirst();
			for (int sender = 0; sender < temp_content_cursor.getCount(); sender++) {
					tempcontactinfo = new draftsinfo();
					tempcontactinfo.setreceiver(temp_content_cursor.getString(0));
					tempcontactinfo.settittle(temp_content_cursor.getString(1));
					tempcontactinfo.setmsg(temp_content_cursor.getString(2));
					tempcontactinfo.setfilepath(temp_content_cursor.getString(3));
					tempcontactinfo.setselfid(temp_content_cursor.getString(4));
					tempcontactinfo.setduration(temp_content_cursor.getInt(5));
					tempcontactinfo.settotaltime(temp_content_cursor.getFloat(6));
					getdraftsinfo.add(tempcontactinfo);
					count++;
					tempcontactinfo = new draftsinfo();
					temp_content_cursor.moveToNext();
				}// forloop end
			}// if end
		temp_content_cursor.close();
		temp_content_cursor = null;

		Map<String, Object> map = new HashMap<String, Object>();
		if (getdraftsinfo.size() != 0) {
			for (int i = 0; i <= count - 1; i++) {
				map = new HashMap<String, Object>();
				map.put("receiver", getdraftsinfo.get(i).getreceiver());
				map.put("tittle", getdraftsinfo.get(i).gettittle());
				map.put("msg", getdraftsinfo.get(i).getmsg());
				list.add(map);
			}// for end
		}//if end
		return list;

	}//getdata end

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long listid) {
		final int id = (int) listid;
		LayoutInflater factory = LayoutInflater.from(getActivity());
		final View v1 = factory.inflate(R.layout.drafts_dailog, null);

		tvR = (TextView) v1.findViewById(R.id.ag_receiver);
		tvT = (TextView) v1.findViewById(R.id.ag_subject);
		tvC = (TextView) v1.findViewById(R.id.ag_msg);
		tvName = (TextView) v1.findViewById(R.id.ag_filename);
		previewImg=(ImageView) v1.findViewById(R.id.ag_previewimg);
		
		tvR.setText(getdraftsinfo.get(id).getreceiver());
		tvT.setText(getdraftsinfo.get(id).gettittle());
		tvC.setText(getdraftsinfo.get(id).getmsg());
		
		attachment=getdraftsinfo.get(id).getfilepath();
		receiver=getdraftsinfo.get(id).getreceiver();
		tittle=getdraftsinfo.get(id).gettittle();
		content=getdraftsinfo.get(id).getmsg();
		duration=getdraftsinfo.get(id).getduration();
		total_time=getdraftsinfo.get(id).gettotaltime();
		selfid=getdraftsinfo.get(id).getselfid();
		file_path = new ArrayList<String>();
		File file = new File(attachment);
		tvName.setText(file.getName());
		previewImg.setClickable(true);
		previewImg.setOnClickListener(preview);
		setPreviewImg();
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		dialog.setTitle("�H�U�T���|���o�e");
		dialog.setView(v1);
		dialog.setPositiveButton("�ǰe", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				selfid=getdraftsinfo.get(id).getselfid();
				new republish().execute();
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

	
	
	View.OnClickListener preview =new View.OnClickListener() {
        public void onClick(View v) {
        	viewfile();
	         }
	     };
	public void fileupload(File filepath) {
		// �ˬd�ϥΪ̦��S����ܭn�W�Ǫ��ɮ�,��ܦn���ɮ׷|�g�Jfile_choice��table��
		
		dialog = ProgressDialog.show(getActivity(), "�еy��", "��ƳB�z��", true);
		dialog.show();
		file_path.add(attachment);
		file_size=(int)filepath.length();
		file_name=filepath.getName();
		postFile = new String();
		boolean[] checktype = new boolean[att_parameter.filetype];
		checktype = att_parameter.checktype(attachment);
			// �p�G��ܪ��O�v���A�h�p��L���v�����סB�j�p�A�ت��O�n�ǵ�ffmpeg�ϥ�
		if (checktype[att_parameter.video]) {
		
			// �H���׬���ǡA���]20MB���@���A�D�X20MB�ݴX��,Math.round���|�ˤ��J����
			// �H��Ƭ���ǡA���]20MB��9.8��A�D�X�ɮר̷Ӭ�Ƥ�,�ݭn���X��,Math.ceil���D�X�j�󦹼ƪ��̤p���,ex.�j��12.8���̤p��Ƭ�13
			file_amount = (int) Math.ceil((double) total_time / duration);
			String[] tempfilename = new String[file_amount];
			for (int i = 0; i < file_amount; i++) {
				tempfilename[i] = "file_name" + index + "_" + String.valueOf(i) + "=" + file_name;
				postFile = postFile + "&" + tempfilename[i];
			}
			ffmpeg_service aa= new ffmpeg_service();
			//String req=selfid+"&"+
			att_parameter.ffmpeg_now=true;
			aa.ffmpeg(getActivity(),getActivity().getContentResolver(),file_path.get(index), 0, duration,selfid,total_time,0);
			aa=null;
			
			Thread publish =new Thread(new Runnable() {
				@Override
				public void run() {
					while (!att_parameter.first) {
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
								Thread.currentThread().interrupt();
								Log.i("update list thread", "Thread.currentThread().isInterrupted()");
							}	
					}
					att_parameter.first=false;
					new SendandAttach().execute();
				}
			});
			publish.start();
			//ffmpeg(file_path.get(index), 0, duration); // ����
			// �ѼơG�ɦW�B�_�l��m�B���ήɶ��B��X�ɮ�
		} else if (checktype[att_parameter.music] || checktype[att_parameter.photo]) {
			// ���v���~�A��L�����Τ�
			File file = new File(file_path.get(index));
			// �}�ҭp���ɮת��רæs�JattachSize
			file_size = (int) file.length();
			file_name = file.getName();
			postFile = postFile + "&file_name" + index + "_0=" + file_name;
			duration = 0;
			file_amount=1;
			FileUtils oname = new FileUtils();
			String outFileName = oname.getTargetFileName(file_path.get(index), split_seq, index);
			ContentValues values = new ContentValues();
			values.put(UserSchema._FILEPATH, outFileName);
			values.put(UserSchema._FILERECORD, index + "_0");
			values.put(UserSchema._FILECHECK, 0);
			values.put(UserSchema._FILENAME, file_name);
			values.put(UserSchema._SELFID, selfid);
			getActivity().getContentResolver().insert(Uri.parse("content://tab.list.d2d/temp_file"), values);
			//dialog.dismiss();
			state = "write";
			new SendandAttach().execute();
		}
			state = "write";
			index = 0;
			// �I�s�۩w�q���禡

	}
	class republish extends AsyncTask<Void, Void, String>{
		ProgressDialog republishdialog=null;
		
		protected void onPreExecute() {
			republishdialog = ProgressDialog.show(getActivity(), "�еy��", "���Ū����", true);


		}
		@Override
		protected String doInBackground(Void... params) {
			
			String respone,token;
			
			submit submit = new submit();
			submit.setrequestString("selfid="+selfid);
			respone=submit.resubmit(Login.latest_cookie);			
		
			return respone;
			
		}

		protected void onPostExecute(String respone) {
			republishdialog.dismiss();
			
			if(att_parameter.chechsuccess(respone)){
				String[] msg=respone.split("&");
				token=msg[1].replace("token=", "");
				
				if(msg[2].equals("true")){
					//�T����Ĥ@�������W�h�A�u�O�S����respone�A��s
					//��stoken��first
					String[] Form = { UserSchema._ID };
					Cursor change_first = getActivity().getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_content"), Form, "selfid='" + selfid + "'", null, null);
					if (change_first.getCount() > 0) {
						change_first.moveToFirst();
						int id_this = 0;
						id_this = Integer.valueOf(change_first.getString(0));
						ContentValues values = new ContentValues();
						values.put(UserSchema._FIRST, "true");
						values.put(UserSchema._MESSAGETOKEN, token);
						String where = UserSchema._ID + " = " + id_this;
						getActivity().getContentResolver().update(Uri.parse("content://tab.list.d2d/temp_content"), values, where, null);
					    
					}
					change_first.close();
					Cursor change_file = getActivity().getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_file"), Form, "selfid='" + selfid + "'", null, null);
					if (change_file.getCount() > 0) {
						change_file.moveToFirst();
						int id_this = 0;
						for(int count=0;count<change_file.getCount();count++){
							id_this = Integer.valueOf(change_file.getString(0));
							ContentValues values = new ContentValues();
							values.put(UserSchema._MESSAGETOKEN, token);
							String where = UserSchema._ID + " = " + id_this;
							getActivity().getContentResolver().update(Uri.parse("content://tab.list.d2d/temp_file"), values, where, null);
							change_file.moveToNext();
						}
						
					    
					}
					change_file.close();
					mHandler.obtainMessage(flash).sendToTarget();
					//�o�̭n�s�Wdalog
				}else if(msg[2].equals("false")){
					//token���W�h���Ofile�S��
					String[] Form = { UserSchema._ID };
					Cursor change_first = getActivity().getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_content"), Form, "selfid='" + selfid + "'", null, null);
					if (change_first.getCount() > 0) {
						change_first.moveToFirst();
						int id_this = 0;
						id_this = Integer.valueOf(change_first.getString(0));
						ContentValues values = new ContentValues();
						values.put(UserSchema._MESSAGETOKEN, token);
						String where = UserSchema._ID + " = " + id_this;
						getActivity().getContentResolver().update(Uri.parse("content://tab.list.d2d/temp_content"), values, where, null);
					    
					}
					change_first.close();
					Cursor change_file = getActivity().getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_file"), Form, "selfid='" + selfid + "'", null, null);
					if (change_file.getCount() > 0) {
						change_file.moveToFirst();
						int id_this = 0;
						for(int count=0;count<change_file.getCount();count++){
							id_this = Integer.valueOf(change_file.getString(0));
							ContentValues values = new ContentValues();
							values.put(UserSchema._MESSAGETOKEN, token);
							String where = UserSchema._ID + " = " + id_this;
							getActivity().getContentResolver().update(Uri.parse("content://tab.list.d2d/temp_file"), values, where, null);
							change_file.moveToNext();
						}
						
					    
					}
					change_file.close();
					

					
					new upfile().execute();
					//��stoekn��W�ǲĤ@��
				}
				//retrieve.token = token.replaceFirst("ret=0&", "");
			}else{
				//�����S�e�X
				String[] msg=respone.split("&");
				msg[1]=msg[1].replace("msg=", "");
				if(msg[1].equalsIgnoreCase("token is null")){
					File file = new File(attachment);
					if(file.exists()){
						fileupload(file);
					}
					else{
						//�ɮפ����F �n�������
					}
					
				}else{
					//��L���~
				}

		}
		}
	}
	private class upfile extends AsyncTask<Void, Void, String> {
		public String path;
		String sqliteString,submit_file_readline;
		//private ArrayList<String> file_path = new ArrayList<String>();
		//public String attachment = null;
		Boolean finishsubmit =false,first=true;
		int Algocount=0;
		
		protected void onPreExecute() {

		}
		@Override
		protected String doInBackground(Void... params) {
		    String response = new String();
		    // ���o�̪�IP
			file_path.add(attachment);
			if (!(file_path.isEmpty())) {
			    // �p�G���[�ɮ׳�����(�]�N�O��file path),�h�i���ɮת��z��
				File file = new File(attachment);
				finishsubmit = false;
				sqliteString = file.getName().replace("'", "''");

				while (!finishsubmit) {
//				    // �ˬd�bDB��table���A�O�_�����W�Ǫ��ɮסA���Ǫ��ɮ�filecheck�O�s�A�W�Ǧn���O�@�C

					Cursor check_finish_cursor = getActivity().getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_file"), new String[] { UserSchema._FILEPATH }, "filecheck='0' and filerecord='file0_0' and messagetoken='" +token + "'", null, null);
				    if (check_finish_cursor.getCount() > 0) {
					check_finish_cursor.moveToFirst();
					CharSequence[] path = new CharSequence[check_finish_cursor.getCount()];
					// �N���ǧ����ɮסA�ϥΰ}�C�x�s�_��
					for (int i = 0; i < check_finish_cursor.getCount(); i++) {
					    path[i] = check_finish_cursor.getString(0);
					    check_finish_cursor.moveToNext();
					}
					// �i����ɦW�z��
					boolean[] checktype = new boolean[att_parameter.filetype];
					checktype = att_parameter.checktype(file.getName());
					// �p�G�O���֡B�Ϥ�
					if (checktype[att_parameter.music] | checktype[att_parameter.photo]) {
					    
						Algocount = 1;
						submit mpsubmit= new submit();
						// ���]�w�ɮ׸��|��submit
						mpsubmit.setattachfile(attachment);
						// �ǤJ�n���檺���ƥH�έn���檺spilt path[]
						mpsubmit.setsource(Algocount, path);

						// �W���ɮ�
						mpsubmit.submit_file(token, 0);
						// Ū�^response
						submit_file_readline = mpsubmit.getsubmit_file_readline();
						mpsubmit=null;
					} else if (checktype[att_parameter.video]) {
					    // Algocount�O��ܥثe�n�W�ǴX���Apath���N�|�ھ�Algocount�ӨM�w�nŪ�X�X�����
					    // ���Ĥ@�����ت��b��A���եثe���������A����
						submit vsubmit=new submit();
					    if (first == true) {
							first = false;
							Algocount = 1;
							/*
							 * �U���T�����O�۩w�q���禡�A���O�]�w�����Ϊ��ɮ׸��|�A�ت��O�Ψӿz���
							 * �A�ӬO�]�w�n�W�Ǫ��ɮ׸��|�έn�W�Ǫ��ɮ׭Ӽ�
							 * �A��method�|�s�@server�ݭn����T �̫�~�O�u���n����W���ɮ�
							 */
		
							// ���]�w�ɮ׸��|��submit
							
							vsubmit.setattachfile(attachment);
							// �ǤJ�n���檺���ƥH�έn���檺spilt path[]
							vsubmit.setsource(Algocount, path);
							// �W���ɮ�
							vsubmit.submit_file(token, 0);
							// ���^response,�Ψӫݷ|��nack���P�_
							submit_file_readline = vsubmit.getsubmit_file_readline();
							check_finish_cursor.close();
					    } else {
							// �P�Ĥ@�����k�A���P�b��A���榸�Ʊ��H��
							Algocount = (int) (Math.random() * 2 + 1);
							// Algocount=1;
							// �p�G�����檺���Ƥj��ѤU�n�W�Ǫ��ɮסA�h���榸�ƥH�ѤU���ɮ׬��D
							if (Algocount >= check_finish_cursor.getCount()) {
							    Algocount = check_finish_cursor.getCount();
							}
		
							vsubmit.setattachfile(attachment);
							vsubmit.setsource(Algocount, path);
							vsubmit.submit_file(token, 0);
							submit_file_readline = vsubmit.getsubmit_file_readline();
							check_finish_cursor.close();
					    }
					    vsubmit=null;
					}

					String[] Form = { UserSchema._ID };
					// ���N��~�W�Ǫ��ɮסA��filecheck set��1
					for (int a = 0; a < Algocount; a++) {
					    Cursor up_tempfile = getActivity().getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_file"), Form, "messagetoken='" + token + "' and filecheck='0'", null, null);
					    if (up_tempfile.getCount() > 0) {
						up_tempfile.moveToFirst();
						ContentValues values = new ContentValues();
						values.put(UserSchema._FILECHECK, 1);
						int id_this = Integer.parseInt(up_tempfile.getString(0));
						String where = UserSchema._ID + " = " + id_this;
						getActivity().getContentResolver().update(Uri.parse("content://tab.list.d2d/temp_file"), values, where, null);
					    }
					    up_tempfile.close();
					}
					/*
					 * ���^readline,�ˬdnack,nack�O�i�����@���ɮפW�ǥ��ѡA
					 * �ھ�nack��filecheck set��0�A�b�U�@�����s�W��
					 */
					Pattern patternnack = Pattern.compile("nack=.*");
					Matcher matchernack = patternnack.matcher(submit_file_readline);
					if (matchernack.find()) {
					    String qq = submit_file_readline.substring(submit_file_readline.indexOf("nack="), submit_file_readline.indexOf("&&b"));
					    String[] nack_name = qq.replace("nack=", "").split("&");
					    for (int i = 0; i < nack_name.length; i++) {
						Cursor up_nack_cursor = getActivity().getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_file"), Form, "filerecord='" + nack_name[i] + "'and filename='" + sqliteString + "'", null, null);
						if (up_nack_cursor.getCount() > 0) {
						    up_nack_cursor.moveToFirst();
						    ContentValues values = new ContentValues();
						    values.put(UserSchema._FILECHECK, 0);
						    int id_this = Integer.parseInt(up_nack_cursor.getString(0));
						    String where = UserSchema._ID + " = " + id_this;
						    getActivity().getContentResolver().update(Uri.parse("content://tab.list.d2d/temp_file"), values, where, null);
						}
						up_nack_cursor.close();
					    }
					}
				    } else {
					// �Yfilecheck=0�����S���A�N���ɮפw�g�ǧ�
					first = true;
					finishsubmit = true;
					
				    }
				    check_finish_cursor.close();
				}// while end
			} // check_filepath!=null end
			file_path.clear();
			response = "true";
			
		    
		    return response;
		}

		protected void onPostExecute(String response) {
			mHandler.obtainMessage(flash).sendToTarget();
//		    if (response.equals("true")) {
//			Cursor reply_cursor = getActivity().getContentResolver().query(Uri.parse("content://tab.list.d2d/user_reply"), new String[] { UserSchema._ID }, "messagetoken='" + token + "'", null, null);
//			if (reply_cursor.getCount() > 0) {
//			    reply_cursor.moveToFirst();
//			    ContentValues values = new ContentValues();
//			    values.put(UserSchema._FILEPATH, path);
//			    int id_this = Integer.parseInt(reply_cursor.getString(0));
//			    String where = UserSchema._ID + " = " + id_this;
//			    getActivity().getContentResolver().update(Uri.parse("content://tab.list.d2d/user_reply"), values, where, null);
//			}
//			reply_cursor.close();
//
//		    }
		}
	    }
	 class SendandAttach extends AsyncTask<Void, Void, String> // implement
	// thread
	{
		public int id_this;
		public String where;
		String file0_0;
		File firstfile = null;
		public String path;

		// Asyntask �e�m�@�~
		@Override
		protected void onPreExecute() {
			dialog.dismiss();
			mHandler.obtainMessage(closedialog).sendToTarget(); // �ǰe�n�D��slist���T����handler
			// �}�Ҹ�ƶǰedialog

		}

		@Override
		protected String doInBackground(Void... params) {
			String er = "no&";
			String sqliteString;
			state="write";
			
			if (state.equals("write")) {
				/// ex : storage/emulated/0/DCIM/100ANDRO/MOV_0259.mp4
				File file = new File(attachment);
				String filename = file.getName();
				filetype = filename.split("\\.");
				System.out.println("postFile==" + postFile);
				Cursor check_finish_cursor = getActivity().getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_file"), new String[] { UserSchema._FILEPATH }, "filecheck='0' and filerecord='file0_0' and selfid='" + selfid + "'", null, null);
				if(check_finish_cursor.getCount()>0){
					check_finish_cursor.moveToFirst();
					file0_0=check_finish_cursor.getString(0);
					firstfile =new File(file0_0);
				}
				check_finish_cursor.close();
				Submit=new submit();
				// ���]�wrequest�r��
				Submit.setrequestString("subject=" + tittle + "&content=" + content + 
						"&selfid="+selfid+"&receiver=" + receiver + 
						"&filecnt=" + file_amount + "&duration=" + duration + 
						"&filename=" + filename + "&filetype=" + filetype[1] + 
						"&filepath=" + attachment + "&length=" + file_size + 
						"&firstlength=" + (int)firstfile.length()+"&urgent=" + urgent  + postFile);
				String resp = Submit.submit1(login.submit1.Login.latest_cookie,firstfile);				
				// �o��O�Ψ��ˬd����̬O�_�s�b��server���A�Y���s�b�h�����o�����W��
				if (!(att_parameter.chechsuccess(resp))) {
					if(resp.equalsIgnoreCase("timeout")){
						er = "yes&timeout";
					}else{
						er = "yes&unknow";
						String[] Form = { UserSchema._ID };
						Cursor del_content = getActivity().getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_content"), Form, "selfid='" + selfid + "'", null, null);
						if (del_content.getCount() > 0) {
							del_content.moveToFirst();
							int id_this = 0;
							id_this = Integer.valueOf(del_content.getString(0));
							String where = UserSchema._ID + " = " + id_this;
							getActivity().getContentResolver().delete(Uri.parse("content://tab.list.d2d/temp_content"), where, null);
						    
						}
						del_content.close();
					
					}
					
				} else {
					
					//UPDATE TOKEN�ܯ�Z��
					String[] Form = { UserSchema._ID };
					Cursor change_token = getActivity().getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_content"), Form, "selfid='" + selfid + "'", null, null);
					if (change_token.getCount() > 0) {
						change_token.moveToFirst();
						int id_this = 0;
						id_this = Integer.valueOf(change_token.getString(0));
						ContentValues values = new ContentValues();
						values.put(UserSchema._MESSAGETOKEN, resp.replace("ret=0&token=", ""));
						String where = UserSchema._ID + " = " + id_this;
						getActivity().getContentResolver().update(Uri.parse("content://tab.list.d2d/temp_content"), values, where, null);
					    
					}
					change_token.close();

					//�Ĥ@���W�ǧ�  ��sFIRST��TRUE
					Cursor change_first = getActivity().getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_content"), Form, "selfid='" + selfid + "'", null, null);
					if (change_first.getCount() > 0) {
						change_first.moveToFirst();
						int id_this = 0;
						id_this = Integer.valueOf(change_first.getString(0));
						ContentValues values = new ContentValues();
						values.put(UserSchema._FIRST, "true");
						String where = UserSchema._ID + " = " + id_this;
						getActivity().getContentResolver().update(Uri.parse("content://tab.list.d2d/temp_content"), values, where, null);
					    
					}
					change_first.close();
					
					Cursor up_tempfile = getActivity().getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_file"), Form, "selfid='" + selfid + "' and filecheck='0'", null, null);
					if (up_tempfile.getCount() > 0) {
						up_tempfile.moveToFirst();
						ContentValues values = new ContentValues();
						values.put(UserSchema._FILECHECK, 1);
						int id_this = Integer.parseInt(up_tempfile.getString(0));
						String where = UserSchema._ID + " = " + id_this;
						getActivity().getContentResolver().update(Uri.parse("content://tab.list.d2d/temp_file"), values, where, null);
					}
					up_tempfile.close();
			}
			}
			return er;
		}

		protected void onPostExecute(String er) {
			String []resp = er.split("&");
			senddialog.dismiss();
			if (resp[0].equals("yes")) {
				if(resp[1]!=null &&resp[1].equalsIgnoreCase("timeout")){
					mHandler.obtainMessage(timeout).sendToTarget(); // �ǰe�n�D��slist���T����handler
				}
				else{
					mHandler.obtainMessage(error).sendToTarget();
				}
			} else {
				 // �ǰe�n�D��slist���T����handler
				fileContentProvider test = new fileContentProvider();
				test.del_table(Uri.parse("content://tab.list.d2d/file_choice"));
				mHandler.obtainMessage(ok).sendToTarget();

			}

		}
	}
		public void viewfile() {
			boolean[] checktype = new boolean[att_parameter.filetype];
			checktype = att_parameter.checktype(attachment);
			if (checktype[att_parameter.music]) {
				Intent it = new Intent(Intent.ACTION_VIEW);
				File file = new File(attachment);
				it.setDataAndType(Uri.fromFile(file), "audio/*");
				startActivity(it);
			} else if (checktype[att_parameter.video]) {
				Intent it = new Intent(Intent.ACTION_VIEW);
				File file = new File(attachment);
				it.setDataAndType(Uri.fromFile(file), "video/*");
				startActivity(it);
			} else if (checktype[att_parameter.photo]) {
				Intent it = new Intent(Intent.ACTION_VIEW);
				File file = new File(attachment);
				it.setDataAndType(Uri.fromFile(file), "image/*");
				startActivity(it);
			} else
				Toast.makeText(getActivity(), "not match file", 4000).show();
		}
		public void setPreviewImg() {
			boolean[] checktype = new boolean[att_parameter.filetype];
			checktype = att_parameter.checktype(attachment);

			if (checktype[att_parameter.music]) {
				previewImg.setImageResource(R.drawable.notes);
			} else if (checktype[att_parameter.video]) {
				Bitmap filebitmap = android.media.ThumbnailUtils.createVideoThumbnail(attachment, Images.Thumbnails.MICRO_KIND);
				// filebitmap=ThumbnailUtils.extractThumbnail(filebitmap,55,60);
				previewImg.setImageBitmap(filebitmap);
			} else if (checktype[att_parameter.photo]) {
				Bitmap filebitmap = BitmapFactory.decodeFile(attachment);
				previewImg.setImageBitmap(filebitmap);
			} else {
				previewImg.setImageResource(R.drawable.questionmark);
			}

		}
}
