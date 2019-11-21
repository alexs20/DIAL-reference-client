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
import java.util.Date;

public class DiscoveredDevice {
	private UPnPDescriptionResponce descriptionResponce = null;
	private SSDPMSearchResponce mSearchResponce = null;
	private URL applicationURL = null;
	private Date updatedAt = null;

	public URL getApplicationURL() {
		return applicationURL;
	}

	protected void setApplicationURL(URL applicationURL) {
		this.applicationURL = applicationURL;
	}

	public UPnPDescriptionResponce getDescriptionResponce() {
		return descriptionResponce;
	}

	protected void setDescriptionResponce(UPnPDescriptionResponce descriptionResponce) {
		this.descriptionResponce = descriptionResponce;
	}

	public SSDPMSearchResponce getMSearchResponce() {
		return mSearchResponce;
	}

	protected void setMSearchResponce(SSDPMSearchResponce mSearchResponce) {
		this.mSearchResponce = mSearchResponce;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	protected void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public String toString() {
		return "DiscoveredDevice [descriptionResponce=" + descriptionResponce + ", mSearchResponce=" + mSearchResponce + ", applicationURL=" + applicationURL
				+ ", updatedAt=" + updatedAt + "]";
	}

}
