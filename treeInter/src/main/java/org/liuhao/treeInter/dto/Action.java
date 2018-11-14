package org.liuhao.treeInter.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class Action {
	
	@XStreamAlias("name")
	private String name = "";
	@XStreamAlias("type")
	private String type = "";
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
