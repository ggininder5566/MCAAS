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

class draftsinfo // 本class用來記錄list中的資料
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
	
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");

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
	 * 這邊是顯示有哪些"使用者"寄簡訊給你，同時會顯示最後一次下載的資料
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
		
		// 此方法在ui線程運行
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case closedialog:
				senddialog = ProgressDialog.show(getActivity(), "請稍候", "資料上傳中", true);
				senddialog.show();
				break;
			case timeout:
				AlertDialog.Builder Dialog0 = new AlertDialog.Builder(getActivity()); // Dialog
				Dialog0.setTitle("連線逾時");
				Dialog0.setMessage("請問是否要重送?");
				Dialog0.setIcon(android.R.drawable.ic_dialog_info);
				Dialog0.setNeutralButton("確定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						new SendandAttach().execute();
						
					}
				});
				Dialog0.setNegativeButton("取消", new DialogInterface.OnClickListener() { // 按下abort
					// 將thread結束
					// 隱藏progressbar
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
				Dialog0.show();
				break;
			case error:
				AlertDialog.Builder Dialog = new AlertDialog.Builder(getActivity()); // Dialog
				Dialog.setTitle("");
				Dialog.setMessage("上傳失敗，查無此收件者");
				Dialog.setIcon(android.R.drawable.ic_dialog_info);
				Dialog.setNeutralButton("確定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						onResume();
					}
				});
				Dialog.show();
				break;
			case ok:
				// 傳送成功後顯示成功視窗
				AlertDialog.Builder Dialog1 = new AlertDialog.Builder(getActivity()); // Dialog
				Dialog1.setTitle("");
				Dialog1.setMessage("傳送成功");
				Dialog1.setIcon(android.R.drawable.ic_dialog_info);
				Dialog1.setNeutralButton("確定", new DialogInterface.OnClickListener() {
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
	public void onResume() {// 此處為,每次切煥頁面時,立即做的更新
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
		//檢查是否有尚未完成的訊息
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
		dialog.setTitle("以下訊息尚未發送");
		dialog.setView(v1);
		dialog.setPositiveButton("傳送", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				selfid=getdraftsinfo.get(id).getselfid();
				new republish().execute();
			}

		});
		dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {

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
		// 檢查使用者有沒有選擇要上傳的檔案,選擇好的檔案會寫入file_choice的table內
		
		dialog = ProgressDialog.show(getActivity(), "請稍候", "資料處理中", true);
		dialog.show();
		file_path.add(attachment);
		file_size=(int)filepath.length();
		file_name=filepath.getName();
		postFile = new String();
		boolean[] checktype = new boolean[att_parameter.filetype];
		checktype = att_parameter.checktype(attachment);
			// 如果選擇的是影片，則計算他的影片長度、大小，目的是要傳給ffmpeg使用
		if (checktype[att_parameter.video]) {
		
			// 以長度為基準，假設20MB為一單位，求出20MB需幾秒,Math.round為四捨五入公式
			// 以秒數為基準，假設20MB為9.8秒，求出檔案依照秒數切,需要切幾塊,Math.ceil為求出大於此數的最小整數,ex.大於12.8的最小整數為13
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
			//ffmpeg(file_path.get(index), 0, duration); // 切割
			// 參數：檔名、起始位置、切割時間、輸出檔案
		} else if (checktype[att_parameter.music] || checktype[att_parameter.photo]) {
			// 除影片外，其他都不用切
			File file = new File(file_path.get(index));
			// 開啟計算檔案長度並存入attachSize
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
			// 呼叫自定義的函式

	}
	class republish extends AsyncTask<Void, Void, String>{
		ProgressDialog republishdialog=null;
		
		protected void onPreExecute() {
			republishdialog = ProgressDialog.show(getActivity(), "請稍候", "資料讀取中", true);


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
					//訊息跟第一塊都有上去，只是沒收到respone，更新
					//更新token跟first
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
					//這裡要新增dalog
				}else if(msg[2].equals("false")){
					//token有上去但是file沒有
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
					//跟新toekn跟上傳第一塊
				}
				//retrieve.token = token.replaceFirst("ret=0&", "");
			}else{
				//完全沒送出
				String[] msg=respone.split("&");
				msg[1]=msg[1].replace("msg=", "");
				if(msg[1].equalsIgnoreCase("token is null")){
					File file = new File(attachment);
					if(file.exists()){
						fileupload(file);
					}
					else{
						//檔案不見了 要做什麼事
					}
					
				}else{
					//其他錯誤
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
		    // 取得最近IP
			file_path.add(attachment);
			if (!(file_path.isEmpty())) {
			    // 如果附加檔案部為空(也就是有file path),則進行檔案的篩選
				File file = new File(attachment);
				finishsubmit = false;
				sqliteString = file.getName().replace("'", "''");

				while (!finishsubmit) {
//				    // 檢查在DB的table中，是否有未上傳的檔案，未傳的檔案filecheck是零，上傳好的是一。

					Cursor check_finish_cursor = getActivity().getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_file"), new String[] { UserSchema._FILEPATH }, "filecheck='0' and filerecord='file0_0' and messagetoken='" +token + "'", null, null);
				    if (check_finish_cursor.getCount() > 0) {
					check_finish_cursor.moveToFirst();
					CharSequence[] path = new CharSequence[check_finish_cursor.getCount()];
					// 將未傳完的檔案，使用陣列儲存起來
					for (int i = 0; i < check_finish_cursor.getCount(); i++) {
					    path[i] = check_finish_cursor.getString(0);
					    check_finish_cursor.moveToNext();
					}
					// 進行副檔名篩選
					boolean[] checktype = new boolean[att_parameter.filetype];
					checktype = att_parameter.checktype(file.getName());
					// 如果是音樂、圖片
					if (checktype[att_parameter.music] | checktype[att_parameter.photo]) {
					    
						Algocount = 1;
						submit mpsubmit= new submit();
						// 先設定檔案路徑給submit
						mpsubmit.setattachfile(attachment);
						// 傳入要執行的次數以及要執行的spilt path[]
						mpsubmit.setsource(Algocount, path);

						// 上傳檔案
						mpsubmit.submit_file(token, 0);
						// 讀回response
						submit_file_readline = mpsubmit.getsubmit_file_readline();
						mpsubmit=null;
					} else if (checktype[att_parameter.video]) {
					    // Algocount是表示目前要上傳幾塊，path中就會根據Algocount來決定要讀出幾筆資料
					    // 做第一塊的目的在於，測試目前的網路狀態為何
						submit vsubmit=new submit();
					    if (first == true) {
							first = false;
							Algocount = 1;
							/*
							 * 下面三筆都是自定義的函式，先是設定未切割的檔案路徑，目的是用來篩選用
							 * 再來是設定要上傳的檔案路徑及要上傳的檔案個數
							 * ，此method會製作server需要的資訊 最後才是真正要執行上傳檔案
							 */
		
							// 先設定檔案路徑給submit
							
							vsubmit.setattachfile(attachment);
							// 傳入要執行的次數以及要執行的spilt path[]
							vsubmit.setsource(Algocount, path);
							// 上傳檔案
							vsubmit.submit_file(token, 0);
							// 取回response,用來待會做nack的判斷
							submit_file_readline = vsubmit.getsubmit_file_readline();
							check_finish_cursor.close();
					    } else {
							// 同第一塊做法，不同在於，執行次數採隨機
							Algocount = (int) (Math.random() * 2 + 1);
							// Algocount=1;
							// 如果欲執行的次數大於剩下要上傳的檔案，則執行次數以剩下的檔案為主
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
					// 先將剛才上傳的檔案，把filecheck set成1
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
					 * 取回readline,檢查nack,nack是告知哪一個檔案上傳失敗，
					 * 根據nack把filecheck set成0，在下一次重新上傳
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
					// 若filecheck=0的都沒找到，代表檔案已經傳完
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

		// Asyntask 前置作業
		@Override
		protected void onPreExecute() {
			dialog.dismiss();
			mHandler.obtainMessage(closedialog).sendToTarget(); // 傳送要求更新list的訊息給handler
			// 開啟資料傳送dialog

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
				// 先設定request字串
				Submit.setrequestString("subject=" + tittle + "&content=" + content + 
						"&selfid="+selfid+"&receiver=" + receiver + 
						"&filecnt=" + file_amount + "&duration=" + duration + 
						"&filename=" + filename + "&filetype=" + filetype[1] + 
						"&filepath=" + attachment + "&length=" + file_size + 
						"&firstlength=" + (int)firstfile.length()+"&urgent=" + urgent  + postFile);
				String resp = Submit.submit1(login.submit1.Login.latest_cookie,firstfile);				
				// 這邊是用來檢查收件者是否存在於server中，若不存在則取消這次的上傳
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
					
					//UPDATE TOKEN至草稿夾
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

					//第一塊上傳完  更新FIRST為TRUE
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
					mHandler.obtainMessage(timeout).sendToTarget(); // 傳送要求更新list的訊息給handler
				}
				else{
					mHandler.obtainMessage(error).sendToTarget();
				}
			} else {
				 // 傳送要求更新list的訊息給handler
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
