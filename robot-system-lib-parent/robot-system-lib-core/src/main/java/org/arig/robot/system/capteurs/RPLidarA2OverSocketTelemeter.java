package org.arig.robot.system.capteurs;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.arig.robot.model.lidar.DeviceInfos;
import org.arig.robot.model.lidar.HealthInfos;
import org.arig.robot.model.lidar.ScanInfos;
import org.arig.robot.model.lidar.communication.*;
import org.arig.robot.model.lidar.communication.enums.LidarStatusResponse;
import org.arig.robot.model.lidar.enums.HealthState;
import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.io.*;
import java.net.Socket;
import java.util.Collections;

/**
 * @author gdepuille on 03/03/17.
 */
@Slf4j
public class RPLidarA2OverSocketTelemeter implements InitializingBean {

    public static short LOW_MORTOR_PWM = 250;
    public static short MAX_MOTOR_PWM = 1023;
    public static short DEFAULT_MOTOR_PWM = 660;

    private final boolean unixSocket;

    private Socket socket;
    private File socketFile;
    private Integer port;
    private String hostname;

    private OutputStreamWriter out;
    private BufferedReader in;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RPLidarA2OverSocketTelemeter(final String hostname, final Integer port) {
        this.unixSocket = false;
        this.hostname = hostname;
        this.port = port;
    }

    public RPLidarA2OverSocketTelemeter(final File socketFile) {
        this.unixSocket = true;
        this.socketFile = socketFile;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // Ouverture de la socket
        if (!unixSocket) {
            Assert.isTrue(StringUtils.isNotBlank(hostname), "Le hostname est obligatoire en mode INET");
            Assert.isTrue(port != null && port > 0, "Le port est obligatoire en mode INET");
            socket = new Socket(this.hostname, this.port);

        } else {
            Assert.notNull(socketFile, "Le fichier pour la socket est obligatoire en mode unix");
            Assert.isTrue(socketFile.exists(), "Le fichier socket n'éxiste pas");
            Assert.isTrue(socketFile.canRead(), "Le fichier socket n'est pas lisible");
            Assert.isTrue(socketFile.canWrite(), "Le fichier socket n'est pas ecrivable");
            socket = AFUNIXSocket.connectTo(new AFUNIXSocketAddress(socketFile));
        }
        socket.setKeepAlive(true);

        // Récupération des IO
        out = new OutputStreamWriter(socket.getOutputStream());
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void printDeviceInfo() {
        DeviceInfos d = deviceInfo();
        log.info("RPLidar A2 version [Firmware : {} ; Hardware {} ; Serial number : {}",
                d.getFirmwareVersion(), d.getHardwareVersion(), d.getSerialNumber());
    }

    public void end() {
        if (socket != null) {
            try {
                socket.shutdownOutput();
                socket.shutdownInput();
            } catch (IOException e) {
                log.warn("Erreur de shutdown sur la socket", e);
            }
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(socket);
        }
    }

    public DeviceInfos deviceInfo() {
        DeviceInfos r;
        try {
            DeviceInfosResponse rawResponse = sendToSocketAndGet(new DeviceInfosQuery(), DeviceInfosResponse.class);
            r = rawResponse.getDatas();
        } catch (Exception e) {
            log.error("Erreur de lecture", e);
            r = new DeviceInfos();
            r.setFirmwareVersion("UNKNOWN");
            r.setHardwareVersion((short) -1);
            r.setSerialNumber("UNKNOWN");
        }

        return r;
    }

    public HealthInfos healthInfo() {
        HealthInfos r;
        try {
            HealthInfosResponse rawResponse = sendToSocketAndGet(new HealthInfosQuery(), HealthInfosResponse.class);
            r = rawResponse.getDatas();
        } catch (Exception e) {
            log.error("Erreur de lecture", e);
            r = new HealthInfos();
            r.setErrorCode((short) -99);
            r.setValue((short) -99);
            r.setLibelle(HealthState.UNKNOWN);
        }

        return r;
    }

    public void startScan() {
        startScan((short) -1);
    }

    public void startScan(Short speed) {
        try {
            StartScanQuery query = new StartScanQuery();
            if (speed > -1) {
                query.setSpeed(speed);
            }
            sendToSocketAndGet(query, StartScanResponse.class);
        } catch (Exception e) {
            log.error("Erreur de lecture", e);
        }
    }

    public void stopScan() {
        try {
            sendToSocketAndGet(new StopScanQuery(), StopScanResponse.class);
        } catch (Exception e) {
            log.error("Erreur de lecture", e);
        }
    }

    public void setSpeed(Short speed) {
        try {
            sendToSocketAndGet(new SetSpeedQuery(speed), SetSpeedResponse.class);
        } catch (Exception e) {
            log.error("Erreur de lecture", e);
        }
    }

    public ScanInfos grabDatas() {
        ScanInfos r;
        try {
            GrabDataResponse rawResponse = sendToSocketAndGet(new GrabDataQuery(), GrabDataResponse.class);
            return rawResponse.getDatas();
        } catch (Exception e) {
            log.error("Erreur de lecture", e);
            r = new ScanInfos();
            r.setIgnored((short) 360);
            r.setScan(Collections.emptyList());
        }

        return r;
    }

    private <Q extends AbstractQuery, R extends AbstractResponse> R sendToSocketAndGet(Q query, Class<R> responseClazz) throws IOException {
        String q = objectMapper.writeValueAsString(query);
        out.write(q + "\r\n");
        out.flush();

        String res = in.readLine();
        R rawResponse = objectMapper.readValue(res, responseClazz);
        if (isResponseError(rawResponse)) {
            throw new IllegalStateException(rawResponse.getErrorMessage());
        }
        return rawResponse;
    }

    private boolean isResponseError(AbstractResponse response) {
        return response.getStatus() == LidarStatusResponse.ERROR;
    }
}
