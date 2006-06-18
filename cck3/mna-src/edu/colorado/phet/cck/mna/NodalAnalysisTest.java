/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck.mna;

/**
 * User: Sam Reid
 * Date: Jun 18, 2006
 * Time: 4:16:22 PM
 * Copyright (c) Jun 18, 2006 by Sam Reid
 */

public class NodalAnalysisTest {
    public static void main( String[] args ) {
        String[]netlist = new String[]{
                "I1 0 1 10",
                "R1 1 0 1"
        };
        MNACircuit circuit = new MNACircuit();
        circuit.parseNetList( netlist );
        MNACircuit.MNASystem system = circuit.getMNASystem();
        System.out.println( "system = " + system );

    }
}
