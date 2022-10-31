<t:if cond="${totiIdentity.isPresent()}" >
	totiAuth.login({
		"access_token": "${totiIdentity.getToken()}",
		"expires_in": ${totiIdentity.getExpirationTime()}
	});
<t:else>
	<%-- token is in JS but not on server - server restarted OR sync logout --%>
	totiAuth.logout();
</t:if>