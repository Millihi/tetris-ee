///////////////////////////////////////////////////////////////////////////////
//  "THE CAKE-WARE LICENSE" (Revision 41):                                   //
//                                                                           //
//      Milfeulle <mail@milfie.uu.me> wrote this file. As long as you        //
//  retain this notice you can do whatever you want with this stuff. If we   //
//  meet some day, and you think this stuff is worth it, you can buy me      //
//  a cake in return.                                                        //
//                                                                           //
//      Milfie                                                               //
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  ByteBuffer
function ByteBuffer (di_bytes) {

   var
      self = this,
      pv_pos = 0,
      pv_mark = 0;

   ////////////////////////////////////////////////////////////////////////////
   //  Public                                                                //
   ////////////////////////////////////////////////////////////////////////////

   function im_initialize () {
      if (typeof di_bytes !== "object" ||
         typeof di_bytes.length !== "number" ||
         typeof di_bytes.push !== "function")
      {
         throw new Error ("Given di_bytes isn't an array.");
      }
      return self;
   }

   self.name = "ByteBuffer";

   self.getArray = function () {
      return di_bytes;
   };

   self.getPosition = function () {
      return pv_pos;
   };
   self.setPosition = function (position) {
      if (position < 0 || position >= di_bytes.length) {
         throw new Error ("Given position is out of range.");
      }
      pv_pos = position;
      return self;
   };

   self.clear = function () {
      pv_mark = 0;
      pv_pos = 0;
      di_bytes.length = 0;
      return self;
   };
   self.mark = function () {
      pv_mark = pv_pos;
      return self;
   };
   self.reset = function () {
      pv_pos = pv_mark;
      return self;
   };

   self.remaining = function () {
      return Math.max (0, (di_bytes.length - pv_pos));
   };

   self.readByte = function () {
      if (pv_pos >= di_bytes.length) {
         throw new Error ("No bytes to read.");
      }
      return di_bytes[pv_pos++];
   };
   self.writeByte = function (value) {
      if ((value >>> 8) != 0) {
         throw new Error ("Not a byte.");
      }
      di_bytes[pv_pos++] = value;
      return self;
   };

   self.readChar = function () {
      if (pv_pos + 2 > di_bytes.length) {
         throw new Error ("No bytes to read.");
      }
      return ((di_bytes[pv_pos++] << 8) | (di_bytes[pv_pos++]));
   };
   self.writeChar = function (value) {
      if ((value >>> 16) != 0) {
         throw new Error ("Not a char.");
      }
      di_bytes[pv_pos++] = ((value >>> 8) & 0xFF);
      di_bytes[pv_pos++] = (value & 0xFF);
      return self;
   };

   self.readInt = function () {
      if (pv_pos + 4 > di_bytes.length) {
         throw new Error ("No bytes to read.");
      }
      return (
         (di_bytes[pv_pos++] << 24) |
         (di_bytes[pv_pos++] << 16) |
         (di_bytes[pv_pos++] << 8) |
         (di_bytes[pv_pos++]));
   };
   self.writeInt = function (value) {
      if (typeof value !== "number") {
         throw new Error ("Not an integer.");
      }
      di_bytes[pv_pos++] = ((value >>> 24) & 0xFF);
      di_bytes[pv_pos++] = ((value >>> 16) & 0xFF);
      di_bytes[pv_pos++] = ((value >>> 8) & 0xFF);
      di_bytes[pv_pos++] = (value & 0xFF);
      return self;
   };

   return im_initialize ();
}
ByteBuffer.allocate = function () {
   return new ByteBuffer ([]);
};
ByteBuffer.wrap = function (bytes) {
   return new ByteBuffer (bytes);
};
