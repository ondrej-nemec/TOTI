package toti.extension.templating;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import ji.common.functions.Env;
import ji.common.structures.MapDictionary;
import ji.socketCommunication.http.structures.RequestParameters;
import toti.answers.Headers;
import toti.answers.request.Identity;
import toti.answers.response.ResponseContainer;
import toti.answers.response.ResponseException;
import toti.application.register.Register;
import toti.extension.templating.parameters.AltParameter;
import toti.extension.templating.parameters.HrefParameter;
import toti.extension.templating.parameters.SrcParameter;
import toti.extension.templating.parameters.TitleParameter;
import toti.extension.templating.tags.IfCurrentTag;
import toti.extension.templating.tags.LinkTag;
import toti.extension.templating.tags.PermissionsTag;
import toti.extension.templating.tags.TranslateTag;
import toti.extensions.Extension;
import toti.templating.Parameter;
import toti.templating.Tag;
import toti.templating.Template;
import toti.templating.TemplateFactory;

public class TemplateExtension implements toti.extensions.TemplateExtension, Extension {
	
	private final Map<String, TemplateFactory> templateFactories;
	private final String tempPath;
	private final boolean minimalizeTemplate;
	private final boolean deleteAuxFiles;
	private final Logger logger;
	
	private final List<Tag> tags;
	private final List<Parameter> parameters;
	
	public TemplateExtension(Env env, Logger logger) {
		this(
			env.getModule("templating").getString("temp"),
			env.getModule("templating").getBoolean("minimalize-templates"),
			env.getModule("templating").getBoolean("delete-temp-java"),
			logger
		);
	}
	
	public TemplateExtension(String tempPath, boolean minimalizeTemplate, boolean deleteAuxFiles, Logger logger) {
		this.templateFactories = new HashMap<>();
		this.tags = Arrays.asList(
			new IfCurrentTag(),
			new LinkTag(),
			new PermissionsTag(),
			new TranslateTag()
		);
		this.parameters = Arrays.asList(
			new AltParameter(),
			new HrefParameter(),
			new SrcParameter(),
			new TitleParameter()
		);
		this.tempPath = tempPath;
		this.logger = logger;
		this.deleteAuxFiles = deleteAuxFiles;
		this.minimalizeTemplate = minimalizeTemplate;
	}
	
	public void registerModule(String module, String modulePath, String templatePath) {
		templateFactories.put(module, new TemplateFactory(
			tempPath, templatePath, module, modulePath,templateFactories,
			deleteAuxFiles, minimalizeTemplate, tags, parameters, logger
		));
	}

	@Override
	public String getTemplate(String module, String filename, Map<String, Object> params, ResponseContainer container) throws Exception {
		try {
			TemplateFactory templateFactory = templateFactories.get(module);
			Template template = templateFactory.getTemplate(filename);
			return template.create(templateFactory, params, new TemplateResponseContainer(container));
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
