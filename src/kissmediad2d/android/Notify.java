package kissmediad2d.android;

import java.io.File;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import database.storage.messageProcess;

import kissmediad2d.android.R;

import login.submit1.Alive;
import login.submit1.Login;
import login.submit1.locupdate;
import login.submit1.retrieve;
import login.submit1.submit;
import tab.list.att_parameter;
import tab.list.fileContentProvider.UserSchema;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TimePicker;
import android.widget.Toast;

class notiinfo {

    public String cookie = new String();
    private String sender, title, token, filesize, date, msg;

    notiinfo() {
	filesize = new String();
	sender = new String();
	title = new String();
	token = new String();
	date = new String();
	msg = new String();
    }

    void setDate(String arg) {
	date = arg;
    }

    void setFilesize(String arg) {
	filesize = arg;
    }

    void setSender(String arg) {
	sender = arg;
    }

    void setContent(String arg) {
	title = arg;
    }

    void setToken(String arg) {
	token = arg;
    }

    void setmsg(String arg) {
	msg = arg;
    }

    String getDate() {
	return date;
    }

    String getSender() {
	return sender;
    }

    String getContent() {
	return title;
    }

    String getToken() {
	return token;
    }

    String getmsg() {
	return msg;
    }

    String getFilesize() {
	return filesize;
    }
}

/**
 * Notification �O���o��ذT���A�@�O�q�����ɮץi�U���A�G�O�q���бN�ɮפW�� �n�D�W�Ǫ��ɮ׷|�b�k�W���X�{reply���r��
 * �I����|��Ū�����e�A�M��|����recivelist
 */
public class Notify extends ListActivity implements OnItemClickListener {

    public String filesize = null, getSender = new String(), subject = null, content = null, messagetoken = null, msg = null;
    public String attachment = null;
    public String sms = null;
    public boolean finishsubmit = false, first = true;
    public String submit_file_readline;
    public int listid = -1;
    public String mod = new String();
    String[] Form = { UserSchema._ID, UserSchema._SENDER, UserSchema._CONTENT, UserSchema._MESSAGETOKEN, UserSchema._FILESIZE, UserSchema._DATE };
    String[] reply = { UserSchema._ID, UserSchema._SENDER, UserSchema._FILENAME, UserSchema._MESSAGETOKEN, UserSchema._DATE, UserSchema._MSG };
    String[] d2d = { UserSchema._ID, UserSchema._SENDER, UserSchema._D2D };
    String H;
    String M;
    private int Algocount, urgent = 0;
	private TimePicker timePicker;
    private ArrayList<String> file_path = new ArrayList<String>();
    private boolean retrieving = false;
	private NotificationManager gNotMgr = null;
    List<notiinfo> getinfo = new ArrayList<notiinfo>();
    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    List<String> fileList = new ArrayList<String>();

    SimpleAdapter adapter;
    ProgressDialog pdialog = null;
    submit Submit = new submit();
    retrieve retreive = new retrieve();
	locupdate locup =new locupdate();

    @Override
    public void onCreate(Bundle savedInstanceState) {
	// requestWindowFeature(Window.FEATURE_NO_TITLE); //set no title
	// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
	// //set fullscreen
	super.onCreate(savedInstanceState);
	setTitle("Notification");

	/*
	 * SimpleAdapter�ΨӰ�data�� mapping,
	 * SimpleAdapter���ĤG������U�W��q�N�O���odata�ӷ��A�^�ǭȬO�@��list�ĤT�����O�n�e�{�b���@��layout�W
	 * �ĥ|����쪺����r�O���o�ĤG�����getdata������T�Ĥ������O�N�ĥ|�������o���ȡA��J�blayout�W������
	 */
	adapter = new SimpleAdapter(this, getData(), R.layout.notification, new String[] { "notititle", "notiinfo", "notiimg", "notidate", "notmsg" }, new int[] { R.id.notititle, R.id.notiinfo, R.id.notiimg, R.id.notidate, R.id.msg });
	setListAdapter(adapter);
	getListView().setOnItemClickListener(this);
	ListView listView = getListView();
	listView.setBackgroundResource(R.drawable.background);
	listView.setCacheColorHint(Color.TRANSPARENT);

	/*
	 * TestForm�O��ӨM�w�ݷ|�n�q��Ʈw���������ƥX�� UserSchema._ID
	 * �o�ǬO�w�q�bfileContentProvider.java�� �o��O�Ψ�""��l��""�Ҧ��q�����ϥΪ��A(�@�h�q���N��@���ɮ�)
	 */

    }

    protected void onResume() {// ���B��,�I���U���� �R���T��
	super.onResume();
	list.clear();
	list = getData();
	adapter.notifyDataSetChanged();
    }

    /*
     * �o�̬Oadapter�һݭn�Ψ쪺��ƨӷ��AArrayList�ϥΪ��O key valuse���覡�x�s
     */
    private List<Map<String, Object>> getData() {
	getinfo.clear();
	notiinfo tempinfo = new notiinfo();

	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); // �Ndate
	String tempdate;
	// uri�O�s������DB���@�Ӥ覡�A�o�̭n�h��²�T��Ʈw��������
	Uri SMS_INBOX = Uri.parse("content://sms/inbox");
	Cursor new_sms_cursor = getContentResolver().query(SMS_INBOX, new String[] { "address", "date", "body" }, "address='0930345396' or address='+886930345396'", null, null);

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
		MsgSave=null;
		// �۩w�q���禡
		

