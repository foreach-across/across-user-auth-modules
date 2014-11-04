package com.foreach.across.modules.user.config.modules;

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.modules.user.controllers.UserAclController;
import com.foreach.across.modules.user.services.GroupAclInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AcrossDepends(required = "SpringSecurityAclModule")
@Configuration
public class UserSpringSecurityAclConfiguration
{
	@Bean
	public UserAclController userAclController() {
		return new UserAclController();
	}

	@Bean
	public GroupAclInterceptor groupAclInterceptor() {
		return new GroupAclInterceptor();
	}
}
