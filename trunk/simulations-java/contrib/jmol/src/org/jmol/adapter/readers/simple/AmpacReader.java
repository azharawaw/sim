/* $RCSfile: ADFReader.java,v $
 * $Author: egonw $
 * $Date: 2004/02/23 08:52:55 $
 * $Revision: 1.3.2.4 $
 *
 * Copyright (C) 2002-2004  The Jmol Development Team
 *
 * Contact: jmol-developers@lists.sf.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 *  02111-1307  USA.
 */
package org.jmol.adapter.readers.simple;

import org.jmol.adapter.smarter.*;

import javax.vecmath.Point3f;

/**
 * A reader for AMPAC output. http://www.semichem.com/ampac/
 *
 * @author Bob Hanson (hansonr@stolaf.edu)
 */
public class AmpacReader extends AtomSetCollectionReader {

  private int atomCount;
  private int freqAtom0 = -1;
  private float[] partialCharges;
  private Point3f[] atomPositions;
    
  @Override
  protected boolean checkLine() throws Exception {
    if (line.indexOf("CARTESIAN COORDINATES") >= 0) {
      if (!doGetModel(++modelNumber))
        return checkLastModel();
      readCoordinates();
      return true;
    }
    if (!doProcessLines)
      return true;
    if (line.indexOf("NET ATOMIC CHARGES") >= 0) {
      readPartialCharges();
      return true;
    }
    if (line.indexOf("VIBRATIONAL FREQUENCIES") >= 0) {
      readFrequencies();
      return true;
    }
    return true;
  }

  /**
   * Reads a set of coordinates
   *
   * @exception Exception  if an I/O error occurs
   */
  private void readCoordinates() throws Exception {

    /*
     * 
          CARTESIAN COORDINATES 
       ATOM             X               Y               Z
        1 C         0.00000000      0.00000000      0.00000000
        2 C         1.53481903      0.00000000      0.00000000
        3 C        -0.75577074      1.21222317      0.00000000
        4 C         2.02628667     -0.07447563      1.48386420
        5 C         2.12355282     -1.19057314     -0.80821824
     * 
     */
    boolean haveFreq = (freqAtom0 >= 0);
    if (haveFreq) {
      atomPositions = new Point3f[atomCount];
    } else {
      atomSetCollection.newAtomSet();
    }
    readLine();
    atomCount = 0;
    while (readLine() != null) {
      String[] tokens = getTokens();
      if (tokens.length < 5)
        break;
      if (haveFreq) {
        atomPositions[atomCount] = new Point3f(parseFloat(tokens[2]), parseFloat(tokens[3]), parseFloat(tokens[4]));
      } else {
        String symbol = tokens[1];
        Atom atom = atomSetCollection.addNewAtom();
        atom.elementSymbol = symbol;
        setAtomCoord(atom, parseFloat(tokens[2]), parseFloat(tokens[3]), parseFloat(tokens[4]));
      }
      atomCount++;
    }
    if (haveFreq)
      setPositions();
  }

  private void setPositions() {
    int maxAtom = atomSetCollection.getAtomCount();
    Atom[] atoms = atomSetCollection.getAtoms();
    for (int i = freqAtom0; i <  maxAtom; i++) {
      atoms[i].set(atomPositions[i % atomCount]);
      atoms[i].partialCharge = partialCharges[i % atomCount];  
    }
  }

  private void readPartialCharges() throws Exception {
/*
          NET ATOMIC CHARGES AND DIPOLE CONTRIBUTIONS
         ATOM NO.   TYPE          CHARGE        ATOM ELECTRON DENSITY
           1         C           -0.0264          4.0264
           2         C            0.1005          3.8995
           3         C           -0.0249          4.0249
 */
    readLine();
    partialCharges = new float[atomCount];
    String[] tokens;
    for (int i = 0; i < atomCount; i++) {
      if (readLine() == null || (tokens = getTokens()).length < 4)
        break;
      partialCharges[i] = parseFloat(tokens[2]);
    }
  }


