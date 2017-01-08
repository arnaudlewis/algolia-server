package services.db

import models.ProbeEvent
import javax.inject._
import play.api.Logger
import play.api.libs.json.{Json}
import play.modules.reactivemongo.json._
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.Future

import common.Algolia.Exceptions.ProbeEventException

class ProbeEventRepo @Inject() (val db: DB) {
  private val collection: Future[JSONCollection] = db.getCollection("probeevents")

  def byId(id: String) : Future[Option[ProbeEvent]] = {
    val query = Json.obj("_id" -> id)
    for {
      coll <- collection
      R <- coll.find(query).one[ProbeEvent]
    } yield R
  }

  def insert(event: ProbeEvent) : Future[Unit] = {
    val writeRes = (for {
      coll <- collection
      R <- coll.insert(event)
    } yield R)

    writeRes.onComplete {
      case Failure(e) => throw new ProbeEventException("Error inserting Probe Event with origin : " + event.origin)
      case Success(writeResult) =>
        Logger.info(s"successfully inserted probeEvent: $writeResult")
    }

    writeRes.map(_ => {})
  }
}
