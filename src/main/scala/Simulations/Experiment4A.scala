package Simulations

import HelperUtils.CreateLogger
import org.cloudbus.cloudsim.allocationpolicies.{VmAllocationPolicyBestFit, VmAllocationPolicyRoundRobin, VmAllocationPolicySimple,VmAllocationPolicyFirstFit}
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
import org.cloudbus.cloudsim.resources.Ram
import org.cloudsimplus.autoscaling.VerticalVmScalingSimple

import collection.mutable.*
import scala.jdk.CollectionConverters.*
import scala.collection.mutable.ListBuffer
import com.typesafe.config.ConfigFactory

class Experiment4A {
}

object Experiment4A{

  val config = ConfigFactory.load("application.conf")
  def Start() :Unit = {

    val logger = CreateLogger(classOf[Experiment4A])

    /*val cloudSim = new CloudSim()
    val broker = new DatacenterBrokerSimple(cloudSim)
    val datacenter = createDatacenter(cloudSim)
    val virtualMachine = (1 to 10).map{_=>createVm()}.toList
    val cloudletList = createCloudlets()
    broker.submitCloudletList(cloudletList.asJava)
    broker.submitVmList(virtualMachine.asJava)
    cloudSim.start()
    new CloudletsTableBuilder(broker.getCloudletFinishedList()).build()
*/

/*    val cloudSim_round = new CloudSim()
    val broker_round = new DatacenterBrokerSimple(cloudSim_round)
    val datacenter_round = createDatacenter_Round(cloudSim_round)
    val virtualMachine_round = (1 to 10).map{_=>createVm()}.toList
    val cloudletList_round = createCloudlets()
    broker_round.submitCloudletList(cloudletList_round.asJava)
    broker_round.submitVmList(virtualMachine_round.asJava)
    cloudSim_round.start()
    new CloudletsTableBuilder(broker_round.getCloudletFinishedList()).build()*/

/*    val cloudSim_bestFit = new CloudSim()
    val broker_bestFit = new DatacenterBrokerSimple(cloudSim_bestFit)
    val datacenter_bestFit = createDatacenter_bestFit(cloudSim_bestFit)
    val virtualMachine_bestFit = (1 to 10).map{_=>createVm()}.toList
    val cloudletList_bestFit = createCloudlets()
    broker_bestFit.submitCloudletList(cloudletList_bestFit.asJava)
    broker_bestFit.submitVmList(virtualMachine_bestFit.asJava)
    cloudSim_bestFit.start()
    new CloudletsTableBuilder(broker_bestFit.getCloudletFinishedList()).build()*/

    val cloudSim_firstFit = new CloudSim()
    val broker_firstFit = new DatacenterBrokerSimple(cloudSim_firstFit)
    val datacenter_firstFit = createDatacenter_firstFit(cloudSim_firstFit)
    val virtualMachine_firstFit = (1 to 10).map{_=>createVm()}.toList
    val cloudletList_firstFit = createCloudlets()
    broker_firstFit.submitCloudletList(cloudletList_firstFit.asJava)
    broker_firstFit.submitVmList(virtualMachine_firstFit.asJava)
    cloudSim_firstFit.start()
    new CloudletsTableBuilder(broker_firstFit.getCloudletFinishedList()).build()

  }

  def createDatacenter(cloudSim : CloudSim) : Datacenter = {
    val num_hosts = config.getString("Experiment4A.CloudProviderProperties.datacenter.hosts")
    val cost = config.getString("Experiment4A.CloudProviderProperties.datacenter.cost").toDouble
    val costPerMem = config.getString("Experiment4A.CloudProviderProperties.datacenter.cpm").toDouble
    val costPerStorage = config.getString("Experiment4A.CloudProviderProperties.datacenter.cps").toDouble
    val costPerBw = config.getString("Experiment4A.CloudProviderProperties.datacenter.cpb").toDouble
    val schedulingInterval = config.getString("Experiment4A.CloudProviderProperties.datacenter.scheduling_interval").toInt

    val hostList = (1 to 3).map{i=>createHost(i)}.toList
    val datacenter = new DatacenterSimple(cloudSim, hostList.asJava)
    datacenter.getCharacteristics().setCostPerSecond(cost).setCostPerMem(costPerMem)
      .setCostPerStorage(costPerStorage).setCostPerBw(costPerBw)
    datacenter.setVmAllocationPolicy(new VmAllocationPolicySimple())
    datacenter.setSchedulingInterval(schedulingInterval)
  }

