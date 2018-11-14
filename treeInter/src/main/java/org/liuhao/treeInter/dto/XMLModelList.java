package org.liuhao.treeInter.dto;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

public class XMLModelList {
	
	@XStreamImplicit(itemFieldName="xmlModel")
	public List<XMLModel> xmlModel = new ArrayList<XMLModel>();
	
	public List<XMLModel> getXmlModel() {
		return xmlModel;
	}

	public void setXmlModel(List<XMLModel> xmlModel) {
		this.xmlModel = xmlModel;
	}
}
