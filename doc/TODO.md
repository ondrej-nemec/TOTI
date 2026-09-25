# TOTI TODO taskkist

## Known issue


## Secure
https://stackoverflow.com/questions/24555890/using-a-custom-truststore-in-java-as-well-as-the-default-one

private KeyStore loadStore(String storePath, String password, String type, List<String> certificates) throws Exception {
        KeyStore store = KeyStore.getInstance(type); // KeyStore.getDefaultType()
        if (storePath == null) {
            store.load(null, password.toCharArray());
        } else {
            try (InputStream storeIS = new FileInputStream(storePath)) {
                store.load(storeIS, password.toCharArray());
            }
        }
        for (int i = 0; i < certificates.size(); i++) {
            String certificatePath = certificates.get(i);
            try (FileInputStream certInputStream = new FileInputStream(certificatePath)) {
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                Certificate certificate = certificateFactory.generateCertificate(certInputStream);
                store.setCertificateEntry("trusted" + i, certificate);
            }
        }
        
        return store;
    }

public String toHash(String message, String salt) throws HashException {
        return toHash(message.getBytes(StandardCharsets.UTF_8), salt.getBytes());
    }

    public String toHash(byte[] message, byte[] salt) throws HashException {
        try {
            MessageDigest digest = MessageDigest.getInstance(algoritm);
            digest.update(salt);
            byte[] hash = Base64.getEncoder().encode(
                digest.digest(message)
            );
            return new String(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new HashException(e);
        }
    }

	private String generateSecret() throws Exception {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
        byte[] secret = new byte[32];
        sr.nextBytes(secret);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(secret);
    }


 private OpcUaClient createClient(UaConfig config, Logger logger) throws Exception {
        return OpcUaClient.create(String.format("opc.tcp://%s:%s", config.host(), config.port()), endpoints ->
            endpoints.stream()
            // .filter(e -> e.getSecurityPolicyUri().equals(SecurityPolicy.Basic256Sha256.getUri()))
            .filter(e ->
                e.getSecurityPolicyUri().equals(config.policy().getUri()) && e.getSecurityMode() == config.mode()
            )
            .findFirst(),
            configBuilder->{
                configBuilder
                .setApplicationName(LocalizedText.english("autmes opc ua client"))
                .setApplicationUri(URN);

                if (config.certificate() != null) {
                    UaCertificate cert = config.certificate();
                    try {
                        KeyStore keyStore = certificate(cert.path(), cert.getPassword(), cert.type(), logger);
                        X509Certificate clientCertificate = getClientCertificate(keyStore);
                        X509Certificate[] clientCertificateChain = getClientCertificateChain(keyStore);
                        KeyPair clientKeyPair = getClientKeyPair(clientCertificate, keyStore, cert.getPassword());

                        configBuilder.setKeyPair(clientKeyPair)
                        .setCertificate(clientCertificate)
                        .setCertificateChain(clientCertificateChain);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                configBuilder.setCertificateValidator(new ClientCertificateValidator() {
                    @Override
                    public void validateCertificateChain(List<X509Certificate> certificateChain) throws UaException {
                        // all certificates are valid
                    }

                    @Override
                    public void validateCertificateChain(List<X509Certificate> certificateChain, String applicationUri, String... validHostNames) throws UaException {
                        // all certificates are valid
                    }
                });
                if (config.user() != null) {
                    UaUser user = config.user();
                    configBuilder.setIdentityProvider(new UsernameProvider(user.name(), user.password()));
                }
                configBuilder.setRequestTimeout(uint(5000));
                return configBuilder.build();
            }
        );
    }

    private static KeyStore certificate(String path, char[] password, String type, Logger logger) throws Exception {
        File file = new File(path);
        // Path serverKeyStore = Paths.get(pathName).resolve(certificateFileName);
        if (file.exists()) {
            logger.info("Loading existing certificate");
            KeyStore store = KeyStore.getInstance(type);
            try (InputStream storeIS = Files.newInputStream(file.toPath())) {
                store.load(storeIS, password);
            }
            return store;
        }
        logger.info("Generate new certificate");
        KeyStore keyStore = KeyStore.getInstance(type);
        keyStore.load(null, password);
        KeyPair keyPair = SelfSignedCertificateGenerator.generateRsaKeyPair(2048);
        SelfSignedCertificateBuilder builder = new SelfSignedCertificateBuilder(keyPair)
            .setCommonName("AUTMES OPC UA Client")
            .setOrganization("AUTMES s.r.o.")
            .setOrganizationalUnit("dev")
            .setLocalityName("Hradec Kralove")
            .setCountryCode("CZ")
            .setApplicationUri(URN)
            .addDnsName("localhost")
            .addIpAddress("127.0.0.1");

        Pattern ipAddrPattern = Pattern
            .compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
        // Get as many hostnames and IP addresses as we can listed in the certificate.
        for (String hostname : HostnameUtil.getHostnames("0.0.0.0")) {
            if (ipAddrPattern.matcher(hostname).matches()) {
                builder.addIpAddress(hostname);
            } else {
                builder.addDnsName(hostname);
            }
        }

        X509Certificate certificate = builder.build();

        keyStore.setKeyEntry(CLIENT_ALIAS, keyPair.getPrivate(), password, new X509Certificate[] { certificate });
        try (OutputStream out = Files.newOutputStream(file.toPath())) {
            keyStore.store(out, password);
        }
        return keyStore;
    }

    private static KeyPair getClientKeyPair(X509Certificate clientCertificate, KeyStore keyStore, char[] password)
            throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
        Key clientPrivateKey = keyStore.getKey(CLIENT_ALIAS, password);
        PublicKey serverPublicKey = clientCertificate.getPublicKey();
        return new KeyPair(serverPublicKey, (PrivateKey) clientPrivateKey);
    }

    private static X509Certificate getClientCertificate(KeyStore keyStore) throws KeyStoreException {
        return (X509Certificate) keyStore.getCertificate(CLIENT_ALIAS);
    }

    private static X509Certificate[] getClientCertificateChain(KeyStore keyStore) throws KeyStoreException {
        return Arrays.stream(keyStore.getCertificateChain(CLIENT_ALIAS)).map(X509Certificate.class::cast)
                .toArray(X509Certificate[]::new);

    }


bude potřeba vylepšit hash a crypt - odebrat base64, vstupy byte[], výstup byte[], sůl nepovinná



## TODO

* core
    * mít možnost nastavit def hlavičky podle typu reqestu a respnosu - např. pro soubory cache atd?
	* response addHeader - přidat metodu pro přidání cookie. a pro čtení cookie
	* pro rozlišení async požadavku potřebuji vlastní hlavičku
		Request.Headers["X-Requested-With"] == "XMLHttpRequest";
	* při redirectu to vrací absolute path. To je problém, pokud je tam např. apache proxy na /aaa/...., A asi to bude problém, pokud na serveru poběží více aplikací rozlišených pomocí path. Vyzkoušet.
	* TOTI server factory - dočasně odebrat http2 z secured - websockety failují
	* U def auth nezapomenout na možnost trvání session - aby po n době došlo k automatickému odhlášení - u getUser kontrolovat expiration time? A save user prodlouží dobu?
* lib-database
	* Migrace: vynucení pořadí - vždy podle názvu
	* Builder: vyřešit createView v always migracích
	* query builder - názvy escapovat, aby šli použít mezery a speciální výrazy
	* foreign key - jako název se musí použít FK__tableName__columnName
	* Migrace: dva adresáře, název jednoho obsahuje název druhého, např migrations a migrationsSim - problém - udělat nějaké testy
	* možná usnadnit case? nějaký subobject?
	* fetch vic streamovat
		Základní řešení (optimalizované)
		List<String> result = new ArrayList<>();
		try (PreparedStatement ps = con.prepareStatement(
				sql,
				ResultSet.TYPE_FORWARD_ONLY,
				ResultSet.CONCUR_READ_ONLY
		)) {
			// důležité pro streaming (hlavně u větších datasetů)
			ps.setFetchSize(1000);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					String value = rs.getString(1); // sloupec C

					if (value != null && filter.apply(value)) {
						result.add(value);
					}
				}
			}
		}
		Co je tady důležité (a proč)
		1. TYPE_FORWARD_ONLY
		umožní driveru jet čistě dopředu → nejrychlejší varianta
		žádné scrollování, žádné cache
		2. setFetchSize(...)
		Tohle má obrovský vliv:
		malá hodnota → víc roundtripů do DB
		velká hodnota → víc paměti 👉 typicky: 500–2000 je rozumný základ u PostgreSQL musí být nenulové, jinak to stáhne vše najednou
		3. getString(1) místo názvu sloupce rs.getString(1); je rychlejší než: rs.getString("C");

* TE - UI
	* Vlastní input pro datum a čas vč. zony
	* ujistit se, že select má nějaký přiznak, že je načtený, callback/promise, který se volá po načtení
	* ji select: multi depends?
	* Grid
		* Filtering: switch for like/equals
		* inrow form, dropdown rows
	* Add HTML tags:
		* fieldset (optionally legend)
		* progress
		* input
			* url
			* image
			* search
			* tel

## Improvement

* Routing
	* add lang to route
	* translate routes
* Template <%!  <%@  -- import?
* JS
	* conditions - relations between inputs (if filled - display/max-length/...)
	* datalist - will be working like select - defined data + load
* HTML to PDF
