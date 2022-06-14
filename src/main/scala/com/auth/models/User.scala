package com.auth.models
import scala.collection.immutable
import spray.json.DefaultJsonProtocol
import spray.json.RootJsonFormat
import spray.json.JsValue
import spray.json.JsObject
import spray.json.JsString
import spray.json.NullOptions


final case class UserWithPassword(
    id: Long,
    password: String,
    firstName: Option[String],
    lastName: Option[String],
    username: String,
    email: String
) {
    def toUser() = User(id, firstName, lastName, username, email)
}

final case class User(
    id: Long,
    firstName: Option[String],
    lastName: Option[String],
    username: String,
    email: String
) 

final case class PostUser(
    firstName: Option[String],
    lastName: Option[String],
    username: String,
    email: String,
    password: String
) {
    def toUserWithPassword(id: Long) = UserWithPassword(id, password, firstName, lastName,username, email)
}

object UserFormats extends DefaultJsonProtocol with NullOptions {
  implicit val dtoUserJsonFormat = jsonFormat6(UserWithPassword)
  implicit val postUserJsonFormat = jsonFormat5(User)
  implicit val getUserJsonFormat = jsonFormat5(PostUser)
}
