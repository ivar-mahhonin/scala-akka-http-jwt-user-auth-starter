package com.auth.registries

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import com.auth.models.{
  UserWithPassword,
  User,
  PostUser,
  GetUsers,
  GetUser,
  CreateUser,
  DeleteUser,
  UserDeleted,
  UserResponse
}
import spray.json.DefaultJsonProtocol._
import spray.json.DefaultJsonProtocol
import spray.json.NullOptions
import com.auth.models.Command

object UserRegistry {
  def apply(): Behavior[Command] = registry(Set.empty)

  private def registry(users: Set[UserWithPassword]): Behavior[Command] =
    Behaviors.receiveMessage {
      case GetUsers(replyTo) =>
        replyTo ! users.map(_.toUser)
        Behaviors.same
      case GetUser(id, replyTo) =>
        replyTo ! UserResponse(users.find(_.id == id).map(_.toUser))
        Behaviors.same
      case CreateUser(user, replyTo) =>
        val newUser = user.toUserWithPassword(users.size)
        replyTo ! UserResponse(Some(newUser.toUser))
        registry(users + newUser)
      case DeleteUser(id, replyTo) =>
        replyTo ! UserDeleted(s"User $id deleted.")
        registry(users.filterNot(_.id == id))
    }
}

object UserRegistryFormats extends DefaultJsonProtocol with NullOptions {
  import com.auth.models.UserFormats._
  implicit val userResponseJsonFormat = jsonFormat1(UserResponse)
  implicit val userDeletedJsonFormat = jsonFormat1(UserDeleted)
}
