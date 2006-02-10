/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see: 
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.numerics;
import org.opensourcephysics.controls.OSPLog;

/**
 * ODEMultistepSolver performs multiple ODE steps so that a uniform step size is maintained.
 *
 * @author       Wolfgang Christian
 * @version 1.0
 */
public class ODEMultistepSolver implements ODEAdaptiveSolver {
  private static int maxMessages = 4; // maximum number of error messages
  public final static int NO_ERROR = 0;
  public final static int CONVERGE_ERROR = 1;
  protected int err_code = NO_ERROR;
  protected String err_msg = "";
  protected ODEAdaptiveSolver odeEngine;
  protected double fixedStepSize = 0.1;
  protected InternalODE internalODE;

  /**
   * Constructs an ODEMultiStep ODE solver for a system of ordinary  differential equations.
   *
   * The default ODESolver is RK45.  Other solvers can be selected using factory methods.
   *
   * @param ode
   */
  public ODEMultistepSolver(ODE ode) {
    odeEngine = new RK45(setODE(ode));
  }

  /**
   * Constructs a ODEMultistepSolver without an ODE so that a factory method
   * can create a custom solver.
   */
  protected ODEMultistepSolver() {}

  /**
   * Sets the ODE for this solver.
   * @param ode ODE
   * @return MyODE
   */
  protected InternalODE setODE(ODE ode) {
    internalODE = new InternalODE(ode);
    return internalODE;
  }

  /**
   * A factory method that creates a multisetp solver using the RK45 engine.
   * @param ode ODE
   * @return ODESolver
   */
  public static ODEAdaptiveSolver MultistepRK45(ODE ode) {
    ODEMultistepSolver multistepSolver = new ODEMultistepSolver();
    multistepSolver.odeEngine = new RK45(multistepSolver.setODE(ode));
    return multistepSolver;
  }

  /**
   * Sets the tolerance of the adaptive ODE sovler.
   * @param tol the tolerance
   */
  public void setTolerance(double tol) {
    odeEngine.setTolerance(tol);
  }

  /**
   * Gets the tolerance of the adaptive ODE sovler.
   * @return
   */
  public double getTolerance() {
    return odeEngine.getTolerance();
  }

  /**
   * Steps (advances) the differential equations by the stepSize.
   *
   * The ODESolver invokes the ODE's getRate method to obtain the initial state of the system.
   * The ODESolver then advances the solution and copies the new state into the
   * state array at the end of the solution step if desired tolerance was reached.
   *
   * @return the actual step
   */
  public double step() {
    internalODE.setInitialConditions(); // stores the ode's initial conditions
    double remainder = 0;
    if(fixedStepSize>0) {
      remainder = plus();
    } else {
      remainder = minus();
    }
    if(err_code==0) {
      internalODE.update();           // updates the ode if the solution converged
      return fixedStepSize-remainder; // the step size that was actually taken
    }
    return 0;
  }

  /**
   * Steps the ode with a postive stepsize.
   *
   * @return the step size
   */
  private double plus() { // positive step size
    double tol = odeEngine.getTolerance();
    double remainder = fixedStepSize; // track the remaining step
    if((odeEngine.getStepSize()<=0)||(                // is the stepsize postive?
            odeEngine.getStepSize()>fixedStepSize)||( // is the stepsize larger than what is requested?
              fixedStepSize-odeEngine.getStepSize()==fixedStepSize // is the stepsize smaller than the precision?
                )) {
      odeEngine.setStepSize(fixedStepSize); // reset the step size and let it adapt to an optimum size
    }
    while(remainder>tol*fixedStepSize) {      // check to see if we are close enough
      double oldRemainder = remainder;
      if(remainder<odeEngine.getStepSize()) { // temporarily reduce the step size so that we hit the exact dt value
        double tempStep = odeEngine.getStepSize(); // save the current optimum step size
        odeEngine.setStepSize(remainder); // set the step size to the remainder
        double delta = odeEngine.step();
        remainder -= delta;
        odeEngine.setStepSize(tempStep);  // restore the original step size
      } else {
        remainder -= odeEngine.step();    // do a step and set the remainder
      }
      // check to see if roundoff error prevents further calculation.
      if((Math.abs(oldRemainder-remainder)<=100*Double.MIN_VALUE)||(tol*fixedStepSize/100.0>odeEngine.getStepSize())) {
        err_msg = "ODEMultiStep did not converge. Remainder="+remainder;
        err_code = CONVERGE_ERROR;
        if(maxMessages>0) {
          maxMessages--;
          OSPLog.warning(err_msg);
        }
        break;
      }
    }
    return remainder;
  }

