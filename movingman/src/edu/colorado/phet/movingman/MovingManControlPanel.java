/*PhET, 2004.*/
package edu.colorado.phet.movingman;

import edu.colorado.phet.common.view.components.VerticalLayoutPanel;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.movingman.common.BufferedGraphicForComponent;
import edu.colorado.phet.movingman.common.plaf.PlafUtil;
import edu.colorado.phet.movingman.plots.DataSeries;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.StringTokenizer;

/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 1:11:38 AM
 * Copyright (c) Jun 30, 2003 by Sam Reid
 */
public class MovingManControlPanel extends JPanel {
    private MovingManModule module;
    private JPanel controllerContainer;
    private PlaybackPanel playbackPanel;
    private JButton reset;

    public void setRunningState() {
        playbackPanel.pause.setEnabled( false );
    }

    public void setPauseState() {
        playbackPanel.pause.doClick( 50 );
    }

    public void motionStarted() {
        playbackPanel.disablePlayback();
    }

    public void goPressed() {
        reset.setEnabled( true );
    }

    public void finishedRecording() {
        playbackPanel.finishedRecording();
    }

    public void playbackFinished() {
        playbackPanel.playbackFinished();
    }

    public void motionPaused() {
        playbackPanel.enablePlayback();
    }

    public void enablePause() {
        playbackPanel.pause.setEnabled( true );
    }

    public class PlaybackPanel extends JPanel {
        private JButton reset;
        private JButton play;
        private JButton pause;
        private JButton rewind;
        private JButton slowMotion;
        private JButton record;

        public PlaybackPanel() throws IOException {
            ImageIcon recordIcon = new ImageIcon( new ImageLoader().loadImage( "images/icons/java/media/Movie24.gif" ) );
            record = new JButton( "Record", recordIcon );
            record.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setMode( module.getMotionMode() );
                    module.setPaused( false );
//                    record.setEnabled( false );
//                    reset.setEnabled( true );
                }
            } );
            ImageIcon resetIcon = new ImageIcon( new ImageLoader().loadImage( "images/icons/java/media/Stop24.gif" ) );
            reset = new JButton( "Reset", resetIcon );
            reset.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.reset();
                    reset.setEnabled( false );
                    record.setEnabled( true );
                }
            } );
            ImageIcon pauseIcon = new ImageIcon( new ImageLoader().loadImage( "images/icons/java/media/Pause24.gif" ) );

            pause = new JButton( "Pause", pauseIcon );
            pause.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    //pausing from playback leaves it alone
                    module.setPaused( true );
                    reset.setEnabled( true );
                    record.setEnabled( true );
                }
            } );
            module.addPauseListener( new MovingManModule.PauseListener() {
                public void modulePaused() {
                    pause.setEnabled( false );
                    record.setEnabled( true );
                    play.setEnabled( true );
                    slowMotion.setEnabled( true );
                }

                public void moduleStarted() {
                    pause.setEnabled( true );
                    record.setEnabled( false );
                    play.setEnabled( false );
                    slowMotion.setEnabled( false );
                    reset.setEnabled( true );
                }
            } );
            ImageIcon playIcon = new ImageIcon( new ImageLoader().loadImage( "images/icons/java/media/Play24.gif" ) );
            play = new JButton( "Playback", playIcon );
            play.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.startPlaybackMode( 1.0 );
                    play.setEnabled( false );
                    reset.setEnabled( true );
                    slowMotion.setEnabled( true );
                    pause.setEnabled( true );
                    rewind.setEnabled( true );
                    record.setEnabled( false );
                }
            } );

            ImageIcon rewindIcon = new ImageIcon( new ImageLoader().loadImage( "images/icons/java/media/Rewind24.gif" ) );
            rewind = new JButton( "Rewind", rewindIcon );
            rewind.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    reset.setEnabled( true );
                    module.rewind();
                    module.setPaused( true );
                    rewind.setEnabled( false );
                    if( module.getPosition().getData().size() > module.getNumResetPoints() + 1 ) {
                        play.setEnabled( true );
                        slowMotion.setEnabled( true );
                    }
                    else {
                        play.setEnabled( false );
                        slowMotion.setEnabled( false );
                    }
                    record.setEnabled( true );
                }
            } );

            ImageIcon slowIcon = new ImageIcon( new ImageLoader().loadImage( "images/icons/java/media/StepForward24.gif" ) );
            slowMotion = new JButton( "Slow Playback", slowIcon );
            slowMotion.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.startPlaybackMode( .4 );
                    play.setEnabled( true );
                    slowMotion.setEnabled( false );
                    pause.setEnabled( true );
                    rewind.setEnabled( true );
                    reset.setEnabled( true );
                    record.setEnabled( true );
                }
            } );

            ImageIcon imageIcon = new ImageIcon( getClass().getClassLoader().getResource( "images/Phet-Flatirons-logo-3-small.jpg" ) );
            JLabel phetIconLabel = new JLabel( imageIcon );

            add( record );
            add( play );
            add( slowMotion );
            add( pause );
            add( rewind );
            add( reset );
            JLabel separator = new JLabel();
            separator.setPreferredSize( new Dimension( 20, 10 ) );
            add( separator );
            add( phetIconLabel );
            didReset();
            module.addStateListener( new MovingManModule.StateListener() {
                public void stateChanged( MovingManModule module ) {
                    if( module.isPaused() ) {
                        //fix the buttons.
                        play.setEnabled( true );
                        slowMotion.setEnabled( true );
                        pause.setEnabled( false );
                        rewind.setEnabled( true );
                    }
                }
            } );
        }

        private void didReset() {
            play.setEnabled( false );
            slowMotion.setEnabled( false );
            pause.setEnabled( false );
            rewind.setEnabled( false );
        }

