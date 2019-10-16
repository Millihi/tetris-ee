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
import java.nio.charset.Charset;

enum TypeSpec {
   ////////////////////////////////////////////////////////////////////////////
   //  Public static section                                                 //
   ////////////////////////////////////////////////////////////////////////////

   BYTE
      {
         @Override
         int getEncodedLength (final Object o) {
            return 1;
         }

         @Override
         void encode (final ByteBuffer buf, final Object o) {
            buf.put ((Byte) o);
         }

         @Override
         Byte decode (final ByteBuffer buf) {
            return buf.get ();
         }
      },
   BYTE_ARRAY
      {
         @Override
         int getEncodedLength (final Object o) {
            return (4 + ((byte[]) o).length);
         }

         @Override
         void encode (final ByteBuffer buf, final Object o) {
            final byte[] bytes = (byte[]) o;
            final int length = bytes.length;

            buf.putInt (length);

            for (int idx = 0; idx < length; ++idx) {
               buf.put (bytes[idx]);
            }
         }

         @Override
         byte[] decode (final ByteBuffer buf) {
            final int length = buf.getInt ();
            final byte[] bytes = new byte[length];

            for (int idx = 0; idx < length; ++idx) {
               bytes[idx] = buf.get ();
            }

            return bytes;
         }
      },
   BYTE_MATRIX
      {
         @Override
         int getEncodedLength (final Object o) {
            final byte[][] bytes = (byte[][]) o;
            int length = 4 + 4;

            if (bytes.length > 0) {
               length += bytes.length * bytes[0].length;
            }

            return length;
         }

         @Override
         void encode (final ByteBuffer buf, final Object o) {
            final byte[][] bytes = (byte[][]) o;
            final int yl = bytes.length;
            final int xl = (bytes.length > 0 ? bytes[0].length : 0);

            buf.putInt (xl).putInt (yl);

            for (int y = 0; y < yl; ++y) {
               for (int x = 0; x < xl; ++x) {
                  buf.put (bytes[y][x]);
               }
            }
         }

         @Override
         byte[][] decode (final ByteBuffer buf) {
            final int xl = buf.getInt ();
            final int yl = buf.getInt ();
            final byte[][] bytes = new byte[yl][xl];

            for (int y = 0; y < yl; ++y) {
               for (int x = 0; x < xl; ++x) {
                  bytes[y][x] = buf.get ();
               }
            }

            return bytes;
         }
      },
   INTEGER
      {
         @Override
         int getEncodedLength (final Object o) {
            return 4;
         }

         @Override
         void encode (final ByteBuffer buf, final Object o) {
            buf.putInt ((Integer) o);
         }

         @Override
         Integer decode (final ByteBuffer buf) {
            return buf.getInt ();
         }
      },
   INTEGER_ARRAY
      {
         @Override
         int getEncodedLength (final Object o) {
            return (4 + 4 * ((int[]) o).length);
         }

         @Override
         void encode (final ByteBuffer buf, final Object o) {
            final int[] value = (int[]) o;
            final int length = value.length;

            buf.putInt (length);

            for (int idx = 0; idx < length; ++idx) {
               buf.putInt (value[idx]);
            }
         }

         @Override
         int[] decode (final ByteBuffer buf) {
            final int length = buf.getInt ();
            final int[] ints = new int[length];

            for (int idx = 0; idx < length; ++idx) {
               ints[idx] = buf.getInt ();
            }

            return ints;
         }
      },
   INTEGER_MATRIX
      {
         @Override
         int getEncodedLength (final Object o) {
            final int[][] ints = (int[][]) o;
            int length = 4 + 4;

            if (ints.length > 0) {
               length += 4 * ints.length * ints[0].length;
            }

            return length;
         }

         @Override
         void encode (final ByteBuffer buf, final Object o) {
            final int[][] ints = (int[][]) o;
            final int yl = ints.length;
            final int xl = (ints.length > 0 ? ints[0].length : 0);

            buf.putInt (xl).putInt (yl);

            for (int y = 0; y < yl; ++y) {
               for (int x = 0; x < xl; ++x) {
                  buf.putInt (ints[y][x]);
               }
            }
         }

         @Override
         int[][] decode (final ByteBuffer buf) {
            final int xl = buf.getInt ();
            final int yl = buf.getInt ();
            final int[][] ints = new int[yl][xl];

            for (int y = 0; y < yl; ++y) {
               for (int x = 0; x < xl; ++x) {
                  ints[y][x] = buf.getInt ();
               }
            }

            return ints;
         }
      },
   BOOLEAN
      {
         @Override
         int getEncodedLength (final Object o) {
            return 1;
         }

         @Override
         void encode (final ByteBuffer buf, final Object o) {
            buf.put ((byte) (((Boolean) o) ? 1 : 0));
         }

         @Override
         Boolean decode (final ByteBuffer buf) {
            return (buf.get () != 0);
         }
      },
   STRING
      {
         @Override
         int getEncodedLength (final Object o) {
            return (4 + 2 * ((String) o).length ());
         }

         @Override
         void encode (final ByteBuffer buf, final Object o) {
            final String value = (String) o;
            final int length = value.length ();

            buf.putInt (length);

            for (int idx = 0; idx < length; ++idx) {
               buf.putChar (value.charAt (idx));
            }
         }

         @Override
         String decode (final ByteBuffer buf) {
            final int length = buf.getInt ();

            return
               new String
                  (buf.array (),
                   buf.position (),
                   length,
                   Charset.forName ("UTF-8"));
         }
      };

   public static int getEncodedLength (final TypeSpec spec,
                                       final Object value)
   {
      return (HEADER_BYTES + spec.getEncodedLength (value));
   }

   public static void encode (final ByteBuffer buffer,
                              final TypeSpec spec,
                              final Object value)
   {
      buffer.putChar (PACKET_ID);
      buffer.putInt (spec.ordinal ());

      spec.encode (buffer, value);
   }

   public static Object decode (final ByteBuffer buffer,
                                final TypeSpec expected)
   {
      if (!skipPacketId (buffer)) {
         throw new IllegalStateException
            ("No spec, expected " + expected.name ());
      }

      final TypeSpec spec = getSpec (buffer.getInt ());

      if (spec != expected) {
         throw new IllegalStateException
            ("Got " + spec.name () + ", expected " + expected.name ());
      }

      return spec.decode (buffer);
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   abstract int getEncodedLength (final Object o);

   abstract void encode (final ByteBuffer buf, final Object o);

   abstract Object decode (final ByteBuffer buf);

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   static final char PACKET_ID = 'T';

   private static final int HEADER_BYTES = 2 + 4;

   private static TypeSpec getSpec (final int code) {
      final TypeSpec[] specs = values ();

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
