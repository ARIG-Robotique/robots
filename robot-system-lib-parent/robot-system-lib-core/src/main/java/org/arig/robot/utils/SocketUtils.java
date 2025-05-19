package org.arig.robot.utils;

import lombok.NoArgsConstructor;

import java.net.Socket;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class SocketUtils {

  public static boolean serverListening(final String host, final int port) {
    return serverListening(host, port, 1000);
  }

  public static boolean serverListening(String host, int port, int timeout) {
    try (Socket s = new Socket()) {
      s.setSoTimeout(timeout);
      s.connect(new java.net.InetSocketAddress(host, port), timeout);
      s.close();
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
