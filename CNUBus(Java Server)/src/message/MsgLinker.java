//server
package message;

import termproj.org.json.JSONArray;
import termproj.org.json.JSONException;
import termproj.org.json.JSONObject;

public class MsgLinker {
	public final static String MSGTOKEN = "MESSAGE";
	public final static String DRAWTOKEN = "DRAW";
	public final static String FILETOKEN = "FILE";
	public final static String LOGINTOKEN = "LOGIN";
	public final static String USERLISTTOKEN = "REQUIREUSERLIST";
	
	public static String msgBuild(String key, String input) {//메시지 만들기(JSON 방식)
		return MsgLinker.msgBuild(key, input, "");
	}
	public static String msgBuild(String key, String input, String user) {//메시지 만들기(JSON 방식)
		JSONObject jsonObject = new JSONObject();
		jsonObject.append(user,input);
		JSONObject message = new JSONObject();
		message.append(key, jsonObject);
		//System.out.println(message.toString());
		return message.toString();
	}
	public static String[] msgRead(String key, String msg) {//메시지 읽기
		//키 값, 메시지 String을 받아서 해당 key에 해당하는 메시지일 시 return (메시지내용 String), 아닐 시 return null
		String[] output = new String[2];//index 0 : 아이디, index 1 : 내용
		JSONObject message = null;
		JSONArray tmp;
		try {
			message = new JSONObject(msg);
			tmp = new JSONArray(message.get(key).toString());
		}
		catch(JSONException e) {
			return null;//없다!
		}
		message = tmp.getJSONObject(0);
		output[0] = message.keys().next();//유저 이름 얻기
		//System.out.println(message.get(output[0]).toString());
		tmp = new JSONArray(message.get(output[0]).toString());
		output[1] = tmp.getString(0);//메시지 내용 얻기
		return output;
	}
}
