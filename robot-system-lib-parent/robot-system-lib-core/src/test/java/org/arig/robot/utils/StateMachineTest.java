package org.arig.robot.utils;

import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class StateMachineTest {

    enum Key {
        ONE,
        TWO,
        THREE
    }

    class State implements Serializable {

    }

    class Transition implements Serializable {

    }

    class Option implements Serializable {

    }

    private StateMachine<Key, State, Transition, Option> machine;

    @Test
    public void testBasic() {
        final AtomicReference<Key> newKey = new AtomicReference<>();
        final AtomicBoolean hadTransition = new AtomicBoolean();

        machine = new StateMachine<>("test");
        machine
                .state(Key.ONE, new State())
                .state(Key.TWO, new State())
                .state(Key.THREE, new State())

                .transition(Key.ONE, Key.TWO)
                .transition(Key.TWO, Key.ONE)
                .transition(Key.TWO, Key.THREE, new Transition())
                .transition(Key.THREE, Key.ONE)

                .current(Key.ONE)
                .onState((key, state, transition, opt) -> {
            newKey.set(key);
            hadTransition.set(transition != null);
        }).build();

        // 1->2
        machine.goTo(Key.TWO);
        assertEquals(newKey.get(), Key.TWO);
        assertFalse(hadTransition.get());

        // 2->3 w. transition
        machine.goTo(Key.THREE);
        assertEquals(newKey.get(), Key.THREE);
        assertTrue(hadTransition.get());

        // 3->2 impossible
        newKey.set(null);
        try {
            machine.goTo(Key.TWO);
            fail();
        } catch (IllegalArgumentException e) {
            // not fail
        }
        assertNull(newKey.get());
        assertEquals(machine.current(), Key.THREE);

        // 3->1->2->1
        machine.goTo(Key.ONE);
        machine.goTo(Key.TWO);
        machine.goTo(Key.ONE);

        // null->3
        newKey.set(null);
        machine.current(null);
        machine.goTo(Key.THREE);
        assertEquals(newKey.get(), Key.THREE);
        assertFalse(hadTransition.get());
    }

}
