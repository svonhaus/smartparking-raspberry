package view

import java.text.SimpleDateFormat
import java.util.Date

/**
 * Created by Steven on 25-04-15.
 */
object UtilConsole
{
  def showMessage(message:String, titre:String, typeMessage:String): Unit =
  {
    val temp0 = "[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) +  "]";

    val temp1 = typeMessage match
    {
      case "INFORMATION_MESSAGE" => temp0 ++ "[INFO] "
      case "ERROR_MESSAGE" => temp0 ++ "[ERROR] "
      case "WARNING_MESSAGE" => temp0 ++ "[WARNING] "
      case _ => temp0 ++ "[INFO] "
    }

    println(temp1 + titre + ": " + message)
  }
}
