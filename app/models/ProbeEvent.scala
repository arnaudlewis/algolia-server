package models

import scala.concurrent.duration.Duration
import play.api.Logger
import play.api.libs.functional.syntax._
import play.api.libs.json._
import reactivemongo.bson.{BSONObjectID, _}
import org.joda.time.DateTime


 // "origin": "sdn-probe-moscow", // name of the probe
 //    "name_lookup_time_ms": 203,
 //    "connect_time_ms": 413,
 //    "transfer_time_ms": 135,
 //    "total_time_ms": 752,
 //    "created_at": "2015-08-10 21:52:21 UTC",
 //    "status": 200

case class ProbeEvent(
  _id : String,
  origin: String,
  nameLookTime: Duration,
  connectTime: Duration,
  transferTime: Duration,
  totalTime: Duration,
  createdAt: DateTime,
  status: Int
)

object ProbeEvent {

  implicit def eventReader : Reads[ProbeEvent] = (
    (__ \ "_id").readNullable[String].map(_.getOrElse(java.util.UUID.randomUUID().toString)) and
    (__ \ "origin").read[String] and
    (__ \ "name_lookup_time_ms").read[Duration] and
    (__ \ "connect_time_ms").read[Duration] and
    (__ \ "transfer_time_ms").read[Duration] and
    (__ \ "total_time_ms").read[Duration] and
    (__ \ "created_at").read[DateTime] and
    (__ \ "status").read[Int]
  )(ProbeEvent.apply _)

  implicit def eventWriter : OWrites[ProbeEvent] = (
    (__ \ "_id").write[String] and
    (__ \ "origin").write[String] and
    (__ \ "name_lookup_time_ms").write[Duration] and
    (__ \ "connect_time_ms").write[Duration] and
    (__ \ "transfer_time_ms").write[Duration] and
    (__ \ "total_time_ms").write[Duration] and
    (__ \ "created_at").write[DateTime] and
    (__ \ "status").write[Int]
  )(unlift(ProbeEvent.unapply _))

  def create(jsEvent: JsValue) : Option[ProbeEvent] = {
    eventReader.reads(jsEvent) match {
      case JsSuccess(event, _) => Some(event)
      case e: JsError =>
        println(e)
        None
    }
  }
}
