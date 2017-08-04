package kissmediad2d.android;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import login.submit1.Login;
import login.submit1.locupdate;
import login.submit1.retrieve;
import login.submit1.submit;
import tab.list.FileUtils;
import tab.list.att_parameter;
import tab.list.f_tab;
import tab.list.fileContentProvider;
import tab.list.fileContentProvider.UserSchema;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import database.storage.messageProcess;

public class writepage extends Activity implements OnClickListener {
	/*
	 * 這邊是用來作驥通知的動作，也是整個KM的主要核心之一，因為需要做檔案的切割
	 */
	private String receiver, title, attachment = null, content, state;
	private String sender, filetype[];
	private EditText etR, etT, etC;
	private TextView tvName;
	private Button send, attach, delete, send_d2d;
	private ImageView previewImg;
	private float TWOMB = 5 * 1024 * 1024, ONEMB;
	private int file_amount, split_seq = 0;
	private String nack = "", ffmpeg_path = new String();
	public String submit_file_readline;
	String str1;
	public Boolean finish =false;
	public BufferedInputStream inputmasge ;
	public DataOutputStream dataOut ;

	int index, urgent = 0;
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");

	retrieve retreive = new retrieve();
	Uri uri;
	int ftimes;
	private String file_name = new String(), postFile, filepath;
	private ArrayList<String> file_path;
	private Float total_time;
	private int duration, file_size, Algocount;
	private MyBroadcastReceiver myBroadcastReceiver;
	private VideoTrimmer trimmer;
	private String alive_cookie;
	submit Submit;
	ProgressDialog dialog = null;
	boolean finishsubmit = false, first = true;
	FileUtils fileutils = new FileUtils();
	FileUtils oname = new FileUtils();
	locupdate locup =new locupdate();
	public void setfilepath(String arg) {
		attachment = arg;

	}

	public void setduration(int arg) {
		duration = arg;

	}

	public writepage() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.write);
		setTitle("Send");
		attachment = new String();
		//send = (Button) findViewById(R.id.sendmail);
