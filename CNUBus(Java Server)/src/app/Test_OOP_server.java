package app;

import java.util.Iterator;
import java.util.LinkedList;

import termproj.org.json.JSONArray;
import termproj.org.json.JSONException;
import termproj.org.json.JSONObject;

import message.MsgLinker;



public class Test_OOP_server {
	public static void main(String[] args) {
		msgLinkerTest();
	}
	private static void msgLinkerTest() {
		String str = MsgLinker.msgBuild("key", "user", "input");
		System.out.println(MsgLinker.msgRead("aa", str)[0]+":"+MsgLinker.msgRead("key", str)[1]);
	}
	private static void jsonTest() {
		String str = "[{ \"name\": \"Alice\", \"age\": 20 }, {12:41}]";
		JSONArray ary= new JSONArray(str);
		
		JSONObject obj = ary.getJSONObject(0);
		String n = obj.get("name").toString();
		int a = obj.getInt("age");
		try {
			System.out.println(obj.get("asdf").toString());
		}
		catch(JSONException e) {
			System.out.println("json error");
		}
		System.out.println(n + " " + a);  // prints "Alice 20"
		System.out.println(ary.toString());
	}
	private static void linkedListTest() {

		LinkedList<String> ls = new LinkedList<String>();
		String s = "입력";
		ls.add("가");
		ls.add(s);
		ls.add("나");
		ls.add("다");
		Iterator<String> it = ls.iterator();
		while(it.hasNext()) {
			System.out.println(it.next());
		}
		System.out.println("삭제결과 : "+ls.remove(s));
		System.out.println("삭제결과 : "+ls.remove(s));
		it = ls.iterator();
		while(it.hasNext()) {
			System.out.println(it.next());
		}
	}
}
