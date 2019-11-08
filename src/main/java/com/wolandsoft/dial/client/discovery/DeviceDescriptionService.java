package com.wolandsoft.dial.client.discovery;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DeviceDescriptionService implements MSearchServiceListener{

	private Map<String, DiscoveredDevice> devicesMap = Collections.synchronizedMap(new HashMap<String, DiscoveredDevice>());
	private final long revalidationPeriod;
	private final ScheduledExecutorService scheduler;
	
	private DeviceDescriptionService(long revalidationPeriod) {
		this.revalidationPeriod = revalidationPeriod;
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
//        try {
//            URL url = new URL(getAPIFullPath(mApiPath));
//            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
//            conn.setConnectTimeout(mConnectTimeout);
//            conn.setReadTimeout(mReadTimeout);
//            conn.setRequestMethod(mMethod);
//            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
//            if (mToken != null) {
//                conn.setRequestProperty("Authorization", "Bearer " + mToken);
//            }
//            conn.connect();
//            if (mInputObj != null) {
//                OutputStream os = conn.getOutputStream();
//                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
//                osw.write(mGson.toJson(mInputObj));
//                osw.flush();
//                osw.close();
//            }
//            int errorCode = conn.getResponseCode();
//            if (errorCode == mExpectedCode) {
//                Scanner s = new Scanner(conn.getInputStream()).useDelimiter("\\A");
//                String data = s.hasNext() ? s.next() : "";
//                s.close();
//                if (mExpectedClass != Void.class && data.trim().length() > 0) {
//                    mOutputObj = new Gson().fromJson(data, mExpectedClass);
//                }
//            } else {
//                // trying de get error message
//                Scanner s = new Scanner(conn.getErrorStream()).useDelimiter("\\A");
//                String data = s.hasNext() ? s.next() : "";
//                s.close();
//                mError = parseError(errorCode, data);
//            }
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
//        } catch (Exception e) {
//            LogEx.w(e);
//            mError = new APIError();
//            mError.setCode(0);
//            mError.setName("UNKNOWN");
//        }
//        return null;
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
		//scheduler.
		
	}
	
}
