package org.arig.robot.stats;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.List;

/**
 * Created by mythril on 04/01/14.
 */
public abstract class AbstractFileExporter implements IStatsExporter {

    @Autowired
    @Getter(AccessLevel.PROTECTED)
    private List<IStatsObject> statsObjectList;

    @Getter(AccessLevel.PROTECTED)
    private File file;

    @Override
    public void setFile(String filePath) {
        setFile(new File(filePath));
    }

    @Override
    public void setFile(File f) {
        this.file = f;
    }
}
