package frameworks.saml;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.saml.SAMLDiscovery;

import frameworks.tenant.TenantContextHolder;

public class IdpDiscovery extends SAMLDiscovery {

	@Override
	protected String getPassiveIDP(HttpServletRequest request) {
		//get tenant code for current user
		String tenantCd = TenantContextHolder.getContext().getCd();

		return "http://burgerking.wisetaillms.com/auth/saml/idp/metadata.php";
	}

}
