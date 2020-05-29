package org.arig.robot.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.arig.robot.model.enums.SensDeplacement;
import org.arig.robot.model.enums.TypeConsigne;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The Class CommandeRobot.
 *
 * @author gdepuille
 */
@Data
public class CommandeRobot {

    /**
     * Position a atteindre pour cette commande
     */
    private Position position;

    /**
     * Commande moteur a appliquer
     */
    private CommandeMoteurPropulsion2Roue moteur;

    /**
     * Vitesse a appliquer
     */
    private VitesseAsservissementPolaire vitesse;

    /**
     * Consigne de déplacement pour l'asservissement
     */
    private ConsigneAsservissementPolaire consigne;

    private boolean frein;

    private SensDeplacement sensDeplacement = SensDeplacement.AUTO;

    @Getter(AccessLevel.NONE)
    private final List<TypeConsigne> types = new ArrayList<>();

    /**
     * Instantiates a new robot consigne.
     */
    public CommandeRobot() {
        position = new Position();
        moteur = new CommandeMoteurPropulsion2Roue();
        vitesse = new VitesseAsservissementPolaire(50, 50);
        consigne = new ConsigneAsservissementPolaire();
        frein = true;
        setTypes(TypeConsigne.DIST, TypeConsigne.ANGLE);
    }

    public void setTypes(final TypeConsigne... values) {
        synchronized (types) {
            types.clear();
            Collections.addAll(types, values);
        }
    }

    public boolean isType(final TypeConsigne... types) {
        for (final TypeConsigne t : types) {
            if(this.types.contains(t)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAllTypes(final TypeConsigne... types) {
        boolean result = true;
        for (final TypeConsigne t : types) {
            result = result & isType(t);
        }

        return result;
    }

    public String typeAsserv() {
        synchronized (types) {
            return types.stream().map(TypeConsigne::name).collect(Collectors.joining(","));
        }
    }
}
