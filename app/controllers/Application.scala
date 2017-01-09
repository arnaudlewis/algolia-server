package controllers

import javax.inject._
import play.api._
import play.api.Logger
import play.api.mvc._
import play.api.libs.json._
import org.joda.time.DateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import services.db.ProbeEventRepo
import models.ProbeEvent

@Singleton
class Application @Inject() (val probeEventRepo: ProbeEventRepo) extends Controller {

  def index = Action.async {
    for {
      origins <- probeEventRepo.getOrigins
    } yield {
      Ok(views.html.index(Json.toJson(origins)))
    }
  }

  val timeTransferWriter: Writes[(Long, DateTime)] = Writes { timeReport =>
    Json.obj(
      "avg_transfer_time" -> timeReport._1,
      "date" -> timeReport._2.toString
    )
  }

  def reportByOrigin(origin: String) = Action.async {
    probeEventRepo.aggregateTimeTransferFor(origin).map { timeTransfer =>
      Ok(Json.toJson(timeTransfer)(Writes.seq(timeTransferWriter)))
    }
  }

  def reportProbeEvent() = Action.async(parse.json) { implicit request =>
    ProbeEvent.create(request.body) match {
      case Some(probeEvent) =>
        probeEventRepo.insert(probeEvent).map { _ =>
          Ok
        }.recover {
          case e : Exception =>
            Logger.error(e.getMessage)
            BadRequest("Unable to insert Probe event")
        }
      case None => Future successful BadRequest(s"Unable to create ProbeEvent from ${request.body}")
    }
  }

}
