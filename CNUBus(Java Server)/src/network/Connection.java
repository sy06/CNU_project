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
		GUIView.view.addMessage(inetaddr.getHostAddress() + " �κ��� �����߽��ϴ�.");

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

	private void auth() {//�ʱ� �α���. ���� �̸�, �� �̸�, �� ��й�ȣ ������ ����.
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				GUIView.view.addMessage("Ŭ���̾�Ʈ�κ��� ���۹��� ���ڿ� : " + line);
				String[] parseline = MsgLinker.msgRead(MsgLinker.LOGINTOKEN, line);
				if(parseline != null) {//�ùٸ� �α��� �޽������� Ȯ��
					GUIView.view.addMessage("Ŭ���̾�Ʈ�κ��� ���۹��� ���ڿ� �Ľ� : " + parseline[0]);
					line = parseline[1];
					String[] splitted = line.split(":");//:������ ������
					GUIView.view.addMessage("LOGIN DETECTED : "+line);
					if(splitted.length!=3) {//�����̸�, ���̸�, ������ �ִ°�?(���� 3��)
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
					this.room().sendToAll(this, MsgLinker.msgBuild(MsgLinker.LOGINTOKEN, "",this.name.toString()));//�α����� ������ ���� �����鿡�� �˸�
					this.talk(MsgLinker.msgBuild(MsgLinker.USERLISTTOKEN, this.room().userNameListToString()));
					//���� ����Ʈ ����
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
				GUIView.view.addMessage(this.name + " Ŭ���̾�Ʈ�κ��� ���۹��� ���ڿ� : " + line);
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