  /**
   * Steps the ode with a negative stepsize.
   *
   * @return the step size
   */
  private double minus() { // negative step size
    double tol = odeEngine.getTolerance();
    double remainder = fixedStepSize; // track the remaining step
    if((odeEngine.getStepSize()>=0)||(                // is the step negative?
            odeEngine.getStepSize()<fixedStepSize)||( // is the stepsize larger than what is requested?
              fixedStepSize-odeEngine.getStepSize()==fixedStepSize // is the stepsize smaller than the precision?
                )) {
      odeEngine.setStepSize(fixedStepSize); // reset the step size and let it adapt to an optimum size
    }
    while(remainder<tol*fixedStepSize) {           // check to see if we are close enough
      double oldRemainder = remainder;
      if(remainder>odeEngine.getStepSize()) {
        double tempStep = odeEngine.getStepSize(); // save the current optimum step size
        odeEngine.setStepSize(remainder); // set the step RK4/5 size to the remainder
        double delta = odeEngine.step();
        remainder -= delta;
        odeEngine.setStepSize(tempStep); // restore the original step size
      } else {
        remainder -= odeEngine.step();   // do a step and set the remainder
      }
      // check to see if roundoff error prevents further calculation.
      if((Math.abs(oldRemainder-remainder)<=100*Double.MIN_VALUE)||(tol*fixedStepSize/100.0<odeEngine.getStepSize())) {
        err_msg = "ODEMultiStep did not converge. Remainder="+remainder;
        err_code = CONVERGE_ERROR;
        if(maxMessages>0) {
          maxMessages--;
          OSPLog.warning(err_msg);
        }
      }
    }
    return remainder;
  }

  /**
   * Initializes the ODE solver.
   *
   * Temporary arrays may be allocated within the ODE solver.
   *
   * @param stepSize
   */
  public void initialize(double stepSize) {
    maxMessages = 4; // reset the message counter to produce more messages
    err_msg = "";
    err_code = NO_ERROR;
    fixedStepSize = stepSize;
    internalODE.setInitialConditions();
    odeEngine.initialize(stepSize);
  }

  /**
   * Sets the fixed step size.  Multi-step solvers will perform one or more internal steps
   * in order to perform a step with the given size.
   *
   * @param stepSize
   */
  public void setStepSize(double stepSize) {
    maxMessages = 4;          // reset the message counter to produce more messages
    fixedStepSize = stepSize; // the fixed step size
    if(stepSize<0) {
      odeEngine.setStepSize(Math.max(-Math.abs(odeEngine.getStepSize()), stepSize));
    } else { // stepSize is positive
      odeEngine.setStepSize(Math.min(odeEngine.getStepSize(), stepSize));
    }
  }

  /**
   * Gets the step size.
   *
   * The step size is the fixed step size, not the size of the ODEAdaptiveSolver steps that are combined into a single step.
   *
   * @return the step size
   */
  public double getStepSize() {
    return fixedStepSize;
  }

  /**
   * A class that saves an internal state that may be different from the orginal ODE.
   * This internal state is used with interpolation solvers.
   */
  protected final class InternalODE implements ODE {
    private ODE ode;
    private double[] engineState = new double[0];

    InternalODE(ODE ode) {
      this.ode = ode;
      setInitialConditions();
    }

    /**
     * Gets the rate using the given state.
     *
     * @param state double[]
     * @param rate double[]
     */
    public void getRate(double[] state, double[] rate) {
      ode.getRate(state, rate);
    }

    /**
     * Gets the state.
     *
     * @return double[]
     */
    public double[] getState() {
      return engineState;
    }

    /**
     * Sets the initial conditions using the current state of the ODE.
     *
     * @return double[]
     */
    public void setInitialConditions() {
      double[] state = ode.getState();
      if(state==null) {
        return;
      }
      if(engineState==null||engineState.length!=state.length) {
        engineState = new double[state.length]; // create an engine state with the correct size
      }
      System.arraycopy(state, 0, engineState, 0, state.length);
    }

    /**
     * updates the ODE state using the engine's internal state.
     */
    public void update() {
      System.arraycopy(engineState, 0, ode.getState(), 0, engineState.length);
    }
  }
}

/* 
 * Open Source Physics software is free software; you can redistribute
 * it and/or modify it under the terms of the GNU General Public License (GPL) as
 * published by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.

 * Code that uses any portion of the code in the org.opensourcephysics package
 * or any subpackage (subdirectory) of this package must must also be be released
 * under the GNU GPL license.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston MA 02111-1307 USA
 * or view the license online at http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2007  The Open Source Physics project
 *                     http://www.opensourcephysics.org
 */