  def createDatacenter_Round(cloudSim : CloudSim) : Datacenter = {
    val num_hosts = config.getString("Experiment4A.CloudProviderProperties.datacenter.hosts")
    val cost = config.getString("Experiment4A.CloudProviderProperties.datacenter.cost").toDouble
    val costPerMem = config.getString("Experiment4A.CloudProviderProperties.datacenter.cpm").toDouble
    val costPerStorage = config.getString("Experiment4A.CloudProviderProperties.datacenter.cps").toDouble
    val costPerBw = config.getString("Experiment4A.CloudProviderProperties.datacenter.cpb").toDouble

    val hostList = (1 to 3).map{i=>createHost(i)}.toList
    val datacenter = new DatacenterSimple(cloudSim, hostList.asJava)
    datacenter.getCharacteristics().setCostPerSecond(cost).setCostPerMem(costPerMem)
      .setCostPerStorage(costPerStorage).setCostPerBw(costPerBw)
    datacenter.setVmAllocationPolicy(new VmAllocationPolicyRoundRobin())
    datacenter
  }

  def createDatacenter_bestFit(cloudSim : CloudSim) : Datacenter = {
    val num_hosts = config.getString("Experiment4A.CloudProviderProperties.datacenter.hosts")
    val cost = config.getString("Experiment4A.CloudProviderProperties.datacenter.cost").toDouble
    val costPerMem = config.getString("Experiment4A.CloudProviderProperties.datacenter.cpm").toDouble
    val costPerStorage = config.getString("Experiment4A.CloudProviderProperties.datacenter.cps").toDouble
    val costPerBw = config.getString("Experiment4A.CloudProviderProperties.datacenter.cpb").toDouble

    val hostList = (1 to 3).map{i=>createHost(i)}.toList
    val datacenter = new DatacenterSimple(cloudSim, hostList.asJava)
    datacenter.getCharacteristics().setCostPerSecond(cost).setCostPerMem(costPerMem)
      .setCostPerStorage(costPerStorage).setCostPerBw(costPerBw)
    datacenter.setVmAllocationPolicy(new VmAllocationPolicyBestFit())
    datacenter
  }

  def createDatacenter_firstFit(cloudSim : CloudSim) : Datacenter = {
    val num_hosts = config.getString("Experiment4A.CloudProviderProperties.datacenter.hosts")
    val cost = config.getString("Experiment4A.CloudProviderProperties.datacenter.cost").toDouble
    val costPerMem = config.getString("Experiment4A.CloudProviderProperties.datacenter.cpm").toDouble
    val costPerStorage = config.getString("Experiment4A.CloudProviderProperties.datacenter.cps").toDouble
    val costPerBw = config.getString("Experiment4A.CloudProviderProperties.datacenter.cpb").toDouble

    val hostList = (1 to 3).map{i=>createHost(i)}.toList
    val datacenter = new DatacenterSimple(cloudSim, hostList.asJava)
    datacenter.getCharacteristics().setCostPerSecond(cost).setCostPerMem(costPerMem)
      .setCostPerStorage(costPerStorage).setCostPerBw(costPerBw)
    datacenter.setVmAllocationPolicy(new VmAllocationPolicyFirstFit())
    datacenter
  }


  def createHost(host_number : Int ) ={
    val mips = config.getString("Experiment4A.CloudProviderProperties.host"+host_number+".mipsCapacity").toInt
    val hostPe = config.getString("Experiment4A.CloudProviderProperties.host"+host_number+".Pes").toInt
    val peList = (1 to hostPe).map{_=>new PeSimple(mips).asInstanceOf[Pe]}.toList
    val hostRam = config.getString("Experiment4A.CloudProviderProperties.host"+host_number+".RAMInMBs").toInt
    val storage = config.getString("Experiment4A.CloudProviderProperties.host"+host_number+".StorageInMBs").toInt
    val host_bw = config.getString("Experiment4A.CloudProviderProperties.host"+host_number+".BandwidthInMBps").toInt
    val host = new HostSimple(hostRam,host_bw,storage,peList.asJava)
    host.setVmScheduler(new VmSchedulerSpaceShared())
  }

