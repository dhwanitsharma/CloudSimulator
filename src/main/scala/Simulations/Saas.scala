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
import org.cloudbus.cloudsim.vms.{Vm, VmCost, VmSimple}
import org.cloudsimplus.builders.tables.CloudletsTableBuilder

import collection.mutable.*
import scala.jdk.CollectionConverters.*
import scala.collection.mutable.ListBuffer
import com.typesafe.config.ConfigFactory

class Saas {
}

/**
 * Created a simulation which provides user an option to select the following 
 * setting to run a Saas system.
 * 1. Slow:
   * Ram        : 1024
   * Storage    : 1024
   * BandWidth  : 1000
   * Pes        : 2
   * Mips       : 1000
 * 
 * 2. Medium:
   * Ram        : 2048
   * Storage    : 2048
   * BandWidth  : 2000
   * Pes        : 4
   * Mips       : 2000  
 * 
 * 3. Fast
   * Ram        : 4096
   * Storage    : 4096
   * BandWidth  : 4000
   * Pes        : 8
   * Mips       : 4000 
 * 
 */
object Saas{

  val config = ConfigFactory.load("application.conf")
  def Start() :Unit = {

    val logger = CreateLogger(classOf[Saas])

    /**
     * Simulation to run the cloudsim for slow settings
     */
    val cloudSim1 = new CloudSim()
    val broker1 = new DatacenterBrokerSimple(cloudSim1)
    val datacenter1 = createDatacenter(cloudSim1,1)
    val virtualMachine1 = createVm(1)
    val cloudletListTaskA_1 = createCloudlets(1)
    val cloudletListTaskB_1= createCloudlets(2)
    val cloudletList = cloudletListTaskA_1.appendedAll(cloudletListTaskB_1)
    broker1.submitCloudletList(cloudletList.asJava)
    broker1.submitVm(virtualMachine1)
    cloudSim1.start()
    new CloudletsTableBuilder(broker1.getCloudletFinishedList()).build()
    printCost(broker1)
    /**
     * Simulation to run the cloudsim for medium settings
     */
    val cloudSim2 = new CloudSim()
    val broker2 = new DatacenterBrokerSimple(cloudSim2)
    val datacenter2 = createDatacenter(cloudSim2,2)
    val virtualMachine2 = createVm(2)
    val cloudletListTaskA_2 = createCloudlets(1)
    val cloudletListTaskB_2 = createCloudlets(2)
    val cloudletList2 = cloudletListTaskA_2.appendedAll(cloudletListTaskB_2)
    broker2.submitCloudletList(cloudletList2.asJava)
    broker2.submitVm(virtualMachine2)
    cloudSim2.start()
    new CloudletsTableBuilder(broker2.getCloudletFinishedList()).build()
    printCost(broker2)
    
    /**
     * Simulation to run the cloudsim for high settings
     */
    val cloudSim3 = new CloudSim()
    val broker3 = new DatacenterBrokerSimple(cloudSim3)
    val datacenter3 = createDatacenter(cloudSim3,3)
    val virtualMachine3 = createVm(3)
    val cloudletListTaskA_3 = createCloudlets(1)
    val cloudletListTaskB_3 = createCloudlets(2)
    val cloudletList3 = cloudletListTaskA_3.appendedAll(cloudletListTaskB_3)
    broker3.submitCloudletList(cloudletList3.asJava)
    broker3.submitVm(virtualMachine3)
    cloudSim3.start()
    new CloudletsTableBuilder(broker3.getCloudletFinishedList()).build()
    printCost(broker3)



  }
  
  /**
   * This function creates new datacenter for the experiment. Can edit the datacenter configurations from the
   * application.conf Experiment1 file.
   * The VmAllocation policy for this datacenter is the BestFitvMAllocation
   * @param cloudSim : Input the cloudsim in which datacenter needs to be created.
   * @return Datacenter : Newly Created Datacenter.
   */
  def createDatacenter(cloudSim : CloudSim,datacenterNumber : Int) : Datacenter = {
    val num_hosts = config.getString("Saas.CloudProviderProperties.datacenter"+datacenterNumber+".hosts").toInt
    val cost = config.getString("Saas.CloudProviderProperties.datacenter"+datacenterNumber+".cost").toDouble
    val costPerMem = config.getString("Saas.CloudProviderProperties.datacenter"+datacenterNumber+".cpm").toDouble
    val costPerStorage = config.getString("Saas.CloudProviderProperties.datacenter"+datacenterNumber+".cps").toDouble
    val costPerBw = config.getString("Saas.CloudProviderProperties.datacenter"+datacenterNumber+".cpb").toDouble
    val schedulingInterval = config.getString("Saas.CloudProviderProperties.datacenter"+datacenterNumber+".scheduling_interval").toInt

    val hostList = (1 to num_hosts).map{i=>createHost(i)}.toList
    val datacenter = new DatacenterSimple(cloudSim, hostList.asJava)
    datacenter.getCharacteristics().setCostPerSecond(cost).setCostPerMem(costPerMem)
      .setCostPerStorage(costPerStorage).setCostPerBw(costPerBw)
    datacenter.setVmAllocationPolicy(new VmAllocationPolicyBestFit())
    datacenter.setSchedulingInterval(schedulingInterval)
  }



