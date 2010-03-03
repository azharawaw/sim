/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.umd.cs.piccolo.util.PDimension;

public class Glucose extends SimpleSugar {
	
	private static final Dimension2D GALACTOSE_ATTACHMENT_POINT_OFFSET = new PDimension(getWidth()/2, 0);
	private static final Dimension2D LAC_Z_ATTACHMENT_POINT_OFFSET = new PDimension(getWidth()/2, 0);
	private static final Dimension2D LAC_I_ATTACHMENT_POINT_OFFSET = new PDimension(getWidth()/2, 0);
	private static final double HOLDOFF_TIME_UNTIL_FIRST_ATTACHMENT = 2; // In seconds.
	private static final double POST_ATTACHMENT_RECOVERY_TIME = 1; // In seconds.
	
	private Galactose galactoseAttachmentPartner;
	private LacZ lacZAttachmentPartner;
	private AttachmentState lacZAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVALABLE;
	private LacI lacIAttachmentPartner;
	private AttachmentState lacIAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVALABLE;
	private double holdoffPriorToFirstAttachmentCountdown = HOLDOFF_TIME_UNTIL_FIRST_ATTACHMENT;
	private double postAttachmentRecoveryCountdown = POST_ATTACHMENT_RECOVERY_TIME;

	public Glucose(IGeneNetworkModelControl model, Point2D initialPosition) {
		super(model, initialPosition, Color.BLUE);
	}
	
	public Glucose(IGeneNetworkModelControl model){
		this(model, new Point2D.Double());
	}
	
	public Glucose(){
		this(null);
	}
	
	public static Dimension2D getGalactoseAttachmentPointOffset(){
		return new PDimension(GALACTOSE_ATTACHMENT_POINT_OFFSET);
	}
	
	public static Dimension2D getLacZAttachmentPointOffset(){
		return new PDimension(LAC_Z_ATTACHMENT_POINT_OFFSET);
	}
	
	public static Dimension2D getLacIAttachmentPointOffset(){
		return new PDimension(LAC_I_ATTACHMENT_POINT_OFFSET);
	}
	
	public void formLactose(Galactose galactose){
		assert galactoseAttachmentPartner == null; // Should not be requested to attach if already attached.
		
		galactoseAttachmentPartner = galactose;
		galactoseAttachmentPartner.attach(this);  // This will move galactose to the appropriate location.
	}
	
	public boolean isAvailableForAttaching(){
		return ( lacZAttachmentState == AttachmentState.UNATTACHED_AND_AVAILABLE &&
				 lacIAttachmentState == AttachmentState.UNATTACHED_AND_AVAILABLE );
	}
	
	public boolean isBoundToGalactose(){
		return !(galactoseAttachmentPartner == null);
	}
	
	public boolean considerProposalFrom(LacZ lacZ){
		boolean proposalAccepted = false;
		
		if (lacZAttachmentState == AttachmentState.UNATTACHED_AND_AVAILABLE && 
			getExistenceState() == ExistenceState.EXISTING){
			
			assert lacZAttachmentPartner == null;  // For debug - Make sure consistent with attachment state.
			lacZAttachmentPartner = lacZ;
			lacZAttachmentState = AttachmentState.MOVING_TOWARDS_ATTACHMENT;
			proposalAccepted = true;
			
			// Set ourself up to move toward the attaching location.
			Dimension2D offsetFromTarget = new PDimension(
					LacZ.getGlucoseAttachmentPointOffset().getWidth() - getLacZAttachmentPointOffset().getWidth(),
					LacZ.getGlucoseAttachmentPointOffset().getHeight() - getLacZAttachmentPointOffset().getHeight());
			setMotionStrategy(new CloseOnMovingTargetMotionStrategy(lacZ, offsetFromTarget,
					LacOperonModel.getMotionBounds()));
		}
		
		return proposalAccepted;
	}
	
	public boolean considerProposalFrom(LacI lacI){
		boolean proposalAccepted = false;
		
		if (lacIAttachmentState == AttachmentState.UNATTACHED_AND_AVAILABLE && 
			getExistenceState() == ExistenceState.EXISTING){
			
			assert lacIAttachmentPartner == null;  // For debug - Make sure consistent with attachment state.
			lacIAttachmentPartner = lacI;
			lacIAttachmentState = AttachmentState.MOVING_TOWARDS_ATTACHMENT;
			proposalAccepted = true;
			
			// Set ourself up to move toward the attaching location.
			Dimension2D offsetFromTarget = new PDimension(
					LacI.getGlucoseAttachmentPointOffset().getWidth() - getLacIAttachmentPointOffset().getWidth(),
					LacI.getGlucoseAttachmentPointOffset().getHeight() - getLacIAttachmentPointOffset().getHeight());
			setMotionStrategy(new CloseOnMovingTargetMotionStrategy(lacI, offsetFromTarget,
					LacOperonModel.getMotionBounds()));
		}
		
		return proposalAccepted;
	}
	
