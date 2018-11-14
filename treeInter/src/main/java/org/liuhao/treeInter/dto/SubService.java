package org.liuhao.treeInter.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class SubService {
	
	@XStreamAlias("key")
	private String key = "";
	@XStreamAlias("path")
	private String path = "";
	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}
	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}
	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
}
