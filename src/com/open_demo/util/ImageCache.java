/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.open_demo.util;

import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.widget.ImageView;

import com.gotye.api.GotyeAPI;
import com.gotye.api.GotyeGroup;
import com.gotye.api.GotyeUser;
import com.open_demo.R;

public class ImageCache {
	
	private ImageCache() {
		// use 1/8 of available heap size
		cache = new LruCache<String, Bitmap>((int) (Runtime.getRuntime().maxMemory() / 8)) {
              @Override
              protected int sizeOf(String key, Bitmap value) {
                  return value.getRowBytes() * value.getHeight();
              }
          };
	}

	private static Map<String, Boolean> hasDownload=new  HashMap<String, Boolean>(); 
	private static ImageCache imageCache = null;

	public static synchronized ImageCache getInstance() {
		if (imageCache == null) {
			imageCache = new ImageCache();
		}
		return imageCache;

	}
	private LruCache<String, Bitmap> cache = null;
	
	
	public void setIcom(ImageView iconView,GotyeUser user){
		 Bitmap bmp=ImageCache.getInstance().get(user.name);
   	  if(bmp!=null){
   		  iconView.setImageBitmap(bmp);
   	  }else{
   		  bmp=BitmapUtil.getBitmap(user.getIcon().getPath());
   		  if(bmp!=null){
   			iconView.setImageBitmap(bmp);
   			put(user.name, bmp);
   		  }else{
   			  
   			  iconView.setImageResource(R.drawable.head_icon_user);
   			  if(user.getIcon().url==null){
   				  return;
   			  }
   			  if(!hasDownload.containsKey(user.getIcon().url)){
   				hasDownload.put(user.getIcon().url, true);
   				 GotyeAPI.getInstance().downloadMedia(user.getIcon().url);
   			  }
   			 
   		  }
   	  }
   	
	}
	public void setIcom(ImageView iconView,GotyeGroup group){
		Bitmap bmp=ImageCache.getInstance().get(group.Id+"");
		if(bmp!=null){
			iconView.setImageBitmap(bmp);
		}else{
			bmp=BitmapUtil.getBitmap(group.getIcon().getPath());
			if(bmp!=null){
				iconView.setImageBitmap(bmp);
				put(group.Id+"", bmp);
			}else{
				
				iconView.setImageResource(R.drawable.head_icon_user);
				if(group.getIcon().url==null){
					return;
				}
				if(!hasDownload.containsKey(group.getIcon().url)){
					hasDownload.put(group.getIcon().url, true);
					GotyeAPI.getInstance().downloadMedia(group.getIcon().url);
				}
				
			}
		}
		
	}
	
	/**
	 * put bitmap to image cache
	 * @param key
	 * @param value
	 * @return  the puts bitmap
	 */
	public Bitmap put(String key, Bitmap value){
		if(TextUtils.isEmpty(key)){
			return null;
		}
		if(value==null){
			return null;
		}
		return cache.put(key, value);
	}
	
	/**
	 * return the bitmap
	 * @param key
	 * @return
	 */
	public Bitmap get(String key){
		if(key==null){
			return null;
		}
		return cache.get(key);
	}
}
