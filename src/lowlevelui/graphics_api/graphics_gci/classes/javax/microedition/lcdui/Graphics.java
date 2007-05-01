/*
 *   
 *
 * Copyright  1990-2007 Sun Microsystems, Inc. All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details (a copy is
 * included at /legal/license.txt).
 * 
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa
 * Clara, CA 95054 or visit www.sun.com if you need additional
 * information or have any questions.
 */

package javax.microedition.lcdui;

import javax.microedition.lcdui.game.Sprite;

import com.sun.me.gci.renderer.GCIRenderContext;
import com.sun.me.gci.renderer.GCIFont;
import com.sun.me.gci.renderer.GCIShapeRenderer;
import com.sun.me.gci.renderer.GCIImageRenderer;
import com.sun.me.gci.renderer.GCITextRenderer;
import com.sun.me.gci.surface.GCIDrawingSurface;

/**
 * Provides simple 2D geometric rendering capability.
 *
 * <p>Drawing primitives are provided for text, images, lines, rectangles,
 * and arcs. Rectangles and arcs may also be filled with a solid color.
 * Rectangles may also be specified with rounded corners. </p>
 *
 * <p>A <code>24</code>-bit color model is provided, with
 * <code>8</code> bits for each of red, green, and
 * blue components of a color. Not all devices support a full
 * <code>24</code> bits' worth
 * of color and thus they will map colors requested by the application into
 * colors available on the device. Facilities are provided in the
 * {@link
 * Display Display} class for obtaining device characteristics, such
 * as
 * whether color is available and how many distinct gray levels are
 * available.
 * Applications may also use {@link Graphics#getDisplayColor(int)
 * getDisplayColor()} to obtain the actual color that would be displayed
 * for a requested color.
 * This enables applications to adapt their behavior to a device without
 * compromising device independence. </p>
 *
 * <p>For all rendering operations, source pixels are always combined with
 * destination pixels using the <em>Source Over Destination</em> rule
 * [Porter-Duff].  Other schemes for combining source pixels with destination
 * pixels, such as raster-ops, are not provided.</p>
 *
 * <p>For the text, line, rectangle, and arc drawing and filling primitives,
 * the source pixel is a pixel representing the current color of the graphics
 * object being used for rendering.  This pixel is always considered to be
 * fully opaque.  With source pixel that is always fully opaque, the Source
 * Over Destination rule has the effect of pixel replacement, where
 * destination pixels are simply replaced with the source pixel from the
 * graphics object.</p>
 *
 * <p>The {@link #drawImage drawImage()} and {@link #drawRegion drawRegion()}
 * methods use an image as the source for rendering operations instead of the
 * current color of the graphics object.  In this context, the Source Over
 * Destination rule has the following properties: a fully opaque pixel in the
 * source must replace the destination pixel, a fully transparent pixel in the
 * source must leave the destination pixel unchanged, and a semitransparent
 * pixel in the source must be alpha blended with the destination pixel.
 * Alpha blending of semitransparent pixels is required.  If an implementation
 * does not support alpha blending, it must remove all semitransparency from
 * image source data at the time the image is created.  See <a
 * href="Image.html#alpha">Alpha Processing</a> for further discussion.
 *
 * <p>The destinations of all graphics rendering are considered to consist
 * entirely of fully opaque pixels.  A property of the Source Over Destination
 * rule is that compositing any pixel with a fully opaque destination pixel
 * always results in a fully opaque destination pixel.  This has the effect of
 * confining full and partial transparency to immutable images, which may only
 * be used as the source for rendering operations.</p>
 *
 * <p>
 * Graphics may be rendered directly to the display or to an off-screen
 * image buffer. The destination of rendered graphics depends on the
 * provenance of the graphics object. A graphics object for rendering
 * to the display is passed to the <code>Canvas</code> object's
 * {@link Canvas#paint(Graphics) paint()}
 * method. This is the only means by which a graphics object may
 * be obtained whose destination is the display. Furthermore, applications
 * may draw using this graphics object only for the duration of the
 * <code>paint()</code> method. </p>
 * <p>
 * A graphics object for rendering to an off-screen image buffer may
 * be obtained by calling the
 * {@link Image#getGraphics() getGraphics()}
 * method on the desired image.
 * A graphics object so obtained may be held indefinitely
 * by the application, and requests may be issued on this graphics
 * object at any time. </p>
 *<p>
 * The default coordinate system's origin is at the
 * upper left-hand corner of the destination. The X-axis direction is
 * positive towards the right, and the Y-axis direction is positive
 * downwards. Applications may assume that horizontal and vertical
 * distances in the coordinate system represent equal distances on the
 * actual device display, that is, pixels are square. A facility is provided
 * for translating the origin of the coordinate system.
 * All coordinates are specified as integers. </p>
 * <p>
 * The coordinate system represents locations between pixels, not the
 * pixels themselves. Therefore, the first pixel in the upper left corner
 * of the display lies in the square bounded by coordinates
 * <code>(0,0) , (1,0) , (0,1) , (1,1)</code>. </p>
 * <p>
 * Under this definition, the semantics for fill operations are clear.
 * Since coordinate grid lines lie between pixels, fill operations
 * affect pixels that lie entirely within the region bounded by the
 * coordinates of the operation. For example, the operation </P>
 * <TABLE BORDER="2">
 * <TR>
 * <TD ROWSPAN="1" COLSPAN="1">
 *    <pre><code>
 *    g.fillRect(0, 0, 3, 2)    </code></pre>
 * </TD>
 * </TR>
 * </TABLE>
 * <P>
 * paints exactly six pixels.  (In this example, and in all subsequent
 * examples, the variable <code>g</code> is assumed to contain a
 * reference to a
 * <code>Graphics</code> object.) </p>
 * <p>
 * Each character of a font contains a set of pixels that forms the shape of
 * the character.  When a character is painted, the pixels forming the
 * character's shape are filled with the <code>Graphics</code>
 * object's current color, and
 * the pixels not part of the character's shape are left untouched.
 * The text drawing calls
 * {@link #drawChar drawChar()},
 * {@link #drawChars drawChars()},
 * {@link #drawString drawString()}, and
 * {@link #drawSubstring drawSubstring()}
 * all draw text in this manner. </p>
 * <p>
 * Lines, arcs, rectangles, and rounded rectangles may be drawn with either a
 * <code>SOLID</code> or a <code>DOTTED</code> stroke style, as set by
 * the {@link #setStrokeStyle
 * setStrokeStyle()} method.  The stroke style does not affect fill, text, and
 * image operations. </p>
 * <p>
 * For the <code>SOLID</code> stroke style,
 * drawing operations are performed with a one-pixel wide pen that fills
 * the pixel immediately
 * below and to the right of the specified coordinate. Drawn lines
 * touch pixels at both endpoints. Thus, the operation </P>
 * <TABLE BORDER="2">
 * <TR>
 * <TD ROWSPAN="1" COLSPAN="1">
 *    <pre><code>
 *    g.drawLine(0, 0, 0, 0);    </code></pre>
 * </TD>
 * </TR>
 * </TABLE>
 * <p>
 * paints exactly one pixel, the first pixel in the upper left corner
 * of the display. </p>
 * <p>
 * Drawing operations under the <code>DOTTED</code> stroke style will
 * touch a subset of
 * pixels that would have been touched under the <code>SOLID</code>
 * stroke style.  The
 * frequency and length of dots is implementation-dependent.  The endpoints of
 * lines and arcs are not guaranteed to be drawn, nor are the corner points of
 * rectangles guaranteed to be drawn.  Dots are drawn by painting with the
 * current color; spaces between dots are left untouched. </p>
 * <p>
 * An artifact of the coordinate system is that the area affected by a fill
 * operation differs slightly from the area affected by a draw operation given
 * the same coordinates. For example, consider the operations </P>
 * <TABLE BORDER="2">
 * <TR>
 * <TD ROWSPAN="1" COLSPAN="1">
 *    <pre><code>
 *    g.fillRect(x, y, w, h); // 1
 *    g.drawRect(x, y, w, h); // 2    </code></pre>
 * </TD>
 * </TR>
 * </TABLE>
 * <P>
 * Statement (1) fills a rectangle <code>w</code> pixels wide and
 * <code>h</code> pixels high.
 * Statement (2) draws a rectangle whose left and top
 * edges are within the area filled by statement (1). However, the
 * bottom and right edges lie one pixel outside the filled area.
 * This is counterintuitive, but it preserves the invariant that </P>
 * <TABLE BORDER="2">
 * <TR>
 * <TD ROWSPAN="1" COLSPAN="1">
 *    <pre><code>
 *    g.drawLine(x, y, x+w, y);
 *    g.drawLine(x+w, y, x+w, y+h);
 *    g.drawLine(x+w, y+h, x, y+h);
 *    g.drawLine(x, y+h, x, y);     </code></pre>
 * </TD>
 * </TR>
 * </TABLE>
 * <P>
 * has an effect identical to statement (2) above. </p>
 * <p>
 * The exact pixels painted by <code>drawLine()</code> and
 * <code>drawArc()</code> are not
 * specified. Pixels touched by a fill operation must either
 * exactly overlap or directly abut pixels touched by the
 * corresponding draw operation. A fill operation must never leave
 * a gap between the filled area and the pixels touched by the
 * corresponding draw operation, nor may the fill operation touch
 * pixels outside the area bounded by the corresponding draw operation. </p>
 *
 * <p>
 * <a name="clip"></a>
 * <h3>Clipping</h3> <p>
 *
 * <p>
 * The clip is the set of pixels in the destination of the
 * <code>Graphics</code> object that may be modified by graphics rendering
 * operations.
 *
 * <p>
 * There is a single clip per <code>Graphics</code> object.
 * The only pixels modified by graphics operations are those that lie within the
 * clip. Pixels outside the clip are not modified by any graphics operations.
 *
 * <p>
 * Operations are provided for intersecting the current clip with
 * a given rectangle and for setting the current clip outright.
 * The application may specify the clip by supplying a clip rectangle
 * using coordinates relative to the current coordinate system.
 *
 * <p>
 * It is legal to specify a clip rectangle whose width or height is zero
 * or negative. In this case the clip is considered to be empty,
 * that is, no pixels are contained within it.
 * Therefore, if any graphics operations are issued under such a clip,
 * no pixels will be modified.
 *
 * <p>
 * It is legal to specify a clip rectangle that extends beyond or resides
 * entirely beyond the bounds of the destination.  No pixels exist outside
 * the bounds of the destination, and the area of the clip rectangle
 * that is outside the destination is ignored.  Only the pixels that lie
 * both within the destination and within the specified clip rectangle
 * are considered to be part of the clip.
 *
 * <p>
 * Operations on the coordinate system,
 * such as {@link Graphics#translate(int, int) translate()},
 * do not modify the clip.
 * The methods
 * {@link Graphics#getClipX() getClipX()},
 * {@link Graphics#getClipY() getClipY()},
 * {@link Graphics#getClipWidth() getClipWidth()} and
 * {@link Graphics#getClipHeight() getClipHeight()}
 * must return a rectangle that,
 * if passed to <code>setClip</code> without an intervening change to
 * the <code>Graphics</code> object's coordinate system, must result in
 * the identical set of pixels in the clip.
 * The rectangle returned from the <code>getClip</code> family of methods
 * may differ from the clip rectangle that was requested in
 * {@link Graphics#setClip(int, int, int, int) setClip()}.
 * This can occur if the coordinate system has been changed or if
 * the implementation has chosen to intersect the clip rectangle
 * with the bounds of the destination of the <code>Graphics</code> object.
 *
 * <p>
 * If a graphics operation is affected by the clip, the pixels
 * touched by that operation must be the same ones that would be touched
 * as if the clip did not affect the operation. For example,
 * consider a clip represented by the rectangle <code>(cx, cy, cw, ch)</code>
 * and a point <code>(x1, y1)</code> that
 * lies outside this rectangle and a point <code>(x2, y2)</code>
 * that lies within this
 * rectangle. In the following code fragment, </P>
 * <TABLE BORDER="2">
 * <TR>
 * <TD ROWSPAN="1" COLSPAN="1">
 *    <pre><code>
 *    g.setClip(0, 0, canvas.getWidth(),
 *                    canvas.getHeight());
 *    g.drawLine(x1, y1, x2, y2); // 3
 *    g.setClip(cx, cy, cw, ch);
 *    g.drawLine(x1, y1, x2, y2); // 4     </code></pre>
 * </TD>
 * </TR>
 * </TABLE>
 * <P>
 * The pixels touched by statement (4) must be identical to the pixels
 * within <code>(cx, cy, cw, ch)</code> touched by statement (3). </p>
 * <p>
 * <a name="anchor"></a>
 * <h3>Anchor Points</h3> <p>
 *
 * The drawing of text is based on &quot;anchor points&quot;.
 * Anchor points are used to minimize the amount of
 * computation required when placing text.
 * For example, in order to center a piece of text,
 * an application needs to call <code>stringWidth()</code> or
 * <code>charWidth()</code> to get the width and then perform a
 * combination of subtraction and division to
 * compute the proper location.
 * The method to draw text is defined as follows:
 * <pre><code>
 * public void drawString(String text, int x, int y, int anchor);
 * </code></pre>
 * This method draws text in the current color,
 * using the current font
 * with its anchor point at <code>(x,y)</code>. The definition
 * of the anchor point must be one of the
 * horizontal constants <code>(LEFT, HCENTER, RIGHT)</code>
 * combined with one of the vertical constants
 * <code>(TOP, BASELINE, BOTTOM)</code> using the bit-wise
 * <code>OR</code> operator.
 * Zero may also be used as the value of an anchor point.
 * Using zero for the anchor point value gives results
 * identical to using <code>TOP | LEFT</code>.</p>
 *
 * <p>
 * Vertical centering of the text is not specified since it is not considered
 * useful, it is hard to specify, and it is burdensome to implement. Thus,
 * the <code>VCENTER</code> value is not allowed in the anchor point
 * parameter of text
 * drawing calls. </p>
 * <p>
 * The actual position of the bounding box
 * of the text relative to the <code>(x, y)</code> location is
 * determined by the anchor point. These anchor
 * points occur at named locations along the
 * outer edge of the bounding box. Thus, if <code>f</code>
 * is <code>g</code>'s current font (as returned by
 * <code>g.getFont()</code>, the following calls will all have
 * identical results: </P>
 * <TABLE BORDER="2">
 * <TR>
 * <TD ROWSPAN="1" COLSPAN="1">
 *    <pre><code>
 *    g.drawString(str, x, y, TOP|LEFT);
 *    g.drawString(str, x + f.stringWidth(str)/2, y, TOP|HCENTER);
 *    g.drawString(str, x + f.stringWidth(str), y, TOP|RIGHT);
 *
 *    g.drawString(str, x,
 *        y + f.getBaselinePosition(), BASELINE|LEFT);
 *    g.drawString(str, x + f.stringWidth(str)/2,
 *        y + f.getBaselinePosition(), BASELINE|HCENTER);
 *    g.drawString(str, x + f.stringWidth(str),
 *        y + f.getBaselinePosition(), BASELINE|RIGHT);
 *
 *    drawString(str, x,
 *        y + f.getHeight(), BOTTOM|LEFT);
 *    drawString(str, x + f.stringWidth(str)/2,
 *        y + f.getHeight(), BOTTOM|HCENTER);
 *    drawString(str, x + f.stringWidth(str),
 *        y + f.getHeight(), BOTTOM|RIGHT);      </code></pre>
 * </TD>
 * </TR>
 * </TABLE>
 * <p>
 * For text drawing, the inter-character and inter-line spacing (leading)
 * specified by the font designer are included as part of the values returned
 * in the {@link Font#stringWidth(java.lang.String) stringWidth()}
 * and {@link Font#getHeight() getHeight()}
 * calls of class {@link Font Font}.
 * For example, given the following code: </P>
 * <TABLE BORDER="2">
 * <TR>
 * <TD ROWSPAN="1" COLSPAN="1">
 *    <pre><code>
 *    // (5)
 *    g.drawString(string1+string2, x, y, TOP|LEFT);
 *
 *    // (6)
 *    g.drawString(string1, x, y, TOP|LEFT);
 *    g.drawString(string2, x + f.stringWidth(string1), y, TOP|LEFT);
 *         </code></pre>
 * </TD>
 * </TR>
 * </TABLE>
 * </P>
 * <P>
 * Code fragments (5) and (6) behave similarly if not identically. This
 * occurs because <code>f.stringWidth()</code>
 * includes the inter-character spacing.  The exact spacing of may differ
 * between these calls if the system supports font kerning.</p>
 *
 * <p>Similarly, reasonable vertical spacing may be
 * achieved simply by adding the font height
 * to the Y-position of subsequent lines. For example: </P>
 * <TABLE BORDER="2">
 * <TR>
 * <TD ROWSPAN="1" COLSPAN="1">
 *    <pre><code>
 *    g.drawString(string1, x, y, TOP|LEFT);
 *    g.drawString(string2, x, y + f.fontHeight(), TOP|LEFT);    </code></pre>
 * </TD>
 * </TR>
 * </TABLE>
 * <P>
 * draws <code>string1</code> and <code>string2</code> on separate lines with
 * an appropriate amount of inter-line spacing. </p>
 * <p>
 * The <code>stringWidth()</code> of the string and the
 * <code>fontHeight()</code> of the font in which
 * it is drawn define the size of the bounding box of a piece of text. As
 * described above, this box includes inter-line and inter-character spacing.
 * The implementation is required to put this space below and to right of the
 * pixels actually belonging to the characters drawn. Applications that wish
 * to position graphics closely with respect to text (for example, to paint a
 * rectangle around a string of text) may assume that there is space below and
 * to the right of a string and that there is <em>no</em> space above
 * and to the
 * left of the string. </p>
 * <p>
 * Anchor points are also used for positioning of images. Similar to text
 * drawing, the anchor point for an image specifies the point on the bounding
 * rectangle of the destination that is to positioned at the
 * <code>(x,y)</code> location
 * given in the graphics request. Unlike text, vertical centering of images
 * is well-defined, and thus the <code>VCENTER</code> value may be
 * used within the anchor
 * point parameter of image drawing requests. Because images have no notion
 * of a baseline, the <code>BASELINE</code> value may not be used
 * within the anchor point
 * parameter of image drawing requests. </p>
 *
 * <h3>Reference</h3>
 *
 * <dl>
 * <dt>Porter-Duff
 * <dd>Porter, T., and T. Duff.  &quot;Compositing Digital Images.&quot;
 * <em>Computer Graphics V18 N3 (SIGGRAPH 1984)</em>, p. 253-259.
 * </dl>
 *
 * @since MIDP 1.0
 */

