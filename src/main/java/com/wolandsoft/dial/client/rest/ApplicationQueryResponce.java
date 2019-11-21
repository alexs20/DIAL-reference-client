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
package com.wolandsoft.dial.client.rest;

public class ApplicationQueryResponce {

	private String dialVer = null;
	private String name = null;
	private Boolean allowStop = null;
	private String state = null;
	private String linkHref = null;
	private String additionalData = null;

	public String getDialVer() {
		return dialVer;
	}

	protected void setDialVer(String dialVer) {
		this.dialVer = dialVer;
	}

	public String getName() {
		return name;
	}

	public Boolean getAllowStop() {
		return allowStop;
	}

	public String getState() {
		return state;
	}

	protected void setName(String name) {
		this.name = name;
	}

	protected void setAllowStop(Boolean allowStop) {
		this.allowStop = allowStop;
	}

	protected void setState(String state) {
		this.state = state;
	}

	public String getLinkHref() {
		return linkHref;
	}

	protected void setLinkHref(String linkHref) {
		this.linkHref = linkHref;
	}

	public String getAdditionalData() {
		return additionalData;
	}

	protected void setAdditionalData(String additionalData) {
		this.additionalData = additionalData;
	}

	@Override
	public String toString() {
		return "ApplicationQueryResponce [dialVer=" + dialVer + ", name=" + name + ", allowStop=" + allowStop + ", state=" + state
				+ ", linkHref=" + linkHref + ", additionalData=" + additionalData + "]";
	}

}
