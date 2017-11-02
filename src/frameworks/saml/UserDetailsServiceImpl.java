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

		User user = new User(nameId, nameId+"__!", true, true, true, true, authorities);

		String preferredLanguage = credential.getAttributeAsString("urn:oid:2.16.840.1.113730.3.1.39"); // this does NOT use the friendly name "preferredLanguage"
		String username = credential.getAttributeAsString("urn:oid:0.9.2342.19200300.100.1.1");
		String country = credential.getAttributeAsString("urn:oid:2.5.6.2");

		logger.info("SAML NameId:"+nameId);
		logger.info("SAML username :"+ username );
		logger.info("SAML preferredLanguage  :"+ preferredLanguage );
		logger.info("SAML country:"+ country );
		
		List<Attribute> attrs = credential.getAttributes();
		for (Attribute attr : attrs ) {
			logger.info("SAML attribute" + attr.toString());
			logger.info("SAML attribute friendly name " + attr.getFriendlyName());
			logger.info("SAML attribute name format " + attr.getNameFormat());
			//logger.info("SAML attribute schema location " + attr.getSchemaLocation());
			logger.info("SAML attribute values" + attr.getAttributeValues());
			for (XMLObject x : attr.getAttributeValues()) {
				if ( x instanceof XSAnyImpl) {
					String s = ((XSAnyImpl)x).getTextContent();
					logger.info("attr val:" + s );
				}
			}
		}

		logger.info("UserDetails:"+user);
		return user;
	}
}