	public void attach(LacZ lacZ){
		if (lacZ != lacZAttachmentPartner){
			// For this bond, it is expected that we were already moving
			// towards this partner.  If not, it's unexpected.
			System.err.println(getClass().getName() + " - Error: Attach request from non-partner.");
			assert false;
			return;
		}
		setPosition(lacZ.getGlucoseAttachmentPointLocation().getX() - LAC_Z_ATTACHMENT_POINT_OFFSET.getWidth(),
				lacZ.getGlucoseAttachmentPointLocation().getY() - LAC_Z_ATTACHMENT_POINT_OFFSET.getHeight());
		Dimension2D followingOffset = new PDimension(
				LacZ.getGlucoseAttachmentPointOffset().getWidth() - LAC_Z_ATTACHMENT_POINT_OFFSET.getWidth(),
				LacZ.getGlucoseAttachmentPointOffset().getHeight() - LAC_Z_ATTACHMENT_POINT_OFFSET.getHeight());
		setMotionStrategy(new FollowTheLeaderMotionStrategy(this, lacZ, followingOffset));
		lacZAttachmentState = AttachmentState.ATTACHED;
	}
	
	public void attach(LacI lacI){
		if (lacI != lacIAttachmentPartner){
			// For this bond, it is expected that we were already moving
			// towards this partner.  If not, it's unexpected.
			System.err.println(getClass().getName() + " - Error: Attach request from non-partner.");
			assert false;
			return;
		}
		setPosition(lacI.getGlucoseAttachmentPointLocation().getX() - LAC_I_ATTACHMENT_POINT_OFFSET.getWidth(),
				lacI.getGlucoseAttachmentPointLocation().getY() - LAC_I_ATTACHMENT_POINT_OFFSET.getHeight());
		Dimension2D followingOffset = new PDimension(
				LacI.getGlucoseAttachmentPointOffset().getWidth() - LAC_I_ATTACHMENT_POINT_OFFSET.getWidth(),
				LacI.getGlucoseAttachmentPointOffset().getHeight() - LAC_I_ATTACHMENT_POINT_OFFSET.getHeight());
		setMotionStrategy(new FollowTheLeaderMotionStrategy(this, lacI, followingOffset));
		lacIAttachmentState = AttachmentState.ATTACHED;
	}
	
	/**
	 * Detach from LacZ.  This is intended to be used by a LacZ instance that
	 * wants to detach.  After being released by LacZ, it is assumed that the
	 * lactose has been "digested", so this molecule fades out of existence.
	 * 
	 * @param lacI
	 */
	public void detach(LacZ lacZ){
		assert lacZ == lacZAttachmentPartner;
		lacZAttachmentPartner = null;
		lacZAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVALABLE;
		setMotionStrategy(new LinearThenRandomMotionStrategy(LacOperonModel.getMotionBoundsAboveDna(),
				getPositionRef(), new Vector2D.Double(-3, -8), 1));
		
		// Once broken down from being a part of lactose, this fades away.
		setExistenceTime(0.5);
	}

	/**
	 * Detach from LacI.  This is intended to be used by a LacI instance that
	 * wants to detach.
	 * 
	 * @param lacI
	 */
	public void detach(LacI lacI){
		assert lacI == lacIAttachmentPartner;
		lacIAttachmentPartner = null;
		lacIAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVALABLE;
		postAttachmentRecoveryCountdown = POST_ATTACHMENT_RECOVERY_TIME;
		setMotionStrategy(new LinearThenRandomMotionStrategy(LacOperonModel.getMotionBoundsAboveDna(), 
				getPositionRef(), new Vector2D.Double(0, 8), 0.5));
	}
	
