package com.auth.models

import akka.actor.typed.ActorRef

sealed trait Command

final case class UserResponse(user: Option[User])
final case class UserDeleted(deleted: String)

final case class GetUsers(replyTo: ActorRef[Set[User]]) extends Command
final case class CreateUser(user: PostUser, replyTo: ActorRef[UserResponse]) extends Command
final case class GetUser(id: Long, replyTo: ActorRef[UserResponse]) extends Command
final case class DeleteUser(id: Long, replyTo: ActorRef[UserDeleted]) extends Command

