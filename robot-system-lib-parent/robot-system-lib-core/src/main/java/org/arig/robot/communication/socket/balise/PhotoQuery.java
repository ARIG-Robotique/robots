package org.arig.robot.communication.socket.balise;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.communication.socket.AbstractQueryWithDatas;
import org.arig.robot.communication.socket.balise.enums.BaliseAction;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class PhotoQuery extends AbstractQueryWithDatas<BaliseAction, PhotoQuery.PhotoQueryParams> {

    @Data
    @AllArgsConstructor
    static class PhotoQueryParams implements Serializable {
        int width;
    }

    public PhotoQuery(int width) {
        super(BaliseAction.PHOTO);
        setDatas(new PhotoQueryParams(width));
    }

}
