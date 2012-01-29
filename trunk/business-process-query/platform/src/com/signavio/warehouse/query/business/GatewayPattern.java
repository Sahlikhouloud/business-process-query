package com.signavio.warehouse.query.business;


public class GatewayPattern {
	//Keep information about what the current pattern is at certain gateway 
	private Activity gateway;
	private String pattern;
	private String gatewayPattern;

	public Activity getGateway() {
		return gateway;
	}

	public void setGateway(Activity gateway) {
		this.gateway = gateway;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getGatewayPattern() {
		return gatewayPattern;
	}

	public void setGatewayPattern(String gatewayPattern) {
		this.gatewayPattern = gatewayPattern;
	}

	public GatewayPattern(Activity gateway, String pattern, String gatewayPattern) {
		this.gateway = gateway;
		this.pattern = pattern;
		this.gatewayPattern = gatewayPattern;
	}
}
