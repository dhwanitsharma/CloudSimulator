import HelperUtils.{CreateLogger, ObtainConfigReference}
import Simulations.{BasicCloudSimPlusExample, Experiment1,Saas,Experiment2,Experiment3,Experiment4,Experiment4A}
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

object Simulation:
  val logger = CreateLogger(classOf[Simulation])

  @main def runSimulation =
    logger.info("Constructing a cloud model...")
    //BasicCloudSimPlusExample.Start()
    Saas.Start()
    logger.info("Finished cloud simulation...")

class Simulation