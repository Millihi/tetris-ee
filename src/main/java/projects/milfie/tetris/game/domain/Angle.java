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

public enum Angle {
   ////////////////////////////////////////////////////////////////////////////
   //  Public static section                                                 //
   ////////////////////////////////////////////////////////////////////////////

   DEGREE_0
      {
         @Override
         public Angle next () {
            return DEGREE_90;
         }

         @Override
         public Angle prev () {
            return DEGREE_270;
         }
      },
   DEGREE_90
      {
         @Override
         public Angle next () {
            return DEGREE_180;
         }

         @Override
         public Angle prev () {
            return DEGREE_0;
         }
      },
   DEGREE_180
      {
         @Override
         public Angle next () {
            return DEGREE_270;
         }

         @Override
         public Angle prev () {
            return DEGREE_90;
         }
      },
   DEGREE_270
      {
         @Override
         public Angle next () {
            return DEGREE_0;
         }

         @Override
         public Angle prev () {
            return DEGREE_180;
         }
      };

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public abstract Angle next ();

   public abstract Angle prev ();

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////
}
