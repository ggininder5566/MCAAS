package kissmediad2d.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tab.list.fileContentProvider.UserSchema;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

class contactinfo // ��class�ΨӰO��list�������
{
	private String contact, img, msg, date;

	contactinfo() {
		date = new String();
		contact = new String();
		img = new String();
		msg = new String();
	}

	public void setdate(String arg) {
		date = arg;
	}

	public void setcontact(String arg) {
		contact = arg;
	}

	public void setimg(String arg) {
		img = arg;
	}

	public void settittle(String arg) {
		msg = arg;
	}

	public String getdate() {
		return date;
	}

	public String getcontact() {
		return contact;
	}

	public String getimg() {
		return img;
	}

	public String getmsg() {
		return msg;
	}
}

public class contactlist extends ListActivity implements OnItemClickListener {

	/*
	 * �o��O��ܦ�����"�ϥΪ�"�H²�T���A�A�P�ɷ|��̫ܳ�@���U�������
	 */
	List<contactinfo> getcontactinfo;
	View view;
	String[] listid;
	boolean finishRetreive = false, newfile = false;
	String filesize = null;
	String temp, getTitle, getContent, getSender = null, getFilename;
	ProgressDialog dialog = null;
	SimpleAdapter adapter;
	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("InBox");
		adapter = new SimpleAdapter(this, getData(), R.layout.contactlist, new String[] { "contacttitle", "contactinfo", "contactimg", "contactdate" }, new int[] { R.id.contacttitle, R.id.contactinfo, R.id.contactimg, R.id.contactdate });

		setListAdapter(adapter);
		getListView().setOnItemClickListener(this);
		ListView listView = getListView();
		listView.setBackgroundResource(R.drawable.background);
		listView.setCacheColorHint(Color.TRANSPARENT);

	}

	@Override
	protected void onResume() {// ���B��,�C�����ح�����,�ߧY������s
		super.onResume();
		list.clear();
		list = getData();
		adapter.notifyDataSetChanged();
	}

	private List<Map<String, Object>> getData() {
		int count = 0;
		contactinfo tempcontactinfo;

		getcontactinfo = new ArrayList<contactinfo>();
		String[] Form = { UserSchema._SENDER };
		String[] Form1 = { UserSchema._TITTLE, UserSchema._DATE, UserSchema._SENDER };
		// �ˬd�O�_���H��H
		Cursor sender_cursor = getContentResolver().query(Uri.parse("content://tab.list.d2d/user_group"), Form, null, null, null);
		// �ܤ֦��@��H��H
		if (sender_cursor.getCount() > 0) {
			sender_cursor.moveToFirst();
			for (int sender = 0; sender < sender_cursor.getCount(); sender++) {
				Cursor sender_data_cursor = getContentResolver().query(Uri.parse("content://tab.list.d2d/user_data"), Form1, "sender='" + sender_cursor.getString(0) + "' and tittle!='null' and userstatus !='delete'", null, null);
				// �H��H�O�_�ܤ֦��@���O��Ū�L���T��,�@���ˬdtittle,
				// ���X�L�h�w�gŪ�L��²�T��,��̫ܳ�@�ʬݹL�����,�ҥH��moveToLast
				if (sender_data_cursor.getCount() > 0) {
					sender_data_cursor.moveToLast();
					tempcontactinfo = new contactinfo();
					tempcontactinfo.settittle(sender_data_cursor.getString(0));
					tempcontactinfo.setdate(sender_data_cursor.getString(1));
					tempcontactinfo.setcontact(sender_data_cursor.getString(2));
					getcontactinfo.add(tempcontactinfo);
					count++;
					tempcontactinfo = new contactinfo();
				}// if sender_data end
				sender_data_cursor.close();
				sender_data_cursor = null;
				sender_cursor.moveToNext();
			}// sender loop end
		}//if sender_cursor end
		sender_cursor.close();

		Map<String, Object> map = new HashMap<String, Object>();
		if (getcontactinfo.size() != 0) {
			for (int i = 0; i <= count - 1; i++) {
				map = new HashMap<String, Object>();
				map.put("contacttitle", getcontactinfo.get(i).getcontact());
				map.put("contactinfo", getcontactinfo.get(i).getmsg());

				map.put("contactimg", R.drawable.head);
				map.put("contactdate", getcontactinfo.get(i).getdate());
				list.add(map);
			}// for end
		}//if end
		return list;

	}//getdata end

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		int id = (int) arg3;
		// contact���T���T��:contact msg date
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		// �ǤJ�H��H

		bundle.putString("contact", getcontactinfo.get(id).getcontact());

		intent.putExtras(bundle);
		// intent.setClass(contactlist.this, dialogViewCtrl.class);
		intent.setClass(contactlist.this, receivelist.class);
		startActivity(intent);

	}


}
