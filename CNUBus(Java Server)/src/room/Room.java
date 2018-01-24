package room;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import message.MsgLinker;
import network.Connection;
import view.GUIView;

public class Room extends Thread {
	private ArrayList<Connection> connectionList;
	private String roomId;
	private String pwd;
	private Queue<Message> msgList;
	private RoomThread roomThread;
	
	private boolean runningState = false;
	
	
	//getter/setter

	public ArrayList<Connection> connectionList(){
		return this.connectionList;
	}
	public String userNameListToString() {
		Iterator<Connection> it = this.connectionList().iterator();
		StringBuffer sb = new StringBuffer();
		do {
			sb.append(it.next().getUserName());
			sb.append(",");
			
		}while(it.hasNext());
		return sb.toString();
	}
	//methods
	public Room(String roomId, String pwd) {
		this.roomId = roomId;
		this.pwd = pwd;
		this.connectionList = new ArrayList<Connection>();
		this.msgList = new LinkedBlockingQueue<Message>();
	}
	public boolean checkpwd(String pwd) {
		if(this.pwd.equals(pwd)) {
			return true;
		}
		return false;
	}

	public String roomId() {
		return this.roomId;
	}
	
	public boolean addConn(Connection conn) {
		boolean b = this.connectionList().add(conn);
		for(int i = 0;i<connectionList().toArray().length;i++) {
			GUIView.view.addMessage(connectionList().get(i).getUserName());
		}
		return b;
	}
	public boolean deleteConn(Connection conn) {
		boolean output = connectionList().remove(conn);
		if(connectionList().isEmpty()) {
			RoomManager.removeRoom(this);
		}
		return output;
	}
	public boolean sendToAll(Connection conn, String msg) {
		Iterator<Connection> it = this.connectionList().iterator();
							System.out.println(""+connectionList().size());
		boolean returnValue = true;
		do {
			Connection sendingConn = it.next();
			Message message = new Message(sendingConn, msg);
							System.out.println(sendingConn.getUserName());
			if(true /*sendingConn != conn*/) {//모든 connection에게 전송
				if(!this.sendMessageTo(message)) {
					returnValue = false;
				}
			}
		}while(it.hasNext());
		return returnValue;
	}
	private boolean sendMessageTo(Message m) {
		msgList.add(m);
		while(this.runningState || roomThread!=null) {
			try {
				roomThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		{
			this.runningState = true;
			GUIView.view.addMessage("Roomthread start");
			roomThread = new RoomThread();
			roomThread.start();
			return runningState;
		}
	}
	private class RoomThread extends Thread{
		public RoomThread() {
		}
		public void run() {
			while(!msgList.isEmpty()) {
				msgList.remove().talk();
			}
			GUIView.view.addMessage("Roomthread end");
			runningState = false;
			roomThread = null;
			return;
		}
		
	}
	private class Message{

		private String name;
		private String msg;
		public Connection connection;
		public Message(Connection conn, String msg) {
			this.connection = conn;
			this.name = conn.getUserName();
			this.msg = msg;
		}
		public boolean talk() {
			GUIView.view.addMessage("to:"+this.connection().getUserName()+" / msg:"+this.msg());
			return this.connection().talk(this.msg());
		}
		public Connection connection() {
			return this.connection;
		}
		public String name() {
			return this.name;
		}
		public String msg() {
			return this.msg;
		}
	}
}
