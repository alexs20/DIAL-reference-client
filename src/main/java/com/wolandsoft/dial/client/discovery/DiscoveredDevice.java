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
