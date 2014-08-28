package com.foreach.across.modules.user.business;

import com.foreach.across.modules.user.config.UserSchemaConfiguration;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Represents a SecurityPrincipal identity that is like a user account, but no physical person.
 *
 * @author Arne Vandamme
 */
@Entity
@DiscriminatorValue("machine")
@Table(name = UserSchemaConfiguration.TABLE_MACHINE_PRINCIPAL)
public class MachinePrincipal extends GroupedPrincipal implements Comparable<MachinePrincipal>
{
	@Column(name = "name")
	private String name;

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
		setPrincipalName( name );
	}

	@Override
	public int compareTo( MachinePrincipal o ) {
		return StringUtils.defaultString( getName() ).compareTo( StringUtils.defaultString( o.getName() ) );
	}
}