public class Graphics {

    // SYNC NOTE: the main Graphics class is entirely unlocked.  There is only
    // one instance of Graphics (created in Display) and it is only ever legal
    // for the application to use it when the system calls the paint() method
    // of a Canvas.  Since paint() calls are serialized, and these methods
    // modify only instance state, no locking is necessary.  (If any method
    // were to read or modify global state, locking would need to be done in
    // those cases.)

    /**
     * Constant for centering text and images horizontally
     * around the anchor point
     *
     * <P>Value <code>1</code> is assigned to <code>HCENTER</code>.</P>
     */
    public static final int HCENTER = 1;
  
    /**
     * Constant for centering images vertically
     * around the anchor point.
     *
     * <P>Value <code>2</code> is assigned to <code>VCENTER</code>.</P>
     */
    public static final int VCENTER = 2;
  
    /**
     * Constant for positioning the anchor point of text and images
     * to the left of the text or image.
     *
     * <P>Value <code>4</code> is assigned to <code>LEFT</code>.</P>
     */
    public static final int LEFT = 4;
  
    /**
     * Constant for positioning the anchor point of text and images
     * to the right of the text or image.
     *
     * <P>Value <code>8</code> is assigned to <code>RIGHT</code>.</P>
     */
    public static final int RIGHT = 8;
  
    /**
     * Constant for positioning the anchor point of text and images
     * above the text or image.
     *
     * <P>Value <code>16</code> is assigned to <code>TOP</code>.</P>
     */
    public static final int TOP = 16;
  
    /**
     * Constant for positioning the anchor point of text and images
     * below the text or image.
     *
     * <P>Value <code>32</code> is assigned to <code>BOTTOM</code>.</P>
     */
    public static final int BOTTOM = 32;
  
    /**
     * Constant for positioning the anchor point at the baseline of text.
     *
     * <P>Value <code>64</code> is assigned to <code>BASELINE</code>.</P>
     */
    public static final int BASELINE = 64;

    /**
     * Constant for the <code>SOLID</code> stroke style.
     *
     * <P>Value <code>0</code> is assigned to <code>SOLID</code>.</P>
     */
    public static final int SOLID = 0;

    /**
     * Constant for the <code>DOTTED</code> stroke style.
     *
     * <P>Value <code>1</code> is assigned to <code>DOTTED</code>.</P>
     */
    public static final int DOTTED = 1;

    /**
     * Create a Graphics object
     * @param gciDrawingSurface
     */
    Graphics(GCIDrawingSurface gciDrawingSurface) {
        this.gciDrawingSurface = gciDrawingSurface;
        gciGraphicsContext = new GCIGraphicsContext();
        createRenderers(this.gciDrawingSurface, gciGraphicsContext);
    }

    /**
     * Sets the width and height member variables of this
     * Graphics object to reflect the correct values e.g. for
     * clipping correctly
     *
     * @param w the width of this Graphics object
     * @param h the height of this Graphics object
     */
    void setDimensions(int w, int h) {
        maxWidth  = (short) (w & 0x7fff);
        maxHeight = (short) (h & 0x7fff);
    }

    /**
     * Translates the origin of the graphics context to the point
     * <code>(x, y)</code> in the current coordinate system. All coordinates
     * used in subsequent rendering operations on this graphics
     * context will be relative to this new origin.<p>
     *
     * The effect of calls to <code>translate()</code> are
     * cumulative. For example, calling
     * <code>translate(1, 2)</code> and then <code>translate(3,
     * 4)</code> results in a translation of
     * <code>(4, 6)</code>. <p>
     *
     * The application can set an absolute origin <code>(ax,
     * ay)</code> using the following
     * technique:<p>
     * <code>
     * g.translate(ax - g.getTranslateX(), ay - g.getTranslateY())
     * </code><p>
     *
     * @param x the x coordinate of the new translation origin
     * @param y the y coordinate of the new translation origin
     * @see #getTranslateX()
     * @see #getTranslateY()
     */
    public synchronized void translate(int x, int y) {
        transX += x;
        transY += y;
    }

    /**
     * Gets the X coordinate of the translated origin of this graphics context.
     * @return X of current origin
     */
    public synchronized int getTranslateX() {
        return transX;
    }

