package com.dinglicom.stdppayload.task;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dinglicom.stdppayload.util.DLUtils;

public class FileReadTask {
	private static final Log LOG = LogFactory.getLog(FileReadTask.class);
	public static Properties p = DLUtils.getProperties("fileQueryConfig.properties");
	//初始化日期对象为null
	public static Calendar nexthour=null;
	
	public void getXDRIDFromFile(){
		ExecutorService exs=Executors.newFixedThreadPool(20);
		String datedir;
		DateFormat df = new SimpleDateFormat("yyyyMMddHH");
		if(nexthour ==null){
			//第一次执行此任务以上一个小时的数据目录为执行时间
			nexthour =Calendar.getInstance();
			nexthour.add(Calendar.HOUR, -1);
			
			datedir=df.format(nexthour.getTime());
			//确定启动数据目录后 更新加1小时
			nexthour.add(Calendar.HOUR,1);
		}else{
			datedir=df.format(nexthour.getTime());
			nexthour.add(Calendar.HOUR,1);
		}
//		Calendar calendar = Calendar.getInstance();
//		calendar.add(Calendar.HOUR, -1);
//		
//		String datedir = df.format(calendar.getTime());
		LOG.info(datedir+" task begin...");
		String xdrid_dir = p.getProperty("xdrid.file.path");
		String path = xdrid_dir+File.separator+datedir;
		File dir = new File(path);
		if(!dir.exists()){
			LOG.warn(path+" not exist ! ! !");
			return;
		}
		File[] dirs = dir.listFiles();
		if(dirs== null || dirs.length<=0){
			LOG.warn(path+"has no files ! ! !");
			return;
		}
		for (int j = 0; j < dirs.length; j++) {
			FileProduceThread thread = new FileProduceThread();
			thread.setDate(datedir);
			//getAbsolutePath获取文件夹全路径
			thread.setFilename(dirs[j].getAbsolutePath());
			//execute(Runnable x) 没有返回值。可以执行任务，但无法判断任务是否成功完成
			exs.execute(thread);
//			LOG.error("aaa");
		}
		//只是关闭没有任务的  或者挂起的线程  有任务正在跑的线程不会被关闭
		exs.shutdown();
	}
	

}
