import com.wolandsoft.dial.client.discovery.SSDPMSearchListener;
import com.wolandsoft.dial.client.discovery.SSDPMSearchResponce;
import com.wolandsoft.dial.client.discovery.SSDPMSearchService;

public class DiscoveryMSearch {

	public static void main(String[] args) throws Exception {

		SSDPMSearchListener listener = new SSDPMSearchListener() {

			@Override
			public void onDeviceDiscovery(SSDPMSearchResponce message) {
				System.out.println("SSDPMSearchResponce: " + message);

			}
		};

		SSDPMSearchService ds = new SSDPMSearchService.Builder()
				.withListener(listener)
				.build();

		Thread.sleep(60000);

		ds.close();

	}

}
