package services.db

import models.ProbeEvent
import javax.inject._
import play.api.Logger
import play.api.libs.json._
import play.api.libs.functional.syntax._
import org.joda.time.DateTime
import play.modules.reactivemongo.json._
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.Future

import common.Algolia.Exceptions.ProbeEventException

class ProbeEventRepo @Inject() (val db: DB) {
  private val collection: Future[JSONCollection] = db.getCollection("probeevents")

  def getOrigins : Future[Set[String]] = {
    for {
      coll <- collection
      R <- coll.distinct[String, Set]("origin")
    } yield R
  }

  def aggregateTimeTransferFor(origin: String) : Future[Seq[(Long, DateTime)]] = {
    collection.flatMap { coll =>
      import coll.BatchCommands.AggregationFramework._

      val matchCommand = Match(Json.obj("origin" -> origin))

      val projectCommand = Project(Json.obj(
        "_id" -> 0,
        "transfer_time_ms" -> 1,
        "hour" -> Json.obj("$hour" -> "$created_at"),
        "day" -> Json.obj("$dayOfMonth" -> "$created_at"),
        "month" -> Json.obj("$month" -> "$created_at"),
        "year" -> Json.obj("$year" -> "$created_at")
      ))

      val groupCommand = Group(
        Json.obj(
          "hour" -> "$hour",
          "day" -> "$day",
          "month" -> "$month",
          "year" -> "$year"
        )
      )("avg_transfer_time" -> Avg("transfer_time_ms"))

      val reader: Reads[(Long, DateTime)] = (
        (__ \ "avg_transfer_time").read[JsNumber].map(_.value.toLong) and
        (
          (__ \ "_id" \ "hour").read[Int] and
          (__ \ "_id" \ "day").read[Int] and
          (__ \ "_id" \ "month").read[Int] and
          (__ \ "_id" \ "year").read[Int]
        ).tupled.map {
          case (hour, day, month, year) => new DateTime(year, month, day, hour, 0)
        }
      ).tupled

      for {
        res <- coll.aggregate(matchCommand, List(projectCommand, groupCommand))
      } yield {
        println(res)
        val toto = res.head(reader)
        println(toto)
        toto
      }
    }
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
