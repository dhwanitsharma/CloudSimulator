package Simulations

import HelperUtils.CreateLogger
import org.cloudbus.cloudsim.allocationpolicies.{VmAllocationPolicyBestFit, VmAllocationPolicyFirstFit, VmAllocationPolicyRoundRobin, VmAllocationPolicySimple}
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.cloudlets.{Cloudlet, CloudletSimple}
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.{Datacenter, DatacenterSimple}
import org.cloudbus.cloudsim.hosts.HostSimple
import org.cloudbus.cloudsim.resources.{Pe, PeSimple}
import org.cloudbus.cloudsim.schedulers.cloudlet.{CloudletSchedulerSpaceShared, CloudletSchedulerTimeShared}
import org.cloudbus.cloudsim.schedulers.vm.{VmSchedulerSpaceShared, VmSchedulerTimeShared}
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic
import org.cloudbus.cloudsim.vms.{Vm, VmSimple}
import org.cloudsimplus.builders.tables.CloudletsTableBuilder
import org.cloudbus.cloudsim.hosts.network.NetworkHost
import org.cloudbus.cloudsim.network.topologies.{BriteNetworkTopology, NetworkTopology}
import org.cloudbus.cloudsim.network.switches.EdgeSwitch
import org.cloudbus.cloudsim.vms.network.NetworkVm

import scala.util.Random
import collection.mutable.*
import scala.jdk.CollectionConverters.*
import scala.collection.mutable.ListBuffer
import com.typesafe.config.ConfigFactory
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter

class Experiment3 {
}

object Experiment3{
  val config = ConfigFactory.load("application.conf")
  val logger = CreateLogger(classOf[Experiment3])
  def Start() :Unit = {
    val cloudSim = new CloudSim()
    val broker = new DatacenterBrokerSimple(cloudSim)

    val networkLat = config.getString("Experiment3.networkLatencyBetweenDC").toInt
    val networkBw = config.getString("Experiment3.networkBwBetweenDc").toInt

    val datacenter1 = createDatacenter(cloudSim,1)
    val datacenter2 = createDatacenter(cloudSim,1)
    val datacenter3 = createDatacenter(cloudSim,1)
    val datacenter4 = createDatacenter(cloudSim,1)

    val networkTopology = new BriteNetworkTopology()
    cloudSim.setNetworkTopology(networkTopology)

    logger.info("Adding networklink with broker")
    // Add link between datacenter and broker
    networkTopology.addLink(datacenter1,broker,networkBw,networkLat)
    networkTopology.addLink(datacenter2,broker,networkBw,networkLat)
    networkTopology.addLink(datacenter3,broker,networkBw,networkLat)
    networkTopology.addLink(datacenter4,broker,networkBw,networkLat)

    logger.info("Adding networklink with datacenters")
    //Add link between the 4 data centers
    networkTopology.addLink(datacenter1,datacenter2,networkBw,networkLat)
    networkTopology.addLink(datacenter1,datacenter3,networkBw,networkLat)
    networkTopology.addLink(datacenter1,datacenter4,networkBw,networkLat)
    networkTopology.addLink(datacenter3,datacenter4,networkBw,networkLat)

    val virtualMachine = (1 to 200).map{_=>createVm()}.toList
    val cloudletList = createCloudlets(virtualMachine)
    broker.submitCloudletList(cloudletList.asJava)
    broker.submitVmList(virtualMachine.asJava)
    logger.info("Starting the CloudSimulation for Experiment 3")
    cloudSim.start()
    logger.info("Starting the CloudSimulation for Experiment 3")
    new CloudletsTableBuilder(broker.getCloudletFinishedList).build()

  }


  def createDatacenter(cloudSim : CloudSim,iteration : Int) : NetworkDatacenter = {
    val num_hosts = config.getString("Experiment3.CloudProviderProperties.datacenter.hosts").toInt
    val cost = config.getString("Experiment3.CloudProviderProperties.datacenter.cost").toDouble
    val costPerMem = config.getString("Experiment3.CloudProviderProperties.datacenter.cpm").toDouble
    val costPerStorage = config.getString("Experiment3.CloudProviderProperties.datacenter.cps").toDouble
    val costPerBw = config.getString("Experiment3.CloudProviderProperties.datacenter.cpb").toDouble
    val schedulingInterval = config.getString("Experiment3.CloudProviderProperties.datacenter.scheduling_interval").toInt

    val vmAlloction = config.getString("Experiment3.CloudProviderProperties.datacenter.hosts")

    val hostList = (iteration to num_hosts).map{i=>createHost(i)}.toList
    val datacenter = new NetworkDatacenter(cloudSim, hostList.asJava,vmAlloction match{
      case "best" => new VmAllocationPolicyBestFit()
      case "round" => new VmAllocationPolicyRoundRobin()
      case "simple" => new VmAllocationPolicySimple()
      case default => new VmAllocationPolicySimple()
    })
    datacenter.setVmAllocationPolicy(new VmAllocationPolicyBestFit())
    datacenter.getCharacteristics().setCostPerSecond(cost).setCostPerMem(costPerMem)
      .setCostPerStorage(costPerStorage).setCostPerBw(costPerBw)
    datacenter.setSchedulingInterval(schedulingInterval)
    createNetwork(datacenter, cloudSim)
    logger.info("Created datacenter "+ datacenter.getName+"with host count"+datacenter.getHostList().size())
    datacenter
  }

