package org.liuhao.treeInter.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class XMLModel {
	
	@XStreamAlias("name")
	private String name = "";
	@XStreamAlias("path")
	private String path = "";
	@XStreamAlias("backpath")
	private String backpath = "";
	@XStreamAlias("charset")
	private String charset = "";
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	/**
	 * @return the backpath
	 */
	public String getBackpath() {
		return backpath;
	}
	/**
	 * @param backpath the backpath to set
	 */
	public void setBackpath(String backpath) {
		this.backpath = backpath;
	}
	/**
	 * @return the charset
	 */
	public String getCharset() {
		return charset;
	}
	/**
	 * @param charset the charset to set
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}
	
}
