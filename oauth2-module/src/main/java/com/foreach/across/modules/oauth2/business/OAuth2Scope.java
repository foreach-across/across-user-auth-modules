package com.foreach.across.modules.oauth2.business;

import com.foreach.across.core.database.AcrossSchemaConfiguration;
import com.foreach.across.modules.oauth2.config.OAuth2SchemaConfiguration;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = OAuth2SchemaConfiguration.TABLE_SCOPE)
public class OAuth2Scope
{

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "seq_oauth_scope_id")
	@TableGenerator(name = "seq_oauth_scope_id", table = AcrossSchemaConfiguration.TABLE_SEQUENCES,
	                pkColumnName = AcrossSchemaConfiguration.SEQUENCE_NAME,
	                valueColumnName = AcrossSchemaConfiguration.SEQUENCE_VALUE, pkColumnValue = "seq_oauth_scope_id",
	                allocationSize = 10)
	private long id;

	@Column(name = "name")
	private String name;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "pk.oAuth2Scope")
	private Set<OAuth2ClientScope> oAuth2ClientScopes = new HashSet<OAuth2ClientScope>();

	public long getId() {
		return id;
	}

	public void setId( long id ) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public Set<OAuth2ClientScope> getOAuth2ClientScopes() {
		return this.oAuth2ClientScopes;
	}

	public void setOAuth2ClientScopes( Set<OAuth2ClientScope> oAuth2ClientScopes ) {
		this.oAuth2ClientScopes = oAuth2ClientScopes;
	}
}
