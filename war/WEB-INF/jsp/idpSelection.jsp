<%@ page import="org.springframework.security.saml.metadata.MetadataManager" %>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="java.util.Set" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<body>
	<h2>IDP Selection</h2>
	<p>Please select Identity Provider to authenticate with.</p>
    <div>
	<%
	WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletConfig().getServletContext());
	MetadataManager mm = context.getBean("metadata", MetadataManager.class);
	Set<String> idps = mm.getIDPEntityNames();
	String defaultIDP = mm.getDefaultIDP();

	pageContext.setAttribute("idp", idps);
	pageContext.setAttribute("defaultIDP", defaultIDP);

	%>
	<form action="<c:url value="${requestScope.idpDiscoReturnURL}"/>" method="get">
		<c:forEach var="idpItem" items="${idp}">
			<input type="radio" <c:if test="${idpItem eq defaultIDP}">checked</c:if> name="${requestScope.idpDiscoReturnParam}" id="idp_<c:out value="${idpItem}"/>" value="<c:out value="${idpItem}"/>"/>
			<label for="idp_<c:out value="${idpItem}"/>"><c:out value="${idpItem}"/></label>
			<br/>
		</c:forEach>
		<br/>
		<input type="submit" value="LOGIN"/>
	</form>
    </div>
</div>
</body>
</html>