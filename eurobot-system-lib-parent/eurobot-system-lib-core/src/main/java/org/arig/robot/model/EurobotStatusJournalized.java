package org.arig.robot.model;

import java.util.ArrayList;
import java.util.List;

public class EurobotStatusJournalized extends EurobotStatus {

    private final List<JournalEvent> journal = new ArrayList<>();

    @Override
    public EurobotStatus ecueilEquipePris(boolean ecueilEquipePris) {
        journal.add(new JournalEvent(EStatusEvent.ECUEIL_EQUIPE_PRIS, Boolean.toString(ecueilEquipePris)));
        return super.ecueilEquipePris(ecueilEquipePris);
    }

    @Override
    public EurobotStatus ecueilCommunEquipePris(boolean ecueilCommunEquipePris) {
        journal.add(new JournalEvent(EStatusEvent.ECUEIL_COMMUN_EQUIPE_PRIS, Boolean.toString(ecueilCommunEquipePris)));
        return super.ecueilCommunEquipePris(ecueilCommunEquipePris);
    }

    @Override
    public EurobotStatus ecueilCommunAdversePris(boolean ecueilCommunAdversePris) {
        journal.add(new JournalEvent(EStatusEvent.ECUEIL_COMMUN_ADVERSE_PRIS, Boolean.toString(ecueilCommunAdversePris)));
        return super.ecueilCommunAdversePris(ecueilCommunAdversePris);
    }

    @Override
    public EurobotStatus mancheAAir1(boolean mancheAAir1) {
        journal.add(new JournalEvent(EStatusEvent.MANCHE_AIR_1, Boolean.toString(mancheAAir1)));
        return super.mancheAAir1(mancheAAir1);
    }

    @Override
    public EurobotStatus mancheAAir2(boolean mancheAAir2) {
        journal.add(new JournalEvent(EStatusEvent.MANCHE_AIR_2, Boolean.toString(mancheAAir2)));
        return super.mancheAAir2(mancheAAir2);
    }

    @Override
    public EurobotStatus phare(boolean phare) {
        journal.add(new JournalEvent(EStatusEvent.PHARE, Boolean.toString(phare)));
        return super.phare(phare);
    }

    @Override
    public EurobotStatus pavillon(boolean pavillon) {
        journal.add(new JournalEvent(EStatusEvent.PAVILLON, Boolean.toString(pavillon)));
        return super.pavillon(pavillon);
    }

    @Override
    public void boueePrise(int numero) {
        journal.add(new JournalEvent(EStatusEvent.BOUEE_PRISE, Integer.toString(numero)));
        super.boueePrise(numero);
    }

    @Override
    public void deposeGrandPort(ECouleurBouee bouee) {
        journal.add(new JournalEvent(EStatusEvent.DEPOSE_GRAND_PORT, Integer.toString(bouee.ordinal())));
        super.deposeGrandPort(bouee);
    }

    @Override
    public void deposePetitPort(ECouleurBouee bouee) {
        journal.add(new JournalEvent(EStatusEvent.DEPOSE_PETIT_PORT, Integer.toString(bouee.ordinal())));
        super.deposePetitPort(bouee);
    }

    @Override
    public void deposeGrandChenalRouge(ECouleurBouee... bouees) {
        for (ECouleurBouee bouee : bouees) {
            journal.add(new JournalEvent(EStatusEvent.DEPOSE_GRAND_CHENAL_ROUGE, Integer.toString(bouee.ordinal())));
        }
        super.deposeGrandChenalRouge(bouees);
    }

    @Override
    public void deposeGrandChenalVert(ECouleurBouee... bouees) {
        for (ECouleurBouee bouee : bouees) {
            journal.add(new JournalEvent(EStatusEvent.DEPOSE_GRAND_CHENAL_VERT, Integer.toString(bouee.ordinal())));
        }
        super.deposeGrandChenalVert(bouees);
    }

    @Override
    public void deposePetitChenalRouge(ECouleurBouee... bouees) {
        for (ECouleurBouee bouee : bouees) {
            journal.add(new JournalEvent(EStatusEvent.DEPOSE_PETIT_CHENAL_ROUGE, Integer.toString(bouee.ordinal())));
        }
        super.deposePetitChenalRouge(bouees);
    }

    @Override
    public void deposePetitChenalVert(ECouleurBouee... bouees) {
        for (ECouleurBouee bouee : bouees) {
            journal.add(new JournalEvent(EStatusEvent.DEPOSE_PETIT_CHENAL_VERT, Integer.toString(bouee.ordinal())));
        }
        super.deposePetitChenalVert(bouees);
    }
}
