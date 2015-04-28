package view

import config.{MyProperties, Config}
import controller.{Barriere, InterfaceKit}
import data.DataAdd
import org.json.JSONObject

import scala.util.{Failure, Success}

/**
 * Objet et méthode principale du projet permettant de lancer l'application en mode console ou fenêtre
 */
object Main
{
  def main(args:Array[String])
  {
    DataAdd.auth() match
    {
      case Success(rep) =>
      {
          Config.token = new JSONObject(rep).getString("access_token")

          if(MyProperties.CONTEXT == 0)
            new UserInterfaceDisplay().initialize()
          else
            new ConsoleDisplay().initialize()
      }
      case Failure(exc) => UtilConsole.showMessage(exc.getMessage, getClass.getName, "ERROR_MESSAGE")
    }
  }
}
