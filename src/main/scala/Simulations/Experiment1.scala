package Simulations

import HelperUtils.{CreateLogger, VmAllocationPolicyRandom}
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

class Experiment1 {
}

/**
 * Experiment 1
 * In this experiment, comparing the performance of Simple, Round Robin, Best Fit and First Fit vm Allocation policy.
 * The experiment 1 runs all 4 simulations simultaneously and prints the results in the console.
 */
object Experiment1{
  val logger = CreateLogger(classOf[Experiment1])
  val config = ConfigFactory.load("application.conf")
  def Start() :Unit = {



    val cloudletnum = config.getString("Experiment1.BrokerProperties.cloudletCount").toInt
    val vm = config.getString("Experiment1.CloudProviderProperties.datacenter.vm").toInt


    /**
     * Simulation for Simple VmAllocation Method
     */
    val cloudSim = new CloudSim()
    val broker = new DatacenterBrokerSimple(cloudSim)
    val datacenter = createDatacenter(cloudSim)
    val virtualMachine = (1 to vm).map{_=>createVm()}.toList
    val cloudletList = ListBuffer.empty [Cloudlet]
    (1 to cloudletnum).map(i=>{cloudletList.addAll(createCloudlets(i))})
    broker.submitCloudletList(cloudletList.asJava)
    broker.submitVmList(virtualMachine.asJava)
    logger.info("Starting the CloudSimulation1 for Experiment 1")
    cloudSim.start()
    logger.info("Finishing the CloudSimulation1 for Experiment 1")
    new CloudletsTableBuilder(broker.getCloudletFinishedList()).build()
    printCost(broker)

    /**
     * Simulation for Round-robin VmAllocation Method
     */
    val cloudSim_round = new CloudSim()
    val broker_round = new DatacenterBrokerSimple(cloudSim_round)
    val datacenter_round = createDatacenter_Round(cloudSim_round)
    val virtualMachine_round = (1 to vm).map{_=>createVm()}.toList
    val cloudletList_round = ListBuffer.empty [Cloudlet]
    (1 to cloudletnum).map(i=>{cloudletList_round.addAll(createCloudlets(i))})
    broker_round.submitCloudletList(cloudletList_round.asJava)
    broker_round.submitVmList(virtualMachine_round.asJava)
    logger.info("Starting the CloudSimulation2 for Experiment 1")
    cloudSim_round.start()
    logger.info("Finishing the CloudSimulation2 for Experiment 1")
    new CloudletsTableBuilder(broker_round.getCloudletFinishedList()).build()
    printCost(broker_round)

    /**
     * Simulation for Best-fit VmAllocation Method
     */
    val cloudSim_bestFit = new CloudSim()
    val broker_bestFit = new DatacenterBrokerSimple(cloudSim_bestFit)
    val datacenter_bestFit = createDatacenter_bestFit(cloudSim_bestFit)
    val virtualMachine_bestFit = (1 to vm).map{_=>createVm()}.toList
    val cloudletList_bestFit = ListBuffer.empty [Cloudlet]
    (1 to cloudletnum).map(i=>{cloudletList_bestFit.addAll(createCloudlets(i))})
    broker_bestFit.submitCloudletList(cloudletList_bestFit.asJava)
    broker_bestFit.submitVmList(virtualMachine_bestFit.asJava)
    logger.info("Starting the CloudSimulation3 for Experiment 1")
    cloudSim_bestFit.start()
    logger.info("Finishing the CloudSimulation3 for Experiment 1")
    new CloudletsTableBuilder(broker_bestFit.getCloudletFinishedList()).build()
    printCost(broker_bestFit)

    /**
     * Simulation for First-fit VmAllocation Method
     */
    val cloudSim_firstFit = new CloudSim()
    val broker_firstFit = new DatacenterBrokerSimple(cloudSim_firstFit)
    val datacenter_firstFit = createDatacenter_firstFit(cloudSim_firstFit)
    val virtualMachine_firstFit = (1 to vm).map{_=>createVm()}.toList
    val cloudletList_firstFit = ListBuffer.empty [Cloudlet]
    (1 to cloudletnum).map(i=>{cloudletList_firstFit.addAll(createCloudlets(i))})
    broker_firstFit.submitCloudletList(cloudletList_firstFit.asJava)
    broker_firstFit.submitVmList(virtualMachine_firstFit.asJava)
    logger.info("Starting the CloudSimulation4 for Experiment 1")
    cloudSim_firstFit.start()
    logger.info("Finishing the CloudSimulation4 for Experiment 1")
    new CloudletsTableBuilder(broker_firstFit.getCloudletFinishedList()).build()
    printCost(broker_firstFit)

    /**
     * Simulation for First-fit VmAllocation Method
     */
    val cloudSim_Random = new CloudSim()
    val broker_Random = new DatacenterBrokerSimple(cloudSim_Random)
    val datacenter_Random = createDatacenter_Random(cloudSim_Random)
    val virtualMachine_Random = (1 to vm).map{_=>createVm()}.toList
    val cloudletList_Random = ListBuffer.empty [Cloudlet]
    (1 to cloudletnum).map(i=>{cloudletList_Random.addAll(createCloudlets(i))})
    broker_Random.submitCloudletList(cloudletList_Random.asJava)
    broker_Random.submitVmList(virtualMachine_Random.asJava)
    logger.info("Starting the CloudSimulation5 for Experiment 1")
    cloudSim_Random.start()
    logger.info("Finishing the CloudSimulation5 for Experiment 1")
    new CloudletsTableBuilder(broker_Random.getCloudletFinishedList()).build()
    printCost(broker_Random)

  }


