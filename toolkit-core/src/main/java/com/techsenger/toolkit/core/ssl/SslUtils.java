/*
 * Copyright 2016-2025 Pavel Castornii.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.techsenger.toolkit.core.ssl;

import java.io.FileInputStream;
import java.security.KeyStore;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public final class SslUtils {

    private static final Logger logger = LoggerFactory.getLogger(SslUtils.class);

    /**
     * See https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html#sslcontext-algorithms .
     */
    private static final String SSL_CONTEXT_PROTOCOL = "TLSv1.3";

    /**
     * See https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html#keystore-types .
     *
     * We don't use JKS as it is proprietary. We use only PKCS12.
     */
    private static final String STORE_TYPE = "PKCS12";

    /**
     * See
     * https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html#keymanagerfactory-algorithms .
     */
    private static final String MANAGER_FACTORY_ALGORITHM = "SunX509";

    /**
     * This context is used on server side. If there are multiple certificates in key store, then one of them
     * (first or not) will be used. So, if there are multiple certificates in key store don't use this method.
     *
     * @return
     */
    public static SSLContext buildContext() throws Exception {
        SSLContext sslContext = SSLContext.getInstance(SSL_CONTEXT_PROTOCOL);
        KeyManagerFactory keyManagerFactory = buildKeyManagerFactory();
        TrustManagerFactory trustManagerFactory = buildTrustManagerFactory();
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
        return sslContext;
    }

    /**
     * This context is used on server side. If there are multiple certificates in key store, then the certificate
     * with the given alias will be used.
     * @param alias always in lowercase see https://bugs.openjdk.java.net/browse/JDK-4891485
     * @return
     */
    public static SSLContext buildContext(String alias) throws Exception {
        SSLContext sslContext = SSLContext.getInstance(SSL_CONTEXT_PROTOCOL);
        KeyManagerFactory keyManagerFactory = buildKeyManagerFactory();
        TrustManagerFactory trustManagerFactory = buildTrustManagerFactory();
        var keyManagers = keyManagerFactory.getKeyManagers();
        wrapX509KeyManagers(alias, keyManagers);
        sslContext.init(keyManagers, trustManagerFactory.getTrustManagers(), null);
        return sslContext;
    }


    /**
     * This factory is used on client side.  If there are multiple certificates in key store, then one of them
     * (first or not) will be used. So, if there are multiple certificates in key store don't use this method.
     * @return
     * @throws Exception
     */
    public static SSLSocketFactory buildSocketFactory() throws Exception {
        SSLContext sslContext = SSLContext.getInstance(SSL_CONTEXT_PROTOCOL);
        KeyManagerFactory keyManagerFactory = buildKeyManagerFactory();
        TrustManagerFactory trustManagerFactory = buildTrustManagerFactory();
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
        return sslContext.getSocketFactory();
    }

    /**
     * This factory is used on client side. If there are multiple certificates in key store, then the certificate
     * with the given alias will be used.
     * @param alias always in lowercase see https://bugs.openjdk.java.net/browse/JDK-4891485
     * @return
     * @throws Exception
     */
    public static SSLSocketFactory buildSocketFactory(String alias) throws Exception {
        SSLContext sslContext = SSLContext.getInstance(SSL_CONTEXT_PROTOCOL);
        KeyManagerFactory keyManagerFactory = buildKeyManagerFactory();
        TrustManagerFactory trustManagerFactory = buildTrustManagerFactory();
        var keyManagers = keyManagerFactory.getKeyManagers();
        wrapX509KeyManagers(alias, keyManagers);
        sslContext.init(keyManagers, trustManagerFactory.getTrustManagers(), null);
        return sslContext.getSocketFactory();
    }

    /**
     * Builds key manager factory.
     * @return
     * @throws Exception
     */
    public static KeyManagerFactory buildKeyManagerFactory() throws Exception {
        String keyStorePassword = System.getProperty("javax.net.ssl.keyStorePassword");
        if (keyStorePassword == null) {
            throw new NullPointerException("KeyStorePassword is null");
        }
        String keyStorePath = System.getProperty("javax.net.ssl.keyStore");
        if (keyStorePath == null) {
            throw new NullPointerException("KeyStorePath is null");
        }
        // Initialise the keystore
        char[] keyStorePasswordArr = keyStorePassword.toCharArray();
        KeyStore keyStore = KeyStore.getInstance(STORE_TYPE);
        try (FileInputStream fis = new FileInputStream(keyStorePath)) {
            keyStore.load(fis, keyStorePasswordArr);
        }
        // Set up the key manager factory
        //It is better not to hardcode algorithm name. See https://security.stackexchange.com/a/98118
        //However, we need to control alias, because of this we can't use KeyManagerFactory.getDefaultAlgorithm()
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(MANAGER_FACTORY_ALGORITHM);
        keyManagerFactory.init(keyStore, keyStorePasswordArr);
        return keyManagerFactory;
    }

    /**
     * Build trust manager factory.
     * @return
     * @throws Exception
     */
    public static TrustManagerFactory buildTrustManagerFactory() throws Exception {
        String trustStorePassword = System.getProperty("javax.net.ssl.trustStorePassword");
        if (trustStorePassword == null) {
            throw new NullPointerException("TrustStorePassword is null");
        }
        String trustStorePath = System.getProperty("javax.net.ssl.trustStore");
        if (trustStorePath == null) {
            throw new NullPointerException("TrustStorePath is null");
        }
        // Initialise the truststore
        char[] trustStorePasswordArr = trustStorePassword.toCharArray();
        KeyStore trustStore = KeyStore.getInstance(STORE_TYPE);
        try (FileInputStream fis = new FileInputStream(trustStorePath)) {
            trustStore.load(fis, trustStorePasswordArr);
        }
        // Set up the trust manager factory
        //It is better not to hardcode algorithm name. See https://security.stackexchange.com/a/98118
        //However, we need to control alias, because of this we can't use TrustManagerFactory.getDefaultAlgorithm()
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(MANAGER_FACTORY_ALGORITHM);
        trustManagerFactory.init(trustStore);
        return trustManagerFactory;
    }

    private static void wrapX509KeyManagers(String alias, KeyManager[] keyManagers) {
        for (int i = 0; i < keyManagers.length; i++) {
            if (keyManagers[i] instanceof X509KeyManager) {
                keyManagers[i] = new AliasedX509KeyManager(alias, (X509KeyManager) keyManagers[i]);
                logger.debug("Wrapped X509KeyManager for alias={}", alias);
            }
        }
    }

    private SslUtils() {
        //empty
    }
}
