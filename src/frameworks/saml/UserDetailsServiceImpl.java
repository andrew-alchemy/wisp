package frameworks.saml;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.schema.impl.XSAnyImpl;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;


/**
 * The UserDetailsService used by the SAML AuthenticationProvider
 * The purpose is to use the SAML Credential to load up an implementation of UserDetails
 * This just uses the vanilla implementation provided by Spring (org.springframework.security.core.userdetails.User.User)
 * It populates that POJO directly with the data found in the SAML Credential
 * 
 * @author Andrew
 *
 */
public class UserDetailsServiceImpl implements SAMLUserDetailsService {
	private Logger logger = Logger.getLogger(UserDetailsServiceImpl.class);
	
	public UserDetailsServiceImpl() {
	}

	
	@Override
	public Object loadUserBySAML(SAMLCredential credential) throws UsernameNotFoundException {
		if ( credential == null || credential.getNameID() == null ) {
			throw new UsernameNotFoundException("SAML Credential is null or NameID is null");
		}

		String nameId = credential.getNameID().getValue();

		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
		authorities.add(authority);

		/*
		 * create a user whose 
		 * -- username is the SAML NameID, 
		 * -- password is NameID with suffix "__!" (this really doesn't matter as it isn't used for anything)
		 * -- has a single authority ROLE_USER
		 * 
		 * The authority will be used in the HellowWorld controller to provide rights based access via 
		 * the javax.security annotations e.g. @RolesAllowed("ROLE_USER") 
		 * 
		 */
		User user = new User(nameId, nameId+"__!", true, true, true, true, authorities);

		String preferredLanguage = credential.getAttributeAsString("urn:oid:2.16.840.1.113730.3.1.39"); // this does NOT use the friendly name "preferredLanguage"
		String username = credential.getAttributeAsString("urn:oid:0.9.2342.19200300.100.1.1");
		String country = credential.getAttributeAsString("urn:oid:2.5.6.2");

		logger.debug("SAML NameId:"+nameId);
		logger.debug("SAML username :"+ username );
		logger.debug("SAML preferredLanguage  :"+ preferredLanguage );
		logger.debug("SAML country:"+ country );
		
		List<Attribute> attrs = credential.getAttributes();
		for (Attribute attr : attrs ) {
			logger.debug("SAML attribute" + attr.toString());
			logger.debug("SAML attribute friendly name " + attr.getFriendlyName());
			logger.debug("SAML attribute name format " + attr.getNameFormat());
			//logger.debug("SAML attribute schema location " + attr.getSchemaLocation());
			logger.debug("SAML attribute values" + attr.getAttributeValues());
			for (XMLObject x : attr.getAttributeValues()) {
				if ( x instanceof XSAnyImpl) {
					String s = ((XSAnyImpl)x).getTextContent();
					logger.debug("attr val:" + s );
				}
			}
		}

		logger.debug("UserDetails:"+user);
		return user;
	}
}
