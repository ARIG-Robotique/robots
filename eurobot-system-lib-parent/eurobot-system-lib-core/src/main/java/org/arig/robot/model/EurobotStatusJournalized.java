package org.arig.robot.model;

public class EurobotStatusJournalized extends EurobotStatus {

    @Override
    public EurobotStatus ecueilEquipePris(boolean ecueilEquipePris) {
        if (ecueilEquipePris) {
            journal.add(new EventLog<>(EStatusEvent.ECUEIL_EQUIPE_PRIS, (byte) 1));
        }
        return super.ecueilEquipePris(ecueilEquipePris);
    }

    @Override
    public EurobotStatus ecueilCommunEquipePris(boolean ecueilCommunEquipePris) {
        if (ecueilCommunEquipePris) {
            journal.add(new EventLog<>(EStatusEvent.ECUEIL_COMMUN_EQUIPE_PRIS, (byte) 1));
        }
        return super.ecueilCommunEquipePris(ecueilCommunEquipePris);
    }

    @Override
    public EurobotStatus ecueilCommunAdversePris(boolean ecueilCommunAdversePris) {
        if (ecueilCommunAdversePris) {
            journal.add(new EventLog<>(EStatusEvent.ECUEIL_COMMUN_ADVERSE_PRIS, (byte) 1));
        }
        return super.ecueilCommunAdversePris(ecueilCommunAdversePris);
    }

    @Override
    public EurobotStatus mancheAAir1(boolean mancheAAir1) {
        if (mancheAAir1) {
            journal.add(new EventLog<>(EStatusEvent.MANCHE_AIR_1, (byte) 1));
        }
        return super.mancheAAir1(mancheAAir1);
    }

    @Override
    public EurobotStatus mancheAAir2(boolean mancheAAir2) {
        if (mancheAAir2) {
            journal.add(new EventLog<>(EStatusEvent.MANCHE_AIR_2, (byte) 1));
        }
        return super.mancheAAir2(mancheAAir2);
    }

    @Override
    public EurobotStatus phare(boolean phare) {
        if (phare) {
            journal.add(new EventLog<>(EStatusEvent.PHARE, (byte) 1));
        }
        return super.phare(phare);
    }

    @Override
    public EurobotStatus pavillon(boolean pavillon) {
        if (pavillon) {
            journal.add(new EventLog<>(EStatusEvent.PAVILLON, (byte) 1));
        }
        return super.pavillon(pavillon);
    }

    @Override
    public void boueePrise(int numero) {
        journal.add(new EventLog<>(EStatusEvent.BOUEE_PRISE, (byte) (numero)));
        super.boueePrise(numero);
    }

    @Override
    public void deposeGrandPort(ECouleurBouee bouee) {
        journal.add(new EventLog<>(EStatusEvent.DEPOSE_GRAND_PORT, (byte) (bouee.ordinal())));
        super.deposeGrandPort(bouee);
    }

    @Override
    public void deposePetitPort(ECouleurBouee bouee) {
        journal.add(new EventLog<>(EStatusEvent.DEPOSE_PETIT_PORT, (byte) (bouee.ordinal())));
        super.deposePetitPort(bouee);
    }

    @Override
    public void deposeGrandChenalRouge(ECouleurBouee... bouees) {
        for (ECouleurBouee bouee : bouees) {
            journal.add(new EventLog<>(EStatusEvent.DEPOSE_GRAND_CHENAL_ROUGE, (byte) (bouee.ordinal())));
        }
        super.deposeGrandChenalRouge(bouees);
    }

    @Override
    public void deposeGrandChenalVert(ECouleurBouee... bouees) {
        for (ECouleurBouee bouee : bouees) {
            journal.add(new EventLog<>(EStatusEvent.DEPOSE_GRAND_CHENAL_VERT, (byte) (bouee.ordinal())));
        }
        super.deposeGrandChenalVert(bouees);
    }

    @Override
    public void deposePetitChenalRouge(ECouleurBouee... bouees) {
        for (ECouleurBouee bouee : bouees) {
            journal.add(new EventLog<>(EStatusEvent.DEPOSE_PETIT_CHENAL_ROUGE, (byte) (bouee.ordinal())));
        }
        super.deposePetitChenalRouge(bouees);
    }

    @Override
    public void deposePetitChenalVert(ECouleurBouee... bouees) {
        for (ECouleurBouee bouee : bouees) {
            journal.add(new EventLog<>(EStatusEvent.DEPOSE_PETIT_CHENAL_VERT, (byte) (bouee.ordinal())));
        }
        super.deposePetitChenalVert(bouees);
    }
}
