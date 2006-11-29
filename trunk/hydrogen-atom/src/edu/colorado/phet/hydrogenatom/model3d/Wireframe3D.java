/*

 * Copyright (c) 2003 Sun Microsystems, Inc. All  Rights Reserved.

 * 

 * Redistribution and use in source and binary forms, with or without

 * modification, are permitted provided that the following conditions

 * are met:

 * 

 * -Redistributions of source code must retain the above copyright

 *  notice, this list of conditions and the following disclaimer.

 * 

 * -Redistribution in binary form must reproduct the above copyright

 *  notice, this list of conditions and the following disclaimer in

 *  the documentation and/or other materials provided with the distribution.

 * 

 * Neither the name of Sun Microsystems, Inc. or the names of contributors

 * may be used to endorse or promote products derived from this software

 * without specific prior written permission.

 * 

 * This software is provided "AS IS," without a warranty of any kind. ALL

 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING

 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE

 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT

 * BE LIABLE FOR ANY DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT

 * OF OR RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THE SOFTWARE OR ITS

 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST

 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,

 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY

 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE, EVEN

 * IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.

 * 

 * You acknowledge that Software is not designed, licensed or intended for

 * use in the design, construction, operation or maintenance of any nuclear

 * facility.

 */



/*

 * CHANGE LIST:

 * 

 * 11/27/06 - obtained code from http://www.cs.cf.ac.uk/Dave/JAVA/3d/3d.html

 * 11/27/06 - discovered code was distributed by Sun with JDK 1.4.2

 * 11/28/06 - added missing copyright header

 * 11/28/06 - formatted code to PhET standards

 * 11/28/06 - organized imports to PhET standards

 * 11/28/06 - made all member data private, added mutators and accessors where needed

 * 11/28/06 - added missing public modifiers to methods

 * 11/28/06 - added some comments

 * 11/28/06 - changed class name to Wireframe3D, since that's what it does

 * 11/28/06 - add ability to set an optional color, used instead of grayscale palette

 * 11/28/06 - add ability to specify the Stroke used to draw the lines in the wireframe

 * 11/28/06 - add ability to specify whether lines are antialiased

 * 11/28/06 - automatically call compress and findBB after parsing input stream

 * 11/28/06 - move parsing of input stream into its own method, called from constructor

 * 11/28/06 - remove bogus rotations applied to matrix in constructor, start with unit matrix

 * 11/28/06 - reorganize code and add section headers

 */



package edu.colorado.phet.hydrogenatom.model3d;



import java.awt.*;

import java.awt.BasicStroke;

import java.awt.Color;

import java.awt.Graphics;

import java.awt.Stroke;

import java.io.*;



/**

 * Wireframe3D draws a wireframe 3D model.

 * <p>

 * This code was distributed with JDK 1.4.2 as class Model3D, 

 * in the Wireframe example applet.

 */

public class Wireframe3D {



    //----------------------------------------------------------------------------

    // Class data

    //----------------------------------------------------------------------------

    

    private static final Stroke DEFAULT_STROKE = new BasicStroke( 1f );

    

    //----------------------------------------------------------------------------

    // Instance data

    //----------------------------------------------------------------------------

    

    private float vert[];

    private int tvert[];

    private int nvert;

    private int maxvert;

    private int con[];

    private int ncon, maxcon;

    private boolean transformed;

    private Matrix3D mat;

    private float xmin, xmax, ymin, ymax, zmin, zmax;

    private Color palette[]; // color palette

    private boolean antialias;

    private Stroke stroke;



    //----------------------------------------------------------------------------

    // Constructors

    //----------------------------------------------------------------------------

    

    /*

     * Creates a 3D wireframe model by parsing an input stream.

     */

    public Wireframe3D( InputStream is ) throws IOException {

        antialias = false;

        stroke = DEFAULT_STROKE;

        initPaletteGray();

        mat = new Matrix3D();

        parseStream( is );

        compress();

        findBB();

    }

    

    //----------------------------------------------------------------------------

    // Mutators and accessors

    //----------------------------------------------------------------------------

    

    public Matrix3D getMatrix() {

        return mat;

    }

    

    public void setTransformed( boolean transformed ) {

        this.transformed = transformed;

    }

    

    public void setColors( Color front, Color back ) {

        initPalette( front, back );

    }

    

    public void setStroke( Stroke stroke ) {

        this.stroke = stroke;

    }

    

    public void setAntialias( boolean antialias ) {

        this.antialias = antialias;

    }

    

    public float getXMax() {

        return xmax;

    }

    

    public float getXMin() {

        return xmin;

    }

    

    public float getYMax() {

        return ymax;

    }

    

    public float getYMin() {

        return ymin;

    }

    

