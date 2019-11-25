import com.wolandsoft.dial.client.discovery.DeviceWOLCallback;
import com.wolandsoft.dial.client.discovery.DeviceWOLService;
import com.wolandsoft.dial.client.discovery.DiscoveredDevice;
import com.wolandsoft.dial.client.discovery.SSDPMSearchService;
import com.wolandsoft.dial.client.discovery.UPnPDescriptionListener;
import com.wolandsoft.dial.client.discovery.UPnPDescriptionService;

public class DiscoveryControllerWOL {

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

		DeviceWOLService wol = new DeviceWOLService.Builder()
				.build();
		
		SSDPMSearchService ss = new SSDPMSearchService.Builder()
				.withListener(ds)
				.withListener(wol)
				.build();

		wol.wakeup("10:dd:b1:c9:00:e4", 10, null, 
				"uuid:4248037a-d4b5-6c7b-0000-000062dc019d::urn:dial-multiscreen-org:service:dial:1",
				new DeviceWOLCallback() {

					@Override
					public void onDeviceWOLSucceed() {
						System.out.println("onDeviceWOLSucceed");
					}

					@Override
					public void onDeviceWOLFail() {
						System.out.println("onDeviceWOLFail");
					}
				});

		Thread.sleep(15000);

		ss.close();
		wol.close();
		ds.close();
	}

}
