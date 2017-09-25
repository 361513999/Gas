package com.hhkj.gas.www.common;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.widget.TextView;
public class SharedUtils {
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	@SuppressWarnings("deprecation")
	public SharedUtils(String NAME) {
		// TODO Auto-generated constructor stub
		preferences = BaseApplication.application.getSharedPreferences(NAME,   Context.MODE_PRIVATE );
	}
	public void reg(final String key,final TextView view){
		preferences.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {
			
			@Override
			public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
				// TODO Auto-generated method stub
				 if(arg0.contains(key)&&key.equals(arg1)){
					 P.c("人数变化");
					 view.setText(getIntValue(key));
				 }
			}
		});
	}
	/**
	 * 清空
	 */
	public void clear() {
		editor = preferences.edit();
		editor.clear();
		editor.commit();
	}
	public void clear(String key) {
		editor = preferences.edit();
		editor.remove(key);
		editor.commit();
	}
	/**
	 * 判断是否存在
	 *
	 * @param tag
	 * @return
	 */
	public boolean isHere(String tag) {
		return preferences.contains(tag);
	}
	/**
	 * 设置String类型的sharedpreferences
	 *
	 * @param key
	 * @return
	 */
	public void setStringValue(String key, String value) {
		editor = preferences.edit();
		editor.putString(key, value);
		boolean flg = editor.commit();
		P.c("保存"+flg);
	}
	public void setIntValue(String key,int value){
		editor = preferences.edit();
		editor.putInt(key, value);
		boolean flg = editor.commit();
		P.c("保存int "+flg);
	}
	public int getIntValue(String key){
		return preferences.getInt(key, 0);
	}
	/**
	 * 获得String类型的sharedpreferences
	 *
	 * @param key
	 * @return
	 */
	public String getStringValue(String key) {
		return preferences.getString(key, "");
	}
	/**
	 */
	public String getValue(String key) {
		return preferences.getString(key, "0");
	}
	/**
	 * 设置boolean类型的sharedpreferences
	 *
	 * @param key
	 * @return
	 */
	public void setBooleanValue(String key, boolean value) {
		editor = preferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	/**
	 * 获得boolean类型的sharedpreferences
	 *
	 * @param key
	 * @return
	 */
	public Boolean getBooleanValue(String key) {
		// 默认为false
		return preferences.getBoolean(key, false);
	}
}
