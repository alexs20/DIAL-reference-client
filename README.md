**Reference implementation of DIAL 2.2 client**

For more details about the DIAL protocol specification please visit [dial-multiscreen.org](http://www.dial-multiscreen.org/)

This library was build with keeping the Android support in mind and therefore has no external libraries linked.

Please note that when using in Android app the "android.permission.INTERNET" permission is required.


**How to discovery**

In order to run devices discovery code you need to use classes from "com.wolandsoft.dial.client.discovery" package which contains a minimal set of modules that could be used to build the discovery engine.

The first and main service is a "SSDPMSearchService".
It produces a broadcast messages every 10 seconds, receives responses, parses them and delivers them to "SSDPMSearchListener".
See the ["DiscoveryMSearch"](https://raw.githubusercontent.com/alexs20/DIAL-reference-client/development/src/example/java/DiscoveryMSearch.java) example.

As a second step, the data delivered to "SSDPMSearchListener" should be pushed into "UPnPDescriptionService".
That service keep list of currently discovered devices, obtains additional metadata and delivers the final data to "UPnPDescriptionListener".
This service also implements the "SSDPMSearchListener" interface, so it could be passed directly as alistener into "SSDPMSearchService".
See the ["DiscoveryController"](https://raw.githubusercontent.com/alexs20/DIAL-reference-client/development/src/example/java/DiscoveryController.java) example.
 
**How to WOL**

When Wake-On-Lan functionality is required the "DeviceWOLService" service could be used by adding it as a listener to the "SSDPMSearchService". After that the wakeup(...) function should be used to wake the device up. Please see the specification document for the details about parameters.
See the "DiscoveryControllerWOL" example. 

**How to query, launch and stop application**

Package "com.wolandsoft.dial.client.rest" contains Callable classes to help with Querying, Launching and Stopping an application on discovered device.
See the "ManageApp" example for the details. 

This package also has a few helper classes to deal with a hidden system application and to bring the device into Low Power Mode.


