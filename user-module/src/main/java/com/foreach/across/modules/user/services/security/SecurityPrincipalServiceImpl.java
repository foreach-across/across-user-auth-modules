package com.foreach.across.modules.user.services.security;

import com.foreach.across.modules.spring.security.business.SecurityPrincipal;
import com.foreach.across.modules.user.repositories.SecurityPrincipalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Arne Vandamme
 */
@Service
public class SecurityPrincipalServiceImpl implements SecurityPrincipalService
{
	@Autowired
	private SecurityPrincipalRepository securityPrincipalRepository;

	@Override
	@SuppressWarnings("unchecked")
	public <T extends SecurityPrincipal> T getPrincipalById( long id ) {
		return (T) securityPrincipalRepository.getById( id );
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends SecurityPrincipal> T getPrincipalByName( String principalName ) {
		return (T) securityPrincipalRepository.getByPrincipalName( principalName );
	}
}
