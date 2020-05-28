package org.arig.robot.model;

import lombok.Builder;
import lombok.Data;
import org.arig.robot.strategy.IAction;

@Builder
@Data
public class ActionSuperviseur {
    private String uuid;
    private int order;
    private String name;
    private boolean valid;

    public static ActionSuperviseur fromAction(IAction a) {
        return ActionSuperviseur.builder()
                .uuid(a.uuid())
                .name(a.name())
                .order(a.order())
                .valid(a.isValid())
                .build();
    }
}
