package toti.extension.templating;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import toti.application.answers.Headers;
import toti.application.answers.request.Identity;
import toti.application.answers.response.ResponseContainer;
import toti.application.answers.response.ResponseException;
import toti.application.application.register.Register;
import toti.application.extensions.Extension;
import toti.extension.templating.parameters.AltParameter;
import toti.extension.templating.parameters.HrefParameter;
import toti.extension.templating.parameters.PlaceholderParameter;
import toti.extension.templating.parameters.SrcParameter;
import toti.extension.templating.parameters.TitleParameter;
import toti.extension.templating.tags.DatetimeFormatTag;
import toti.extension.templating.tags.IfCurrentTag;
import toti.extension.templating.tags.LinkTag;
import toti.extension.templating.tags.PermissionsTag;
import toti.extension.templating.tags.TranslateTag;
import toti.lib.common.structures.MapDictionary;
import toti.lib.files.env.Env;
import toti.lib.tcpip.structures.RequestParameters;
import toti.lib.templating.Parameter;
import toti.lib.templating.Tag;
import toti.lib.templating.Template;
import toti.lib.templating.TemplateFactory;

public class TemplateExtension implements toti.application.extensions.TemplateFactory, Extension {
	
	private final TemplateFactory templateFactory;
	
	private final List<Tag> tags;
	private final List<Parameter> parameters;

	public static final String VARIABLE_NAME_LINK = "__totiExt_link";
	public static final String VARIABLE_NAME_MAPPED_ACTION = "__totiExt_mappedAction";
	public static final String VARIABLE_NAME_AUTHORIZE = "__totiExt_authorize";

	private Authorize authorize;
	
	public TemplateExtension(Env env, Logger logger) {
		this(
			env.getSection("templating").getString("temp"),
			env.getSection("templating").getBoolean("minimalize-templates"),
			env.getSection("templating").getBoolean("delete-temp-java"),
			logger
		);
	}
	
	public TemplateExtension(String tempPath, boolean minimalizeTemplate, boolean deleteAuxFiles, Logger logger) {
		this.tags = new LinkedList<>(Arrays.asList(
			new IfCurrentTag(),
			new LinkTag(),
			new PermissionsTag(),
			new TranslateTag(),
			new DatetimeFormatTag()
		));
		this.parameters = new LinkedList<>(Arrays.asList(
			new AltParameter(),
			new HrefParameter(),
			new SrcParameter(),
			new TitleParameter(),
			new PlaceholderParameter()
		));
		this.templateFactory = new TemplateFactory(
			tempPath, new HashMap<>(),
			deleteAuxFiles, minimalizeTemplate, tags, parameters, logger
		);
		this.authorize = (identity, params)->{
			throw new RuntimeException("TemplateExtension: no Authorize set");
		};
	}

	public void setAuthorize(Authorize authorize) {
		this.authorize = authorize;
	}
	
	public void registerTags(List<Tag> tags) {
		this.tags.addAll(tags);
	}
	
	public void registerParameters(List<Parameter> parameters) {
		this.parameters.addAll(parameters);
	}
	
	public void registerModule(String module, String templatePath) {
		templateFactory.addModule(module, templatePath);
	}

	@Override
	public String getTemplate(String module, String filename, Map<String, Object> params, ResponseContainer container) throws Exception {
		try {
			Template template = templateFactory.getTemplate(module, filename);
			params.put(VARIABLE_NAME_LINK, container.link());
			params.put(VARIABLE_NAME_MAPPED_ACTION, container.mapped());
			params.put(VARIABLE_NAME_AUTHORIZE, authorize);
			return template.create(params);
		} catch (Exception e) {
			throw new ResponseException(e);
		}
	}

	@Override
	public void onApplicationStart() throws Exception {}

	@Override
	public void onApplicationStop() throws Exception {}

	@Override
	public String getIdentifier() {
		return getClass().getName();
	}

	@Override
	public void init(Env appEnv, Register register) {}

	@Override
	public void onRequestStart(Identity identity, MapDictionary<String> sessionSpace, Headers requestHeaders,
		MapDictionary<String> queryParams, RequestParameters requestBody) {}

	@Override
	public void onRequestEnd(Identity identity, MapDictionary<String> sessionSpace, Headers responseHeaders) {}

	protected List<Tag> getTags() {
		return tags;
	}
	
	protected List<Parameter> getParameters() {
		return parameters;
	}
	
}
