package toti.answers.action;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.structures.RequestParameters;
import ji.translator.Translator;
import toti.answers.request.Identity;
import toti.answers.request.Request;
import toti.answers.response.Response;
import toti.validation.Validator;

public class ResponseBuilder implements Step1, ResponseAction {
	
	private final List<BodyType> allowedBody;
	private Prevalidate prevalidate = (r, t, i)->{};
	private Authorize authorize = (r, t, i)->{};
	private Validate validate = (r, t, i)->{};
	private Create create;
	
	private ResponseBuilder(BodyType[] types) {
		this.allowedBody = Arrays.asList(types);
	}
	
	public static Step1 get() {
		return new ResponseBuilder(BodyType.values());
	}
	
	public static Step1 get(BodyType...bodyTypes) {
		return new ResponseBuilder(bodyTypes);
	}

	@Override
	public Step2 prevalidate(Prevalidate prevalidate) {
		this.prevalidate = prevalidate;
		return this;
	}
	
	@Override
	public Step3 authorize(Authorize authorize) {
		this.authorize = authorize;
		return this;
	}

	@Override
	public Step4 validate(Validate validate) {
		this.validate = validate;
		return this;
	}

	@Override
	public Step4 validate(Validator validator) {
		this.validate = new Validate() {
			@Override
			public void validate(Request request, Translator translator, Identity identity) throws RequestInterruptedException {
				RequestParameters params = new RequestParameters();
				params.putAll(request.getBodyParams().toMap());
				params.putAll(request.getQueryParams().toMap());
				
				Map<String, Set<String>> errors = validator.validate(params, translator);
				if (!errors.isEmpty()) {
					throw new RequestInterruptedException(Response.create(StatusCode.BAD_REQUEST).getJson(errors));
				}
			}
		};
		return this;
	}

	@Override
	public ResponseAction createRequest(Create create) {
		this.create = create;
		return this;
	}

	@Override
	public Prevalidate getPrevalidate() {
		return prevalidate;
	}

	@Override
	public Authorize getAuthorize() {
		return authorize;
	}

	@Override
	public Validate getValidate() {
		return validate;
	}

	@Override
	public Create getCreate() {
		return create;
	}

	@Override
	public List<BodyType> getAllowedBody() {
		return allowedBody;
	}
	
}
