<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ page import="java.util.*, 
	java.security.Principal, org.springframework.security.core.Authentication, 
	org.springframework.security.core.GrantedAuthority, org.springframework.security.saml.SAMLCredential,
	org.springframework.security.core.userdetails.User"
%><%@ taglib uri="c.tld" prefix="c"
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
<div>Hello ${userDetails.username} - it appears you were successfully authenticated</div>

<H3>Authorities</H3>
<table>
<c:forEach items="${roles}" var="role" >
	<tr>
		<td>${role}</td>
	</tr>
</c:forEach>
</table>

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
