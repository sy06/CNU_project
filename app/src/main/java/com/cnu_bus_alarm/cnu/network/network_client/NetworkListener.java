package com.cnu_bus_alarm.cnu.network.network_client;

import java.io.IOException;
import java.util.Random;

public class NetworkListener extends Thread implements BackgroundListener {
	CallbackEvent<String> ev;
	boolean exec;
	LinkedQueue<String> msgList;
	private Connection conn;
	
	//for Debug***
	Random r = new Random();
	//************
	public void joinThread() {
		try {
			super.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public NetworkListener(Connection c, CallbackEvent<String> ev) {
		this.conn = c;
		this.ev = ev;
		this.exec = true;
		this.msgList = new LinkedQueue<String>();
	}
	@Override
	public void run() {
		while(exec) {
			String pop;
			if(this.msgList.isEmpty()) {
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				try {
					if(conn.reader().ready()) {
						msgList.add(conn.reader().readLine());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				//msgList.DEBUG_printAll();
			}
			else {
				pop = msgList.pop();
				if(pop == null) {
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}else {
					ev.run(pop);
				}
			}
		}
	}
	@Override
	public void start() {
		super.start();
	}
	
	@Override
	public void stopListen() {
		this.exec = false;
	}
	@Override
	public void callback(CallbackEvent<String> ev) {
		this.ev = ev;
	}
}
