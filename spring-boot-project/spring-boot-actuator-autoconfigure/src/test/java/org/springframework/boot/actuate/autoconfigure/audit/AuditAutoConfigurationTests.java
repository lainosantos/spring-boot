/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.actuate.autoconfigure.audit;

import org.junit.jupiter.api.Test;

import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.boot.actuate.audit.InMemoryAuditEventRepository;
import org.springframework.boot.actuate.audit.listener.AbstractAuditListener;
import org.springframework.boot.actuate.audit.listener.AuditListener;
import org.springframework.boot.actuate.security.AbstractAuthenticationAuditListener;
import org.springframework.boot.actuate.security.AbstractAuthorizationAuditListener;
import org.springframework.boot.actuate.security.AuthenticationAuditListener;
import org.springframework.boot.actuate.security.AuthorizationAuditListener;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.event.AbstractAuthorizationEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link AuditAutoConfiguration}.
 *
 * @author Dave Syer
 * @author Vedran Pavic
 * @author Madhura Bhave
 */
public class AuditAutoConfigurationTests {

	private WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(AuditAutoConfiguration.class));

	@Test
	public void autoConfigurationIsDisabledByDefault() {
		this.contextRunner.run((context) -> assertThat(context).doesNotHaveBean(AuditAutoConfiguration.class));
	}

	@Test
	public void autoConfigurationIsEnabledWhenAuditEventRepositoryBeanPresent() {
		this.contextRunner.withUserConfiguration(CustomAuditEventRepositoryConfiguration.class).run((context) -> {
			assertThat(context.getBean(AuditEventRepository.class)).isNotNull();
			assertThat(context.getBean(AuthenticationAuditListener.class)).isNotNull();
			assertThat(context.getBean(AuthorizationAuditListener.class)).isNotNull();
		});
	}

	@Test
	public void ownAuthenticationAuditListener() {
		this.contextRunner.withUserConfiguration(CustomAuditEventRepositoryConfiguration.class)
				.withUserConfiguration(CustomAuthenticationAuditListenerConfiguration.class)
				.run((context) -> assertThat(context.getBean(AbstractAuthenticationAuditListener.class))
						.isInstanceOf(TestAuthenticationAuditListener.class));
	}

	@Test
	public void ownAuthorizationAuditListener() {
		this.contextRunner.withUserConfiguration(CustomAuditEventRepositoryConfiguration.class)
				.withUserConfiguration(CustomAuthorizationAuditListenerConfiguration.class)
				.run((context) -> assertThat(context.getBean(AbstractAuthorizationAuditListener.class))
						.isInstanceOf(TestAuthorizationAuditListener.class));
	}

	@Test
	public void ownAuditListener() {
		this.contextRunner.withUserConfiguration(CustomAuditEventRepositoryConfiguration.class)
				.withUserConfiguration(CustomAuditListenerConfiguration.class)
				.run((context) -> assertThat(context.getBean(AbstractAuditListener.class))
						.isInstanceOf(TestAuditListener.class));
	}

	@Test
	public void backsOffWhenDisabled() {
		this.contextRunner.withUserConfiguration(CustomAuditEventRepositoryConfiguration.class)
				.withPropertyValues("management.auditevents.enabled=false")
				.run((context) -> assertThat(context).doesNotHaveBean(AuditListener.class)
						.doesNotHaveBean(AuthenticationAuditListener.class)
						.doesNotHaveBean(AuthorizationAuditListener.class));
	}

	@Configuration(proxyBeanMethods = false)
	public static class CustomAuditEventRepositoryConfiguration {

		@Bean
		public TestAuditEventRepository testAuditEventRepository() {
			return new TestAuditEventRepository();
		}

	}

	public static class TestAuditEventRepository extends InMemoryAuditEventRepository {

	}

	@Configuration(proxyBeanMethods = false)
	protected static class CustomAuthenticationAuditListenerConfiguration {

		@Bean
		public TestAuthenticationAuditListener authenticationAuditListener() {
			return new TestAuthenticationAuditListener();
		}

	}

	protected static class TestAuthenticationAuditListener extends AbstractAuthenticationAuditListener {

		@Override
		public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
		}

		@Override
		public void onApplicationEvent(AbstractAuthenticationEvent event) {
		}

	}

	@Configuration(proxyBeanMethods = false)
	protected static class CustomAuthorizationAuditListenerConfiguration {

		@Bean
		public TestAuthorizationAuditListener authorizationAuditListener() {
			return new TestAuthorizationAuditListener();
		}

	}

	protected static class TestAuthorizationAuditListener extends AbstractAuthorizationAuditListener {

		@Override
		public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
		}

		@Override
		public void onApplicationEvent(AbstractAuthorizationEvent event) {
		}

	}

	@Configuration(proxyBeanMethods = false)
	protected static class CustomAuditListenerConfiguration {

		@Bean
		public TestAuditListener testAuditListener() {
			return new TestAuditListener();
		}

	}

	protected static class TestAuditListener extends AbstractAuditListener {

		@Override
		protected void onAuditEvent(AuditEvent event) {

		}

	}

}