    /**
     * Gets the Y coordinate of the translated origin of this graphics context.
     * @return Y of current origin
     */
    public synchronized int getTranslateY() {
        return transY;
    }

    /**
     * Gets the current color.
     * @return an integer in form <code>0x00RRGGBB</code>
     * @see #setColor(int, int, int)
     */
    public synchronized int getColor() {
        return rgbColor;
    }

    /**
     * Gets the red component of the current color.
     * @return integer value in range <code>0-255</code>
     * @see #setColor(int, int, int)
     */
    public synchronized int getRedComponent() {
        return (rgbColor >> 16) & 0xff;
    }

    /**
     * Gets the green component of the current color.
     * @return integer value in range <code>0-255</code>
     * @see #setColor(int, int, int)
     */
    public synchronized int getGreenComponent() {
        return (rgbColor >> 8) & 0xff;
    }

    /**
     * Gets the blue component of the current color.
     * @return integer value in range <code>0-255</code>
     * @see #setColor(int, int, int)
     */
    public synchronized int getBlueComponent() {
        return rgbColor & 0xff;
    }

    /**
     * Gets the current grayscale value of the color being used for rendering
     * operations. If the color was set by
     * <code>setGrayScale()</code>, that value is simply
     * returned. If the color was set by one of the methods that allows setting
     * of the red, green, and blue components, the value returned is 
     * computed from
     * the RGB color components (possibly in a device-specific fashion)
     * that best
     * approximates the brightness of that color.
     *
     * @return integer value in range <code>0-255</code>
     * @see #setGrayScale
     */
    public synchronized int getGrayScale() {
        return gray;
    }

    /**
     * Sets the current color to the specified RGB values. All subsequent
     * rendering operations will use this specified color.
     * @param red the red component of the color being set in range
     * <code>0-255</code>
     * @param green the green component of the color being set in range
     * <code>0-255</code>
     * @param blue the blue component of the color being set in range
     * <code>0-255</code>
     * @throws IllegalArgumentException if any of the color components
     * are outside of range <code>0-255</code>
     * @see #getColor
     */
    public synchronized void setColor(int red, int green, int blue) {
        if ((red < 0)   || (red > 255) 
            || (green < 0) || (green > 255)
            || (blue < 0)  || (blue > 255)) {
            throw new IllegalArgumentException("Value out of range");
        }

        rgbColor = (red << 16) | (green << 8) | blue;
        gray = grayVal(red, green, blue);
        pixel = getPixel(rgbColor, gray, false);

        gciShapeRenderer.paintModified();
        gciImageRenderer.paintModified();
        gciTextRenderer.paintModified();
    }

    /**
     * Sets the current color to the specified RGB values. All subsequent
     * rendering operations will use this specified color. The RGB value
     * passed in is interpreted with the least significant eight bits
     * giving the blue component, the next eight more significant bits
     * giving the green component, and the next eight more significant
     * bits giving the red component. That is to say, the color component
     * is specified in the form of <code>0x00RRGGBB</code>. The high
     * order byte of
     * this value is ignored.
     *
     * @param RGB the color being set
     * @see #getColor
     */
    public synchronized void setColor(int RGB) {
        if (pixel == -1 || (RGB & 0x00ffffff) != rgbColor) {
            int red   = (RGB >> 16) & 0xff;
            int green = (RGB >> 8)  & 0xff;
            int blue  = (RGB)  & 0xff;

            rgbColor = RGB & 0x00ffffff;
            gray = grayVal(red, green, blue);
            pixel = getPixel(rgbColor, gray, false);

            gciShapeRenderer.paintModified();
            gciImageRenderer.paintModified();
            gciTextRenderer.paintModified();
        }
    }

    /**
     * Sets the current grayscale to be used for all subsequent
     * rendering operations. For monochrome displays, the behavior
     * is clear. For color displays, this sets the color for all
     * subsequent drawing operations to be a gray color equivalent
     * to the value passed in. The value must be in the range
     * <code>0-255</code>.
     * @param value the desired grayscale value
     * @throws IllegalArgumentException if the gray value is out of range
     * @see #getGrayScale
     */
    public synchronized void setGrayScale(int value) {
        if ((value < 0) || (value > 255)) {
            throw new IllegalArgumentException("Gray value out of range");
        }

        if (pixel == -1 || gray != value) {
            rgbColor = (value << 16) | (value << 8) | value;
            gray = value;
            pixel = getPixel(rgbColor, gray, true);
       
            gciShapeRenderer.paintModified();
            gciImageRenderer.paintModified();
            gciTextRenderer.paintModified();
        }
    }

    /**
     * Gets the current font.
     * @return current font
     * @see javax.microedition.lcdui.Font
     * @see #setFont(javax.microedition.lcdui.Font)
     */
    public synchronized Font getFont() {
        return currentFont;
    }

    /**
     * Sets the stroke style used for drawing lines, arcs, rectangles, and 
     * rounded rectangles.  This does not affect fill, text, and image 
     * operations.
     * @param style can be <code>SOLID</code> or <code>DOTTED</code>
     * @throws IllegalArgumentException if the <code>style</code> is illegal
     * @see #getStrokeStyle
     */
    public synchronized void setStrokeStyle(int style) {
        if ((style != SOLID) && (style != DOTTED)) {
            throw new IllegalArgumentException("Invalid line style");
        }

        this.style = style;

        gciShapeRenderer.strokeModified();
        gciImageRenderer.strokeModified();
        gciTextRenderer.strokeModified();
    }

    /**
     * Gets the stroke style used for drawing operations.
     * @return stroke style, <code>SOLID</code> or <code>DOTTED</code>
     * @see #setStrokeStyle
     */
    public synchronized int getStrokeStyle() {
        return style;
    }

    /**
     * Sets the font for all subsequent text rendering operations.  If font is 
     * <code>null</code>, it is equivalent to
     * <code>setFont(Font.getDefaultFont())</code>.
     * 
     * @param font the specified font
     * @see javax.microedition.lcdui.Font
     * @see #getFont()
     * @see #drawString(java.lang.String, int, int, int)
     * @see #drawChars(char[], int, int, int, int, int)
     */
    public synchronized void setFont(Font font) {
        currentFont = (font == null) ? Font.getDefaultFont() : font;
        gciTextRenderer.fontModified();
    }
  
    /**
     * Gets the X offset of the current clipping area, relative
     * to the coordinate system origin of this graphics context.
     * Separating the <code>getClip</code> operation into two methods returning
     * integers is more performance and memory efficient than one
     * <code>getClip()</code> call returning an object.
     * @return X offset of the current clipping area
     * @see #clipRect(int, int, int, int)
     * @see #setClip(int, int, int, int)
     */
    public synchronized int getClipX() {
        return clipX1 - transX;
    }

    /**
     * Gets the Y offset of the current clipping area, relative
     * to the coordinate system origin of this graphics context.
     * Separating the <code>getClip</code> operation into two methods returning
     * integers is more performance and memory efficient than one
     * <code>getClip()</code> call returning an object.
     * @return Y offset of the current clipping area
     * @see #clipRect(int, int, int, int)
     * @see #setClip(int, int, int, int)
     */
    public synchronized int getClipY() {
        return clipY1 - transY;
    }

    /**
     * Gets the width of the current clipping area.
     * @return width of the current clipping area.
     * @see #clipRect(int, int, int, int)
     * @see #setClip(int, int, int, int)
     */
    public synchronized int getClipWidth() {
        return clipX2 - clipX1;
    }


    /**
     * Gets the height of the current clipping area.
     * @return height of the current clipping area.
     * @see #clipRect(int, int, int, int)
     * @see #setClip(int, int, int, int)
     */
    public synchronized int getClipHeight() {
        return clipY2 - clipY1;
    }

    /**
     * Internal routine to get the clip in a single call. The input
     * parameter must be a 4 element integer array. The values of the
     * array upon return will be equal to the same values as would be
     * returned from getClipX(), getClipY(), getClipX()+getClipWidth(), 
     * and getClipY()+getClipHeight().
     *
     * @param region a four element array to hold the clip rectangle
     */
    void getClip(int[] region) {
        region[0] = clipX1 - transX;
        region[1] = clipY1 - transY;
        region[2] = clipX2 - transX;
        region[3] = clipY2 - transY;
    }

    /**
     * Intersects the current clip with the specified rectangle.
     * The resulting clipping area is the intersection of the current
     * clipping area and the specified rectangle.
     * This method can only be used to make the current clip smaller.
     * To set the current clip larger, use the <code>setClip</code> method.
     * Rendering operations have no effect outside of the clipping area.
     * @param x the x coordinate of the rectangle to intersect the clip with
     * @param y the y coordinate of the rectangle to intersect the clip with
     * @param width the width of the rectangle to intersect the clip with
     * @param height the height of the rectangle to intersect the clip with
     * @see #setClip(int, int, int, int)
     */
    public synchronized void clipRect(int x, int y, int width, int height) {

        int translatedX1, translatedY1;
        int translatedX2, translatedY2;

        if (width <= 0 || height <= 0) {
            clipX1 = clipY1 = clipX2 = clipY2 = 0;
            clipped = true;
            gciImageRenderer.clipModified();
            gciTextRenderer.clipModified();
            gciShapeRenderer.clipModified();
            return;
        }

        // Translate the given coordinates
        translatedX1 = x + transX;
        translatedY1 = y + transY;

        // Detect overflow
        if (translatedX1 < 0) {
            translatedX1 = (x < 0 || transX < 0) ? 0 : maxWidth;
        }
        if (translatedY1 < 0) {
            translatedY1 = (y < 0 || transY < 0) ? 0 : maxHeight;
        }

        // If the passed in rect is below our current clip
        if ((clipX2 < translatedX1) || (clipY2 < translatedY1)) {
            // we have no intersection
            clipX1 = clipY1 = clipX2 = clipY2 = 0;
            clipped = true;
            gciImageRenderer.clipModified();
            gciTextRenderer.clipModified();
            gciShapeRenderer.clipModified();
            return;
        }

        if (translatedX1 > clipX1) {
            clipX1 = (short) (translatedX1 & 0x7fff);
            clipped = true;
        }

        if (translatedY1 > clipY1) {
            clipY1 = (short) (translatedY1 & 0x7fff);
            clipped = true;
        }

        // Start handling bottom right area

        translatedX2 = x + transX + width;
        translatedY2 = y + transY + height;

        // Detect Overflow
        if (translatedX2 < 0) {
            translatedX2 = (x < 0 || transX < 0) ? translatedX1 : maxWidth;
        }
        if (translatedY2 < 0) {
            translatedY2 = (y < 0 || transY < 0) ? translatedY1 : maxHeight;
        }

        // If the passed in rect is above our current clip
        if (translatedX2 < clipX1 || translatedY2 < clipY1) {
            // we have no intersection
            clipX1 = clipY1 = clipX2 = clipY2 = 0;
            clipped = true;
            gciImageRenderer.clipModified();
            gciTextRenderer.clipModified();
            gciShapeRenderer.clipModified();
            return;
        }

        if (translatedX2 <= clipX2) {
            clipX2 = (short) translatedX2;
            clipped = true;
        }

        if (translatedY2 <= clipY2) {
            clipY2 = (short) translatedY2;
            clipped = true;
        }

        if (clipped == true) {
            if (clipX2 < clipX1)
              clipX2 = clipX1;
            if (clipY2 < clipY1)
              clipY2 = clipY1;
        }
        /**
         *  sanity check 

         if (clipX1 < 0 || clipY1 < 0 ||
             clipX2 > maxWidth || clipY2 > maxHeight ||
             clipX1 > clipX2 || clipY1 > clipY2)
             System.out.println("Graphics:clipRect error: clipX1 = "+clipX1+
             " clipY1 = "+clipY1+" clipX2 = "+clipX2+" clipY2 = "+clipY2+
              " maxWidth = "+maxWidth+" maxHeight = "+maxHeight);
         if (runtimeClipEnforce)
             System.out.println("Graphics:clipRect runtimeClipEnforce:"+
             " systemClipX1 = "+systemClipX1+" systemClipY1 = "+systemClipY1+
             " systemClipX2 = "+systemClipX2+" systemClipY2 = "+systemClipY2);

         * end sanity check 
         */

        gciImageRenderer.clipModified();
        gciTextRenderer.clipModified();
        gciShapeRenderer.clipModified();

    }

