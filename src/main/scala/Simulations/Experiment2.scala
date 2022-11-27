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
import org.cloudbus.cloudsim.vms.{Vm, VmCost, VmSimple}
import org.cloudsimplus.builders.tables.CloudletsTableBuilder

import collection.mutable.*
import scala.jdk.CollectionConverters.*
import scala.collection.mutable.ListBuffer
import com.typesafe.config.ConfigFactory

class Experiment2 {

}

/**
 * Experiment 2
 * In this experiment, comparing the performance of scheduler policies like  VmSchedulerTimeShared and VmSchedulerSpaceShared.
 * The experiment 4 runs all 3 simulations simultaneously and prints the results in the console.
 *
 * Simulation 1:
 *  Number of Host : 2
 *  VmScheduler policy : Both hosts have TimeShared policy
 *
 * Simulation 2:
 *  Number of Host : 2
 *  VmScheduler policy : Both hosts have SpaceShared policy
 *
 * Simulation 3:
 *  Number of Host : 2
 *  VmScheduler policy : One host has TimeShared Policy, One host has SpaceShared Policy
 */
object Experiment2{
  val logger = CreateLogger(classOf[Experiment2])
  val config = ConfigFactory.load("application.conf")
  def Start() :Unit = {
    val cloudletnum = config.getString("Experiment2.BrokerProperties.cloudletCount").toInt
    val vm = config.getString("Experiment2.CloudProviderProperties.datacenter.vm").toInt
    val logger = CreateLogger(classOf[Experiment2])
    /**
     *  Simulation 1:
     *  Number of Host : 2
     *  VmScheduler policy : Both hosts have TimeShared policy
     */
    val cloudSim = new CloudSim()
    val broker = new DatacenterBrokerSimple(cloudSim)
    val datacenter = createDatacenter(cloudSim,1)
    val virtualMachine = (1 to vm).map{_=>createVm()}.toList
    val cloudletList = ListBuffer.empty [Cloudlet]
    (1 to cloudletnum).map(i=>{cloudletList.addAll(createCloudlets(i))})
    broker.submitCloudletList(cloudletList.asJava)
    broker.submitVmList(virtualMachine.asJava)
    logger.info("Starting the CloudSimulation1 for Experiment 2")
    cloudSim.start()
    logger.info("Finishing the CloudSimulation1 for Experiment 2")
    new CloudletsTableBuilder(broker.getCloudletFinishedList()).build()
    printCost(broker)

    /**
     *  Simulation 2:
     *  Number of Host : 2
     *  VmScheduler policy : Both hosts have SpaceShared policy
     */
    val cloudSim_SpaceShared = new CloudSim()
    val broker_SpaceShared = new DatacenterBrokerSimple(cloudSim_SpaceShared)
    val datacenter_SpaceShared = createDatacenter(cloudSim_SpaceShared,3)
    val virtualMachine_SpaceShared = (1 to vm).map{_=>createVm()}.toList
    val cloudletList_SpaceShared = ListBuffer.empty [Cloudlet]
    (1 to cloudletnum).map(i=>{cloudletList_SpaceShared.addAll(createCloudlets(i))})
    broker_SpaceShared.submitCloudletList(cloudletList_SpaceShared.asJava)
    broker_SpaceShared.submitVmList(virtualMachine_SpaceShared.asJava)
    logger.info("Starting the CloudSimulation2 for Experiment 2")
    cloudSim_SpaceShared.start()
    logger.info("Finishing the CloudSimulation2 for Experiment 2")
    new CloudletsTableBuilder(broker_SpaceShared.getCloudletFinishedList()).build()
    printCost(broker_SpaceShared)

    /**
     *  Simulation 3:
     *  Number of Host : 2
     *  VmScheduler policy : One host has TimeShared Policy, One host has SpaceShared Policy
     */
    val cloudSim_Mix = new CloudSim()
    val broker_Mix = new DatacenterBrokerSimple(cloudSim_Mix)
    val datacenter_Mix = createDatacenter(cloudSim_Mix,5)
    val virtualMachine_Mix = (1 to vm).map{_=>createVm()}.toList
    val cloudletList_Mix = ListBuffer.empty [Cloudlet]
    (1 to cloudletnum).map(i=>{cloudletList_Mix.addAll(createCloudlets(i))})
    broker_Mix.submitCloudletList(cloudletList_Mix.asJava)
    broker_Mix.submitVmList(virtualMachine_SpaceShared.asJava)
    logger.info("Starting the CloudSimulation3 for Experiment 2")
    cloudSim_Mix.start()
    logger.info("Finishing the CloudSimulation3 for Experiment 2")
    new CloudletsTableBuilder(broker_Mix.getCloudletFinishedList()).build()
    printCost(broker_Mix)



  }
  /**
   * This function creates new datacenter for the experiment. Can edit the datacenter configurations from the
   * application.conf Experiment1 file.
   * The VmAllocation policy for this datacenter is the Custom Random vMAllocation
   * @param cloudSim : Input the cloudsim in which datacenter needs to be created.
   * @return Datacenter : Newly Created Datacenter.
   */
  def createDatacenter(cloudSim : CloudSim,iteration : Int) : Datacenter = {
    val num_hosts = config.getString("Experiment2.CloudProviderProperties.datacenter.hosts")
    val cost = config.getString("Experiment2.CloudProviderProperties.datacenter.cost").toDouble
    val costPerMem = config.getString("Experiment2.CloudProviderProperties.datacenter.cpm").toDouble
    val costPerStorage = config.getString("Experiment2.CloudProviderProperties.datacenter.cps").toDouble
    val costPerBw = config.getString("Experiment2.CloudProviderProperties.datacenter.cpb").toDouble
    val schedulingInterval = config.getString("Experiment2.CloudProviderProperties.datacenter.scheduling_interval").toInt

    val hostList = (iteration to iteration+1).map{i=>createHost(i)}.toList
    val datacenter = new DatacenterSimple(cloudSim, hostList.asJava)
    datacenter.getCharacteristics().setCostPerSecond(cost).setCostPerMem(costPerMem)
      .setCostPerStorage(costPerStorage).setCostPerBw(costPerBw)
    datacenter.setVmAllocationPolicy(new VmAllocationPolicyBestFit())
    logger.info("Created datacenter "+ datacenter.getName+"with host count"+datacenter.getHostList().size())
    datacenter.setSchedulingInterval(schedulingInterval)
  }


