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
//  Rpc
function Rpc () {

   var
      self = this,
      SPEC = null;

   ////////////////////////////////////////////////////////////////////////////
   //  Private modules                                                       //
   ////////////////////////////////////////////////////////////////////////////

   ////////////////////////////////////////////////////////////////////////////
   //  Codec
   function Codec () {}
   Codec.willDecode = function (string) {
      if (typeof string !== "string" || string.length < 4) {
         return false;
      }

      var bytes, packetId;

      try {
         bytes = DatatypeConverter.parseHexBinary (string.substr (0, 4));
         packetId = ((bytes[0] << 8) | bytes[1]) | 0;

         return (packetId === SPEC.PACKET_ID);
      }
      catch (e) {
         return false;
      }
   };
   Codec.decode = function (string) {
      return Codec.Envelope.decode (
         DatatypeConverter.parseHexBinary (string));
   };
   Codec.encode = function (commands) {
      return DatatypeConverter.printHexBinary (
         Codec.Envelope.encode (commands));
   };

   Codec.Envelope = function Envelope () {};
   Codec.Envelope.skipPacketId = function (buffer) {
      if (buffer.remaining () < 2) {
         return false;
      }
      if (buffer.mark ().readChar () != SPEC.PACKET_ID) {
         buffer.reset ();
         return false;
      }
      return true;
   };
   Codec.Envelope.getEncodedLength = function (commands) {
      var length = 2 + 4 + 4;

      for (var j = 0, jl = commands.length; j < jl; ++j) {
         length += Codec.Command.getEncodedLength (commands[i]);
      }

      return length;
   };
   Codec.Envelope.encode = function (commands) {
      if (typeof commands !== "object" || commands.length <= 0) {
         throw new Error ("No commands given");
      }

      var buffer = ByteBuffer.allocate ();

      buffer
         .writeChar (SPEC.PACKET_ID)
         .writeInt (SPEC.Version)
         .writeInt (commands.length);

      for (var i = 0, il = commands.length; i < il; ++i) {
         Codec.Command.encode (buffer, commands[i]);
      }

      return buffer.getArray ();
   };
   Codec.Envelope.decode = function (bytes) {
      if (typeof bytes !== "object" || bytes.length <= 0) {
         throw new Error ("Given bytes is empty.");
      }

      var buffer = ByteBuffer.wrap (bytes);

      if (!Codec.Envelope.skipPacketId (buffer)) {
         throw new Error ("No envelope found.");
      }

      var version = buffer.readInt ();

      if (version != SPEC.Version) {
         throw new
            Error ("Got " + version + " version, expected " + SPEC.Version);
      }

      var length = buffer.readInt (), commands = [];

      while (length-- > 0) {
         commands.push (Codec.Command.decode (buffer));
      }

      return commands;
   };

   Codec.Command = function Command () {};
   Codec.Command.skipPacketId = function (buffer) {
      if (buffer.remaining () < 2) {
         return false;
      }
      if (buffer.mark ().readChar () != SPEC.COMMAND.PACKET_ID) {
         buffer.reset ();
         return false;
      }
      return true;
   };
   Codec.Command.getEncodedLength = function (command) {
      var length = 2 + 4;
      var args = command.spec.PARAMETERS, values = command.args;

      for (var j = 0, jl = args.length; j < jl; ++j) {
         length += Codec.Type.getEncodedLength (args.NAME, values[j]);
      }

      return length;
   };
   Codec.Command.encode = function (buffer, command) {
      if (typeof command !== "object") {
         throw new Error ("Not a command.");
      }

      var spec = command.spec, args = command.args;

      if (typeof spec !== "object") {
         throw new Error ("Spec for command " + command + " not found.");
      }

      if (spec.DIRECTION !== SPEC.DIRECTION.OUTCOMING) {
         throw new Error ("Not an outcoming command: " + spec.NAME);
      }

      buffer
         .writeChar (SPEC.COMMAND.PACKET_ID)
         .writeInt (spec.ID);

      for (var j = 0, jl = spec.PARAMETERS.length; j < jl; ++j) {
         Codec.Type.encode (buffer, spec.PARAMETERS[i].NAME, args[j]);
      }
   };
   Codec.Command.decode = function (buffer) {
      if (!Codec.Command.skipPacketId (buffer)) {
         throw new Error ("No command found");
      }

      var spec = SPEC.COMMAND[buffer.readInt ()];

      if (typeof spec !== "object") {
         throw new Error ("No spec for command: [" + buffer + "].");
      }

      if (spec.DIRECTION !== SPEC.DIRECTION.INCOMING) {
         throw new Error ("Not an incoming command.");
      }

      var args = [];

      for (var i = 0, len = spec.PARAMETERS.length; i < len; ++i) {
         args.push (Codec.Type.decode (buffer, spec.PARAMETERS[i].NAME));
      }

      return CommandFactory.createInternal (spec, args);
   };

   Codec.Type = function Type () {};
   Codec.Type.skipPacketId = function (buffer) {
      if (buffer.remaining () < 2) {
         return false;
      }
      if (buffer.mark ().readChar () != SPEC.TYPE.PACKET_ID) {
         buffer.reset ();
         return false;
      }
      return true;
   };
   Codec.Type.getEncodedLength = function (typeName, value) {
      return (2 + 4 + Codec.Type[typeName].getEncodedLength (value));
   };
   Codec.Type.encode = function (buffer, typeName, value) {
      if (typeof SPEC.TYPE[typeName] !== "object") {
         throw new Error ("Type " + typeName + " is unknown.");
      }
      Codec.Type[typeName].encode (buffer, value);
   };
   Codec.Type.decode = function (buffer, typeName) {
      if (!Codec.Type.skipPacketId (buffer)) {
         throw new Error ("No " + typeName + " packet found.");
      }
      var type = SPEC.TYPE[buffer.readInt ()];
      if (typeof type !== "object") {
         throw new Error ("Got unknown type, expected " + typeName);
      }
      if (type.NAME !== typeName) {
         throw new Error ("Got " + type.NAME + ", expected " + typeName);
      }
      return Codec.Type[typeName].decode (buffer);
   };
   Codec.Type.BYTE = {
      getEncodedLength: function (value) {
         return 1;
      },
      encode: function (buffer, value) {
         buffer.writeByte (value);
      },
      decode: function (buffer) {
         return buffer.readByte ();
      }
   };
   Codec.Type.BYTE_ARRAY = {
      getEncodedLength: function (value) {
         return (4 + value.length);
      },
      encode: function (buffer, value) {
         var idx = 0, length = value.length;
         buffer.writeInt (length);
         while (idx < length) {
            buffer.writeByte (value[idx++]);
         }
      },
      decode: function (buffer) {
         var bytes = [], idx = 0;
         var length = buffer.readInt ();
         while (idx < length) {
            bytes[idx++] = buffer.readByte ();
         }
         return bytes;
      }
   };
   Codec.Type.BYTE_MATRIX = {
      getEncodedLength: function (value) {
         var length = 4 + 4;
         if (value.length > 0) {
            length += value.length * value[0].length;
         }
         return length;
      },
      encode: function (buffer, value) {
         var width = value.length;
         var height = (value.length > 0 ? value[0].length : 0);
         var row = 0, col = 0;
         buffer.writeInt (width).writeInt (height);
         for (row = 0; row < height; ++row) {
            for (col = 0; col < width; ++col) {
               buffer.writeByte (value[row][col]);
            }
         }
      },
      decode: function (buffer) {
         var width = buffer.readInt ();
         var height = buffer.readInt ();
         var bytes = [], row = 0, col = 0;
         for (row = 0; row < height; ++row) {
            bytes[row] = [];
            for (col = 0; col < width; ++col) {
               bytes[row][col] = buffer.readByte ();
            }
         }
         return bytes;
      }
   };
   Codec.Type.INTEGER = {
      getEncodedLength: function (value) {
         return 4;
      },
      encode: function (buffer, value) {
         buffer.writeInt (value);
      },
      decode: function (buffer) {
         return buffer.readInt ();
      }
   };
   Codec.Type.INTEGER_ARRAY = {
      getEncodedLength: function (value) {
         return (4 + 4 * value.length);
      },
      encode: function (buffer, value) {
         var idx = 0, length = value.length;
         buffer.writeInt (length);
         while (idx < length) {
            buffer.writeInt (value[idx++]);
         }
      },
      decode: function (buffer) {
         var ints = [], idx = 0;
         var length = buffer.readInt ();
         while (idx < length) {
            ints[idx++] = buffer.readInt ();
         }
         return ints;
      }
   };
   Codec.Type.INTEGER_MATRIX = {
      getEncodedLength: function (value) {
         var length = 4 + 4;
         if (value.length > 0) {
            length += 4 * value.length * value[0].length;
         }
         return length;
      },
      encode: function (buffer, value) {
         var width = value.length;
         var height = (value.length > 0 ? value[0].length : 0);
         var row = 0, col = 0;
         buffer.writeInt (width).writeInt (height);
         for (row = 0; row < height; ++row) {
            for (col = 0; col < width; ++col) {
               buffer.writeInt (value[row][col]);
            }
         }
      },
      decode: function (buffer) {
         var width = buffer.readInt ();
         var height = buffer.readInt ();
         var ints = [], row = 0, col = 0;
         for (row = 0; row < height; ++row) {
            ints[row] = [];
            for (col = 0; col < width; ++col) {
               ints[row][col] = buffer.readInt ();
            }
         }
         return ints;
      }
   };
   Codec.Type.BOOLEAN = {
      getEncodedLength: function (value) {
         return 1;
      },
      encode: function (buffer, value) {
         buffer.writeByte (value ? 1 : 0);
      },
      decode: function (buffer) {
         return (buffer.readByte () != 0);
      }
   };
   Codec.Type.STRING = {
      getEncodedLength: function (value) {
         return (4 + 2 * value.length);
      },
      encode: function (buffer, value) {
         var idx = 0, length = value.length;
         buffer.writeInt (length);
         while (idx < length) {
            buffer.writeChar (value.charCodeAt (idx++));
         }
      },
      decode: function (buffer) {
         var chars = [], idx = 0, length = buffer.readInt ();
         while (idx < length) {
            chars[idx++] = buffer.readChar ();
         }
         return String.fromCharCode.apply (null, chars);
      }
   };

   ////////////////////////////////////////////////////////////////////////////
   //  CommandFactory
   function CommandFactory () {}
   CommandFactory.createByName = function (name, args) {
      if (typeof name !== "string") {
         throw new Error ("Given command name is incorrect.");
      }
      return CommandFactory.createBySpec (SPEC.COMMAND[name], args);
   };
   CommandFactory.createBySpec = function (spec, args) {
      if (typeof spec !== "object" ||
         typeof spec.PARAMETERS !== "object") {
         throw new Error ("Given command spec is incorrect.");
      }
      if (typeof args.length !== "number" ||
         typeof args.push !== "function") {
         throw new Error ("Given arguments is not an array.");
      }
      if (spec.DIRECTION !== SPEC.DIRECTION.OUTCOMING) {
         throw new Error ("Can't create an incoming command " + spec.NAME);
      }
      if (spec.PARAMETERS.length !== args.length) {
         throw new Error
         ("Incorrect arguments length, " +
             "expected " + spec.PARAMETERS.length + ", " +
             "got " + args.length);
      }
      return CommandFactory.createInternal (spec, args);
   };
   CommandFactory.createInternal = function (spec, args) {
      return { name: "Command", spec: spec, args: args };
   };

   ////////////////////////////////////////////////////////////////////////////
   //  Public                                                                //
   ////////////////////////////////////////////////////////////////////////////

   function im_initialize () {
      return self;
   }

   self.name = "Rpc";

   self.Codec = {
      willDecode: Codec.willDecode,
      decode: Codec.decode,
      encode: Codec.encode
   };

   self.CommandFactory = {
      create: CommandFactory.createByName
   };

   self.loadSpec = function (spec) {
      if (typeof spec !== "object") {
         throw new Error ("Given spec isn't an object.");
      }
      pm_initSpec (spec);
   };

   return im_initialize ();

   ////////////////////////////////////////////////////////////////////////////
   //  Private                                                               //
   ////////////////////////////////////////////////////////////////////////////

   function pm_initSpec (spec) {
      lm_replaceRefs (spec);

      SPEC = spec;

      /////////////////////////////////////////////////////////////////////////
      //  lm_replaceRefs
      function lm_replaceRefs (source) {
         if (typeof source === "object") {
            for (var prop in source) {
               if (source.hasOwnProperty (prop)) {
                  var child = source[prop];
                  if (lm_isRef (child)) {
                     source[prop] = lm_resolveRef (child, spec);
                  }
                  else {
                     lm_replaceRefs (child);
                  }
               }
            }
         }
      }

      /////////////////////////////////////////////////////////////////////////
      //  lm_isRef
      function lm_isRef (ref) {
         return (typeof ref === "object" && ref.type === "REFERENCE");
      }

      /////////////////////////////////////////////////////////////////////////
      //  lm_resolveRef
      function lm_resolveRef (ref, root) {
         var dst = root, path = ref.path;
         for (var idx = 0, len = path.length; idx < len; ++idx) {
            dst = dst[path[idx]];
         }
         if (typeof dst === "undefined") {
            throw new Error ("Unknown ref: " + ref.path);
         }
         if (dst === root) {
            throw new Error ("Empty ref.");
         }
         return dst;
      }
   }
}
