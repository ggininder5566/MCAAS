package kissmediad2d.android;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import login.submit1.Logout;
import login.submit1.locupdate;
import tab.list.fileContentProvider;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.widget.Button;
import android.widget.EditText;

public class MainFragment extends FragmentActivity implements ActionBar.TabListener {

	SectionsPagerAdapter mSectionsPagerAdapter;

	ViewPager mViewPager;
	private EditText et, et2;
	private String getid, getpw, getip;
	private boolean LoginNPE = true;
	private int cs = 0;
	private static final int UPDATE_LIST_MES = 0;
	private boolean finishUpdateList = false;
	public boolean retrieving = true;
	public String sms = null;
	public String LoginrequestString = new String();
	public static String login_name;
	public static String Homeip = "163.20.52.40:8100";
	public static String method;
	Boolean idle=true;
	String[] aliveIp;
	private NotificationManager gNotMgr = null;
	public String[] rep, req;
	Button no_bn, in_bn, send_bn, ed_bn, log_bn, reg_bn;
	locupdate locup =new locupdate();
	ProgressDialog pdialog = null;
	Logout logout = new Logout();
	fileContentProvider KM_DB = new fileContentProvider();
	int i = 0;
	List<String> fileList = new ArrayList<String>();
	String filesize = null;
	String bdtoken =new String();
	String bdsender =new String();
	String bdtype =new String();
	String bdreretrieve = new String();
	String butoken =new String();
	String busender =new String();
	int tabPosition;
	public String selectitem= null;
	public String sdcardPath = Environment.getExternalStorageDirectory().toString() + File.separator + "KM" + "/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Bundle bundle =getIntent().getExtras();
		selectitem=bundle.getString("location");
		final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setIcon(null);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                actionBar.setSelectedNavigationItem(position);
            }
        });
        actionBar.addTab(actionBar.newTab()
                 .setIcon(kissmediad2d.android.R.drawable.ic_action_chat)
                 .setTabListener(this));
        actionBar.addTab(actionBar.newTab()
                .setIcon(kissmediad2d.android.R.drawable.ic_action_email)
                .setTabListener(this));
        actionBar.addTab(actionBar.newTab()
                .setIcon(kissmediad2d.android.R.drawable.ic_action_new_email)
                .setTabListener(this));
//        actionBar.addTab(actionBar.newTab()
//                .setIcon(kissmediad2d.android.R.drawable.ic_action_event)
//                .setTabListener(this));
        mViewPager.setCurrentItem(Integer.valueOf(selectitem));
	}



    public static class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}


        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    // The first section of the app is the most interesting -- it offers
                    // a launchpad into the other demonstrations in this example application.
                	return CopyOfNotify.newInstance(i + 1);
                case 1:
                	return CopyOfcontactlist.newInstance(i + 1);
                case 2:
                	return CopyOfwritepage.newInstance(i + 1);
//                case 3:
//                	return drafts.newInstance(i + 1);
            }
			return CopyOfNotify.newInstance(i + 1);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Section " + (position + 1);
        }
    }

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
			tabPosition = tab.getPosition();	
		
    	String newTitle = null;
    	if(mViewPager!=null){
    		//updatemenu call onPrepareOptionsMenu
    		invalidateOptionsMenu();
    		 mViewPager.setCurrentItem(tab.getPosition());
    		 switch (tabPosition)
    	     {
    	         case 0:
    	             newTitle = getString(R.string.Renew);
    	             break;
    	         case 1:
    	             newTitle = getString(R.string.InBox);
    	             break;
    	         case 2:
    	             newTitle = getString(R.string.Publish);
    	             break;
//    	         case 3:
//    	             newTitle = getString(R.string.Drafts);
//    	             break;
    	             
    	     }

    	     if  (null != newTitle && !newTitle.isEmpty())
    	     {
    	         setTitle(newTitle);
    	     }
    	}
    	// When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
}
