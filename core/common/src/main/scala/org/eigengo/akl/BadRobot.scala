package org.eigengo.akl

import scalaz.{Failure, Success, Validation}

object BadRobot {
  val constUnit: Any ⇒ Unit = const(())

  def const[A, U](c: ⇒ U)(ignored: A): U = c

  implicit class ValidationOps[E, A](v: Validation[E, A]) {
    lazy val ! = v match {
      case Success(a) ⇒ a
      case Failure(e) ⇒ throw new IllegalArgumentException(s"Value $v is error $e.")
    }

  }

}