  /**
   * This function creates new datacenter for the experiment. Can edit the datacenter configurations from the
   * application.conf Experiment1 file.
   * The VmAllocation policy for this datacenter is Simple
   * @param cloudSim : Input the cloudsim in which datacenter needs to be created.
   * @return Datacenter : Newly Created Datacenter.
   */
  def createDatacenter(cloudSim : CloudSim) : Datacenter = {
    val num_hosts = config.getString("Experiment1.CloudProviderProperties.datacenter.hosts")
    val cost = config.getString("Experiment1.CloudProviderProperties.datacenter.cost").toDouble
    val costPerMem = config.getString("Experiment1.CloudProviderProperties.datacenter.cpm").toDouble
    val costPerStorage = config.getString("Experiment1.CloudProviderProperties.datacenter.cps").toDouble
    val costPerBw = config.getString("Experiment1.CloudProviderProperties.datacenter.cpb").toDouble
    val schedulingInterval = config.getString("Experiment1.CloudProviderProperties.datacenter.scheduling_interval").toInt

    val hostList = (1 to 3).map{i=>createHost(i)}.toList
    val datacenter = new DatacenterSimple(cloudSim, hostList.asJava)
    datacenter.getCharacteristics().setCostPerSecond(cost).setCostPerMem(costPerMem)
      .setCostPerStorage(costPerStorage).setCostPerBw(costPerBw)
    datacenter.setVmAllocationPolicy(new VmAllocationPolicySimple())
    logger.info("Created datacenter "+ datacenter.getName+"with host count"+datacenter.getHostList().size())
    datacenter.setSchedulingInterval(schedulingInterval)
  }

  /**
   * This function creates new datacenter for the experiment. Can edit the datacenter configurations from the
   * application.conf Experiment1 file.
   * The VmAllocation policy for this datacenter is Round Robin
   * @param cloudSim : Input the cloudsim in which datacenter needs to be created.
   * @return Datacenter : Newly Created Datacenter.
   */
  def createDatacenter_Round(cloudSim : CloudSim) : Datacenter = {
    val num_hosts = config.getString("Experiment1.CloudProviderProperties.datacenter.hosts")
    val cost = config.getString("Experiment1.CloudProviderProperties.datacenter.cost").toDouble
    val costPerMem = config.getString("Experiment1.CloudProviderProperties.datacenter.cpm").toDouble
    val costPerStorage = config.getString("Experiment1.CloudProviderProperties.datacenter.cps").toDouble
    val costPerBw = config.getString("Experiment1.CloudProviderProperties.datacenter.cpb").toDouble

    val hostList = (1 to 3).map{i=>createHost(i)}.toList
    val datacenter = new DatacenterSimple(cloudSim, hostList.asJava)
    datacenter.getCharacteristics().setCostPerSecond(cost).setCostPerMem(costPerMem)
      .setCostPerStorage(costPerStorage).setCostPerBw(costPerBw)
    datacenter.setVmAllocationPolicy(new VmAllocationPolicyRoundRobin())
    logger.info("Created datacenter "+ datacenter.getName+"with host count"+datacenter.getHostList().size())
    datacenter
  }

