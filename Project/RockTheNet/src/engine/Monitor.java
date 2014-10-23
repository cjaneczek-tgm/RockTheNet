package engine;

import api.Stoppable;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.snmp4j.smi.OID;

/**
 * This class is used as a thread that monitors the throughput.
 * It gets launched when the policies are read for the first time and
 * then continously updates the throughput within the defined (Properties) time intervall
 * @author Adrian Bergler
 * @version 2014-10-23
 */
public class Monitor implements Stoppable, Runnable {

	private Map<OID,List<Long>> throughput;

	private Monitor monitor;
	
	/*
	 * Singleton at the moment.
	 * Not exactly sure if this is the best idea, but it totally makes sense because:
	 * 	- You only need one monitor for the required task.
	 * 	- It is way easier to ensure that the monitor is only launched once
	 * 		and is launched when the policies are read for the first time
	 */
	private Monitor(){
		throughput = new HashMap<OID,List<Long>>();
	}
	
	/**
	 * Returns the monitor-object
	 * @return the monitor-object
	 */
	public Monitor get(){
		if(monitor == null) monitor = new Monitor();
		return monitor;
	}
	
	public List<Long> getCurrentThroughput() {
		return null;
	}


	/**
	 * @see api.Stoppable#stop()
	 */
	@Override
	public void stop() {
		//TODO
	}


	/**
	 * Runs this thread. ORLY.
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
