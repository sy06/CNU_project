package com.cnu_bus_alarm.cnu.network.test;
import java.net.*;
import java.util.Random;
import java.io.*;
 
public class EchoClient {
       public static void run(){
             try{
                   
                    // 1. ������ IP�� ������ ���� ��Ʈ ��(10001)�� ���ڷ� �־� socket ����

	          	   InetAddress ipaddress = InetAddress.getByName("localhost");
	          	   System.out.println(ipaddress+" �� ����");
	          	   Socket sock = new Socket(ipaddress, 13461);
                    BufferedReader keyboard =
                           new BufferedReader(new InputStreamReader(System.in));
                   
                    // 2. ������ Socket���κ��� InputStream�� OutputStream�� ����
                    OutputStream out = sock.getOutputStream();
                    InputStream in = sock.getInputStream();
                   
                    // 3. InputStream�� BufferedReader �������� ��ȯ
                    //    OutputStream�� PrintWriter �������� ��ȯ
                    PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));
                   
                    // 4. Ű����κ��� �� �پ� �Է¹޴� BufferedReader ��ü ����
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                   
                    String line = null;

                    ReadLineThread rt;
                    rt = new ReadLineThread(br,sock);
                    Thread t = new Thread(rt);
                    t.start();
                    Random r = new Random();
                    pw.println("LOGIN:"+"[DUMMYID]"+r.nextInt(100)+":ASDF:ASDF");
                    pw.flush();
                    // 5. Ű����κ��� �� ���� �Է¹���
                    while((line = keyboard.readLine()) != null){
                           if(line.equals("quit")) break;
                           if(t.getState()==Thread.State.WAITING) {
                        	   t.notify();
                        	   System.out.println(ipaddress+" �� ����");
                        	   sock = new Socket(ipaddress, 13461);
                               out = sock.getOutputStream();
                               in = sock.getInputStream();
                               pw = new PrintWriter(new OutputStreamWriter(out));
                               br = new BufferedReader(new InputStreamReader(in));
                           }
                           // 6. PrintWriter�� �ִ� println() �޼ҵ带 �̿��� �������� ����
                           pw.println(line);
                           pw.flush();
                          
                           // 7. ������ �ٽ� ��ȯ�ϴ� ���ڿ��� BufferedReader�� �ִ�
                           //    readLine()�� �̿��ؼ� �о����
                    }
                    
                    t.wait();
                    pw.close();
                    br.close();
                    sock.close();
             }catch(Exception e){
            	 e.printStackTrace();
             }
       }
}