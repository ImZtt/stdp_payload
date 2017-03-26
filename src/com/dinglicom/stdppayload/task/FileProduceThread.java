package com.dinglicom.stdppayload.task;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;

import com.dinglicom.stdppayload.util.ByteConvert;
import com.dinglicom.stdppayload.util.DLUtils;
/**
 * 读文件的线程类
 * @author 
 *
 */
public class FileProduceThread extends Thread {
	private static final Log LOG = LogFactory.getLog(FileProduceThread.class); 
	private GetPayloadBySocket client = new GetPayloadBySocket();
	private String filename = "";
	private String date = "";
	private static byte[] wheader = DLUtils.createWireSharkHead();
	
	public String getFilename() {
		return filename;
	}


	public void setFilename(String filename) {
		this.filename = filename;
	}


	public String getDate() {
		return date;
	}


	public void setDate(String date) {
		this.date = date;
	}

    
	@Override
	public void run() {
		// TODO Auto-generated method stub
		File file = new File(this.getFilename());
//		Map<String,List<byte[]>> msisdnmap = new  HashMap<String,List<byte[]>>();
		try {
			//BufferedReader从字符输入流中读取文本，缓冲各个字符，从而实现字符、数组和行的高效读取
			BufferedReader bw = new BufferedReader(new FileReader(file), 1024*1024);
			String line  = null;
			byte[] pcaps = new byte[0];
			int counts=0;
			while((line=bw.readLine()) != null){
				String[] arr = line.split("\\t");
				String xdrid = arr[0];
				String starttime = arr[1];
				String msisdn = arr[2];
				JSONObject json = JSONObject.fromObject(arr);
				String cdr_id=json.getString("cdr_id");
				String data=json.getString("data");
				long st = new Date().getTime();
//				if(!(Pattern.matches("[0-9]*", xdrid)&&Pattern.matches("[0-9]*", starttime)
//						&&Pattern.matches("[0-9]*", msisdn))){
//					continue;
//				}  
				
				counts=counts+1;
				pcaps = client.getPayload(xdrid, starttime,counts);
				if(pcaps==null||pcaps.length==0){
					continue;
				}
//				LOG.info(xdrid+","+starttime+" return "+pcaps.length+",used "+(new Date().getTime()-st)+ " ms");
				this.writepayload(pcaps, msisdn, xdrid, starttime);
//				if(!msisdnmap.containsKey(msisdn)){
//					msisdnmap.put(msisdn, new ArrayList<byte[]>());
//				}
//				msisdnmap.get(msisdn).add(pcaps);
			}
			
			
//			this.writePcaps(msisdnmap);
			bw.close();
		} catch (FileNotFoundException e) {
			LOG.warn(e,e);
		} catch (IOException e) {
			LOG.warn(e,e);
		}
		LOG.info(this.getFilename()+"  ending...");
	}
	/**
	 * 单条xdrid记录写入pcap
	 * @param pcaps
	 * @param msisdn
	 * @param xdrid
	 * @param stime
	 */
	private void writepayload(byte[] pcaps,String msisdn,String xdrid,String stime){
		String output = FileReadTask.p.getProperty("pcaps.file.path");
		String dir = output+File.separator+msisdn+File.separator+this.getDate();
		File file = new File(dir);
		if(!file.exists()){
			file.mkdirs();
		}
		String pcapname = dir+File.separator+xdrid+"_"+stime+".pcap";
		FileOutputStream fos;
//		LOG.error("ccc");
		try {
			fos = new FileOutputStream(pcapname, true);
			DataOutputStream out = new DataOutputStream(new BufferedOutputStream(fos));
			out.write(wheader);
			out.write(pcaps);
			out.close();
			LOG.info(pcapname+" writed..."); 
		} catch (FileNotFoundException e1) {
			LOG.warn(e1,e1);
		} catch (IOException e1) {
			LOG.warn(e1,e1);
		}
	}
//追加写？
	private void writePcaps(Map<String,List<byte[]>> msisdnmap){
		String output = FileReadTask.p.getProperty("pcaps.file.path");

		Iterator<Map.Entry<String,List<byte[]>>> it = msisdnmap
				.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry<String,List<byte[]>> e = (Map.Entry<String,List<byte[]>>) it
					.next();
			String dir = output+File.separator+this.getDate();
			File file = new File(dir);
			if(!file.exists()){
				file.mkdirs();
			}
			String pcapname = dir+File.separator+e.getKey()+".pcap";
			boolean header = false;
			if(!new File(pcapname).exists()){
				header = true;
			}
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(pcapname, true);
				DataOutputStream out = new DataOutputStream(new BufferedOutputStream(fos));
				if(header){
					out.write(wheader);
				}
				for (int i = 0; i <e.getValue().size(); i++) {
					out.write(e.getValue().get(i));
				}
				out.close();
				LOG.info(pcapname+" writed..."); 
			} catch (FileNotFoundException e1) {
				LOG.warn(e1,e1);
			} catch (IOException e1) {
				LOG.warn(e1,e1);
			}
			
		}
	}
	
}