    /**
     * Sets the current clip to the rectangle specified by the
     * given coordinates.
     * Rendering operations have no effect outside of the clipping area.
     * @param x the x coordinate of the new clip rectangle
     * @param y the y coordinate of the new clip rectangle
     * @param width the width of the new clip rectangle
     * @param height the height of the new clip rectangle
     * @see #clipRect(int, int, int, int)
     */
    public synchronized void setClip(int x, int y, int width, int height) {

        int translatedX1, translatedY1;
        int translatedX2, translatedY2;

        // If width or height is zero or less then zero,
        // we do not preserve the current clipping and
        // set all clipping values to zero.
        if ((width <= 0) || (height <= 0)) {
            clipX1 = clipY1 = clipX2 = clipY2 = 0;
            clipped = true;
            gciImageRenderer.clipModified();
            gciTextRenderer.clipModified();
            gciShapeRenderer.clipModified();
            return;
        }

        // Translate the given coordinates
        translatedX1 = x + transX;
        translatedY1 = y + transY;

        // Detect Overflow
        if (translatedX1 < 0) {
            translatedX1 = (x < 0 || transX < 0) ? 0 : maxWidth;
        }
        if (translatedY1 < 0) {
            translatedY1 = (y < 0 || transY < 0) ? 0 : maxHeight;
        }

        clipX1 = (short)(translatedX1 & 0x7fff);
        clipY1 = (short)(translatedY1 & 0x7fff);

        if ((translatedX1 >= maxWidth) 
            || (translatedY1 >= maxHeight)) {
            clipX1 = clipY1 = clipX2 = clipY2 = 0;
            clipped = true;
            gciImageRenderer.clipModified();
            gciTextRenderer.clipModified();
            gciShapeRenderer.clipModified();
            return;
        }

        // Check against the runtime library clip values
        if (runtimeClipEnforce) {
          if (clipX1 < systemClipX1)
                  clipX1 = systemClipX1;
          if (clipY1 < systemClipY1) {
                  clipY1 = systemClipY1;
          }
        }

        // Translate the given width, height to abs. coordinates
        translatedX2 = x + transX + width;
        translatedY2 = y + transY + height;

        // Detect overflow
        if (translatedX2 < 0) {
            translatedX2 = (x < 0 || transX < 0) ? translatedX1 : maxWidth;
        } else {
          if (translatedX2 > maxWidth)
            translatedX2 = maxWidth;
        }
        if (translatedY2 < 0) {
            translatedY2 = (y < 0 || transY < 0) ? translatedY1 : maxHeight;
        } else {
          if (translatedY2 > maxHeight)
            translatedY2 = maxHeight;
        }

        clipX2 = (short) (translatedX2 & 0x7FFF);
        clipY2 = (short) (translatedY2 & 0x7FFF);

        // Check against the runtime library clip values
        if (runtimeClipEnforce) {
          if (clipX2 > systemClipX2)
                  clipX2 = systemClipX2;
          if (clipY2 > systemClipY2)
                  clipY2 = systemClipY2;
        }

        if ((clipX1 != 0) || (clipY1 != 0)
                || (clipX2 != maxWidth) || (clipY2 != maxHeight)) {
            clipped = true;
        }

        /**
         *  sanity check
        if (clipX1 < 0 || clipY1 < 0 ||
            clipX2 > maxWidth || clipY2 > maxHeight ||
            clipX1 > clipX2 || clipY1 > clipY2)
          System.out.println("Graphics:setClip error: clipX1 = "+clipX1+
              " clipY1 = "+clipY1+" clipX2 = "+clipX2+" clipY2 = "+clipY2+
              " maxWidth = "+maxWidth+" maxHeight = "+maxHeight);
        if (runtimeClipEnforce)
          System.out.println("Graphics:setClip runtimeClipEnforce:"+
              " systemClipX1 = "+systemClipX1+" systemClipY1 = "+systemClipY1+
              " systemClipX2 = "+systemClipX2+" systemClipY2 = "+systemClipY2);

         * end sanity check 
         */

        gciImageRenderer.clipModified();
        gciTextRenderer.clipModified();
        gciShapeRenderer.clipModified();
    }

    /**
     * Draws a line between the coordinates <code>(x1,y1)</code> and
     * <code>(x2,y2)</code> using
     * the current color and stroke style.
     * @param x1 the x coordinate of the start of the line
     * @param y1 the y coordinate of the start of the line
     * @param x2 the x coordinate of the end of the line
     * @param y2 the y coordinate of the end of the line
     */
    public void drawLine(int x1, int y1, int x2, int y2) {
        gciShapeRenderer.drawLine(x1 + transX, y1 + transY, 
                                  x2 + transX, y2 + transY);
    }

    /**
     * Fills the specified rectangle with the current color.
     * If either width or height is zero or less,
     * nothing is drawn.
     * @param x the x coordinate of the rectangle to be filled
     * @param y the y coordinate of the rectangle to be filled
     * @param width the width of the rectangle to be filled
     * @param height the height of the rectangle to be filled
     * @see #drawRect(int, int, int, int)
     */
    public void fillRect(int x, int y, int width, int height) {
        gciShapeRenderer.fillRect(x + transX, y + transY, width, height);
    }
 
    /**
     * Draws the outline of the specified rectangle using the current
     * color and stroke style.
     * The resulting rectangle will cover an area <code>(width + 1)</code>
     * pixels wide by <code>(height + 1)</code> pixels tall.
     * If either width or height is less than
     * zero, nothing is drawn.
     * @param x the x coordinate of the rectangle to be drawn
     * @param y the y coordinate of the rectangle to be drawn
     * @param width the width of the rectangle to be drawn
     * @param height the height of the rectangle to be drawn
     * @see #fillRect(int, int, int, int)
     */
    public void drawRect(int x, int y, int width, int height) {
        gciShapeRenderer.drawRect(x + transX, y + transY, width, height);
    }

    /**
     * Draws the outline of the specified rounded corner rectangle
     * using the current color and stroke style.
     * The resulting rectangle will cover an area <code>(width +
     * 1)</code> pixels wide
     * by <code>(height + 1)</code> pixels tall.
     * If either <code>width</code> or <code>height</code> is less than
     * zero, nothing is drawn.
     * @param x the x coordinate of the rectangle to be drawn
     * @param y the y coordinate of the rectangle to be drawn
     * @param width the width of the rectangle to be drawn
     * @param height the height of the rectangle to be drawn
     * @param arcWidth the horizontal diameter of the arc at the four corners
     * @param arcHeight the vertical diameter of the arc at the four corners
     * @see #fillRoundRect(int, int, int, int, int, int)
     */
    public void drawRoundRect(int x, int y, int width, int height,
                              int arcWidth, int arcHeight) {
        gciShapeRenderer.drawRoundRect(x + transX, y + transY, width, height,
                                       arcWidth, arcHeight);
    }
 
    /**
     * Fills the specified rounded corner rectangle with the current color.
     * If either <code>width</code> or <code>height</code> is zero or less,
     * nothing is drawn.
     * @param x the x coordinate of the rectangle to be filled
     * @param y the y coordinate of the rectangle to be filled
     * @param width the width of the rectangle to be filled
     * @param height the height of the rectangle to be filled
     * @param arcWidth the horizontal diameter of the arc at the four
     * corners
     * @param arcHeight the vertical diameter of the arc at the four corners
     * @see #drawRoundRect(int, int, int, int, int, int)
     */
    public void fillRoundRect(int x, int y, int width, int height,
                              int arcWidth, int arcHeight) {
        gciShapeRenderer.fillRoundRect(x + transX, y + transY, width, height,
                                       arcWidth, arcHeight);
    }
                          
    /**
     * Fills a circular or elliptical arc covering the specified rectangle.
     * <p>
     * The resulting arc begins at <code>startAngle</code> and extends
     * for <code>arcAngle</code> degrees.
     * Angles are interpreted such that <code>0</code> degrees
     * is at the <code>3</code> o'clock position.
     * A positive value indicates a counter-clockwise rotation
     * while a negative value indicates a clockwise rotation.
     * <p>
     * The center of the arc is the center of the rectangle whose origin
     * is (<em>x</em>,&nbsp;<em>y</em>) and whose size is specified by the
     * <code>width</code> and <code>height</code> arguments.
     * <p>
     * If either <code>width</code> or <code>height</code> is zero or less,
     * nothing is drawn.
     *
     * <p> The filled region consists of the &quot;pie wedge&quot;
     * region bounded
     * by the arc
     * segment as if drawn by <code>drawArc()</code>, the radius extending from
     * the center to
     * this arc at <code>startAngle</code> degrees, and radius extending
     * from the
     * center to this arc at <code>startAngle + arcAngle</code> degrees. </p>
     *
     * <p> The angles are specified relative to the non-square extents of
     * the bounding rectangle such that <code>45</code> degrees always
     * falls on the
     * line from the center of the ellipse to the upper right corner of
     * the bounding rectangle. As a result, if the bounding rectangle is
     * noticeably longer in one axis than the other, the angles to the
     * start and end of the arc segment will be skewed farther along the
     * longer axis of the bounds. </p>
     *
     * @param x the <em>x</em> coordinate of the upper-left corner of
     * the arc to be filled.
     * @param y the <em>y</em> coordinate of the upper-left corner of the
     * arc to be filled.
     * @param width the width of the arc to be filled
     * @param height the height of the arc to be filled
     * @param startAngle the beginning angle.
     * @param arcAngle the angular extent of the arc,
     * relative to the start angle.
     * @see #drawArc(int, int, int, int, int, int)
     */
    public void fillArc(int x, int y, int width, int height,
                        int startAngle, int arcAngle) {
        gciShapeRenderer.fillArc(x + transX, y + transY, width, height, 
                                 startAngle, arcAngle);
    }

