package com.cnu_bus_alarm.cnu.network.network_client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Connection extends Thread {
	// instance variables
	private Socket sock = new Socket();
	private PrintWriter pw;
	private BufferedReader br;

	private String address;
	private int port;

	// Constant
	public Connection(String address, int port) {
		this.address = address;
		this.port = port;
		this.startConnection();
		this.start();
	}

	// public Getter
	public PrintWriter writer() {
		return this.pw;
	}

	public BufferedReader reader() {
		return this.br;
	}

	public String address() {
		return this.address;
	}

	public int port() {
		return this.port;
	}

	// public setter
	public void setAddress(String address) {
		this.address = address;
	}

	public void setPort(int port) {
		this.port = port;
	}

	// public method
	public boolean isConnected() {/// 미구현 상태
		return true;
	}

	public boolean networkStatus() {/// 미구현 상태
		return true;
	}

	public void run() {
		while (this.status() == 1) {
			if (this.writer() != null) {
				this.writer().println("1"+0x00);//연결 유지를 위해
				this.writer().flush();
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public String toString() {
		return this.address + ":" + this.port;
	}

	public boolean startConnection() {
		Connector c = new Connector();
		c.start();
		try {
			c.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (this.status() == 1)
			return true;
		return false;
	}

	public boolean resetConnection() {// 이 클래스를 사용하는 클래스의 Thread 종료가 선행되어야 함.

		Connector c = new Connector();
		c.start();
		try {
			c.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (this.status() == 1)
			return true;
		return false;
	}

	public boolean stopConnection() {// 이 클래스를 사용하는 클래스의 Thread 종료가 선행되어야 함.
		if (status() == 1) {
			try {
				this.pw.close();
				this.br.close();
				this.sock.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	// private method
	private int status() {
		// -1 : Error//0 : not connected//1 : connected//
		if (this.sock.isClosed())
			return 0;
		if (this.sock.isConnected())
			return 1;
		return -1;
	}

	// private class Connector
	private class Connector extends Thread {
		public void run() {
			System.out.println("연결 시작");
			try {
				if (status() == 1) {
					pw.close();
					br.close();
					sock.close();
				}
				// 1. 서버의 IP와 서버의 동작 포트 값(10001)을 인자로 넣어 socket 생성

				InetAddress ipaddress = InetAddress.getByName(address);
				System.out.println(ipaddress + " 에 연결");
				sock = new Socket(ipaddress, port);
				sock.setKeepAlive(true);
				System.out.println("소켓 성공");

				// 2. 생성된 Socket으로부터 InputStream과 OutputStream을 구함
				OutputStream out = sock.getOutputStream();
				InputStream in = sock.getInputStream();

				// 3. InputStream은 BufferedReader 형식으로 변환
				// OutputStream은 PrintWriter 형식으로 변환
				pw = new PrintWriter(new OutputStreamWriter(out));
				System.out.println("writer 생성됨");

				// 4. 키보드로부터 한 줄씩 입력받는 BufferedReader 객체 생성
				br = new BufferedReader(new InputStreamReader(in));
				System.out.println("reader 생성됨");
			} catch(java.net.UnknownHostException e) {
				System.out.println("[ERR] Invalid address");
				e.printStackTrace();
			}catch (Exception e) {
				//e.printStackTrace();이 줄을 활성화하여 Network 시작하기
			}

		}
	}
}
