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

package com.techsenger.toolkit.rmi.server;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.Objects;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;
import org.slf4j.LoggerFactory;

/**
 * Rmi registry.
 *
 * <p>RMI by default is developed for working only with one network interface because it allows to set only
 * one java.rmi.server.hostname. Because of this limitation we make export static for every rmi exporting.
 * see for details https://community.oracle.com/blogs/emcmanus/2006/12/22/multihomed-computers-and-rmi
 *
 * <p>I am still not sure about this statement. Everything seems to work without problems if client and server
 * have access to same SocketFactories.
 *
 * @author Pavel Castornii
 */
public class RmiRegistry {

    public static final class ClientSocketFactory implements RMIClientSocketFactory, Serializable {

        /**
         * Address of the server.
         */
        private InetAddress serverAddress;

        /**
         * Constructor.
         * @param serverAddress of the server.
         */
        public ClientSocketFactory(final InetAddress serverAddress) {
            this.serverAddress = serverAddress;
        }

        /**
         * Creates socket.
         * @param host of the socket.
         * @param serverPort of the socket.
         * @return created socket.
         * @throws IOException if there is an IO error.
         */
        @Override
        public Socket createSocket(final String host, final int serverPort) throws IOException {
            Socket socket = new Socket(serverAddress, serverPort);
            logger.debug("In ClientSocketFactory passed host and port are {} {} used host and port are {} {}",
                    host, serverPort, serverAddress, serverPort);
            return socket;
        }

        /**
         * Creates hashCode of the object.
         * @return hash code.
         */
        @Override
        public int hashCode() {
            //Checkstyle:OFF: MagicNumber
            int hash = 3;
            hash = 53 * hash + Objects.hashCode(this.serverAddress);
            return hash;
            //Checkstyle:ON: MagicNumber
        }

        /**
         * Checks if that object equals to this one.
         * @param obj object to be checked.
         * @return true if it is equal or false if not.
         */
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ClientSocketFactory other = (ClientSocketFactory) obj;
            if (!Objects.equals(this.serverAddress, other.serverAddress)) {
                return false;
            }
            return true;
        }
    }

    public static final class SslClientSocketFactory extends SslRMIClientSocketFactory {

        private InetAddress serverAddress;

        /**
         * Constructor.
         * @param serverAddress of the socket.
         */
        public SslClientSocketFactory(final InetAddress serverAddress) {
            this.serverAddress = serverAddress;
        }

        /**
         * Creates socket.
         * @param host of the socket.
         * @param serverPort of the socket.
         * @return created socket.
         * @throws IOException if there is an IO error.
         */
        @Override
        public Socket createSocket(final String host, final int serverPort) throws IOException {
            SSLSocketFactory sf = (SSLSocketFactory) SSLSocketFactory.getDefault();
            logger.debug("In SslClientSocketFactory passed host and port are {} {}, used host and port are {} {}",
                    host, serverPort, serverAddress, serverPort);
            return sf.createSocket(serverAddress, serverPort);
        }

        /**
         * Creates hashCode of the object.
         * @return hash code.
         */
        @Override
        public int hashCode() {
            //Checkstyle:OFF: MagicNumber
            int hash = 5;
            hash = 71 * hash + Objects.hashCode(this.serverAddress);
            return hash;
            //Checkstyle:ON: MagicNumber
        }

        /**
         * Checks if that object equals to this one.
         * @param obj object to be checked.
         * @return true if it is equal or false if not.
         */
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final SslClientSocketFactory other = (SslClientSocketFactory) obj;
            if (!Objects.equals(this.serverAddress, other.serverAddress)) {
                return false;
            }
            return true;
        }
    }

    /**
     * Server socket factory.
     */
    private static final class ServerSocketFactory implements RMIServerSocketFactory {

        /**
         * Address.
         */
        private InetAddress address;

        /**
         * Constructor.
         * @param address of the socket.
         */
        private ServerSocketFactory(final InetAddress address) {
            this.address = address;
        }

        /**
         * Creates server socket.
         * @param port of the socket.
         * @return created server socket.
         * @throws IOException if there is an IO error.
         */
        @Override
        public ServerSocket createServerSocket(final int port) throws IOException {
            return new ServerSocket(port, 0, address);
        }

        /**
         * Creates hashCode of the object.
         * @return hash code.
         */
        @Override
        public int hashCode() {
            //Checkstyle:OFF: MagicNumber
            int hash = 3;
            hash = 97 * hash + Objects.hashCode(this.address);
            return hash;
            //Checkstyle:ON: MagicNumber
        }

        /**
         * Checks if that object equals to this one.
         * @param obj object to be checked.
         * @return true if it is equal or false if not.
         */
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ServerSocketFactory other = (ServerSocketFactory) obj;
            if (!Objects.equals(this.address, other.address)) {
                return false;
            }
            return true;
        }
    }

    /**
     * Ssl server socket factory.
     */
    private static final class SslServerSocketFactory extends SslRMIServerSocketFactory {

        /**
         * Address.
         */
        private InetAddress address;

        /**
         * Constructor.
         * @param address of the socket.
         * @param clientAuthNeeded true if client must present its ssl certificate or false if mustn't.
         */
        private SslServerSocketFactory(final InetAddress address, final boolean clientAuthNeeded) {
            super(null, null, clientAuthNeeded);
            this.address = address;
        }

        /**
         * Creates server socket.
         * @param port of the socket.
         * @return creates server socket.
         * @throws IOException if there is an IO error.
         */
        @Override
        public ServerSocket createServerSocket(final int port) throws IOException {
            SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            ServerSocket serverSocket = ssf.createServerSocket(port, 0, address);
            //((SSLServerSocket)serverSocket).setEnabledProtocols(
            //new String[]{"TLSv1","TLSv1.1","TLSv1.2","SSLv2Hello"}
            //);
            return serverSocket;
        }

        /**
         * Creates hashCode of the object.
         * @return hash code.
         */
        @Override
        public int hashCode() {
            //Checkstyle:OFF: MagicNumber
            int hash = 3;
            hash = 53 * hash + Objects.hashCode(this.address);
            return hash;
            //Checkstyle:ON: MagicNumber
        }

        /**
         * Checks if that object equals to this one.
         * @param obj object to be checked.
         * @return true if it is equal or false if not.
         */
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final SslServerSocketFactory other = (SslServerSocketFactory) obj;
            if (!Objects.equals(this.address, other.address)) {
                return false;
            }
            return true;
        }
    }

    /**
     * Logger.
     */
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(RmiRegistry.class);

    /**
     * Java RMI Registry.
     */
    private final Registry registry;

    /**
     * Server socket factory.
     */
    private final RMIServerSocketFactory serverSocketFactory;

    /**
     * Client socket factory class must be on classpath of client.
     */
    private final RMIClientSocketFactory clientSocketFactory;

    /**
     * After closing manager is not avalibale - no any thread
     * can export any unicast objects.
     */
    private volatile boolean wasClosed = false;

    /**
     * Configuration of the server.
     */
    private final DefaultRmiServerConfig config;

    /**
     * Constructor.
     * @param config of the server.
     * @throws RemoteException if there is a remote error.
     * @throws UnknownHostException if host wasn't found.
     */
    public RmiRegistry(final DefaultRmiServerConfig config) throws RemoteException, UnknownHostException {
        this.config = config;
        InetAddress serverAddress = InetAddress.getByName(config.getSocketAddress().getHostName());
        if (config.isSsl()) {
            serverSocketFactory = new SslServerSocketFactory(serverAddress, config.isSslClientAuthNeeded());
            clientSocketFactory = new SslClientSocketFactory(serverAddress);
            //serverSocketFactory = new SslRMIServerSocketFactory(null,null,serverConfig.isSslClientAuthNeeded());
            //clientSocketFactory=new SslRMIClientSocketFactory();
            //true - the last argument if client also must send its certificate. To support
            //both side checking we must enable keystore and trustsore both on client and
            //on server
        } else {
            serverSocketFactory = new ServerSocketFactory(serverAddress);
            clientSocketFactory = new ClientSocketFactory(serverAddress);
        }
        //the registry is exported via createRegistry.
        registry = LocateRegistry.createRegistry(config.getSocketAddress().getPort(),
                    clientSocketFactory, serverSocketFactory);
    }

