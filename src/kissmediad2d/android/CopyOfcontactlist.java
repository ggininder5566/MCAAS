package kissmediad2d.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import login.submit1.Logout;
import tab.list.fileContentProvider;
import tab.list.fileContentProvider.UserSchema;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

class copyofcontactinfo // 本class用來記錄list中的資料
{
	private String contact, img, msg, date;

	copyofcontactinfo() {
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

public class CopyOfcontactlist extends Fragment implements OnItemClickListener{

	/*
	 * 這邊是顯示有哪些"使用者"寄簡訊給你，同時會顯示最後一次下載的資料
	 */
	List<copyofcontactinfo> getcontactinfo;
	View view;
	ListView Audiolist;
	String[] listid;
	boolean finishRetreive = false, newfile = false;
	String filesize = null;
	String temp, getTitle, getContent, getSender = null, getFilename;
	ProgressDialog dialog = null;
	SimpleAdapter adapter;
	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

	private static final String ARG_SECTION_NUMBER = "section_number";
	public static CopyOfcontactlist newInstance(int sectionNumber) {
		CopyOfcontactlist fragment = new CopyOfcontactlist();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public CopyOfcontactlist() {
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
		adapter = new SimpleAdapter(getActivity(), getData(), R.layout.contactlist, new String[] { "contacttitle", "contactinfo", "contactimg", "contactdate" }, new int[] { R.id.contacttitle, R.id.contactinfo, R.id.contactimg, R.id.contactdate });
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
    	inflater.inflate(R.menu.main, menu);
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
        case android.R.id.home:

        	getActivity().finish();
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
		copyofcontactinfo tempcontactinfo;

		getcontactinfo = new ArrayList<copyofcontactinfo>();
		String[] Form = { UserSchema._SENDER };
		String[] Form1 = { UserSchema._TITTLE, UserSchema._DATE, UserSchema._SENDER };
		// 檢查是否有寄件人
		Cursor sender_cursor = getActivity().getContentResolver().query(Uri.parse("content://tab.list.d2d/user_group"), Form, null, null, null);
		sender_cursor.moveToFirst();
		// 至少有一位寄件人
		if (sender_cursor.getCount() > 0) {
			for (int sender = 0; sender < sender_cursor.getCount(); sender++) {
				Cursor sender_data_cursor = getActivity().getContentResolver().query(Uri.parse("content://tab.list.d2d/user_data"), Form1, "sender='" + sender_cursor.getString(0) + "' and tittle!='null' and userstatus !='delete'", null, null);
				// 寄件人是否至少有一筆是有讀過的訊息,一樣檢查tittle,
				// 撈出過去已經讀過的簡訊中,顯示最後一封看過的資料,所以用moveToLast
				if (sender_data_cursor.getCount() > 0) {
					sender_data_cursor.moveToLast();
					tempcontactinfo = new copyofcontactinfo();
					tempcontactinfo.settittle(sender_data_cursor.getString(0));
					tempcontactinfo.setdate(sender_data_cursor.getString(1));
					tempcontactinfo.setcontact(sender_data_cursor.getString(2));
					getcontactinfo.add(tempcontactinfo);
					count++;
					tempcontactinfo = new copyofcontactinfo();
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
				list.get(1);
			}// for end
		}//if end
		return list;

	}//getdata end

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long listid) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		int id = (int) listid;
		// contact的三項訊息:contact msg date
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		// 傳入寄件人

		bundle.putString("contact", getcontactinfo.get(id).getcontact());

		intent.putExtras(bundle);
		// intent.setClass(contactlist.this, dialogViewCtrl.class);
		intent.setClass(getActivity(), receivelist.class);
		getActivity().startActivity(intent);
		
	}



}
