package org.arig.robot.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.springframework.beans.factory.InitializingBean;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
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
    private ModuleLunaire moduleLunaireExpected;
    private ModuleLunaire moduleLunaireDroite;
    private ModuleLunaire moduleLunaireCentre;

    // Magasin module lunaire
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private Deque<ModuleLunaire> magasinModule = new LinkedList<>();

    public void addModuleDansMagasin(ModuleLunaire m) {
        magasinModule.add(m);
    }

    public boolean hasNextModule() {
        return magasinModule.peek() != null;
    }

    public ModuleLunaire nextModule() {
        return magasinModule.poll();
    }

    public boolean hasModuleDansMagasin() {
        return CollectionUtils.isNotEmpty(magasinModule);
    }

    public boolean canAddModuleMagasin() {
        return magasinModule.size() < IConstantesNerellConfig.nbModuleMax;
    }

    public Integer nbModulesMagasin() {
        return magasinModule.size();
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
    private boolean magasinServiceEnable = false;

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
