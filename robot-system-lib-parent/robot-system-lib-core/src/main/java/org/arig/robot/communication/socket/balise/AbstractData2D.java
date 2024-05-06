package org.arig.robot.communication.socket.balise;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractData2D implements Serializable {

    private String name;
    private int width;
    private int height;
    private int xfov;
    private int yfov;
    private List<ArucoObject> arucoObjects;
    private List<YoloObject> yoloObjects;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ArucoObject implements Serializable {

        private int index;
        private List<Corner> corners;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Corner implements Serializable {

            private float x;
            private float y;

        }

    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class YoloObject implements Serializable {

        private int index;
        private int tlx;
        private int tly;
        private int brx;
        private int bry;
        private float confidence;

    }

}
