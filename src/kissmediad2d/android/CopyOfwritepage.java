package kissmediad2d.android;


import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;


import login.submit1.Logout;
import login.submit1.locupdate;
import login.submit1.retrieve;
import login.submit1.submit;
import tab.list.FileUtils;
import tab.list.att_parameter;
import tab.list.f_tab;
import tab.list.fileContentProvider;
import tab.list.fileContentProvider.UserSchema;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import ffmpeg.process.ffmpeg_service;
public class CopyOfwritepage extends Fragment implements OnClickListener {
	/*
	 * 這邊是用來作驥通知的動作，也是整個KM的主要核心之一，因為需要做檔案的切割
	 */
	 String receiver, title, attachment = null, content, state;
	 String filetype[];
	 EditText etR, etT, etC;
	 TextView tvName;
	 Button delete;
	 ToggleButton sms;
	 ImageView previewImg;
	 float TWOMB = 5 * 1024 * 1024, ONEMB;
	 int file_amount, split_seq = 0;
	 static final int BLACK = -16777216;  // Constant to represent the RGB binary value of black. In binary - 1111111 00000000 00000000 00000000
	  static final int WHITE = -1;  // Constant to represent the RGB binary value of white. In binary - 1111111 1111111 1111111 1111111
	 Bitmap magnified_key_image_2,keyImage,chiperImage,finalimage,filebitmap,black_white,magnified_key_image;
	    
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
	int index, urgent = 0;
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");

	retrieve retreive = new retrieve();

	int ftimes;
	 String file_name = new String(), postFile, filepath;
	 ArrayList<String> file_path;
	 Float total_time;
	 int duration, file_size, Algocount;
	ProgressDialog senddialog = null;
	 VideoTrimmer trimmer;
	 String alive_cookie,selfid;
	submit Submit;
	ProgressDialog dialog = null;
	boolean finishsubmit = false, first = true;
	FileUtils fileutils = new FileUtils();
	FileUtils oname = new FileUtils();
	String outFileName;
	File file;
	 public String sdcardPath = Environment.getExternalStorageDirectory().toString() + File.separator + "KM" + "/";
	boolean checkFileType = false ;
	locupdate locup =new locupdate();
	public String token ,filename;
	public void setfilepath(String arg) {
		attachment = arg;

	}

	public void setduration(int arg) {
		duration = arg;

	}
	private static final String ARG_SECTION_NUMBER = "section_number";