  def createHost(host_number : Int ) : NetworkHost = {
    val scheduler = config.getString("Experiment3.CloudProviderProperties.host"+host_number+".schedule")
    val mips = config.getString("Experiment3.CloudProviderProperties.host"+host_number+".mipsCapacity").toInt
    val hostPe = config.getString("Experiment3.CloudProviderProperties.host"+host_number+".Pes").toInt
    val peList = (1 to hostPe).map{_=>new PeSimple(mips).asInstanceOf[Pe]}.toList
    val hostRam = config.getString("Experiment3.CloudProviderProperties.host"+host_number+".RAMInMBs").toInt
    val storage = config.getString("Experiment3.CloudProviderProperties.host"+host_number+".StorageInMBs").toInt
    val host_bw = config.getString("Experiment3.CloudProviderProperties.host"+host_number+".BandwidthInMBps").toInt
    val host = new NetworkHost(hostRam,host_bw,storage,peList.asJava)

    if(scheduler.equals("TimeShared")) {
      host.setVmScheduler(new VmSchedulerTimeShared())
    }else{
      host.setVmScheduler(new VmSchedulerSpaceShared())}
    logger.info("Created Host "+ host.getId)
    host
  }

  def createVm() : NetworkVm ={
    val virtualMachine_Mips = config.getString("Experiment3.CloudProviderProperties.vm.mipsCapacity").toInt
    val virtualMachine_Pes = config.getString("Experiment3.CloudProviderProperties.vm.pes").toInt
    val virtualMachine_Ram = config.getString("Experiment3.CloudProviderProperties.vm.RAMInMBs").toInt
    val virtualMachine_Bw = config.getString("Experiment3.CloudProviderProperties.vm.BandwidthInMBps").toInt
    val virtualMachine_Size = config.getString("Experiment3.CloudProviderProperties.vm.StorageInMBs").toInt
    val vm = new NetworkVm(virtualMachine_Mips,virtualMachine_Pes)
    vm.setRam(virtualMachine_Ram).setSize(virtualMachine_Size).setBw(virtualMachine_Bw)
    vm.setCloudletScheduler(new CloudletSchedulerTimeShared)
    logger.info("Created VM"+vm.getId)
    vm
  }

  def createCloudlets(vmList:List[NetworkVm]) : List[Cloudlet] = {
    val utilRatio = config.getString("Experiment3.utilizationRatio").toDouble
    val MaxResourceUtil = config.getString("Experiment3.maxResourceRatio").toDouble
    val utilModel = new UtilizationModelDynamic(0.3).setMaxResourceUtilization(0.5)
    val cloudletNumber = config.getString("Experiment3.BrokerProperties.cloudlet.number").toInt
    val cloudletList = ListBuffer.empty [Cloudlet]
    create(cloudletNumber, utilModel, cloudletList)

    def create(number:Int, model:UtilizationModelDynamic, list : ListBuffer[Cloudlet]) : Unit ={
      if(number == 0) return
      val cloudlet_Pes = config.getString("Experiment3.BrokerProperties.cloudlet.pes").toInt
      val cloudlet_Size = config.getString("Experiment3.BrokerProperties.cloudlet.size").toInt
      val cloudlet_FileSize = config.getString("Experiment3.BrokerProperties.cloudlet.filesize").toInt
      val cloudlet = new CloudletSimple(cloudlet_Size, cloudlet_Pes, model).setSizes(cloudlet_FileSize)
      list += cloudlet
      logger.info("Created cloudlet"+cloudlet.getId)
      create(number-1,model, list)
    }
    cloudletList.foreach(cloudlet => {
      val rand = Random.between(0,vmList.size)
      val selectedVm = vmList.apply(rand)
      cloudlet.setVm(selectedVm)
      cloudlet.setBroker(selectedVm.getBroker)
    })
    cloudletList.toList
  }

  def createNetwork(dc: NetworkDatacenter, simulation: CloudSim): Unit = {
    val edgeSwitches = List.fill(2) {
      val edgeSwitch = new EdgeSwitch(simulation, dc)
      dc.addSwitch(edgeSwitch)
      edgeSwitch
    }
    val hostList = dc.getHostList[NetworkHost]
    hostList.forEach(host => {
      logger.info("Adding swith for "+ host.getId)
      val switchNum = getSwitchIndex(host, edgeSwitches(0).getPorts)
      edgeSwitches(switchNum).connectHost(host)
    })
  }

  def getSwitchIndex(host: NetworkHost, switchPorts: Int): Int = Math.round(host.getId % Integer.MAX_VALUE) / switchPorts

}
