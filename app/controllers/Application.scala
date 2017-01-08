package controllers

import javax.inject._
import play.api._
import play.api.Logger
import play.api.mvc._
import play.api.libs.json._

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

  // val timeTransferWriter: Writes[Seq[(Int, Long)]] = Writes { timeList =>
  //   JsArray(timeList.map { t =>
  //     Json.obj(t._1 -> t._2)
  //   })
  // }

  def reportByOrigin(origin: String, duration: Option[Int]) = Action.async {
    probeEventRepo.aggregateTimeTransferFor(origin, duration).map { timeTransfer =>
      //seq[(hour, timeTransfer)]
      // Ok(timeTransferWriter.writes(timeTransfer))
      Ok
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
