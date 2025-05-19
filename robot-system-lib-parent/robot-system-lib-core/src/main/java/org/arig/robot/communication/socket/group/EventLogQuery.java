package org.arig.robot.communication.socket.group;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.ArrayUtils;
import org.arig.robot.communication.socket.AbstractQueryWithData;
import org.arig.robot.communication.socket.group.enums.GroupAction;

@EqualsAndHashCode(callSuper = true)
public class EventLogQuery extends AbstractQueryWithData<GroupAction, byte[]> {

  private EventLogQuery() {
    super(GroupAction.EVENT_LOG);
  }

  private EventLogQuery(byte[] data) {
    super(GroupAction.EVENT_LOG, data);
  }

  public static <E extends Enum<E>> EventLogQuery build(E event, byte[] value) {
    byte[] data = new byte[value.length + 1];
    data[0] = (byte) event.ordinal();
    if (value.length > 0) {
      System.arraycopy(value, 0, data, 1, value.length);
    }
    return new EventLogQuery(data);
  }

  @JsonIgnore
  public int getEventOrdinal() {
    return getData()[0];
  }

  @JsonIgnore
  public byte[] getValue() {
    return ArrayUtils.subarray(getData(), 1, getData().length);
  }
}
