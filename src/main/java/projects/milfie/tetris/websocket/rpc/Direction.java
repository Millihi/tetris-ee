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

package projects.milfie.tetris.websocket.rpc;

enum Direction {
   ////////////////////////////////////////////////////////////////////////////
   //  Public static section                                                 //
   ////////////////////////////////////////////////////////////////////////////

   INCOMING
      {
         @Override
         public Direction opposite () {
            return OUTCOMING;
         }
      },
   OUTCOMING
      {
         @Override
         public Direction opposite () {
            return INCOMING;
         }
      };

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public abstract Direction opposite ();
}
