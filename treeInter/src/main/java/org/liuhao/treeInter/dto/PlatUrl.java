package org.liuhao.treeInter.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class PlatUrl {
	
	@XStreamAlias("ci_key")
	private String ci_key = "";
	@XStreamAlias("ci_user")
	private String ci_user = "";
	@XStreamAlias("ci_password")
	private String ci_password = "";
	@XStreamAlias("ci_url")
	private String ci_url = "";
	@XStreamAlias("ci_amount")
	private String ci_amount = "";
	@XStreamAlias("ci_charset")
	private String ci_charset = "";
	@XStreamAlias("ci_description")
	private String ci_description = "";
	/**
	 * @return the ci_key
	 */
	public String getCi_key() {
		return ci_key;
	}
	/**
	 * @param ci_key the ci_key to set
	 */
	public void setCi_key(String ci_key) {
		this.ci_key = ci_key;
	}
	/**
	 * @return the ci_user
	 */
	public String getCi_user() {
		return ci_user;
	}
	/**
	 * @param ci_user the ci_user to set
	 */
	public void setCi_user(String ci_user) {
		this.ci_user = ci_user;
	}
	/**
	 * @return the ci_password
	 */
	public String getCi_password() {
		return ci_password;
	}
	/**
	 * @param ci_password the ci_password to set
	 */
	public void setCi_password(String ci_password) {
		this.ci_password = ci_password;
	}
	/**
	 * @return the ci_url
	 */
	public String getCi_url() {
		return ci_url;
	}
	/**
	 * @param ci_url the ci_url to set
	 */
	public void setCi_url(String ci_url) {
		this.ci_url = ci_url;
	}
	/**
	 * @return the ci_amount
	 */
	public String getCi_amount() {
		return ci_amount;
	}
	/**
	 * @param ci_amount the ci_amount to set
	 */
	public void setCi_amount(String ci_amount) {
		this.ci_amount = ci_amount;
	}
	/**
	 * @return the ci_charset
	 */
	public String getCi_charset() {
		return ci_charset;
	}
	/**
	 * @param ci_charset the ci_charset to set
	 */
	public void setCi_charset(String ci_charset) {
		this.ci_charset = ci_charset;
	}
	/**
	 * @return the ci_description
	 */
	public String getCi_description() {
		return ci_description;
	}
	/**
	 * @param ci_description the ci_description to set
	 */
	public void setCi_description(String ci_description) {
		this.ci_description = ci_description;
	}
}
