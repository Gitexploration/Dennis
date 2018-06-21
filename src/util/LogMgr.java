package util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class LogMgr {
	
	public static final String common="common";
	public static final String connector="connector";
	public static final String mail="mail";
	

	private static Logger logger ;
	public static Logger getLogger(String str){
		logger= LogManager.getLogger(str);
		return logger;
	}
}
