package org.arig.robot.system.communication;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.arig.robot.communication.socket.AbstractQuery;
import org.arig.robot.communication.socket.AbstractResponse;
import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;
import org.springframework.util.Assert;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

@Slf4j
public abstract class AbstractSocketClient<T extends Enum<T>> {

    private final boolean unixSocket;

    private Socket socket;
    private File socketFile;
    private Integer port;
    private String hostname;
    private int timeout;

    private OutputStreamWriter out;
    private BufferedReader in;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AbstractSocketClient(final String hostname, final Integer port, final int timeout) {
        this.unixSocket = false;
        this.hostname = hostname;
        this.port = port;
        this.timeout = timeout;
    }

    public AbstractSocketClient(final File socketFile) {
        this.unixSocket = true;
        this.socketFile = socketFile;
    }

    @SneakyThrows
    protected void openIfNecessary() {
        if (!isOpen()) {
            openSocket();
        }
    }

    public void openSocket() throws IOException {
        // Ouverture de la socket
        if (!unixSocket) {
            Assert.isTrue(StringUtils.isNotBlank(hostname), "Le hostname est obligatoire en mode INET");
            Assert.isTrue(port != null && port > 0, "Le port est obligatoire en mode INET");
            socket = new Socket();
            socket.connect(new InetSocketAddress(this.hostname, this.port), this.timeout);
            socket.setSoTimeout(this.timeout);
            log.info("Connexion INET au serveur {}:{}", this.hostname, this.port);

        } else {
            Assert.notNull(socketFile, "Le fichier pour la socket est obligatoire en mode unix");
            Assert.isTrue(socketFile.exists(), "Le fichier socket n'éxiste pas");
            Assert.isTrue(socketFile.canRead(), "Le fichier socket n'est pas lisible");
            Assert.isTrue(socketFile.canWrite(), "Le fichier socket n'est pas inscriptible");
            socket = AFUNIXSocket.connectTo(AFUNIXSocketAddress.of(socketFile));
            log.info("Connexion UNIX au fichier {}", socketFile.getAbsolutePath());
        }
        socket.setKeepAlive(true);

        // Récupération des IO
        if (out != null) {
            out.close();
        }
        out = new OutputStreamWriter(socket.getOutputStream());
        if (in != null) {
            in.close();
        }
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public boolean isOpen() {
        return socket != null && socket.isConnected();
    }

    public void end() {
        end(false);
    }

    public void end(boolean force) {
        if (isOpen() || force) {
            try {
                socket.shutdownOutput();
                socket.shutdownInput();
            } catch (IOException e) {
            }
            closeQuietly(in);
            closeQuietly(out);
            closeQuietly(socket);
            socket = null;
        }
    }

    protected <Q extends AbstractQuery<T>, R extends AbstractResponse<T>> R sendToSocketAndGet(Q query, Class<R> responseClazz) throws IllegalStateException {
        try {
            if (isOpen()) {
                String q = objectMapper.writeValueAsString(query);
                log.debug("Requête : {}", q);

                out.write(q + "\r\n");
                out.flush();

                if (responseClazz == null) {
                    return null;
                }

                String res = in.readLine();
                if (res == null) {
                    throw new IOException("Null result");
                }
                log.debug("Réponse : {}", res);

                JsonNode rawReponse = objectMapper.readTree(res);
                String strAction = rawReponse.get("action").asText();
                if (StringUtils.isBlank(strAction)) {
                    throw new IllegalStateException("Réponse vide");
                }
                if (strAction.equals(AbstractSocketServer.DATA_INVALID) || strAction.equals(AbstractSocketServer.DATA_UNPARSABLE)) {
                    throw new IllegalStateException(rawReponse.get("errorMessage").asText());
                }

                R reponse = objectMapper.readValue(res, responseClazz);
                if (reponse.isError()) {
                    throw new IllegalStateException(reponse.getErrorMessage());
                }
                return reponse;
            } else {
                throw new IllegalStateException("Socket non ouverte");
            }
        } catch (SocketTimeoutException e) {
            log.warn("Timeout lors de la requete");
            throw new IllegalStateException("Timeout lors de la requete");
        } catch (IOException e) {
            log.warn("Connexion perdue", e);
            end(true);
            throw new IllegalStateException("Socket perdu");
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
            throw e;
        }
    }

    private void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException var2) {
        }
    }
}
