package org.arig.robot.nerell.utils.shell;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.arig.robot.Ordonanceur;
import org.arig.robot.constants.IConstantesUtiles;
import org.arig.robot.model.EStrategy;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.nerell.utils.ShellInputReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ShellComponent
@ShellCommandGroup("match")
public class StartMatchCommands {

    @Autowired
    private RobotStatus rs;

    @Autowired
    private ShellInputReader shellInputReader;

    @SneakyThrows
    @ShellMethod("Démarrer un match")
    public void start() throws IOException {

        choixTeam();
        choixStratgies();

        // begin match
        Ordonanceur.getInstance().run();
    }

    private void choixTeam() {
        boolean choixTeam = false;
        do {
            String teamAnswer = shellInputReader.prompt("Choisi ton équipe en entrant le chiffre : \n 1.JAUNE \n 2.BLEU  \n");
            if (StringUtils.isNotBlank(teamAnswer) && StringUtils.isNumeric(teamAnswer)) {
                int teamNumber = Integer.parseInt(teamAnswer);
                if (teamNumber > 0 && teamNumber < 3) {
                    Team team = teamNumber == 1 ? Team.JAUNE : Team.BLEU;
                    System.setProperty(IConstantesUtiles.ENV_PROP_TEAM, team.name());
                    choixTeam = true;
                }
            }
        } while (!choixTeam);

    }

    private void choixStratgies() {
        StringBuilder choixStratQuestion = new StringBuilder("Choisi tes stratégies en entrant les chiffres (séparés par ',' ) : \n");

        List<String> allStrategies = new ArrayList<>(EnumSet.allOf(EStrategy.class))
                .stream()
                .map(EStrategy::name)
                .collect(Collectors.toList());

        for (int i = 0; i < allStrategies.size(); i++) {
            choixStratQuestion
                    .append(i + 1)
                    .append(". : ")
                    .append(allStrategies.get(i))
                    .append("\n");
        }

        choixStratQuestion
                .append((allStrategies.size() + 1))
                .append(". : ")
                .append("Skip")
                .append("\n");

        boolean choixStrategies = false;

        String question = choixStratQuestion.toString();

        String defautChoix = String.valueOf(allStrategies.size() + 1);

        List<String> strategies;

        do {
            strategies = new ArrayList<>();

            String listStrategies = shellInputReader.prompt(question, defautChoix);

            if (StringUtils.isNotBlank(listStrategies)) {

                List<String> strategiesAns = Arrays.asList(listStrategies.split(","));

                for (int i = 0; i < strategiesAns.size(); i++) {
                    if (StringUtils.isNumeric(strategiesAns.get(i))) {

                        int answer = Integer.valueOf(strategiesAns.get(i)) - 1;

                        if (answer < 0 && answer > allStrategies.size()) {
                            choixStrategies = false;
                            break;
                        } else if (answer == allStrategies.size()) {
//                             skip réponse
                            choixStrategies = true;
                            strategies = null;
                            break;
                        } else {
                            choixStrategies = true;
                            strategies.add(allStrategies.get(answer));
                        }
                    } else {
                        choixStrategies = false;
                        break;
                    }
                }
            } else {
                choixStrategies = true;
            }
        } while (!choixStrategies);

        if (CollectionUtils.isNotEmpty(strategies)) {
            System.setProperty(IConstantesUtiles.ENV_PROP_STRATEGIES, strategies.stream().collect(Collectors.joining(",")));
        } else {
            log.info("Tu as choisi aucune stratégie");
        }
    }
}
