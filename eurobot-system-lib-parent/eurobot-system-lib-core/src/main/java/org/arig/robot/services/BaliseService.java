package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.Echantillons;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.Point;
import org.arig.robot.model.StatutDistributeur;
import org.arig.robot.model.Team;
import org.arig.robot.model.balise.StatutBalise;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class BaliseService extends AbstractBaliseService<StatutBalise> {

    @Autowired
    private EurobotStatus rs;

    private StatutBalise statut;

    public void updateStatus() {
        statut = balise.getStatut();

        if (statut != null && statut.detectionOk()) {
            updateDistributeurs();
            updateEchantillons();
        }
    }

    private void updateEchantillons() {
        List<StatutBalise.Echantillon> echantillonsDetectes = statut.getDetection().getEchantillons();
        Echantillons echantillons = rs.echantillons();

        // mise à jour des positions et ajouts des nouveaux
        echantillonsDetectes.forEach(echantillon -> {
            Point point = echantillon.getPoint();
            echantillons.findEchantillon(point, echantillon.getC())
                    .ifPresentOrElse(
                            echantillonExistant -> echantillonExistant.setPt(point),
                            () -> echantillons.addEchantillon(point, echantillon.getC(), rs.team(), rs.siteDeFouillePris(), rs.siteDeFouilleAdversePris())
                    );
        });

        // suppression de ceux qui sont partis
        echantillons.getEchantillons().removeIf(echantillon -> {
            boolean missing = echantillonsDetectes.stream()
                    .noneMatch(e -> Echantillons.match(echantillon, e.getPoint(), e.getC()));
            return missing;
        });

        // sites de fouille et sites de départ
//        boolean siteFouilleJaunePris = true;
//        boolean siteFouilleVioletPris = true;
//        boolean siteEchantillonsJaunePris = true;
//        boolean siteEchantillonsVioletPris = true;
//
//        for (Echantillon e : echantillons.getEchantillons()) {
//            siteFouilleJaunePris &= !echantillons.isInSiteFouilleJaune(e);
//            siteFouilleVioletPris &= !echantillons.isInSiteFouilleViolet(e);
//            siteEchantillonsJaunePris &= !echantillons.isInSiteEchantillonsJaune(e);
//            siteEchantillonsVioletPris &= !echantillons.isInSiteEchantillonsViolet(e);
//        }
//
//        if (!rs.siteDeFouillePris() && (rs.team() == Team.JAUNE && siteFouilleJaunePris || rs.team() == Team.VIOLET && siteFouilleVioletPris)) {
//            group.siteDeFouillePris();
//        }
//        if (!rs.siteDeFouilleAdversePris() && (rs.team() == Team.JAUNE && siteFouilleVioletPris || rs.team() == Team.VIOLET && siteFouilleJaunePris)) {
//            group.siteDeFouilleAdversePris();
//        }
//        if (!rs.siteEchantillonPris() && (rs.team() == Team.JAUNE && siteEchantillonsJaunePris || rs.team() == Team.VIOLET && siteEchantillonsVioletPris)) {
//            group.siteEchantillonPris();
//        }
//        if (!rs.siteEchantillonAdversePris() && (rs.team() == Team.JAUNE && siteEchantillonsVioletPris || rs.team() == Team.VIOLET && siteEchantillonsJaunePris)) {
//            group.siteEchantillonAdversePris();
//        }
    }

    private void updateDistributeurs() {
        List<StatutBalise.PresenceDistrib> distribs = statut.getDetection().getDistribs();

        boolean firstPris = distribs.get(0) == StatutBalise.PresenceDistrib.ABSENT;
        boolean secondPris = distribs.get(1) == StatutBalise.PresenceDistrib.ABSENT;

        if (rs.team() == Team.JAUNE) {
            if (rs.distributeurCommunEquipe() != StatutDistributeur.PRIS_NOUS) {
                StatutDistributeur newStatut = firstPris ? StatutDistributeur.PRIS_BALISE : StatutDistributeur.PAS_PRIS;
                if (rs.distributeurCommunEquipe() != newStatut) {
                    rs.distributeurCommunEquipe(newStatut);
                }
            }
            if (rs.distributeurCommunAdverse() != StatutDistributeur.PRIS_NOUS) {
                StatutDistributeur newStatut = secondPris ? StatutDistributeur.PRIS_BALISE : StatutDistributeur.PAS_PRIS;
                if (rs.distributeurCommunAdverse() != newStatut) {
                    rs.distributeurCommunAdverse(newStatut);
                }
            }
        } else {
            if (rs.distributeurCommunEquipe() != StatutDistributeur.PRIS_NOUS) {
                StatutDistributeur newStatut = secondPris ? StatutDistributeur.PRIS_BALISE : StatutDistributeur.PAS_PRIS;
                if (rs.distributeurCommunEquipe() != newStatut) {
                    rs.distributeurCommunEquipe(newStatut);
                }
            }
            if (rs.distributeurCommunAdverse() != StatutDistributeur.PRIS_NOUS) {
                StatutDistributeur newStatut = firstPris ? StatutDistributeur.PRIS_BALISE : StatutDistributeur.PAS_PRIS;
                if (rs.distributeurCommunAdverse() != newStatut) {
                    rs.distributeurCommunAdverse(newStatut);
                }
            }
        }
    }

}