  /**
   * This function creates new datacenter for the experiment. Can edit the datacenter configurations from the
   * application.conf Experiment1 file.
   * The VmAllocation policy for this datacenter is Best Fit
   * @param cloudSim : Input the cloudsim in which datacenter needs to be created.
   * @return Datacenter : Newly Created Datacenter.
   */
  def createDatacenter_bestFit(cloudSim : CloudSim) : Datacenter = {
    val num_hosts = config.getString("Experiment1.CloudProviderProperties.datacenter.hosts")
    val cost = config.getString("Experiment1.CloudProviderProperties.datacenter.cost").toDouble
    val costPerMem = config.getString("Experiment1.CloudProviderProperties.datacenter.cpm").toDouble
    val costPerStorage = config.getString("Experiment1.CloudProviderProperties.datacenter.cps").toDouble
    val costPerBw = config.getString("Experiment1.CloudProviderProperties.datacenter.cpb").toDouble

    val hostList = (1 to 3).map{i=>createHost(i)}.toList
    val datacenter = new DatacenterSimple(cloudSim, hostList.asJava)
    datacenter.getCharacteristics().setCostPerSecond(cost).setCostPerMem(costPerMem)
      .setCostPerStorage(costPerStorage).setCostPerBw(costPerBw)
    datacenter.setVmAllocationPolicy(new VmAllocationPolicyBestFit())
    logger.info("Created datacenter "+ datacenter.getName+"with host count"+datacenter.getHostList().size())
    datacenter
  }

  /**
   * This function creates new datacenter for the experiment. Can edit the datacenter configurations from the
   * application.conf Experiment1 file.
   * The VmAllocation policy for this datacenter is First Fit
   * @param cloudSim : Input the cloudsim in which datacenter needs to be created.
   * @return Datacenter : Newly Created Datacenter.
   */
  def createDatacenter_firstFit(cloudSim : CloudSim) : Datacenter = {
    val num_hosts = config.getString("Experiment1.CloudProviderProperties.datacenter.hosts").toInt
    val cost = config.getString("Experiment1.CloudProviderProperties.datacenter.cost").toDouble
    val costPerMem = config.getString("Experiment1.CloudProviderProperties.datacenter.cpm").toDouble
    val costPerStorage = config.getString("Experiment1.CloudProviderProperties.datacenter.cps").toDouble
    val costPerBw = config.getString("Experiment1.CloudProviderProperties.datacenter.cpb").toDouble

    val hostList = (1 to num_hosts).map{i=>createHost(i)}.toList
    val datacenter = new DatacenterSimple(cloudSim, hostList.asJava)
    datacenter.getCharacteristics().setCostPerSecond(cost).setCostPerMem(costPerMem)
      .setCostPerStorage(costPerStorage).setCostPerBw(costPerBw)
    datacenter.setVmAllocationPolicy(new VmAllocationPolicyFirstFit())
    logger.info("Created datacenter "+ datacenter.getName+"with host count"+datacenter.getHostList().size())
    datacenter
  }

