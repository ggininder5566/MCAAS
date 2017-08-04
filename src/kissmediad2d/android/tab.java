package kissmediad2d.android;

import kissmediad2d.android.R;
import login.submit1.Logout;
import tab.list.fileContentProvider;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class tab extends TabActivity implements OnTabChangeListener {
	/**
	 * 這裡是建立頁面的切換，tabhost負責把各個activity放在一個區域內集中管理
	 */
	TabHost tabhost;
	TabSpec tab1, tab2, tab3, tab4;
	Button back;
	Logout logout = new Logout();
	fileContentProvider test = new fileContentProvider();
	Uri uri;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getIntent().setData(Uri.parse("content://tab.list.d2d/file_choice"));
		final Uri uri_test = getIntent().getData();
		uri = uri_test;

		requestWindowFeature(Window.FEATURE_NO_TITLE); // set no title
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // set
																														// fullscreen
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		// //橫向

		setContentView(R.layout.tab);

		back = (Button) findViewById(R.id.tabback);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				logout.logout_start();
			}
		});

		tabhost = getTabHost();
		tab1 = tabhost.newTabSpec("tab1").setIndicator("", getResources().getDrawable(R.drawable.questionmark)).setContent(new Intent(tab.this, Notify.class));
		tabhost.addTab(tab1);

		tab2 = tabhost.newTabSpec("tab2").setIndicator("", getResources().getDrawable(R.drawable.inbox)).setContent(new Intent(tab.this, contactlist.class));
		tabhost.addTab(tab2);

		tab3 = tabhost.newTabSpec("tab3").setIndicator("", getResources().getDrawable(R.drawable.send)).setContent(new Intent(tab.this, writepage.class));
		tabhost.addTab(tab3);

		tab4 = tabhost.newTabSpec("tab4").setIndicator("", getResources().getDrawable(R.drawable.editdata)).setContent(new Intent(tab.this, edit.class));
		tabhost.addTab(tab4);

		for (int i = 0; i < tabhost.getTabWidget().getChildCount(); i++) {
			TextView tv = (TextView) tabhost.getTabWidget().getChildAt(i).findViewById(android.R.id.title); // Unselected
																											// Tabs
			tv.setTextColor(Color.parseColor("#000000"));
		}

		tabhost.setOnTabChangedListener(this);
		test.del_table(uri);
	}

	@Override
	public void onTabChanged(String arg0) {
		// TODO Auto-generated method stub

	}

}
