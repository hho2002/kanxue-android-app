package com.pediy.bbs.kanxue;

//import android.app.AlertDialog;
import android.app.TabActivity;
//import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

import com.pediy.bbs.kanxue.net.Api;

public class MainActivity extends TabActivity {
	private long m_exitTime = 0;
	private String[] m_tabTitle = new String[] { "新贴", "主页", "安全资讯", "设置","夜间模式" };

	private Class<?>[] m_tabIntent = new Class<?>[] { ForumDisplayPage.class,
			ForumHomePage.class, ForumDisplayPage.class, SettingPage.class ,ForumHomePage.class};

	private int[] m_tabIcon = new int[] { R.drawable.collections_view_as_list,
			R.drawable.collections_view_as_grid, R.drawable.coffee,
			R.drawable.action_settings,R.drawable.btn_night_mode };

	private Bundle[] m_data = new Bundle[] {
			createBundle(Api.NEW_FORUM_ID, "新贴", true), null,
			createBundle(Api.SECURITY_FORUM_ID, "安全资讯", true), null ,null};

	private TabHost tabHost;  //Tviker add
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
		tabHost = getTabHost();
		for (int i = 0; i < this.m_tabTitle.length; i++) {
			String title = this.m_tabTitle[i];
			Intent intent = new Intent(this, m_tabIntent[i]);
			if (m_data[i] != null) {
				intent.putExtras(m_data[i]);
			}
			View tab = getLayoutInflater().inflate(R.layout.forum_tab, null);
			ImageView imgView = (ImageView) tab.findViewById(R.id.tabIcon);
			imgView.setImageResource(m_tabIcon[i]);
			TabSpec spec = tabHost.newTabSpec(title).setIndicator(tab)
					.setContent(intent);
			tabHost.addTab(spec);
		}
		
		// 每次进入检测更新
		//App app = (App) this.getApplication();
		//app.checkUpdate(this);
		
		tabHost.setOnTabChangedListener(new AnimatedTabHostListener(tabHost,this,this));
		
	}

	// 按两下返回键退出，在tabActivity中不好用
	/*
	 * @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
	 * if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() ==
	 * KeyEvent.ACTION_DOWN) { if((System.currentTimeMillis() - m_exitTime) >
	 * 2000) { //System.currentTimeMillis()无论何时调用，肯定大于2000 Toast.makeText(this,
	 * "再按一次退出程序",Toast.LENGTH_SHORT).show(); m_exitTime =
	 * System.currentTimeMillis(); } else { finish(); System.exit(0); } return
	 * true; } return super.onKeyDown(keyCode, event); }
	 */

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int keyCode = event.getKeyCode();
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - m_exitTime) > 2000) { // System.currentTimeMillis()无论何时调用，肯定大于2000
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				m_exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.dispatchKeyEvent(event);
	}
	
	private Bundle createBundle(int id, String title, boolean bHideBackBtn) {
		Bundle data = new Bundle();
		data.putInt("id", id);
		data.putString("title", title);
		data.putBoolean("isHideBackBtn", bHideBackBtn);
		return data;
	}
}