//        private void doMotion() {
//            module.setMode( module.getMotionMode() );
//            module.setPaused( false );
//        }

        private void doManual() {
            module.setRecordMode();
            module.setPaused( false );
            pause.setEnabled( true );
            play.setEnabled( false );
            slowMotion.setEnabled( false );
        }

        public void disablePlayback() {
            play.setEnabled( false );
            slowMotion.setEnabled( false );
            pause.setEnabled( false );
            rewind.setEnabled( false );
        }

        public void finishedRecording() {
            rewind.setEnabled( false );
            pause.setEnabled( false );
            play.setEnabled( true );
            slowMotion.setEnabled( true );
        }

        public void playbackFinished() {
            rewind.setEnabled( true );
            pause.setEnabled( false );
            play.setEnabled( false );
            slowMotion.setEnabled( false );
        }

        public void enablePlayback() {
            rewind.setEnabled( false );
            pause.setEnabled( false );
            play.setEnabled( true );
            slowMotion.setEnabled( true );
        }
    }

    public MovingManControlPanel( final MovingManModule module ) throws IOException {
        this.module = module;
        addAlphaPanel();
        final Dimension preferred = new Dimension( 200, 400 );
        setSize( preferred );
        setPreferredSize( preferred );
        setLayout( new BorderLayout() );

        controllerContainer = new JPanel();
        controllerContainer.setPreferredSize( new Dimension( 200, 200 ) );
        controllerContainer.setSize( new Dimension( 200, 200 ) );
        add( controllerContainer, BorderLayout.CENTER );

        playbackPanel = new PlaybackPanel();

        JPanel panel = new JPanel();
        panel.setLayout( new BorderLayout() );

        add( panel, BorderLayout.NORTH );
        JButton changeControl = new JButton( "Change number of smoothing points." );
        changeControl.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                String value = JOptionPane.showInputDialog( "How many smoothing points?", "15" );
                module.setNumSmoothingPoints( Integer.parseInt( value ) );
            }
        } );

        VerticalLayoutPanel northPanel = new VerticalLayoutPanel();
        final JMenu viewMenu = new JMenu( "View" );
        JMenuItem[] items = PlafUtil.getLookAndFeelItems();
        for( int i = 0; i < items.length; i++ ) {
            JMenuItem item = items[i];
            viewMenu.add( item );
        }

        new Thread( new Runnable() {
            public void run() {
                try {
                    while( module.getFrame() == null || !module.getFrame().isVisible() ) {
                        Thread.sleep( 1000 );
                    }
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
                module.getFrame().getJMenuBar().add( viewMenu );
                module.getFrame().setExtendedState( JFrame.MAXIMIZED_HORIZ );
                module.getFrame().setExtendedState( JFrame.MAXIMIZED_BOTH );
            }
        } ).start();
        ImageIcon imageIcon = new ImageIcon( getClass().getClassLoader().getResource( "images/Phet-Flatirons-logo-3-small.jpg" ) );
        JLabel phetIconLabel = new JLabel( imageIcon );
        northPanel.add( phetIconLabel );

        final JCheckBox invertAxes = new JCheckBox( "Invert X-Axis", false );
        invertAxes.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setRightDirPositive( !invertAxes.isSelected() );
            }
        } );
        northPanel.add( invertAxes );
        reset = new JButton( "Reset" );
        reset.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.reset();
                reset.setEnabled( false );
            }
        } );
        reset.setEnabled( false );
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout( new BoxLayout( buttonPanel, BoxLayout.X_AXIS ) );
        buttonPanel.add( reset );

        northPanel.setFill( GridBagConstraints.NONE );
        northPanel.setAnchor( GridBagConstraints.WEST );
        northPanel.setInsets( new Insets( 4, 4, 4, 4 ) );
        northPanel.add( buttonPanel );
        panel.add( northPanel, BorderLayout.NORTH );
    }

    JFrame alphaFrame = new JFrame();

    private void addAlphaPanel() {
        final JPanel contentPane = new JPanel();
        contentPane.setLayout( new BoxLayout( contentPane, BoxLayout.Y_AXIS ) );
        Field[] f = AlphaComposite.class.getFields();
        final JList list = new JList( f );

        list.addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent e ) {
                BufferedGraphicForComponent bg = module.getBackground();
                Field ac = (Field)list.getSelectedValue();
                AlphaComposite alpha = null;
                try {
                    alpha = (AlphaComposite)ac.get( null );
                }
                catch( IllegalAccessException e1 ) {
                    e1.printStackTrace();
                }
                bg.setAlphaComposite( alpha );
                list.repaint();
                contentPane.invalidate();
                contentPane.validate();
                contentPane.repaint();
            }
        } );
        contentPane.add( list );
        alphaFrame.setContentPane( contentPane );