  def createVm() : Vm ={
    val virtualMachine_Mips = config.getString("Experiment4A.CloudProviderProperties.vm.mipsCapacity").toInt
    val virtualMachine_Pes = config.getString("Experiment4A.CloudProviderProperties.vm.pes").toInt
    val virtualMachine_Ram = config.getString("Experiment4A.CloudProviderProperties.vm.RAMInMBs").toInt
    val virtualMachine_Bw = config.getString("Experiment4A.CloudProviderProperties.vm.BandwidthInMBps").toInt
    val virtualMachine_Size = config.getString("Experiment4A.CloudProviderProperties.vm.StorageInMBs").toInt
    val vm = new VmSimple(virtualMachine_Mips,virtualMachine_Pes)
    vm.setRam(virtualMachine_Ram).setSize(virtualMachine_Size).setBw(virtualMachine_Bw)
    vm.setCloudletScheduler(new CloudletSchedulerTimeShared)

    createVerticalRamScalingForVm(vm)
    //vm.setPeVerticalScaling(createVerticalCpuScaling())

  }

  def createCloudlets() : List[Cloudlet] = {
    val utilRatio = config.getString("Experiment4A.utilizationRatio").toDouble
    val MaxResourceUtil = config.getString("Experiment4A.maxResourceRatio").toDouble
    val utilModel = new UtilizationModelDynamic(0.3).setMaxResourceUtilization(0.5)
    val cloudletNumber = config.getString("Experiment4A.BrokerProperties.cloudlet.number").toInt
    val cloudletList = ListBuffer.empty [Cloudlet]
    create(cloudletNumber, utilModel, cloudletList)

    def create(number:Int, model:UtilizationModelDynamic, list : ListBuffer[Cloudlet]) : Unit ={
      if(number == 0) return
      val cloudlet_Pes = config.getString("Experiment4A.BrokerProperties.cloudlet.pes").toInt
      val cloudlet_Size = config.getString("Experiment4A.BrokerProperties.cloudlet.size").toInt
      val cloudlet_FileSize = config.getString("Experiment4A.BrokerProperties.cloudlet.filesize").toInt
      val cloudlet = new CloudletSimple(cloudlet_Size, cloudlet_Pes, model).setSizes(cloudlet_FileSize)
      list += cloudlet
      create(number-1,model, list)
    }
    cloudletList.toList
  }

  def createVerticalRamScalingForVm(vm:Vm) : Vm = {
    val verticalRamScaling = new VerticalVmScalingSimple(classOf[Ram], 0.1)
    val lowerRamUtilizationThreshold = config.getString("Experiment4.CloudProviderProperties.scaling.lowerThreshold").toDouble
    val highRamUtilizationThreshold = config.getString("Experiment4.CloudProviderProperties.scaling.higherThreshold").toDouble
    verticalRamScaling.setLowerThresholdFunction(verticalRamScaling => lowerRamUtilizationThreshold)
    verticalRamScaling.setUpperThresholdFunction(verticalRamScaling => highRamUtilizationThreshold)
    vm.setRamVerticalScaling(verticalRamScaling)
  }

  def createVerticalCpuScaling() : VerticalVmScalingSimple = {
    val verticalCPUScaling = new VerticalVmScalingSimple(classOf[Ram], 0.1)
    verticalCPUScaling.setResourceScaling(vs => 2*vs.getScalingFactor*vs.getAllocatedResource())
    verticalCPUScaling.setLowerThresholdFunction(verticalCPUScaling=>0.4)
    verticalCPUScaling.setLowerThresholdFunction(verticalCPUScaling=>0.8)
    verticalCPUScaling
  }

}
