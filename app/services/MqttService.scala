package services

import com.hivemq.client.mqtt.datatypes.{MqttQos, MqttTopic}
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish
import models.ZoomWebhookModel
import play.api.libs.json.Json

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.FutureConverters._
import scala.jdk.OptionConverters._

@Singleton
class MqttService @Inject()(implicit ec: ExecutionContext) {
  import MqttService._

  private val mqttClient = Mqtt5Client.builder().serverHost(mqttHost).buildAsync()

  def publish(zoomEvent: ZoomWebhookModel.Event): Future[Unit] = {
    val topic = MqttTopic.of(s"$topicPrefix/${zoomEvent.payload.account_id}/${zoomEvent.event}")
    val payload = Json.toJson(zoomEvent).toString().getBytes
    val publishRequest = Mqtt5Publish.builder().topic(topic).qos(MqttQos.AT_LEAST_ONCE).payload(payload).build()
    for {
      _ <- mqttClient.connect().asScala
      publishResult <- mqttClient.publish(publishRequest).asScala
      _ = publishResult.getError.toScala.foreach(throw _)
      _ <- mqttClient.disconnect().asScala
    } yield ()
  }
}

object MqttService {
  private val mqttHost = sys.env.getOrElse("ZWM_MQTT_HOST", "localhost")
  private lazy val topicPrefix = "zoom-webhook"
}
