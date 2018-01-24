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
	
	public static String msgBuild(String key, String input) {//�޽��� �����(JSON ���)
		return MsgLinker.msgBuild(key, input, "");
	}
	public static String msgBuild(String key, String input, String user) {//�޽��� �����(JSON ���)
		JSONObject jsonObject = new JSONObject();
		jsonObject.append(user,input);
		JSONObject message = new JSONObject();
		message.append(key, jsonObject);
		//System.out.println(message.toString());
		return message.toString();
	}
	public static String[] msgRead(String key, String msg) {//�޽��� �б�
		//Ű ��, �޽��� String�� �޾Ƽ� �ش� key�� �ش��ϴ� �޽����� �� return (�޽������� String), �ƴ� �� return null
		String[] output = new String[2];//index 0 : ���̵�, index 1 : ����
		JSONObject message = null;
		JSONArray tmp;
		try {
			message = new JSONObject(msg);
			tmp = new JSONArray(message.get(key).toString());
		}
		catch(JSONException e) {
			return null;//����!
		}
		message = tmp.getJSONObject(0);
		output[0] = message.keys().next();//���� �̸� ���
		//System.out.println(message.get(output[0]).toString());
		tmp = new JSONArray(message.get(output[0]).toString());
		output[1] = tmp.getString(0);//�޽��� ���� ���
		return output;
	}
}
