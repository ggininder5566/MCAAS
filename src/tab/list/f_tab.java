package tab.list;

import android.R;
import android.net.Uri;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;

import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class f_tab extends Activity {
	/*
	 * 從writepage呼叫的，這邊是用來管理fragment用的(f_系列的class)
	 */
	Uri uri;
	fileContentProvider test = new fileContentProvider();
	int a = 0, b = 0, c = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getIntent().setData(Uri.parse("content://tab.list.file.cloud/file_choice"));
		final Uri uri_test = getIntent().getData();
		uri = uri_test;

		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		// 使用ActionBar
		final ActionBar actbar = getActionBar();
		// 設定ActionBar的模式
		actbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // set
		// fullscreen

		actbar.setDisplayShowHomeEnabled(false);
		actbar.setDisplayShowTitleEnabled(false);
		setContentView(kissmediad2d.android.R.layout.f_tab);
		actbar.setDisplayShowTitleEnabled(false);
		// 加入f_bmusic到actbar,並設定監聽器
		f_bmusic af = new f_bmusic();
		actbar.addTab(actbar.newTab().setText("MUSIC").setTabListener(new la(af)));
		// 加入f_bvideo到actbar,並設定監聽器
		f_bvideo bf = new f_bvideo();
		actbar.addTab(actbar.newTab().setText("VIDEO").setTabListener(new lb(bf)));
		// 加入f_bbimage到actbar,並設定監聽器
		f_bimage cf = new f_bimage();
		actbar.addTab(actbar.newTab().setText("IMAGE").setTabListener(new lc(cf)));
		// 初始化temp_file與file_choice
		test.del_table(uri);
	}

	// 這邊是監聽頁面切換時所要做的事，
	public class la implements ActionBar.TabListener {
		private f_bmusic frag;

		public la(f_bmusic fragment) {
			frag = fragment;
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub

		}

		// 當頁面被選擇時則顯示頁面
		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			if (a == 0) {
				a=1;
				ft.add(android.R.id.content, frag, null);
			}
			ft.show(frag);
		}

		// 當頁面被取消時則隱藏頁面，目的是為了保持前一頁面的狀態
		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			ft.hide(frag);
		}

	}

	public class lb implements ActionBar.TabListener {
		private f_bvideo frag;

		public lb(f_bvideo fragment) {
			frag = fragment;
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			if (b == 0) {
				b=1;
				ft.add(android.R.id.content, frag, null);
			}

			ft.show(frag);
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			ft.hide(frag);
		}

	}

	public class lc implements ActionBar.TabListener {
		private f_bimage frag;

		public lc(f_bimage fragment) {
			frag = fragment;
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			if (c == 0) {
				c=1;
				ft.add(android.R.id.content, frag, null);
			}
			ft.show(frag);
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			ft.hide(frag);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		
		return true;
	}

}
