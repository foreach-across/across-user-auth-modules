package com.foreach.across.modules.oauth2.business;

import com.foreach.across.modules.oauth2.config.OAuth2SchemaConfiguration;

import javax.persistence.*;

@Entity
@Table(name = OAuth2SchemaConfiguration.TABLE_CLIENT_SCOPE)
@AssociationOverrides({
		                      @AssociationOverride(name = "pk.oAuth2Client",
		                                           joinColumns = @JoinColumn(name = "client_id")),
		                      @AssociationOverride(name = "pk.oAuth2Scope",
		                                           joinColumns = @JoinColumn(name = "scope_id")) })
public class OAuth2ClientScope implements Comparable
{

	@EmbeddedId
	private OAuth2ClientScopeId pk = new OAuth2ClientScopeId();

	@Column(name = "auto_approve")
	private boolean autoApprove;

	public OAuth2ClientScopeId getPk() {
		return pk;
	}

	public void setPk( OAuth2ClientScopeId pk ) {
		this.pk = pk;
	}

	@Transient
	public OAuth2Client getOAuth2Client() {
		return this.getPk().getOAuth2Client();
	}

	public void setOAuth2Client( OAuth2Client oAuth2Client ) {
		this.getPk().setOAuth2Client( oAuth2Client );
	}

	@Transient
	public OAuth2Scope getOAuth2Scope() {
		return this.getPk().getOAuth2Scope();
	}

	public void setOAuth2Scope( OAuth2Scope oAuth2Scope ) {
		this.getPk().setOAuth2Scope( oAuth2Scope );
	}

	public boolean isAutoApprove() {
		return autoApprove;
	}

	public void setAutoApprove( boolean autoApprove ) {
		this.autoApprove = autoApprove;
	}

	@Override
	public int compareTo( Object o ) {
		OAuth2ClientScope that = (OAuth2ClientScope) o;
		return this.getOAuth2Scope().getName().compareTo( that.getOAuth2Scope().getName() );
	}
}
