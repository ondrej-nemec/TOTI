package toti.http;

public class SslCredentials {

	private String certificateStore;
	private String certificateStorePassword;
	private String certificateType;
	
	private String trustedClientsStore;
	private String clientTrustStorePassword;
	private String clientTrustedType;
	
	private boolean trustAll = true;
	private boolean sniHostCheck = true;
	
	public String getCertificateStore() {
		return certificateStore;
	}
	public String getCertificateStorePassword() {
		return certificateStorePassword;
	}
	public String getCertificateType() {
		return certificateType;
	}
	
	public String getTrustedClientsStore() {
		return trustedClientsStore;
	}
	public String getClientTrustStorePassword() {
		return clientTrustStorePassword;
	}
	public String getClientTrustedType() {
		return clientTrustedType;
	}
	
	public boolean useCertificate() {
		return certificateStore != null;
	}
	
	public boolean useTrustedClients() {
		return trustedClientsStore != null;
	}
	
	public boolean trustAll() {
		return trustAll;
	}
	
	public boolean sniHostCheck() {
		return sniHostCheck;
	}
	
	public void setCertificateStore(String certificateStore, String certificateStorePassword, String certificateType) {
		this.certificateStore = certificateStore;
		this.certificateStorePassword = certificateStorePassword;
		this.certificateType = certificateType;
	}
	
	public void setSniHostCheck(boolean sniHostCheck) {
		this.sniHostCheck = sniHostCheck;
	}
	
	public void setTrustAll(boolean trustAll) {
		this.trustAll = trustAll;
	}
	
	public void setTrustedClientsStore(String trustedClientsStore, String clientTrustStorePassword, String clientTrustedType) {
		this.trustedClientsStore = trustedClientsStore;
		this.clientTrustStorePassword = clientTrustStorePassword;
		this.clientTrustedType = clientTrustedType;
		this.trustAll = false;
	}

	
}
