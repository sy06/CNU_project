package com.cnu_bus_alarm.cnu.network.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class ReadLineThread implements Runnable {
	BufferedReader br;
	Socket sock;

	public ReadLineThread(BufferedReader br, Socket sock) {
		this.br = br;
		this.sock = sock;
	}

	public void run() {
		while (true) {
			try {
				if(br.ready()) {
					String echo = br.readLine();
					System.out.println("�����κ��� ���޹��� ���ڿ� :" + echo);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}