import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ConfigTest extends AnyFlatSpec with Matchers{
  val config = ConfigFactory.load("application.conf")
 it should "Experiment 2, host config should be present for each host" in{
   val num_hosts = config.getString("Experiment2.CloudProviderProperties.datacenter.hosts").toInt
   try {
     (1 to num_hosts).map(i => {
       val scheduler = config.getString("Experiment2.CloudProviderProperties.host" + i + ".schedule")
     })
   }catch {
     case e : Exception => assert(false)
   }
 }

  it should "Experiment 2, Cloudlet should have unique config" in{
    val cloudletnum = config.getString("Experiment2.BrokerProperties.cloudletCount").toInt
    try {
      (1 to cloudletnum).map(i => {
        val cloudlet_Pes = config.getString("Experiment2.BrokerProperties.cloudlet"+i+".pes").toInt
        val a = 1
      })
    }catch {
      case e : Exception => assert(false)
    }
  }

  it should "Experiment 3, host config should be present for each host" in{
    val num_hosts = config.getString("Experiment3.CloudProviderProperties.datacenter.hosts").toInt
    try {
      (1 to num_hosts).map(i => {
        val scheduler = config.getString("Experiment3.CloudProviderProperties.host" + i + ".schedule")
      })
    }catch {
      case e : Exception => assert(false)
    }
  }
  it should "Saas, Cloudlet should have unique config" in{
    val cloudletnum = config.getString("Saas.BrokerProperties.cloudletCount").toInt
    try {
      (1 to cloudletnum).map(i => {
        val cloudlet_Pes = config.getString("Saas.BrokerProperties.cloudlet"+i+".pes").toInt
        val a = 1
      })
    }catch {
      case e : Exception => assert(false)
    }
  }
  it should "Paas, Cloudlet should have unique config" in{
    val cloudletnum = config.getString("Paas.BrokerProperties.cloudletCount").toInt
    try {
      (1 to cloudletnum).map(i => {
        val cloudlet_Pes = config.getString("Paas.BrokerProperties.cloudlet"+i+".pes").toInt
        val a = 1
      })
    }catch {
      case e : Exception => assert(false)
    }
  }
  it should "Iaas, Cloudlet should have unique config" in{
    val cloudletnum = config.getString("Iaas.BrokerProperties.cloudletCount").toInt
    try {
      (1 to cloudletnum).map(i => {
        val cloudlet_Pes = config.getString("Iaas.BrokerProperties.cloudlet"+i+".pes").toInt
        val a = 1
      })
    }catch {
      case e : Exception => assert(false)
    }
  }

}
