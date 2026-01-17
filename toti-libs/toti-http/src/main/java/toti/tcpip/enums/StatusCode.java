package toti.tcpip.enums;

import toti.lib.common.exceptions.LogicException;

public enum StatusCode {

	CONTINUE(100, "Continue"),
	SWITCHING_PROTOCOL(101, "Switching Protocol"),
	PROCESING(102, "Processing"), //  (WebDAV)
	EARLY_HINTS(103, "Early Hints"),
	
	OK(200, "OK"),
	CREATED(201, "Created"),
	ACCEPTED(202, "Accepted"),
	NON_AUTHORITATIVE_INFORMATION(203, "Non-Authoritative Information"),
	NO_CONTENT(204, "No Content"),
	RESET_CONTENT(205, "Reset Content"),
	PARTIAL_CONTENT(206, "Partial Content"),
	MULTI_STATUS(207, "Multi-Status"), //  (WebDAV)
	ALREADY_REPORTED(208, "Already Reported"), //  (WebDAV)
	IM_USER(226, "IM Used"), //  (HTTP Delta encoding)
	
	MULTIPLE_CHOICE(300, "Multiple Choice"),
	MOVED_PERMANENTLY(301, "Moved Permanently"),
	FOUND(302, "Found"),
	SEE_OTHER(303, "See Other"),
	NOT_MODIFIED(304, "Not Modified"),
	USE_PROXY(305, "Use Proxy"),
	UNUSED(306, "unused"),
	TEMPORARY_REDIRECT(307, "Temporary Redirect"),
	PERMANENT_REDIRECT(308, "Permanent Redirect"),
	
	BAD_REQUEST(400, "Bad Request"),
	UNAUTHORIZED(401, "Unauthorized"),
	PAYMENT_REQUIRED(402, "Payment Required"),
	FORBIDDEN(403, "Forbidden"),
	NOT_FOUND(404, "Not Found"),
	METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
	NOT_ACCEPTABLE(406, "Not Acceptable"),
	PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),
	REQUEST_TIMEOUT(408, "Request Timeout"),
	CONFLICT(409, "Conflict"),
	GONE(410, "Gone"),
	LENGTH_REQUIRED(411, "Length Required"),
	PRECONDITION_FAILED(412, "Precondition Failed"),
	PAYLOAD_TOO_LARGE(413, "Payload Too Large"),
	URI_TOO_LONG(414, "URI Too Long"),
	UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
	REQUESTED_RANGE_NOT_SATISFIABLE(416, "Requested Range Not Satisfiable"),
	EXPECTATION_FAILED(417, "Expectation Failed"),
	I_AM_A_TEAPORT(418, "I'm a teapot"),
	MISDIRECTED_REQUEST(421, "Misdirected Request"),
	UNPROCESSABLE_CONTENT(422, "Unprocessable Content"),
	UNPROCESSABLE_ENTITY(422, "Unprocessable Entity"), //  (WebDAV)
	LOCKED(423, "Locked"), //  (WebDAV)
	FAILED_DEPENDENCY(424, "Failed Dependency (WebDAV)"),
	TOO_EARLY(425, "Too Early"),
	UPGRADE_REQUIRED(426, "Upgrade Required"),
	PRECONDITION_REQUIRED(428, "Precondition Required"),
	TOO_MANY_REQUESTS(429, "Too Many Requests"),
	REQUEST_HEADER_FIELDS_TOO_LARGE(431, "Request Header Fields Too Large"),
	UNAVAILABLE_FOR_LEGAL_REASONS(451, "Unavailable For Legal Reasons"),
	
	INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
	NOT_IMPLEMENTED(501, "Not Implemented"),
	BAD_GATEWAY(502, "Bad Gateway"),
	SERVICE_UNAVAILABLE(503, "Service Unavailable"),
	GATEWAY_TIMEOUT(504, "Gateway Timeout"),
	HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported"),
	VARIANT_ALSO_NEGOTIATES(506, "Variant Also Negotiates"),
	INSUFFICIENT_STORAGE(507, "Insufficient Storage"), //  (WebDAV)
	LOOP_DETECTED(508, "Loop Detected"), //  (WebDAV)
	NOT_EXTENDED(510, "Not Extended"),
	NETWORK_AUTHENTICATION_REQUIRED(511, "Network Authentication Required");
	
	private final String description;
	
	private final int code;
	
	private StatusCode(int code, String descriptin) {
		this.code = code;
		this.description = descriptin;
	}
	
	@Override
	public String toString() {
		return String.format("%d %s", code, description);
	}
	
	public int getCode() {
		return code;
	}
	
	public String getDescription() {
		return description;
	}
	
	public static StatusCode forCode(int code) {
		for (StatusCode statusCode : values()) {
			if (statusCode.getCode() == code) {
				return statusCode;
			}
		}
		throw new LogicException("Wrong status code: " + code);
	}

}
