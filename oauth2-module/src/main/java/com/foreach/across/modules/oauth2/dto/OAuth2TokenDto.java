package com.foreach.across.modules.oauth2.dto;

public class OAuth2TokenDto
{
	private String value;

	public OAuth2TokenDto( String value ) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue( String value ) {
		this.value = value;
	}
}
