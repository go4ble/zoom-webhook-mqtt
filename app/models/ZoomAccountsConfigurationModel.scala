package models

import com.typesafe.config
import play.api.ConfigLoader

object ZoomAccountsConfigurationModel {
  val ConfigKey = "zoomAccounts"

  type Config = Map[String, ZoomAccountConfiguration]

  case class ZoomAccountConfiguration(verificationToken: String)

  implicit val zoomAccountConfigurationLoader: ConfigLoader[ZoomAccountConfiguration] = (rootConfig: config.Config, path: String) => {
    val config = rootConfig.getConfig(path)
    ZoomAccountConfiguration(config.getString("verificationToken"))
  }
}