    /**
     * Draws the outline of a circular or elliptical arc
     * covering the specified rectangle,
     * using the current color and stroke style.
     * <p>
     * The resulting arc begins at <code>startAngle</code> and extends
     * for <code>arcAngle</code> degrees, using the current color.
     * Angles are interpreted such that <code>0</code>&nbsp;degrees
     * is at the <code>3</code>&nbsp;o'clock position.
     * A positive value indicates a counter-clockwise rotation
     * while a negative value indicates a clockwise rotation.
     * <p>
     * The center of the arc is the center of the rectangle whose origin
     * is (<em>x</em>,&nbsp;<em>y</em>) and whose size is specified by the
     * <code>width</code> and <code>height</code> arguments.
     * <p>
     * The resulting arc covers an area
     * <code>width&nbsp;+&nbsp;1</code> pixels wide
     * by <code>height&nbsp;+&nbsp;1</code> pixels tall.
     * If either <code>width</code> or <code>height</code> is less than zero,
     * nothing is drawn.
     *
     * <p> The angles are specified relative to the non-square extents of
     * the bounding rectangle such that <code>45</code> degrees always
     * falls on the
     * line from the center of the ellipse to the upper right corner of
     * the bounding rectangle. As a result, if the bounding rectangle is
     * noticeably longer in one axis than the other, the angles to the
     * start and end of the arc segment will be skewed farther along the
     * longer axis of the bounds. </p>
     *
     * @param x the <em>x</em> coordinate of the upper-left corner
     * of the arc to be drawn
     * @param y the <em>y</em> coordinate of the upper-left corner
     * of the arc to be drawn
     * @param width the width of the arc to be drawn
     * @param height the height of the arc to be drawn
     * @param startAngle the beginning angle
     * @param arcAngle the angular extent of the arc, relative to
     * the start angle
     * @see #fillArc(int, int, int, int, int, int)
     */
    public void drawArc(int x, int y, int width, int height,
                       int startAngle, int arcAngle)  {
        gciShapeRenderer.drawArc(x + transX, y + transY, width, height, 
                                 startAngle, arcAngle);
    }

    /**
     * Draws the specified <code>String</code> using the current font and color.
     * The <code>x,y</code> position is the position of the anchor point.
     * See <a href="#anchor">anchor points</a>.
     * @param str the <code>String</code> to be drawn
     * @param x the x coordinate of the anchor point
     * @param y the y coordinate of the anchor point
     * @param anchor the anchor point for positioning the text
     * @throws NullPointerException if <code>str</code> is <code>null</code>
     * @throws IllegalArgumentException if anchor is not a legal value
     * @see #drawChars(char[], int, int, int, int, int)
     */
    public void drawString(java.lang.String str,
                           int x, int y, int anchor) {
        if (str == null) {
            throw new NullPointerException();
        }
 
        if (!checkAnchor(anchor, VCENTER)) {
            throw new IllegalArgumentException();
        }

        x += transX;
        y += transY;

        if ((anchor & LEFT) == 0) {
            int strWidth = currentFont.stringWidth(str);
            if ((anchor & RIGHT) != 0){
                x -= strWidth;
            } else if ((anchor & HCENTER) != 0) {
                x -= (strWidth / 2);
            }
        }

        if ((anchor & BASELINE) == 0) {
            if ((anchor & TOP) != 0) {
                y += currentFont.getBaselinePosition();
            } else if ((anchor & BOTTOM) != 0) {
                y -= currentFont.getHeight() - 
                     currentFont.getBaselinePosition();
            }
        }

        gciTextRenderer.drawString(str, x, y);
    }

    /**
     * Draws the specified <code>String</code> using the current font and color.
     * The <code>x,y</code> position is the position of the anchor point.
     * See <a href="#anchor">anchor points</a>.
     *
     * <p>The <code>offset</code> and <code>len</code> parameters must
     * specify a valid range of characters within
     * the string <code>str</code>.
     * The <code>offset</code> parameter must be within the
     * range <code>[0..(str.length())]</code>, inclusive.
     * The <code>len</code> parameter
     * must be a non-negative integer such that
     * <code>(offset + len) &lt;= str.length()</code>.</p>
     *
     * @param str the <code>String</code> to be drawn
     * @param offset zero-based index of first character in the substring
     * @param len length of the substring
     * @param x the x coordinate of the anchor point
     * @param y the y coordinate of the anchor point
     * @param anchor the anchor point for positioning the text
     * @see #drawString(String, int, int, int).
     * @throws StringIndexOutOfBoundsException if <code>offset</code>
     * and <code>length</code> do not specify
     * a valid range within the <code>String</code> <code>str</code>
     * @throws IllegalArgumentException if <code>anchor</code>
     * is not a legal value
     * @throws NullPointerException if <code>str</code> is <code>null</code>
     */
    public void drawSubstring(String str, int offset, int len,
                              int x, int y, int anchor) {
        // will throw NullPointerException
        int strLen = str.length();
        if ((offset < 0) || (offset > strLen) || 
            (len < 0) || (len > strLen) ||
            ((offset + len) < 0) || ((offset + len) > strLen)) {
            throw new StringIndexOutOfBoundsException();
        }

        drawString(str.substring(offset, offset + len), x, y, anchor);
    }

    /**
     * Draws the specified character using the current font and color.
     * @param character the character to be drawn
     * @param x the x coordinate of the anchor point
     * @param y the y coordinate of the anchor point
     * @param anchor the anchor point for positioning the text; see
     * <a href="#anchor">anchor points</a>
     *
     * @throws IllegalArgumentException if <code>anchor</code>
     * is not a legal value
     *
     * @see #drawString(java.lang.String, int, int, int)
     * @see #drawChars(char[], int, int, int, int, int)
     */
    public void drawChar(char character, int x, int y, int anchor) {
        drawString(new String(new char[]{character}), x, y, anchor);
    }

    /**
     * Draws the specified characters using the current font and color.
     *
     * <p>The <code>offset</code> and <code>length</code> parameters must
     * specify a valid range of characters within
     * the character array <code>data</code>.
     * The <code>offset</code> parameter must be within the
     * range <code>[0..(data.length)]</code>, inclusive.
     * The <code>length</code> parameter
     * must be a non-negative integer such that
     * <code>(offset + length) &lt;= data.length</code>.</p>
     *
     * @param data the array of characters to be drawn
     * @param offset the start offset in the data
     * @param length the number of characters to be drawn
     * @param x the x coordinate of the anchor point
     * @param y the y coordinate of the anchor point
     * @param anchor the anchor point for positioning the text; see
     * <a href="#anchor">anchor points</a>
     *
     * @throws ArrayIndexOutOfBoundsException if <code>offset</code>
     * and <code>length</code>
     * do not specify a valid range within the data array
     * @throws IllegalArgumentException if anchor is not a legal value
     * @throws NullPointerException if <code>data</code> is <code>null</code>
     *
     * @see #drawString(java.lang.String, int, int, int)
     */
    public void drawChars(char[] data, int offset, int length,
                          int x, int y, int anchor) {
       

        // this will throw NullPointerException if data == null
        int chLen = data.length;

        if ((offset < 0) || (offset > chLen) || 
            (length < 0) || (length > chLen) ||
            ((offset + length) < 0) || ((offset + length) > chLen)) {
            throw new ArrayIndexOutOfBoundsException();
        }

        drawString(new String(data, offset, length), x, y, anchor);

    }
 
    /**
     * Draws the specified image by using the anchor point.
     * The image can be drawn in different positions relative to
     * the anchor point by passing the appropriate position constants.
     * See <a href="#anchor">anchor points</a>.
     *
     * <p>If the source image contains transparent pixels, the corresponding
     * pixels in the destination image must be left untouched.  If the source
     * image contains partially transparent pixels, a compositing operation 
     * must be performed with the destination pixels, leaving all pixels of 
     * the destination image fully opaque.</p>
     *
     * <p>If <code>img</code> is the same as the destination of this Graphics
     * object, the result is undefined.  For copying areas within an
     * <code>Image</code>, {@link #copyArea copyArea} should be used instead.
     * </p>
     *
     * @param img the specified image to be drawn
     * @param x the x coordinate of the anchor point
     * @param y the y coordinate of the anchor point
     * @param anchor the anchor point for positioning the image
     * @throws IllegalArgumentException if <code>anchor</code>
     * is not a legal value
     * @throws NullPointerException if <code>img</code> is <code>null</code>
     * @see Image
     */
    public void drawImage(Image img, int x, int y, int anchor) {

        if (img == null) {
            throw new NullPointerException();
        }

        int[] point = new int[]{x, y};
        if (!normalizeAnchor(point, img.getWidth(), img.getHeight(), anchor)) {
            throw new IllegalArgumentException();
        }

	render(img, point[0] + transX, point[1]+ transY, anchor);
    }

    /**
     * Copies a region of the specified source image to a location within
     * the destination, possibly transforming (rotating and reflecting)
     * the image data using the chosen transform function.
     *
     * <p>The destination, if it is an image, must not be the same image as
     * the source image.  If it is, an exception is thrown.  This restriction
     * is present in order to avoid ill-defined behaviors that might occur if
     * overlapped, transformed copies were permitted.</p>
     *
     * <p>The transform function used must be one of the following, as defined
     * in the {@link javax.microedition.lcdui.game.Sprite Sprite} class:<br>
     *
     * <code>Sprite.TRANS_NONE</code> - causes the specified image
     * region to be copied unchanged<br>
     * <code>Sprite.TRANS_ROT90</code> - causes the specified image
     * region to be rotated clockwise by 90 degrees.<br>
     * <code>Sprite.TRANS_ROT180</code> - causes the specified image
     * region to be rotated clockwise by 180 degrees.<br>
     * <code>Sprite.TRANS_ROT270</code> - causes the specified image
     * region to be rotated clockwise by 270 degrees.<br>
     * <code>Sprite.TRANS_MIRROR</code> - causes the specified image
     * region to be reflected about its vertical center.<br>
     * <code>Sprite.TRANS_MIRROR_ROT90</code> - causes the specified image
     * region to be reflected about its vertical center and then rotated
     * clockwise by 90 degrees.<br>
     * <code>Sprite.TRANS_MIRROR_ROT180</code> - causes the specified image
     * region to be reflected about its vertical center and then rotated
     * clockwise by 180 degrees.<br>
     * <code>Sprite.TRANS_MIRROR_ROT270</code> - causes the specified image
     * region to be reflected about its vertical center and then rotated
     * clockwise by 270 degrees.<br></p>
     *
     * <p>If the source region contains transparent pixels, the corresponding
     * pixels in the destination region must be left untouched.  If the source
     * region contains partially transparent pixels, a compositing operation
     * must be performed with the destination pixels, leaving all pixels of
     * the destination region fully opaque.</p>
     *
     * <p> The <code>(x_src, y_src)</code> coordinates are relative to
     * the upper left
     * corner of the source image.  The <code>x_src</code>,
     * <code>y_src</code>, <code>width</code>, and <code>height</code>
     * parameters specify a rectangular region of the source image.  It is
     * illegal for this region to extend beyond the bounds of the source
     * image.  This requires that: </P>
     * <TABLE BORDER="2">
     * <TR>
     * <TD ROWSPAN="1" COLSPAN="1">
     *    <pre><code>
     *   x_src &gt;= 0
     *   y_src &gt;= 0
     *   x_src + width &lt;= source width
     *   y_src + height &lt;= source height    </code></pre>
     * </TD>
     * </TR>
     * </TABLE>
     * <P>
     * The <code>(x_dest, y_dest)</code> coordinates are relative to
     * the coordinate
     * system of this Graphics object.  It is legal for the destination
     * area to extend beyond the bounds of the <code>Graphics</code>
     * object.  Pixels
     * outside of the bounds of the <code>Graphics</code> object will
     * not be drawn.</p>
     *
     * <p>The transform is applied to the image data from the region of the
     * source image, and the result is rendered with its anchor point
     * positioned at location <code>(x_dest, y_dest)</code> in the
     * destination.</p>
     *
     * @param src the source image to copy from
     * @param x_src the x coordinate of the upper left corner of the region
     * within the source image to copy
     * @param y_src the y coordinate of the upper left corner of the region
     * within the source image to copy
     * @param width the width of the region to copy
     * @param height the height of the region to copy
     * @param transform the desired transformation for the selected region
     * being copied
     * @param x_dest the x coordinate of the anchor point in the
     * destination drawing area
     * @param y_dest the y coordinate of the anchor point in the
     * destination drawing area
     * @param anchor the anchor point for positioning the region within
     * the destination image
     *
     * @throws IllegalArgumentException if <code>src</code> is the
     * same image as the
     * destination of this <code>Graphics</code> object
     * @throws NullPointerException if <code>src</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>transform</code> is invalid
     * @throws IllegalArgumentException if <code>anchor</code> is invalid
     * @throws IllegalArgumentException if the region to be copied exceeds
     * the bounds of the source image
     */
    public void drawRegion(Image src, 
                           int x_src, int y_src,
                           int width, int height, 
                           int transform,
                           int x_dest, int y_dest, 
                           int anchor) {

        if (src == null) {
            throw new NullPointerException();
        }
        // TODO check if src is the same image as destination of this Graphics

        if ((transform < Sprite.TRANS_NONE) || 
            (transform > Sprite.TRANS_MIRROR_ROT90)) {
            throw new IllegalArgumentException();
        }
     
        int[] point = new int[]{x_dest, y_dest};

        if (!normalizeAnchor(point, width, height, anchor)) {
            throw new IllegalArgumentException();
        }

        // TODO check if the following needed - moved from native
        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight();
        if ((height < 0) || (width < 0) || 
            (point[0] < 0) || (point[1] < 0) ||
           ((x_src + width) > imgWidth) || 
           ((y_src + height) > imgHeight)) {
            throw new IllegalArgumentException();
        }

	renderRegion(img, x_src, y_src, width, height, transform,
		     point[0] + transX, point[1]+ transY, anchor);
    }

