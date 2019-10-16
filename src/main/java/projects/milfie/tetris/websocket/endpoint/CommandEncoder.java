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

package projects.milfie.tetris.websocket.endpoint;

import projects.milfie.tetris.websocket.rpc.Command;
import projects.milfie.tetris.websocket.rpc.EnvelopeSpec;

import java.util.logging.Logger;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import javax.xml.bind.DatatypeConverter;

public class CommandEncoder
   implements Encoder.Text<Command[]>
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   @Override
   public String encode (final Command[] commands)
      throws EncodeException
   {
      try {
         return
            DatatypeConverter.printHexBinary
               (EnvelopeSpec.encode (commands));
      }
      catch (final Throwable e) {
         throw new EncodeException (e, e.getMessage ());
      }
   }

   @Override
   public void init (final EndpointConfig config) {
      LOG.info
         ("Insert encoder @" + Integer.toHexString (this.hashCode ()) +
          " into service.");
   }

   @Override
   public void destroy () {
      LOG.info
         ("Removed encoder @" + Integer.toHexString (this.hashCode ()) +
          " from service.");
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final Logger LOG =
      Logger.getLogger (CommandEncoder.class.getSimpleName ());
}
