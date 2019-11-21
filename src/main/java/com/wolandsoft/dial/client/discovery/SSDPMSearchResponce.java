/*
    Copyright © 2019 Alexander Shulgin
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
        http://www.apache.org/licenses/LICENSE-2.0
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/
package com.wolandsoft.dial.client.discovery;

import java.net.URL;

public class SSDPMSearchResponce {
	private URL location = null;
	private String usn = null;
	private String wakeupMac = null;
	private Integer wakeupTimeout = null;

	public URL getLocation() {
		return location;
	}

	protected void setLocation(URL location) {
		this.location = location;
	}

	public String getUsn() {
		return usn;
	}

	protected void setUsn(String usn) {
		this.usn = usn;
	}

	public String getWakeupMac() {
		return wakeupMac;
	}

	protected void setWakeupMac(String wakeupMac) {
		this.wakeupMac = wakeupMac;
	}

	public Integer getWakeupTimeout() {
		return wakeupTimeout;
	}

	protected void setWakeupTimeout(Integer wakeupTimeout) {
		this.wakeupTimeout = wakeupTimeout;
	}

	@Override
	public String toString() {
		return "SSDPMSearchResponce [location=" + location + ", usn=" + usn + ", wakeupMac=" + wakeupMac + ", wakeupTimeout=" + wakeupTimeout + "]";
	}
}
