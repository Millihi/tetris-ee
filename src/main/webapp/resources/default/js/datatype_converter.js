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
//  DatatypeConverter
function DatatypeConverter () {}

DatatypeConverter.parseHexBinary = function (hex) {
   if ((hex.length & 1) !== 0) {
      throw new Error ("Not even-length: [" + hex + "].");
   }

   var i = 0, length = hex.length, ch, byte = 0, bytes = [];

   while (i < length) {
      ch = lm_validate (hex.charCodeAt (i++));
      byte = ((byte << 4) | ((ch > 64 ? ch + 9 : ch) & 0xF));
      ch = lm_validate (hex.charCodeAt (i++));
      byte = ((byte << 4) | ((ch > 64 ? ch + 9 : ch) & 0xF)) & 0xFF;
      bytes.push (byte);
   }

   return bytes;

   function lm_validate (ch) {
      if (ch > 0x2F && ch < 0x3A) { return ch; }
      if (ch > 0x40 && ch < 0x47) { return ch; }
      if (ch > 0x60 && ch < 0x67) { return ch; }
      throw new Error
         ("Invalid char in hex sequence at " + (i - 1) + ": " + ch);
   }
};

DatatypeConverter.printHexBinary = function (bytes) {
   var
      alpha = "A".charCodeAt (0) - 10,
      digit = "0".charCodeAt (0),
      chars = [], i = 0, length = bytes.length, byte, nibble;

   while (i < length) {
      byte = bytes[i++];
      if ((byte >>> 8) !== 0) {
         throw new Error (i + "'s byte is out of range: " + byte);
      }
      nibble = (byte >>> 4);
      chars.push (nibble + (nibble > 9 ? alpha : digit));
      nibble = (byte & 0xF);
      chars.push (nibble + (nibble > 9 ? alpha : digit));
   }

   return String.fromCharCode.apply (null, chars);
};
