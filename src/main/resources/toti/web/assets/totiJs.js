/* TOTI script version 0.1.2 */
<t:include file="assets/js/totiSettings.js" />
<t:include file="assets/js/totiImages.js" />
<t:include file="assets/js/totiTranslations.js" />
<t:include file="assets/js/totiUtils.js" />
<t:include file="assets/js/totiStorage.js" />
<t:include file="assets/js/totiLang.js" />
<t:include file="assets/js/totiLoad.js" />
<t:include file="assets/js/totiAuth.js" />
<t:include file="assets/js/totiDisplay.js" />
<t:include file="assets/js/totiControl.js" />
<t:if cond="${useProfiler}" >
	<t:include file="assets/js/totiProfiler.js" />
</t:if>
<t:include file="assets/js/totiForm.js" />
<t:include file="assets/js/totiGrid.js" />
	

<t:if cond="${totiIdentity.isPresent()}" >
	totiAuth.login({
		"access_token": "${totiIdentity.getToken()}",
		"expires_in": Date.now() + ${totiIdentity.getExpirationTime()}
	});
<t:else>
	<%-- token is in JS but not on server - server restarted OR sync logout --%>
	totiAuth.logout();
</t:if>