	private Handler mHandler = new Handler() {
		
		// 此方法在ui線程運行
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case closedialog:
				senddialog = ProgressDialog.show(getActivity(), "請稍候", "資料處理中", true);
				senddialog.show();
				break;
			case timeout:
				att_parameter.ffmpeg_now=false;
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
						fileContentProvider test = new fileContentProvider();
						test.del_table(Uri.parse("content://tab.list.d2d/file_choice"));
						previewImg.setVisibility(View.INVISIBLE);
						tvName.setVisibility(View.INVISIBLE);
						delete.setVisibility(View.INVISIBLE);
						etR.setText("");
						etT.setText("");
						etC.setText("");
					}
				});
				Dialog0.show();
				break;
			case ok:
				AlertDialog.Builder Dialog = new AlertDialog.Builder(getActivity()); // Dialog
				Dialog.setTitle("");
				Dialog.setMessage("傳送失敗，查無此收件者");
				Dialog.setIcon(android.R.drawable.ic_dialog_info);
				Dialog.setNeutralButton("確定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						etR.setText("");
						att_parameter.ffmpeg_now=false;
					}
				});
				Dialog.show();
				break;
			case error:
				// 傳送成功後顯示成功視窗
				AlertDialog.Builder Dialog1 = new AlertDialog.Builder(getActivity()); // Dialog
				Dialog1.setTitle("");
				Dialog1.setMessage("發佈成功");
				Dialog1.setIcon(android.R.drawable.ic_dialog_info);
				Dialog1.setNeutralButton("確定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						etR.setText("");
						etT.setText("");
						etC.setText("");
						att_parameter.ffmpeg_now=false;
					}
				});
				Dialog1.show();
				break;
			}
		}
	};
	public static CopyOfwritepage newInstance(int sectionNumber) {
		CopyOfwritepage fragment = new CopyOfwritepage();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}
	public CopyOfwritepage() {
		fileContentProvider test = new fileContentProvider();
		test.del_table(Uri.parse("content://tab.list.d2d/file_choice"));
		
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
		attachment = new String();
		View rootView = inflater.inflate(R.layout.write, container,
				false);
		//send = (Button) rootView.findViewById(R.id.sendmail);
		//attach = (Button) rootView.findViewById(R.id.attachbutton);
		delete = (Button) rootView.findViewById(R.id.delete);
		tvName = (TextView) rootView.findViewById(R.id.writename);
		previewImg = (ImageView) rootView.findViewById(R.id.previewimg);
		sms = (ToggleButton) rootView.findViewById(R.id.Regsb);
		sms.setOnCheckedChangeListener(sbcheck);
		previewImg.setVisibility(View.INVISIBLE);
		tvName.setVisibility(View.INVISIBLE);
		delete.setVisibility(View.INVISIBLE);
		previewImg.setClickable(true);
		previewImg.setOnClickListener(this);
		
		//send.setOnClickListener(this);
		//attach.setOnClickListener(this);
		delete.setOnClickListener(this);
		tvName.setOnClickListener(this);
		etR = (EditText) rootView.findViewById(R.id.receiver);
		etT = (EditText) rootView.findViewById(R.id.subject);
		etC = (EditText) rootView.findViewById(R.id.content);
		Submit = new submit();
		// 這一段是做廣播用的，當ffmpeg做完時會透過廣播的方式，告訴writepage做下一個切割

		return rootView;
	}
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
    	menu.clear();
    	inflater.inflate(R.menu.publish, menu);
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
			ConnectivityManager CM = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = CM.getActiveNetworkInfo();
    		if (info == null || !info.isAvailable()) {
    			
    		}else{
    			Logout logout = new Logout();
    			logout.logout_start();
    		
    		}
			info=null;
			CM=null;
			// intent.setClass(writepage.this, browse.class);
			intent.setClass(getActivity(), logininput.class);
			// startActivityForResult(intent,0);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); 
			startActivity(intent);
			getActivity().finish();
			
			break;
        case R.id.attachfile:
    		// intent.setClass(writepage.this, browse.class);
    		intent.setClass(getActivity(), f_tab.class);
    		// startActivityForResult(intent,0);
    		startActivity(intent);
    		break;
        case R.id.send:
        	if(att_parameter.ffmpeg_now){
        		Toast.makeText(getActivity(), "抱歉，目前有背景程式在處理，請稍後再試", Toast.LENGTH_LONG).show();
        	}else{
        		sendmail();
        	}
    		
    		break;
        case android.R.id.home:

        	getActivity().finish();
        	break;
        }
        
		return true;
	}
	
	//辨識目前選擇是sms還是wlan
	public OnCheckedChangeListener sbcheck = new OnCheckedChangeListener() {
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked) {
				urgent=1;
				sms.setChecked(true);
			} else {
				 urgent= 0;
				 sms.setChecked(false);
			}
			
		}
	};
	/*
	 * 這邊是監聽在這個layout上的button
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
//		case R.id.sendmail:
//			Cursor up_file_cursor = getActivity().getContentResolver().query(uri, form, null, null, null);
//			if (up_file_cursor.getCount() > 0) {
//				// 自定義的函式，此為開始切割檔案>上傳 的完整動作的開頭
//				AlertDialog.Builder Dialog = new AlertDialog.Builder(getActivity());
//				Dialog.setTitle("上傳檔案");
//				Dialog.setMessage("請選擇通知方式");
//				Dialog.setIcon(android.R.drawable.ic_dialog_info);
//				Dialog.setNegativeButton("使用簡訊通知", new DialogInterface.OnClickListener() { // 不接收檔案
//							@Override
//							public void onClick(DialogInterface dialog, int which) {
//								urgent = 1;
//								sendmail();
//							}
//						});
//				Dialog.setPositiveButton("使用網路通知", new DialogInterface.OnClickListener() { // 接收檔案
//							@Override
//							public void onClick(DialogInterface dialog, int which) {
//								urgent = 0;
//								sendmail();
//							}
//						});
//				Dialog.show();
//			} else {
//				Toast.makeText(getActivity().getApplicationContext(), "請至少選擇一份檔案", Toast.LENGTH_LONG).show();
//			}
//
//			break;
//		case R.id.attachbutton:
//			// 選擇附加的檔案
//			attachbutton();
//			break;
		case R.id.previewimg:
			viewfile();
			break;
		case R.id.delete:
			// 刪除所選擇的檔案，同時把訊息隱藏起來
			fileContentProvider test = new fileContentProvider();
			test.del_table(Uri.parse("content://tab.list.d2d/file_choice"));
			previewImg.setVisibility(View.INVISIBLE);
			tvName.setVisibility(View.INVISIBLE);
			delete.setVisibility(View.INVISIBLE);
			break;

		}
	}

	
	String[] form = { UserSchema._FILEPATH, UserSchema._DURATION, UserSchema._FILESIZE, UserSchema._FILENAME, UserSchema._ID };
	

	public void fileupload(String arg) {
		// 檢查使用者有沒有選擇要上傳的檔案,選擇好的檔案會寫入file_choice的table內
		file_path = new ArrayList<String>();
		Cursor up_file_cursor = getActivity().getContentResolver().query(Uri.parse("content://tab.list.d2d/file_choice"), form, null, null, null);
		if (up_file_cursor.getCount() > 0) {
			dialog = ProgressDialog.show(getActivity(), "請稍候", "資料處理中", true);
			dialog.show();
			up_file_cursor.moveToFirst();
			file_path.add(up_file_cursor.getString(0));
			selfid=randomString(20);
			postFile = new String();
			for (index = 0; index < up_file_cursor.getCount(); index++) {
				boolean[] checktype = new boolean[att_parameter.filetype];
				checktype = att_parameter.checktype(attachment);
				// 如果選擇的是影片，則計算他的影片長度、大小，目的是要傳給ffmpeg使用
				if (checktype[att_parameter.video]) {
					total_time = up_file_cursor.getFloat(1) / 1000;
					file_size = Integer.valueOf(up_file_cursor.getString(2));
					file_name = up_file_cursor.getString(3);
					// 計算1byte為幾秒
					ONEMB = total_time / file_size; // 計算1MB大約有幾秒
					System.out.println("ONEMB==" + ONEMB);
					// 以長度為基準，假設20MB為一單位，求出20MB需幾秒,Math.round為四捨五入公式
					duration = Math.round(ONEMB * TWOMB);
					System.out.println("duration==" + duration);
					// 以秒數為基準，假設20MB為9.8秒，求出檔案依照秒數切,需要切幾塊,Math.ceil為求出大於此數的最小整數,ex.大於12.8的最小整數為13
					file_amount = (int) Math.ceil((double) total_time / duration);
					System.out.println("filecnt==" + file_amount);
					String[] tempfilename = new String[file_amount];
					for (int i = 0; i < file_amount; i++) {
						tempfilename[i] = "file_name" + index + "_" + String.valueOf(i) + "=" + file_name;
						postFile = postFile + "&" + tempfilename[i];
					}
					/**
					 * filecontenprovider是自定義的資料庫，此為記錄所有table的資訊，
					 * 這裡的del_table是自定義的方法，表刪除所選擇的table 刪除temp_file這張table的用途在於
					 * ，避免保留上一次的上傳清單出現在這一次新的清單內
					 */
					
					ContentValues values = new ContentValues();
					values.put(UserSchema._FILEPATH, file_path.get(index));
					values.put(UserSchema._TITTLE, title);
					values.put(UserSchema._CONTENT, content);
					values.put(UserSchema._RECEIVER, receiver);
					values.put(UserSchema._SELFID, selfid);
					values.put(UserSchema._DURATION, duration);
					values.put(UserSchema._TOTAL_TIME, total_time);
					getActivity().getContentResolver().insert(Uri.parse("content://tab.list.d2d/temp_content"), values);
					//Thread aliveThread = new Thread(ffmpeg_run);
					//aliveThread.start();
					ffmpeg_service aa= new ffmpeg_service();
					//String req=selfid+"&"+
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
							dialog.dismiss();
							new SendandAttach().execute();
						}
					});
					publish.start();
					aa.ffmpeg(getActivity(),getActivity().getContentResolver(),file_path.get(index), 0, duration,selfid,total_time,0);
					aa=null;
					

					//ffmpeg(file_path.get(index), 0, duration); // 切割
					// 參數：檔名、起始位置、切割時間、輸出檔案
				} else if (checktype[att_parameter.music]) {
					// 除影片外，其他都不用切
					File file = new File(file_path.get(index));
					// 開啟計算檔案長度並存入attachSize
					file_size = (int) file.length();
					file_name = file.getName();
					postFile = postFile + "&file_name" + index + "_0=" + file_name;
					duration = 0;
					file_amount=1;
					FileUtils oname = new FileUtils();
					
					ContentValues values = new ContentValues();
					try {
						FileInputStream inputStream = new FileInputStream(new File(file_path.get(index)));
						 byte[] data = new byte[1024];
						 FileOutputStream outputStream =new FileOutputStream(new File(Environment.getExternalStorageDirectory().toString() + File.separator + "KM" + "/"+file_name)); 
					        while (inputStream.read(data) != -1) {  
					            outputStream.write(data);  
					        }  
					        inputStream.close();  
					        outputStream.close(); 
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}  catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			        
			        
					values.put(UserSchema._FILEPATH, Environment.getExternalStorageDirectory().toString() + File.separator + "KM" + "/"+file_name);
					values.put(UserSchema._FILERECORD, "file"+index + "_0");
					values.put(UserSchema._FILECHECK, 0);
					values.put(UserSchema._FILENAME, file_name);
					values.put(UserSchema._SELFID, selfid);
					getActivity().getContentResolver().insert(Uri.parse("content://tab.list.d2d/temp_file"), values);
					//dialog.dismiss();
					state = "write";
					dialog.dismiss();
					new SendandAttach().execute();
				}else if(checktype[att_parameter.photo]){
                    // 除影片外，其他都不用切
                    File file = new File(file_path.get(index));
                    // 開啟計算檔案長度並存入attachSize
                    file_size = (int) file.length();
                    file_name = file.getName();
                    file_amount=2;
                    String[] tempfilename = new String[file_amount];
                    for (int i = 0; i < file_amount; i++) {
                        tempfilename[i] = "file_name" + index + "_" + String.valueOf(i) + "=" + file_name;
                        postFile = postFile + "&" + tempfilename[i];
                    }
                    duration = 0;

                    checkFileType=true;

                    //2016/06/30新增修改(檔案的copy)
                    try {
                        FileInputStream inputStream = new FileInputStream(new File(file_path.get(index)));
                        byte[] data = new byte[1024];
                        FileOutputStream outputStream =new FileOutputStream(new File(Environment.getExternalStorageDirectory().toString() + File.separator + "KM" + "/"+file_name));
                        while (inputStream.read(data) != -1) {
                            outputStream.write(data);
                        }
                        inputStream.close();
                        outputStream.close();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }  catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    state = "write";
                    dialog.dismiss();
                    new SendandAttach().execute();
                    }else{
					att_parameter.ffmpeg_now=false;
					Toast.makeText(getActivity(), "你所選的檔案類型不支援，請重新選擇", Toast.LENGTH_LONG).show();
					dialog.dismiss();
				}
				up_file_cursor.moveToNext();
			}
		} else {
			att_parameter.ffmpeg_now=false;
			Toast.makeText(getActivity(), "尚未選擇附加檔案，請重新選擇", Toast.LENGTH_LONG).show();
		}

		state = "write";
		up_file_cursor.close();
		index = 0;
		// 呼叫自定義的函式

	}

	public void sendmail() {
		receiver = etR.getText().toString();
		title = etT.getText().toString();
		content = etC.getText().toString();
		ConnectivityManager CM = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = CM.getActiveNetworkInfo();

		if (receiver.equals("")) {
			Toast.makeText(getActivity(), "收件者不可為空白", Toast.LENGTH_LONG).show();
		}else if (title.equals("")) {
			Toast.makeText(getActivity(), "標題不可為空白", Toast.LENGTH_LONG).show();
		}else if (content.equals("")) {
			Toast.makeText(getActivity(), "內容不可為空白", Toast.LENGTH_LONG).show();
		}else if (info == null || !info.isAvailable()) {
			Toast.makeText(getActivity(), "目前沒有網路唷!所以無法發佈", Toast.LENGTH_LONG).show();
		}else{
			att_parameter.ffmpeg_now=true;
			fileupload("write");
		}
		info=null;
		CM=null;

	}

	 class SendandAttach extends AsyncTask<Void, Void, String> // implement
	// thread
	{
		public int id_this;
		public String where;
		
		public String path;

		// Asyntask 前置作業
		@Override
		protected void onPreExecute() {
			
			mHandler.obtainMessage(closedialog).sendToTarget(); // 傳送要求更新list的訊息給handler
			// 開啟資料傳送dialog

		}

		@Override
		protected String doInBackground(Void... params) {
			String er = "no&";
			String sqliteString;
			String file0_0;
			File firstfile = null;
			System.out.println("state會錯"+state);
			state="write";
			boolean[] checktype = null;
			if (state.equals("write")) {
				if(checkFileType){
                    
                    checkFileType=false;
                }
				/// ex : storage/emulated/0/DCIM/100ANDRO/MOV_0259.mp4
				File file = new File(attachment);
				String filename = file.getName();
				filetype = filename.split("\\.");
				Cursor check_finish_cursor = getActivity().getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_file"), new String[] { UserSchema._FILEPATH }, "filecheck='0' and filerecord='file0_0' and selfid='" + selfid + "'", null, null);
				if(check_finish_cursor.getCount()>0){
					check_finish_cursor.moveToFirst();
					file0_0=check_finish_cursor.getString(0);
					firstfile =new File(file0_0);
					checktype = new boolean[att_parameter.filetype];
					checktype = att_parameter.checktype(file.getName());
				}
				check_finish_cursor.close();
				// 2013/4/14 4$刪掉filecount與post
				// 2013/8/10/ 補上filecnt並把前面;改成&
				System.out.println("postFile==" + postFile);
				// 先設定request字串
				
				// 2013/11/08 豆豆 修改上傳資訊metadata
				Submit.setrequestString("subject=" + title + "&content=" + content + 
										"&selfid="+selfid+"&receiver=" + receiver + 
										"&filecnt=" + file_amount + "&duration=" + duration + 
										"&filename=" + filename + "&filetype=" + filetype[1] + 
										"&filepath=" + attachment + "&length=" + file_size + 
										"&firstlength=" + (int)firstfile.length()+"&urgent=" + urgent  + postFile);
				// 2013/4/3 4$新增cookie(alive_cookie)
				
				//純入草稿夾
				
				String resp = Submit.submit1(login.submit1.Login.latest_cookie,firstfile);
				
				
				
				// 這邊是用來檢查收件者是否存在於server中，若不存在則取消這次的上傳
				if (!(att_parameter.chechsuccess(resp))) {
					if(resp.equalsIgnoreCase("timeout")){
						er = "yes&timeout";
					}else{
						er = "yes&";
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
					token=resp.replace("ret=0&token=", "");//20160802
					Cursor up_tempfile = getActivity().getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_file"), Form, "selfid='" + selfid + "' and filecheck='0'", null, null);
					if (up_tempfile.getCount() > 0) {
						up_tempfile.moveToFirst();
						ContentValues values = new ContentValues();
						values.put(UserSchema._FILECHECK, 1);
						if (checktype[att_parameter.music] || checktype[att_parameter.photo]){
							values.put(UserSchema._MESSAGETOKEN, token);//20160802	
						
						}
						int id_this = Integer.parseInt(up_tempfile.getString(0));
						String where = UserSchema._ID + " = " + id_this;
						getActivity().getContentResolver().update(Uri.parse("content://tab.list.d2d/temp_file"), values, where, null);
					}
					up_tempfile.close();
					
					if (checktype[att_parameter.photo]){
						//20160801 更新圖片所有的token
						Cursor uptoken_cursor = getActivity().getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_file"), Form, "selfid='"+selfid+"'", null, null);
						if (uptoken_cursor.getCount() > 0) {
							ContentValues values = new ContentValues();
							values = new ContentValues();
							uptoken_cursor.moveToFirst();
							values.put(UserSchema._MESSAGETOKEN, token);
							for (int i = 0; i < uptoken_cursor.getCount(); i++) {
								int id_this = Integer.parseInt(uptoken_cursor.getString(0));
								String file_where = UserSchema._ID + " = " + id_this;
								getActivity().getContentResolver().update(Uri.parse("content://tab.list.d2d/temp_file"), values, file_where, null);
								uptoken_cursor.moveToNext();
							}
						}
						uptoken_cursor.close();
					}
					
					// 第一塊傳完

				}
			}
			return er;
		}

		protected void onPostExecute(String er) {
			String []resp = er.split("&");
			senddialog.dismiss();
			if (resp[0].equals("yes")) {
				
				if(resp.length>1 &&resp[1].equalsIgnoreCase("timeout")){
					mHandler.obtainMessage(timeout).sendToTarget(); // 傳送要求更新list的訊息給handler
				}
				else{
					mHandler.obtainMessage(ok).sendToTarget();
				}
				

			} else {
				 // 傳送要求更新list的訊息給handler
				fileContentProvider test = new fileContentProvider();
				test.del_table(Uri.parse("content://tab.list.d2d/file_choice"));
				previewImg.setVisibility(View.INVISIBLE);
				tvName.setVisibility(View.INVISIBLE);
				delete.setVisibility(View.INVISIBLE);
				mHandler.obtainMessage(error).sendToTarget();

			}

		}
	}

	// 此為呼叫選擇檔案的頁面
	public void attachbutton() {
		receiver = etR.getText().toString();
		title = etT.getText().toString();
		content = etC.getText().toString();
		Intent intent = new Intent();
		// intent.setClass(writepage.this, browse.class);
		intent.setClass(getActivity(), f_tab.class);
		// startActivityForResult(intent,0);
		startActivity(intent);

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



	public Intent videoTrimmingServiceIntent() {
		return new Intent(getActivity(), VideoTrimmingService.class);
	}

	// 這邊是接收來自FFMPEG做完時的廣播
//	public class MyBroadcastReceiver extends BroadcastReceiver {
//		int starttime;
//		int split_seq=0,index,duration;
//		float total_time;
//		String path;
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			
//
//			String result = intent.getStringExtra(VideoTrimmingService.EXTRA_KEY_OUT);
//			// 如果目前切割的INDEX小於所要切割的總size，則繼續執行
//			// 例如檔案為50M,每10M一切，總size為5塊
//			String[] medadata =result.split("&");
//			path=medadata[0];
//			File file = new File(path);
//			String outFileName= medadata[1];		
//			index=Integer.valueOf(medadata[2]);
//			split_seq=Integer.valueOf(medadata[3]);
//			duration=Integer.valueOf(medadata[5]);
//			total_time=Float.valueOf(medadata[6]);
//			selfid=medadata[7];
//			Cursor file_exit = getActivity().getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_file"), null, "selfid='"+selfid +"' and filerecord ='"+"file"+index+"_"+split_seq+"'", null, null);
//			if(file_exit.getCount()>0){
//				//do nothing
//			}else{
//				ContentValues values = new ContentValues();
//				values = new ContentValues();
//				values.put(UserSchema._FILEPATH, outFileName);
//				values.put(UserSchema._FILERECORD, "file"+index+"_"+split_seq);
//				values.put(UserSchema._FILECHECK, 0);
//				values.put(UserSchema._SELFID, selfid);
//				values.put(UserSchema._FILENAME, file.getName());
//				//還要加自訂ID(跟SERVER)
//				getActivity().getContentResolver().insert(Uri.parse("content://tab.list.d2d/temp_file"), values);
//			
//			}
//			split_seq++;
//			if(split_seq==1){
//				//dialog.dismiss();
//				new SendandAttach().execute();
//			}else{
//				
//			}
//			if (split_seq < (total_time / duration)) {
//				ffmpeg_service aa= new ffmpeg_service();
//				//String req=selfid+"&"+
//				aa.ffmpeg(getActivity(),getActivity().getContentResolver(),path, (split_seq * duration)-1, duration,selfid,total_time,split_seq);
//				aa=null;
//			}
//			else {
//				Log.i("COPYofWIRTE", "有值行到這邊唷(為曾經切過)");
//				//刪除切割紀錄(用ID抓)
//				split_seq = 0;
//				String token,content_where,file_where,ffmpeg_where = null;
//				String[] Form = { UserSchema._ID,UserSchema._MESSAGETOKEN ,};
//				Cursor token_cursor = getActivity().getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_content"), Form, "selfid='"+selfid+"'", null, null);
//				if (token_cursor.getCount() > 0) {
//					int id_this1 = 0;
//					token_cursor.moveToFirst();
//					id_this1 = Integer.valueOf(token_cursor.getString(0));
//					token=token_cursor.getString(1);
//					ContentValues values1 = new ContentValues();
//					values1.put(UserSchema._FINISH, "yes");
//					String where1 = UserSchema._ID + " = " + id_this1;
//					getActivity().getContentResolver().update(Uri.parse("content://tab.list.d2d/temp_content"), values1, where1, null);
//					//第一塊上傳完  更新FIRST為TRUE
//
//					Cursor uptoken_cursor = getActivity().getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_file"), Form, "selfid='"+selfid+"'", null, null);
//					if (uptoken_cursor.getCount() > 0) {
//						ContentValues values = new ContentValues();
//						values = new ContentValues();
//						uptoken_cursor.moveToFirst();
//						values.put(UserSchema._MESSAGETOKEN, token);
//						for (int i = 0; i < uptoken_cursor.getCount(); i++) {
//							int id_this = Integer.parseInt(uptoken_cursor.getString(0));
//							file_where = UserSchema._ID + " = " + id_this;
//							getActivity().getContentResolver().update(Uri.parse("content://tab.list.d2d/temp_file"), values, file_where, null);
//							uptoken_cursor.moveToNext();
//						}
//					}
//					uptoken_cursor.close();
//					
//					Cursor ffmpeg_id = getActivity().getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_ffmpeg"), new String[] { UserSchema._ID } , "selfid='"+selfid+"'", null, null);
//					if (ffmpeg_id.getCount() > 0) {
//						ffmpeg_id.moveToFirst();
//						ffmpeg_where =ffmpeg_id.getString(0);						
//					}
//					ffmpeg_id.close();
//					
//					int id_this =Integer.parseInt(ffmpeg_where);
//					String where = UserSchema._ID + " = " +  id_this;
//					getActivity().getContentResolver().delete(Uri.parse("content://tab.list.d2d/temp_ffmpeg"), where, null);
//				}
//				token_cursor.close();
//				Toast.makeText(getActivity().getApplicationContext(), "YA!切完了", Toast.LENGTH_LONG).show();
//			}
//
//		}
//	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// un-register BroadcastReceiver


	}

	// 當從attachment回來時，顯示剛剛所選擇的檔案在writepage上。
	public void onResume() {

		super.onResume();
		Cursor ch_tmepfile = getActivity().getContentResolver().query(Uri.parse("content://tab.list.d2d/file_choice"), form, null, null, null);
		if (ch_tmepfile.getCount() > 0) {
			ch_tmepfile.moveToFirst();
			attachment = ch_tmepfile.getString(0);
			File file = new File(attachment);
			tvName.setText(file.getName());
			previewImg.setVisibility(View.VISIBLE);
			tvName.setVisibility(View.VISIBLE);
			delete.setVisibility(View.VISIBLE);
			setPreviewImg();
		}
		ch_tmepfile.close();
	}
	 public String randomString(int len) {
		  String str = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		  StringBuffer sb = new StringBuffer();
		  for (int i = 0; i < len; i++) {
		   int idx = (int)(Math.random() * str.length());
		   sb.append(str.charAt(idx));
		  }
		  return sb.toString();
		 }
	 
	

}
