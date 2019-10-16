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

public abstract class Property<E> {
   ////////////////////////////////////////////////////////////////////////////
   //  Public static section                                                 //
   ////////////////////////////////////////////////////////////////////////////

   public static final class Score
      extends Property<Long>
   {
      /////////////////////////////////////////////////////////////////////////
      //  Public static section                                              //
      /////////////////////////////////////////////////////////////////////////

      public static final int ROW_COST = 10;

      /////////////////////////////////////////////////////////////////////////
      //  Public section                                                     //
      /////////////////////////////////////////////////////////////////////////

      public Score (final Combo combo,
                    final Level level)
      {
         super ();
         if (combo == null) {
            throw new IllegalArgumentException ("Given combo is null.");
         }
         if (level == null) {
            throw new IllegalArgumentException ("Given level is null.");
         }
         this.combo = combo;
         this.level = level;
      }

      /////////////////////////////////////////////////////////////////////////
      //  Private section                                                    //
      /////////////////////////////////////////////////////////////////////////

      private final Combo combo;
      private final Level level;

      @Override
      protected void set (final Long value) {
         if (value == null) {
            throw new IllegalArgumentException ("Given value is null.");
         }
         if (value < 0) {
            throw new IllegalArgumentException ("Given value is negative.");
         }
         super.set (value);
      }

      @Override
      protected void update (final Object obj) {
         final int removedRows = (Integer) obj;

         if (removedRows < 0) {
            throw new IllegalArgumentException ("Negative rows count given.");
         }

         if (removedRows == 0) {
            combo.set (0);
         }
         else {
            int score =
               ROW_SCALE * removedRows +
               K_LEVEL * level.get ();

            if (removedRows > 1) {
               score += K_ROWS * (removedRows - 1);
            }

            if (combo.get () > 0) {
               score += K_COMBO * combo.get ();
            }

            combo.set (combo.get () + 1);
            super.set (value + score);
            level.update (value);
         }
      }

      @Override
      protected void reset () {
         this.accept ();
         value = 0L;
      }

      /////////////////////////////////////////////////////////////////////////
      //  Private static section                                             //
      /////////////////////////////////////////////////////////////////////////

      private static final int ROW_SCALE = ROW_COST;
      private static final int K_COMBO   = 4;
      private static final int K_ROWS    = 3;
      private static final int K_LEVEL   = 2;
   }

   public static final class Combo
      extends Property<Integer>
   {
      /////////////////////////////////////////////////////////////////////////
      //  Public section                                                     //
      /////////////////////////////////////////////////////////////////////////

      public Combo () {
         super ();
      }

      /////////////////////////////////////////////////////////////////////////
      //  Private section                                                    //
      /////////////////////////////////////////////////////////////////////////

      @Override
      protected void set (final Integer value) {
         if (value == null) {
            throw new IllegalArgumentException ("Given value is null.");
         }
         if (value < 0) {
            throw new IllegalArgumentException ("Given value is negative.");
         }
         super.set (value);
      }

      @Override
      protected void update (final Object obj) {
         return;
      }

      @Override
      protected void reset () {
         this.accept ();
         value = 0;
      }
   }

   public static final class Level
      extends Property<Integer>
   {
      /////////////////////////////////////////////////////////////////////////
      //  Public static section                                              //
      /////////////////////////////////////////////////////////////////////////

      public static final int MIN_LEVEL = 1;
      public static final int MAX_LEVEL = 100;

      /////////////////////////////////////////////////////////////////////////
      //  Public section                                                     //
      /////////////////////////////////////////////////////////////////////////

      public Level () {
         super ();
      }

      public int getRemains () {
         return MAX_LEVEL - value;
      }

      /////////////////////////////////////////////////////////////////////////
      //  Private section                                                    //
      /////////////////////////////////////////////////////////////////////////

      @Override
      protected void set (final Integer value) {
         if (value == null) {
            throw new IllegalArgumentException ("Given value is null.");
         }
         if (value < 0) {
            throw new IllegalArgumentException ("Given value is negative.");
         }
         super.set (value);
      }

      @Override
      protected void update (final Object obj) {
         final Long score = (Long) obj;

         if (score == null) {
            throw new IllegalArgumentException ("Given score is null.");
         }

         super.set ((int) calculateLevel ((double) score));
      }

      @Override
      protected void reset () {
         this.accept ();
         value = MIN_LEVEL;
      }

      /////////////////////////////////////////////////////////////////////////
      //  Private static section                                             //
      /////////////////////////////////////////////////////////////////////////

      private static final double
         K_NORM   = 2.0 / Math.PI,
         X_SCALE  = 1.0 / Math.pow (MAX_LEVEL + Score.ROW_COST, 2.0),
         Y_SCALE  = MAX_LEVEL,
         Y_OFFSET = MIN_LEVEL;

      private static double calculateLevel (final double total) {
         return Y_OFFSET + K_NORM * Y_SCALE * Math.atan (X_SCALE * total);
      }
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public E get () {
      return value;
   }

   public boolean isChanged () {
      return changed;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   protected E       value;
   private   boolean changed;

   private Property () {
      this.reset ();
   }

   protected abstract void update (final Object obj);

   protected void set (final E value) {
      this.changed |= !(this.value.equals (value));
      this.value = value;
   }

   protected void accept () {
      changed = false;
   }

   protected abstract void reset ();

   protected void mark () {
      changed = true;
   }
}
