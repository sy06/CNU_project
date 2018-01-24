package network;

import java.net.ServerSocket;
import java.net.Socket;

import view.GUIView;

public class AcceptThread implements Runnable{
	ServerSocket serverSocket;
	Socket sock;
	public AcceptThread(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}
	public void run(){
		GUIView.view.addMessage("Wating Connect ..");
		while(true) {
			try {
				sock = serverSocket.accept();
				GUIView.view.addMessage("connected Client : "+sock);
				Connection conn = new Connection(sock);
				ConnectionContainer.addConnection(conn);
				conn.start();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
