/* Copyright 2004, Sam Reid */
package edu.colorado.phet.common.tests.graphics;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.*;
import edu.colorado.phet.common.view.util.RectangleUtils;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

/**
 * User: Sam Reid
 * Date: Dec 6, 2004
 * Time: 5:19:19 PM
 * Copyright (c) Dec 6, 2004 by Sam Reid
 */

public class TestPhetGraphics extends JFrame {
    private ApparatusPanel panel;
    private AbstractClock clock;

    static interface TestPhetGraphicSource {
        public PhetGraphic createGraphic( ApparatusPanel panel );
    }

    public TestPhetGraphics() throws HeadlessException {
        super( "Test PhetGraphics" );
        panel = new ApparatusPanel();
        panel.addGraphicsSetup( new BasicGraphicsSetup() );
        TestPhetGraphicSource[] graphics = new TestPhetGraphicSource[]{
            new TestPhetGraphicSource() {
                public PhetGraphic createGraphic( ApparatusPanel panel ) {
                    return new PhetTextGraphic( panel, new Font( "Lucida Sans", Font.BOLD, 24 ), "PhetGraphic Test", Color.blue, 100, 100 );
                }
            },
            new TestPhetGraphicSource() {
                public PhetGraphic createGraphic( ApparatusPanel panel ) {
                    return new PhetShapeGraphic( panel, new Rectangle( 50, 50, 50, 50 ), Color.green, new BasicStroke( 2 ), Color.black );
                }
            },
            new TestPhetGraphicSource() {
                public PhetGraphic createGraphic( ApparatusPanel panel ) {
                    return new PhetImageGraphic( panel, "images/Phet-Flatirons-logo-3-small.gif" );
                }
            },
            new TestPhetGraphicSource() {
                public PhetGraphic createGraphic( ApparatusPanel panel ) {
                    return new PhetMultiLineTextGraphic( panel, new String[]{"PhET", "Multi-", "Line", "TextGraphic"}, new Font( "dialog", 0, 28 ), 200, 200, Color.red, 1, 1, Color.yellow );
                }
            },
            new TestPhetGraphicSource() {
                public PhetGraphic createGraphic( ApparatusPanel panel ) {
                    return new PhetShadowTextGraphic( panel, "Shadowed", new Font( "dialog", Font.BOLD, 28 ), 120, 120, Color.blue, 1, 1, Color.green );
                }
            },
            new TestPhetGraphicSource() {
                public PhetGraphic createGraphic( ApparatusPanel panel ) {
                    CompositePhetGraphic cpg = new CompositePhetGraphic( panel );
                    cpg.addGraphic( new PhetShapeGraphic( panel, new Ellipse2D.Double( 130, 30, 30, 30 ), Color.red ) );
                    cpg.addGraphic( new PhetShapeGraphic( panel, new Ellipse2D.Double( 160, 30, 30, 30 ), Color.blue ) );
                    cpg.addGraphic( new PhetShadowTextGraphic( panel, "compositegraphic", new Font( "Lucida Sans", 0, 12 ), 130, 30, Color.white, 1, 1, Color.black ) );
                    return cpg;
                }
            }
        };
        for( int i = 0; i < graphics.length; i++ ) {
            TestPhetGraphicSource graphic = graphics[i];
            final PhetGraphic pg = graphic.createGraphic( panel );
            pg.setCursorHand();
            pg.addMouseInputListener( new MouseInputAdapter() {
                // implements java.awt.event.MouseMotionListener
                public void mouseDragged( MouseEvent e ) {
                    if( SwingUtilities.isRightMouseButton( e ) ) {
                        Point ctr = RectangleUtils.getCenter( pg.getBounds() );
                        pg.transform( AffineTransform.getRotateInstance( Math.PI / 36, ctr.x, ctr.y ) );
                    }
                }
            } );
            pg.addTranslationListener( new TranslationListener() {
                public void translationOccurred( TranslationEvent translationEvent ) {
                    if( SwingUtilities.isLeftMouseButton( translationEvent.getMouseEvent() ) ) {
                        pg.transform( AffineTransform.getTranslateInstance( translationEvent.getDx(), translationEvent.getDy() ) );
                    }
                }
            } );
            panel.addGraphic( pg );
        }

        setContentPane( panel );
        setSize( 600, 600 );
        panel.requestFocus();
        panel.addMouseListener( new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                panel.requestFocus();
            }
        } );
        clock = new SwingTimerClock( 1, 30, true );
        panel.addGraphic( new PhetShapeGraphic( panel, new Rectangle( 5, 5, 5, 5 ), Color.black ) );
        final RepaintDebugGraphic rdg = new RepaintDebugGraphic( panel, clock, 100 );
//        rdg.setTransparency( 128);
        panel.addGraphic( rdg );

        rdg.setActive( false );
        rdg.setVisible( false );

        panel.addKeyListener( new KeyListener() {
            public void keyPressed( KeyEvent e ) {
                if( e.getKeyCode() == KeyEvent.VK_SPACE ) {
                    rdg.setActive( !rdg.isActive() );
                    rdg.setVisible( rdg.isActive() );
                }
            }

            public void keyReleased( KeyEvent e ) {
            }

            public void keyTyped( KeyEvent e ) {
            }
        } );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

    }


    public static void main( String[] args ) {
        new TestPhetGraphics().start();
    }

    private void start() {
        clock.start();
        panel.requestFocus();
        setVisible( true );
    }
}
