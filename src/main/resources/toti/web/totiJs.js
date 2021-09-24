/* TOTI script version 0.1.2 */
<t:include file="js/totiSettings.js" />
<t:include file="js/totiImages.js" />
<t:include file="js/totiTranslations.js" />
<t:include file="js/totiUtils.js" />
<t:include file="js/totiStorage.js" />
<t:include file="js/totiLang.js" />
<t:include file="js/totiLoad.js" />
<t:include file="js/totiAuth.js" />
<t:include file="js/totiDisplay.js" />
<t:include file="js/totiControl.js" />
<t:if cond="${useProfiler}" >
	<t:include file="js/totiProfiler.js" />
</t:if>
<t:include file="js/totiForm.js" />
<t:include file="js/totiGrid.js" />