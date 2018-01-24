package com.cnu_bus_alarm.cnu.network.network_client;

import java.io.IOException;
import java.util.Random;

public class DummyListener extends Thread implements BackgroundListener {
	CallbackEvent ev;
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
	public DummyListener(Connection c, CallbackEvent ev) {
		this.conn = c;
		this.ev = ev;
		this.exec = true;
		this.msgList = new LinkedQueue<String>();
	}
	@Override
	public void run() {
		while(exec) {
			if(this.msgList.isEmpty()) {
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				//*for Debug***
				int i = r.nextInt(4);
				if(i==1) {
					System.out.println("message added : "+this.msgList.isEmpty());
					msgList.add("[DUMMY]message"+r.nextInt(100));
					msgList.DEBUG_printAll();
				}
				//************
			}
			else {
				ev.run(msgList.pop());
			}
		}
	}
	public void start() {
		super.start();
	}
	@Override
	public void callback(CallbackEvent ev) {
		this.ev = ev;
	}
	@Override
	public void stopListen() {
		this.exec = false;
		
	}
}
