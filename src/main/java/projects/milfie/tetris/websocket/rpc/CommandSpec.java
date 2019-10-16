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

public enum CommandSpec {
   ////////////////////////////////////////////////////////////////////////////
   //  Public static section                                                 //
   ////////////////////////////////////////////////////////////////////////////

   ////////////////////////////////////////////////////////////////////////////
   //  Incoming
   ROTATE_LEFT (Direction.INCOMING),
   ROTATE_RIGHT (Direction.INCOMING),
   MOVE_LEFT (Direction.INCOMING),
   MOVE_RIGHT (Direction.INCOMING),
   MOVE_DOWN (Direction.INCOMING),
   PUT_DOWN (Direction.INCOMING),
   START (Direction.INCOMING),
   STOP (Direction.INCOMING),
   PAUSE (Direction.INCOMING),
   ////////////////////////////////////////////////////////////////////////////
   //  Outcoming
   ENTER_RUN (Direction.OUTCOMING),
   ENTER_PAUSE (Direction.OUTCOMING),
   ENTER_STOP (Direction.OUTCOMING),
   ENTER_ERROR (Direction.OUTCOMING),
   UPDATE_COMBO
      (Direction.OUTCOMING,
       TypeSpec.STRING),
   UPDATE_SCORE
      (Direction.OUTCOMING,
       TypeSpec.STRING),
   UPDATE_LEVEL
      (Direction.OUTCOMING,
       TypeSpec.STRING),
   UPDATE_REGION
      (Direction.OUTCOMING,
       TypeSpec.INTEGER,
       TypeSpec.INTEGER,
       TypeSpec.BYTE_MATRIX),
   UPDATE_BRICK
      (Direction.OUTCOMING,
       TypeSpec.BYTE_MATRIX),
   WIN (Direction.OUTCOMING),
   LOOSE (Direction.OUTCOMING);

   public static int getEncodedLength (final Command command) {
      final CommandSpec spec = command.getSpec ();
      final TypeSpec[] types = spec.typeSpecs;
      final Object[] values = command.getArguments ();

      int length = HEADER_BYTES;

      for (int i = 0, il = types.length; i < il; ++i) {
         length += TypeSpec.getEncodedLength (types[i], values[i]);
      }

      return length;
   }

   public static ByteBuffer encode (final ByteBuffer buffer,
                                    final Command command)
   {
      final CommandSpec spec = command.getSpec ();

      if (spec.direction != Direction.OUTCOMING) {
         throw new IllegalArgumentException ("Not an outcoming command");
      }

      buffer.putChar (PACKET_ID);
      buffer.putInt (spec.ordinal ());

      final TypeSpec[] types = spec.typeSpecs;
      final Object[] values = command.getArguments ();

      for (int i = 0, il = types.length; i < il; ++i) {
         TypeSpec.encode (buffer, types[i], values[i]);
      }

      return buffer;
   }

   public static Command decode (final ByteBuffer buffer) {
      if (buffer.remaining () <= 0) {
         throw new IllegalArgumentException ("Given buffer is empty");
      }

      if (!skipPacketId (buffer)) {
         throw new IllegalArgumentException ("No command found");
      }

      final CommandSpec spec = getSpec (buffer.getInt ());

      if (spec == null || spec.direction == Direction.OUTCOMING) {
         throw new IllegalArgumentException ("Not an incoming message");
      }

      final TypeSpec[] types = spec.typeSpecs;
      final Object[] values = new Object[types.length];

      for (int i = 0, il = values.length; i < il; ++i) {
         values[i] = TypeSpec.decode (buffer, types[i]);
      }

      return new Command (spec, values);
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   private CommandSpec (final Direction direction,
                        final TypeSpec... typeSpecs)
   {
      this.direction = direction;
      this.typeSpecs = typeSpecs;
   }

   Direction getDirection () {
      return direction;
   }

   TypeSpec[] getTypeSpecs () {
      return typeSpecs;
   }

   private final Direction  direction;
   private final TypeSpec[] typeSpecs;

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   static final char PACKET_ID = 'C';

   private static final int HEADER_BYTES = 2 + 4;

   private static CommandSpec getSpec (final int code) {
      final CommandSpec[] specs = values ();

      return ((code < 0 || code >= specs.length) ? null : specs[code]);
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
