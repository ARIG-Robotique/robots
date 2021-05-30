package org.arig.robot.system.communication;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.arig.robot.communication.socket.AbstractQuery;
import org.arig.robot.communication.socket.AbstractResponse;
import org.arig.robot.communication.socket.GenericResponse;
import org.arig.robot.communication.socket.enums.StatusResponse;
import org.springframework.util.Assert;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public abstract class AbstractSocketServer<T extends Enum<T>> {

    private static final String DATA_INVALID = "DATA_INVALID";
    private static final String DATA_UNPARSABLE = "DATA_UNPARSABLE";

    private final boolean unixSocket;
    private final Executor executor;
    private final AtomicBoolean stop = new AtomicBoolean(false);

    private ServerSocket server;
    private Socket socket;
    private Integer port;

    private OutputStreamWriter out;
    private BufferedReader in;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AbstractSocketServer(final int port, final Executor executor) {
        this.unixSocket = false;
        this.executor = executor;
        this.port = port;
    }

    public AbstractSocketServer(final File socketFile) {
        // TODO https://kohlschutter.github.io/junixsocket/junixsocket-server/index.html
        throw new NotImplementedException("Server socket unix non supporté");
    }

    protected abstract Class<T> getActionEnum();

    protected abstract Class<? extends AbstractQuery<T>> getQueryClass(T action);

    protected abstract AbstractResponse<T> handleQuery(AbstractQuery<T> query);

    public void openSocket() throws IOException {
        stop.set(false);

        if (!unixSocket) {
            Assert.isTrue(port != null && port > 0, "Le port est obligatoire en mode INET");
            server = new ServerSocket(port);
            log.info("Initialisation de la socket INET sur le port {}", port);

            executor.execute(() -> {
                while (!stop.get()) {
                    waitConnectionInet();
                }
            });
        } else {
            // TODO
        }
    }

    public boolean isOpen() {
        return socket != null && socket.isConnected();
    }

    public void end() {
        stop.set(true);

        if (!unixSocket) {
            // il n'y a pas de moyen propre d'interrompre "server.accept" qui est dans l'autre thread
            // donc on kill le serveur...
            closeQuietly(server);
        } else {
            // TODO
        }
    }

    private void waitConnectionInet() {
        try {
            log.debug("Attente de connexion sur la socket ...");
            socket = server.accept();
            socket.setKeepAlive(true);
            log.info("Connexion INET avec le client {}:{}", socket.getInetAddress(), socket.getPort());

            initStreams();
            getQuery();

        } catch (Exception e) {
            if (!stop.get()) {
                log.warn(e.getMessage(), e);
            }
            closeQuietly(in);
            closeQuietly(out);
            closeQuietly(socket);
            socket = null;
        }
    }

    protected void initStreams() throws IOException {
        if (out != null) {
            out.close();
        }
        out = new OutputStreamWriter(socket.getOutputStream());
        if (in != null) {
            in.close();
        }
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    private void getQuery() throws IOException {
        while (true) {
            // lecture de la ligne
            String q = in.readLine();
            if (StringUtils.isBlank(q)) {
                log.warn("Requête vide du client");
                sendError(DATA_INVALID, "Requête vide");
                continue;
            }

            log.debug("Requête : {}", q);

            // raw parsing
            JsonNode rawQuery;
            try {
                rawQuery = objectMapper.readTree(q);
            } catch (Exception e) {
                log.warn("Erreur de lecture du JSON", e);
                sendError(DATA_UNPARSABLE, "Erreur de lecture du JSON");
                continue;
            }

            // détection de la query
            T action = null;
            Class<? extends AbstractQuery<T>> queryClass;
            try {
                String strAction = rawQuery.get("action").asText();
                if (StringUtils.isBlank(strAction)) {
                    throw new NullPointerException("Pas d'attribut action");
                }
                action = Enum.valueOf(getActionEnum(), strAction);
                queryClass = getQueryClass(action);
                if (queryClass == null) {
                    throw new NullPointerException("Le handler n'a pas fourni de classe de requete");
                }
            } catch (Exception e) {
                log.warn("Action {} non supportée", action, e);
                sendError(action == null ? DATA_UNPARSABLE : action.name(), String.format("Action %s non supportée", action));
                continue;
            }

            // parsing complet
            AbstractQuery<T> query;
            try {
                query = objectMapper.convertValue(rawQuery, queryClass);
            } catch (Exception e) {
                log.warn("Erreur de lecture du JSON", e);
                sendError(DATA_UNPARSABLE, "Erreur de lecture du JSON");
                continue;
            }

            AbstractResponse<T> response;
            try {
                response = handleQuery(query);
                if (response == null) {
                    throw new NullPointerException("Le handler n'a pas fourni de réponse");
                }
            } catch (Exception e) {
                log.warn("Erreur d'éxécution", e);
                sendError(action.name(), e.getMessage());
                continue;
            }

            if (response.getStatus() == null) {
                response.setStatus(response.getErrorMessage() != null ? StatusResponse.ERROR : StatusResponse.OK);
            }
            sendResponse(response);
        }
    }

    private void sendError(String action, String message) throws IOException {
        GenericResponse response = new GenericResponse();
        response.setAction(action);
        response.setStatus(StatusResponse.ERROR);
        response.setErrorMessage(message);
        sendResponse(response);
    }

    private void sendResponse(Object response) throws IOException {
        String r = objectMapper.writeValueAsString(response);
        log.debug("Réponse : {}", r);
        out.write(r + "\r\n");
        out.flush();
    }

    private void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
        }
    }
}
