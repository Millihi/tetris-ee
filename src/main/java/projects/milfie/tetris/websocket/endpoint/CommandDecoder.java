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
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import javax.xml.bind.DatatypeConverter;

public class CommandDecoder
   implements Decoder.Text<Command[]>
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   @Override
   public boolean willDecode (final String s) {
      return (s != null && !s.isEmpty () && s.startsWith (PACKET_ID));
   }

   @Override
   public Command[] decode (final String s)
      throws DecodeException
   {
      try {
         return EnvelopeSpec.decode (DatatypeConverter.parseHexBinary (s));
      }
      catch (final Throwable e) {
         throw new DecodeException (s, e.getMessage (), e);
      }
   }

   @Override
   public void init (final EndpointConfig config) {
      LOG.info
         ("Insert decoder @" + Integer.toHexString (this.hashCode ()) +
          " into service.");
   }

   @Override
   public void destroy () {
      LOG.info
         ("Removed decoder @" + Integer.toHexString (this.hashCode ()) +
          " from service.");
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final String PACKET_ID =
      DatatypeConverter.printHexBinary (EnvelopeSpec.getPacketId ());

   private static final Logger LOG =
      Logger.getLogger (CommandDecoder.class.getSimpleName ());
}
