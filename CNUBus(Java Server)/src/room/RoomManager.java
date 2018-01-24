package room;

import java.util.Iterator;
import java.util.LinkedList;

import network.Connection;

public class RoomManager {
	private static LinkedList<Room> rooms= new LinkedList<Room>();
	
	private RoomManager() {}
	private static LinkedList<Room> rooms() {
		return RoomManager.rooms;
	}
	public static int size() {
		return rooms().size();
	}
	public static Room find(String roomId, String pwd) {
		Iterator<Room> it = rooms.iterator();
		while(it.hasNext()) {
			Room room = it.next();
			if(room.roomId().equals(roomId) && room.checkpwd(pwd) ) {
				return room;
			}
		}
		return null;
	}
	public static boolean findAndRemove(Connection conn) {
		return conn.room().deleteConn(conn);
	}
	public static Room existRoomEnter(Connection connection, Room room) {
		room.addConn(connection);
		return room;
	}
	public static Room makeAndEnter(Connection connection, String id, String pwd) {
		Room room = new Room(id, pwd);
		rooms.add(room);
		rooms.get(0).addConn(connection);
		return room;
	}
	public static void removeRoom(Room room) {
		RoomManager.rooms().remove(room);
	}
}
