package com.at4wireless.security;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.jasig.cas.client.validation.TicketValidationException;
import org.junit.Assert;
import org.junit.Test;

public class TestCASTicketValidator {
	private static final String lfCASResponse = "<cas:serviceResponse xmlns:cas='http://www.yale.edu/tp/cas'>"
		+"<cas:authenticationSuccess>"
		+"<cas:user>cjcollier</cas:user>"
		+"<cas:attributes>"
		+"<cas:attraStyle>Jasig</cas:attraStyle>"
		+"<cas:uid>85233</cas:uid>"
		+"<cas:mail>cjcollier@linuxfoundation.org</cas:mail>"
		+"<cas:created>1454939906</cas:created>"
		+"<cas:timezone>America/Los_Angeles</cas:timezone>"
		+"<cas:language></cas:language>"
		+"<cas:drupal_roles>authenticated user</cas:drupal_roles>"
		+"<cas:drupal_roles>administrator</cas:drupal_roles>"
		+"<cas:drupal_roles>lf-group-admin</cas:drupal_roles>"
		+"<cas:drupal_roles>group administrator</cas:drupal_roles>"
		+"<cas:group>lf-collab-admins</cas:group>"
		+"<cas:group>lf-itwiki-user</cas:group>"
		+"<cas:group>lf-racktables</cas:group>"
		+"<cas:group>lf-staff</cas:group>"
		+"<cas:group>lfinfra-gerrit-lfit</cas:group>"
		+"<cas:group>lf-mailman3-admins</cas:group>"
		+"<cas:group>fdio-gerrit-one-committers</cas:group>"
		+"<cas:group>collab-external-rt-access</cas:group>"
		+"<cas:group>fdio-gerrit-vppsb-committers</cas:group>"
		+"<cas:group>fdio-gerrit-deb_dpdk-committers</cas:group>"
		+"<cas:group>fdio-gerrit-ci-management-committers</cas:group>"
		+"<cas:group>fdio-gerrit-trex-committers</cas:group>"
		+"<cas:field_lf_first_name>C.J.</cas:field_lf_first_name>"
		+"<cas:field_lf_full_name>C.J. Collier</cas:field_lf_full_name>"
		+"<cas:field_lf_last_name>Collier</cas:field_lf_last_name>"
		+"<cas:profile_name_first>C.J.</cas:profile_name_first>"
		+"<cas:profile_name_last>Collier</cas:profile_name_last>"
		+"<cas:profile_name_full>C.J. Collier</cas:profile_name_full>"
		+"</cas:attributes>"
		+"</cas:authenticationSuccess>"
		+"</cas:serviceResponse>";
	
	@Test
	public void testCasToCttUserParsing() {
		CustomCas20ServiceTicketValidator customCas20ServiceTicketValidator =
				new CustomCas20ServiceTicketValidator("https://identity.linuxfoundation.org/cas/serviceValidate");
		
		final Map<String, Object> attributes = customCas20ServiceTicketValidator.extractCustomAttributes(lfCASResponse);
		try {
			final String role = customCas20ServiceTicketValidator.parseRoleFromCasToCtt(attributes);
			assertTrue(role.equals("ROLE_USER"));
		} catch (TicketValidationException e) {
			Assert.fail(e.getMessage());
		}
	}
}
