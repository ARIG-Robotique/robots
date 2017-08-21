package org.arig.robot.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.springframework.beans.factory.InitializingBean;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public class RobotStatus extends AbstractRobotStatus implements InitializingBean {

    @Setter(AccessLevel.NONE)
    private boolean simulateur = false;

    public void setSimulateur() {
        simulateur = true;
    }

    private Team team = Team.UNKNOWN;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private StopWatch matchTime = new StopWatch();

    public void startMatch() {
        matchTime.start();
    }

    public void stopMatch() {
        matchTime.stop();

        // Arrêt de l'asservissement et des moteurs, et tout et tout
        this.disableAsserv();
        this.disableAvoidance();
        this.disableMatch();
        this.disableCalageBordure();
        this.disableMagasin();
        this.disablePinces();
    }

    public long getElapsedTime() {
        return matchTime.getTime();
    }

    @Setter(AccessLevel.NONE)
    private boolean calageBordureEnabled = false;

    public void enableCalageBordure() {
        log.info("Activation calage bordure");
        calageBordureEnabled = true;
    }

    public void disableCalageBordure() {
        log.info("Désactivation calage bordure");
        calageBordureEnabled = false;
    }

    private boolean hasPetitesBalles = false;

    // Pinces
    private final List<ModuleLunaire> moduleLunaireExpected = new LinkedList<>();

    public void addModuleLunaireExpected(ModuleLunaire m) {
        synchronized (moduleLunaireExpected) {
            moduleLunaireExpected.add(m);
        }
    }

    public boolean hasModuleLunaireExpected() {
        synchronized (moduleLunaireExpected) {
            return !moduleLunaireExpected.isEmpty();
        }
    }

    public ModuleLunaire nextModuleLunaireExpected() {
        synchronized (moduleLunaireExpected) {
            ModuleLunaire m = moduleLunaireExpected.get(0);
            moduleLunaireExpected.remove(0);
            return m;
        }
    }

    private ModuleLunaire moduleLunaireDroite;
    private ModuleLunaire moduleLunaireCentre;

    // Magasin module lunaire
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private final List<ModuleLunaire> magasinModule = new LinkedList<>();

    public void addModuleDansMagasin(ModuleLunaire m) {
        synchronized (magasinModule) {
            log.info("RS : ajout module magasin");
            magasinModule.add(m);
        }
    }

    public ModuleLunaire nextModule() {
        synchronized (magasinModule) {
            ModuleLunaire mod = magasinModule.get(0);

            log.info("RS : max extract module {}", mod);

            magasinModule.remove(0);

            return mod;
        }
    }

    public boolean hasModuleDansMagasin() {
        synchronized (magasinModule) {
            int size = magasinModule.size();
            log.info("RS : taille magasin {}", size);
            return size > 0;
        }
    }

    public boolean canAddModuleMagasin() {
        synchronized (magasinModule) {
            return magasinModule.size() < IConstantesNerellConfig.nbModuleMax;
        }
    }

    public Integer nbModulesMagasin() {
        synchronized (magasinModule) {
            return magasinModule.size();
        }
    }

    // Fusées
    private boolean fuseeMonochromeJauneRecupere = false;
    private boolean fuseePolychromeJauneRecupere = false;
    private boolean fuseeMonochromeBleuRecupere = false;
    private boolean fuseePolychromeBleuRecupere = false;

    // Modules sur table
    private Map<Integer, Boolean> modulesRecuperes = new HashMap<>();

    public void setModuleRecupere(Integer numero) {
        modulesRecuperes.put(numero, true);
    }

    public Boolean isModuleRecupere(Integer numero) {
        return modulesRecuperes.get(numero);
    }

    // Cratères
    private boolean cratereZoneDepartBleuRecupere = false;
    private boolean cratereBaseLunaireBleuRecupere = false;
    private boolean cratereImmenseBleuRecupere = false;

    private boolean cratereZoneDepartJauneRecupere = false;
    private boolean cratereBaseLunaireJauneRecupere = false;
    private boolean cratereImmenseJauneRecupere = false;

    @Setter(AccessLevel.NONE)
    private int nbTransfertsElfa = 0;

    public int addTransfertElfa() {
        return ++nbTransfertsElfa;
    }

    @Setter(AccessLevel.NONE)
    private int nbDeposesDepart = 0;

    public int addDeposeDepart() {
        return ++nbDeposesDepart;
    }

    @Setter(AccessLevel.NONE)
    private boolean pincesEnabled = false;

    public void enablePinces() {
        log.info("Activation des pinces");
        pincesEnabled = true;
    }

    public void disablePinces() {
        log.info("Désactivation des pinces");
        pincesEnabled = false;
    }

    @Setter(AccessLevel.NONE)
    private boolean magasinServiceEnable = true;

    public void disableMagasin() {
        magasinServiceEnable = false;
    }

    public void enableMagasin() {
        magasinServiceEnable = true;
    }

    // Base Lunaires
    @Setter(AccessLevel.NONE)
    private Map<Integer, Integer[]> nbModulesDansBase = new HashMap<>();

    public Integer getNbModulesDansBase(Integer numBase) {
        return nbModulesDansBase.get(numBase)[0];
    }

    public Integer nbPlacesDansBase(Integer numBase) {
        return nbModulesDansBase.get(numBase)[1] - nbModulesDansBase.get(numBase)[0];
    }

    public void addModuleDansBase(Integer numBase) {
        nbModulesDansBase.get(numBase)[0]++;
    }

    public boolean canAddModuleDansBase(Integer numBase) {
        return nbModulesDansBase.get(numBase)[0] < nbModulesDansBase.get(numBase)[1];
    }

    public void setBaseFull(Integer numBase) {
        nbModulesDansBase.get(numBase)[0] = nbModulesDansBase.get(numBase)[1];
    }

    /**
     * INIT
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        modulesRecuperes.put(1, false);
        modulesRecuperes.put(2, false);
        modulesRecuperes.put(3, false);
        modulesRecuperes.put(4, false);
        modulesRecuperes.put(5, false);
        modulesRecuperes.put(6, false);
        modulesRecuperes.put(7, false);
        modulesRecuperes.put(8, false);
        modulesRecuperes.put(9, false);
        modulesRecuperes.put(10, false);

        nbModulesDansBase.put(1, new Integer[]{0, 4});
        nbModulesDansBase.put(3, new Integer[]{0, 6});
        nbModulesDansBase.put(2, new Integer[]{0, 6});
        nbModulesDansBase.put(4, new Integer[]{0, 6});
        nbModulesDansBase.put(5, new Integer[]{0, 4});
    }
}
