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
import toti.application.extensions.Extension;
import toti.lib.common.structures.MapDictionary;
import toti.lib.files.env.Env;
import toti.lib.tcpip.structures.RequestParameters;
import toti.lib.templating.Parameter;
import toti.lib.templating.Tag;
import toti.lib.templating.Template;
import toti.lib.templating.TemplateFactory;

public class TemplateExtension implements toti.application.extensions.TemplateExtension, Extension {
	
	private final Map<String, TemplateFactory> templateFactories;
	private final String tempPath;
	private final boolean minimalizeTemplate;
	private final boolean deleteAuxFiles;
	private final Logger logger;
	
	private final List<Tag> tags;
	private final List<Parameter> parameters;
	
	public TemplateExtension(Env env, Logger logger) {
		this(
			env.getSection("templating").getString("temp"),
			env.getSection("templating").getBoolean("minimalize-templates"),
			env.getSection("templating").getBoolean("delete-temp-java"),
			logger
		);
	}
	
	public TemplateExtension(String tempPath, boolean minimalizeTemplate, boolean deleteAuxFiles, Logger logger) {
		this.templateFactories = new HashMap<>();
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
		this.tempPath = tempPath;
		this.logger = logger;
		this.deleteAuxFiles = deleteAuxFiles;
		this.minimalizeTemplate = minimalizeTemplate;
	}
	
	public void registerTags(List<Tag> tags) {
		this.tags.addAll(tags);
	}
	
	public void registerParameters(List<Parameter> parameters) {
		this.parameters.addAll(parameters);
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
