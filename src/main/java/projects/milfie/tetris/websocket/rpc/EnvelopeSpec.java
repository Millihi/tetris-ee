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

import java.nio.ByteBuffer;

public class EnvelopeSpec {
   ////////////////////////////////////////////////////////////////////////////
   //  Public static section                                                 //
   ////////////////////////////////////////////////////////////////////////////

   public static byte[] getPacketId () {
      return PACKET_ID_BYTE;
   }

   public static byte[] encode (final Command[] commands) {
      if (commands == null || commands.length <= 0) {
         throw new IllegalArgumentException ("No commands given");
      }

      final int bytesCount = getEncodedLength (commands);
      final ByteBuffer buffer = ByteBuffer.allocate (bytesCount);

      buffer.putChar (PACKET_ID);
      buffer.putInt (VERSION);
      buffer.putInt (commands.length);

      for (int i = 0, il = commands.length; i < il; ++i) {
         CommandSpec.encode (buffer, commands[i]);
      }

      return buffer.array ();
   }

   public static Command[] decode (final byte[] bytes) {
      if (bytes == null || bytes.length <= 0) {
         throw new IllegalArgumentException ("No message given");
      }

      final ByteBuffer buffer = ByteBuffer.wrap (bytes);

      if (!skipPacketId (buffer)) {
         throw new IllegalStateException ("No envelope given");
      }

      final int version = buffer.getInt ();

      if (version != VERSION) {
         throw new IllegalStateException
            ("Got version " + version + ", expected " + VERSION);
      }

      final int length = buffer.getInt ();
      final Command[] commands = new Command[length];

      for (int i = 0; i < length; ++i) {
         commands[i] = CommandSpec.decode (buffer);
      }

      return commands;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   static final char PACKET_ID = 'E';
   static final int  VERSION   = 1;

   private static final byte[] PACKET_ID_BYTE =
      new byte[]{0x00, (byte) PACKET_ID};

   private static final int HEADER_BYTE_LENGTH = 2 + 4 + 4;

   private static int getEncodedLength (final Command[] commands) {
      int length = HEADER_BYTE_LENGTH;

      for (int i = 0, il = commands.length; i < il; ++i) {
         length += CommandSpec.getEncodedLength (commands[i]);
      }

      return length;
   }

   private static boolean skipPacketId (final ByteBuffer buffer) {
      if (buffer.remaining () < 2) {
         return false;
      }

      buffer.mark ();

      if (buffer.getChar () != PACKET_ID) {
         buffer.reset ();
         return false;
      }

      return true;
   }
}
