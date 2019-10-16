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

import java.util.Random;

public final class Brick
   implements Rectable<Brick>, Rotable<Brick>
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public static section                                                 //
   ////////////////////////////////////////////////////////////////////////////

   public static Brick create (final Random random) {
      if (random == null) {
         throw new IllegalArgumentException ("The random is null.");
      }
      return
         new Brick
            (getRandomFigure (random, null),
             getRandomAngle (random, null));
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public Brick (final Figure figure) {
      this (figure, Angle.DEGREE_0);
   }

   public Brick (final Figure figure,
                 final Angle angle)
   {
      if (figure == null) {
         throw new IllegalArgumentException ("The figure is null.");
      }
      if (angle == null) {
         throw new IllegalArgumentException ("The angle is null.");
      }
      this.loadFigure (figure, angle);
   }

   public Brick (final Brick brick) {
      this.set (brick);
   }

   public Brick set (final Brick brick) {
      if (brick == null) {
         throw new IllegalArgumentException ("The brick is null.");
      }
      this.x = brick.x;
      this.y = brick.y;
      this.loadFigure (brick.figure, brick.angle);
      return this;
   }

   public Brick set (final Random random) {
      if (random == null) {
         throw new IllegalArgumentException ("The random is null.");
      }
      this.x = 0;
      this.y = 0;
      this.loadFigure
         (getRandomFigure (random, this.figure),
          getRandomAngle (random, this.angle));
      return this;
   }

   public Brick setCoords (final int x, final int y) {
      return this.setX (x).setY (y);
   }

   public int getX () {
      return x;
   }

   public Brick setX (final int x) {
      assert (x >= 0) : "The x coordinate is negative.";
      this.x = x;
      return this;
   }

   public int getY () {
      return y;
   }

   public Brick setY (final int y) {
      assert (y >= 0) : "The y coordinate is negative.";
      this.y = y;
      return this;
   }

   public Angle getAngle () {
      return angle;
   }

   public Brick setAngle (final Angle angle) {
      loadFigure (figure, angle);
      return this;
   }

   public Figure getFigure () {
      return figure;
   }

   public Brick setFigure (final Figure figure) {
      loadFigure (figure, angle);
      return this;
   }

   public int getWidth () {
      return width;
   }

   public int getHeight () {
      return height;
   }

   @Override
   public int getLeftBound () {
      return (x);
   }

   @Override
   public int getBottomBound () {
      return (y);
   }

   @Override
   public int getRightBound () {
      return (x + width - 1);
   }

   @Override
   public int getTopBound () {
      return (y + height - 1);
   }

   @Override
   public Brick rotateClockwise () {
      this.x += (figure.getWidth (angle) - figure.getHeight (angle)) / 2;
      this.y += (figure.getHeight (angle) - figure.getWidth (angle)) / 2;
      loadFigure (figure, angle.next ());
      return this;
   }

   @Override
   public Brick rotateCounterClockwise () {
      this.x += (figure.getWidth (angle) - figure.getHeight (angle)) / 2;
      this.y += (figure.getHeight (angle) - figure.getWidth (angle)) / 2;
      loadFigure (figure, angle.prev ());
      return this;
   }

   public byte read (final int x, final int y) {
      final int _x = x - this.x;
      final int _y = y - this.y;
      if (_x >= 0 && _y >= 0 && _x < width && _y < height) {
         if (figure.getPointValue (angle, _x, _y)) {
            return setValue;
         }
      }
      return Canvas.DEFAULT_VALUE;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private int    x        = 0;
   private int    y        = 0;
   private int    width    = 0;
   private int    height   = 0;
   private Angle  angle    = Angle.DEGREE_0;
   private Figure figure   = null;
   private byte   setValue = Canvas.DEFAULT_VALUE;

   private void loadFigure (final Figure figure,
                            final Angle angle)
   {
      assert (figure != null) : "The figure is null.";
      assert (angle != null) : "The angle is null.";
      this.figure = figure;
      this.angle = angle;
      this.width = figure.getWidth (angle);
      this.height = figure.getHeight (angle);
      this.setValue = (byte) (Canvas.DEFAULT_VALUE + 1 + figure.ordinal ());
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static Figure getRandomFigure (final Random random,
                                          final Figure last)
   {
      final Figure[] figures = Figure.values ();
      Figure next;

      do {
         next = figures[random.nextInt (figures.length)];
      } while (next == last);

      return next;
   }

   private static Angle getRandomAngle (final Random random,
                                        final Angle last)
   {
      final Angle[] angles = Angle.values ();
      Angle next;

      do {
         next = angles[random.nextInt (angles.length)];
      } while (next == last);

      return next;
   }
}
