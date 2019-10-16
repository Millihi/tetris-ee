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

public enum Figure {

   ////////////////////////////////////////////////////////////////////////////
   //  Public static section                                                 //
   ////////////////////////////////////////////////////////////////////////////

   CAPITAL_I_FORM (new boolean[][]{
      {true},
      {true},
      {true},
      {true}
   }, new boolean[][]{
      {true, true, true, true}
   }),
   SMALL_I_FORM (new boolean[][]{
      {true},
      {true},
      {true}
   }, new boolean[][]{
      {true, true, true}
   }),
   SMALL_O_FORM (new boolean[][]{
      {true, true},
      {true, true}
   }),
   SMALL_Z_FORM (new boolean[][]{
      {true, true, false},
      {false, true, true}
   }, new boolean[][]{
      {false, true},
      {true, true},
      {true, false}
   }),
   SMALL_S_FORM (new boolean[][]{
      {false, true, true},
      {true, true, false}
   }, new boolean[][]{
      {true, false},
      {true, true},
      {false, true}
   }),
   SMALL_T_FORM (new boolean[][]{
      {true, false},
      {true, true},
      {true, false}
   }, new boolean[][]{
      {true, true, true},
      {false, true, false}
   }, new boolean[][]{
      {false, true},
      {true, true},
      {false, true}
   }, new boolean[][]{
      {false, true, false},
      {true, true, true}
   }),
   CAPITAL_J_FORM (new boolean[][]{
      {false, true},
      {false, true},
      {true, true}
   }, new boolean[][]{
      {true, false, false},
      {true, true, true}
   }, new boolean[][]{
      {true, true},
      {true, false},
      {true, false}
   }, new boolean[][]{
      {true, true, true},
      {false, false, true}
   }),
   CAPITAL_L_FORM (new boolean[][]{
      {true, false},
      {true, false},
      {true, true}
   }, new boolean[][]{
      {true, true, true},
      {true, false, false}
   }, new boolean[][]{
      {true, true},
      {false, true},
      {false, true}
   }, new boolean[][]{
      {false, false, true},
      {true, true, true}
   });

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public int getWidth (final Angle angle) {
      assert (angle != null) : "The angle is null.";
      return shapes[angle.ordinal ()][0].length;
   }

   public int getHeight (final Angle angle) {
      assert (angle != null) : "The angle is null.";
      return shapes[angle.ordinal ()].length;
   }

   public boolean[][] getShape (final Angle angle) {
      assert (angle != null) : "The angle is null.";
      return shapes[angle.ordinal ()];
   }

   public boolean getPointValue (final Angle angle,
                                 final int x,
                                 final int y)
   {
      assert (angle != null) : "The angle is null.";
      assert (x >= 0) : "The x coordinate is negative.";
      assert (y >= 0) : "The y coordinate is negative.";
      return shapes[angle.ordinal ()][y][x];
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private final boolean[][][] shapes =
      new boolean[Angle.values ().length][][];

   private Figure (final boolean[][] shape0) {
      this.shapes[Angle.DEGREE_0.ordinal ()] = shape0;
      this.shapes[Angle.DEGREE_90.ordinal ()] = shape0;
      this.shapes[Angle.DEGREE_180.ordinal ()] = shape0;
      this.shapes[Angle.DEGREE_270.ordinal ()] = shape0;
   }

   private Figure (final boolean[][] shape0,
                   final boolean[][] shape90)
   {
      this.shapes[Angle.DEGREE_0.ordinal ()] = shape0;
      this.shapes[Angle.DEGREE_90.ordinal ()] = shape90;
      this.shapes[Angle.DEGREE_180.ordinal ()] = shape0;
      this.shapes[Angle.DEGREE_270.ordinal ()] = shape90;
   }

   private Figure (final boolean[][] shape0,
                   final boolean[][] shape90,
                   final boolean[][] shape180,
                   final boolean[][] shape270)
   {
      this.shapes[Angle.DEGREE_0.ordinal ()] = shape0;
      this.shapes[Angle.DEGREE_90.ordinal ()] = shape90;
      this.shapes[Angle.DEGREE_180.ordinal ()] = shape180;
      this.shapes[Angle.DEGREE_270.ordinal ()] = shape270;
   }
}
