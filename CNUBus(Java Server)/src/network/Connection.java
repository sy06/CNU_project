package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import message.MsgLinker;
import room.Room;
import room.RoomManager;
import view.GUIView;

public class Connection extends Thread {
	
	private Socket sock;
	private PrintWriter pw;
	private BufferedReader br;
	private String name;
	private Room room;
	private boolean disconnected;

	public Connection(Socket socket) {
		this.sock = socket;
		this.room = null;
	}

	public void run() {
		this.init();
		if(disconnected) {
			this.exit();
			return;
		}
		this.listen();
		this.exit();
	}

	private void init() {
		InetAddress inetaddr = sock.getInetAddress();
		GUIView.view.addMessage(inetaddr.getHostAddress() + " 로부터 접속했습니다.");

		try {
			OutputStream out = this.sock.getOutputStream();
			InputStream in = this.sock.getInputStream();

			this.pw = new PrintWriter(new OutputStreamWriter(out));
			this.br = new BufferedReader(new InputStreamReader(in));
			disconnected = false;
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void auth() {//초기 로그인. 유저 이름, 방 이름, 방 비밀번호 순서로 받음.
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				GUIView.view.addMessage("클라이언트로부터 전송받은 문자열 : " + line);
				String[] parseline = MsgLinker.msgRead(MsgLinker.LOGINTOKEN, line);
				if(parseline != null) {//올바른 로그인 메시지인지 확인
					GUIView.view.addMessage("클라이언트로부터 전송받은 문자열 파싱 : " + parseline[0]);
					line = parseline[1];
					String[] splitted = line.split(":");//:단위로 나누기
					GUIView.view.addMessage("LOGIN DETECTED : "+line);
					if(splitted.length!=3) {//유저이름, 방이름, 방비번이 있는가?(인자 3개)
						GUIView.view.addMessage("INVALID LOGIN FORMAT");
						continue;
					}
					GUIView.view.addMessage("LOGIN...");
					this.name = splitted[0];
					Room findroom = RoomManager.find(splitted[1],splitted[2]);
					if(findroom != null) {
						this.room = RoomManager.existRoomEnter(this, findroom);
					}
					else {
						this.room = RoomManager.makeAndEnter(this, splitted[1], splitted[2]);
					}
					GUIView.view.addMessage("LOGIN SUCCESS! : "+this.name);
					this.room().sendToAll(this, MsgLinker.msgBuild(MsgLinker.LOGINTOKEN, "",this.name.toString()));//로그인한 유저를 기존 유저들에게 알림
					this.talk(MsgLinker.msgBuild(MsgLinker.USERLISTTOKEN, this.room().userNameListToString()));
					//유저 리스트 전송
					return;
				}
				if(disconnected) {
					return;
				}
			}
		} catch (SocketException e) {
			GUIView.view.addMessage("unauthenticated socket disconnected");
			disconnected = true;
		} catch(IOException e) {
			disconnected = true;
			e.printStackTrace();
		}
	}

	private void listen() {
		try {
			String line = null;
			while ((line = br.readLine()) != null) {
				if(line.equals(""+0x00)) {
					continue;
				}
				GUIView.view.addMessage(this.name + " 클라이언트로부터 전송받은 문자열 : " + line);
				this.room().sendToAll(this, line);
				if(disconnected) {
					return;
				}
			}

		} catch (SocketException e) {
			GUIView.view.addMessage("socket disconnected : " + this.name);
			disconnected = true;
		} catch (Exception e) {
			disconnected = true;
			e.printStackTrace();
		}
		disconnected = true;
	}
	
	public void exit() {
		pw.close();
		try {
			br.close();
			sock.close();
		} catch (IOException e) {
			GUIView.view.addMessage("exit() error");
			e.printStackTrace();
		}
		
	}
	
	//Not use in thread lifecycle
	public boolean talk(String message) {
		try {
			pw.println(message);
            pw.flush();
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public Room room() {
		return room;
	}
	
	public void setDisconnected() {
		this.disconnected = true;
	}
	public String getUserName() {
		return this.name;
	}
	/*
	//token match
	private boolean tokenFind(char[] c, String target) {
		StringBuffer token = new StringBuffer();
		for(int i = 0;i<c.length;i++) {
			token.append("["+)
		}
	}*/
	
}
