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

import java.util.Arrays;

public final class Canvas
   implements Rectable<Canvas>
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public static section                                                 //
   ////////////////////////////////////////////////////////////////////////////

   public static final int  DEFAULT_SIZE  = 16;
   public static final byte DEFAULT_VALUE = 0x00;

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public Canvas () {
      this.canvas = new byte[height][width];
      this.clear ();
   }

   public Canvas (final int width, final int height) {
      if (width <= 0) {
         throw new IllegalArgumentException ("The width is not positive.");
      }
      if (height <= 0) {
         throw new IllegalArgumentException ("The height is not positive.");
      }
      this.canvas = new byte[height][width];
      this.setWidth (width);
      this.setHeight (height);
      this.clear ();
   }

   public int getWidth () {
      return width;
   }

   public void setWidth (final int width) {
      assert (width > 0) : "The width is not positive.";
      ensureCapacity (width, this.height);
      this.width = width;
   }

   public int getHeight () {
      return height;
   }

   public void setHeight (final int height) {
      assert (height > 0) : "The height is not positive.";
      ensureCapacity (this.width, height);
      this.height = height;
   }

   @Override
   public int getLeftBound () {
      return 0;
   }

   @Override
   public int getBottomBound () {
      return 0;
   }

   @Override
   public int getRightBound () {
      return (width - 1);
   }

   @Override
   public int getTopBound () {
      return (height - 1);
   }

   public byte read (final int x, final int y) {
      assert (x >= 0 && x < width) : "The x coordinate is out of range.";
      assert (y >= 0 && y < height) : "The y coordinate is out of range.";
      return canvas[y][x];
   }

   public void write (final int x, final int y, final byte value) {
      assert (x >= 0 && y >= 0) : "The coordinate is negative.";
      ensureCapacity (x + 1, y + 1);
      canvas[y][x] = value;
   }

   public byte inc (final int x, final int y) {
      assert (x >= 0 && y >= 0) : "The coordinate is negative.";
      ensureCapacity (x + 1, y + 1);
      canvas[y][x] += 1;
      return canvas[y][x];
   }

   public byte dec (final int x, final int y) {
      assert (x >= 0 && y >= 0) : "The coordinate is negative.";
      ensureCapacity (x + 1, y + 1);
      canvas[y][x] -= 1;
      return canvas[y][x];
   }

   public byte add (final int x, final int y, final byte value) {
      assert (x >= 0 && y >= 0) : "The coordinate is negative.";
      ensureCapacity (x + 1, y + 1);
      canvas[y][x] += value;
      return canvas[y][x];
   }

   public byte sub (final int x, final int y, final byte value) {
      assert (x >= 0 && y >= 0) : "The coordinate is negative.";
      ensureCapacity (x + 1, y + 1);
      canvas[y][x] -= value;
      return canvas[y][x];
   }

   public byte and (final int x, final int y, final byte value) {
      assert (x >= 0 && y >= 0) : "The coordinate is negative.";
      ensureCapacity (x + 1, y + 1);
      canvas[y][x] &= value;
      return canvas[y][x];
   }

   public byte or (final int x, final int y, final byte value) {
      assert (x >= 0 && y >= 0) : "The coordinate is negative.";
      ensureCapacity (x + 1, y + 1);
      canvas[y][x] |= value;
      return canvas[y][x];
   }

   public byte xor (final int x, final int y, final byte value) {
      assert (x >= 0 && y >= 0) : "The coordinate is negative.";
      ensureCapacity (x + 1, y + 1);
      canvas[y][x] ^= value;
      return canvas[y][x];
   }

   public byte not (final int x, final int y) {
      assert (x >= 0 && y >= 0) : "The coordinate is negative.";
      ensureCapacity (x + 1, y + 1);
      canvas[y][x] = (byte) ~(canvas[y][x]);
      return canvas[y][x];
   }

   public void swapColumns (final int x1, final int x2) {
      assert (x1 >= 0 && x1 < width) : "The x1 coordinate is out of range.";
      assert (x2 >= 0 && x2 < width) : "The x2 coordinate is out of range.";
      if (x1 != x2) {
         for (int y = 0; y < height; ++y) {
            final byte canvasYX1;
            canvasYX1 = canvas[y][x1];
            canvas[y][x1] = canvas[y][x2];
            canvas[y][x2] = canvasYX1;
         }
      }
   }

   public void swapRows (final int y1, final int y2) {
      assert (y1 >= 0 && y1 < height) : "The y1 coordinate is out of range.";
      assert (y2 >= 0 && y2 < height) : "The y2 coordinate is out of range.";
      if (y1 != y2) {
         final byte[] canvasY1;
         canvasY1 = canvas[y1];
         canvas[y1] = canvas[y2];
         canvas[y2] = canvasY1;
      }
   }

   public void clearColumn (final int x) {
      assert (x >= 0 && x < width) : "The x coordinate is out of range.";
      for (int y = 0; y < height; ++y) {
         canvas[y][x] = DEFAULT_VALUE;
      }
   }

   public void clearRow (final int y) {
      assert (y >= 0 && y < height) : "The y coordinate is out of range.";
      Arrays.fill (canvas[y], DEFAULT_VALUE);
   }

   public void clear () {
      for (int y = 0, len = canvas.length; y < len; ++y) {
         Arrays.fill (canvas[y], DEFAULT_VALUE);
      }
   }

   @Override
   public String toString () {
      final StringBuilder builder = new StringBuilder ();

      for (int y = 0; y < height; ++y) {
         for (int x = 0; x < width; ++x) {
            builder.append ((canvas[y][x] != 0 ? '@' : '.'));
         }
         builder.append ('\n');
      }
      return builder.toString ();
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private int width  = DEFAULT_SIZE;
   private int height = DEFAULT_SIZE;
   private byte[][] canvas;

   private void ensureCapacity (final int desiredWidth,
                                final int desiredHeight)
   {
      if (this.width < desiredWidth) {
         correctHorizontalCapacity (desiredWidth);
         this.width = desiredWidth;
      }

      if (this.height < desiredHeight) {
         correctVerticalCapacity (desiredHeight);
         this.height = desiredHeight;
      }
   }

   private void correctHorizontalCapacity (final int desired) {
      final int available = canvas[0].length;

      if (available < desired) {
         final int suggested = findNewSize (available, desired);

         for (int y = 0, len = canvas.length; y < len; ++y) {
            canvas[y] = Arrays.copyOf (canvas[y], suggested);
            Arrays.fill (canvas[y], width, desired, DEFAULT_VALUE);
         }
      }
      else {
         for (int y = 0; y < height; ++y) {
            Arrays.fill (canvas[y], width, desired, DEFAULT_VALUE);
         }
      }
   }

   private void correctVerticalCapacity (final int desired) {
      final int available = canvas.length;

      if (available < desired) {
         final int suggested = findNewSize (available, desired);
         final int rowCapacity = canvas[0].length;

         canvas = Arrays.copyOf (canvas, suggested);

         for (int y = height; y < suggested; ++y) {
            canvas[y] = new byte[rowCapacity];
            Arrays.fill (canvas[y], 0, width, DEFAULT_VALUE);
         }
      }
      else {
         for (int y = height; y < desired; ++y) {
            Arrays.fill (canvas[y], 0, width, DEFAULT_VALUE);
         }
      }
   }

   private static int findNewSize (final int reference,
                                   final int desired)
   {
      int size = reference;

      while (size < desired) {
         if (Integer.MAX_VALUE - size < size) {
            throw new IllegalStateException ("Storage overflow.");
         }
         size += size;
      }

      return size;
   }
}
