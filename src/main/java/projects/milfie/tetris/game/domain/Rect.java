/*****************************************************************************
 * "THE CAKE-WARE LICENSE" (Revision 42):                                    *
 *                                                                           *
 *     Milfie <mail@milfie.uu.me> wrote this file. As long as you retain     *
 * this notice you can do whatever you want with this stuff. If we meet      *
 * some day, and you think this stuff is worth it, you must buy me a cake    *
 * in return.                                                                *
 *                                                                           *
 *     Milfie.                                                               *
 *****************************************************************************/

package projects.milfie.tetris.game.domain;

public class Rect
   implements Rectable<Rect>
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public static section                                                 //
   ////////////////////////////////////////////////////////////////////////////

   public static final Rect ZERO = new Rect (0, 0, 0, 0);

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   /**
    * Creates a new invalidated rect.
    */
   public Rect () {
      this.invalidate ();
   }

   /**
    * Creates a new rect from the given one. Ensures that created rect is
    * valid, otherwise throws an {@link IllegalStateException} exception.
    *
    * @param source the source rect, which data will be copied to the new one.
    *
    * @throws java.lang.IllegalStateException if the created rect is invalid.
    */
   public Rect (final Rect source) {
      this.set (source);
   }

   /**
    * Creates a new rect from the given corner's points. Ensures that created
    * rect is valid, otherwise throws an {@link IllegalStateException}
    * exception.
    *
    * @param left   the <i>x</i> coordinate of the left lower corner.
    * @param bottom the <i>y</i> coordinate of the left lower corner.
    * @param right  the <i>x</i> coordinate of the right upper corner.
    * @param top    the <i>y</i> coordinate of the right upper corner.
    *
    * @throws java.lang.IllegalStateException if the created rect is invalid.
    */
   public Rect (final int left, final int bottom,
                final int right, final int top)
   {
      this.set (left, bottom, right, top);
   }

   public Rect set (final Rect source) {
      return
         this.set
            (source.leftBound, source.bottomBound,
             source.rightBound, source.topBound);
   }

   public Rect set (final int left, final int bottom,
                    final int right, final int top)
   {
      this.leftBound = left;
      this.bottomBound = bottom;
      this.rightBound = right;
      this.topBound = top;
      return this.validate ();
   }

   public Rect setLowerCorner (final int left, final int bottom) {
      this.leftBound = left;
      this.bottomBound = bottom;
      return this;
   }

   public Rect setUpperCorner (final int right, final int top) {
      this.rightBound = right;
      this.topBound = top;
      return this;
   }

   @Override
   public int getLeftBound () {
      return leftBound;
   }

   public Rect setLeftBound (final int bound) {
      this.leftBound = bound;
      return this;
   }

   @Override
   public int getBottomBound () {
      return bottomBound;
   }

   public Rect setBottomBound (final int bound) {
      this.bottomBound = bound;
      return this;
   }

   @Override
   public int getRightBound () {
      return rightBound;
   }

   public Rect setRightBound (final int bound) {
      this.rightBound = bound;
      return this;
   }

   @Override
   public int getTopBound () {
      return topBound;
   }

   public Rect setTopBound (final int bound) {
      this.topBound = bound;
      return this;
   }

   public boolean isValid () {
      return
         (leftBound >= 0 && bottomBound >= 0 &&
          rightBound >= 0 && topBound >= 0 &&
          leftBound <= rightBound && bottomBound <= topBound);
   }

   public Rect validate () {
      if (leftBound < 0) {
         throw new IllegalStateException ("Left is negative.");
      }
      if (bottomBound < 0) {
         throw new IllegalStateException ("Bottom is negative.");
      }
      if (rightBound < 0) {
         throw new IllegalStateException ("Right is negative.");
      }
      if (topBound < 0) {
         throw new IllegalStateException ("Top is negative.");
      }
      if (leftBound > rightBound) {
         throw new IllegalStateException ("Left > Right.");
      }
      if (bottomBound > topBound) {
         throw new IllegalStateException ("Bottom > Top.");
      }
      return this;
   }

   public Rect invalidate () {
      this.leftBound = Integer.MAX_VALUE;
      this.bottomBound = Integer.MAX_VALUE;
      this.rightBound = 0;
      this.topBound = 0;
      return this;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private int leftBound   = 0;
   private int bottomBound = 0;
   private int rightBound  = 0;
   private int topBound    = 0;

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////
}
