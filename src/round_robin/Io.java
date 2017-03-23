package round_robin;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class implements functionality associated with
 * the I/O device of the simulated system.
 */
public class Io {
    private Process activeProcess = null;
    
    private ConcurrentLinkedQueue<Process> ioQueue;
    private long avgIoTime;
    private Statistics statistics;

    /**
     * Creates a new I/O device with the given parameters.
     * @param ioQueue		The I/O queue to be used.
     * @param avgIoTime		The average duration of an I/O operation.
     * @param statistics	A reference to the statistics collector.
     */
    public Io(ConcurrentLinkedQueue<Process> ioQueue, long avgIoTime, Statistics statistics) {
        this.ioQueue = ioQueue;
        this.avgIoTime = avgIoTime;
        this.statistics = statistics;
    }

    /**
     * Adds a process to the I/O queue, and initiates an I/O operation
     * if the device is free.
     * @param requestingProcess	The process to be added to the I/O queue.
     * @param clock				The time of the request.
     * @return					The event ending the I/O operation, or null
     *							if no operation was initiated.
     */
    public Event addIoRequest(Process requestingProcess, long clock) {
    	//Add process to ioQueue.
    	ioQueue.add(requestingProcess);
    	//Update process nofTimesInIoQueue variable.
    	requestingProcess.addToIoQueue(clock);
        //Check if current ioQueue length is larger then ioQueueLargestLength and if it is update value with current ioQueue length.
    	if (statistics.ioQueueLargestLength < ioQueue.size()) {
    		statistics.ioQueueLargestLength++;    		
    	}
        //Check if there is no activeProcess.
    	if (activeProcess == null) {
    		//Start IO operation
    		return startIoOperation(clock);
        }
    	//If no operation was initiated return null.
        return null;
    }

    /**
     * Starts a new I/O operation if the I/O device is free and there are
     * processes waiting to perform I/O.
     * @param clock		The global time.
     * @return			An event describing the end of the I/O operation that was started,
     *					or null	if no operation was initiated.
     */
    public Event startIoOperation(long clock) {
    	if (activeProcess == null) {
    		if (!ioQueue.isEmpty()) {
    			//Set activeProcess as first element in ioQueue.
    			activeProcess = (Process)ioQueue.poll();
    			//Update process timeSpentWaitingForIo and timeOfLastEvent variable.
    			activeProcess.enterIo(clock);
    			//Generate new event END_IO as process will be finished with its I/O operation.
    			return new Event(Event.END_IO, clock + (long) (2 * Math.random() * avgIoTime));
    		}    		
    	}
    	//If no operation was initiated return null.
        return null;
    }

    /**
     * This method is called when a discrete amount of time has passed.
     * @param timePassed	The amount of time that has passed since the last call to this method.
     */
    public void timePassed(long timePassed) {
    	//Update ioQueueLengthTime statistics whenever timePassed function is called.
    	statistics.ioQueueLengthTime += ioQueue.size() * timePassed;
    }

    /**
     * Removes the process currently doing I/O from the I/O device.
     * @return	The process that was doing I/O, or null if no process was doing I/O.
     */
    public Process removeActiveProcess(long clock) {
    	//Check if there is an active process
    	if(activeProcess != null){
    		//Save the process that is currently performing I/O.
    		Process p = activeProcess;
        	//Update process timeSpentInIo and timeOfLastEvent variables and generate a new time until next IO operation.
    		p.exitIo(clock);
    		//Update nofProcessedIoOperations variable.
    		statistics.nofProcessedIoOperations++;
    		//Remove active process
    		activeProcess = null;
    		//Return the active process.
    		return p;
    	}
		//If no process was doing I/O return null.
        return null;
    }
    
    /**
     * Returns the process currently using the I/O.
     * @return	The process currently using the I/O.
     */
    public Process getActiveProcess() {
    	//Return the activeProcess.
        return activeProcess;
    }
}
