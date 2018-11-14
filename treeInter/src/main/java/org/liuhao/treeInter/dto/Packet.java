package org.liuhao.treeInter.dto;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("packet")
public class Packet implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//特殊处理类信�?
	@XStreamAlias("actionList")
	ActionList actionList = new ActionList();
	//报文模板信息
	@XStreamAlias("xmlModelList")
	XMLModelList xmlModelList = new XMLModelList();
	//继承重写的子类名称，如果没有则默认为父类
	@XStreamAlias("subServiceList")
	SubServiceList subServiceList = new SubServiceList();
	//平台账号地址
	@XStreamAlias("platUrlList")
	PlatUrlList platUrlList = new PlatUrlList();
	
	public ActionList getActionList() {
		return actionList;
	}
	public void setActionList(ActionList actionList) {
		this.actionList = actionList;
	}
	public XMLModelList getXmlModelList() {
		return xmlModelList;
	}
	public void setXmlModelList(XMLModelList xmlModelList) {
		this.xmlModelList = xmlModelList;
	}
	/**
	 * @return the subServiceList
	 */
	public SubServiceList getSubServiceList() {
		return subServiceList;
	}
	/**
	 * @param subServiceList the subServiceList to set
	 */
	public void setSubServiceList(SubServiceList subServiceList) {
		this.subServiceList = subServiceList;
	}
	/**
	 * @return the platUrlList
	 */
	public PlatUrlList getPlatUrlList() {
		return platUrlList;
	}
	/**
	 * @param platUrlList the platUrlList to set
	 */
	public void setPlatUrlList(PlatUrlList platUrlList) {
		this.platUrlList = platUrlList;
	}
	
	
}