//it is commented because multiple interfaces seem to work and without it.
//    private Remote export(Remote remoteObject,InetSocketAddress sa,ClientSocketFactory csf,ServerSocketFactory ssf){
//        try {
//            //see here https://community.oracle.com/blogs/emcmanus/2006/12/22/multihomed-computers-and-rmi
//            //System.setProperty("java.rmi.server.hostname",sa.getHostName());
//            return UnicastRemoteObject.exportObject(remoteObject,sa.getPort(),csf,ssf);
//        } catch (RemoteException ex) {
//            Logger.getLogger(RmiRegistry.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }

    /**
     * Exports remote objects to Java RMI registry.
     * Be careful. If url is provided then object is binded to public open registry.
     * If you don't want it then use RmiServiceRegistry
     *
     * @param remoteObject that must be exported.
     * @param url of object has it.
     */
    public final void export(final AbstractRmiRemote remoteObject, final String url) {
        //even if we don't create socket factories it can export and without them
        if (wasClosed || (registry == null)) {
            return;
        }
        try {
            //Remote stub=export(...)
            Remote stub;
            stub = UnicastRemoteObject.exportObject(
                    remoteObject, config.getSocketAddress().getPort(), clientSocketFactory, serverSocketFactory);
            if (url != null && remoteObject.getAccess() == RmiRemoteAccess.PUBLIC) {
                registry.rebind(url, stub);
                logger.debug("To public registry instance of {} with url {} was added",
                        remoteObject.getClass().getName(), url);
            }
        } catch (RemoteException ex) {
            logger.error("There was an error exporting object on {}", config.getSocketAddress());
        }
    }

    /**
     * Unexports object from Java RMI Registry.
     * @param remoteObject that must be unexported.
     */
    public final void unexport(final AbstractRmiRemote remoteObject) {
        try {
            UnicastRemoteObject.unexportObject(remoteObject, true);
        } catch (NoSuchObjectException ex) {
            logger.error("There was an error unexporting object on {}", config.getSocketAddress());
        }
    }

    /**
     * Closes this registry.
     */
    public final synchronized void close() {
        wasClosed = true;
        try {
            UnicastRemoteObject.unexportObject(registry, true);
        } catch (NoSuchObjectException ex) {
            logger.error("There was an error closing registry on {}", config.getSocketAddress());
        }
    }
}
