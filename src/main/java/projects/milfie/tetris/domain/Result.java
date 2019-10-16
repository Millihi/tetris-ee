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

package projects.milfie.tetris.domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Entity
@Table (name = "RESULTS")
@NamedQueries (value = {
   @NamedQuery (
      name = Result.QUERY_FIND_ALL,
      query = "SELECT r FROM Result r"
   )
})
public class Result
   implements Serializable
{
   ////////////////////////////////////////////////////////////////////////////
   //  Public static section                                                 //
   ////////////////////////////////////////////////////////////////////////////

   public static final String
      QUERY_FIND_ALL = "Result.findAll";

   public static final String
      NAME_PATTERN = "^[a-zA-Z][a-zA-Z0-9]*([ _-][a-zA-Z0-9]+)*$",
      NAME_DEFAULT = "Unnamed";

   public static final long MIN_SCORE = 0L;
   public static final int  MIN_LEVEL = 1;

   public static class Builder {
      /////////////////////////////////////////////////////////////////////////
      //  Public section                                                     //
      /////////////////////////////////////////////////////////////////////////

      public Builder setName (final String name) {
         this.name = name;
         return this;
      }

      public Builder setDate (final Date date) {
         this.date = date;
         return this;
      }

      public Builder setScore (final long score) {
         this.score = score;
         return this;
      }

      public Builder setLevel (final int level) {
         this.level = level;
         return this;
      }

      public Result build () {
         return new Result (this);
      }

      /////////////////////////////////////////////////////////////////////////
      //  Private section                                                    //
      /////////////////////////////////////////////////////////////////////////

      private String name;
      private Date   date;
      private long   score;
      private int    level;
   }

   public static Builder newBuilder () {
      return new Builder ();
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   ////////////////////////////////////////////////////////////////////////////
   //  Id
   @Id
   @GeneratedValue
      (generator = "ResultIdSeq",
       strategy = GenerationType.SEQUENCE)
   @SequenceGenerator
      (name = "ResultIdSeq",
       sequenceName = "RESULT_ID_SEQ",
       initialValue = 1,
       allocationSize = 1)
   @Column (name = "ID", nullable = false)
   private long id;

   public long getId () {
      return id;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Name
   @NotNull
   @Pattern (regexp = Result.NAME_PATTERN)
   @Column (name = "NAME")
   private String name = Result.NAME_DEFAULT;

   public String getName () {
      return name;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Date
   @Temporal (TemporalType.TIMESTAMP)
   @Column (name = "DATE", nullable = false)
   private Date date;

   public Date getDate () {
      return date;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Score
   @Min (Result.MIN_SCORE)
   @Column (name = "SCORE", nullable = false)
   private long score = Result.MIN_SCORE;

   public long getScore () {
      return score;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Level
   @Min (Result.MIN_LEVEL)
   @Column (name = "LEVEL", nullable = false)
   private int level = Result.MIN_LEVEL;

   public int getLevel () {
      return level;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   protected Result () {
   }

   protected Result (final Builder builder) {
      this.name = builder.name;
      this.date = builder.date;
      this.score = builder.score;
      this.level = builder.level;
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private static section                                                //
   ////////////////////////////////////////////////////////////////////////////

   private static final long serialVersionUID = 201909251624L;
}
