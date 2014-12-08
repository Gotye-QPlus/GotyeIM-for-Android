package com.gotye.api;

import org.json.JSONException;
import org.json.JSONObject;
/**
 * 消息中的多媒体部分
 * @author Administrator
 *
 */
public class Media {
	public static final int MEDIA_STATUS_DEFAULT=0;
	public static final int MEDIA_STATUS_DOWNLOADING=1;
	public static final int MEDIA_STATUS_DOWNLOADED=2;
	public static final int MEDIA_STATUS_DOWNLOAD_FAILED=3;
	private int duration;
	
	private String path;
	
	private String path_ex;
	
	private GotyeMediaType type ;

	private int status;
	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath_ex() {
		return path_ex;
	}

	public void setPath_ex(String path_ex) {
		this.path_ex = path_ex;
	}

	public GotyeMediaType getType() {
		return type;
	}

	public void setType(GotyeMediaType type) {
		this.type = type;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Media [duration=" + duration + ", path=" + path + ", path_ex="
				+ path_ex + ", type=" + type + "]";
	}
	
	public static Media jsonToMedia(JSONObject obj){
		Media media=new Media();
		try {
			if(obj.has("duration")){
				media.duration=obj.getInt("duration");
			}
			if(obj.has("path_ex")){
				media.path_ex=obj.getString("path_ex");
			}
			
			if(obj.has("status")){
				media.status=obj.getInt("status"); //0,1 downloading,2,down,3,shibai
			}
			media.path=obj.getString("path");
			int type=obj.getInt("type");
			media.type=GotyeMediaType.values()[type];
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return media;
	}
	
	

}
