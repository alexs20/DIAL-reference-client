import java.net.URL;

import com.wolandsoft.dial.client.discovery.DiscoveredDevice;
import com.wolandsoft.dial.client.discovery.SSDPMSearchService;
import com.wolandsoft.dial.client.discovery.UPnPDescriptionListener;
import com.wolandsoft.dial.client.discovery.UPnPDescriptionService;
import com.wolandsoft.dial.client.rest.ApplicationLaunchCallable;
import com.wolandsoft.dial.client.rest.ApplicationQueryCallable;
import com.wolandsoft.dial.client.rest.ApplicationQueryResponce;
import com.wolandsoft.dial.client.rest.ApplicationStopCallable;

public class ManageApp {

	public static void main(String[] args) throws Exception {
		UPnPDescriptionListener controllerListener = new UPnPDescriptionListener() {

			@Override
			public void onDeviceDiscovered(DiscoveredDevice device) {
				System.out.println("onDeviceDiscovered " + device);
				if (device.getDescriptionResponce().getFriendlyName().equals("DIAL server sample")) {
					try {
						ApplicationQueryCallable aq = new ApplicationQueryCallable.Builder()
								.withApplicationURL(device.getApplicationURL())
								.withApplicationName("YouTube")
								.build();
						System.out.println("Querying YouTube app");
						ApplicationQueryResponce aqr = aq.call();
						System.out.println("appQuery result " + aqr);

						ApplicationLaunchCallable al = new ApplicationLaunchCallable.Builder()
								.withApplicationURL(device.getApplicationURL())
								.withApplicationName("YouTube")
								.build();
						System.out.println("Launching YouTube app");
						URL appInstanceUrl = al.call();
						System.out.println("appInstance " + appInstanceUrl);

						ApplicationStopCallable as = new ApplicationStopCallable.Builder()
								.withApplicationInstanceURL(appInstanceUrl)
								.build();
						System.out.println("Stopping YouTube app");
						as.call();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onDeviceRemoved(DiscoveredDevice device) {
				System.out.println("onDeviceRemoved " + device);

			}
		};
		
		UPnPDescriptionService ds = new UPnPDescriptionService.Builder()
				.withListener(controllerListener)
				.build();

		SSDPMSearchService ss = new SSDPMSearchService.Builder()
				.withListener(ds)
				.build();


		Thread.sleep(15000);

		ss.close();
		ds.close();
	}

}
