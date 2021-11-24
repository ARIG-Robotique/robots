package org.arig.robot.services;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.model.EStrategy;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.ecran.EcranConfig;
import org.arig.robot.model.ecran.EcranParams;
import org.arig.robot.model.ecran.EcranState;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class EcranService extends AbstractEcranService<EcranConfig, EcranState> {

    public EcranService() {
        super(new EcranState());
    }

    @Override
    public void updateStateInfo(EcranState stateInfos) {
        super.updateStateInfo(stateInfos);
    }

    @Override
    public EcranConfig config() {
        EcranConfig config = super.config();
        if (config == null) {
            config = new EcranConfig();
        }
        return config;
    }

    @Override
    protected EcranParams getParams() {
        EcranParams ecranParams = new EcranParams();
        ecranParams.setTeams(ImmutableMap.of(
                ETeam.JAUNE.name(), "yellow",
                ETeam.VIOLET.name(), "purple"
        ));
        ecranParams.setStrategies(Stream.of(EStrategy.values()).map(Enum::name).collect(Collectors.toList()));
        ecranParams.setOptions(Arrays.asList(IEurobotConfig.OPTION_1, IEurobotConfig.OPTION_2));
        return ecranParams;
    }
}
