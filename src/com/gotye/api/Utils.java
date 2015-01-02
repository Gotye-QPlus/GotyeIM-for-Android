package com.gotye.api;

import org.json.JSONException;
import org.json.JSONObject;

public class Utils {
	static GotyeChatTarget jsonToSession(JSONObject jsonObject) {
		GotyeChatTarget target = null;
		try {
			int type = jsonObject.getInt("type");
			if (type == 0) {
				target = new GotyeUser();
			} else if (type == 1) {
				target = new GotyeRoom();
			} else {
				target = new GotyeGroup();
			}
			target.Id = jsonObject.getLong("id");
			target.name = jsonObject.getString("name");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return target;
	}
}
