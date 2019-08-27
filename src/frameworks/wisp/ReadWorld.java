package frameworks.wisp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.saml.SAMLCredential;
import org.opensaml.saml2.core.Attribute;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * This controller constitutes the actual custom SP application services 
 *   
 * @author Andrew
 *
 */
@Controller
@RequestMapping("/read/")
public class ReadWorld {
	
	//SAML 2 URNs for some common attributes
	public static final String SURNAME_URN = "urn:oid:2.5.4.4";
	public static final String GIVENNAME_URN = "urn:oid:2.5.4.42";
	public static final String COUNTRY_URN = "urn:oid:2.5.6.2";
	public static final String LANGUAGE_URN = "urn:oid:2.16.840.1.113730.3.1.39";

	/**
	 * This is just the secured endpoint
	 * You can hit this directly to provoke the SAML login "https://your.server.com/wisp/hello/world" 
	 * It is also the default "success" endpoint for IDP initiated SSO    
	 * 
	 * Note that the ROLE_USER is the SimpleGrantedAuthority assigned to the UserDetails in frameworks.saml.UserDetailsServiceImpl
	 * 
	 * It just shapes some of the authentication data for ease of presentation in a JSP that dumps out some key elements of the Authentication such as NameID and the SAML attributes 
	 * 
	 * @param auth
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="/1234", method=RequestMethod.GET)
	@RolesAllowed("ROLE_USER")
	public String sayHelloWorld(
			Model m,
			Authentication auth,
			HttpServletRequest request,
			HttpServletResponse response
			) throws IOException {
		
		//the user details (username, password, authorities)
		UserDetails userDetails = null;
		if (auth.getDetails() instanceof UserDetails) {
			userDetails = (UserDetails)auth.getDetails();
		}
		//the authorities (rights/roles) - see UserDetailsServiceImpl where we just add one SimpleGrantedAuthority 
		List<String> roles = new ArrayList<String>();
		Collection<? extends GrantedAuthority> permissions = auth.getAuthorities();
		for( GrantedAuthority permission : permissions) {
			String role = permission.getAuthority();
			if ( role != null ) {
				roles.add(role);
			}
		}
		
		//turn the Attribute XML into a simple string Map of attribute names and values for easy presentations
		Map<String, String> attrs = new HashMap<String, String>();
		SAMLCredential credential = null;
		if (auth.getCredentials() instanceof SAMLCredential) {
			credential = (SAMLCredential)auth.getCredentials();
			List<Attribute> xattrs = credential.getAttributes();
			for (Attribute attr : xattrs ) {
				String s = credential.getAttributeAsString( attr.getName() );
				attrs.put( attr.getFriendlyName(), s );
			}

		}
		//put all this on the model for easy access in the JSP
		m.addAttribute("userDetails", userDetails);
		m.addAttribute("roles", roles);
		m.addAttribute("credential", credential);
		m.addAttribute("attrs", attrs);

		return "helloWorld";
	}
	
	
	/**
	 * This endpoint is an example of an SP resource that expects to find and use specific attributes from the SAML Authentication 
	 * We can use this to test SamlRelayState
	 * This will be used in the IDP=-initiated SSO use case by adding the target parameter to the shibboleth URL /profile/SAML2/Unsolicited/SSO
	 * 
	 * @param m
	 * @param auth
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="/otherworld", method=RequestMethod.GET)
	@RolesAllowed("ROLE_USER")
	public String sayHelloOtherWorld(
			Model m,
			Authentication auth,
			HttpServletRequest request,
			HttpServletResponse response
			) throws IOException {

		//get the attributes we want
		if (auth.getCredentials() instanceof SAMLCredential) {
			SAMLCredential credential = (SAMLCredential)auth.getCredentials();
			//Look for the surname 
			m.addAttribute("nameLast", credential.getAttributeAsString( SURNAME_URN ));
			//Look for the first name 
			m.addAttribute("nameFirst", credential.getAttributeAsString( GIVENNAME_URN ));
			//Look for the country code
			m.addAttribute("country", credential.getAttributeAsString( COUNTRY_URN ));
			//Look for the language
			m.addAttribute("language", credential.getAttributeAsString( LANGUAGE_URN ));
		}

		return "helloOtherWorld";
	}
	
	/**
	 * This is an UNSECURED endpoint - used as the target for SAML failure
	 * This extracts the root SAMLException where possible from the key AUTHENTICATION_EXCEPTION 
	 * (see SimpleUrlAuthenticationFailureHandler)
	 *  
	 * Allows GET and POST
	 *  
	 * @param m
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="/goodbye")
	public String sayGoodbye(
			Model m,
			HttpServletRequest request,
			HttpServletResponse response
			) throws IOException {
		
		Object o =  request.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION); //in request if forward or session if redirect from SimpleUrlAuthenticationFailureHandler
		if ( o != null && o instanceof AuthenticationException) {
			AuthenticationException ex = (AuthenticationException)o;
			m.addAttribute("msg1", ex.getMessage() );
			if ( ex.getCause() != null ) { ///this should be the underlying SAMLException 
				m.addAttribute("msg2", ex.getCause().getMessage() );
			}
		}

		return "goodbye";
	}
	
	
	
}
