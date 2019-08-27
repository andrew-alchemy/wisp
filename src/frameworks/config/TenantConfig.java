package frameworks.config;


import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;

import frameworks.tenant.TenantContext;
import frameworks.tenant.TenantContextFilter;


/**
 * Spring configuration bean for Tenant "discovery"
 * Will scan the available datasources and use that as the basis for producing a list of 
 * valid tenants for the current deployment 
 * 
 * 
 * @author Andrew
 *
 */
@Configuration
// it seems we need this in the appCtx.xml - cannot magically add it here 
// @ComponentScan("frameworks.config")
// We use these to define the external property files - note that in this case they in the root of the core.jar package structure
@PropertySources({
	@PropertySource(value="classpath:ontrack.properties",  ignoreResourceNotFound=true),
	@PropertySource(value="classpath:${fw.environment}/ontrack.properties", ignoreResourceNotFound=true)
})
public class TenantConfig {
	
	private Logger logger = Logger.getLogger(getClass());
	
	//@Value did not seem to work with the property soucres above (?!) but using Spring Environment DOES work as expected
	//@Value("${jndi.jdbc.context:java:jboss/ontrack/jdbc/MAIN}") String jndiJdbc;
	//@Value("${jndi.jdbc.test:true}") Boolean testConnection;

    @Autowired Environment env;
	
	/**
	 * create a Spring bean "tenants" of type List<TenantContext>
	 * that contains the collection of available tenants based on the configured data sources 
	 *  
	 * @return
	 * @throws NoTenantException 
	 */
	@Bean(name="tenants")
	public List<TenantContext> getTenants() throws NoTenantException {
		List<TenantContext> tenants = new ArrayList<TenantContext>();
		try {
			Context ctx = new InitialContext();
			String jndiJdbc = env.getProperty("jndi.jdbc.context", "java:comp/env/jdbc");
			Context jdbcCtx = (Context) ctx.lookup( jndiJdbc );

			//JNDI query of all entries under the context configured in JBoss for JDBC Datasources
			//NOte this also matches the JNDI context defined for Hibernate session factory
			NamingEnumeration<NameClassPair> list = jdbcCtx.list("");
			while (list.hasMore()) {
				NameClassPair ncp = list.next();
				//String cn = ncp.getClassName();
				String n = ncp.getName(); //this will be the tenant code e.g. BK or TH 
				try {
					//optionally verify the DataSource by grabbing a connection
					String testConnection = env.getProperty("jndi.jdbc.test", "true");
					if ( Boolean.parseBoolean(testConnection) ) {
						DataSource ds = (DataSource)jdbcCtx.lookup( n );
						Connection c = ds.getConnection();
						c.close();
					}
					//if its all OK then add it to the list
					tenants.add( new TenantContext(n) );
				} catch ( Exception e ) {
					logger.info( e.getMessage() );
				}
			}
			
		} catch (NamingException e) {
			logger.info( e.getMessage() );
		}
		
		if (tenants.size() < 1) throw new NoTenantException();
		
		return tenants;
	}

	/**
	 * create the web filter that examines inbound URLs and uses a regex to determine the corresponding tenant code 
	 * this bean is applied via web.xml through Spring's DelegatingFilterProxy
	 * 
	 * @return
	 * @throws NoTenantException 
	 */
	@Bean(name="TenantContextFilter")
	public TenantContextFilter getTenantContextFilter() throws NoTenantException { 
		TenantContextFilter tcf = new TenantContextFilter();
		tcf.setTenants( this.getTenants() );
		tcf.setTenantResolverMap( getTenantResolverMap() );
		return tcf;
	}
	
	/**
	 * define the map of tenant codes to regex patterns
	 * the regex patterns are in the ontrack.properties file
	 * 
	 * @return
	 * @throws NoTenantException 
	 */
	@Bean(name="tenantResolverMap")
	public Map<String, String> getTenantResolverMap() throws NoTenantException {
		List<TenantContext> tenants = this.getTenants();
		Map<String, String> tenantResolverMap = new HashMap<String,String>( tenants.size() );
		for ( TenantContext tenant : tenants ) {
			String regex = env.getProperty("tenant.resolver." + tenant.getCd() );
			tenantResolverMap.put(tenant.getCd(),  regex );
		}
		return tenantResolverMap;
	}

	
}

