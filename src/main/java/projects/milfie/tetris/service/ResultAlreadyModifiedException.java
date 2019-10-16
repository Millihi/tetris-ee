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

package projects.milfie.tetris.service;

public class ResultAlreadyModifiedException
   extends AlreadyModifiedException
{

   public ResultAlreadyModifiedException () {
      super ();
   }

   public ResultAlreadyModifiedException (final String message) {
      super (message);
   }

   public ResultAlreadyModifiedException (final Throwable cause) {
      super (cause);
   }

   public ResultAlreadyModifiedException (final String message,
                                          final Throwable cause)
   {
      super (message, cause);
   }

   private static final long serialVersionUID = 201909260531L;
}