  /**
   * Function creates the host for the cloudsim datacenter.
   * @param host_number : This number will read data from the application.conf file
   * @return host : returns a host
   */
  def createHost(host_number : Int ) ={
    val mips = config.getString("Saas.CloudProviderProperties.host"+host_number+".mipsCapacity").toInt
    val hostPe = config.getString("Saas.CloudProviderProperties.host"+host_number+".Pes").toInt
    val peList = (1 to hostPe).map{_=>new PeSimple(mips).asInstanceOf[Pe]}.toList
    val hostRam = config.getString("Saas.CloudProviderProperties.host"+host_number+".RAMInMBs").toInt
    val storage = config.getString("Saas.CloudProviderProperties.host"+host_number+".StorageInMBs").toInt
    val host_bw = config.getString("Saas.CloudProviderProperties.host"+host_number+".BandwidthInMBps").toInt
    val host = new HostSimple(hostRam,host_bw,storage,peList.asJava)
    host.setVmScheduler(new VmSchedulerSpaceShared())
  }
  
  /**
   * Function creates the vm for the cloudsim. Can edit the vM configuration in the application.conf.Experiment2.vm
   * @return Vm  : returns the Vm created
   */
  def createVm(vm_Number : Int) : Vm ={
    val virtualMachine_Mips = config.getString("Saas.CloudProviderProperties.vm"+vm_Number+".mipsCapacity").toInt
    val virtualMachine_Pes = config.getString("Saas.CloudProviderProperties.vm"+vm_Number+".pes").toInt
    val virtualMachine_Ram = config.getString("Saas.CloudProviderProperties.vm"+vm_Number+".RAMInMBs").toInt
    val virtualMachine_Bw = config.getString("Saas.CloudProviderProperties.vm"+vm_Number+".BandwidthInMBps").toInt
    val virtualMachine_Size = config.getString("Saas.CloudProviderProperties.vm"+vm_Number+".StorageInMBs").toInt
    val vm = new VmSimple(virtualMachine_Mips,virtualMachine_Pes)
    vm.setRam(virtualMachine_Ram).setSize(virtualMachine_Size).setBw(virtualMachine_Bw)
    vm.setCloudletScheduler(new CloudletSchedulerTimeShared)
  }

  /**
   * This function is used to create clouldlets for the experiment.
   * @param cloudLetNumber : This number helps in getting the configuraiton from application.conf file
   * @return List[[Cloudlet]] : Returns a list of Clouldlets.
   */
  def createCloudlets(cloudLetNumber : Int) : List[Cloudlet] = {
    val utilRatio = config.getString("Saas.utilizationRatio").toDouble
    val MaxResourceUtil = config.getString("Saas.maxResourceRatio").toDouble
    val utilModel = new UtilizationModelDynamic(0.3).setMaxResourceUtilization(0.5)
    val cloudletNumber = config.getString("Saas.BrokerProperties.cloudlet"+cloudLetNumber+".number").toInt
    val cloudletList = ListBuffer.empty [Cloudlet]
    create(cloudletNumber, utilModel, cloudletList)

    def create(number:Int, model:UtilizationModelDynamic, list : ListBuffer[Cloudlet]) : Unit ={
      if(number == 0) return
      val cloudlet_Pes = config.getString("Saas.BrokerProperties.cloudlet"+cloudLetNumber+".pes").toInt
      val cloudlet_Size = config.getString("Saas.BrokerProperties.cloudlet"+cloudLetNumber+".size").toInt
      val cloudlet_FileSize = config.getString("Saas.BrokerProperties.cloudlet"+cloudLetNumber+".filesize").toInt
      val cloudlet = new CloudletSimple(cloudlet_Size, cloudlet_Pes, model).setSizes(cloudlet_FileSize)
      list += cloudlet
      create(number-1,model, list)
    }
    cloudletList.toList
  }
  /**
   * This function is used to print the cost of the simulation
   * @param broker : The broker of the simulation
   */
  def printCost(broker : DatacenterBrokerSimple) ={
    // Initialize variables
    var totalCost: Double           = 0
    var totalNonIdleVms: Int        = 0
    var processingTotalCost: Double = 0
    var memoryTotalCost: Double     = 0
    var storageTotalCost: Double    = 0
    var bwTotalCost: Double         = 0

    // For each VM object, create a VmCost object and extract the costs and prints it
    for (vm <- broker.getVmCreatedList.asScala) {
      val cost: VmCost      = new VmCost(vm)
      processingTotalCost   += cost.getProcessingCost
      memoryTotalCost       += cost.getMemoryCost
      storageTotalCost      += cost.getStorageCost
      bwTotalCost           += cost.getBwCost
      totalCost             += cost.getTotalCost
      System.out.println(cost)
    }
  }
}
