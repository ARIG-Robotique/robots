package org.arig.robot.stats;

import java.io.File;

/**
 * Created by mythril on 04/01/14.
 */
public interface IStatsExporter {

    void setFile(String filePath);

    void setFile(File f);

    void export();

    void end();
}
