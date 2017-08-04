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
 * Notification 是取得兩種訊息，一是通知有檔案可下載，二是通知請將檔案上傳 要求上傳的檔案會在右上角出現reply的字樣
 * 點擊後會先讀取內容，然後會跳到recivelist
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
	 * SimpleAdapter用來做data的 mapping,
	 * SimpleAdapter的第二個欄位顧名思義就是取得data來源，回傳值是一個list第三個欄位是要呈現在哪一個layout上
	 * 第四個欄位的關鍵字是取得第二個欄位getdata內的資訊第五個欄位是將第四個欄位取得的值，放入在layout上的元件
	 */
	adapter = new SimpleAdapter(this, getData(), R.layout.notification, new String[] { "notititle", "notiinfo", "notiimg", "notidate", "notmsg" }, new int[] { R.id.notititle, R.id.notiinfo, R.id.notiimg, R.id.notidate, R.id.msg });
	setListAdapter(adapter);
	getListView().setOnItemClickListener(this);
	ListView listView = getListView();
	listView.setBackgroundResource(R.drawable.background);
	listView.setCacheColorHint(Color.TRANSPARENT);

	/*
	 * TestForm是原來決定待會要從資料庫中撈什麼資料出來 UserSchema._ID
	 * 這些是定義在fileContentProvider.java內 這邊是用來""初始化""所有通知的使用狀態(一則通知代表一個檔案)
	 */

    }

    protected void onResume() {// 此處為,點擊下載後 刪除訊息
	super.onResume();
	list.clear();
	list = getData();
	adapter.notifyDataSetChanged();
    }

    /*
     * 這裡是adapter所需要用到的資料來源，ArrayList使用的是 key valuse的方式儲存
     */
    private List<Map<String, Object>> getData() {
	getinfo.clear();
	notiinfo tempinfo = new notiinfo();

	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); // 將date
	String tempdate;
	// uri是連結內建DB的一個方式，這裡要去抓簡訊資料庫中的收件夾
	Uri SMS_INBOX = Uri.parse("content://sms/inbox");
	Cursor new_sms_cursor = getContentResolver().query(SMS_INBOX, new String[] { "address", "date", "body" }, "address='0930345396' or address='+886930345396'", null, null);

	// 檢查是否有新的sms
	if (new_sms_cursor.getCount() > 0) {
	    new_sms_cursor.moveToFirst();
	    for (int i = 0; i < new_sms_cursor.getCount(); i++) {

		// decode到一般人看得懂得型式
		Date d = new Date(Long.parseLong(new_sms_cursor.getString(new_sms_cursor.getColumnIndex("date"))));
		tempdate = dateFormat.format(d);

		sms = new_sms_cursor.getString(new_sms_cursor.getColumnIndex("body"));
		
		messageProcess MsgSave = new messageProcess();
		MsgSave.insertdata(getContentResolver(), sms, tempdate);
		MsgSave=null;
		// 自定義的函式
		

		new_sms_cursor.moveToNext();
	    }// end for
	}// end if
	new_sms_cursor.close();

	// 因為被要求開啟d2d,所以先檢查d2d是否為1
	Cursor d2d_cursor = getContentResolver().query(Uri.parse("content://tab.list.d2d/user_reply"), reply, "msg = 'd2d' and d2d='1'", null, null);
	if (d2d_cursor.getCount() > 0) {
		d2d_cursor.moveToFirst();
	    /*
	     * 看有幾筆簡訊未讀,就建立幾筆通知到getinfo裡面，例如有四筆未讀的通知，就建立四筆簡訊的相關資訊到getinfo裡面
	     */
	    for (int x = 0; x < d2d_cursor.getCount(); x++) {
		// 將簡訊存入自定義的物件(tempinfo)
		tempinfo.setSender(d2d_cursor.getString(1));
		tempinfo.setContent("要求您開啟d2d_server");
		tempinfo.setToken(d2d_cursor.getString(3));
		tempinfo.setDate(d2d_cursor.getString(4));
		tempinfo.setmsg(d2d_cursor.getString(5));
		// 將自訂物件存入至getinfo
		getinfo.add(tempinfo);
		tempinfo = new notiinfo();
		d2d_cursor.moveToNext();
	    }

	}
	d2d_cursor.close();

	// 由於已經讀過retrievable的信件,所以有紀錄tittle的名稱,故撈tittle 為null的data,即表示撈未讀信件
	Cursor user_cursor = getContentResolver().query(Uri.parse("content://tab.list.d2d/user_data"), Form, "read is null and content is not null", null, null);
	if (user_cursor.getCount() > 0) {
	    user_cursor.moveToFirst();
	    /*
	     * 看有幾筆簡訊未讀,就建立幾筆通知到getinfo裡面，例如有四筆未讀的通知，就建立四筆簡訊的相關資訊到getinfo裡面
	     */
	    for (int x = 0; x < user_cursor.getCount(); x++) {
		// 將簡訊存入自定義的物件(tempinfo)
		tempinfo.setSender(user_cursor.getString(1));
		tempinfo.setContent(user_cursor.getString(2));
		tempinfo.setToken(user_cursor.getString(3));
		tempinfo.setFilesize(user_cursor.getString(4));
		tempinfo.setDate(user_cursor.getString(5));
		// 將自訂物件存入至getinfo
		getinfo.add(tempinfo);
		tempinfo = new notiinfo();
		user_cursor.moveToNext();
	    }
	}
	user_cursor.close();

	/*
	 * 如同上述的作法，但是這邊才是真正要做 key value的事情，value就是來自於上述的getinfo
	 * 例如簡訊A的notititle為是什麼?從getinfo中拿出來 簡訊A的notiinfo是什麼?一樣也是從getinfo中拿出來
	 */
	Map<String, Object> map = new HashMap<String, Object>();
	for (int i = 0; i < getinfo.size(); i++) { // put info array into
	    // list
	    map = new HashMap<String, Object>();
	    // 將每筆簡訊的資訊存入到自定義的adapter的資料欄位
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

    // 當按下通知時，要做的事情
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long position) {

	String aa;
	Cursor change_state = getContentResolver().query(Uri.parse("content://tab.list.d2d/user_data"), null, "userstatus='download'", null, null);
	// 檢查目前有沒有檔案正在下載
	if (change_state.getCount() > 0) {
	    Toast.makeText(getApplicationContext(), "目前尚有檔案下載中，稍後再試", Toast.LENGTH_LONG).show();
	    change_state.close();
	} else {
	    change_state.close();
	    listid = (int) position;
	    // 檢查此筆檔案是可下載 還是 要求上傳檔案
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
		// 這裡是retrievable
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		// 傳入寄件人
		bundle.putString("contact", getinfo.get(listid).getSender());
		intent.putExtras(bundle);
		// intent.setClass(contactlist.this, dialogViewCtrl.class);
		intent.setClass(Notify.this, receivelist.class);
		startActivity(intent);
	    }

	}// else end

    }

    // 建立撥放序列
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