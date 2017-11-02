package frameworks.wisp;

import java.io.IOException;
import java.util.Collection;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/hello/")
@RolesAllowed("ROLE_USER")
public class HelloWorld {

	
	@RequestMapping(value="/world", method=RequestMethod.GET)
	@RolesAllowed("ROLE_USER")
	public String sayHelloWorld(
			Authentication auth,
			HttpServletRequest request,
			HttpServletResponse response
			) throws IOException {
		
		Collection<? extends GrantedAuthority> permissions = auth.getAuthorities();
		Object c = auth.getCredentials();
		Object userDetails = auth.getDetails();
		String name = auth.getName();
		Object p = auth.getPrincipal();
		Boolean isAuth = auth.isAuthenticated();

		return "helloWorld";
	}
	
}
