package org.liuhao.treeInter.dto;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

public class ActionList {
	
	@XStreamImplicit(itemFieldName="action")
	public List<Action> action = new ArrayList<Action>();
	public List<Action> getAction() {
		return action;
	}

	public void setAction(List<Action> action) {
		this.action = action;
	}
}
