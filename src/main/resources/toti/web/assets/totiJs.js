/* TOTI script version 1.0.0 */

<t:include file="assets/js/totiSortedMap.js" />
<t:include file="assets/js/totiTranslations.js" />
<t:include file="assets/js/totiImages.js" />

<t:include file="assets/js/totiStorage.js" />
<t:include file="assets/js/totiUtils.js" />
<t:include file="assets/js/totiLang.js" />
<t:include file="assets/js/totiLoad.js" />
<t:include file="assets/js/totiAuth.js" />
<t:if cond="${useProfiler}" >
	<t:include file="assets/js/totiProfiler.js" />
</t:if>
<t:include file="assets/js/totiFormCustomTemplate.js" />
<t:include file="assets/js/totiFormDefaultTemplate.js" />
<t:include file="assets/js/totiGridCustomTemplate.js" />
<t:include file="assets/js/totiGridDefaultTemplate.js" />
<t:include file="assets/js/totiDisplay.js" />

<t:include file="assets/js/totiControl.js" />
<t:include file="assets/js/totiForm.js" />
<t:include file="assets/js/totiGrid.js" />

<t:if cond="${totiIdentity.isPresent()}" >
	totiAuth.login({
		"access_token": "${totiIdentity.getToken()}",
		"expires_in": ${totiIdentity.getExpirationTime()}
	});
<t:else>
	<%-- token is in JS but not on server - server restarted OR sync logout --%>
	totiAuth.logout();
</t:if>