import HelperUtils.{CreateLogger}
import Simulations.{Experiment1,Saas,Experiment2,Experiment3,Experiment4,Experiment4A,Paas,Iaas}
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

object Simulation:
  val logger = CreateLogger(classOf[Simulation])

  @main def runSimulation =
    logger.info("Constructing a cloud model...")
    //BasicCloudSimPlusExample.Start()
    Experiment2.Start()
    logger.info("Finished cloud simulation...")

class Simulation