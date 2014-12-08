package com.gotye.api;

import java.io.Serializable;
/**
 * 用户、聊天室、群头像对象
 * @author Administrator
 *
 */
public class Icon implements Serializable{
	
  public String path;
  
  public String path_ex;
  
  public String url;
  
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

public String getUrl() {
	return url;
}

public void setUrl(String url) {
	this.url = url;
}

@Override
public String toString() {
	return "Icon [path=" + path + ", path_ex=" + path_ex + ", url=" + url + "]";
}
  
  
  
}