//		attach = (Button) findViewById(R.id.attachbutton);
		delete = (Button) findViewById(R.id.delete);
		tvName = (TextView) findViewById(R.id.writename);
		previewImg = (ImageView) findViewById(R.id.previewimg);
		
		previewImg.setVisibility(View.INVISIBLE);
		tvName.setVisibility(View.INVISIBLE);
		delete.setVisibility(View.INVISIBLE);
		previewImg.setClickable(true);
		previewImg.setOnClickListener(this);

		//send.setOnClickListener(this);
		//attach.setOnClickListener(this);
		delete.setOnClickListener(this);
		tvName.setOnClickListener(this);
		etR = (EditText) findViewById(R.id.receiver);
		etT = (EditText) findViewById(R.id.subject);
		etC = (EditText) findViewById(R.id.content);

		Submit = new submit();
		// 這一段是做廣播用的，當ffmpeg做完時會透過廣播的方式，告訴writepage做下一個切割
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(VideoTrimmingService.ACTION_MyIntentService);
		intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

		registerReceiver(myBroadcastReceiver, intentFilter);
		getIntent().setData(Uri.parse("content://tab.list.d2d/file_choice"));
		final Uri uri_test = getIntent().getData();
		uri = uri_test;
	}

	/*
	 * 這邊是監聽在這個layout上的button
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
//		case R.id.sendmail:
//			Cursor up_file_cursor = getContentResolver().query(uri, form, null, null, null);
//			if (up_file_cursor.getCount() > 0) {
//				// 自定義的函式，此為開始切割檔案>上傳 的完整動作的開頭
//				AlertDialog.Builder Dialog = new AlertDialog.Builder(this);
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
//				Toast.makeText(getApplicationContext(), "請至少選擇一份檔案", Toast.LENGTH_LONG).show();
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


	public void send_msg(String message){
		try{
			dataOut.writeUTF(message);
			dataOut.flush();
		}catch (IOException e) {
		      e.printStackTrace();
	    } 
		
	}
	
	public String get_msg(){
		String input_msg="";
		try{
			input_msg= 	new DataInputStream(inputmasge).readUTF();
		
		}catch (IOException e) {
		      e.printStackTrace();
	    }  
		
		return input_msg;
	}
	
	String[] form = { UserSchema._FILEPATH, UserSchema._DURATION, UserSchema._FILESIZE, UserSchema._FILENAME, UserSchema._ID };
	
	public class send_d2d extends AsyncTask<Void, Void, String> {

		ProgressDialog senddialog = null;
		String ff;
		InetAddress serverAddr = null;
		SocketAddress sc_add =null;
		Socket socket =null;
		public String[] aliveIp;
		String er = "no";
		// Asyntask 前置作業
		@Override
		protected void onPreExecute() {
			// 開啟資料傳送dialog
			senddialog = ProgressDialog.show(writepage.this, "請稍候", "資料上傳中", true);
			senddialog.show();
		}		
		@Override
		protected String doInBackground(Void... arg0) {
			System.out.println("失敗失敗");
			Cursor up_file_cursor = getContentResolver().query(uri, form, null, null, null);
			if (up_file_cursor.getCount() > 0) {
				up_file_cursor.moveToFirst();
				ff =up_file_cursor.getString(0);
			}
			up_file_cursor.close();
			File file = new File(ff);
 			System.out.printf("傳送檔案: %s%n", file.getName());
			aliveIp = locup.locationupdate(Login.latest_cookie,logininput.getIPAddress(getApplicationContext()),att_parameter.port);
			System.out.println(aliveIp[0]);
			messageProcess MsgSave = new messageProcess();
			MsgSave.checkwlan(getContentResolver(), aliveIp[4]);
			MsgSave=null;

			
			String resp=locup.getip(receiver);
			System.out.println("this="+resp+"  ");
			if (!(att_parameter.chechsuccess(resp))) {
				er = "yes";
				System.out.println(er);
			}
			else{
				System.out.println(er+"  ");
				try{
					String[] temp = resp.split("&");
					System.out.println("  "+resp);
					String[] socketport = temp[1].replace("socket=", "").split(":");
					System.out.println("yo_"+socketport[0]+"  ");
					if(att_parameter.out_ip.equals(socketport[0])){
						serverAddr = InetAddress.getByName(socketport[1]);
						System.out.println("socketport[1]="+socketport[1]);
					}else{
						serverAddr = InetAddress.getByName(socketport[0]);
						System.out.println("socketport[0]="+socketport[0]);
					}
					
	  				sc_add= new InetSocketAddress(serverAddr,Integer.valueOf(socketport[2]));
	  				socket = new Socket();
	  				//嘗試連線，成功就會往嚇跑
	  				socket.connect(sc_add,2000);
	  				System.out.println("connsess=");
					//讀取資料，讀到才會往下
	  				inputmasge = new BufferedInputStream(socket.getInputStream());
	  				dataOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
						
	  				System.out.println("start");
  					//讀出資料
					String mesage = get_msg();
					System.out.println(finish);
					finish=false;
	  				while(!finish){
	  					System.out.println(mesage);
	  					if(mesage.equals("sub_ok")){
	  						mesage="";
		  		  	        //輸出檔名給對方
	  						System.out.println("sub_ok11111");
//	  						DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
//	  						dataOut.writeUTF(file.getName());
	  						send_msg(file.getName());
	  						String gg =get_msg();
	  						System.out.println(gg);
	  						//dataOut.writeUTF("/storage/emulated/0/mnt/sdcard/video.mp4");
	  		  	           // dataOut.flush();
		  		  	        System.out.println("sub_ok1");
	  		  	            //取得回覆訊息
		  		  	        //String mesage1 = new DataInputStream(new BufferedInputStream(socket.getInputStream())).readUTF();
		  		  	       // System.out.println(mesage1);
	  		  	            //開始傳檔案，先讀取我們要傳的檔案
		  		  	        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file)); 
		  		  	    DataOutputStream dataOut1 = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
							
		  		  	        int readin; 
	  		  	            while((readin = inputStream.read()) != -1) { 
	  		  	                //他寫入到輸出 
	  		  	            	dataOut1.write(readin); 
	  		  	                 Thread.yield();
	  		  	            }
	  		  	        //mesage = new DataInputStream(new BufferedInputStream(socket.getInputStream())).readUTF();
	  		  	        dataOut1.close();
		  	            inputStream.close();
		  	            System.out.println("end");
	  					}
	  					else if(mesage.equals("ret_ok")){
	  						System.out.println("end2");
	  						inputmasge.close();
	  						finish=true;
	  						System.out.println("end3");
	  					}
	  					else {
	  						finish=true;
	  					}
	  						
	  				}
	  		 
	  	 
	  	            
	  	 
	  	            socket.close();
	  	 
	  	            System.out.println("\n檔案傳送完畢！"); 
	  			}catch (UnknownHostException e) {
	  				
	  		    } catch (SocketException e) {


	  		    } catch(IOException e) {

	  		    	System.out.println("\n失敗！"); 
	  		    }
	  		

			}
			return er;
			}
		protected void onPostExecute(String er) {
			fileContentProvider test = new fileContentProvider();
			test.del_table(Uri.parse("content://tab.list.d2d/file_choice"));
			senddialog.dismiss();
			if (er.equals("yes")) {
				// 若er成立，則表示輸入的收件者不存在
				AlertDialog.Builder Dialog = new AlertDialog.Builder(writepage.this); // Dialog
				Dialog.setTitle("");
				Dialog.setMessage("傳送失敗，查無此收件者");
				Dialog.setIcon(android.R.drawable.ic_dialog_info);
				Dialog.setNeutralButton("確定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						etR.setText("");
						etT.setText("");
						etC.setText("");
					}
				});
				Dialog.show();
			} else {

				// 傳送成功後顯示成功視窗
				AlertDialog.Builder Dialog = new AlertDialog.Builder(writepage.this); // Dialog
				Dialog.setTitle("");
				Dialog.setMessage("傳送成功");
				Dialog.setIcon(android.R.drawable.ic_dialog_info);
				Dialog.setNeutralButton("確定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						etR.setText("");
						etT.setText("");
						etC.setText("");
					}
				});
				Dialog.show();
			}

		}
		
	}

	
	public void fileupload(String arg) {
		// 檢查使用者有沒有選擇要上傳的檔案,選擇好的檔案會寫入file_choice的table內
		file_path = new ArrayList<String>();
		Cursor up_file_cursor = getContentResolver().query(uri, form, null, null, null);
		if (up_file_cursor.getCount() > 0) {
			dialog = ProgressDialog.show(writepage.this, "請稍候", "資料處理中", true);
			dialog.show();
			up_file_cursor.moveToFirst();
			file_path.add(up_file_cursor.getString(0));

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
					ffmpeg(file_path.get(index), 0, duration); // 切割
					// 參數：檔名、起始位置、切割時間、輸出檔案
				} else if (checktype[att_parameter.music] || checktype[att_parameter.photo]) {
					// 除影片外，其他都不用切
					File file = new File(file_path.get(index));
					// 開啟計算檔案長度並存入attachSize
					file_size = (int) file.length();
					file_name = file.getName();
					postFile = postFile + "&file_name" + index + "_0=" + file_name;
					duration = 0;
					FileUtils oname = new FileUtils();
					String outFileName = oname.getTargetFileName(file_path.get(index), split_seq, index);
					ContentValues values = new ContentValues();
					values.put(UserSchema._FILEPATH, outFileName);
					values.put(UserSchema._FILERECORD, index + "_0");
					values.put(UserSchema._FILECHECK, 0);
					values.put(UserSchema._FILENAME, file_name);
					getContentResolver().insert(Uri.parse("content://tab.list.d2d/temp_file"), values);
					dialog.dismiss();
					state = "write";
					new SendandAttach().execute();
				}
				up_file_cursor.moveToNext();
			}
		} else {
			Toast.makeText(getApplicationContext(), "請至少選擇一份檔案", Toast.LENGTH_LONG).show();
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

		if (receiver.equals("")) {
			Toast.makeText(getApplicationContext(), "收件者不可為空白", Toast.LENGTH_LONG).show();
		} else {
			fileupload("write");
		}

	}

	private class SendandAttach extends AsyncTask<Void, Void, String> // implement
	// thread
	{
		public int id_this;
		public String where;
		public String[] aliveIp;
		ProgressDialog senddialog = null;
		public String path;

		// Asyntask 前置作業
		@Override
		protected void onPreExecute() {
			// 開啟資料傳送dialog
			senddialog = ProgressDialog.show(writepage.this, "請稍候", "資料上傳中", true);
			senddialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			String er = "no";
			String sqliteString;
			System.out.println("state會錯"+state);
			state="write";
			if (state.equals("write")) {
				/// ex : storage/emulated/0/DCIM/100ANDRO/MOV_0259.mp4
				File file = new File(attachment);
				String filename = file.getName();
				filetype = filename.split("\\.");

				// 2013/4/14 4$刪掉filecount與post
				// 2013/8/10/ 補上filecnt並把前面;改成&
				System.out.println("postFile==" + postFile);
				// 先設定request字串
				// 2013/11/08 豆豆 修改上傳資訊metadata
				Submit.setrequestString("subject=" + title + "&content=" + content + "&receiver=" + receiver + "&filecnt=" + file_amount + "&duration=" + duration + "&filename=" + filename + "&filetype=" + filetype[1] + "&filepath=" + attachment + "&length=" + file_size + "&urgent=" + urgent + postFile);
				// 2013/4/3 4$新增cookie(alive_cookie)
				//這裡會有錯 FILE先暫時
				String resp = Submit.submit1(login.submit1.Login.latest_cookie,file);

				// 這邊是用來檢查收件者是否存在於server中，若不存在則取消這次的上傳
				if (!(att_parameter.chechsuccess(resp))) {
					er = "yes";
				} else {
					Cursor up_filetoken_cursor = getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_file"), new String[] { UserSchema._ID }, "filename='" + filename + "'and messagetoken is null", null, null);
					ContentValues values = new ContentValues();
					if (up_filetoken_cursor.getCount() > 0) {
						up_filetoken_cursor.moveToFirst();
						values.put(UserSchema._MESSAGETOKEN, resp.replace("ret=0&token=", ""));
						for (int i = 0; i < up_filetoken_cursor.getCount(); i++) {
							id_this = Integer.parseInt(up_filetoken_cursor.getString(0));
							where = UserSchema._ID + " = " + id_this;
							getContentResolver().update(Uri.parse("content://tab.list.d2d/temp_file"), values, where, null);
							up_filetoken_cursor.moveToNext();
						}
					}
					up_filetoken_cursor.close();

					boolean[] checktype = new boolean[att_parameter.filetype];
					checktype = att_parameter.checktype(file.getName());

					String response = retreive.conversation(resp.replace("ret=0&token=", ""), "reply", urgent);
					boolean result = att_parameter.chechsuccess(response);
					if (result) {
						if (checktype[att_parameter.video]) {
						
							String[] temp = response.split("&");
							path = temp[1].replace("path=", "");
							file_path.add(path);
							if (!(file_path.isEmpty())) {
								// 如果附加檔案部為空(也就是有file path),則進行檔案的篩選
								finishsubmit = false;
								for (int x = 0; x < file_path.size(); x++) { // 取得目前要上傳的檔案路徑
									attachment = file_path.get(x);

									sqliteString = file.getName().replace("'", "''");

									while (!finishsubmit) {
										// 檢查在DB的table中，是否有未上傳的檔案，未傳的檔案filecheck是零，上傳好的是一。
										Cursor check_finish_cursor = getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_file"), new String[] { UserSchema._FILEPATH }, "filecheck='0' and messagetoken='" + resp.replace("ret=0&token=", "") + "'", null, null);
										if (check_finish_cursor.getCount() > 0) {
											check_finish_cursor.moveToFirst();
											CharSequence[] path = new CharSequence[check_finish_cursor.getCount()];
											// 將未傳完的檔案，使用陣列儲存起來
											for (int i = 0; i < check_finish_cursor.getCount(); i++) {
												path[i] = check_finish_cursor.getString(0);
												check_finish_cursor.moveToNext();
											}
											// 進行副檔名篩選

											// Algocount是表示目前要上傳幾塊，path中就會根據Algocount來決定要讀出幾筆資料
											// 做第一塊的目的在於，測試目前的網路狀態為何
											Algocount = 1;
											// 先設定檔案路徑給submit
											Submit.setattachfile(attachment);
											// 傳入要執行的次數以及要執行的spilt path[]
											Submit.setsource(Algocount, path);
											System.out.println("hhhhhhhhhhhhhhhh"+resp);
											// 上傳檔案
											Submit.submit_file(resp.replace("ret=0&token=", ""), x);
											// 取回response,用來待會做nack的判斷
											submit_file_readline = Submit.getsubmit_file_readline();
											check_finish_cursor.close();

											String[] Form = { UserSchema._ID };
											// 先將剛才上傳的檔案，把filecheck set成1
											for (int a = 0; a < Algocount; a++) {
												Cursor up_tempfile = getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_file"), Form, "messagetoken='" + resp.replace("ret=0&token=", "") + "' and filecheck='0'", null, null);
												if (up_tempfile.getCount() > 0) {
													up_tempfile.moveToFirst();
													values = new ContentValues();
													values.put(UserSchema._FILECHECK, 1);
													int id_this = Integer.parseInt(up_tempfile.getString(0));
													String where = UserSchema._ID + " = " + id_this;
													getContentResolver().update(Uri.parse("content://tab.list.d2d/temp_file"), values, where, null);
												}
												up_tempfile.close();
												// 第一塊傳完
												finishsubmit = true;
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
													Cursor up_nack_cursor = getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_file"), Form, "filerecord='" + nack_name[i] + "'and filename='" + sqliteString + "'", null, null);
													if (up_nack_cursor.getCount() > 0) {
														up_nack_cursor.moveToFirst();
														values = new ContentValues();
														values.put(UserSchema._FILECHECK, 0);
														int id_this = Integer.parseInt(up_nack_cursor.getString(0));
														String where = UserSchema._ID + " = " + id_this;
														getContentResolver().update(Uri.parse("content://tab.list.d2d/temp_file"), values, where, null);
													}
													up_nack_cursor.close();
												}
											}// matchernack end
										}// check_finish_curso end
									}// while end
								}// for end
							} // check_filepath!=null end
							file_path.clear();
							response = "true";
						}// check video end
					else if (checktype[att_parameter.photo]||checktype[att_parameter.music]) {
						Cursor check_cursor = getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_file"), new String[] { UserSchema._FILEPATH }, "filecheck='0' and messagetoken='" + resp.replace("ret=0&token=", "") + "'", null, null);
						CharSequence[] path = new CharSequence[0];
						if(check_cursor.getCount()>0){
							CharSequence[] path_MP = new CharSequence[check_cursor.getCount()];
							check_cursor.moveToFirst();
							path_MP[0]=check_cursor.getString(0);
							Algocount = 1;
							// 先設定檔案路徑給submit
							Submit.setattachfile(attachment);
							// 傳入要執行的次數以及要執行的spilt path[]
							Submit.setsource(Algocount,path_MP);
							// 上傳檔案
							Submit.submit_file(resp.replace("ret=0&token=", ""), 0);
							// 讀回response
							submit_file_readline = Submit.getsubmit_file_readline();
						}

								
							
						}//checktype music photo end
				}//result end
			}
			}
			return er;
		}

		protected void onPostExecute(String er) {
			fileContentProvider test = new fileContentProvider();
			test.del_table(Uri.parse("content://tab.list.d2d/file_choice"));
			previewImg.setVisibility(View.INVISIBLE);
			tvName.setVisibility(View.INVISIBLE);
			delete.setVisibility(View.INVISIBLE);
			senddialog.dismiss();
			if (er.equals("yes")) {
				// 若er成立，則表示輸入的收件者不存在
				AlertDialog.Builder Dialog = new AlertDialog.Builder(writepage.this); // Dialog
				Dialog.setTitle("");
				Dialog.setMessage("傳送失敗，查無此收件者");
				Dialog.setIcon(android.R.drawable.ic_dialog_info);
				Dialog.setNeutralButton("確定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						etR.setText("");
						etT.setText("");
						etC.setText("");
					}
				});
				Dialog.show();
			} else {

				// 傳送成功後顯示成功視窗
				AlertDialog.Builder Dialog = new AlertDialog.Builder(writepage.this); // Dialog
				Dialog.setTitle("");
				Dialog.setMessage("傳送成功");
				Dialog.setIcon(android.R.drawable.ic_dialog_info);
				Dialog.setNeutralButton("確定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						etR.setText("");
						etT.setText("");
						etC.setText("");
					}
				});
				Dialog.show();
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
		intent.setClass(writepage.this, f_tab.class);
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
			Toast.makeText(this, "not match file", 4000).show();
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

	/*
	 * 這邊會呼叫FFMPEG來做切割
	 */
	public void ffmpeg(String file_path, int start, int duration) {
		File file = new File(file_path);
		String inputFileName = file_path;
		ffmpeg_path = file_path;
		String outFileName = oname.getTargetFileName(file_path, split_seq, index);
		split_seq++;
		ContentValues values = new ContentValues();
		values.put(UserSchema._FILEPATH, outFileName);
		values.put(UserSchema._FILERECORD, oname.getindex());
		values.put(UserSchema._FILECHECK, 0);
		values.put(UserSchema._FILENAME, file.getName());

		getContentResolver().insert(Uri.parse("content://tab.list.d2d/temp_file"), values);
		File ch_file = new File(outFileName);
		// 先檢查該影片在過去是否有被切割過，如果這一塊被切割過就跳過不切
		if (ch_file.exists()) {
			if (split_seq < (total_time / duration)) {
				ffmpeg(file_path, split_seq * duration, duration);

			} else {
				split_seq = 0;
				dialog.dismiss();
				new SendandAttach().execute();

			}
		} else {
			// 傳入四個參數給FFMPEG
			Bundle bundle = new Bundle();
			bundle.putString("inputFileName", inputFileName);
			bundle.putString("outFileName", outFileName);
			bundle.putInt("start", start);
			bundle.putInt("duration", duration);
			Intent intent = videoTrimmingServiceIntent();
			intent.putExtras(bundle);
			startService(intent);
		}

	}

	public Intent videoTrimmingServiceIntent() {
		System.out.println("TEST Intent");
		return new Intent(this, VideoTrimmingService.class);
	}

	// 這邊是接收來自FFMPEG做完時的廣播
	public class MyBroadcastReceiver extends BroadcastReceiver {
		int starttime;

		@Override
		public void onReceive(Context context, Intent intent) {
			String result = intent.getStringExtra(VideoTrimmingService.EXTRA_KEY_OUT);
			// 如果目前切割的INDEX小於所要切割的總size，則繼續執行
			// 例如檔案為50M,每10M一切，總size為5塊
			if (split_seq < (total_time / duration)) {
				ffmpeg(ffmpeg_path, (split_seq * duration)-1, duration);
			}

			else {
				split_seq = 0;
				dialog.dismiss();
				new SendandAttach().execute();
				System.out.println("will finish");

			}

		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// un-register BroadcastReceiver
		unregisterReceiver(myBroadcastReceiver);

	}

	// 當從attachment回來時，顯示剛剛所選擇的檔案在writepage上。
	protected void onResume() {

		super.onResume();
		Cursor ch_tmepfile = getContentResolver().query(uri, form, null, null, null);
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
    
}
