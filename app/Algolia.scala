package common

import javax.inject._
import play.api._
import play.api.Play.current

object Algolia {
  object Exceptions {
    class ProbeEventException(msg: String) extends Exception(msg)
  }
}
