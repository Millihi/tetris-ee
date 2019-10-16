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
//  Canvas
function Canvas (di_model) {

   var
      self = this,
      pv_cells = null,
      pv_width = 0,
      pv_height = 0;

   ////////////////////////////////////////////////////////////////////////////
   //  Public                                                                //
   ////////////////////////////////////////////////////////////////////////////

   function im_initialize () {
      if (typeof di_model !== "object" || di_model.name !== "Model") {
         throw new Error ("Invalid Model.");
      }
      if (typeof document !== "object") {
         throw new Error ("No document.");
      }

      pv_cells = [];

      var container = document.querySelector ("TABLE.canvas");
      var trs = container.querySelectorAll ("TR");

      for (var y = trs.length - 1, yl = -1; y > yl; --y) {
         var cells = [];
         var tds = trs[y].querySelectorAll (".cell-inner");

         for (var x = 0, xl = tds.length; x < xl; ++x) {
            cells.push (tds[x]);
         }

         pv_cells.push (cells);
      }

      pv_width = pv_cells[0].length;
      pv_height = pv_cells.length;

      if (pv_width <= 0 || pv_height <= 0) {
         throw new Error ("Dimensions are negative.");
      }

      return self;
   }

   self.name = "Canvas";

   self.updateRegion = function (x0, y0, bytes) {
      if (x0 < 0 || x0 >= pv_width) {
         throw new Error ("Given x0 is out of range.");
      }
      if (y0 < 0 || y0 >= pv_height) {
         throw new Error ("Given y0 is out of range.");
      }
      if (bytes.length <= 0 || bytes[0].length <= 0) {
         throw new Error ("Given matrix has zero component.");
      }

      var xl = x0 + bytes[0].length, yl = y0 + bytes.length;

      if (yl > pv_height || xl > pv_width) {
         throw new Error ("Given matrix is out of range.");
      }

      var x, _x, y, _y, color;

      for (y = y0, _y = 0; y < yl; ++y, ++_y) {
         for (x = x0, _x = 0; x < xl; ++x, ++_x) {
            color = (bytes[_y][_x] === 0 ? "white" : "black");
            pv_cells[y][x].setAttribute ("class", color);
         }
      }
   };

   return im_initialize ();
}
