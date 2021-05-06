package org.arig.robot.nerell.utils.shell.providers;

import org.arig.robot.strategy.IAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;
import org.springframework.shell.standard.ValueProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class NerellActionsProvider implements ValueProvider {

    @Autowired
    private List<IAction> actions;

    @Override
    public boolean supports(MethodParameter parameter, CompletionContext completionContext) {
        return true;
    }

    @Override
    public List<CompletionProposal> complete(MethodParameter parameter, CompletionContext completionContext, String[] hints) {
        return actions.stream()
                .filter(a -> a.getClass().getSimpleName().contains(completionContext.currentWordUpToCursor()))
                .map(a -> new CompletionProposal(a.getClass().getSimpleName()))
                .collect(Collectors.toList());
    }

}
