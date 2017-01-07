package services.db

import reactivemongo.api.{DefaultDB, MongoConnection}
import reactivemongo.play.json.collection.JSONCollection


import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import play.modules.reactivemongo.ReactiveMongoApi

import javax.inject._

class DB @Inject() (val reactiveMongoApi: ReactiveMongoApi) {
  def getCollection(name: String): Future[JSONCollection] =
    reactiveMongoApi.database.map(_.collection[JSONCollection](name))
}
