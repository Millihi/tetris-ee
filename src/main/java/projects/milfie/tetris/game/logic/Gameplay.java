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

package projects.milfie.tetris.game.logic;

import projects.milfie.tetris.game.domain.*;

import java.util.Random;

public final class Gameplay {
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public Gameplay (final int width, final int height) {
      this.gamestate = new Gamestate ();
      this.random = new Random ();
      this.canvas = new Canvas (width, height);
      this.brick = Brick.create (this.random);
      this.test = new Brick (this.brick);
   }

   public void setComputer (final Computer computer) {
      if (computer == null) {
         throw new IllegalArgumentException ("Given computer is null.");
      }
      this.computer = computer;
   }

   public Gamestate getGamestate () {
      return gamestate;
   }

   public void setWidth (final int width) {
      canvas.setWidth (width);
   }

   public void setHeight (final int height) {
      canvas.setHeight (height);
   }

   public void reset () {
      gamestate.reset ();
      computer.reset ();
      canvas.clear ();
      nextBrick ();
      test.set (brick);
   }

   public void update () {
      if (gamestate.getLevel ().isChanged ()) {
         computer.adjust (gamestate.getLevel ());
      }
   }

   public void moveLeft () {
      test.set (brick).setX (brick.getX () - 1);

      if (canMove (test, canvas)) {
         swapBricks ();
         partialUpdate
            (Bound.LEFT.of (brick), Bound.BOTTOM.of (brick),
             Bound.RIGHT.of (test), Bound.TOP.of (test));
      }
   }

   public void moveRight () {
      test.set (brick).setX (brick.getX () + 1);

      if (canMove (test, canvas)) {
         swapBricks ();
         partialUpdate
            (Bound.LEFT.of (test), Bound.BOTTOM.of (test),
             Bound.RIGHT.of (brick), Bound.TOP.of (brick));
      }
   }

   public void moveDown () {
      test.set (brick).setY (brick.getY () - 1);

      if (canMove (test, canvas)) {
         swapBricks ();
         partialUpdate
            (Bound.LEFT.of (brick), Bound.BOTTOM.of (brick),
             Bound.RIGHT.of (test), Bound.TOP.of (test));
      }
      else {
         putBrick ();
      }
   }

   public void putDown () {
      test.set (brick).setY (brick.getY () - 1);

      while (canMove (test, canvas)) {
         swapBricks ();
         test.set (brick).setY (brick.getY () - 1);
      }

      putBrick ();
   }

   public void rotateClockwise () {
      rotateInternal (Rotation.CLOCKWISE);
   }

   public void rotateCounterClockwise () {
      rotateInternal (Rotation.COUNTERCLOCKWISE);
   }

   public void requestFullUpdate () {
      fullUpdate ();
   }

   public byte[][] getUpdateData () {
      final Rect region = gamestate.getUpdateRect ();

      if (!region.isValid ()) {
         throw new IllegalStateException ("No update data given.");
      }

      return getRegion
         (brick, canvas,
          Bound.LEFT.of (region), Bound.BOTTOM.of (region),
          Bound.RIGHT.of (region), Bound.TOP.of (region));
   }

   public byte[][] getBrickData () {
      return getRegion (brick);
   }

