import HelperUtils.{CreateLogger}
import Simulations.{Experiment1,Saas,Experiment2,Experiment3,Paas,Iaas}
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

object Simulation:
  val logger = CreateLogger(classOf[Simulation])

  def main(args: Array[String]): Unit = {
    val inputSelection= args(0).toInt
    logger.info("Constructing a cloud model...")
    inputSelection match {
      case 1 => Experiment1.Start()
      case 2 => Experiment2.Start()
      case 3 => Experiment3.Start()
      case 4 => Saas.Start()
      case 5 => Paas.Start()
      case 6 => Iaas.Start()
      case default => println("Please select the correct choice of input from 1 to 6")
    }
    logger.info("Finished cloud simulation...")
  }

class Simulation