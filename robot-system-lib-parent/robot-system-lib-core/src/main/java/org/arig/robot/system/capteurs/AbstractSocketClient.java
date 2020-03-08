package org.arig.robot.system.capteurs;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.net.Socket;

@Slf4j
public class AbstractSocketClient<T extends Enum> {

    private final boolean unixSocket;

    private Socket socket;
    private File socketFile;
    private Integer port;
    private String hostname;

    private OutputStreamWriter out;
    private BufferedReader in;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AbstractSocketClient(final String hostname, final Integer port) {
        this.unixSocket = false;
        this.hostname = hostname;
        this.port = port;
    }

    public AbstractSocketClient(final File socketFile) {
        this.unixSocket = true;
        this.socketFile = socketFile;
    }

    public void openSocket() throws Exception {
        // Ouverture de la socket
        if (!unixSocket) {
            Assert.isTrue(StringUtils.isNotBlank(hostname), "Le hostname est obligatoire en mode INET");
            Assert.isTrue(port != null && port > 0, "Le port est obligatoire en mode INET");
            socket = new Socket(this.hostname, this.port);

        } else {
            Assert.notNull(socketFile, "Le fichier pour la socket est obligatoire en mode unix");
            Assert.isTrue(socketFile.exists(), "Le fichier socket n'éxiste pas");
            Assert.isTrue(socketFile.canRead(), "Le fichier socket n'est pas lisible");
            Assert.isTrue(socketFile.canWrite(), "Le fichier socket n'est pas inscriptible");
            socket = AFUNIXSocket.connectTo(new AFUNIXSocketAddress(socketFile));
        }
        socket.setKeepAlive(true);

        // Récupération des IO
        out = new OutputStreamWriter(socket.getOutputStream());
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public boolean isOpen() {
        return socket != null;
    }

    public void end() {
        if (isOpen()) {
            try {
                socket.shutdownOutput();
                socket.shutdownInput();
            } catch (IOException e) {
                log.warn("Erreur de shutdown sur la socket", e);
            }
            closeQuietly(in);
            closeQuietly(out);
            closeQuietly(socket);
        }
    }

    protected <Q extends AbstractQuery<T>, R extends AbstractResponse<T>> R sendToSocketAndGet(Q query, Class<R> responseClazz) throws IOException {
        if (isOpen()) {
            String q = objectMapper.writeValueAsString(query);
            out.write(q + "\r\n");
            out.flush();

            String res = in.readLine();
            R rawResponse = objectMapper.readValue(res, responseClazz);
            if (rawResponse.isError()) {
                throw new IllegalStateException(rawResponse.getErrorMessage());
            }
            return rawResponse;
        }
        else {
            throw new IllegalStateException("Socket non ouvert");
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
