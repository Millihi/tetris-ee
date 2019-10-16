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

package projects.milfie.tetris.dao;

import projects.milfie.tetris.domain.Result;

import java.util.List;
import javax.enterprise.context.RequestScoped;

@RequestScoped
class ResultDAOImpl
   extends AbstractDAOImpl<Long, Result>
   implements ResultDAO
{

   ResultDAOImpl () {
      super (Result.class);
   }

   @Override
   public List<Result> findAll () {
      return
         entityManager
            .createNamedQuery (Result.QUERY_FIND_ALL, entityClass)
            .getResultList ();
   }
}
