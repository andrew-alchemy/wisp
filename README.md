# wisp
Widdle SP - A tiny SAML2 Service Provider

This is a shell web application using Spring Security, Spring SAML, Spring MVC that can be used to test interactions with a SAML IDP. This i mainly intended as a lightweight support tool for use in building out the OnTrack IDP. 

The structure follows our typical patterns so should be easy to follow. 

The SP metadata is available here:
https://your.server.com/wisp/saml/metadata

The secured application is here:
https://your.server.com/wisp/hello/world

Accessing this endpoint will trigger the SAML mechanism. If you are not authenticated you will see an IDP selection page. Pick one and proceed to the IDPs login. Upon auccessful authentication you will see a confirmation page of the NameID, LocalEntityId (SP), RemoteEntityId (IDP) and a list of attributes provided by the IDP.


The TestShib IDP is preinstalled; see https://www.testshib.org/ for more details.

A second IDP can be defined in wisp.properties by the property frameworks.spring.saml.idp.metadata

