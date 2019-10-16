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

public final class Computer {
   ////////////////////////////////////////////////////////////////////////////
   //  Public static section                                                 //
   ////////////////////////////////////////////////////////////////////////////

   public static final int MIN_DELAY =    50 / 50;
   public static final int MAX_DELAY = 1_000 / 50;

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public Gameplay getGameplay () {
      return gameplay;
   }

   public void setGameplay (final Gameplay gameplay) {
      if (gameplay == null) {
         throw new IllegalArgumentException ("Given gameplay is null.");
      }
      this.gameplay = gameplay;
   }

   public int getDelay () {
      return delay;
   }

   public void setDelay (final int delay) {
      if (delay < MIN_DELAY || delay > MAX_DELAY) {
         throw new IllegalArgumentException ("Given delay is out of range.");
      }
      this.delay = delay;
   }

   public void adjust (final Property.Level level) {
      if (level == null) {
         throw new IllegalArgumentException ("Given level is null.");
      }
      final int newDelay = MIN_DELAY + (int) (K_NORM * level.getRemains ());
      this.delay = (newDelay > MAX_DELAY ? MAX_DELAY : newDelay);
   }

   public void reset () {
      counter = 0;
   }

   public void update () {
      ++counter;

      if (counter >= delay) {
         counter = 0;
         gameplay.moveDown ();
      }
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private Gameplay gameplay = null;
   private int      delay    = MAX_DELAY;
   private int      counter  = 0;

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final double
      K_NORM = ((double) MAX_DELAY) / ((double) Property.Level.MAX_LEVEL);
}
