/* copyright (c) DeltaYang89@gmail.com
 * Author: Tveiker (看雪论坛ID)
 * Date:2013-11-2
 * 实现Tab切换的动画
 */

package com.pediy.bbs.kanxue;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

public class AnimatedTabHostListener implements OnTabChangeListener {

	private static final int ANIMATION_TIME = 240;
	private TabHost tabHost;
	private View	preview;
	private View	curView;
	private int 	curTabIndex	= 0;
	private int 	preTabIndex = 0;  //当点击夜间模式时使得Tab选项卡不变
	private	Context mContext;
	private MainActivity mainActivity;
	private NightModeManager nightmodemanager = null;
	//如果有加代码请将“夜间模式”放到最后一个
	private String[] m_tabTitle = new String[] { "新贴", "主页", "安全资讯", "设置","夜间模式" };
	
	
	/**
     * Brightness value for dim backlight
     */
    private static final int BRIGHTNESS_DIM = 20;
    
    /**
     * Brightness value for fully on
     */
    private static final int BRIGHTNESS_ON = 255;
    
    // Backlight range is from 0 - 255. Need to make sure that user
    // doesn't set the backlight to 0 and get stuck
    private static final int MINIMUM_BACKLIGHT = BRIGHTNESS_DIM + 10;
    private static final int MAXIMUM_BACKLIGHT = BRIGHTNESS_ON;
    
    private int mOldBrightness;
    private int mCurrentBrightness = 0;
    private int mOldAutomatic;
    private boolean mAutomaticAvailable;
	
	public AnimatedTabHostListener(TabHost tabhost,Context context,MainActivity ma) {
		// TODO Auto-generated constructor stub
		this.tabHost = tabhost;
		this.mainActivity = ma;
		this.preview = tabHost.getCurrentView();
		mContext = context;
		nightmodemanager = NightModeManager.init(mContext);
	}
	
	@Override
	public void onTabChanged(String tabId) {
		// TODO Auto-generated method stub
		for(int i=0 ; i<m_tabTitle.length-1; ++i){
			if(tabId==m_tabTitle[i]){
				preTabIndex = i;
				break;
			}
		}	
		if(tabId=="夜间模式"){
			tabHost.setCurrentTab(preTabIndex);
			showBrightnessSettingDialog();
		}
		int temTab = tabHost.getCurrentTab();
		curView = tabHost.getCurrentView();
		if(temTab>curTabIndex){
			preview.setAnimation(outToLeftAnimation());
			curView.setAnimation(inFromRightAnimation());
		}
		else if(temTab<curTabIndex){
			preview.setAnimation(outToRightAnimation());
			curView.setAnimation(inFromLeftAnimation());
		}
		preview = curView;
		curTabIndex = temTab;
	}
	