    /**
     * Copies the contents of a rectangular area
     * <code>(x_src, y_src, width, height)</code> to a destination area,
     * whose anchor point identified by anchor is located at
     * <code>(x_dest, y_dest)</code>.  The effect must be that the
     * destination area
     * contains an exact copy of the contents of the source area
     * immediately prior to the invocation of this method.  This result must
     * occur even if the source and destination areas overlap.
     *
     * <p>The points <code>(x_src, y_src)</code> and <code>(x_dest,
     * y_dest)</code> are both specified
     * relative to the coordinate system of the <code>Graphics</code>
     * object.  It is
     * illegal for the source region to extend beyond the bounds of the
     * graphic object.  This requires that: </P>
     * <TABLE BORDER="2">
     * <TR>
     * <TD ROWSPAN="1" COLSPAN="1">
     *    <pre><code>
     *   x_src + tx &gt;= 0
     *   y_src + ty &gt;= 0
     *   x_src + tx + width &lt;= width of Graphics object's destination
     *   y_src + ty + height &lt;= height of Graphics object's destination
     *    </code></pre>
     * </TD>
     * </TR>
     * </TABLE>
     * 
     * <p>where <code>tx</code> and <code>ty</code> represent the X and Y 
     * coordinates of the translated origin of this graphics object, as 
     * returned by <code>getTranslateX()</code> and
     * <code>getTranslateY()</code>, respectively.</p>
     * 
     * <P>
     * However, it is legal for the destination area to extend beyond
     * the bounds of the <code>Graphics</code> object.  Pixels outside
     * of the bounds of
     * the <code>Graphics</code> object will not be drawn.</p>
     *
     * <p>The <code>copyArea</code> method is allowed on all
     * <code>Graphics</code> objects except those
     * whose destination is the actual display device.  This restriction is
     * necessary because allowing a <code>copyArea</code> method on
     * the display would
     * adversely impact certain techniques for implementing
     * double-buffering.</p>
     *
     * <p>Like other graphics operations, the <code>copyArea</code>
     * method uses the Source
     * Over Destination rule for combining pixels.  However, since it is
     * defined only for mutable images, which can contain only fully opaque
     * pixels, this is effectively the same as pixel replacement.</p>
     *
     * @param x_src  the x coordinate of upper left corner of source area
     * @param y_src  the y coordinate of upper left corner of source area
     * @param width  the width of the source area
     * @param height the height of the source area
     * @param x_dest the x coordinate of the destination anchor point
     * @param y_dest the y coordinate of the destination anchor point
     * @param anchor the anchor point for positioning the region within
     *        the destination image
     *
     * @throws IllegalStateException if the destination of this
     * <code>Graphics</code> object is the display device
     * @throws IllegalArgumentException if the region to be copied exceeds
     * the bounds of the source image
     *
     */
    public synchronized void copyArea(int x_src, int y_src, 
                                      int width, int height,
                                      int x_dest, int y_dest, int anchor) {

        if (isScreenGraphics()) {
            throw new IllegalStateException();
        }
         
        if((width < 0) || (height < 0) || (x_src < 0) || (y_src < 0) ||
           ((x_src + width) > maxWidth) || ((y_src + height) > maxHeight)) {
            throw new IllegalArgumentException();
        }

        // TODO perform copyArea
    }

    /**
     * Fills the specified triangle will the current color.  The lines
     * connecting each pair of points are included in the filled
     * triangle.
     *
     * @param x1 the x coordinate of the first vertex of the triangle
     * @param y1 the y coordinate of the first vertex of the triangle
     * @param x2 the x coordinate of the second vertex of the triangle
     * @param y2 the y coordinate of the second vertex of the triangle
     * @param x3 the x coordinate of the third vertex of the triangle
     * @param y3 the y coordinate of the third vertex of the triangle
     *
     */
    public void fillTriangle(int x1, int y1, 
                             int x2, int y2,
                             int x3, int y3) {
        int[] xPoints = new int[]{x1 + transX, x2 + transX, x3 + transX};
        int[] yPoints = new int[]{y1 + transY, y2 + transY, y3 + transY};
 
        gciShapeRenderer.fillPolygon(xPoints, yPoints, 3);
    }
    

    /**
     * Renders a series of device-independent RGB+transparency values in a
     * specified region.  The values are stored in
     * <code>rgbData</code> in a format
     * with <code>24</code> bits of RGB and an eight-bit alpha value
     * (<code>0xAARRGGBB</code>),
     * with the first value stored at the specified offset.  The
     * <code>scanlength</code>
     * specifies the relative offset within the array between the
     * corresponding pixels of consecutive rows.  Any value for
     * <code>scanlength</code> is acceptable (even negative values)
     * provided that all resulting references are within the
     * bounds of the <code>rgbData</code> array. The ARGB data is
     * rasterized horizontally from left to right within each row.
     * The ARGB values are
     * rendered in the region specified by <code>x</code>,
     * <code>y</code>, <code>width</code> and <code>height</code>, and
     * the operation is subject to the current clip region
     * and translation for this <code>Graphics</code> object.
     *
     * <p>Consider <code>P(a,b)</code> to be the value of the pixel
     * located at column <code>a</code> and row <code>b</code> of the
     * Image, where rows and columns are numbered downward from the
     * top starting at zero, and columns are numbered rightward from
     * the left starting at zero. This operation can then be defined
     * as:</p>
     *
     * <TABLE BORDER="2">
     * <TR>
     * <TD ROWSPAN="1" COLSPAN="1">
     *    <pre><code>
     *    P(a, b) = rgbData[offset + (a - x) + (b - y) * scanlength]
     *         </code></pre>
     * </TD>
     * </TR>
     * </TABLE>
     *
     * <p> for </p>
     *
     * <TABLE BORDER="2">
     * <TR>
     * <TD ROWSPAN="1" COLSPAN="1">
     *    <pre><code>
     *     x &lt;= a &lt; x + width
     *     y &lt;= b &lt; y + height    </code></pre>
     * </TD>
     * </TR>
     * </TABLE>
     * <p> This capability is provided in the <code>Graphics</code>
     * class so that it can be
     * used to render both to the screen and to offscreen
     * <code>Image</code> objects.  The
     * ability to retrieve ARGB values is provided by the {@link Image#getRGB}
     * method. </p>
     *
     * <p> If <code>processAlpha</code> is <code>true</code>, the
     * high-order byte of the ARGB format
     * specifies opacity; that is, <code>0x00RRGGBB</code> specifies a
     * fully transparent
     * pixel and <code>0xFFRRGGBB</code> specifies a fully opaque
     * pixel.  Intermediate
     * alpha values specify semitransparency.  If the implementation does not
     * support alpha blending for image rendering operations, it must remove
     * any semitransparency from the source data prior to performing any
     * rendering.  (See <a href="Image.html#alpha">Alpha Processing</a> for
     * further discussion.)
     * If <code>processAlpha</code> is <code>false</code>, the alpha
     * values are ignored and all pixels
     * must be treated as completely opaque.</p>
     *
     * <p> The mapping from ARGB values to the device-dependent
     * pixels is platform-specific and may require significant
     * computation.</p>
     *
     * @param rgbData an array of ARGB values in the format
     * <code>0xAARRGGBB</code>
     * @param offset the array index of the first ARGB value
     * @param scanlength the relative array offset between the
     * corresponding pixels in consecutive rows in the
     * <code>rgbData</code> array
     * @param x the horizontal location of the region to be rendered
     * @param y the vertical location of the region to be rendered
     * @param width the width of the region to be rendered
     * @param height the height of the region to be rendered
     * @param processAlpha <code>true</code> if <code>rgbData</code>
     * has an alpha channel,
     * false if all pixels are fully opaque
     *
     * @throws ArrayIndexOutOfBoundsException if the requested operation
     * will attempt to access an element of <code>rgbData</code>
     * whose index is either negative or beyond its length
     * @throws NullPointerException if <code>rgbData</code> is <code>null</code>
     *
     */
    public void drawRGB(int[] rgbData, int offset, int scanlength,
                        int x, int y, int width, int height,
                        boolean processAlpha) {
        // TODO - draw Implementation
    }

    /**
     * Gets the color that will be displayed if the specified color
     * is requested. This method enables the developer to check the
     * manner in which RGB values are mapped to the set of distinct 
     * colors that the device can actually display. For example, 
     * with a monochrome device, this method will return either
     * <code>0xFFFFFF</code> (white) or <code>0x000000</code> (black) 
     * depending on the brightness of the specified color.
     *
     * @param color the desired color (in <code>0x00RRGGBB</code>
     * format, the high-order
     * byte is ignored)
     * @return the corresponding color that will be displayed on the device's
     * screen (in <code>0x00RRGGBB</code> format)
     *
     */
    public int getDisplayColor(int color) {
        // TODO add implementation
        return color;
    }


    // private implementation //

    /**
     * The clip values are in the translated
     * coordinate system and maintained as intersected
     * with 0,0,maxWidth,maxHeight
     */
    private short clipX1, clipY1, clipX2, clipY2;

    /** Translated x,y coordinates */
    private int transX, transY;
    /** 
     * System clip to hold any system clipping.
     * This saves the clip values imposed by
     * the system (e.g. from Theme support).
     * The clip values are in the translated
     * coordinate system.
     */
    private short systemClipX1, systemClipY1, systemClipX2, systemClipY2;
    /** used by dsPaint for saving translated values */ 
    private int ax, ay;
    /** A flag indicating the clipping state */
    private boolean clipped = false;
    /** Pixel values  (-1 when uninitialized) */
    private int rgbColor, gray;
    /** pixel value (-1 when uninitialized) */
    private int pixel;
    /** Line stroke style */
    private int style;
    /** The current Font */
    private Font currentFont;
    /** The maximum width and height */
    private short maxWidth, maxHeight;
    /** A flag indicating MIDP runtime library clip state */
    private boolean runtimeClipEnforce = false;

