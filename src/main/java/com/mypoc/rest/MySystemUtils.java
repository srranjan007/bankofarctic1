package com.mypoc.rest;

public class MySystemUtils {
		  
		  private MySystemUtils() {}
		  
		 
		  public static long getPID() {
			  
			 long pid =  0;
		    String processName =
		      java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
		    
		    if (null != processName)
		    	pid = Long.parseLong(processName.split("@")[0]);
		    
		    return  pid;
		  }
		  
}
