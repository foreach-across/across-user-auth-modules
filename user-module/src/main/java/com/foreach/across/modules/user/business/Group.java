package com.foreach.across.modules.user.business;

import com.foreach.across.modules.user.config.UserSchemaConfiguration;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@DiscriminatorValue("group")
@Table(name = UserSchemaConfiguration.TABLE_GROUP)
public class Group extends NonGroupedPrincipal implements Comparable<Group>
{
	@Column(name = "name")
	private String name;

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
		setPrincipalName( "group:" + name );
	}

	@Override
	public int compareTo( Group other ) {
		return getName().compareTo( other.getName() );
	}
}