  /**
   * Function creates the host for the cloudsim datacenter.
   * @param host_number : This number will read data from the application.conf file
   * @return host : returns a host
   */
  def createHost(host_number : Int ) ={
    val scheduler = config.getString("Experiment2.CloudProviderProperties.host"+host_number+".schedule")
    val mips = config.getString("Experiment2.CloudProviderProperties.host"+host_number+".mipsCapacity").toInt
    val hostPe = config.getString("Experiment2.CloudProviderProperties.host"+host_number+".Pes").toInt
    val peList = (1 to hostPe).map{_=>new PeSimple(mips).asInstanceOf[Pe]}.toList
    val hostRam = config.getString("Experiment2.CloudProviderProperties.host"+host_number+".RAMInMBs").toInt
    val storage = config.getString("Experiment2.CloudProviderProperties.host"+host_number+".StorageInMBs").toInt
    val host_bw = config.getString("Experiment2.CloudProviderProperties.host"+host_number+".BandwidthInMBps").toInt
    val host = new HostSimple(hostRam,host_bw,storage,peList.asJava)

    if(scheduler.equals("TimeShared")) {
       host.setVmScheduler(new VmSchedulerTimeShared())
    }else{
       host.setVmScheduler(new VmSchedulerSpaceShared())
    }
    logger.info("Created Host "+ host.getId)
    host
  }
  /**
   * Function creates the vm for the cloudsim. Can edit the vM configuration in the application.conf.Experiment2.vm
   * @return Vm  : returns the Vm created
   */
  def createVm() : Vm ={
    val virtualMachine_Mips = config.getString("Experiment2.CloudProviderProperties.vm.mipsCapacity").toInt
    val virtualMachine_Pes = config.getString("Experiment2.CloudProviderProperties.vm.pes").toInt
    val virtualMachine_Ram = config.getString("Experiment2.CloudProviderProperties.vm.RAMInMBs").toInt
    val virtualMachine_Bw = config.getString("Experiment2.CloudProviderProperties.vm.BandwidthInMBps").toInt
    val virtualMachine_Size = config.getString("Experiment2.CloudProviderProperties.vm.StorageInMBs").toInt
    val vm = new VmSimple(virtualMachine_Mips,virtualMachine_Pes)
    vm.setRam(virtualMachine_Ram).setSize(virtualMachine_Size).setBw(virtualMachine_Bw)
    logger.info("Created VM"+vm.getId)
    vm.setCloudletScheduler(new CloudletSchedulerTimeShared)
  }


  /**
   * This function is used to create clouldlets for the experiment.
   * @param cloudLetNumber : This number helps in getting the configuraiton from application.conf file
   * @return List[[Cloudlet]] : Returns a list of Clouldlets.
   */
  def createCloudlets(cloudLetNumber : Int) : List[Cloudlet] = {
    val utilRatio = config.getString("Experiment2.utilizationRatio").toDouble
    val MaxResourceUtil = config.getString("Experiment2.maxResourceRatio").toDouble
    val utilModel = new UtilizationModelDynamic(utilRatio).setMaxResourceUtilization(MaxResourceUtil)
    val cloudletNumber = config.getString("Experiment2.BrokerProperties.cloudlet"+cloudLetNumber+".number").toInt
    val cloudletList = ListBuffer.empty [Cloudlet]
    create(cloudletNumber, utilModel, cloudletList)

    def create(number:Int, model:UtilizationModelDynamic, list : ListBuffer[Cloudlet]) : Unit ={
      if(number == 0) return
      val cloudlet_Pes = config.getString("Experiment2.BrokerProperties.cloudlet"+cloudLetNumber+".pes").toInt
      val cloudlet_Size = config.getString("Experiment2.BrokerProperties.cloudlet"+cloudLetNumber+".size").toInt
      val cloudlet_FileSize = config.getString("Experiment2.BrokerProperties.cloudlet"+cloudLetNumber+".filesize").toInt
      val cloudlet = new CloudletSimple(cloudlet_Size, cloudlet_Pes, model).setSizes(cloudlet_FileSize)
      list += cloudlet
      logger.info("Created cloudlet"+cloudlet.getId)
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
