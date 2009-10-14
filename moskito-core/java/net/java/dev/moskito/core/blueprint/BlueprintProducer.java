package net.java.dev.moskito.core.blueprint;

import java.util.ArrayList;
import java.util.List;

import net.java.dev.moskito.core.predefined.ActionStats;
import net.java.dev.moskito.core.predefined.Constants;
import net.java.dev.moskito.core.producers.IStats;
import net.java.dev.moskito.core.producers.IStatsProducer;
import net.java.dev.moskito.core.registry.ProducerRegistryFactory;
import net.java.dev.moskito.core.stats.Interval;
import net.java.dev.moskito.core.usecase.running.ExistingRunningUseCase;
import net.java.dev.moskito.core.usecase.running.PathElement;
import net.java.dev.moskito.core.usecase.running.RunningUseCase;
import net.java.dev.moskito.core.usecase.running.RunningUseCaseContainer;

/**
 * This special producer type is used whenever you have monitorable objects which are created on request and should be recycled afterwards (for example command pattern, or struts 2 actions).
 * The problem with the per-request-creation of objects is that if you tie the usual moskito producer to them, they remain registered in the producer registry and your app leaks memory, since 
 * the objects never get collected. The solution is to use the BlueprintProducer which is used to gather the stats for a category or family of objects. You can think of blueprintproducer - object relationship 
 * as of object and its class. 
 * @author lrosenberg
 *
 */
public class BlueprintProducer implements IStatsProducer{
	/**
	 * The id of the producer
	 */
	private String producerId;
	/**
	 * The category of the producer
	 */
	private String category;
	/**
	 * The name of the subsystem.
	 */
	private String subsystem;
	
	/**
	 * Producer stats object. The default blueprint producer supports only one method - execute, and uses actionstats for storage since its the most appropriate stats object (used for struts1 actions).
	 */
	private ActionStats stats;

	/**
	 * The list of the stats. Contains one element.
	 */
	private List<IStats> statsList;
	
	/**
	 * Creates a new BlueprintProducer object with given parameters
	 * @param aProducerId a producer id
	 * @param aCategory the name of the category (for presentation needs)
	 * @param aSubsystem the name of the subsystem (for presentation needs)
	 */
	public BlueprintProducer(String aProducerId, String aCategory, String aSubsystem){
		producerId = aProducerId;
		category = aCategory;
		subsystem = aSubsystem;
		
		stats = new ActionStats("execute", getMonitoringIntervals());
		statsList = new ArrayList<IStats>();
		statsList.add(stats);
		ProducerRegistryFactory.getProducerRegistryInstance().registerProducer(this);

	}
	
	/**
	 * Called by the surrounding code whenever the action which we are measuring is actually executed.
	 * @param executor the object the action is executed on
	 * @param parameters parameters for the action
	 * @return
	 * @throws Exception
	 */
	public Object execute(BlueprintCallExecutor executor, Object... parameters) throws Exception{
		stats.addRequest();
		long startTime = System.nanoTime();
		RunningUseCase aRunningUseCase = RunningUseCaseContainer.getCurrentRunningUseCase();
		PathElement currentElement = null;
		ExistingRunningUseCase runningUseCase = aRunningUseCase.useCaseRunning() ? 
				(ExistingRunningUseCase)aRunningUseCase : null; 
		if (runningUseCase !=null)
			currentElement = runningUseCase.startPathElement(new StringBuilder(getProducerId()).append('.').append("execute").toString());
		try {
			return executor.execute(parameters);
		}  catch (Exception e) {
			stats.notifyError();
			throw e;
		} finally {
			long duration = System.nanoTime() - startTime;
			stats.addExecutionTime(duration);
			stats.notifyRequestFinished();
			if (currentElement!=null)
				currentElement.setDuration(duration);
			if (runningUseCase !=null)
				runningUseCase.endPathElement();
		}
		
	}
	
	
	public String getCategory() {
		return category;
	}

	public String getProducerId() {
		return producerId;
	}

	public List<IStats> getStats() {
		return statsList;
	}

	public String getSubsystem() {
		return subsystem;
	}
	
	protected Interval[] getMonitoringIntervals(){
		return Constants.DEFAULT_INTERVALS;
	}

	
}
