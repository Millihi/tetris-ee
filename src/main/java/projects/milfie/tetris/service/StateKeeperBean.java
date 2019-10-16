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

package projects.milfie.tetris.service;

import projects.milfie.tetris.domain.Result;

import java.io.Serializable;
import java.util.logging.Logger;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.StatefulTimeout;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Stateful
@StatefulTimeout (-1)
public class StateKeeperBean
   implements Serializable
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public static section                                                 //
   ////////////////////////////////////////////////////////////////////////////

   public static final String NAME =
      StateKeeperBean.class.getSimpleName ();

   public static final String DEFAULT_NAME   = Result.NAME_DEFAULT;
   public static final int    DEFAULT_WIDTH  = 10;
   public static final int    DEFAULT_HEIGHT = 15;

   public static final String NAME_PATTERN = Result.NAME_PATTERN;

   public static StateKeeperBean obtainFromJNDI () {
      try {
         return (StateKeeperBean)
            new InitialContext ().lookup ("java:module/" + NAME);
      }
      catch (final NamingException e) {
         return null;
      }
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public void resetTemporal () {
      error = null;
      score = Result.MIN_SCORE;
      level = Result.MIN_LEVEL;
   }

   @Remove
   public void remove () {
      LOG.info ("Destroyed state @" + Integer.toHexString (this.hashCode ()));
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Name
   @NotNull
   @Pattern (regexp = NAME_PATTERN)
   private String name = DEFAULT_NAME;

   public String getName () {
      return name;
   }

   public void setName (final String name) {
      this.name = name;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Width
   @NotNull
   @Min (5)
   @Max (20)
   private int width = DEFAULT_WIDTH;

   public int getWidth () {
      return width;
   }

   public void setWidth (final int width) {
      this.width = width;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Height
   @NotNull
   @Min (10)
   @Max (40)
   private int height = DEFAULT_HEIGHT;

   public int getHeight () {
      return height;
   }

   public void setHeight (final int height) {
      this.height = height;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Score
   @Min (Result.MIN_SCORE)
   private long score = Result.MIN_SCORE;

   public long getScore () {
      return score;
   }

   public void setScore (final long score) {
      this.score = score;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Level
   @Min (Result.MIN_LEVEL)
   private int level = Result.MIN_LEVEL;

   public int getLevel () {
      return level;
   }

   public void setLevel (final int level) {
      this.level = level;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Error
   private String error = null;

   public boolean hasError () {
      return (error != null && !error.isEmpty ());
   }

   public String getError () {
      return error;
   }

   public void setError (final String error) {
      this.error = error;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final long serialVersionUID = 201908252037L;

   private static final Logger LOG =
      Logger.getLogger (StateKeeperBean.class.getSimpleName ());
}
