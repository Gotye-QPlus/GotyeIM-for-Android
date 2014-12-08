package com.open_demo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceUitl {
   public static void setBooleanValue(Context context,String key,boolean value){
	     SharedPreferences spf=context.getSharedPreferences("boolean_prop", Context.MODE_PRIVATE);
	     Editor edit=spf.edit();
	     edit.putBoolean(key, value);
	     edit.commit();
   }
   
   public static boolean getBooleanValue(Context context,String key){
	   SharedPreferences spf=context.getSharedPreferences("boolean_prop", Context.MODE_PRIVATE);
	   return spf.getBoolean(key, false);
   }
   
   
}