  /*
 VIBRATIONAL FREQUENCIES AND ERRORS (CM-1),
 REDUCED FORCE CONSTANTS (MILLIDYNE/ANGSTROMS),
 DIPOLE DERIVATIVES (DEBYE/ANGSTROMS),
 IR INTENSITIES (DEBYE**2/ANGSTROMS**2),
 AND NORMAL MODES (CARTESIAN COORDINATES):
 FREQ  :    0.000    0.000    0.000    0.000    0.000    0.000   36.593   41.323
 ERROR :    0.000    0.000    0.000    0.000    0.000    0.000    0.232    0.179
 F-CST :  0.00000  0.00000  0.00000  0.00000  0.00000  0.00000  0.00039  0.00050
 DIP(X):    0.000    0.000    0.000    0.000    0.000    0.000    0.360   -0.234
 DIP(Y):    0.000    0.000    0.000    0.000    0.000    0.000    0.042    0.072
 DIP(Z):    0.000    0.000    0.000    0.000    0.000    0.000   -0.075    0.063
 DIP TOT    0.000    0.000    0.000    0.000    0.000    0.000    0.370    0.253
 IR ITEN    0.000    0.000    0.000    0.000    0.000    0.000    0.137    0.064
   1C (x) -0.0580   0.0000   0.0000   0.0000   0.0000   0.0000   0.0015  -0.0144
   1C (y)  0.0004   0.0099   0.0253   0.0282   0.0379  -0.0207   0.0035  -0.0044
   1C (z) -0.0005  -0.0130  -0.0038  -0.0421   0.0193  -0.0325   0.0220   0.0138
   2C (x) -0.0580   0.0000   0.0000   0.0000   0.0000   0.0000   0.0011  -0.0144
   2C (y) -0.0012   0.0005   0.0294   0.0175   0.0538   0.0046   0.0048   0.0030
...
  43H (z) -0.0080  -0.0373  -0.0940  -0.0222   0.0731  -0.0293  -0.2284  -0.0146
 
 FREQ  :   55.945   74.416   92.094  113.796  122.738  127.109  139.067  192.137
 ERROR :    0.423    0.355    0.055    0.229    1.070    2.320    0.418    0.073
 F-CST :  0.00092  0.00163  0.00250  0.00381  0.00444  0.00476  0.00570  0.01088
 DIP(X):    0.080   -0.162    0.156    0.025    0.091    0.113    0.039   -0.032
 DIP(Y):   -0.020   -0.069    0.027    0.044    0.004   -0.012   -0.159    0.044
 DIP(Z):    0.033    0.071    0.105   -0.080    0.012   -0.094    0.136   -0.012
 DIP TOT    0.089    0.190    0.190    0.095    0.091    0.147    0.213    0.056
 IR ITEN    0.008    0.036    0.036    0.009    0.008    0.022    0.045    0.003
   1C (x) -0.0168  -0.0006  -0.0156  -0.0203   0.0031  -0.0024  -0.0143  -0.0095
   1C (y)  0.0068   0.0021   0.0008  -0.0092   0.0076   0.0127  -0.0045   0.0445
   1C (z) -0.0053   0.0112  -0.0319   0.0157  -0.0048  -0.0088   0.0141  -0.0083

   *
   *
   */
  /**
   * Reads a set of vibrations.
   *
   * @exception Exception  if an I/O error occurs
   */
  private void readFrequencies() throws Exception {
    while (readLine() != null && line.indexOf("FREQ  :") < 0) {
    }
    while (line != null && line.indexOf("FREQ  :") >= 0) {
      String[] frequencies = getTokens();
      while (readLine() != null && line.indexOf("IR I") < 0) {
      }
      int iAtom0 = atomSetCollection.getAtomCount();
      if (vibrationNumber == 0)
        freqAtom0 = iAtom0;
      int frequencyCount = frequencies.length - 2;
      boolean[] ignore = new boolean[frequencyCount];
      for (int i = 0; i < frequencyCount; ++i) {
        ignore[i] = !doGetVibration(++vibrationNumber);
        if (ignore[i])
          continue;
        atomSetCollection.cloneLastAtomSet();
        atomSetCollection.setAtomSetName(frequencies[i + 2] + " cm^-1");
        atomSetCollection.setAtomSetProperty("Frequency", frequencies[i + 2]
            + " cm^-1");
        atomSetCollection.setAtomSetProperty(SmarterJmolAdapter.PATH_KEY,
            "Frequencies");
      }
      fillFrequencyData(iAtom0, atomCount, atomCount, ignore, false, 8, 9, null);
      readLine();
      readLine();
    }
  }
  
}
