package Simulations

import HelperUtils.CreateLogger
import org.cloudbus.cloudsim.allocationpolicies.{VmAllocationPolicyBestFit, VmAllocationPolicyFirstFit, VmAllocationPolicyRoundRobin, VmAllocationPolicySimple}
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.cloudlets.{Cloudlet, CloudletSimple}
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.{Datacenter, DatacenterSimple}
import org.cloudbus.cloudsim.hosts.HostSimple
import org.cloudbus.cloudsim.resources.{Pe, PeSimple}
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared
import org.cloudbus.cloudsim.schedulers.vm.{VmSchedulerSpaceShared, VmSchedulerTimeShared}
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic
import org.cloudbus.cloudsim.vms.{Vm, VmSimple}
import org.cloudsimplus.builders.tables.CloudletsTableBuilder
import org.cloudbus.cloudsim.hosts.network.NetworkHost
import org.cloudbus.cloudsim.network.topologies.{BriteNetworkTopology, NetworkTopology}
import org.cloudbus.cloudsim.network.switches.EdgeSwitch
import org.cloudbus.cloudsim.resources.Ram
import org.cloudsimplus.autoscaling.VerticalVmScalingSimple

import collection.mutable.*
import scala.jdk.CollectionConverters.*
import scala.collection.mutable.ListBuffer
import com.typesafe.config.ConfigFactory
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter

class Experiment4 {
}
object Experiment4{
  val config = ConfigFactory.load("application.conf")

  def Start() :Unit = {
    val logger = CreateLogger(classOf[Experiment4])
    val cloudSim = new CloudSim()
    val broker = new DatacenterBrokerSimple(cloudSim)
    val datacenter = createDatacenter(cloudSim,1)
    val virtualMachine = (1 to 10).map{_=>createVm()}.toList
    val cloudletList = createCloudlets()
    broker.submitCloudletList(cloudletList.asJava)
    broker.submitVmList(virtualMachine.asJava)
    cloudSim.start()
    new CloudletsTableBuilder(broker.getCloudletFinishedList()).build()
  }

  def createDatacenter(cloudSim : CloudSim,iteration : Int) : Datacenter = {
    val num_hosts = config.getString("Experiment4.CloudProviderProperties.datacenter.hosts").toInt
    val cost = config.getString("Experiment4.CloudProviderProperties.datacenter.cost").toDouble
    val costPerMem = config.getString("Experiment4.CloudProviderProperties.datacenter.cpm").toDouble
    val costPerStorage = config.getString("Experiment4.CloudProviderProperties.datacenter.cps").toDouble
    val costPerBw = config.getString("Experiment4.CloudProviderProperties.datacenter.cpb").toDouble
    val schedulingInterval = config.getString("Experiment4.CloudProviderProperties.datacenter.scheduling_interval").toInt

    val hostList = (iteration to num_hosts).map{i=>createHost(i)}.toList
    val datacenter = new DatacenterSimple(cloudSim, hostList.asJava)
    datacenter.getCharacteristics().setCostPerSecond(cost).setCostPerMem(costPerMem)
      .setCostPerStorage(costPerStorage).setCostPerBw(costPerBw)
    datacenter.setVmAllocationPolicy(new VmAllocationPolicyFirstFit())
    datacenter.setSchedulingInterval(schedulingInterval)
  }

  def createHost(host_number : Int ) ={
    val scheduler = config.getString("Experiment4.CloudProviderProperties.host"+host_number+".schedule")
    val mips = config.getString("Experiment4.CloudProviderProperties.host"+host_number+".mipsCapacity").toInt
    val hostPe = config.getString("Experiment4.CloudProviderProperties.host"+host_number+".Pes").toInt
    val peList = (1 to hostPe).map{_=>new PeSimple(mips).asInstanceOf[Pe]}.toList
    val hostRam = config.getString("Experiment4.CloudProviderProperties.host"+host_number+".RAMInMBs").toInt
    val storage = config.getString("Experiment4.CloudProviderProperties.host"+host_number+".StorageInMBs").toInt
    val host_bw = config.getString("Experiment4.CloudProviderProperties.host"+host_number+".BandwidthInMBps").toInt
    val host = new HostSimple(hostRam,host_bw,storage,peList.asJava)

    if(scheduler.equals("TimeShared")) {
      host.setVmScheduler(new VmSchedulerTimeShared())
    }else{
      host.setVmScheduler(new VmSchedulerSpaceShared())}
  }

