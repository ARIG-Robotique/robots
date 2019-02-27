package org.arig.robot.ui.controller;

import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import lombok.AccessLevel;
import lombok.Getter;
import org.arig.robot.constants.IConstantesConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;

public abstract class AbstractSpringDataFxController {

    @FXMLViewFlowContext
    @Getter(AccessLevel.PROTECTED)
    private ViewFlowContext viewFlowContext;

    private ApplicationContext springCtx;

    @PostConstruct
    public void init() throws Exception {
        springCtx = (ApplicationContext) viewFlowContext.getRegisteredObject(IConstantesConfig.keySpringContext);
        if (springCtx != null) {
            springCtx.getAutowireCapableBeanFactory().autowireBean(this);
        }
    }

    protected Resource getResource(String location) {
        return springCtx.getResource(location);
    }
}
