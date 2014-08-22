package com.foreach.across.modules.user.business;

import com.foreach.across.modules.user.config.UserSchemaConfiguration;
import org.hibernate.annotations.BatchSize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Extension to {@link NonGroupedPrincipal}
 * that allows being a member of one or more principal groups.
 *
 * @author Arne Vandamme
 */
public abstract class GroupedPrincipal extends NonGroupedPrincipal
{
	@ManyToMany(fetch = FetchType.EAGER)
	@BatchSize(size = 50)
	@JoinTable(
			name = UserSchemaConfiguration.TABLE_PRINCIPAL_GROUP,
			joinColumns = @JoinColumn(name = "principal_id"),
			inverseJoinColumns = @JoinColumn(name = "group_id"))
	private Set<Group> groups = new TreeSet<>();

	public Set<Group> getGroups() {
		return groups;
	}

	public void setGroups( Set<Group> groups ) {
		this.groups = groups;
	}

	public boolean isMemberOf( Group group ) {
		return groups.contains( group );
	}

	public void addGroup( Group group ) {
		groups.add( group );
	}

	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> authorities = new LinkedHashSet<>();

		for ( Role role : getRoles() ) {
			authorities.add( new SimpleGrantedAuthority( role.getName() ) );
			for ( Permission permission : role.getPermissions() ) {
				authorities.add( new SimpleGrantedAuthority( permission.getName() ) );
			}
		}

		for ( Group group : getGroups() ) {
			authorities.addAll( group.getAuthorities() );
		}

		return authorities;
	}
}
