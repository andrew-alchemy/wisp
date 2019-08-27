package frameworks.tenant;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Determine the current Tenant by virtue of the hostname on the request
 * and track in a thread local.
 * 
 * TODO couple this into spring security so that the only real tracking of tenant is in the authentication.
 * 
 * @author Frameworks Inc.
 */
public class TenantContextFilter extends OncePerRequestFilter {
	
	private List<TenantContext> tenants;
	private Map<String, String> tenantResolverMap = Collections.emptyMap();
	private ConcurrentMap<String, String> tenantCache = new ConcurrentHashMap<>();

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response, 
			FilterChain filterChain)
					throws ServletException, IOException {
		
		try {
			// extract the tenant from request - if it is valid then keep going
			if ( applyTenant(request.getServerName()) ) {
				filterChain.doFilter(request, response);
			} else {
				//otherwise return a 400 if the tenant is NOT valiid
				response.sendError( HttpStatus.BAD_REQUEST.value() );
			}
		} finally {
			TenantContextHolder.clearContext();
		}
	}

	protected boolean applyTenant(String serverName) {
		boolean validTenant = false;
		
		String tenant = tenantCache.get(serverName);

		if(tenant == null) {
			for(Map.Entry<String, String> entry : tenantResolverMap.entrySet()) {
				if(serverName.toLowerCase().matches(entry.getValue())) {
					tenant = entry.getKey();
					tenantCache.putIfAbsent(serverName, tenant);
					break;
				}
			}
		}
		//We have a valid tenant if 
		// a. we find a matching tenant for the current URL
		// b. the matching tenant is available (injected list of tenants based on availability of JNDI datasource)  
		if ( tenant != null ) {
			TenantContext tc = new TenantContext(tenant);
			if ( tenants.contains( tc )) {
				TenantContextHolder.setContext( tc );
				validTenant = true;
			}
		}

		return validTenant;
	}

	/* getters and setters */
	public void setTenantResolverMap(Map<String, String> tenantResolverMap) {
		this.tenantResolverMap = tenantResolverMap;
	}
	
	public void setTenants(List<TenantContext> tenants) {
		this.tenants = tenants;
	}

}
