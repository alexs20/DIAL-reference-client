package com.wolandsoft.dial.client.discovery;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

public class DeviceDescriptionService implements MSearchServiceListener{

	private Map<String, DiscoveredDevice> devicesMap = Collections.synchronizedMap(new HashMap<String, DiscoveredDevice>());
	private final long revalidationPeriod;
	private final ScheduledExecutorService scheduler;
	private final int connectTimeout;
	private final int readTimeout;
	
	private DeviceDescriptionService(long revalidationPeriod, int connectTimeout, int readTimeout) {
		this.revalidationPeriod = revalidationPeriod;
		this.connectTimeout = connectTimeout;
		this.readTimeout = readTimeout;
		this.scheduler = Executors.newScheduledThreadPool(1);
		
		scheduler.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
			  Set<String> s = devicesMap.keySet();
			  synchronized (devicesMap) { 
			      Iterator<String> i = s.iterator();
			      while (i.hasNext()) {
			    	  String key = i.next();
			    	  DiscoveredDevice device = devicesMap.get(key);
			    	  if ((device.getUpdatedAt().getTime() + revalidationPeriod * 1000) < System.currentTimeMillis()) {
			    		  revalidateDevice(device);
			    	  }
			      }
			  }
				
			}}, revalidationPeriod, revalidationPeriod, TimeUnit.SECONDS);
	}

	private void revalidateDevice(DiscoveredDevice device) {
		try {
            URL url = device.getMSearch().getLocation();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            conn.connect();
            int retCode = conn.getResponseCode();
            if (retCode == 200) {
            	try (Scanner s = new Scanner(conn.getInputStream()).useDelimiter("\\A")) {
            		String data = s.hasNext() ? s.next().trim() : "";
            		s.close();
            		if (data.length() > 0) {
            			DeviceDescriptionResponce devDesc = DeviceDescriptionParser.parse(data);
            			if (devDesc != null) {
            				device.setDeviceDescritption(devDesc);
            				device.setUpdatedAt(new Date());
            			}
            		}
            	}
//            } else {
//                // trying de get error message
//                Scanner s = new Scanner(conn.getErrorStream()).useDelimiter("\\A");
//                String data = s.hasNext() ? s.next() : "";
//                s.close();
//                mError = parseError(errorCode, data);
            }
//        } catch (SocketTimeoutException ste) {
//            mError = new APIError();
//            mError.setCode(1);
//            mError.setName("TIMEOUT");
//        } catch (InterruptedIOException ie) {
//            Thread.currentThread().interrupt();
//            LogEx.w(ie);
//            mError = new APIError();
//            mError.setCode(0);
//            mError.setName("UNKNOWN");
        } catch (Exception e) {
        	// ignore
        }
	}
	
	@Override
	public void onDeviceDiscovery(MSearchResponce message) {
		DiscoveredDevice device = devicesMap.get(message.getUsn());
		if (device == null) {
			device = new DiscoveredDevice();
			device.setMSearch(message);
			revalidateDevice(device);
		} else {
			device.setUpdatedAt(new Date());
		}
	}
	
}