//        module.getApparatusPanel().addKeyListener( new KeyListener() {
//            public void keyTyped( KeyEvent e ) {
//            }
//
//            public void keyPressed( KeyEvent e ) {
//            }
//
//            public void keyReleased( KeyEvent e ) {
//                if( e.getKeyCode() == KeyEvent.VK_SPACE ) {
//                    alphaFrame.setVisible( true );
//                }
//            }
//        } );
        module.getApparatusPanel().addMouseListener( new MouseListener() {
            public void mouseClicked( MouseEvent e ) {
            }

            public void mousePressed( MouseEvent e ) {
                module.getApparatusPanel().requestFocus();
                module.getApparatusPanel().requestFocusInWindow();
            }

            public void mouseReleased( MouseEvent e ) {
            }

            public void mouseEntered( MouseEvent e ) {
            }

            public void mouseExited( MouseEvent e ) {
            }
        } );
        alphaFrame.setContentPane( contentPane );
        alphaFrame.pack();
    }

    public static DataSeries parseString( String str ) {
        DataSeries data = new DataSeries();
        StringTokenizer st = new StringTokenizer( str );
        while( st.hasMoreElements() ) {
            String s = (String)st.nextElement();
            double value = Double.parseDouble( s );
            data.addPoint( value );
        }
        return data;
    }

    public void startRecordingManual() {
        playbackPanel.doManual();
        if( !reset.isEnabled() ) {
            reset.setEnabled( true );
        }
    }

    public static String toString( DataSeries data ) {
        StringBuffer string = new StringBuffer();
        for( int i = 0; i < data.size(); i++ ) {
            double x = data.pointAt( i );
            string.append( x );
            if( i < data.size() ) {
                string.append( "," );
            }
        }
        return string.toString();
    }

    public void setPaused() {
        playbackPanel.pause.setEnabled( false );
        playbackPanel.play.setEnabled( true );
    }

    public JComponent getMediaPanel() {
        return playbackPanel;
    }

}
