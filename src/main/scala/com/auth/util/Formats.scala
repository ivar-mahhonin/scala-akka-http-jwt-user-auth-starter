package com.auth.util

import spray.json.DefaultJsonProtocol
import spray.json.NullOptions

class Formats[T](fieldAmount: Int) extends DefaultJsonProtocol with NullOptions {
}