		new_sms_cursor.moveToNext();
	    }// end for
	}// end if
	new_sms_cursor.close();

	// �]���Q�n�D�}��d2d,�ҥH���ˬdd2d�O�_��1
	Cursor d2d_cursor = getContentResolver().query(Uri.parse("content://tab.list.d2d/user_reply"), reply, "msg = 'd2d' and d2d='1'", null, null);
	if (d2d_cursor.getCount() > 0) {
		d2d_cursor.moveToFirst();
	    /*
	     * �ݦ��X��²�T��Ū,�N�إߴX���q����getinfo�̭��A�Ҧp���|����Ū���q���A�N�إߥ|��²�T��������T��getinfo�̭�
	     */
	    for (int x = 0; x < d2d_cursor.getCount(); x++) {
		// �N²�T�s�J�۩w�q������(tempinfo)
		tempinfo.setSender(d2d_cursor.getString(1));
		tempinfo.setContent("�n�D�z�}��d2d_server");
		tempinfo.setToken(d2d_cursor.getString(3));
		tempinfo.setDate(d2d_cursor.getString(4));
		tempinfo.setmsg(d2d_cursor.getString(5));
		// �N�ۭq����s�J��getinfo
		getinfo.add(tempinfo);
		tempinfo = new notiinfo();
		d2d_cursor.moveToNext();
	    }

	}
	d2d_cursor.close();

	// �ѩ�w�gŪ�Lretrievable���H��,�ҥH������tittle���W��,�G��tittle ��null��data,�Y��ܼ���Ū�H��
	Cursor user_cursor = getContentResolver().query(Uri.parse("content://tab.list.d2d/user_data"), Form, "read is null and content is not null", null, null);
	if (user_cursor.getCount() > 0) {
	    user_cursor.moveToFirst();
	    /*
	     * �ݦ��X��²�T��Ū,�N�إߴX���q����getinfo�̭��A�Ҧp���|����Ū���q���A�N�إߥ|��²�T��������T��getinfo�̭�
	     */
	    for (int x = 0; x < user_cursor.getCount(); x++) {
		// �N²�T�s�J�۩w�q������(tempinfo)
		tempinfo.setSender(user_cursor.getString(1));
		tempinfo.setContent(user_cursor.getString(2));
		tempinfo.setToken(user_cursor.getString(3));
		tempinfo.setFilesize(user_cursor.getString(4));
		tempinfo.setDate(user_cursor.getString(5));
		// �N�ۭq����s�J��getinfo
		getinfo.add(tempinfo);
		tempinfo = new notiinfo();
		user_cursor.moveToNext();
	    }
	}
	user_cursor.close();

	/*
	 * �p�P�W�z���@�k�A���O�o��~�O�u���n�� key value���Ʊ��Avalue�N�O�Ӧ۩�W�z��getinfo
	 * �Ҧp²�TA��notititle���O����?�qgetinfo�����X�� ²�TA��notiinfo�O����?�@�ˤ]�O�qgetinfo�����X��
	 */
	Map<String, Object> map = new HashMap<String, Object>();
	for (int i = 0; i < getinfo.size(); i++) { // put info array into
	    // list
	    map = new HashMap<String, Object>();
	    // �N�C��²�T����T�s�J��۩w�q��adapter��������
	    map.put("notititle", getinfo.get(i).getSender());
	    map.put("notiinfo", getinfo.get(i).getContent());
	    map.put("notidate", getinfo.get(i).getDate());
	    map.put("notiimg", R.drawable.message);
	    if (getinfo.get(i).getmsg().equals("reply")) {
		map.put("notmsg", getinfo.get(i).getmsg());
	    }
	    list.add(map);
	}
	return list;
    }

    // ����U�q���ɡA�n�����Ʊ�
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long position) {

	String aa;
	Cursor change_state = getContentResolver().query(Uri.parse("content://tab.list.d2d/user_data"), null, "userstatus='download'", null, null);
	// �ˬd�ثe���S���ɮץ��b�U��
	if (change_state.getCount() > 0) {
	    Toast.makeText(getApplicationContext(), "�ثe�|���ɮפU�����A�y��A��", Toast.LENGTH_LONG).show();
	    change_state.close();
	} else {
	    change_state.close();
	    listid = (int) position;
	    // �ˬd�����ɮ׬O�i�U�� �٬O �n�D�W���ɮ�
	    if (getinfo.get(listid).getmsg().equals("reply")) {

	    }else if (getinfo.get(listid).getmsg().equals("d2d")) {

	    }
	    else {

		Cursor change_read = getContentResolver().query(Uri.parse("content://tab.list.d2d/user_data"), Form, "messagetoken='" + getinfo.get(listid).getToken() + "'", null, null);
		if (change_read.getCount() > 0) {
		    change_read.moveToFirst();
		    for (int i = 0; i < change_read.getCount(); i++) {
			int id_this = 0;
			id_this = Integer.valueOf(change_read.getString(0));
			ContentValues values = new ContentValues();
			values.put(UserSchema._READ, 1);
			String where = UserSchema._ID + " = " + id_this;
			getContentResolver().update(Uri.parse("content://tab.list.d2d/user_data"), values, where, null);
			change_read.moveToNext();
		    }
		}
		change_read.close();
		// �o�̬Oretrievable
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		// �ǤJ�H��H
		bundle.putString("contact", getinfo.get(listid).getSender());
		intent.putExtras(bundle);
		// intent.setClass(contactlist.this, dialogViewCtrl.class);
		intent.setClass(Notify.this, receivelist.class);
		startActivity(intent);
	    }

	}// else end

    }

    // �إ߼���ǦC
    public static String changeToSeries(String arg, int no) {//
	String series = new String();
	String temp[] = arg.split("-_");
	String temp1[] = temp[0].split("_-");

	for (int i = 0; i < no; i++) {
	    int head = Integer.valueOf(temp1[0]) + i;
	    series = series + "&" + head + "_-" + temp1[1] + "-_" + i + temp[1].substring(1, temp[1].length());

	}
	return series;
    }

   
}