    public float getZMax() {

        return zmax;

    }

    

    public float getZMin() {

        return zmin;

    }

    

    //----------------------------------------------------------------------------

    // Rendering

    //----------------------------------------------------------------------------

    

    /**

     * Paints this model to a graphics context.  It uses the matrix associated

     * with this model to map from model space to screen space.

     * 

     * @param g2

     */

    public void paint( Graphics2D g2 ) {



        if ( vert == null || nvert <= 0 ) {

            return;

        }

        

        transform();

        

        int lg = 0;

        int lim = ncon;

        int c[] = con;

        int v[] = tvert;

        if ( lim <= 0 || nvert <= 0 ) {

            return;

        }

        

        // Save graphics state

        Color saveColor = g2.getColor();

        Object saveAntialiasValue = g2.getRenderingHint( RenderingHints.KEY_ANTIALIASING );

        Stroke saveStroke = g2.getStroke();

        

        if ( antialias ) {

            g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

        }

        g2.setStroke( stroke );

        

        // Draw wireframe

        for ( int i = 0; i < lim; i++ ) {

            int T = c[i];

            int p1 = ( ( T >> 16 ) & 0xFFFF ) * 3;

            int p2 = ( T & 0xFFFF ) * 3;



            // choose a color from the palette based on depth

            int colorIndex = v[p1 + 2] + v[p2 + 2];

            if ( colorIndex < 0 ) {

                colorIndex = 0;

            }

            else if ( colorIndex > 15 ) {

                colorIndex = 15;

            }

            if ( colorIndex != lg ) {

                lg = colorIndex;

                g2.setColor( palette[colorIndex] );

            }



            g2.drawLine( v[p1], v[p1 + 1], v[p2], v[p2 + 1] );

        }

        

        // Restore graphics state

        g2.setColor( saveColor );

        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, saveAntialiasValue );

