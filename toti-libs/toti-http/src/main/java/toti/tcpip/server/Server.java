package toti.tcpip.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import org.apache.logging.log4j.Logger;

import toti.common.structures.NamedThredFactory;
import toti.tcpip.SSL;
import toti.tcpip.SslCredentials;

public class Server {
private final Logger logger;
    
    private final ServerSocket serverSocket;
    
    private final ExecutorService executor;
    private final ScheduledExecutorService sheduled;
    private ScheduledFuture<?> clientWaitingFuture;
    
    private int threadCount = 0;
    private final int maxThread;
    
    private final long readTimeOut;
    
    private final String charset;
    
    private final Servant servant;
    
    public Server(
    		int port,
    		int threadPool,
    		long readTimeOut,
    		Servant servant,
    		Optional<SslCredentials> ssl,
    		String charset,
    		Logger logger) throws Exception {
        this.executor = Executors.newFixedThreadPool(threadPool, new NamedThredFactory("client-request"));
        this.sheduled = Executors.newScheduledThreadPool(1, new NamedThredFactory("ji-server"));
        this.logger = logger;
        this.servant = servant;
        this.charset = charset;
        this.maxThread = threadPool;
        this.readTimeOut = readTimeOut;
        
        this.serverSocket = getSocket(port, ssl);
        logger.info("Server prepared " + serverSocket.getInetAddress() + ":" + serverSocket.getLocalPort());
    }
    
    private ServerSocket getSocket(int port, Optional<SslCredentials> ssl) throws Exception {
    	if (ssl.isPresent()) {
    		return getSecuredSocket(port, ssl.get());
    	}
    	logger.warn("No Secured connection credentials, so unsecured server socket used");
    	return getUnsecuredSocket(port);
    }
    
    private ServerSocket getUnsecuredSocket(int port) throws IOException {
    	return new ServerSocket(port);
    }
    
    private ServerSocket getSecuredSocket(int port, SslCredentials ssl) throws Exception {
    	SSLServerSocketFactory factory = SSL.getSSLContext(ssl).getServerSocketFactory();
        
        ServerSocket listener = factory.createServerSocket(port);
        SSLServerSocket sslListener = (SSLServerSocket) listener;

        if (ssl.useTrustedClients()) {
        	sslListener.setNeedClientAuth(true); // client certs required
        } else {
        	sslListener.setNeedClientAuth(false); // client certs not required
        }
        if (Arrays.asList(sslListener.getEnabledProtocols()).contains("TLSv1.3")) {
        	 sslListener.setEnabledProtocols(new String[] {"TLSv1.3"});
        } else { // fix for Java 8
        	sslListener.setEnabledProtocols(new String[] {"TLSv1.2"});
        }
   		return sslListener;
    }

    public synchronized void start() {
    	try {
			serverSocket.setSoTimeout(0);
		} catch (SocketException e) {
			logger.fatal("Server secket waiting time cannot be setted", e);
		}
    	if (clientWaitingFuture == null) {
    		clientWaitingFuture = sheduled.scheduleAtFixedRate(getClientChacker(), 0, 10, TimeUnit.MILLISECONDS);
    	}
        logger.info("Server running");
    }
    
    public synchronized void pause() {
        try {
			serverSocket.setSoTimeout(1);
		} catch (SocketException e) {
			logger.fatal("Server secket waiting time cannot be setted", e);
		}
    	if (clientWaitingFuture != null) {
    		clientWaitingFuture.cancel(true);
	    	clientWaitingFuture = null;
	    	logger.info("Server paused - no serving to clients");
    	}
    }
    
    public void stop() throws InterruptedException {
    	try {
			serverSocket.setSoTimeout(1);
		} catch (SocketException e) {
			logger.fatal("Server secket waiting time cannot be setted", e);
		}
    	logger.info("Stopping server");
    	if (clientWaitingFuture != null) {
    		clientWaitingFuture.cancel(true);
    	}
    	executor.shutdownNow();
        executor.awaitTermination(30, TimeUnit.SECONDS);
        sheduled.shutdownNow();
        sheduled.awaitTermination(30, TimeUnit.SECONDS);
        logger.info("Server stopped");
    }
    
    protected int getActualThreadCount() {
        return threadCount;
    }

    /****************************/
    
    private Runnable getClientChacker() {
        return ()->{
        	if (Thread.currentThread().isInterrupted()) {
        		return;
        	}
            try {
            	logger.trace(String.format("Waiting for client(%d/%d)...", threadCount, maxThread));
            	Socket clientSocket = serverSocket.accept(); // accept is blocking
            	if (Thread.currentThread().isInterrupted()) {
            		return;
            	}
                executor.execute(serveToClient(clientSocket,  charset));
            } catch (SocketTimeoutException e) {
                try {
					logger.warn("Connection closed - reading timeout: " + serverSocket.getSoTimeout());
				} catch (IOException e1) {
					logger.fatal("Preparing sockets", e);
				}
            } catch (IOException e) {
                logger.fatal("Preparing sockets", e);
            }
        };
    }
    
    /*************************/
    
    private Runnable serveToClient(Socket clientSocket, String charset) {
        return ()->{
            threadCount++;
            String client =  clientSocket.getInetAddress() + ":" + clientSocket.getPort();
            logger.trace("Client " + threadCount + " connected - " + client);
            try {
				clientSocket.setSoTimeout((int)readTimeOut);
				servant.serve(clientSocket, charset);
				
		//	} catch (SocketException e) {
		//		logger.error("Read Timeout cannot be setted", e);
			} catch (SocketTimeoutException e) {
                logger.warn("Connection closed - reading timeout");
            } catch (IOException e) {
                logger.fatal("Reading from socket", e);
            } catch (Exception e) {
            	logger.fatal("Problem in Server", e);
            } finally {
            	try {
					clientSocket.close();
				} catch (IOException e) {
					logger.info("Socket closing problem", e);
				}
				threadCount--;
			}
            
            logger.trace("Client " + threadCount + " disconnected - " + client);
        };
    }

}
