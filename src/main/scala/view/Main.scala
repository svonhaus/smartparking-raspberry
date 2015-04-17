package view

object Main
{
  def main(args:Array[String])
  {
    val context = 1

    if(context == 0)
      new UserInterfaceDisplay().initialize()
    else
      new ConsoleDisplay().initialize()
  }
}
