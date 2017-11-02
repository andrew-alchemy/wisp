<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ page import="java.util.*, 
	java.security.Principal, org.springframework.security.core.Authentication, 
	org.springframework.security.core.GrantedAuthority, org.springframework.security.saml.SAMLCredential,
	org.springframework.security.core.userdetails.User,	org.opensaml.saml2.core.Attribute,
	org.opensaml.xml.XMLObject,	org.opensaml.xml.schema.impl.XSAnyImpl"
%><%@ taglib uri="c.tld" prefix="c"
%><%
final Principal requestPrincipal = request.getUserPrincipal();
try {
	if ( requestPrincipal instanceof Authentication ) {
		Authentication auth = (Authentication)requestPrincipal;
		org.springframework.security.core.userdetails.User user = new User("Error","Error", new ArrayList<GrantedAuthority>());
		SAMLCredential credential = null;
		if (auth.getDetails() instanceof org.springframework.security.core.userdetails.User) {
			user = (org.springframework.security.core.userdetails.User)auth.getDetails();
		}

		Map<String, String> attrs = new HashMap<String, String>();
		if (auth.getCredentials() instanceof SAMLCredential) {
			credential = (SAMLCredential)auth.getCredentials();
			List<Attribute> xattrs = credential.getAttributes();
			for (Attribute attr : xattrs ) {
				String s = credential.getAttributeAsString( attr.getName() );
				attrs.put( attr.getFriendlyName(), s );
			}
		}

		pageContext.setAttribute("user", user, pageContext.PAGE_SCOPE );
		pageContext.setAttribute("attrs", attrs, pageContext.PAGE_SCOPE );
		pageContext.setAttribute("credential", credential, pageContext.PAGE_SCOPE );
		

	}
} catch ( Exception e ) {
	out.print( e );
}
%><style>
body {
	font-family: sans-serif;
	margin:1em;
}
table {
    border-collapse: collapse;
    border: 1px solid black;
    margin: 1em;
}
td {
    padding: 0.4em;
}
</style>
<H1>WISP SAML2 Service Provider</H1>
<div>Hello ${user.username} - it appears you were successfully authenticated</div>

<H3>Entities</H3>
<div>Local SP ID: ${credential.localEntityID}</div>
<div>Remote IDP ID: ${credential.remoteEntityID}</div>

<H3>Attributes</H3>
<table>
<c:forEach items="${attrs}" var="attr" >
	<tr>
		<td>${attr.key}</td>
		<td>${attr.value}</td>
	</tr>
</c:forEach>
</table>

</body>
</html>
