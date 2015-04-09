package controller

/**
 * Created by Steven on 18-03-15.
 */
object Sensors
{
  val functionSharpSensor = (x:Int) =>
  {
    if(x!=20)
    {
      val result = 4800 / (x - 20)
      if(result > 10 && result < 100) Some(result) else None
    }
    else None
  }

  val functionIrSensor = (x:Int) =>
  {
    if(x < 500) Some(1) else Some(0)
  }

  val listAnalogSensors= List(
    (0, (((x:Int) => functionSharpSensor(x)), (3, 200.toLong))), // (indexSensor, (functionToComputeAnalogValue, (treshold, interval)))
    (1, (((x:Int) => functionSharpSensor(x)), (3, 200.toLong))))
}
