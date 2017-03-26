package com.dinglicom.stdppayload.test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.dinglicom.stdppayload.util.ByteConvert;
import com.dinglicom.stdppayload.util.DLUtils;

public class SocketTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Socket client = null;  
        DataInputStream in = null;  
        DataOutputStream out = null;  
        try {  
            client = new Socket("127.0.0.1",8585);  
//            InetAddress addr=Inet4Address.getByName(serverIP);  
//            client.connect(new InetSocketAddress(addr,Integer.parseInt(severport)), 20000);  
            // 发送  
            out = new DataOutputStream(client.getOutputStream());  
            byte[] xdridarr = DLUtils.hexStringToBytes(args[0]);
            byte[] s1u = new byte[1];	s1u[0]=11;
            byte[] id = DLUtils.appendArray(s1u, xdridarr);
            byte[] time = ByteConvert.intToBytes(Integer.parseInt(args[1]));
            byte[] retime = DLUtils.appendArray(new byte[4], time);
            byte[] sendmsg = DLUtils.appendArray(id, retime);  
            out.write(sendmsg);  
            out.flush();  
            // 接收  
            in = new DataInputStream(client.getInputStream());  
            byte[] bts = new byte[10000];  
            int totalLen = 0, len = 0;  
            while ((len = in.read(bts, totalLen, 100)) != -1) {  
                totalLen += len;  
                if(totalLen>=bts.length){
                	bts = DLUtils.appendArray(bts, new byte[10000]);
                }
            }
            System.out.println(DLUtils.getHexString(DLUtils.getArray(bts, 0, totalLen)));
        } catch (Exception e) {  
        	e.printStackTrace();
        } finally {  
            try {  
            	if(in!=null)
            		in.close();
            	if(out!=null)
            	out.close();
            	if(client!=null)
                client.close();  
            } catch (IOException e1) {  
            	e1.printStackTrace();
            }  
        } 
	}

}
