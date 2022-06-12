package models

import play.api.libs.json.{JsObject, Json, OFormat}

import java.time.Instant

object ZoomWebhookModel {
  case class Event(event: String, payload: Payload, event_ts: Instant)

  case class Payload(account_id: String, `object`: JsObject)

  implicit val payloadFormat: OFormat[Payload] = Json.format
  implicit val eventFormat: OFormat[Event] = Json.format
}
