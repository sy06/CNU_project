package app;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;

import network.AcceptThread;
import network.ConnectionContainer;
import view.GUIView;

public class AppController {
	private ActionListener exitL;
	private ActionListener stopL;
	private ActionListener startL;
	private ActionListener restartL;
	private ServerSocket serverSocket;
	
	public Thread acceptThread;
	
	public void run() {
		try {
			serverSocket = new ServerSocket(5555);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.setListener();
		GUIView.view = new GUIView(exitL, stopL, startL, restartL);
		
	}
	public void setListener() {

		this.exitL = new ActionListener() {
		@Override
			public void actionPerformed(ActionEvent e) {
				try {
					stopL.actionPerformed(e);
				}catch(Exception err) {
					err.printStackTrace();
				}
				System.exit(1);
			}
		};
		this.stopL = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ConnectionContainer.disconnectAll();
				acceptThread.interrupt();
			}
		};
		this.startL = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				acceptThread = new Thread(new AcceptThread(serverSocket));
				acceptThread.start();
				
			}
		};
		this.restartL = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopL.actionPerformed(e);
				startL.actionPerformed(e);
			}
		};
	}
}
