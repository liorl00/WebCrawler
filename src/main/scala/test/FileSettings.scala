package test

import test.FileSettings.Settings

case class FileSettings(fileSettings: Settings)

object FileSettings {

  case class Settings (batchSize: Int,
                       fileName: String)

}