package org.arig.robot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author gdepuille on 10/05/17.
 */
@Data
@Accessors(chain = true, fluent = true)
@NoArgsConstructor
@AllArgsConstructor
public class ModuleLunaire {

    public enum Type {
        MONOCHROME, POLYCHROME
    }

    private Integer numero;

    private Type type;

    public static ModuleLunaire monochrome() {
        return new ModuleLunaire().type(Type.MONOCHROME);
    }

    public static ModuleLunaire polychrome() {
        return new ModuleLunaire().type(Type.POLYCHROME);
    }

    public boolean isMonochrome() {
        return type == Type.MONOCHROME;
    }

    public boolean isPolychrome() {
        return !isMonochrome();
    }
}
