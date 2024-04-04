package toti.samples.application.controllers;

import ji.socketCommunication.http.HttpMethod;
import ji.socketCommunication.http.StatusCode;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.answers.action.ResponseAction;
import toti.answers.action.ResponseBuilder;
import toti.answers.response.Response;

@Controller("request")
public class RequestController {
	
	@Action(path="basic")
	public ResponseAction get() {
		return ResponseBuilder.get()
		.createResponse((request, translator, identity)->{
			return Response.create(StatusCode.OK).getText("Working: GET");
		});
	}
	
	@Action(path="basic")
	public ResponseAction get(String parameter) {
		return ResponseBuilder.get()
		.createResponse((request, translator, identity)->{
			return Response.create(StatusCode.OK).getText("Working: GET " + parameter);
		});
	}
	
	@Action(path="post", methods=HttpMethod.POST)
	public ResponseAction post() {
		return ResponseBuilder.get()
		.createResponse((request, translator, identity)->{
			return Response.create(StatusCode.OK).getText("Working: POST");
		});
	}
	
	@Action(path="get-or-post", methods= {HttpMethod.POST, HttpMethod.GET})
	public ResponseAction getOrPost() {
		return ResponseBuilder.get()
		.createResponse((request, translator, identity)->{
			return Response.create(StatusCode.OK).getText("Working: GET or POST " + request.getMethod());
		});
	}
	/*
	?someText=aaaa&someNumber=12&anotherNumber=21&bool=true'
	?names[]=smith.john&names[]=doe.jane&names[]=my.name'
	?rate[0]=273.15&rate[26.85]=300&rate[100]=373.15'
	?map[list][]=273.15&map[list][]=300&map[100]=373.15'
	?age=42&name=my_name&list[]=a&list[]=b&map[a]=aa&map[b]=bb'
	?age=42&name=my_name&list[]=a&list[]=b&map[a]=aa&map[b]=bb'
	?age=42&list[]=a&list[]=b&map[a]=aa&map[b]=bb'
*/
	@Action(path="query")
	public ResponseAction query() {
		return ResponseBuilder.get()
		.createResponse((request, translator, identity)->{
			return Response.create(StatusCode.OK).getJson(request.getQueryParams().toMap());
		});
	}
	// TODO file
	// TODO body + body types

}
