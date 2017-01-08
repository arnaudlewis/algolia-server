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

  def getOrigins : Future[Set[String]] = {
    for {
      coll <- collection
      R <- coll.distinct[String, Set]("origin")
    } yield R
  }

  def aggregateTimeTransferFor(origin: String, duration: Option[Int]) = {
    Future successful ???
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


// #Mongo aggregation query

// {"$match" : {
//   $and: [
//      {"origin" : "sdn-probe-tokyo"},
//      {"created_at": {gt: ISODate("2015-08-10T22:00:00Z")}},
//    ]
// }}

// {
//  $project:
//    {
//     "_id" : 0,
//     "transfer_time_ms" : 1,
//     "hour": {$hour: "$created_at"},
//     "day": {$dayOfMonth: "$created_at"},
//     "month": {$month: "$created_at"},
//     "year": {$year: "$created_at"}
//    }
// }

// { $group : {
//   _id : {hour: "$hour", day: "$day", month: "$month", year: "$year"},
//   avg_transfer_time: { $avg: "$transfer_time_ms" }
// }}

// db.probeevents.aggregate([{"$match" : {$and: [{"origin" : "sdn-probe-tokyo"}, {"created_at": {"$gte": ISODate("2015-08-10T23:00:00Z")}}, ] }},{$project: {"_id" : 0, "transfer_time_ms" : 1, "hour": {$hour: "$created_at"}, "day": {$dayOfMonth: "$created_at"}, "month": {$month: "$created_at"}, "year": {$year: "$created_at"} } }, { $group : {_id : {hour: "$hour", day: "$day", month: "$month", year: "$year"}, avg_transfer_time: { $avg: "$transfer_time_ms" } }}])