        g2.setStroke( saveStroke );

    }



    //----------------------------------------------------------------------------

    // Private

    //----------------------------------------------------------------------------

    

    /*

     * Reads and parses an input stream that contains the description

     * of the wireframe model.

     */

    private void parseStream( InputStream is ) throws IOException {

        Reader r = new BufferedReader( new InputStreamReader( is ) );

        StreamTokenizer st = new StreamTokenizer( r );

        st.eolIsSignificant( true );

        st.commentChar( '#' );

        scan: while ( true ) {

            switch ( st.nextToken() ) {

            default:

                break scan;

            case StreamTokenizer.TT_EOL:

                break;

            case StreamTokenizer.TT_WORD:

                if ( "v".equals( st.sval ) ) {

                    double x = 0, y = 0, z = 0;

                    if ( st.nextToken() == StreamTokenizer.TT_NUMBER ) {

                        x = st.nval;

                        if ( st.nextToken() == StreamTokenizer.TT_NUMBER ) {

                            y = st.nval;

                            if ( st.nextToken() == StreamTokenizer.TT_NUMBER ) {

                                z = st.nval;

                            }

                        }

                    }

                    addVert( (float) x, (float) y, (float) z );

                    while ( st.ttype != StreamTokenizer.TT_EOL && st.ttype != StreamTokenizer.TT_EOF ) {

                        st.nextToken();

                    }

                }

                else if ( "f".equals( st.sval ) || "fo".equals( st.sval ) || "l".equals( st.sval ) ) {

                    int start = -1;

                    int prev = -1;

                    int n = -1;

                    while ( true ) {

                        if ( st.nextToken() == StreamTokenizer.TT_NUMBER ) {

                            n = (int) st.nval;

                            if ( prev >= 0 ) {

                                addLine( prev - 1, n - 1 );

                            }

                            if ( start < 0 ) {

                                start = n;

                            }

                            prev = n;

                        }

                        else if ( st.ttype == '/' ) {

                            st.nextToken();

                        }

                        else {

                            break;

                        }

                    }

                    if ( start >= 0 ) {

                        addLine( start - 1, prev - 1 );

                    }

                    if ( st.ttype != StreamTokenizer.TT_EOL ) {

                        break scan;

                    }

                }

                else {

                    while ( st.nextToken() != StreamTokenizer.TT_EOL && st.ttype != StreamTokenizer.TT_EOF ) {

                        ;

                    }

                }

            }

        }

        is.close();

    }

    

    /*

     * Initializes the palette to a range of grays.

     */

    private void initPaletteGray() {

        palette = new Color[16];

        for ( int i = 0; i < 16; i++ ) {

            int grey = (int) ( 170 * ( 1 - Math.pow( i / 15.0, 2.3 ) ) );

            palette[i] = new Color( grey, grey, grey );

        }

    }

    

    /*

     * Initializes the palette to a range of colors.

     */

    private void initPalette( Color front, Color back ) {



        int fr = front.getRed();

        int fg = front.getGreen();

        int fb = front.getBlue();

        

        int br = back.getRed();

        int bg = back.getGreen();

        int bb = back.getBlue();

        

        float rdelta = ( fr - br ) / 16f;

        float gdelta = ( fg - bg ) / 16f;

        float bdelta = ( fb - bb ) / 16f;

        

        for ( int i = 0; i < 16; i++ ) {

            float r = ( fr - ( i * rdelta ) ) / 255f;

            float g = ( fg - ( i * gdelta ) ) / 255f;

            float b = ( fb - ( i * bdelta ) ) / 255f;

            palette[i] = new Color( r, g, b );

        }

    }

    

    /*

     * Adds a vertex to this model.

     */

    private int addVert( float x, float y, float z ) {

        int i = nvert;

        if ( i >= maxvert ) {

            if ( vert == null ) {

                maxvert = 100;

                vert = new float[maxvert * 3];

            }

            else {

                maxvert *= 2;

                float nv[] = new float[maxvert * 3];

                System.arraycopy( vert, 0, nv, 0, vert.length );

                vert = nv;

            }

        }

        i *= 3;

        vert[i] = x;

        vert[i + 1] = y;

        vert[i + 2] = z;

        return nvert++;

    }



    /*

     * Adds a line from vertex p1 to vertex p2.

     */

    private void addLine( int p1, int p2 ) {

        int i = ncon;

        if ( p1 >= nvert || p2 >= nvert ) {

            return;

        }

        if ( i >= maxcon ) {

            if ( con == null ) {

                maxcon = 100;

                con = new int[maxcon];

            }

            else {

                maxcon *= 2;

                int nv[] = new int[maxcon];

                System.arraycopy( con, 0, nv, 0, con.length );

                con = nv;

            }

        }

        if ( p1 > p2 ) {

            int t = p1;

            p1 = p2;

            p2 = t;

        }

        con[i] = ( p1 << 16 ) | p2;

        ncon = i + 1;

    }



    /*

     * Transforms all the points in this model.

     */

    private void transform() {

        if ( transformed || nvert <= 0 ) {

            return;

        }

        if ( tvert == null || tvert.length < nvert * 3 ) {

            tvert = new int[nvert * 3];

        }

        mat.transform( vert, tvert, nvert );

        transformed = true;

    }



    /*

     * ?

     */

    private void sort( int lo0, int hi0 ) {

        int a[] = con;

        int lo = lo0;

        int hi = hi0;

        if ( lo >= hi ) {

            return;

        }

        int mid = a[( lo + hi ) / 2];

        while ( lo < hi ) {

            while ( lo < hi && a[lo] < mid ) {

                lo++;

            }

            while ( lo < hi && a[hi] >= mid ) {

                hi--;

            }

            if ( lo < hi ) {

                int T = a[lo];

                a[lo] = a[hi];

                a[hi] = T;

            }

        }

        if ( hi < lo ) {

            int T = hi;

            hi = lo;

            lo = T;

        }

        sort( lo0, lo );

        sort( lo == lo0 ? lo + 1 : lo, hi0 );

    }



    /*

     * Eliminates duplicate lines.

     */

    private void compress() {

        int limit = ncon;

        int c[] = con;

        sort( 0, ncon - 1 );

        int d = 0;

        int pp1 = -1;

        for ( int i = 0; i < limit; i++ ) {

            int p1 = c[i];

            if ( pp1 != p1 ) {

                c[d] = p1;

                d++;

            }

            pp1 = p1;

        }

        ncon = d;

    }

    

    /**

     * Finds the bounding box of this model.

     */

    private void findBB() {

        if ( nvert <= 0 ) {

            return;

        }

        float v[] = vert;

        float xmin = v[0], xmax = xmin;

        float ymin = v[1], ymax = ymin;

        float zmin = v[2], zmax = zmin;

        for ( int i = nvert * 3; ( i -= 3 ) > 0; ) {

            float x = v[i];

            if ( x < xmin ) {

                xmin = x;

            }

            if ( x > xmax ) {

                xmax = x;

            }

            float y = v[i + 1];

            if ( y < ymin ) {

                ymin = y;

            }

            if ( y > ymax ) {

                ymax = y;

            }

            float z = v[i + 2];

            if ( z < zmin ) {

                zmin = z;

            }

            if ( z > zmax ) {

                zmax = z;

            }

        }

        this.xmax = xmax;

        this.xmin = xmin;

        this.ymax = ymax;

        this.ymin = ymin;

        this.zmax = zmax;

        this.zmin = zmin;

    }

}

