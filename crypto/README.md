# Cornerstone cryptography

## Bundle signing

Initialize the cryptographic keystore by executing `mvn -f crypto/pom.xml` from inside the `cornerstone` directory.

- This will create the subdirectory `.au.id.raboczi.cornerstone` in your home directory.  If contains:
  - the keystore `keystore.pkcs12` holding the private key used to sign bundles
  - the public certificate `signer.cer` that can be given to whomever wants to trust the signed bundles
- You only need to do this once, unless you delete the `.au.id.raboczi.cornerstone` directory.


## TLS

The following instructions assume that Karaf serves HTTP at port 8181 and HTTPS at port 8443 of the domain `raboczi.id.au`.
Substitute the values for your particular host in what follows.

### Obtain a certificate

If you do not have a certificate already, one can be obtained from [Let's Encrypt](https://letsencrypt.org).

- Install [Certbot](https://certbot.eff.org)
- Execute the following command (Karaf cannot be running while you do this):
  ```
  sudo certbot certonly --standalone --tls-sni-01-port 8443 --http-01-port 8181 -d raboczi.id.au
  ```
- The certificate can be renewed when it is near expiry by the following command (again, while Karaf is not running):
  ```
  sudo certbot renew --standalone --http-01-port 8181
  ```

### Create a keystore

- Install [OpenSSL](https://www.openssl.org)
- Execute the following command to create `keystore.jks`, substituting your own certificate files and keystore password:
  ```
  sudo openssl pkcs12 -export \
    -in /opt/local/etc/letsencrypt/live/raboczi.id.au/fullchain.pem \
    -inkey /opt/local/etc/letsencrypt/live/raboczi.id.au/privkey.pem \
    -out keystore.jks \
    -password pass:mystorepass
  ```

### Configure Karaf

- Copy `keystore.jks` into `$KARAF_HOME/etc/`.
- Edit `$KARAF_HOME/etc/user.properties` to change the password for the admin user "karaf".
- Start karaf with `$KARAF_HOME/bin/karaf`.
- From the karaf console, or by directly editing `etc/org.ops4j.pax.web.cfg`:
  ```
  config:edit org.ops4j.pax.web
  config:property-set org.osgi.service.http.port 8181
  config:property-set org.osgi.service.http.port.secure 8443
  config:property-set org.osgi.service.http.secure.enabled true
  config:property-set org.ops4j.pax.web.ssl.keystore ${karaf.etc}/keystore.jks
  config:property-set org.ops4j.pax.web.ssl.password mystorepass
  config:property-set org.ops4j.pax.web.ssl.keypassword mystorepass
  config:update
  ```
