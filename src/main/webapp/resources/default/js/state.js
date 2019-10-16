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
//  State
function State (di_model) {

   var
      self = this,
      pv_comboValue = null,
      pv_scoreValue = null,
      pv_levelValue = null,
      pv_brickCells = null,
      pv_brickWidth = null,
      pv_brickHeight = null;

   ////////////////////////////////////////////////////////////////////////////
   //  Public                                                                //
   ////////////////////////////////////////////////////////////////////////////

   function im_initialize () {
      if (typeof di_model !== "object" || di_model.name !== "Model") {
         throw new Error ("Invalid model given.");
      }
      if (!document || !jQuery) {
         throw new Error ("Either of document or jQuery not found.");
      }

      var stateScreen = jQuery (".state-rank");
      pv_comboValue = stateScreen.find (".rank-combo .value");
      pv_scoreValue = stateScreen.find (".rank-score .value");
      pv_levelValue = stateScreen.find (".rank-level .value");

      pv_brickCells = [];

      var container = document.querySelector ("TABLE.state-brick");
      var trs = container.querySelectorAll ("TR");

      for (var y = trs.length - 1, yl = -1; y > yl; --y) {
         var cells = [];
         var tds = trs[y].querySelectorAll (".cell-inner");

         for (var x = 0, xl = tds.length; x < xl; ++x) {
            cells.push (tds[x]);
         }

         pv_brickCells.push (cells);
      }

      pv_brickWidth = pv_brickCells[0].length;
      pv_brickHeight = pv_brickCells.length;

      if (pv_brickWidth <= 0 || pv_brickHeight <= 0) {
         throw new Error ("Dimensions are negative.");
      }

      return self;
   }

   self.name = "State";

   self.setCombo = function (value) {
      pv_comboValue.text (value);
   };

   self.setScore = function (value) {
      pv_scoreValue.text (value);
   };

   self.setLevel = function (value) {
      pv_levelValue.text (value);
   };

   self.setBrick = function (bytes) {
      if (bytes.length <= 0 || bytes[0].length <= 0) {
         throw new Error ("Given matrix has zero component.");
      }

      var
         x0 = (Math.max (0, pv_brickWidth - bytes[0].length) / 2) | 0,
         y0 = (Math.max (0, pv_brickHeight - bytes.length) / 2) | 0;
      var
         x1 = x0 + bytes[0].length - 1,
         y1 = y0 + bytes.length - 1;

      if (y1 >= pv_brickHeight || x1 >= pv_brickWidth) {
         throw new Error ("Given matrix is out of range.");
      }

      var x, y, byte, color;

      for (y = 0; y < pv_brickHeight; ++y) {
         for (x = 0; x < pv_brickWidth; ++x) {
            if (y < y0 || x < x0 || y > y1 || x > x1) {
               pv_brickCells[y][x].setAttribute ("class", "white");
            }
            else {
               byte = bytes[y - y0][x - x0];
               color = (byte === 0 ? "white" : "black");
               pv_brickCells[y][x].setAttribute ("class", color);
            }
         }
      }
   };

   return im_initialize ();
}
