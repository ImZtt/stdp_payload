package com.dinglicom.stdppayload.task;



import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class FileContorller {
	 
	private static final Log LOG = LogFactory.getLog(FileContorller.class);
	/**
	 * 查询
	 */
	@RequestMapping(value = "/queryAll", method = RequestMethod.POST)
	public void subscriptQuery(){
//		FileReadTask frt = new FileReadTask();
//		frt.getXDRIDFromFile();
		
	}	
	
} 
