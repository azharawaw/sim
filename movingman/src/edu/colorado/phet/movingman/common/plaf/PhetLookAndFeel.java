package edu.colorado.phet.movingman.common.plaf;

import smooth.basic.SmoothTitledBorder;
import smooth.metal.SmoothLookAndFeel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InsetsUIResource;
import java.awt.*;
import java.util.ArrayList;

public class PhetLookAndFeel extends SmoothLookAndFeel {
    private static Font font;
    private static Color foregroundColor;
    public static final Color backgroundColor = new Color( 200, 240, 200 );
    private String[] types = new String[]{
        "Button", "MenuItem", "Panel", "Dialog",
        "CheckBox", "RadioButton", "ComboBox",
        "Menu", "MenuItem", "MenuBar",
        "Slider", "CheckBoxMenuItem", "RadioButtonMenuItem",
        "TextField", "TextArea", "Spinner", "Label"
    };

    public static Font getFont() {
        return font;
    }

    static {
        Font font1280 = new Font( "Lucida Sans", Font.PLAIN, 18 );
        Font font1040 = new Font( "Lucida Sans", Font.PLAIN, 14 );
        Font font800 = new Font( "Lucida Sans", Font.PLAIN, 8 );

        Font uifont = font1040;
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        System.out.println( "d = " + d );
        if( d.width > 1024 ) {
            uifont = font1280;
            System.out.println( "Chose font for width> 1280" );
        }
        else if( d.width <= 800 ) {
            uifont = font800;
            System.out.println( "Chose font for <=800" );
        }
        else {
            System.out.println( "Chose font for width between between 800 and 1280" );
        }
        font = uifont;

//        backgroundColor = CCK3Module.backgroundColor;
        foregroundColor = Color.black;
    }

    public PhetLookAndFeel() {
    }

    protected void initComponentDefaults( UIDefaults table ) {
        super.initComponentDefaults( table );
        ColorUIResource background = new ColorUIResource( backgroundColor );
        ColorUIResource foreground = new ColorUIResource( foregroundColor );
        FontUIResource fontResource = new FontUIResource( font );
        FontUIResource borderFont = new FontUIResource( new Font( "Lucida Sans", Font.ITALIC, font.getSize() ) );

        InsetsUIResource insets = new InsetsUIResource( 2, 2, 2, 2 );
        ArrayList def = new ArrayList();
        for( int i = 0; i < types.length; i++ ) {
            String type = types[i];
            def.add( type + ".font" );
            def.add( fontResource );
            def.add( type + ".foreground" );
            def.add( foreground );
            if( !type.equals( "TextField" ) ) {
                def.add( type + ".background" );
                def.add( background );
            }
            def.add( type + ".margin" );
            def.add( insets );
        }
        def.add( "TitledBorder.font" );
        def.add( borderFont );

        def.add( "TextField.background" );
        def.add( new ColorUIResource( Color.white ) );
        Object[] defaults = def.toArray();
        table.putDefaults( defaults );
    }

    public static Border createSmoothBorder( String s ) {
        return new SmoothTitledBorder( s );
    }
}