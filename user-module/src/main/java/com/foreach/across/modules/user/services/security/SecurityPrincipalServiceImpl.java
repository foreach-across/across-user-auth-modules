package com.foreach.across.modules.user.services.security;

import com.foreach.across.core.events.AcrossEventPublisher;
import com.foreach.across.modules.user.business.NonGroupedPrincipal;
import com.foreach.across.modules.user.events.SecurityPrincipalRenamedEvent;
import com.foreach.across.modules.user.repositories.SecurityPrincipalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Arne Vandamme
 */
@Service
public class SecurityPrincipalServiceImpl implements SecurityPrincipalService
{
	@Autowired
	private SecurityPrincipalRepository securityPrincipalRepository;

	@Autowired
	private AcrossEventPublisher eventPublisher;

	@Override
	@SuppressWarnings("unchecked")
	public <T extends NonGroupedPrincipal> T getPrincipalById( long id ) {
		return (T) securityPrincipalRepository.getById( id );
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends NonGroupedPrincipal> T getPrincipalByName( String principalName ) {
		return (T) securityPrincipalRepository.getByPrincipalName( principalName );
	}

	@Override
	@Transactional
	public void publishRenameEvent( String oldPrincipalName, String newPrincipalName ) {
		SecurityPrincipalRenamedEvent renamedEvent = new SecurityPrincipalRenamedEvent( oldPrincipalName,
		                                                                                newPrincipalName );

		eventPublisher.publish( renamedEvent );
	}
}
