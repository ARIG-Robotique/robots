package org.arig.robot.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.arig.robot.model.enums.TypeConsigne;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

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
        types.clear();
        for (final TypeConsigne tc : values) {
            types.add(tc);
        }
    }

    public boolean isType(final TypeConsigne t) {
        return types.contains(t);
    }

    public boolean isAllTypes(final TypeConsigne... types) {
        boolean result = true;
        for (final TypeConsigne t : types) {
            result = result & isType(t);
        }

        return result;
    }

    public synchronized String typeAsserv() {
        Function<TypeConsigne, String> f = TypeConsigne::name;
        Optional<String> res = types.stream().map(f).reduce((a1, a2) -> a1 + "," + a2);
        return res.isPresent() ? res.get() : StringUtils.EMPTY;
    }
}