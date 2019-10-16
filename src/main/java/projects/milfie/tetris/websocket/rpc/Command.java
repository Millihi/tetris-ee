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

public final class Command {
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public Command (final CommandSpec spec,
                   final Object... arguments)
   {
      if (spec == null) {
         throw new IllegalArgumentException ("Given spec is null.");
      }
      if (arguments == null) {
         throw new IllegalArgumentException ("Given arguments is null.");
      }
      this.spec = spec;
      this.arguments = arguments;
   }

   public CommandSpec getSpec () {
      return spec;
   }

   public Object[] getArguments () {
      return arguments;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private final CommandSpec spec;
   private final Object[]    arguments;
}
