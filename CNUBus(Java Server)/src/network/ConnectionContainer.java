package network;

import java.util.Iterator;
import java.util.LinkedList;

import room.RoomManager;


public class ConnectionContainer {
	private static LinkedList<Connection> socketList = new LinkedList<Connection>();//접속자 많아질 시 세마포 필요..
	
	private ConnectionContainer() {}
	public static int size() {
		return socketList.size();
	}
	public static void addConnection(Connection conn) {
		socketList().add(conn);
	}
	public static boolean removeConnection(Connection conn) {
		RoomManager.findAndRemove(conn);
		return socketList().remove(conn);
	}
	public static LinkedList<Connection> socketList() {
		return socketList;
	}
	public static Connection getConnectionWithIdx(int index) {
		return ConnectionContainer.socketList().get(index);
	}
	public static int getConnectionIdx(Connection cnx) {
		return socketList().indexOf(cnx);
		}
	public static void disconnectAll() {
		Iterator<Connection> it = socketList().iterator();
		while(it.hasNext()) {
			it.next().setDisconnected();
		}
	}
}
