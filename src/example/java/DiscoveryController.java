import com.wolandsoft.dial.client.discovery.DiscoveredDevice;
import com.wolandsoft.dial.client.discovery.SSDPMSearchService;
import com.wolandsoft.dial.client.discovery.UPnPDescriptionListener;
import com.wolandsoft.dial.client.discovery.UPnPDescriptionService;

public class DiscoveryController {

	public static void main(String[] args) throws Exception {
		UPnPDescriptionListener controllerListener = new UPnPDescriptionListener() {

			@Override
			public void onDeviceDiscovered(DiscoveredDevice device) {
				System.out.println("onDeviceDiscovered " + device);
				
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
