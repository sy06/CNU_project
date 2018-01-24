
package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.StringTokenizer;

public class Server {
   public static void main(String args[]) throws IOException, SocketException {
      ServerSocket serverSocket = null;
      Socket clientSocket = null;
      MulticastSocket ms = null;
      PrintWriter out = null;
      BufferedReader in = null;
      String inputLine = null;
      double longitude = 0;
      double latitude = 0;
      String nosun = null;
      DatagramSocket ds = null;
      DatagramPacket dp = null;

      while (true) {
         serverSocket = new ServerSocket(5555);
         try {
            clientSocket = serverSocket.accept();
            System.out.println("클라이언트 연결");

            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            inputLine = in.readLine();
            String word[] = new String[3];
            int count = 0;
            System.out.println("클라이언트로부터 받은 문자열 : " + inputLine);
            out.println(inputLine);

            if (inputLine != null) { // null이 아닐때까지 (파일의 끝까지 읽기)
               StringTokenizer parser = new StringTokenizer(inputLine, "|");
               while (parser.hasMoreTokens()) {
                  word[count] = parser.nextToken(); // 단어를 받아옴
                  count++;
               }
               latitude = Double.parseDouble(word[0]);
               longitude = Double.parseDouble(word[1]);
               nosun = word[2];
            }
            System.out.println("latitude:" + latitude + ",longitude:" + longitude + ",nosun:" + nosun);

            // // 멀티캐스트시작
            // ms = new MulticastSocket();
            // ms.setTimeToLive(225);
            // InetAddress addr = InetAddress.getByName("224.0.0.2");
            // ms.joinGroup(addr);
            // String msg = inputLine;
            // if (msg != null && latitude != 0.0 && longitude != 0.0) {
            // DatagramPacket dp = new DatagramPacket(msg.getBytes(), msg.getBytes().length,
            // addr, 1200);
            // ms.send(dp);
            // System.out.println("정보 전달 완료\n");
            // } else {
            // System.out.println("전달할 정보가 없습니다.(정보전달실패)\n");
            // }
            // if (ms != null) {
            // ms.close();
            // }

            try {
               String str = inputLine;
               byte b[] = new byte[1024];
               b = str.getBytes();
               ds = new DatagramSocket(7000);
               InetAddress ia = InetAddress.getByName("168.188.129.143");
               dp = new DatagramPacket(b, b.length);
               System.out.println("s:send packet" + str.getBytes().length);
               ds.send(dp);
               System.out.println("전송완료");
               if (ds != null)
                  ds.close();
               if (dp != null)
                  dp = null;
            } catch (Exception e) {
               e.printStackTrace();
            }
            if (in != null) {
               in.close();
            }
            if (out != null) {
               out.close();
            }

         } catch (SocketException e) {
            clientSocket.close();
         }

         if (serverSocket != null) {
            serverSocket.close();
         }
      }

   }
}
