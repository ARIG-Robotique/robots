package org.arig.robot.system.communication;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tel.schich.javacan.CanFrame;
import tel.schich.javacan.RawCanChannel;

import java.io.IOException;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class CANRefreshThread extends Thread {

  private final String name;
  private final RawCanChannel channel;
  private final Consumer<CanFrame> callback;

  private boolean running = true;

  public void stopThread() {
    running = false;
  }

  @Override
  public void run() {
    log.info("Start CAN refresh thread for channel {}", name);
    while(running) {
      CanFrame frame;
      try {
        frame = channel.read();
        callback.accept(frame);
      } catch (IOException e) {
        log.warn("Error while reading frame from CAN channel {}", name, e);
      }
    }
    log.info("Stop CAN refresh thread for channel {}", name);
  }
}
