package toti.lib.tcpip;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import toti.lib.files.access.FileUtils;

public class SSL {
	
	public static SSLContext getSSLContext(SslCredentials credentians) throws Exception {
		SSLContext sc = SSLContext.getInstance("TLS");
		sc.init(
			getCertificateManager(credentians),
			getTrustManager(credentians), 
			getRandom()
		);
		return sc;
	}
	
	public static SecureRandom getRandom() throws NoSuchAlgorithmException {
		if (System.getProperty("os.name").toLowerCase().contains("linux")) {
			 return SecureRandom.getInstance("NativePRNGNonBlocking");
		}
		return SecureRandom.getInstanceStrong();
	}
	
	public static TrustManager[] getTrustManager(SslCredentials credentians) throws Exception {
		if (credentians.useTrustedClients()) {
			KeyStore trustStore = loadStore(
				credentians.getTrustedClientsStore(),
				credentians.getClientTrustStorePassword(),
				credentians.getClientTrustedType()
			);
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(trustStore);
			return tmf.getTrustManagers();
		}
		if (credentians.trustAll()) {
			return new TrustManager[]{
				new X509TrustManager() {
					@Override public X509Certificate[] getAcceptedIssuers() { return null; }
					@Override public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
					@Override public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
				}
			};
		}
		return new TrustManager[]{};
	}
	
	public static KeyManager[] getCertificateManager(SslCredentials credentians) throws Exception {
		if (credentians.useCertificate()) {
			KeyStore keyStore = loadStore(
				credentians.getCertificateStore(),
				credentians.getCertificateStorePassword(),
				credentians.getCertificateType()
			);
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(keyStore, credentians.getCertificateStorePassword().toCharArray());
			return kmf.getKeyManagers();
		}
		return new KeyManager[]{};
	}
	
	private static KeyStore loadStore(String storePath, String password, String type) throws Exception {
		KeyStore store = KeyStore.getInstance(type); // KeyStore.getDefaultType()
		InputStream storeIS = FileUtils.createInputStream(storePath);
		store.load(storeIS, password.toCharArray());
		storeIS.close();
		return store;
	}
	
}
