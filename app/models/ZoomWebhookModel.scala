package models

import play.api.libs.json.{Json, OFormat}

import java.time.{Instant, OffsetDateTime}

object ZoomWebhookModel {
  case class Event(event: String, payload: Payload, event_ts: Instant)

  case class Payload(account_id: String, `object`: Object)

  case class Object(start_time: OffsetDateTime,
                    end_time: Option[OffsetDateTime], // present for "meeting.ended" event
                    topic: String,
                    id: String,
                    uuid: String,
                    host_id: String)

  implicit val objectFormat: OFormat[Object] = Json.format
  implicit val payloadFormat: OFormat[Payload] = Json.format
  implicit val eventFormat: OFormat[Event] = Json.format
}
