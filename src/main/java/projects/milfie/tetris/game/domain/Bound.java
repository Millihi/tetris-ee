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

public enum Bound {
   ////////////////////////////////////////////////////////////////////////////
   //  Public static section                                                 //
   ////////////////////////////////////////////////////////////////////////////

   TOP
      {
         @Override
         public int of (final Rectable<?> rectable) {
            return rectable.getTopBound ();
         }

         @Override
         public int minOf (final Rectable<?> left, final Rectable<?> right) {
            return Math.min (left.getTopBound (), right.getTopBound ());
         }

         @Override
         public int maxOf (final Rectable<?> left, final Rectable<?> right) {
            return Math.max (left.getTopBound (), right.getTopBound ());
         }
      },
   RIGHT
      {
         @Override
         public int of (final Rectable<?> rectable) {
            return rectable.getRightBound ();
         }

         @Override
         public int minOf (final Rectable<?> left, final Rectable<?> right) {
            return Math.min (left.getRightBound (), right.getRightBound ());
         }

         @Override
         public int maxOf (final Rectable<?> left, final Rectable<?> right) {
            return Math.max (left.getRightBound (), right.getRightBound ());
         }
      },
   BOTTOM
      {
         @Override
         public int of (final Rectable<?> rectable) {
            return rectable.getBottomBound ();
         }

         @Override
         public int minOf (final Rectable<?> left, final Rectable<?> right) {
            return Math.min (left.getBottomBound (), right.getBottomBound ());
         }

         @Override
         public int maxOf (final Rectable<?> left, final Rectable<?> right) {
            return Math.max (left.getBottomBound (), right.getBottomBound ());
         }
      },
   LEFT
      {
         @Override
         public int of (final Rectable<?> rectable) {
            return rectable.getLeftBound ();
         }

         @Override
         public int minOf (final Rectable<?> left, final Rectable<?> right) {
            return Math.min (left.getLeftBound (), right.getLeftBound ());
         }

         @Override
         public int maxOf (final Rectable<?> left, final Rectable<?> right) {
            return Math.max (left.getLeftBound (), right.getLeftBound ());
         }
      };

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public abstract int of (final Rectable<?> rectable);

   public abstract int minOf (final Rectable<?> left, final Rectable<?> right);

   public abstract int maxOf (final Rectable<?> left, final Rectable<?> right);
}
