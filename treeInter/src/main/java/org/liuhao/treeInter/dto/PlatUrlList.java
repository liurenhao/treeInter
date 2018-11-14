package org.liuhao.treeInter.dto;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

public class PlatUrlList {
	
	@XStreamImplicit(itemFieldName="urlList")
	public List<PlatUrl> platUrls = new ArrayList<PlatUrl>();

	/**
	 * @return the platUrls
	 */
	public List<PlatUrl> getPlatUrls() {
		return platUrls;
	}

	/**
	 * @param platUrls the platUrls to set
	 */
	public void setPlatUrls(List<PlatUrl> platUrls) {
		this.platUrls = platUrls;
	}


	
}