   public byte[][] getCanvasData () {
      return getRegion (canvas);
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private final Random    random;
   private final Canvas    canvas;
   private final Gamestate gamestate;

   private Computer computer;
   private Brick    brick;
   private Brick    test;

   private void putBrick () {
      writeBrick (brick, canvas);
      final int removedRows = removeRows (brick, canvas);
      test.set (brick);
      nextBrick ();
      fullUpdate ();
      gamestate.addRemovedRows (removedRows);
      gamestate.setGameover (Bound.TOP.of (test) >= canvas.getHeight ());
   }

   private void nextBrick () {
      computer.reset ();
      brick
         .set (random)
         .setX ((canvas.getWidth () - brick.getWidth ()) / 2)
         .setY (canvas.getHeight ());
      gamestate.setNewBrick (true);
   }

   private void rotateInternal (final Rotation rotation) {
      rotation.of (test.set (brick));

      if (Bound.RIGHT.of (test) > Bound.RIGHT.of (canvas)) {
         test.setX (canvas.getWidth () - test.getWidth ());
      }
      if (Bound.LEFT.of (test) < Bound.LEFT.of (canvas)) {
         test.setX (Bound.LEFT.of (canvas));
      }

      if (!hasCollisions (test, canvas)) {
         swapBricks ();
         partialUpdate
            (Bound.LEFT.minOf (test, brick), Bound.BOTTOM.minOf (test, brick),
             Bound.RIGHT.maxOf (test, brick), Bound.TOP.maxOf (test, brick));
         gamestate.setNewBrick (true);
      }
   }

   private void swapBricks () {
      final Brick temp = brick;
      brick = test;
      test = temp;
   }

   private void partialUpdate (final int x0, final int y0,
                               final int x1, final int y1)
   {
      final int
         xl = Math.max (x0, Bound.LEFT.of (canvas)),
         yl = Math.max (y0, Bound.BOTTOM.of (canvas)),
         xh = Math.min (x1, Bound.RIGHT.of (canvas)),
         yh = Math.min (y1, Bound.TOP.of (canvas));

      if (xh >= xl && yh >= yl) {
         final Rect updateRegion = gamestate.getUpdateRect ();
         updateRegion
            .setLowerCorner
               (Math.min (xl, Bound.LEFT.of (updateRegion)),
                Math.min (yl, Bound.BOTTOM.of (updateRegion)))
            .setUpperCorner
               (Math.max (xh, Bound.RIGHT.of (updateRegion)),
                Math.max (yh, Bound.TOP.of (updateRegion)));
      }
   }

   private void fullUpdate () {
      gamestate
         .getUpdateRect ()
         .setLowerCorner (Bound.LEFT.of (canvas), Bound.BOTTOM.of (canvas))
         .setUpperCorner (Bound.RIGHT.of (canvas), Bound.TOP.of (canvas));
      gamestate.getScore ().mark ();
      gamestate.getLevel ().mark ();
      gamestate.setNewBrick (true);
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static boolean canMove (final Brick brick,
                                   final Canvas canvas)
   {
      return
         (Bound.LEFT.of (brick) >= Bound.LEFT.of (canvas) &&
          Bound.BOTTOM.of (brick) >= Bound.BOTTOM.of (canvas) &&
          Bound.RIGHT.of (brick) <= Bound.RIGHT.of (canvas) &&
          !hasCollisions (brick, canvas));
   }

   private static boolean hasCollisions (final Brick brick,
                                         final Canvas canvas)
   {
      final int
         x0 = Bound.LEFT.maxOf (canvas, brick),
         y0 = Bound.BOTTOM.maxOf (canvas, brick),
         x1 = Bound.RIGHT.minOf (canvas, brick),
         y1 = Bound.TOP.minOf (canvas, brick);

      boolean result = false;

      for (int y = y0; y <= y1; ++y) {
         for (int x = x0; x <= x1; ++x) {
            result |= (canvas.read (x, y) != 0) && (brick.read (x, y) != 0);
         }
      }

      return result;
   }

   private static void writeBrick (final Brick brick,
                                   final Canvas canvas)
   {
      final int
         x0 = Bound.LEFT.maxOf (canvas, brick),
         y0 = Bound.BOTTOM.maxOf (canvas, brick),
         x1 = Bound.RIGHT.minOf (canvas, brick),
         y1 = Bound.TOP.minOf (canvas, brick);

      for (int y = y0; y <= y1; ++y) {
         for (int x = x0; x <= x1; ++x) {
            canvas.add (x, y, brick.read (x, y));
         }
      }
   }

   private static int removeRows (final Brick brick,
                                  final Canvas canvas)
   {
      final int
         x0 = Bound.LEFT.of (canvas),
         x1 = Bound.RIGHT.of (canvas),
         yh = Bound.TOP.of (canvas),
         y0 = Bound.BOTTOM.maxOf (canvas, brick),
         y1 = Bound.TOP.minOf (canvas, brick);

      int rows = 0;

      for (int y = y1; y >= y0; --y) {
         boolean isFull = true;

         for (int x = x0; isFull && (x <= x1); ++x) {
            isFull = (canvas.read (x, y) != 0);
         }

         if (isFull) {
            canvas.clearRow (y);

            for (int yf = y; yf < yh; ) {
               canvas.swapRows (yf++, yf);
            }

            ++rows;
         }
      }

      return rows;
   }

   private static byte[][] getRegion (final Brick brick,
                                      final Canvas canvas,
                                      final int x0, final int y0,
                                      final int x1, final int y1)
   {
      final int
         width = x1 - x0 + 1,
         height = y1 - y0 + 1;

      assert (width > 0) : "Calculated width isn't positive.";
      assert (height > 0) : "Calculated height isn't positive.";

      final byte[][] region = new byte[height][width];

      for (int y = y0, _y = 0; y <= y1; ++y, ++_y) {
         for (int x = x0, _x = 0; x <= x1; ++x, ++_x) {
            region[_y][_x] = (byte) (canvas.read (x, y) + brick.read (x, y));
         }
      }

      return region;
   }

   private static byte[][] getRegion (final Brick brick) {
      final int
         x0 = Bound.LEFT.of (brick),
         y0 = Bound.BOTTOM.of (brick),
         x1 = Bound.RIGHT.of (brick),
         y1 = Bound.TOP.of (brick),
         width = x1 - x0 + 1,
         height = y1 - y0 + 1;

      assert (width > 0) : "Calculated width isn't positive.";
      assert (height > 0) : "Calculated height isn't positive.";

      final byte[][] region = new byte[height][width];

      for (int y = y0, _y = 0; y <= y1; ++y, ++_y) {
         for (int x = x0, _x = 0; x <= x1; ++x, ++_x) {
            region[_y][_x] = brick.read (x, y);
         }
      }

      return region;
   }

   private static byte[][] getRegion (final Canvas canvas) {
      final int
         x0 = Bound.LEFT.of (canvas),
         y0 = Bound.BOTTOM.of (canvas),
         x1 = Bound.RIGHT.of (canvas),
         y1 = Bound.TOP.of (canvas),
         width = x1 - x0 + 1,
         height = y1 - y0 + 1;

      assert (width > 0) : "Calculated width isn't positive.";
      assert (height > 0) : "Calculated height isn't positive.";

      final byte[][] region = new byte[height][width];

      for (int y = y0, _y = 0; y <= y1; ++y, ++_y) {
         for (int x = x0, _x = 0; x <= x1; ++x, ++_x) {
            region[_y][_x] = canvas.read (x, y);
         }
      }

      return region;
   }
}
