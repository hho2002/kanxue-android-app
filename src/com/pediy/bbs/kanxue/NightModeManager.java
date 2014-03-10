/*
 * copyright (c) DeltaYang89@gmail.com
 * Author:Tveiker  (看雪论坛ID)
 * Date:2013-11-1
 * 管理页面的亮度,实现夜间模式
 */

package com.pediy.bbs.kanxue;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.WindowManager;

public class NightModeManager {
	//控制屏幕亮度是全局的，所以选择单例模式
	private Context mContext;
	private static NightModeManager brightnessManager;
	
	private NightModeManager(final Context context) {
		// TODO Auto-generated constructor stub
		mContext = context;
	}
	
	public static NightModeManager init(final Context context){
		if(brightnessManager == null){
			brightnessManager = new NightModeManager(context);
		}
		return brightnessManager;
	}
	
	public static NightModeManager getInstance(){
		return brightnessManager;
	}
	
	//设置屏幕亮度[0---255]
	public void setBrightnessValue(Activity activity,int brightnessValue){
		if(brightnessValue<0)
			brightnessValue = 0;
		if(brightnessValue>255)
			brightnessValue = 255;
		WindowManager.LayoutParams layoutParams = activity.getWindow().getAttributes();
		layoutParams.screenBrightness = Float.valueOf(brightnessValue*(1f/255f));
		activity.getWindow().setAttributes(layoutParams);
	}
	
	//保存屏幕亮度
	public void saveBrightnessValue(int brightnessValue){
		ContentResolver resolver = mContext.getContentResolver();
		Uri uri = android.provider.Settings.System.getUriFor("screen_brightness");
		android.provider.Settings.System.putInt(resolver, "screen_brightness",brightnessValue);
		resolver.notifyChange(uri, null);
	}
	
	//获取屏幕亮度值
	public int getBrightnessValue(){
		int brightnessValue = 255;
		ContentResolver resolver = mContext.getContentResolver();
		try {
			brightnessValue = android.provider.Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return brightnessValue;
	}
	
	//开启亮度自动调节
	public void startAutoBrightness() {
        final ContentResolver resolver = mContext.getContentResolver();
        Settings.System.putInt(resolver,Settings.System.SCREEN_BRIGHTNESS_MODE,
                               Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
        final Uri uri = android.provider.Settings.System.getUriFor("screen_brightness");
        resolver.notifyChange(uri, null);
    }
	
	//停止亮度自动调节
	 public void stopAutoBrightness(){
	        final ContentResolver resolver = mContext.getContentResolver();
	        Settings.System.putInt(resolver,Settings.System.SCREEN_BRIGHTNESS_MODE,
	                               Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
	        final Uri uri = android.provider.Settings.System.getUriFor("screen_brightness");
	        resolver.notifyChange(uri, null);
	  }
	 
	 //保存亮度显示模式
	 public void setBrightnessMode(int mode){
		 Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, mode);
	 }
	 
	 //获取亮度显示模式
	 public int getBrightnessMode(){
	    	try {
				return Settings.System.getInt(mContext.getContentResolver(),
				        Settings.System.SCREEN_BRIGHTNESS_MODE);
			} catch (SettingNotFoundException e) {
				return Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
			}
	 }
	 
	 public boolean isAutoBrightness() {
	      boolean automicBrightness = false;
	      try {
	          final ContentResolver resolver = mContext.getContentResolver();
	          automicBrightness = Settings.System.getInt(resolver,Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
	      } catch (final SettingNotFoundException e) {
	          e.printStackTrace();
	      }
	      return automicBrightness;
	 }
}