	/**
	 * Set the time for lactose to exist, after which it will fade out.  This
	 * was created to allow lactose to fade out at the same time as lacI when
	 * they are all bonded together.
	 * 
	 * TODO: As of this writing (Dec 15, 2009), lactose fades out after being
	 * bonded to LacI.  We don't know if this is the desired behavior, since
	 * it isn't specified, so the behavior may eventually be changed such that
	 * lactose can only be removed after being broken down by LacZ.  If that
	 * becomes the case, this method should go away.  UPDATE Jan 1, 2010 - 
	 * After a review with the folks from UBC, it was decided that lactose and
	 * LacI should NOT fade out after bonding.  Instead, the lactose should be
	 * released after a while.  The only way for lactose to leave the sim will
	 * be for it to be digested by LacZ.  So, this should be removed once it
	 * is determined that not fading is the desired behavior.
	 * 
	 * @param existenceTime
	 */
	public void setLactoseExistenceTime(double existenceTime){
		
		assert galactoseAttachmentPartner != null;
		galactoseAttachmentPartner.setOkayToFade(true);
		galactoseAttachmentPartner.setExistenceTime(existenceTime);
		setExistenceTime(existenceTime);
		setOkayToFade(true);
		
	}
	
	/**
	 * This is called to force this molecule to release the attached galactose
	 * molecule, essentially breaking down from lactose into the constituent
	 * molecules.
	 */
	public void releaseGalactose(){
		if (galactoseAttachmentPartner == null){
			System.err.println(getClass().getName() + " - Error: Told to detach galactose when not attached.");
			return;
		}
		galactoseAttachmentPartner.detach(this);
		galactoseAttachmentPartner = null;
	}
	
	/**
	 * Get the location in absolute model space of this element's attachment
	 * point for LacZ.
	 */
	public Point2D getLacZAttachmentPointLocation(){
		return (new Point2D.Double(getPositionRef().getX() + LAC_Z_ATTACHMENT_POINT_OFFSET.getWidth(),
				getPositionRef().getY() + LAC_Z_ATTACHMENT_POINT_OFFSET.getHeight()));
	}

	@Override
	public void stepInTime(double dt) {
		if (holdoffPriorToFirstAttachmentCountdown >= 0){
			holdoffPriorToFirstAttachmentCountdown -= dt;
			if (holdoffPriorToFirstAttachmentCountdown <= 0){
				lacIAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
				lacZAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
			}
		}
		else if (lacIAttachmentState == AttachmentState.UNATTACHED_BUT_UNAVALABLE  && !isUserControlled()){
			postAttachmentRecoveryCountdown -= dt;
			if (postAttachmentRecoveryCountdown <= 0){
				// Recovery complete - we are ready to attach again.
				lacIAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
			}
		}
		super.stepInTime(dt);
	}

	@Override
	public void setDragging(boolean dragging) {
		if (dragging == true){
			// The user has grabbed this node and is moving it.  Is it
			// attached to a LacI or a LacZ?
			if (lacIAttachmentPartner != null){
				// It is attached to a LacI, so it needs to detach.
				assert lacIAttachmentState == AttachmentState.ATTACHED || lacIAttachmentState == AttachmentState.MOVING_TOWARDS_ATTACHMENT;  // State consistency test.
				lacIAttachmentPartner.detach(this);
				lacIAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVALABLE;
				lacIAttachmentPartner = null;
				setMotionStrategy(new RandomWalkMotionStrategy(LacOperonModel.getMotionBoundsAboveDna()));
			}
			else if (lacZAttachmentPartner != null){
				// It is attached to a LacZ, so it needs to detach.
				assert lacZAttachmentState == AttachmentState.ATTACHED || lacZAttachmentState == AttachmentState.MOVING_TOWARDS_ATTACHMENT;  // State consistency test.
				lacZAttachmentPartner.detach(this);
				lacZAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVALABLE;
				lacZAttachmentPartner = null;
				setMotionStrategy(new RandomWalkMotionStrategy(LacOperonModel.getMotionBoundsAboveDna()));
			}
		}
		else{
			// This element has just been released by the user.  It should be
			// considered available.
			lacIAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
			lacZAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
			
			/*
			// If this was dropped within range of the lac operator, it should
			// try to attach to it.
			LacOperator lacOperator = getModel().getLacOperator();
			if ( lacOperator != null && 
				 glucoseAttachmentState != AttachmentState.ATTACHED &&
				 getPositionRef().distance(lacOperator.getPositionRef()) < LAC_OPERATOR_IMMEDIATE_ATTACH_DISTANCE){
				
				// We are in range, so try to attach.
				lacOperator.requestImmediateAttach(this);
			}
			 */
		}
			
		super.setDragging(dragging);
	}
}
