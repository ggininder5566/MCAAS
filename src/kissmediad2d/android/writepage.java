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
	 * �o��O�Ψӧ@�k�q�����ʧ@�A�]�O���KM���D�n�֤ߤ��@�A�]���ݭn���ɮת�����
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
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy�~MM��dd��HH:mm:ss");

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
		// �o�@�q�O���s���Ϊ��A��ffmpeg�����ɷ|�z�L�s�����覡�A�i�Dwritepage���U�@�Ӥ���
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(VideoTrimmingService.ACTION_MyIntentService);
		intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

		registerReceiver(myBroadcastReceiver, intentFilter);
		getIntent().setData(Uri.parse("content://tab.list.d2d/file_choice"));
		final Uri uri_test = getIntent().getData();
		uri = uri_test;
	}

	/*
	 * �o��O��ť�b�o��layout�W��button
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
//		case R.id.sendmail:
//			Cursor up_file_cursor = getContentResolver().query(uri, form, null, null, null);
//			if (up_file_cursor.getCount() > 0) {
//				// �۩w�q���禡�A�����}�l�����ɮ�>�W�� ������ʧ@���}�Y
//				AlertDialog.Builder Dialog = new AlertDialog.Builder(this);
//				Dialog.setTitle("�W���ɮ�");
//				Dialog.setMessage("�п�ܳq���覡");
//				Dialog.setIcon(android.R.drawable.ic_dialog_info);
//				Dialog.setNegativeButton("�ϥ�²�T�q��", new DialogInterface.OnClickListener() { // �������ɮ�
//							@Override
//							public void onClick(DialogInterface dialog, int which) {
//								urgent = 1;
//								sendmail();
//							}
//						});
//				Dialog.setPositiveButton("�ϥκ����q��", new DialogInterface.OnClickListener() { // �����ɮ�
//							@Override
//							public void onClick(DialogInterface dialog, int which) {
//								urgent = 0;
//								sendmail();
//							}
//						});
//				Dialog.show();
//			} else {
//				Toast.makeText(getApplicationContext(), "�Цܤֿ�ܤ@���ɮ�", Toast.LENGTH_LONG).show();
//			}
//
//			break;
//		case R.id.attachbutton:
//			// ��ܪ��[���ɮ�
//			attachbutton();
//			break;
		case R.id.previewimg:
			viewfile();
			break;
		case R.id.delete:
			// �R���ҿ�ܪ��ɮסA�P�ɧ�T�����ð_��
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
		// Asyntask �e�m�@�~
		@Override
		protected void onPreExecute() {
			// �}�Ҹ�ƶǰedialog
			senddialog = ProgressDialog.show(writepage.this, "�еy��", "��ƤW�Ǥ�", true);
			senddialog.show();
		}		
		@Override
		protected String doInBackground(Void... arg0) {
			System.out.println("���ѥ���");
			Cursor up_file_cursor = getContentResolver().query(uri, form, null, null, null);
			if (up_file_cursor.getCount() > 0) {
				up_file_cursor.moveToFirst();
				ff =up_file_cursor.getString(0);
			}
			up_file_cursor.close();
			File file = new File(ff);
 			System.out.printf("�ǰe�ɮ�: %s%n", file.getName());
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
	  				//���ճs�u�A���\�N�|���~�]
	  				socket.connect(sc_add,2000);
	  				System.out.println("connsess=");
					//Ū����ơAŪ��~�|���U
	  				inputmasge = new BufferedInputStream(socket.getInputStream());
	  				dataOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
						
	  				System.out.println("start");
  					//Ū�X���
					String mesage = get_msg();
					System.out.println(finish);
					finish=false;
	  				while(!finish){
	  					System.out.println(mesage);
	  					if(mesage.equals("sub_ok")){
	  						mesage="";
		  		  	        //��X�ɦW�����
	  						System.out.println("sub_ok11111");
//	  						DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
//	  						dataOut.writeUTF(file.getName());
	  						send_msg(file.getName());
	  						String gg =get_msg();
	  						System.out.println(gg);
	  						//dataOut.writeUTF("/storage/emulated/0/mnt/sdcard/video.mp4");
	  		  	           // dataOut.flush();
		  		  	        System.out.println("sub_ok1");
	  		  	            //���o�^�аT��
		  		  	        //String mesage1 = new DataInputStream(new BufferedInputStream(socket.getInputStream())).readUTF();
		  		  	       // System.out.println(mesage1);
	  		  	            //�}�l���ɮסA��Ū���ڭ̭n�Ǫ��ɮ�
		  		  	        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file)); 
		  		  	    DataOutputStream dataOut1 = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
							
		  		  	        int readin; 
	  		  	            while((readin = inputStream.read()) != -1) { 
	  		  	                //�L�g�J���X 
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
	  	 
	  	            System.out.println("\n�ɮ׶ǰe�����I"); 
	  			}catch (UnknownHostException e) {
	  				
	  		    } catch (SocketException e) {


	  		    } catch(IOException e) {

	  		    	System.out.println("\n���ѡI"); 
	  		    }
	  		

			}
			return er;
			}
		protected void onPostExecute(String er) {
			fileContentProvider test = new fileContentProvider();
			test.del_table(Uri.parse("content://tab.list.d2d/file_choice"));
			senddialog.dismiss();
			if (er.equals("yes")) {
				// �Yer���ߡA�h��ܿ�J������̤��s�b
				AlertDialog.Builder Dialog = new AlertDialog.Builder(writepage.this); // Dialog
				Dialog.setTitle("");
				Dialog.setMessage("�ǰe���ѡA�d�L�������");
				Dialog.setIcon(android.R.drawable.ic_dialog_info);
				Dialog.setNeutralButton("�T�w", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						etR.setText("");
						etT.setText("");
						etC.setText("");
					}
				});
				Dialog.show();
			} else {

				// �ǰe���\����ܦ��\����
				AlertDialog.Builder Dialog = new AlertDialog.Builder(writepage.this); // Dialog
				Dialog.setTitle("");
				Dialog.setMessage("�ǰe���\");
				Dialog.setIcon(android.R.drawable.ic_dialog_info);
				Dialog.setNeutralButton("�T�w", new DialogInterface.OnClickListener() {
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
		// �ˬd�ϥΪ̦��S����ܭn�W�Ǫ��ɮ�,��ܦn���ɮ׷|�g�Jfile_choice��table��
		file_path = new ArrayList<String>();
		Cursor up_file_cursor = getContentResolver().query(uri, form, null, null, null);
		if (up_file_cursor.getCount() > 0) {
			dialog = ProgressDialog.show(writepage.this, "�еy��", "��ƳB�z��", true);
			dialog.show();
			up_file_cursor.moveToFirst();
			file_path.add(up_file_cursor.getString(0));

			postFile = new String();
			for (index = 0; index < up_file_cursor.getCount(); index++) {
				boolean[] checktype = new boolean[att_parameter.filetype];
				checktype = att_parameter.checktype(attachment);
				// �p�G��ܪ��O�v���A�h�p��L���v�����סB�j�p�A�ت��O�n�ǵ�ffmpeg�ϥ�
				if (checktype[att_parameter.video]) {
					total_time = up_file_cursor.getFloat(1) / 1000;
					file_size = Integer.valueOf(up_file_cursor.getString(2));
					file_name = up_file_cursor.getString(3);
					// �p��1byte���X��
					ONEMB = total_time / file_size; // �p��1MB�j�����X��
					System.out.println("ONEMB==" + ONEMB);
					// �H���׬���ǡA���]20MB���@���A�D�X20MB�ݴX��,Math.round���|�ˤ��J����
					duration = Math.round(ONEMB * TWOMB);
					System.out.println("duration==" + duration);
					// �H��Ƭ���ǡA���]20MB��9.8��A�D�X�ɮר̷Ӭ�Ƥ�,�ݭn���X��,Math.ceil���D�X�j�󦹼ƪ��̤p���,ex.�j��12.8���̤p��Ƭ�13
					file_amount = (int) Math.ceil((double) total_time / duration);
					System.out.println("filecnt==" + file_amount);
					String[] tempfilename = new String[file_amount];
					for (int i = 0; i < file_amount; i++) {
						tempfilename[i] = "file_name" + index + "_" + String.valueOf(i) + "=" + file_name;
						postFile = postFile + "&" + tempfilename[i];
					}
					/**
					 * filecontenprovider�O�۩w�q����Ʈw�A�����O���Ҧ�table����T�A
					 * �o�̪�del_table�O�۩w�q����k�A��R���ҿ�ܪ�table �R��temp_file�o�itable���γ~�b��
					 * �A�קK�O�d�W�@�����W�ǲM��X�{�b�o�@���s���M�椺
					 */
					ffmpeg(file_path.get(index), 0, duration); // ����
					// �ѼơG�ɦW�B�_�l��m�B���ήɶ��B��X�ɮ�
				} else if (checktype[att_parameter.music] || checktype[att_parameter.photo]) {
					// ���v���~�A��L�����Τ�
					File file = new File(file_path.get(index));
					// �}�ҭp���ɮת��רæs�JattachSize
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
			Toast.makeText(getApplicationContext(), "�Цܤֿ�ܤ@���ɮ�", Toast.LENGTH_LONG).show();
		}

		state = "write";
		up_file_cursor.close();
		index = 0;
		// �I�s�۩w�q���禡

	}

	public void sendmail() {
		receiver = etR.getText().toString();
		title = etT.getText().toString();
		content = etC.getText().toString();

		if (receiver.equals("")) {
			Toast.makeText(getApplicationContext(), "����̤��i���ť�", Toast.LENGTH_LONG).show();
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

		// Asyntask �e�m�@�~
		@Override
		protected void onPreExecute() {
			// �}�Ҹ�ƶǰedialog
			senddialog = ProgressDialog.show(writepage.this, "�еy��", "��ƤW�Ǥ�", true);
			senddialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			String er = "no";
			String sqliteString;
			System.out.println("state�|��"+state);
			state="write";
			if (state.equals("write")) {
				/// ex : storage/emulated/0/DCIM/100ANDRO/MOV_0259.mp4
				File file = new File(attachment);
				String filename = file.getName();
				filetype = filename.split("\\.");

				// 2013/4/14 4$�R��filecount�Ppost
				// 2013/8/10/ �ɤWfilecnt�ç�e��;�令&
				System.out.println("postFile==" + postFile);
				// ���]�wrequest�r��
				// 2013/11/08 ���� �ק�W�Ǹ�Tmetadata
				Submit.setrequestString("subject=" + title + "&content=" + content + "&receiver=" + receiver + "&filecnt=" + file_amount + "&duration=" + duration + "&filename=" + filename + "&filetype=" + filetype[1] + "&filepath=" + attachment + "&length=" + file_size + "&urgent=" + urgent + postFile);
				// 2013/4/3 4$�s�Wcookie(alive_cookie)
				//�o�̷|���� FILE���Ȯ�
				String resp = Submit.submit1(login.submit1.Login.latest_cookie,file);

				// �o��O�Ψ��ˬd����̬O�_�s�b��server���A�Y���s�b�h�����o�����W��
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
								// �p�G���[�ɮ׳�����(�]�N�O��file path),�h�i���ɮת��z��
								finishsubmit = false;
								for (int x = 0; x < file_path.size(); x++) { // ���o�ثe�n�W�Ǫ��ɮ׸��|
									attachment = file_path.get(x);

									sqliteString = file.getName().replace("'", "''");

									while (!finishsubmit) {
										// �ˬd�bDB��table���A�O�_�����W�Ǫ��ɮסA���Ǫ��ɮ�filecheck�O�s�A�W�Ǧn���O�@�C
										Cursor check_finish_cursor = getContentResolver().query(Uri.parse("content://tab.list.d2d/temp_file"), new String[] { UserSchema._FILEPATH }, "filecheck='0' and messagetoken='" + resp.replace("ret=0&token=", "") + "'", null, null);
										if (check_finish_cursor.getCount() > 0) {
											check_finish_cursor.moveToFirst();
											CharSequence[] path = new CharSequence[check_finish_cursor.getCount()];
											// �N���ǧ����ɮסA�ϥΰ}�C�x�s�_��
											for (int i = 0; i < check_finish_cursor.getCount(); i++) {
												path[i] = check_finish_cursor.getString(0);
												check_finish_cursor.moveToNext();
											}
											// �i����ɦW�z��

											// Algocount�O��ܥثe�n�W�ǴX���Apath���N�|�ھ�Algocount�ӨM�w�nŪ�X�X�����
											// ���Ĥ@�����ت��b��A���եثe���������A����
											Algocount = 1;
											// ���]�w�ɮ׸��|��submit
											Submit.setattachfile(attachment);
											// �ǤJ�n���檺���ƥH�έn���檺spilt path[]
											Submit.setsource(Algocount, path);
											System.out.println("hhhhhhhhhhhhhhhh"+resp);
											// �W���ɮ�
											Submit.submit_file(resp.replace("ret=0&token=", ""), x);
											// ���^response,�Ψӫݷ|��nack���P�_
											submit_file_readline = Submit.getsubmit_file_readline();
											check_finish_cursor.close();

											String[] Form = { UserSchema._ID };
											// ���N��~�W�Ǫ��ɮסA��filecheck set��1
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
												// �Ĥ@���ǧ�
												finishsubmit = true;
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
							// ���]�w�ɮ׸��|��submit
							Submit.setattachfile(attachment);
							// �ǤJ�n���檺���ƥH�έn���檺spilt path[]
							Submit.setsource(Algocount,path_MP);
							// �W���ɮ�
							Submit.submit_file(resp.replace("ret=0&token=", ""), 0);
							// Ū�^response
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
				// �Yer���ߡA�h��ܿ�J������̤��s�b
				AlertDialog.Builder Dialog = new AlertDialog.Builder(writepage.this); // Dialog
				Dialog.setTitle("");
				Dialog.setMessage("�ǰe���ѡA�d�L�������");
				Dialog.setIcon(android.R.drawable.ic_dialog_info);
				Dialog.setNeutralButton("�T�w", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						etR.setText("");
						etT.setText("");
						etC.setText("");
					}
				});
				Dialog.show();
			} else {

				// �ǰe���\����ܦ��\����
				AlertDialog.Builder Dialog = new AlertDialog.Builder(writepage.this); // Dialog
				Dialog.setTitle("");
				Dialog.setMessage("�ǰe���\");
				Dialog.setIcon(android.R.drawable.ic_dialog_info);
				Dialog.setNeutralButton("�T�w", new DialogInterface.OnClickListener() {
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

	// �����I�s����ɮת�����
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
	 * �o��|�I�sFFMPEG�Ӱ�����
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
		// ���ˬd�Ӽv���b�L�h�O�_���Q���ιL�A�p�G�o�@���Q���ιL�N���L����
		if (ch_file.exists()) {
			if (split_seq < (total_time / duration)) {
				ffmpeg(file_path, split_seq * duration, duration);

			} else {
				split_seq = 0;
				dialog.dismiss();
				new SendandAttach().execute();

			}
		} else {
			// �ǤJ�|�ӰѼƵ�FFMPEG
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

	// �o��O�����Ӧ�FFMPEG�����ɪ��s��
	public class MyBroadcastReceiver extends BroadcastReceiver {
		int starttime;

		@Override
		public void onReceive(Context context, Intent intent) {
			String result = intent.getStringExtra(VideoTrimmingService.EXTRA_KEY_OUT);
			// �p�G�ثe���Ϊ�INDEX�p��ҭn���Ϊ��`size�A�h�~�����
			// �Ҧp�ɮ׬�50M,�C10M�@���A�`size��5��
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

	// ��qattachment�^�ӮɡA��ܭ��ҿ�ܪ��ɮצbwritepage�W�C
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
