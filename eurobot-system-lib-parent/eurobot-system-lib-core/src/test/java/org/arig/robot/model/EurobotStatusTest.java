package org.arig.robot.model;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.communication.balise.enums.EDirectionGirouette;
import org.arig.robot.utils.EcueilUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

@Slf4j
@RunWith(BlockJUnit4ClassRunner.class)
public class EurobotStatusTest {

    @Test
    @SneakyThrows
    public void testSerialize() {
        // write
        EurobotStatus status1 = new EurobotStatus();
        status1.setTeam(1);
        status1.ecueilCommunEquipePris(true);
        status1.phare(true);
        status1.boueePrise(5);
        status1.boueePrise(10);
        status1.boueePrise(15);
        status1.deposeGrandPort(ECouleurBouee.ROUGE);
        status1.deposeGrandChenalRouge(ECouleurBouee.ROUGE, ECouleurBouee.VERT);
        status1.deposePetitChenalVert(ECouleurBouee.INCONNU, ECouleurBouee.VERT);
        status1.directionGirouette(EDirectionGirouette.UP);
        status1.couleursEcueilCommunEquipe(EcueilUtils.tirageCommunEquipe(ETeam.JAUNE, 1));
        status1.couleursEcueilCommunAdverse(EcueilUtils.tirageCommunAdverse(ETeam.JAUNE, 1));
        status1.hautFond(Arrays.asList(
                new Bouee(0, ECouleurBouee.ROUGE, new Point(100, 100)),
                new Bouee(0, ECouleurBouee.VERT, new Point(300, 100))
        ));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        status1.writeObject(oos); // on n'appelle pas oos.writeObject(status) pour ne pas embarquer les metadata
        oos.close();

        // debug
        log.debug("{}", baos.size());
        StringBuilder baosContent = new StringBuilder();
        for (byte b : baos.toByteArray()) {
            baosContent.append(String.format("0x%02X", b));
            baosContent.append(" ");
        }
        log.debug(baosContent.toString());

        // read
        EurobotStatus status2 = new EurobotStatus();

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        status2.readObject(ois);

        Assert.assertEquals(status1, status2);
    }

}
