package com.cnu_bus_alarm.cnu.network.network_client;

import com.cnu_bus_alarm.cnu.network.message.MsgLinker;

public class NetworkManager {
	//private instance variables
	private static CallbackEvent<String[]> chatEvent = new DefaultCallback();
	private static CallbackEvent<String[]> drawEvent = new DefaultCallback();
	private static CallbackEvent<String[]> fileEvent = new DefaultCallback();
	private static CallbackEvent<String[]> loginEvent = new DefaultCallback();
	private static CallbackEvent<String[]> userListEvent = new DefaultCallback();

	private static Connection connection;
	private static BackgroundListener listener;
	private static BackgroundSender sender;

	// public Getter & Setter
	// if client catches message, it works with each callback
	public static void setChatEvent(CallbackEvent<String[]> newChatEvent) {//init message callback
		NetworkManager.chatEvent = newChatEvent;
	}
	public static void setDrawEvent(CallbackEvent<String[]> newDrawEvent) {//init draw callback.
		NetworkManager.drawEvent = newDrawEvent;
	}
	public static void setFileEvent(CallbackEvent<String[]> newFileEvent) {// init file callback.
		NetworkManager.fileEvent = newFileEvent;
	}
	public static void setLoginEvent(CallbackEvent<String[]> newLoginEvent) {// init Login callback.
		NetworkManager.loginEvent = newLoginEvent;
	}
	public static void setUserListEvent(CallbackEvent<String[]> newUserListEvent) {// init newUserList callback.
		NetworkManager.userListEvent = newUserListEvent;
	}
	//inner class
	private static class CallbackForThreeMessage implements CallbackEvent<String>{//NetworkListener
		
		@Override
		public void run(String input) {
			String[] chat = MsgLinker.msgRead(MsgLinker.MSGTOKEN, input);
			String[] draw = MsgLinker.msgRead(MsgLinker.DRAWTOKEN, input);
			String[] file = MsgLinker.msgRead(MsgLinker.FILETOKEN, input);
			String[] login = MsgLinker.msgRead(MsgLinker.LOGINTOKEN, input);
			String[] userlist = MsgLinker.msgRead(MsgLinker.USERLISTTOKEN, input);
			if(chat != null) {//on chat msg received
				NetworkManager.chatEvent.run(chat);
			}
			else if(draw != null) {//on draw msg received
				NetworkManager.drawEvent.run(draw);
			}
			else if(file != null) {//on file msg received
				/*StringBuffer sbuffer = new StringBuffer();
				String[] splitted = input.split(""+NetworkManager.FILETOKEN);
				for(int i = 1;i<splitted.length-1;i++) {
					sbuffer.append(splitted[i]);
					if(i+1<splitted.length-1) {
						break;
					}
					sbuffer.append(NetworkManager.FILETOKEN);
				}*/
				NetworkManager.fileEvent.run(file);
			}
			else if(login != null) {//on login msg received
				NetworkManager.loginEvent.run(login);
			}
			else if(userlist != null) {//on Loginned
				NetworkManager.userListEvent.run(userlist);
			}
		}
	}
	private static class DefaultCallback implements CallbackEvent<String[]>{//Callback Event�� ���� �������� �ʾ��� ��

		@Override
		public void run(String[] input) {
			System.out.println("정의되지 않은 콜백입니다. : "+input);
		}
		
	}
	//private method
	public static void init() {//Sender 및 Receiver를 정의
		NetworkManager.connection = new Connection("192.168.1.10", 5555);//커넥션 설정
		NetworkManager.listener = new NetworkListener(connection, new CallbackForThreeMessage());//리스너 설정
		NetworkManager.sender = new NetworkSender(connection);//센더 설정
		listener.start();
	}
	public static void login(String authinfo) {//로그인 메시지 보내기
		NetworkManager.sender.send(MsgLinker.msgBuild(MsgLinker.LOGINTOKEN, authinfo));
	}
	public static void chat(String chat) {//채팅 메시지 보내기
		NetworkManager.sender.send(MsgLinker.msgBuild(MsgLinker.MSGTOKEN, chat));
	}
	public static void draw(String draw) {//드로우 메시지 보내기
		NetworkManager.sender.send(MsgLinker.msgBuild(MsgLinker.DRAWTOKEN, draw));
	}
	public static void file(String file) {//파일 보내기
		NetworkManager.sender.send(MsgLinker.msgBuild(MsgLinker.FILETOKEN, file));
	}
	public static void userList() {//유저리스트 요청 메시지 보내기
		NetworkManager.sender.send(MsgLinker.msgBuild(MsgLinker.USERLISTTOKEN, ""));
	}
}
