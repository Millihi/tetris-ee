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

import javax.ejb.ApplicationException;

@ApplicationException (rollback = true)
public abstract class ServiceException
   extends Exception
{

   public ServiceException () {
      super ();
   }

   public ServiceException (final String message) {
      super (message);
   }

   public ServiceException (final Throwable cause) {
      super (cause);
   }

   public ServiceException (final String message,
                            final Throwable cause)
   {
      super (message, cause);
   }

   private static final long serialVersionUID = 201906161757L;
}
