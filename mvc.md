# Potřeba

* redirect - DONE - v header Location:
* https - DONE - chybí certifikaty clientů
* session a cookies - DONE

* json parsing - stream parser done

* web services
* grid + formuláře
* poušení controlerů
* upload souborů
* jstl - DONE
* ochrana před útoky - DONE
	* SQL Injection - escapování
	* XSS - v hlavičce: x-xss protection, převod <>"'& při výpisu
		
# hlavičky:
* "X-Frame-Options: ----" = "CSP:frame-ancestors 'none'"	
   - nacteni stranky ve framu
   - SAMEORIGIN (jen na stejne domene)
   - DENY (zadne framy) ALLOW (default)
* "Content-Security-Policy(-Report-Only)" - ktere zdroje se mohou načitat - white list, odděluje ;
	- default-src 'self' - jen z teto domeny
	- img-src, script-src ('self' 'unsafe-inline' 'strict-dynamic'), object-src 'none' - nelze vložit flash
	- aby bylo možne bezpečne použit script primo ve strance, je potřeba použít atribut nonce="nahodne_cislo", totez cislo se musi pridat do script-scr 'nonce-nahodne_cislo'
	- idealni hlavicka
		script-src 'strict-dynamic' 'nonce-nahodne_cislo' 'unsafe-inline' http: https:; object-src 'none'; report-uri url -> json
	- form-action 'self' všechny formuláře na stránce se odesílají pouze na mojí doménu
	- framy jsou vypnuty, musí se zapinat solo
* "Strict-Transport-Security: max-age= ; " + (includeSubDomains;) - vždy navazase spojeni pres https
* "X-Content-Type-Options: nosniff" - striktni typ responsu - musi to poslat ten soubor, ktery je nebezpecny
* "Public-Key-Pins" - lze přijmout https pokud certifikat s verejnym klicem X - muze byt trochu problem, radi se pouzit vice klicu nebo report only mod
* "X-XSS-Protection 1; mode=block" - proti xss, default je 1 ale bez mode=block - muze blokovat chteny js pokud ho poslu v url; proto je lepsi zabezpecit proti xcc serveru nebo vypnout ochranu
	+ report=url tam se pošle json {request url, request body}

* "Access-Control-Allow-Origin: *"
* "Content-Type"
* "Location: " - redirect

* "Set-Cookie" - nastavi cookie, muze byt vycekrat vse za strednikem je nepovinne
	Set-Cookie: <cookie-name>=<cookie-value>; Expires=<date>; Max-Age=<non-zero-digit>; Domain=<domain-value>; Path=<path-value>; Secure; HttpOnly; SameSite=[Strict, lax, none]
		https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Set-Cookie


# ochrana zdroju z cdn
do tagu <script src="..." integrity="--hash--" crossorigin="anonymous"> - pro script a link
- crossorigin: kdo o soubor zada, pridava hlavicku do poradavku, anonymous - nepridavaji se cookies, certs,...

do cookies httponly (pak nejdou ukrast session cookies - nema k ni pristup js, ale jen protokol http) a secure - ??

------------

# generovani certifikatu
openssl genrsa -out rsa-private.key 1024
openssl rsa -in rsa-private.key -out rsa-public.key -pubout -outform PEM

openssl req -new -key rsa-private.key -out csr.pem
openssl x509 -req -days 9999 -in csr.pem -signkey rsa-private.key -out cert.pem

openssl pkcs12 -export -in cert.pem -inkey rsa-private.key -name 127.0.0.1 -out seervercert-PKCS-12.p12
keytool -importkeystore -deststorepass [password] -destkeystore server-keystore.jks -srckeystore seervercert-PKCS-12.p12 -srcstoretype PKCS12

## nacitani cizich jarek
URLClassLoader child = new URLClassLoader(
        new URL[] {myJar.toURI().toURL()},
        this.getClass().getClassLoader()
);
Class classToLoad = Class.forName("com.MyClass", true, child);
Method method = classToLoad.getDeclaredMethod("myMethod");
Object instance = classToLoad.newInstance();
Object result = method.invoke(instance);

## js eval
http://www.java2s.com/Tutorials/Java/Scripting_in_Java/0040__Scripting_in_Java_eval.htm

## web services
https://www.quora.com/How-can-a-server-send-an-update-without-a-client-request - not completed

##  utoky

https://blog.netwrix.com/2018/05/15/top-10-most-common-types-of-cyber-attacks/
https: https://love2dev.com/blog/how-https-works/

### Denial-of-service (DoS) and distributed denial-of-service (DDoS) attacks
zahlcení požadavky

### Man-in-the-middle (MitM) attack
zachytávání komunikace mezi serverem a clientem
- session hijacking - cizi pocitač se tvaří jako původní
- IP Spoolfing - posilani paketu s trusted IP
- Replay - znovu poslani stare zpravy

