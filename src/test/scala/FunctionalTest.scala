import Simulations.Experiment2.createCloudlets
import Simulations.Paas.{config, createHost, createVm}
import Simulations.Iaas.{config, createHost, createVm}
import com.typesafe.config.{Config, ConfigFactory}
import org.cloudbus.cloudsim.cloudlets.Cloudlet
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.mutable.ListBuffer

class FunctionalTest extends AnyFlatSpec with Matchers{
  val config = ConfigFactory.load("application.conf")
  it should "Number of Hosts created should be same as per the config in Paas" in{
    val num_hosts = config.getString("Paas.CloudProviderProperties.datacenter"+1+".hosts").toInt
    val hostList = (1 to num_hosts).map{i=>Simulations.Paas.createHost(i)}.toList
    assert(hostList.size.equals(num_hosts))
  }

  it should "Number of Vms created should be same as per the config in Paas" in{
    val vms = config.getString("Paas.vm").toInt
    val virtualMachine1 = (1 to vms).map{i=>Simulations.Paas.createVm(i,1)}.toList
    assert(virtualMachine1.size.equals(vms))
  }

  it should "Number of Hosts created should be same as per the config in Iaas" in{
    val num_hosts = config.getString("Iaas.CloudProviderProperties.datacenter"+1+".hosts").toInt
    val hostList = (1 to num_hosts).map{i=>Simulations.Iaas.createHost(i)}.toList
    assert(hostList.size.equals(num_hosts))
  }

  it should "Number of Vms created should be same as per the config in Iaas" in{
    val vms = config.getString("Iaas.vm").toInt
    val virtualMachine1 = (1 to vms).map{i=>Simulations.Iaas.createVm(i,1)}.toList
    assert(virtualMachine1.size.equals(vms))
  }

  it should "Number of Hosts created should be same as per the config in Saas" in{
    val num_hosts = config.getString("Saas.CloudProviderProperties.datacenter"+1+".hosts").toInt
    val hostList = (1 to num_hosts).map{i=>Simulations.Iaas.createHost(i)}.toList
    assert(hostList.size.equals(num_hosts))
  }

  it should "Number of Vms created should be same as per the config in Saas" in{
    val vms = config.getString("Saas.vm").toInt
    val virtualMachine1 = (1 to vms).map{i=>Simulations.Iaas.createVm(i,1)}.toList
    assert(virtualMachine1.size.equals(vms))
  }
  
}
