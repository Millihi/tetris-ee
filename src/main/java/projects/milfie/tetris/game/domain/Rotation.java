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

public enum Rotation {
   ////////////////////////////////////////////////////////////////////////////
   //  Public static section                                                 //
   ////////////////////////////////////////////////////////////////////////////

   CLOCKWISE
      {
         @Override
         public <T extends Rotable<T>> T of (final T rotable) {
            return rotable.rotateClockwise ();
         }
      },
   COUNTERCLOCKWISE
      {
         @Override
         public <T extends Rotable<T>> T of (final T rotable) {
            return rotable.rotateCounterClockwise ();
         }
      };

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public abstract <T extends Rotable<T>> T of (final T rotable);
}
