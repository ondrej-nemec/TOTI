<t:include file="/ui/assets/js/totiSortedMap.js" />
<t:include file="/ui/assets/js/totiTranslations.js" />
<t:include file="/ui/assets/js/totiImages.js" />

<t:include file="/ui/assets/js/totiStorage.js" />
<t:include file="/ui/assets/js/totiUtils.js" />
<t:include file="/ui/assets/js/totiLang.js" />
<t:include file="/ui/assets/js/totiLoad.js" />
<t:include file="/ui/assets/js/totiAuth.js" />
<t:include file="/ui/assets/js/totiStandartSelect.js" />
<t:include file="/ui/assets/js/totiExtendedSelect.js" />
<t:include file="/ui/assets/js/totiFormCustomTemplate.js" />
<t:include file="/ui/assets/js/totiFormDefaultTemplate.js" />
<t:include file="/ui/assets/js/totiGridCustomTemplate.js" />
<t:include file="/ui/assets/js/totiGridDefaultTemplate.js" />
<t:include file="/ui/assets/js/totiDisplay.js" />

<t:include file="/ui/assets/js/totiControl.js" />
<t:include file="/ui/assets/js/totiForm.js" />
<t:include file="/ui/assets/js/totiGrid.js" />

<t:if cond="${totiIdentity.isPresent()}" >
	totiAuth.login({
		"access_token": "${totiIdentity.getToken()}",
		"expires_in": ${totiIdentity.getExpirationTime()}
	});
<t:else>
	<%-- token is in JS but not on server - server restarted OR sync logout --%>
	totiAuth.logout();
</t:if>