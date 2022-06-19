package com.auth.routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route

import scala.concurrent.Future
import com.auth.registries.{UserRegistry}
import com.auth.registries.UserRegistry._
import com.auth.registries.UserRegistryFormats._

import com.auth.models.{
  User,
  PostUser,
  UserFormats,
  GetUser,
  UserResponse,
  CreateUser,
  UserDeleted,
  GetUsers,
  DeleteUser
}

import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout
import com.auth.models.Command

class UserRoutes(userRegistry: ActorRef[Command])(implicit val system: ActorSystem[_]) {
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import UserFormats._

  private implicit val timeout = Timeout.create(
    system.settings.config.getDuration("user-jwt-auth.api.ask-timeout")
  )

  def getUsers(): Future[Set[User]] = userRegistry.ask(GetUsers)
  def getUser(id: Long): Future[UserResponse] = userRegistry.ask(GetUser(id, _))
  def createUser(user: PostUser): Future[UserResponse] = userRegistry.ask(CreateUser(user, _))
  def deleteUser(id: Long): Future[UserDeleted] = userRegistry.ask(DeleteUser(id, _))

  val userRoutes: Route =
    pathPrefix("users") {
      concat(
        pathEnd {
          concat(
            get {
              complete(getUsers())
            },
            post {
              entity(as[PostUser]) { user =>
                onSuccess(createUser(user)) { performed =>
                  complete((StatusCodes.Created, performed))
                }
              }
            }
          )
        },
        path(IntNumber) { id =>
          concat(
            get {
              rejectEmptyResponse {
                onSuccess(getUser(id)) { response =>
                  complete(response.user)
                }
              }
            },
            delete {
              onSuccess(deleteUser(id)) { performed =>
                complete((StatusCodes.OK, performed))
              }
            }
          )
        }
      )
    }
}
