package models
import org.joda.time.{DateTime, Seconds}
import reactivemongo.bson._
import scala.concurrent.duration._
import play.api.libs.json._

object `package` {

  implicit object JSONDurationHandler extends Format[Duration] {
    def reads(jsDuration: play.api.libs.json.JsValue): JsResult[Duration] =
      jsDuration.asOpt[Long] match {
        case Some(durationL) => JsSuccess(Duration(durationL, MILLISECONDS))
        case None => JsError()
      }

    def writes(duration: Duration): JsValue = JsNumber(duration.toMillis)
  }
}
