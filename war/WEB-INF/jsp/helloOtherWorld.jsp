<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ page import="java.util.*, 
	frameworks.wisp.HelloWorld"
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
<div>Hello ${nameFirst} ${nameLast}</div>
<div>We welcome your visit from ${country} and will try and respect your request to converse in ${language}</div>

</body>
</html>
