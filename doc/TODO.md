# TOTI TODO taskkist

## Known issue


## TODO

* application
	* potřebuji vyřešit posílání flash message z controlleru do view při not rest api - možnost: mít objekt na jsp parametry, v něm mít speciální místo pro třeba toti_flash. možná by tam mohl být i nonce
		flash:
		využije se session space pro přenos
		response parameters by měly umět automaticky poslat flash, připadně co nejjednodušeji
		toti js bude umět vykreslit? spousta flash bude pro form

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