    /** 
     * The display ID associated with the device Graphics.
     * Note: that Graphics created with an Image will have display ID set
     * to -1. For device Graphics it will be set in 
     * <code> getScreenGraphics() </code>. 
     */
    private int displayId = -1;

    /**
     * If not null, this instance represent Graphics context for that
     * Image.
     */
    private Image img;


    /** GCITextRenderer associated with this Graphics instance */
    private GCITextRenderer gciTextRenderer;

    /** GCIShapeRenderer associated with this Graphics instance */
    private GCIShapeRenderer gciShapeRenderer;

    /** GCIImageRenderer associated with this Graphics instance */
    private GCIImageRenderer gciImageRenderer;

    /** GCIDrawingSurface associated with this Graphics instance */
    private GCIDrawingSurface gciDrawingSurface;

    /** GCIRenderContext associated with this Graphics instance */
    private GCIRenderContext gciGraphicsContext;

    /**
     * Retrieve the Graphics context for the given Image
     *
     * @param img The Image to get a Graphics context for
     * @return Graphics Will return a new ImageGraphics object if
     *                  the Image is non-null.
     */
    static Graphics getImageGraphics(Image img) {
        if (null == img) {
            throw new NullPointerException();
        }

        Graphics g = new Graphics(img.getImageData().gciDrawingSurface);
        g.img = img;
        g.setDimensions(img.getWidth(), img.getHeight());
        g.reset();

        // construct and return a new ImageGraphics
        // object that uses the Image img as the 
        // destination.
        return g;
    }

    /**
     * Retrieve the Graphics context for the given Image and
     * explicitly set the dimensions of the created context.
     *
     * It is possible the Image is bigger than area the Graphics
     * context should provide access to, e.g. off-screen buffer
     * created for full screen mode, but used for both normal and
     * full modes with no resizing.
     *
     * @param img The Image to get a Graphics context for
     * @param width The width of the Graphics context
     * @param height The height of the Graphics context
     * @return Graphics Will return a new ImageGraphics object if
     *                  the Image is non-null.
     */
    static Graphics getImageGraphics(Image img, int width, int height) {
        if (null == img) {
            throw new NullPointerException();
        }

        int w = img.getWidth();
        int h = img.getHeight();
        if (w > width) { w = width; }
        if (h > height) { h = height; }

        Graphics g = new Graphics(img.getImageData().gciDrawingSurface);
        g.img = img;
        g.setDimensions(w, h);
        g.reset();
        return g;
    }

    /**
     * Retrieve the Graphics context that renders to the 
     * device's display
     *
     * @param displayId The graphics object will be associated
     *        with Display with that displayId
     * @param width The width of the Graphics context
     * @param height The height of the Graphics context.
     * @return Graphics 
     */
    static Graphics getScreenGraphics(int displayId, int width, int height,
                                      GCIDrawingSurface gciDrawingSurface) {

        Graphics g = new Graphics(gciDrawingSurface);
        g.displayId = displayId;

        g.img = null;
        g.setDimensions(width, height);
        g.reset();

        return g;
    }

    /**
     * Determines if this a <code>Graphics</code> object used to 
     * represent the device. 
     * @return true if this Graphics represents the device;
     *         false - otherwise
     */
    boolean isScreenGraphics() {
        return (displayId != -1);
    }

    /**
     * Reset this Graphics context with the given coordinates
     *
     * @param x1 The upper left x coordinate
     * @param y1 The upper left y coordinate
     * @param x2 The lower right x coordinate
     * @param y2 The lower right y coordinate
     */
    void reset(int x1, int y1, int x2, int y2) {
        resetGC();
        transX = transY = 0;
        setClip(x1, y1, x2 - x1, y2 - y1);
    }

    /**
     * Reset this Graphics context to its default dimensions
     * (same as reset(0, 0, maxWidth, maxHeight)
     */
    void reset() {
        reset(0, 0, maxWidth, maxHeight);
    }

    /**
     * Reset the Graphic context with all items related
     * to machine independent context. 
     * There is no translation and clipping involve 
     * since different implementations may map it
     * directly or not.
     * Only Font, Style, and Color are reset in
     * this function.
     */
    void resetGC() {
        currentFont = Font.getDefaultFont();
        style       = SOLID;

        rgbColor    = gray = 0;
        pixel       = getPixel(rgbColor, gray, true);

        int attr = GCIRenderContext.ATTRIBUTE_PAINT |
                   GCIRenderContext.ATTRIBUTE_FONT |
                   GCIRenderContext.ATTRIBUTE_STROKE;
        gciImageRenderer.attributesModified(attr);
        gciTextRenderer.attributesModified(attr);
        gciShapeRenderer.attributesModified(attr);
    }


    /**
     * Preserve MIDP runtime GC.
     * - Our internal MIDP clip to protect 
     * it from MIDlet drawing on top of our widget.
     * - Translation
     *
     * @param systemX The system upper left x coordinate
     * @param systemY The system upper left y coordinate
     * @param systemW The system width of the rectangle
     * @param systemH The system height of the rectangle
     */
    void preserveMIDPRuntimeGC(int systemX, int systemY, int systemW,
                               int systemH) {
        runtimeClipEnforce = true;
        clipRect(systemX, systemY, systemW, systemH);

        // this is the first time, we setup 
        // the systemClip values.
        systemClipX1 = clipX1;
        systemClipY1 = clipY1;
        systemClipX2 = clipX2;
        systemClipY2 = clipY2;

        // Preserve the translation system
        translate(systemX, systemY);
        ax = getTranslateX();
        ay = getTranslateY();
    }

    /**
     * Restore the runtime GC.
     * - Release the internal runtime clip values by
     * unsetting the variable.
     * - Restore the original translation
     */
    void restoreMIDPRuntimeGC() {
        runtimeClipEnforce = false;
        translate(ax-getTranslateX(), ay-getTranslateY());
    }


    /**
     * Renders provided Image onto this Graphics object.
     *
     * @param img the Image to be rendered
     * @param x the x coordinate of the anchor point
     * @param y the y coordinate of the anchor point
     * @param anchor the anchor point for positioning the image
     * @return false if <code>anchor</code> is not a legal value
     *
     * @see Image
     */
    boolean render(Image img, int x, int y, int anchor) {

        // TODO anchor

        ImageData imgData = img.getImageData();
        if (imgData.gciMaskDrawingSurface == null) {
	    gciImageRenderer.drawImage(imgData.gciDrawingSurface,
				       0, 0, 
				       imgData.getWidth(), 
				       imgData.getHeight(),
				       x, y);
	 
	} else {
	    gciImageRenderer.maskBlit(imgData.gciDrawingSurface,
				      imgData.gciMaskDrawingSurface,
				      0, 0, 
				      imgData.getWidth(), 
				      imgData.getHeight(), 0, 0,
				      x, y);
	}
	return true;
    }

    /**
     * Renders the specified region of the provided Image object
     * onto this Graphics object.
     *
     * @param img  the Image object to be rendered
     * @param x_src the x coordinate of the upper left corner of the region
     * within the source image to copy
     * @param y_src the y coordinate of the upper left corner of the region
     * within the source image to copy
     * @param width the width of the region to copy
     * @param height the height of the region to copy
     * @param transform the desired transformation for the selected region
     * being copied
     * @param x_dest the x coordinate of the anchor point in the
     * destination drawing area
     * @param y_dest the y coordinate of the anchor point in the
     * destination drawing area
     * @param anchor the anchor point for positioning the region within
     * the destination image
     *
     * @return false if <code>src</code> is the same image as the
     * destination of this <code>Graphics</code> object,
     * or <code>transform</code> is invalid,
     * or <code>anchor</code> is invalid,
     * or the region to be copied exceeds the bounds of the source image.
     *
     * @see Image
     */
    boolean renderRegion(Image img,
			 int x_src, int y_src,
			 int width, int height,
			 int transform,
			 int x_dest, int y_dest,
			 int anchor) {
        // TODO anchor & transform

        ImageData imgData = img.getImageData();

	if (imgData.gciMaskDrawingSurface == null) {
	    gciImageRenderer.drawImage(imgData.gciDrawingSurface,
				       x_src, y_src, width, height, 
				       x_dest, y_dest);
	} else {
	    gciImageRenderer.maskBlit(imgData.gciDrawingSurface,
				      imgData.gciMaskDrawingSurface,
				      x_src, y_src, width, height, 
				      x_src, y_src,
				      y_dest, x_dest);
	}

	return true;
    }

    /**
     * Get a gray value given the RGB values
     *
     * @param red The Red pixel value
     * @param green The Green pixel value
     * @param blue The Blue pixel value
     * @return int The grayscale value corresponding to the RGB color
     */
    private static int grayVal(int red, int green, int blue) {
        /* CCIR Rec 601 luma (nonlinear rgb to nonlinear "gray") */
        return (red*76 + green*150 + blue*29) >> 8;
    }

    /**
     * Get a specific pixel value
     *
     * @param rgb
     * @param gray
     * @param isGray
     * @return int
     */
    private int getPixel(int rgb, int gray, boolean isGray) {
        // TODO -add impl
        return rgb;
    }

    /**
     * Returns the maximal width available for the clipping
     * withing this Graphics context
     * @return The width of the Graphics context
     */
    short getMaxWidth() {
        return maxWidth;
    }

    /**
     * Returns the maximal height available for the clipping
     * withing this Graphics context
     * @return The height of the Graphics context
     */
    short getMaxHeight() {
        return maxHeight;
    }

    /**
     * Checks that the anchor is set correctly.
     *
     * @param anchor The anchor type. 
     * The definition of the anchor point must be one of
     * the horizontal constants (LEFT, HCENTER, RIGHT)  combined with one of
     * the vertical constants (TOP, BASELINE, BOTTOM, VCENTER) 
     * using the bit-wise OR operator.
     * Zero may also be used as the value of an anchor point.
     * Using zero for the anchor point value gives results 
     * identical to using TOP | LEFT.
     *
     * @param illegal_vpos The component from list of constants 
     * (LEFT, HCENTER, RIGHT, TOP, BASELINE, BOTTOM, VCENTER) 
     * that is not allowed for this anchor. 
     * Using zero for illegal_vpos shows all
     * values from this list are allowed
     * @return treu if anchor is valid, otherwise - false.
     *
     */
    boolean checkAnchor(int anchor, int illegal_vpos) {

        /* optimize for most frequent case */
        if (anchor == (TOP|LEFT) || anchor == 0) {
            return true;
        }

        boolean result  = 
            (anchor > 0) && (anchor < (BASELINE << 1))
             && ((anchor & illegal_vpos) == 0);

        if (result) {
            int n = anchor & (TOP | BOTTOM | BASELINE | VCENTER);
            /* exactly one bit set */
            result = (n != 0) && ((n & (n - 1)) == 0); 
        }

        if (result) {
            int n = anchor & (LEFT | RIGHT | HCENTER);
            /* exactly one bit set */
            result = (n != 0) && ((n & (n - 1)) == 0);
        }

        return result;
    }