  /**
   * This function creates new datacenter for the experiment. Can edit the datacenter configurations from the
   * application.conf Experiment1 file.
   * The VmAllocation policy for this datacenter is the Custom Random vMAllocation
   * @param cloudSim : Input the cloudsim in which datacenter needs to be created.
   * @return Datacenter : Newly Created Datacenter.
   */
  def createDatacenter_Random(cloudSim : CloudSim) : Datacenter = {
    val num_hosts = config.getString("Experiment1.CloudProviderProperties.datacenter.hosts").toInt
    val cost = config.getString("Experiment1.CloudProviderProperties.datacenter.cost").toDouble
    val costPerMem = config.getString("Experiment1.CloudProviderProperties.datacenter.cpm").toDouble
    val costPerStorage = config.getString("Experiment1.CloudProviderProperties.datacenter.cps").toDouble
    val costPerBw = config.getString("Experiment1.CloudProviderProperties.datacenter.cpb").toDouble

    val hostList = (1 to num_hosts).map{i=>createHost(i)}.toList
    val datacenter = new DatacenterSimple(cloudSim, hostList.asJava)
    datacenter.getCharacteristics().setCostPerSecond(cost).setCostPerMem(costPerMem)
      .setCostPerStorage(costPerStorage).setCostPerBw(costPerBw)
    datacenter.setVmAllocationPolicy(new VmAllocationPolicyRandom())
    logger.info("Created datacenter "+ datacenter.getName+"with host count"+datacenter.getHostList().size())
    datacenter
  }

  /**
   * Function creates the host for the cloudsim datacenter.
   * @param host_number : This number will read data from the application.conf file
   * @return host : returns a host
   */
  def createHost(host_number : Int ) ={
    val mips = config.getString("Experiment1.CloudProviderProperties.host"+host_number+".mipsCapacity").toInt
    val hostPe = config.getString("Experiment1.CloudProviderProperties.host"+host_number+".Pes").toInt
    val peList = (1 to hostPe).map{_=>new PeSimple(mips).asInstanceOf[Pe]}.toList
    val hostRam = config.getString("Experiment1.CloudProviderProperties.host"+host_number+".RAMInMBs").toInt
    val storage = config.getString("Experiment1.CloudProviderProperties.host"+host_number+".StorageInMBs").toInt
    val host_bw = config.getString("Experiment1.CloudProviderProperties.host"+host_number+".BandwidthInMBps").toInt
    val host = new HostSimple(hostRam,host_bw,storage,peList.asJava)
    logger.info("Created Host "+ host.getId)
    host.setVmScheduler(new VmSchedulerSpaceShared())
  }

  /**
   * Function creates the vm for the cloudsim. Can edit the vM configuration in the application.conf.Experiment1.vm
   * @return Vm  : returns the Vm created
   */
  def createVm() : Vm ={
    val virtualMachine_Mips = config.getString("Experiment1.CloudProviderProperties.vm.mipsCapacity").toInt
    val virtualMachine_Pes = config.getString("Experiment1.CloudProviderProperties.vm.pes").toInt
    val virtualMachine_Ram = config.getString("Experiment1.CloudProviderProperties.vm.RAMInMBs").toInt
    val virtualMachine_Bw = config.getString("Experiment1.CloudProviderProperties.vm.BandwidthInMBps").toInt
    val virtualMachine_Size = config.getString("Experiment1.CloudProviderProperties.vm.StorageInMBs").toInt
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
    val utilRatio = config.getString("Experiment1.utilizationRatio").toDouble
    val MaxResourceUtil = config.getString("Experiment1.maxResourceRatio").toDouble
    val utilModel = new UtilizationModelDynamic(utilRatio).setMaxResourceUtilization(MaxResourceUtil)
    val cloudletNumber = config.getString("Experiment1.BrokerProperties.cloudlet"+cloudLetNumber+".number").toInt
    val cloudletList = ListBuffer.empty [Cloudlet]
    create(cloudletNumber, utilModel, cloudletList)

    def create(number:Int, model:UtilizationModelDynamic, list : ListBuffer[Cloudlet]) : Unit ={
      if(number == 0) return
      val cloudlet_Pes = config.getString("Experiment1.BrokerProperties.cloudlet"+cloudLetNumber+".pes").toInt
      val cloudlet_Size = config.getString("Experiment1.BrokerProperties.cloudlet"+cloudLetNumber+".size").toInt
      val cloudlet_FileSize = config.getString("Experiment1.BrokerProperties.cloudlet"+cloudLetNumber+".filesize").toInt
      val cloudlet = new CloudletSimple(cloudlet_Size, cloudlet_Pes, model).setSizes(cloudlet_FileSize)
      logger.info("Created cloudlet"+cloudlet.getId)
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
