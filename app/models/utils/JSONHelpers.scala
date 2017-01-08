package models
import org.joda.time.{DateTime, DateTimeZone, Seconds}
import org.joda.time.format.DateTimeFormat
import reactivemongo.bson._
import scala.concurrent.duration._
import play.api.libs.json._

object `package` {

  val dateTimeFormat = "yyyy-MM-dd HH:mm:ss z"
  val formatter = DateTimeFormat.forPattern(dateTimeFormat)

  implicit val dateTimeWriter = new Writes[DateTime] {
    def writes(datetime: DateTime): JsValue = {
      Json.obj("$date" -> datetime.getMillis)
    }
  }
  implicit val JSONDateTimeReader = new Reads[DateTime] {
    def reads(datetime: JsValue): JsResult[DateTime] = {
      datetime.validate[String].map(d => formatter.parseDateTime(d))
    }
  }


  implicit object JSONDurationHandler extends Format[Duration] {
    def reads(jsDuration: play.api.libs.json.JsValue): JsResult[Duration] =
      jsDuration.asOpt[Long] match {
        case Some(durationL) => JsSuccess(Duration(durationL, MILLISECONDS))
        case None => JsError()
      }
    def writes(duration: Duration): JsValue = JsNumber(duration.toMillis)
  }
}
