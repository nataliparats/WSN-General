package se.kth.ssvl.tslab.wsn.general.bps;

public class BPS {
	
	/* ********* MEMBER VARIABLES ********** */
	private static BPS instance;
	
	private static BPSService service; 
	
	/* ********* INITIALIZATION AND CONSTRUCTOR********** */
	/**
	 * This method will return an singleton instance of BPS, 
	 * which is used as a main entry point to the library. Note: Must be called after init() method
	 * @return The BPS singleton
	 * @throws BPSException Throws and exception if BPS has not been initialized using init() method
	 */
	public static BPS getInstance() throws BPSException {
		if (instance == null) {
			if (service == null) {
				throw new BPSException("BPS was not initialized with a BPSService before being used");
			}
			instance = new BPS(service);
		}
		
		return instance;
	}
	
	
	/**
	 * The init method will take in a BPSService which needs to
	 *  implement methods for getting different BPS classes (e.g BPSLogger).
	 * The library will use the different classes as callbacks, since the 
	 * 	implementation must be implemented outside the BPS library.  
	 * @param service The BPSService implementation.
	 * @throws BPSException 
	 */
	public void init(BPSService _service) throws BPSException {
		if (_service == null) {
			throw new BPSException("BPSService cannot be null, we really need this guy to work!");
		}
		instance = new BPS(_service);
	}
	
	/**
	 * Constructor for BPS, which is private since BPS is a singleton.
	 * @param service The BPSService implementation that the BPS library will use for device-specific methods.
	 */
	private BPS(BPSService _service) {
		service = _service;
	}
	
	
	/* ********* GETTER METHODS FOR BPS CLASSES ********** */	
	/**
	 * Gets the BPSCommunication object passed in from the BPSService.
	 * @return The BPSCommunication object in the BPSService
	 */
	public BPSCommunication getBPSCommunication() {
		return service.getBPSCommunication();
	}
	
	/**
	 * Gets the BPSLogger object passed in from the BPSService.
	 * @return The BPSLogger object in the BPSService
	 */
	public BPSLogger getBPSLogger() {
		return service.getBPSLogger();
	}
	
	/**
	 * Gets the BPSNotificationReceiver object passed in from the BPSService.
	 * @return The BPSNotificationReceiver object in the BPSService
	 */
	public BPSNotificationReceiver getBPSNotificationReceiver() {
		return service.getBPSNotificationReceiver();
	}
	
	
	
}
