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

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509KeyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * When we have multiple private certificates in one key store we can't select the necessary certificate by alias,
 * as default implementations doesn't provide it. So, we use this wrapper, to work with certificate that has
 * certain alias.
 *
 * When this manager is used in SSLContext, then the following method chain:
 * a) chooseEngineServerAlias(String keyType, Principal[] issuers, SSLEngine engine)
 * b) getPrivateKey(String alias)
 * c) getCertificateChain(String alias)
 *
 * When this manager is used in SSLSocketFactory, the the following method chain:
 * a) chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket)
 * b) getPrivateKey(String alias)
 * c) getCertificateChain(String alias)
 *
 * @author Pavel Castornii
 */
public class AliasedX509KeyManager extends X509ExtendedKeyManager {

    private static final Logger logger = LoggerFactory.getLogger(AliasedX509KeyManager.class);

    private final String alias;
    private final X509ExtendedKeyManager keyManager;

    public AliasedX509KeyManager(String keyAlias, X509KeyManager keyManager) {
        this.alias = keyAlias;
        this.keyManager = (X509ExtendedKeyManager) keyManager;
    }

    @Override
    public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
        logger.trace("Alias={} was chosen as client alias", this.alias);
        return this.alias;
    }

    @Override
    public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
        return keyManager.chooseServerAlias(keyType, issuers, socket);
    }

    @Override
    public String[] getClientAliases(String keyType, Principal[] issuers) {
        return keyManager.getClientAliases(keyType, issuers);
    }

    @Override
    public String[] getServerAliases(String keyType, Principal[] issuers) {
        return keyManager.getServerAliases(keyType, issuers);
    }

    @Override
    public X509Certificate[] getCertificateChain(String alias) {
        return keyManager.getCertificateChain(alias);
    }

    @Override
    public PrivateKey getPrivateKey(String alias) {
        return keyManager.getPrivateKey(alias);
    }

    @Override
    public String chooseEngineServerAlias(String keyType, Principal[] issuers, SSLEngine engine) {
        logger.trace("Alias={} was chosen as server alias", this.alias);
        return this.alias;
    }

    @Override
    public String chooseEngineClientAlias(String[] keyType, Principal[] issuers, SSLEngine engine) {
        return keyManager.chooseEngineClientAlias(keyType, issuers, engine);
    }
}
