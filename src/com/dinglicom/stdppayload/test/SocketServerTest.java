package com.dinglicom.stdppayload.test;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.quartz.impl.jdbcjobstore.FiredTriggerRecord;


public class SocketServerTest {

	public static void main(String[] args) {
		ServerSocket serverSocket;
		byte[] data = new byte[50];
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte)i;
		}
		try {
			serverSocket = new ServerSocket(8585);
			while (true) {  
				Socket socket = null;  
				try {  
					//接收客户连接,只要客户进行了连接,就会触发accept();从而建立连接  
					socket = serverSocket.accept();  
					socket.getOutputStream().write(data);
//					DataInputStream reader = new DataInputStream(socket.getInputStream());  
//					byte readLine = reader.readByte();
//			            while (true) {  
//			                if (readLine >= 0) {  
//			                    System.out.println(readLine);  
//			                    readLine = reader.readByte();  
//			                } else {  
//			                    break;  
//			                }  
//			            }  
//					socket.close();
				} catch (Exception e) {  
					e.printStackTrace();  
				}  
			}  
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
