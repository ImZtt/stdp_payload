package com.dinglicom.stdppayload.task;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dinglicom.stdppayload.util.ByteConvert;
import com.dinglicom.stdppayload.util.DLUtils;

public class GetPayloadBySocket {
	private static final Log LOG = LogFactory.getLog(GetPayloadBySocket.class);
	private String serverIP = FileReadTask.p.getProperty("stdp.server.ip");
	private String severport = "";
	private String s1uFlag = "0b";
	public byte[] getPayload(String xdrid,String begintime,int counts){
//		LOG.error("getpay");
		Socket client = null;  
        DataInputStream in = null;  
        DataOutputStream out = null;  
        byte[] pcaps = null;
        byte[] payload = new byte[0];
        
        try {  
        	if(counts%2==0){
            	severport=FileReadTask.p.getProperty("stdp.server.port");
//            	LOG.error("port");
            }else{
            	severport=FileReadTask.p.getProperty("stdp.server.port1");
//            	LOG.error("port1");
            }
            client = new Socket(serverIP,Integer.parseInt(severport));  
            client.setSoTimeout(30000);  
            // ����  
            out = new DataOutputStream(client.getOutputStream());  
            byte[] xdridarr = DLUtils.hexStringToBytes(s1uFlag+xdrid);
            byte[] time = ByteConvert.longToBytes(Long.parseLong(begintime));
            byte[] sendmsg = DLUtils.appendArray(xdridarr, time); 
            out.write(sendmsg);  
            out.flush();  
            
            // ����  
            in = new DataInputStream(client.getInputStream()); 
            byte[] bts = new byte[10000];  
            int totalLen = 0, len = 0;  
            while ((len = in.read(bts, totalLen, 100)) != -1) {  
                totalLen += len;  
                if(totalLen>=bts.length){
                	bts = DLUtils.appendArray(bts, new byte[10000]);
                }
            }
            
//            LOG.debug("response length:"+totalLen);
            if(bts[0]!=1){
            	LOG.info("not found:"+xdrid+","+begintime);
            	return null;
            }
            byte[] leng = DLUtils.getArray(bts, 21, 2);
            int plen = ByteConvert.bytesToUshort(leng);
            payload = DLUtils.getArray(bts, 23, plen);
            pcaps = this.getPcapHeader(payload);
        } catch (Exception e) {  
            LOG.warn(e,e);
        } finally {  
            try {  
            	in.close();
            	out.close();
                client.close();  
            } catch (IOException e1) {  
            	LOG.warn(e1,e1);
            }  
        } 
        return pcaps;
	}
	
	private byte[] getPcapHeader(byte[] payload){
		int count = 0;
		int len = 0;
		int timestamp = 0;
		long timefloat = 0;
		boolean reverse = false;
		byte[] pcap = new byte[0];
		try {
			while(count < payload.length){
				len = ByteConvert.bytesToUshort(
						DLUtils.getArray(payload,count, 2,reverse));
				timestamp = ByteConvert.bytesToInt(
						DLUtils.getArray(payload,count+6, 4,reverse));
				timefloat = ByteConvert.bytesToUint(
						DLUtils.getArray(payload,count+10, 4,reverse))/1000;///1000000;
				
				byte[] payLoad = DLUtils.getArray(payload, count+14, len-12); 
				byte[] pcapheader = this.createPcapHead(timestamp, timefloat,len-12);
				byte[] tmp = DLUtils.appendArray(pcapheader, payLoad);
				pcap = DLUtils.appendArray(pcap, tmp);
				count += (len+2);
				if(count > payload.length){
					LOG.warn("payload length is error!!!");
				}
			}
		} catch (Exception e) {
			LOG.warn(e,e);
		}
		return pcap;
	}
	/**
	 * ���pcap��ͷ
	 * @return
	 */
	private byte[] createPcapHead(long timestamp,long timefloat, int length){
		byte[] sec = DLUtils.arrReverse(ByteConvert.uintToBytes(timestamp));
		byte[] usec = DLUtils.arrReverse(ByteConvert.uintToBytes(timefloat));
		byte[] caplen = DLUtils.arrReverse(ByteConvert.intToBytes(length));
		byte[] len = DLUtils.arrReverse(ByteConvert.intToBytes(length));
		byte[] times = DLUtils.appendArray(sec,usec);
		byte[] lens = DLUtils.appendArray(caplen,len);
		return DLUtils.appendArray(times, lens);
	}

	
	
}