    /**
     * Normalizes anchor coordinates to top-left coordinates.
     *
     * @return true if anchor is valid, otherwise - false.
     */
    boolean normalizeAnchor(int[] location, int width, int height, 
                            int anchor) {
        /* optimize for most frequent case*/
        if (anchor == (TOP | LEFT) || anchor == 0) {
            return true;
        }

        if ((anchor & 0x7F) != anchor) {
            return false;
        }

        switch (anchor & (LEFT | RIGHT | HCENTER)) {
        case LEFT:
            break;
            
        case RIGHT:
            location[0] -= width;
            break;
            
        case HCENTER:
            location[0] -= (width >> 1);
            break;
    
        default:
            return false;
        }
    
        switch (anchor & (TOP | BOTTOM | VCENTER)) {
        case TOP:
            break;
            
        case BOTTOM:
            location[1] -= height;
            break;
      
        case VCENTER:
            location[1] -= (height >> 1);
            break;
    
        default:
            return false;
        }
        
        return true;
    }

    /**
     * Creates GCI Renderers based on the passed in GCIDrawingSurface
     * @param gciDrawingSurface GCIDrawingSurface for which the renderer
     *        should be created
     */
    private void createRenderers(GCIDrawingSurface gciDrawingSurface,
                                 GCIRenderContext gciGraphicsContext) {
        gciTextRenderer = 
            (GCITextRenderer) gciDrawingSurface.createTextRenderer();
        gciTextRenderer.contextCreated(gciGraphicsContext);
        gciShapeRenderer = 
            (GCIShapeRenderer)gciDrawingSurface.createPBPShapeRenderer();
        gciShapeRenderer.contextCreated(gciGraphicsContext);
        gciImageRenderer = 
            (GCIImageRenderer)gciDrawingSurface.createImageRenderer();
        gciImageRenderer.contextCreated(gciGraphicsContext);
    }


    /**
     * <code>GCIRenderContext</code> contains the attributes used for
     * rendereing. The render context is passed to all rendering operations.
     * 
     * @version 1.9 10/24/06 18:22:24
     */
    class GCIGraphicsContext implements GCIRenderContext {

        /**
         * Returns the drawing surface associated with the context. The 
         * return value is non-null.
         */
        public GCIDrawingSurface getDrawingSurface() {
            return gciDrawingSurface;
        }

        /**
         * Returns if anti-aliasing is turned on or off.
         *
         * @return <code>true</code> if anti-aliasing is ON and <code>false</code>
         *         if anti-aliasing is off
         */
        public boolean isAntiAliasingOn() {
            //TODO check
            return false;
        }

        /** 
         * Returns value for interpolation quality.
         * @return an <code>int</code> containing the current interpolation
         * quality.
         *
         * @see #INTERP_QUALITY_NEAREST
         * @see #INTERP_QUALITY_BILINEAR
         * @see #INTERP_QUALITY_BICUBIC
         */
        public int getInterpQuality() {
            // TODO  check
            return GCIRenderContext.INTERP_QUALITY_NEAREST;
        }
    
        /**
         * Returns the current clip region in the form <code>x</code>,
         * <code>y</code>, <code>width</code>, <code>height</code>.
         *
         * @param clipBounds an array of 4 <code>int</code>s to be filled
         * in with the clip information in the form of the
         * <code>x</code>, <code>y</code>, <code>width</code>, and
         * <code>height</code> values of the clip region, respectively.
         * @return a reference to the <code>clipBounds</code> array.
         *
         * @throws IllegalArgumentException if <code>clipBounds</code> is
         * <code>null</code> or has length not equal to 4.
         */
        public int[] getClip(int[] clipBounds) {
            if (clipBounds.length != 4) {
                throw new IllegalArgumentException();
            }
            clipBounds[0] = clipX1;
            clipBounds[1] = clipY1;
            clipBounds[2] = clipX2 - clipX1;
            clipBounds[3] = clipY2 - clipY1;

            return clipBounds;
        }

        /**
         * Returns the current color as an ARGB value packed into an
         * <code>int</code>.
         *
         * @return the current drawing color packed into an
         * <code>int</code> as an <code>ARGB_8888</code> value.
         */
        public int getPaintColor() {
            return (0xff000000 | rgbColor);
        }
    
        /**
         * Returns the current alternating color used for XOR drawing.
         *
         * @return an <code>int</code> containing a color in
         * <code>ARGB_8888</code> format.
         */
        public int getXORColor() {
            // TODO - correct
            return (0xff000000);
        }
    
        /**
         * Returns the current composite rule, which will be one of
         * <code>COMPOSITE_SRC</code> or <code>COMPOSITE_SRC_OVER</code>.
         *
         * @return an <code>int</code> containing the current compositing
         * rule.
         *
         * @see #COMPOSITE_CLEAR
         * @see #COMPOSITE_SRC_OVER
         * @see #COMPOSITE_SRC
         */
        public int getCompositeRule(){
            return GCIRenderContext.COMPOSITE_SRC_OVER;
        }

        /**
         * Returns the current alpha value to be used for compositing, as
         * a <code>float</code> between <code>0</code> and <code>1</code>.
         *
         * @return the alpha value as a <code>float</code>.
         */
        public float getCompositeAlpha() {
            return 1;
        }

        /**
         * Returns the current paint mode setting, one of
         * <code>PAINT_MODE_COLOR</code> or <code>PAINT_MODE_XOR</code>.
         * The initial value is <code>PAINT_MODE_COLOR</code>.
         *
         * @return an <code>int</code> equal to either
         * <code>PAINT_MODE_*</code>.
         *
         * @see #PAINT_MODE_COLOR
         * @see #PAINT_MODE_XOR
         * @see #PAINT_MODE_LINEAR_GRADIENT
         */
        public int getPaintMode() {
            return GCIRenderContext.PAINT_MODE_COLOR;
        }
    
        /**
         * Returns the current line width.  The initial value is
         * <code>1.0F</code>.
         *
         * @return a <code>float</code> containing the current line width.
         */
        public float getLineWidth() {
            return 1;
        }
        
        /**
         * Returns the current miter limit.  The initial value is
         * <code>10.0f</code> (ten).
         *
         * @return a <code>float</code> containing the current miter
         * limit.
         */
        public float getMiterLimit() {
            return 10;
        }

        /**
         * Returns the current join style.  The initial value is
         * <code>JOIN_MITER</code>.
         *
         * @return an <code>int</code> containing the current line join
         * style.
         */
        public int getJoinStyle() {
            return GCIRenderContext.JOIN_MITER;
        }
    
        /**
         * Returns the current line cap style.  The initial value is
         * <code>CAP_BUTT</code>.
         *
         * @return an <code>int</code> containing the current line cap
         * style.
         */
        public int getCapStyle() {
            return GCIRenderContext.CAP_BUTT;
        }
    
        /**
         * Returns the current line dash pattern as an array of
         * <code>float</code>s, or <code>null</code> if dashing is not
         * enabled.  The initial value is <code>null</code>.
         *
         * @return an array of <code>float</code>s containing the current
         * dash pattern, or <code>null</code> if dashing is not enabled.
         *
         * @see #setDashPattern(float[], float)
         */
        public float[] getDashPattern() {
            // TODO enable dashing
            return null;
        }
    
        /**
         * Returns the current dash phase.  The initial value is
         * <code>0.0f</code>.
         *
         * @return a <code>float</code> containing the current dash phase.
         *
         * @see #setDashPattern(float[], float)
         */
        public float getDashPhase() {
            return 0;
        }
    
        /**
         * Returns the current affine transform in a caller-supplied array
         * of <code>float</code>s.
         *
         * @param transform an array of 6 <code>float</code>s to be filled
         * in.
         * @return a reference to the <code>transform</code> parameter.
         *
         * @throws IllegalArgumentException if <code>transform</code> is
         * <code>null</code> or &nbsp; <code>transform.length != 6</code>.
         */
        public float[] getTransformMatrix(float[] transform) {
            if (transform == null || transform.length != 6)
                throw new IllegalArgumentException(
                 "Transform must be non-null and have a length equal to 6");
            transform[0] = 0;
            transform[1] = 0;
            transform[2] = 0;
            transform[3] = 0;
            transform[4] = transX;
            transform[5] = transY;
            return transform;
        }

        /**
         * Returns the <code>x0</code> coordinate of the current linear
         * gradient, or <code>0</code> if no gradient is set.
         *
         * @return the gradient <code>x0</code> coordinate as a
         * <code>float</code>, or <code>0</code>.
         */
        public float getLinearGradientX0() {
            return 0;
        }
        
        /**
         * Returns the <code>y0</code> coordinate of the current linear
         * gradient, or <code>0</code> if no gradient is set.
         *
         * @return the gradient <code>y0</code> coordinate as a
         * <code>float</code>, or <code>0</code>.
         */
        public float getLinearGradientY0() {
            return 0;
        }

        /**
         * Returns the <code>argb0</code> color of the current linear
         * gradient, or <code>0</code> if no gradient is set.
         *
         * @return the gradient <code>argb0</code> color as an
         * <code>int</code>, or <code>0</code>.
         */
        public float getLinearGradientARGB0() {
            return 0;
        }


        /**
         * Returns the <code>x1</code> coordinate of the current linear
         * gradient, or <code>0</code> if no gradient is set.
         *
         * @return the gradient <code>x1</code> coordinate as a
         * <code>float</code>, or <code>0</code>.
         */
        public float getLinearGradientX1() {
            return 0;
        }
        
        /**
         * Returns the <code>y1</code> coordinate of the current linear
         * gradient, or <code>0</code> if no gradient is set.
         *
         * @return the gradient <code>y1</code> coordinate as a
         * <code>float</code>, or <code>0</code>.
         */
        public float getLinearGradientY1() {
            return 0;
        }

        /**
         * Returns the <code>argb1</code> color of the current linear
         * gradient, or <code>0</code> if no gradient is set.
         *
         * @return the gradient <code>argb1</code> color as an
         * <code>int</code>, or <code>0</code>.
         */
        public float getLinearGradientARGB1() {
            return 0;
        }
        
        /**
         * Returns <code>true</code> if there is a linear gradient current
         * set, and it is cyclic, and <code>false</code> otherwise.
         *
         * @return <code>true</code> if a cyclic linear gradient is set.
         */
        public boolean getLinearGradientIsCyclic() {
            return false;
        }

        /**
         * Returns the current value of the flatness hint.  This value may
         * or may not represent the actual level of curve fidelity that
         * will be produced by the renderer.  The initial value is ???.
         *
         * @return a <code>float</code> indicating the current value of
         * the flatness hint.
         *
         * @see #setFlatnessHint(float)
         */
        public float getFlatnessHint() {
            // TODO what should be returned
            return 0;
        }
    
        /**
         * Returns the font object. 
         */
        public GCIFont getGCIFont() {
            Font f = (currentFont == null ? 
                      Font.getDefaultFont() :
                      currentFont); 
            return f.gciFont;
        }
    }

    /**
     * The creator of this Graphics instance
     *
     * IMPL_NOTE: The information about Graphics object creator is
     *   needed to JSRs (e.g. JSR239) that are given with Graphics
     *   instance and has no further information on its creator changes,
     *   e.g. of resizing, but at the same time should be able to paint
     *   properly into this Graphics.
     */
    private Object creator = null;

    /**
     * Returns the creator of this Graphics object
     * @return Graphics creator reference
     */
    synchronized Object getCreator() {
        return creator;
    }

    /**
     * Sets the creator of this Graphics object
     * @param creator the reference to creator of this Graphics object
     */
    synchronized void setCreator(Object creator) {
        // Ignore repeated attempts to set creator
        if (this.creator == null) {
            this.creator = creator;
        }
    }
    
} // class Graphics
