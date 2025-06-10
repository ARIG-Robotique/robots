package org.arig.robot.utils;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Accessors(fluent = true)
@RequiredArgsConstructor
public class StateMachine<KEY extends Enum<?>, STATE extends Serializable, TRANSITION extends Serializable, OPTION extends Serializable> {

  @FunctionalInterface
  public static interface OnState<KEY extends Enum<?>, STATE extends Serializable, TRANSITION extends Serializable, OPTION extends Serializable> {
    void accept(KEY key, STATE state, TRANSITION transition, OPTION... options);
  }

  private final Map<KEY, STATE> states = new HashMap<>();
  private final Map<Pair<KEY, KEY>, TRANSITION> transitions = new HashMap<>();

  protected final String name;

  @Getter
  @Setter
  private KEY currentState = null;

  @Setter
  private TRANSITION defaultTransition = null;

  @Setter
  private OnState<KEY, STATE, TRANSITION, OPTION> onState;

  @Getter
  @Setter
  private boolean disableCheck = false;

  public Set<KEY> states() {
    return states.keySet();
  }

  public Set<Pair<KEY, KEY>> transisions() {
    return transitions.keySet();
  }

  public void build() {
    // TODO vérifier chaque état à au moins une transition

    assert onState != null : "[" + name + "] onState handler not registered";

    log.info("{} :: states", name);
    states.keySet().stream().sorted().forEach(k -> log.info("  * {}", k));

    log.info("{} :: transitions", name);
    transitions.keySet().stream().sorted().forEach(k -> log.info("  * {} -> {}", k.getLeft(), k.getRight()));
  }

  public StateMachine<KEY, STATE, TRANSITION, OPTION> state(@NonNull KEY key, @NonNull STATE state) {
    assert !states.containsKey(key) : "[" + name + "] " + key.name() + " state already registered";

    states.put(key, state);

    return this;
  }

  public StateMachine<KEY, STATE, TRANSITION, OPTION> transition(@NonNull KEY from, @NonNull KEY to) {
    transition(from, to, null);
    return this;
  }

  public StateMachine<KEY, STATE, TRANSITION, OPTION> transition(@NonNull KEY from, @NonNull KEY to, TRANSITION transition) {
    assert states.containsKey(from) : "[" + name + "] " + from.name() + " state missing";
    assert states.containsKey(to) : "[" + name + "] " + to.name() + " state missing";

    Pair<KEY, KEY> key = Pair.of(from, to);

    assert !transitions.containsKey(key) : "[" + name + "] " + from.name() + "->" + to.name() + " transition already registered";

    transitions.put(key, ObjectUtils.firstNonNull(transition, defaultTransition));

    return this;
  }

  // TODO calcul automatique des différents points de passage si manquant dans "transitions"
  public void goTo(@NonNull KEY to) {
    goTo(to, (OPTION) null);
  }

  public void goTo(@NonNull KEY to, OPTION... option) {
    if (to == currentState) {
      return;
    }

    log.info("{} -> {}", name, to);
    Assert.isTrue(states.containsKey(to), "[" + name + "] " + to.name() + " state not registered");

    if (currentState != null) {
      Pair<KEY, KEY> key = Pair.of(currentState, to);
      if (!disableCheck) {
        Assert.isTrue(transitions.containsKey(key), "[" + name + "] " + currentState.name() + "->" + to.name() + " transition not registered");
      }
      onState.accept(to, states.get(to), transitions.getOrDefault(key, defaultTransition), option);
    } else {
      onState.accept(to, states.get(to), defaultTransition, option);
    }

    currentState = to;
  }

}