=> ochrana: správné šifrování, časové razítko na zprávě

### Phishing and spear phishing attacks
poslani emailu, ktery se tvari, ze je od duverihodne osoby

### Drive-by attack
vlozeni skriptu do html nebo php kodu, zda se, ze primo na serveru

### Password attack
prolomeni hesla
- brute-force
- dictionary

=> ochrana - system zamceni uctu po nekolika opakovanich

### SQL injection attack

sql v textu inputu

=> ochrana - escapovani vkladanych paramentru do sql dotazu

### Cross-site scripting (XSS) attack

software tretich stran bezici v prohlizeci

=> ochrana - escapovani html znaku
musi byt v hlavicce "X-XSS-Protection 1; mode=block"  - samo o sobě nestači

https://securityheaders.cz/x-xss-protection

Definice hlavičky X-XSS-Protection
Zakázáný filtr
X-XSS-Protection: 0;
Ochrana XSS je aktivována, prohlížeč se bude snažit stránku čistit a opravit
X-XSS-Protection: 1;
Ochrana XSS je aktivována, prohlížeč při útoku stránku nezobrazí
X-XSS-Protection: 1; mode=block
Ochrana XSS je aktivována, prohlížeč reportuje porušení pravidel
X-XSS-Protection: 1; report=<reporting-uri>
Prohlížeč neblokuje stránku, ale pokusí se ji vyčistit a opravit. Současně provede reporting útoku. Reportování lze využít pouze u Chrome a prohlížečích postavených na jádru WebKit. Data potenciálních XSS útoků jsou odesílány v JSON formátu na uvedenou adresu.

Nastavení X-XSS-Protection
.htaccess
# X-XSS-Protection settings
Header set X-XSS-Protection "1; mode=block"
Reportování na doménu
Header set X-XSS-Protection "1; report=https://domenaxyz.cz/report"
Nginx
add_header X-XSS-Protection "1; mode=block";


### Eavesdropping attack
chytani klicovych slov v zprave

=> ochrana - šifrovani

### Birthday attack
utok na hash - hleda text se stejnym hashem

### Malware attack

## https
generovani certifikatu
openssl genrsa -out key.pem
openssl req -new -key key.pem -out csr.pem
openssl x509 -req -days 9999 -in csr.pem -signkey key.pem -out cert.pem
rm csr.pem

https://deliciousbrains.com/ssl-certificate-authority-for-local-https-development/
https://www.smashingmagazine.com/2017/06/guide-switching-http-https/

ECDHE-RSA-AES256-GCM-SHA384
or example, the setting ECDHE-RSA-AES256-GCM-SHA384 means that the key will be exchanged using the Elliptic
Curve Diffie-Hellman Ephemeral (ECDHE) key exchange algorithm; the CA signed the certificate using the
 Rivest-Shamir-Adleman (RSA) algorithm; the symmetric message encryption will use the Advanced Encryption 
Standard (AES) cipher, with a 256-bit key and GCM mode of operation; and message integrity will be verified
 using the SHA secure hashing algorithm, using 384-bit digests.
 
 keytool -importcert -file certificate.cer -keystore keystore.jks -alias "Alias" 
 keytool -delete -alias keyAlias -keystore keystore-name -storepass password
  
 keytool -genkeypair -alias server -keyalg EC -sigalg SHA384withECDSA -keysize 256 -keystore servercert.p12 -storetype pkcs12 -v -storepass abc123 -validity 10000 -ext san=ip:127.0.0.1
 
 "/c/Program Files/Java/java-1.8.0-openjdk/bin/keytool.exe"
******************
# klic
openssl genrsa -out client.key.pem 4096
# pozadavek k podpisu- Common name - ip nebo hostname
heslo abc123
openssl req -new -key client.key.pem -out client.csr
# generovani certifikatu
openssl x509 -req -days 9999 -in client.csr -signkey client.key.pem -out client.cert.pem

server.keystore.jks abc123
client.keystore.jks abc123

 keytool -importcert -file certificate.cer -keystore keystore.jks -alias "Alias" 
************
keytool -list \
        -keystore [*.p12 file] \
        -storepass [password] \
        -storetype PKCS12 \
        -v
        
/**************/
openssl genrsa -out rsa.private 1024
openssl rsa -in rsa.private -out rsa.public -pubout -outform PEM

openssl req -new -key key.pem -out csr.pem
openssl x509 -req -days 9999 -in rsa.private -signkey key.pem -out cert.pem

openssl pkcs12 -export -in [<em>filename-certificate</em>] -inkey [<em>filename-key</em>] -name [<em>host</em>] -out [<em>filename-new</em>-PKCS-12.p12]
keytool -importkeystore -deststorepass [password] -destkeystore [filename-new-keystore.jks] -srckeystore [filename-new-PKCS-12.p12] -srcstoretype PKCS12