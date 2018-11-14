package org.liuhao.treeInter.dto;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

public class SubServiceList {
	
	@XStreamImplicit(itemFieldName="subServices")
	public List<SubService> subServices = new ArrayList<SubService>();

	/**
	 * @return the subServices
	 */
	public List<SubService> getSubServices() {
		return subServices;
	}

	/**
	 * @param subServices the subServices to set
	 */
	public void setSubServices(List<SubService> subServices) {
		this.subServices = subServices;
	}

	
}
