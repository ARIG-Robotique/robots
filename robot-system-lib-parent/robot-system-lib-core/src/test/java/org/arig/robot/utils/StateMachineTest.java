package org.arig.robot.utils;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

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

    private StateMachine<Key, State, Transition> machine;

    @BeforeEach
    public void init() {
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
                .build();
    }


    @Test
    public void testBasic() {
        final AtomicReference<Key> newKey = new AtomicReference<>();
        final AtomicBoolean hadTransition = new AtomicBoolean();
        machine.onState((key, state, transition) -> {
            newKey.set(key);
            hadTransition.set(transition != null);
        });

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