	void showBrightnessSettingDialog(){
		System.out.println("tviker-----show Dialog");
		final Builder builder = new AlertDialog.Builder(mainActivity);
		final View view = mainActivity.getLayoutInflater().inflate(R.layout.brightness_view, null);
		
		final SeekBar brightSeekBar = (SeekBar)view.findViewById(R.id.brightness_bar);
		brightSeekBar.setMax(MAXIMUM_BACKLIGHT - MINIMUM_BACKLIGHT);
		int brightness = nightmodemanager.getBrightnessValue();  
	    int process = brightness - MINIMUM_BACKLIGHT;
	    if (process<0) {  
        	process = 0;  
        	mOldBrightness = MINIMUM_BACKLIGHT;  
        	mCurrentBrightness = MINIMUM_BACKLIGHT;  
        }else{  
        	mOldBrightness = brightness;  
        	mCurrentBrightness = brightness;  
        }  
	    brightSeekBar.setProgress(process);
	    final CheckBox autoBrightness = (CheckBox)view.findViewById(R.id.auto_brightness);  
        mOldAutomatic = nightmodemanager.getBrightnessMode();  
        mAutomaticAvailable = nightmodemanager.isAutoBrightness();  
        autoBrightness.setChecked(mAutomaticAvailable);  
        if(mAutomaticAvailable){  
            brightSeekBar.setVisibility(View.GONE);  
        }else{  
        	brightSeekBar.setVisibility(View.VISIBLE);  
        }
        autoBrightness.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
            @Override  
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {  
             mAutomaticAvailable = isChecked;  
             if(isChecked){  
                    brightSeekBar.setVisibility(View.GONE);  
                    nightmodemanager.startAutoBrightness();  
                    //systemManager.setBrightness(MainActivity.this, systemManager.getScreenBrightness());  
                    int process = nightmodemanager.getBrightnessValue() - MINIMUM_BACKLIGHT;  
                    if (process<0) {  
                    	process = 0;  
                    }// end if  
                    brightSeekBar.setProgress(process);  
                }else{  
                	brightSeekBar.setVisibility(View.VISIBLE);  
                	nightmodemanager.stopAutoBrightness();  
                    //systemManager.setBrightness(MainActivity.this, systemManager.getScreenBrightness());  
                    int process = nightmodemanager.getBrightnessValue() - MINIMUM_BACKLIGHT;  
                    if (process<0) {
                    	process = 0;  
                    }// end if  
                    brightSeekBar.setProgress(process);  
                }  
            }  
        });
        brightSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {  
            
            @Override  
            public void onStopTrackingTouch(final SeekBar seekBar) {  
       
            }  
       
            @Override  
            public void onStartTrackingTouch(final SeekBar seekBar) {  
       
            }  
       
            @Override  
            public void onProgressChanged(final SeekBar seekBar, final int progress,  
                    final boolean fromUser) {  
            	mCurrentBrightness = progress + MINIMUM_BACKLIGHT;  
                nightmodemanager.setBrightnessValue(mainActivity, mCurrentBrightness);  
            }  
        });
        builder.setTitle(R.string.brightness);  
        builder.setView(view);  
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){  
       
            @Override  
            public void onClick(final DialogInterface dialog, final int which) {  
             // set brightness  
                if(mAutomaticAvailable){  
                    nightmodemanager.saveBrightnessValue(nightmodemanager.getBrightnessValue());  
                }else{  
                    nightmodemanager.saveBrightnessValue(mCurrentBrightness);  
                }  
            }  
       
        });  
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {  
       
            @Override  
            public void onClick(final DialogInterface dialog, final int which) {  
                // recover brightness  
             nightmodemanager.setBrightnessValue(mainActivity, mOldBrightness);  
             nightmodemanager.saveBrightnessValue(mOldBrightness);  
                // recover automatic brightness mode  
             nightmodemanager.setBrightnessMode(mOldAutomatic);  
            }  
        });  
        builder.show();
	}
	
	private Animation setProperties(Animation animation){
	        animation.setDuration(ANIMATION_TIME);
	        animation.setInterpolator(new AccelerateInterpolator());
	        return animation;
	}
	
	private Animation inFromRightAnimation(){
		Animation inFromRight = new TranslateAnimation( Animation.RELATIVE_TO_PARENT, 1.0f,
														Animation.RELATIVE_TO_PARENT, 0.0f, 
														Animation.RELATIVE_TO_PARENT, 0.0f,
														Animation.RELATIVE_TO_PARENT, 0.0f);
		return setProperties(inFromRight);
	}
	
	private Animation inFromLeftAnimation(){
		Animation inFromLeft = new TranslateAnimation(  Animation.RELATIVE_TO_PARENT,-1.0f,
														Animation.RELATIVE_TO_PARENT,0.0f,
														Animation.RELATIVE_TO_PARENT,0.0f,
														Animation.RELATIVE_TO_PARENT,0.0f);
		return setProperties(inFromLeft);
	}
	
	private Animation outToRightAnimation(){
		Animation outToRight = new TranslateAnimation(	Animation.RELATIVE_TO_PARENT,0.0f,
														Animation.RELATIVE_TO_PARENT,1.0f,
														Animation.RELATIVE_TO_PARENT,0.0f,
														Animation.RELATIVE_TO_PARENT,0.0f);
		return setProperties(outToRight);
	}
	
	private Animation outToLeftAnimation(){
		Animation outToLeft = new TranslateAnimation(	Animation.RELATIVE_TO_PARENT, 0.0f, 
														Animation.RELATIVE_TO_PARENT, -1.0f, 
														Animation.RELATIVE_TO_PARENT, 0.0f, 
														Animation.RELATIVE_TO_PARENT, 0.0f);
		return setProperties(outToLeft);
	}
}
