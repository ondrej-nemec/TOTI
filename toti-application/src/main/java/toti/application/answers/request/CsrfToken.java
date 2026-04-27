package toti.application.answers.request;

public record CsrfToken(String tokenName, String actualToken, boolean hasRequestValidatedToken) {

}
