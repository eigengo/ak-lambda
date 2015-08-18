package org.eigengo.akl

import akka.persistence.PersistentActor

import scalaz.Validation

/**
 * Convenience wrapper around ``Validation[+E, +A]`` which allows the errors and successes to be persisted
 * in the journal
 */
trait PersistentActorValidation {
  actor: PersistentActor ⇒

  val constUnit: Any ⇒ Unit = { _ ⇒ ()}

  /**
   * Adds the ``persist`` and ``persistAsync`` functions that persist the failures and successes
   *
   * @param v the wrapped ``Validation[+E, +A]``
   * @tparam E the left type
   * @tparam A the right type
   */
  implicit class RichValidation[+E, +A](v: Validation[E, A]) {

    /** Persist the L or R */
    def persist(): Unit = {
      v.fold(err ⇒ actor.persist(err)(constUnit), succ ⇒ actor.persist(succ)(constUnit))
    }

    /** Asynchronously persist the L or R */
    def persistAsync(): Unit = {
      v.fold(err ⇒ actor.persistAsync(err)(constUnit), succ ⇒ actor.persistAsync(succ)(constUnit))
    }
  }

}