  def createVm() : Vm ={
    val virtualMachine_Mips = config.getString("Experiment4.CloudProviderProperties.vm.mipsCapacity").toInt
    val virtualMachine_Pes = config.getString("Experiment4.CloudProviderProperties.vm.pes").toInt
    val virtualMachine_Ram = config.getString("Experiment4.CloudProviderProperties.vm.RAMInMBs").toInt
    val virtualMachine_Bw = config.getString("Experiment4.CloudProviderProperties.vm.BandwidthInMBps").toInt
    val virtualMachine_Size = config.getString("Experiment4.CloudProviderProperties.vm.StorageInMBs").toInt
    val vm = new VmSimple(virtualMachine_Mips,virtualMachine_Pes)
    vm.setRam(virtualMachine_Ram).setSize(virtualMachine_Size).setBw(virtualMachine_Bw)
    vm.setCloudletScheduler(new CloudletSchedulerTimeShared)

    createVerticalRamScalingForVm(vm)
    vm.setPeVerticalScaling(createVerticalCpuScaling())
  }

  def createCloudlets() : List[Cloudlet] = {
    val utilRatio = config.getString("Experiment4.utilizationRatio").toDouble
    val MaxResourceUtil = config.getString("Experiment4.maxResourceRatio").toDouble
    val utilModel = new UtilizationModelDynamic(0.3).setMaxResourceUtilization(0.5)
    val cloudletNumber = config.getString("Experiment4.BrokerProperties.cloudlet.number").toInt
    val cloudletList = ListBuffer.empty [Cloudlet]
    create(cloudletNumber, utilModel, cloudletList)

    def create(number:Int, model:UtilizationModelDynamic, list : ListBuffer[Cloudlet]) : Unit ={
      if(number == 0) return
      val cloudlet_Pes = config.getString("Experiment4.BrokerProperties.cloudlet.pes").toInt
      val cloudlet_Size = config.getString("Experiment4.BrokerProperties.cloudlet.size").toInt
      val cloudlet_FileSize = config.getString("Experiment4.BrokerProperties.cloudlet.filesize").toInt
      val cloudlet = new CloudletSimple(cloudlet_Size, cloudlet_Pes, model).setSizes(cloudlet_FileSize)
      list += cloudlet
      create(number-1,model, list)
    }
    cloudletList.toList
  }

  def createVerticalRamScalingForVm(vm:Vm) : Vm = {
    val verticalRamScaling = new VerticalVmScalingSimple(classOf[Ram], 0.6)
    val lowerRamUtilizationThreshold = config.getString("Experiment4.CloudProviderProperties.scaling.lowerThreshold").toDouble
    val highRamUtilizationThreshold = config.getString("Experiment4.CloudProviderProperties.scaling.higherThreshold").toDouble
    verticalRamScaling.setLowerThresholdFunction(verticalRamScaling => lowerRamUtilizationThreshold)
    verticalRamScaling.setUpperThresholdFunction(verticalRamScaling => highRamUtilizationThreshold)
    vm.setRamVerticalScaling(verticalRamScaling)
  }

  def createVerticalCpuScaling() : VerticalVmScalingSimple = {
    val verticalCPUScaling = new VerticalVmScalingSimple(classOf[Pe], 0.1)
    verticalCPUScaling.setResourceScaling(vs => 2*vs.getScalingFactor*vs.getAllocatedResource())
    verticalCPUScaling.setLowerThresholdFunction(verticalCPUScaling=>0.4)
    verticalCPUScaling.setLowerThresholdFunction(verticalCPUScaling=>0.8)
    verticalCPUScaling
  }


}
