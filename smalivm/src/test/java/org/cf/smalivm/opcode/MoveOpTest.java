package org.cf.smalivm.opcode;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.UnknownValue;
import org.junit.Before;
import org.junit.Test;

public class MoveOpTest {

    private static final String CLASS_NAME = "Lmove_test;";

    private VMState expected;
    private VMState initial;

    @Test
    public void canMoveException() {
        expected.setRegisters(0, new UnknownValue(), "Ljava/lang/Exception;");

        VMTester.test(CLASS_NAME, "moveException()V", expected);
    }

    @Test
    public void canMoveRegisterObject() {
        initial.setRegisters(0, new Object(), "Ljava/lang/Object;");

        // Must invoke VM directly to ensure reference identity
        ExecutionGraph graph = VMTester.execute(CLASS_NAME, "moveRegisterObject()V", initial);
        int[] addresses = graph.getConnectedTerminatingAddresses();
        assertTrue("Should terminate when expected: " + Arrays.toString(addresses) + " == {1}",
                        Arrays.equals(addresses, new int[] { 1 }));

        HeapItem register0 = graph.getRegisterConsensus(1, 0);
        HeapItem register1 = graph.getRegisterConsensus(1, 1);

        assertSame(register0, register1);
        assertTrue(register0 instanceof Object);
    }

    @Test
    public void canMoveRegisterPrimitive() {
        initial.setRegisters(0, 42, "I");
        expected.setRegisters(0, 42, "I", 1, 42, "I");

        VMTester.test(CLASS_NAME, "moveRegisterPrimitive()V", initial, expected);
    }

    @Test
    public void canMoveResult() {
        initial.setRegisters(MethodState.ResultRegister, 42, "I");
        expected.setRegisters(0, 42, "I");

        VMTester.test(CLASS_NAME, "moveResult()V", initial, expected);
    }

    @Before
    public void setUp() {
        expected = new VMState();
        initial = new VMState();
    }

}
