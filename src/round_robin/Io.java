package round_robin;

import java.util.LinkedList;

/**
 * This class implements functionality associated with
 * the I/O device of the simulated system.
 */
public class Io {
    private Process activeProcess = null;
    
    private LinkedList<Process> ioQueue;
    private long avgIoTime;
    private Statistics statistics;

    /**
     * Creates a new I/O device with the given parameters.
     * @param ioQueue		The I/O queue to be used.
     * @param avgIoTime		The average duration of an I/O operation.
     * @param statistics	A reference to the statistics collector.
     */
    public Io(LinkedList<Process> ioQueue, long avgIoTime, Statistics statistics) {
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
    	if (getActiveProcess() == null) {
        	activeProcess = ioQueue.pop();
        	activeProcess.enterIo(clock);
        	//TODO add event for process.
        	return startIoOperation(clock);
        }
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
    	if(getActiveProcess()==null){
        	activeProcess = ioQueue.pop();
        	activeProcess.addToIoQueue(clock);
        	statistics.nofProcessedIoOperations++;
        	
        	return new Event(Event.END_IO, (long) (Math.random() * avgIoTime * 2)+clock);
        }
        return null;
    }

    /**
     * This method is called when a discrete amount of time has passed.
     * @param timePassed	The amount of time that has passed since the last call to this method.
     */
    public void timePassed(long timePassed) {
    	statistics.ioQueueLengthTime += ioQueue.size()*timePassed;
        if (ioQueue.size() > statistics.ioQueueLargestLength){
        	statistics.ioQueueLargestLength = ioQueue.size();
        }
    }

    /**
     * Removes the process currently doing I/O from the I/O device.
     * @return	The process that was doing I/O, or null if no process was doing I/O.
     */
    public Process removeActiveProcess(long clock) {
    	Process p = getActiveProcess();
    	if(getActiveProcess()!=null){
    		p.exitIo(clock);
    		activeProcess = null;
    	}
        return p;
    }

    public Process getActiveProcess() {
        return activeProcess;
    }
    
  //Generates events for processes in the IO segment of the system, depending on their variables.
    public Event generateEvent(Process p, long clock) {
    	return null;
    }
}
