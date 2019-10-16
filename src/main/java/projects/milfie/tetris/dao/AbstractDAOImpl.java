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

import java.io.Serializable;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

abstract class AbstractDAOImpl<K, E extends Serializable>
   implements AbstractDAO<K, E>
{

   ////////////////////////////////////////////////////////////////////////////
   //  Public section                                                        //
   ////////////////////////////////////////////////////////////////////////////

   public AbstractDAOImpl (final Class<E> entityClass) {
      this.entityClass = entityClass;
   }

   @Override
   public E find (final K key) {
      return entityManager.find (entityClass, key);
   }

   @Override
   public E update (final E entity) {
      return entityManager.merge (entity);
   }

   @Override
   public void refresh (final E entity) {
      entityManager.refresh (entity);
   }

   @Override
   public void persist (final E entity) {
      entityManager.persist (entity);
   }

   @Override
   public void remove (final E entity) {
      if (entityManager.contains (entity)) {
         entityManager.remove (entity);
      }
      else {
         entityManager.remove (entityManager.merge (entity));
      }
   }

   ////////////////////////////////////////////////////////////////////////////
   //  Private section                                                       //
   ////////////////////////////////////////////////////////////////////////////

   @PersistenceContext (unitName = "Tetris_EEPU")
   protected EntityManager entityManager;
   protected Class<E>      entityClass;
}
