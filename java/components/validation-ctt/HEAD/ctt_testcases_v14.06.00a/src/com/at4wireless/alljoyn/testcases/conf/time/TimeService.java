package com.at4wireless.alljoyn.testcases.conf.time;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.parsers.ParserConfigurationException;


import org.alljoyn.bus.BusException;
import org.alljoyn.bus.ErrorReplyBusException;
import org.alljoyn.bus.Status;
import org.alljoyn.services.common.BusObjectDescription;
import org.allseen.timeservice.Date;
import org.allseen.timeservice.DateTime;
import org.allseen.timeservice.Pair;
import org.allseen.timeservice.Period;
import org.allseen.timeservice.Schedule;
import org.allseen.timeservice.Schedule.WeekDay;
import org.allseen.timeservice.Time;
import org.allseen.timeservice.client.Alarm;
import org.allseen.timeservice.client.Alarm.AlarmHandler;
import org.allseen.timeservice.client.AlarmFactory;
import org.allseen.timeservice.client.Clock;
import org.allseen.timeservice.client.SessionListenerHandler;
import org.allseen.timeservice.client.TimeServiceClient;
import org.allseen.timeservice.client.Timer;
import org.allseen.timeservice.client.Timer.TimerHandler;
import org.allseen.timeservice.client.TimerFactory;
import org.xml.sax.SAXException;

import com.at4wireless.alljoyn.core.about.AboutAnnouncementDetails;
import com.at4wireless.alljoyn.core.commons.ServiceHelper;
import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;
import com.at4wireless.alljoyn.core.interfacevalidator.InterfaceValidator;
import com.at4wireless.alljoyn.core.interfacevalidator.ValidationResult;
import com.at4wireless.alljoyn.core.introspection.BusIntrospector;
import com.at4wireless.alljoyn.core.introspection.XmlBasedBusIntrospector;
import com.at4wireless.alljoyn.core.introspection.bean.InterfaceDetail;
import com.at4wireless.alljoyn.core.introspection.bean.IntrospectionInterface;
import com.at4wireless.alljoyn.core.introspection.bean.IntrospectionNode;
import com.at4wireless.alljoyn.core.introspection.bean.NodeDetail;

public class TimeService {


	 Boolean pass=true;

	protected  final String TAG = "TimeServiceTestSuite";
	private  final WindowsLoggerImpl logger =  new WindowsLoggerImpl(TAG);

	private  final String INTROSPECTABLE_INTERFACE_NAME = "org.allseen.Introspectable";


	private  final String BUS_APPLICATION_NAME = "TimeServiceTestSuite";
	private  final String ERROR_MSG_BUS_INTROSPECTION = "Encountered exception while trying to introspect the bus";
	private  final String DBUS_ERROR_SERVICE_UNKNOWN = "org.freedesktop.DBus.Error.ServiceUnknown";

	protected  AboutAnnouncementDetails deviceAboutAnnouncement;
	private  ServiceHelper serviceHelper;
	private  TimeServiceClient timeServiceClient;
	private  SimpleDateFormat simpleDateFormat;
	private  long ANNOUCEMENT_TIMEOUT_IN_SECONDS = 30;
	private  final String DATE_FORMAT = "yyyy-MM-dd";

	// private  TimeServiceClient timeServiceClient;


	private  UUID dutAppId;
	private  String dutDeviceId;

	private  BlockingQueue<Pair<TimeServiceClient, Status>> joinSessionQueue = new ArrayBlockingQueue<Pair<TimeServiceClient, Status>>(1);


	private  SimpleDateFormat dateAndTimeDisplayFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");

	//private BlockingQueue<Pair<TimeServiceClient, Status>> joinSessionQueue = new ArrayBlockingQueue<Pair<TimeServiceClient, Status>>(1);


	 boolean ICST_TimeServiceFramework=false;
	 boolean ICST_ClockInterface=false;
	 boolean ICST_Date=false;
	 boolean ICST_Milliseconds=false;
	 boolean ICST_TimeAuthorityInterface=false;
	 boolean ICST_AlarmFactoryInterface=false;
	 boolean ICST_AlarmInterface=false;
	 boolean ICST_TimerFactoryInterface=false;
	 boolean ICST_TimerInterface=false;

	////////////////
	 String IXITCO_AppId=null;

	 String IXITCO_DeviceId=null;

	 String IXITCO_DefaultLanguage=null;



	////////////////

	 String IXITT_ClockVersion=null;
	 String IXITT_TimeAuthorityVersion=null;
	 String IXITT_AlarmFactoryVersion=null;

	 String IXITT_AlarmVersion=null;
	 String IXITT_TimerFactoryVersion=null;
	 String IXITT_TimerVersion=null;

	public TimeService(String testCase, boolean iCST_TimeServiceFramework,
			boolean iCST_ClockInterface, boolean iCST_Date,
			boolean iCST_Milliseconds, boolean iCST_TimeAuthorityInterface,
			boolean iCST_AlarmFactoryInterface, boolean iCST_AlarmInterface,
			boolean iCST_TimerFactoryInterface, boolean iCST_TimerInterface,
			String iXITCO_AppId, String iXITCO_DeviceId,
			String iXITCO_DefaultLanguage, String iXITT_ClockVersion,
			String iXITT_TimeAuthorityVersion,
			String iXITT_AlarmFactoryVersion, String iXITT_AlarmVersion,
			String iXITT_TimerFactoryVersion, String iXITT_TimerVersion,
			String gPCO_AnnouncementTimeout) {


		ICST_TimeServiceFramework=iCST_TimeServiceFramework;
		ICST_ClockInterface=iCST_ClockInterface;
		ICST_Date=iCST_Date;
		ICST_Milliseconds=iCST_Milliseconds;
		ICST_TimeAuthorityInterface=iCST_TimeAuthorityInterface;
		ICST_AlarmFactoryInterface=iCST_AlarmFactoryInterface;
		ICST_AlarmInterface=iCST_AlarmInterface;
		ICST_TimerFactoryInterface=iCST_TimerFactoryInterface;
		ICST_TimerInterface=iCST_TimerInterface;
		IXITCO_AppId=iXITCO_AppId;
		IXITCO_DeviceId=iXITCO_DeviceId;
		IXITCO_DefaultLanguage=iXITCO_DefaultLanguage;
		IXITT_ClockVersion=iXITT_ClockVersion;
		IXITT_TimeAuthorityVersion=iXITT_TimeAuthorityVersion;
		IXITT_AlarmFactoryVersion=iXITT_AlarmFactoryVersion;
		IXITT_AlarmVersion=iXITT_AlarmVersion;
		IXITT_TimerFactoryVersion=iXITT_TimerFactoryVersion;
		IXITT_TimerVersion=iXITT_TimerVersion;
		
		ANNOUCEMENT_TIMEOUT_IN_SECONDS = Integer.parseInt(gPCO_AnnouncementTimeout);

		try{
			runTestCase(testCase);
					}catch(Exception e){
			
			//if(e.getMessage().equals("Timed out waiting for About announcement")){
				//fail("Timed out waiting for About announcement");
			//}else{
				fail("Exception: "+e.toString());
			//}
		}
	}
	public  void runTestCase(String testCase) throws Exception{
		setUp(IXITCO_DeviceId,IXITCO_AppId);		
		if(testCase.equals("TimeService-v1-01")){
			testTime_v1_01_GetObjectDescription();
		}else if(testCase.equals("TimeService-v1-02")){
			testTime_v1_02_VerifyClocks();
		}else if(testCase.equals("TimeService-v1-03")){
			testTime_v1_03_VerifyTimers();
		}else if(testCase.equals("TimeService-v1-04")){
			testTime_v1_04_VerifyAlarms();
		}else {
			fail("TestCase not valid");
		}
		tearDown();
	}


	private  void setUp(String iXITCO_DeviceId, String iXITCO_AppId) throws Exception {
		logger.debug("test setUp started");

		try {

			dutDeviceId = iXITCO_DeviceId;
			logger.debug("Running Time Service test case against Device ID: "+ dutDeviceId);
			dutAppId = UUID.fromString(iXITCO_AppId);
			
			logger.debug("Running Time Service test case against App ID: "+ dutAppId);

			serviceHelper = getServiceHelper();
			serviceHelper.initialize(BUS_APPLICATION_NAME, dutDeviceId, dutAppId);

			deviceAboutAnnouncement = serviceHelper.waitForNextDeviceAnnouncement(ANNOUCEMENT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
			assertNotNull("Timed out waiting for About announcement", deviceAboutAnnouncement);

			timeServiceClient = new TimeServiceClient(serviceHelper.getBusAttachment(), deviceAboutAnnouncement.getServiceName(), deviceAboutAnnouncement.getDeviceId(),
					deviceAboutAnnouncement.getAppId(), deviceAboutAnnouncement.getObjectDescriptions());

			timeServiceClient.joinSessionAsync(new SessionListenerHandler() {
				@Override
				public void sessionLost(int arg0, TimeServiceClient arg1) {
				}

				@Override
				public void sessionJoined(TimeServiceClient arg0, Status arg1) {
					joinSessionQueue.add(new Pair<TimeServiceClient, Status>(arg0,arg1));
				}
			});

			// not if timeout will throw Interrupt Exception.
			Pair<TimeServiceClient, Status> joinRes = joinSessionQueue.poll(ANNOUCEMENT_TIMEOUT_IN_SECONDS * 2, TimeUnit.SECONDS);

			assertEquals(Status.OK, joinRes.second);

		} catch (Exception exception) {
			try {
				releaseResources();
			} catch (Exception newException) {
				logger.debug("Exception releasing resources: "+ newException.toString());
			}

			throw exception;
		}
	}













	public  void testTime_v1_01_GetObjectDescription() throws Exception {

		List<InterfaceDetail> standardizedIntrospectionInterfacesExposedOnBus = getIntrospector().getStandardizedInterfacesExposedOnBus();
		for (InterfaceDetail objectDetail : standardizedIntrospectionInterfacesExposedOnBus) {
			for (IntrospectionInterface intfName : objectDetail.getIntrospectionInterfaces()) {
				logger.debug(String.format("Found object at %s implementing %s", objectDetail.getPath(), intfName.getName()));
			}
		}
		ValidationResult validationResult = getInterfaceValidator().validate(standardizedIntrospectionInterfacesExposedOnBus);

		assertTrue(validationResult.getFailureReason(), validationResult.isValid());

		BusIntrospector busIntrospector = getIntrospector();
		Set<String> announcementPathInterfaceSet = new HashSet<String>();
		Set<String> busIntrospectPathInterfaceSet = new HashSet<String>();

		for (BusObjectDescription busObjectDescription : deviceAboutAnnouncement.getObjectDescriptions()) {
			String path = busObjectDescription.path;
			populateAnnouncementPathInterfaceSet(announcementPathInterfaceSet, busObjectDescription, path);
			populateBusIntrospectPathInterfaceSet(busIntrospector, busIntrospectPathInterfaceSet, path);
		}

		for (String announcementKey : announcementPathInterfaceSet) {
			String[] pathAndInterfaces = announcementKey.split(":");
			if (!pathAndInterfaces[1].equals(INTROSPECTABLE_INTERFACE_NAME)) {
				String errorMessage = new StringBuilder("AboutAnnouncement advertises interface ").append(pathAndInterfaces[1]).append(" at path ").append(pathAndInterfaces[0])
						.append(", but bus does not contain such interface at that path.").toString();
				assertTrue(errorMessage, busIntrospectPathInterfaceSet.contains(announcementKey));
				List<InterfaceDetail> x = busIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(pathAndInterfaces[0], pathAndInterfaces[1]);
				validationResult = getInterfaceValidator().validate(x);
				logger.debug("Validation of " + pathAndInterfaces[0] + " " + pathAndInterfaces[1] + " is " + validationResult.isValid() + " resaon : " + validationResult.getFailureReason());
				assertTrue(validationResult.getFailureReason(), validationResult.isValid());
			}
		}

	} 





	public  void testTime_v1_02_VerifyClocks() throws Exception {

		ValidationResult validationResult;
		List<InterfaceDetail> interfaceDetailList;
		String interfaceNameTimeAuthority = "org.allseen.Time.TimeAuthority";
		String interfaceNameClock = "org.allseen.Time.Clock";
		String interfacename;
		BusIntrospector busIntrospector = getIntrospector();

		if (timeServiceClient.isClockAnnounced()) {

			for (Clock clock : timeServiceClient.getAnnouncedClockList()) {
				if (clock.isAuthority()) {
					interfacename = interfaceNameTimeAuthority;
				} else {
					interfacename = interfaceNameClock;
				}
				interfaceDetailList = busIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(clock.getObjectPath(), interfacename);
				validationResult = getInterfaceValidator().validate(interfaceDetailList);
				logger.debug("Validation of " + clock.getObjectPath() + " " + interfacename + " is " + validationResult.isValid());
				assertTrue(validationResult.getFailureReason(), validationResult.isValid());
				new ClockChecker(clock).validate();
			}
		}

	}



	public  class ClockChecker {
		org.allseen.timeservice.client.Clock currentClock;

		ClockChecker(org.allseen.timeservice.client.Clock clock) {
			this.currentClock = clock;
		}

		public void validate() throws Exception {
			logger.debug("ClockChecker vlaidate");
			checkClockSet();
		}

		private void checkClockSet() throws Exception {
			final Calendar calander = Calendar.getInstance();

			DateTime currentDateTime = convertCalendarToDateTime(calander);

			boolean checkIsSet = false;
			if (!currentClock.retrieveIsSet()) {
				checkIsSet = true;
			}
			currentClock.setDateTime(currentDateTime);
			if (checkIsSet) {
				assertEquals("Clock was not correctly check",true, currentClock.retrieveIsSet());
			}
			Calendar receivdTime = convertDateTimeToCalendar(currentClock.retrieveDateTime());
			logger.debug("Set Time to " + dateAndTimeDisplayFormat.format(calander.getTime()) + " received " + dateAndTimeDisplayFormat.format(receivdTime.getTime()));
			assertEquals("Time was not set correctly",false, Math.abs(receivdTime.getTimeInMillis() - calander.getTimeInMillis()) > 1000 * 60);
		}



	}





	public  void testTime_v1_03_VerifyTimers() throws Exception {
		String interfaceNameTimer = "org.allseen.Time.Timer";
		ValidationResult validationResult;
		List<InterfaceDetail> interfaceDetailList;
		BusIntrospector busIntrospector = getIntrospector();

		if (timeServiceClient.isTimerFactoryAnnounced()) {
			for (TimerFactory timerFactory : timeServiceClient.getAnnouncedTimerFactoryList()) {
				for (org.allseen.timeservice.client.Timer timer : timerFactory.retrieveTimerList()) {
					interfaceDetailList = busIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(timer.getObjectPath(), interfaceNameTimer);
					validationResult = getInterfaceValidator().validate(interfaceDetailList);
					logger.debug("Validation of " + timer.getObjectPath() + " " + interfaceNameTimer + " is " + validationResult);
					assertTrue(validationResult.getFailureReason(), validationResult.isValid());
				}
				// check creation of new Timer and its deletion.
				int initalSize = timerFactory.retrieveTimerList().size();
				org.allseen.timeservice.client.Timer tempTimer = timerFactory.newTimer();
				assertEquals("Timer has been added correctly",initalSize + 1, timerFactory.retrieveTimerList().size());
				timerFactory.deleteTimer(tempTimer.getObjectPath());
				assertEquals("Timer has been deleted correctly",initalSize, timerFactory.retrieveTimerList().size());
			}
		}
		org.allseen.timeservice.client.Timer workingTimer = null;

		if (timeServiceClient.isTimerAnnounced()) {
			workingTimer = timeServiceClient.getAnnouncedTimerList().get(0);

		} else if (timeServiceClient.isTimerFactoryAnnounced()) {
			TimerFactory timerFactory = timeServiceClient.getAnnouncedTimerFactoryList().get(0);
			if (timerFactory.retrieveTimerList().size() > 0) {
				workingTimer = timerFactory.retrieveTimerList().get(0);
			} else {
				workingTimer = timerFactory.newTimer();
			}
		}
		if (workingTimer != null) {
			TimerChecker timerChecker = new TimerChecker(workingTimer);
			timerChecker.validate();
			timeServiceClient.release();
		}

	}




	public  void testTime_v1_04_VerifyAlarms() throws Exception {
		String interfaceNameAlarm = "org.allseen.Time.Alarm";
		ValidationResult validationResult;
		List<InterfaceDetail> interfaceDetailList;
		BusIntrospector busIntrospector = getIntrospector();
		if (timeServiceClient.isAlarmFactoryAnnounced()) {
			for (AlarmFactory alarmFactory : timeServiceClient.getAnnouncedAlarmFactoryList()) {
				for (org.allseen.timeservice.client.Alarm alarm : alarmFactory.retrieveAlarmList()) {
					interfaceDetailList = busIntrospector.getInterfacesExposedOnBusUnderSpecifiedPathBasedOnName(alarm.getObjectPath(), interfaceNameAlarm);
					validationResult = getInterfaceValidator().validate(interfaceDetailList);
					logger.debug("Validation of " + alarm.getObjectPath() + " " + interfaceNameAlarm + " is " + validationResult);
					assertTrue(validationResult.getFailureReason(), validationResult.isValid());
				}
				// check creation of new Timer and its deletion.
				int initalSize = alarmFactory.retrieveAlarmList().size();
				org.allseen.timeservice.client.Alarm tempAlarm = alarmFactory.newAlarm();
				assertEquals("Alarm was not added",initalSize + 1, alarmFactory.retrieveAlarmList().size());
				alarmFactory.deleteAlarm(tempAlarm.getObjectPath());
				assertEquals("Alarm was not deleted",initalSize, alarmFactory.retrieveAlarmList().size());
			}
		}
		org.allseen.timeservice.client.Alarm workingAlarm = null;

		if (timeServiceClient.isAlarmAnnounced()) {
			workingAlarm = timeServiceClient.getAnnouncedAlarmList().get(0);

		} else if (timeServiceClient.isAlarmFactoryAnnounced()) {
			AlarmFactory timerFactory = timeServiceClient.getAnnouncedAlarmFactoryList().get(0);
			if (timerFactory.retrieveAlarmList().size() > 0) {
				workingAlarm = timerFactory.retrieveAlarmList().get(0);
			} else {
				workingAlarm = timerFactory.newAlarm();
			}
		}
		if (workingAlarm != null) {
			AlarmChecker alarmChecker = new AlarmChecker(workingAlarm, timeServiceClient);
			alarmChecker.vlaidate();

		}

	}








	public  class AlarmChecker {
		private org.allseen.timeservice.client.Alarm currentAlarm;
		private String alarmCheckerTitle = "Alarm checker";
		private TimeServiceClient timeServiceClient;

		private CountDownLatch latch;
		private ScheduledFuture primaryScheduledFuture;
		private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

		public AlarmChecker(org.allseen.timeservice.client.Alarm alarm, TimeServiceClient timeServiceClient) {
			this.timeServiceClient = timeServiceClient;
			currentAlarm = alarm;
		}

		public void vlaidate() throws Exception {
			prepareForTest();
			test1();
		}

		boolean setClock(DateTime dateTime) throws Exception {
			if (timeServiceClient.isClockAnnounced() && timeServiceClient.getAnnouncedClockList().size() > 0) {
				for (Clock clock : timeServiceClient.getAnnouncedClockList()) {
					clock.setDateTime(dateTime);
				}
				return true;
			}
			return false;
		}

		private void prepareForTest() throws Exception {
			logger.debug("TimerChecker prepareForTest");
			if (currentAlarm.retrieveIsEnabled()) {
				currentAlarm.setEnabled(false);
				assertEquals("",false, currentAlarm.retrieveIsEnabled());
			}
			currentAlarm.setTitle(alarmCheckerTitle);

			Schedule schedule = new Schedule(new Time((byte) 12, (byte) 1, (byte) 0, (short) 0), new HashSet<WeekDay>());
			currentAlarm.setSchedule(schedule);
			assertEquals("",alarmCheckerTitle, currentAlarm.retrieveTitle());
			assertEquals("",schedule.toString(), currentAlarm.retrieveSchedule().toString());
		}

		private void startScheduler() throws Exception {

			latch = new CountDownLatch(1);
			primaryScheduledFuture = scheduledExecutorService.schedule(new Callable<Boolean>() {
				public Boolean call() throws Exception {
					currentAlarm.setEnabled(true);
					logger.debug("AlarmChekcer calling enable");
					latch.await();
					return true;
				}
			}, 1, TimeUnit.SECONDS);
			try {
				primaryScheduledFuture.get(70, TimeUnit.SECONDS);
			} catch (TimeoutException e) {
				throw new Exception("Alarm  didn't expire on time");
			}
		}

		private void test1() throws Exception {

			if (!setClock(new org.allseen.timeservice.DateTime(new org.allseen.timeservice.Date((short) 2014, (byte) 1, (byte) 5), new org.allseen.timeservice.Time((byte) 12, (byte) 0, (byte) 0,
					(short) 0), (short) 120))) {
				throw new TimeoutException("Alarm checker unable to set Clock");
			}

			currentAlarm.registerAlarmHandler(new AlarmHandler() {
				@Override
				public void handleAlarmReached(Alarm arg0) {
					logger.debug("AlarmChekcer handleAlarmReached");
					latch.countDown();
				}


			});

			logger.debug("AlarmChekcer Start test for 12:01:00:0000 5/1/2014 ALL WEEKDAYS");
			startScheduler();

			currentAlarm.setEnabled(false);
			currentAlarm.setSchedule(new Schedule(new Time((byte) 12, (byte) 1, (byte) 0, (short) 0), new HashSet<WeekDay>() {
				{
					add(WeekDay.THURSDAY);
					add(WeekDay.SATURDAY);
				}
			}));
			setClock(new org.allseen.timeservice.DateTime(new org.allseen.timeservice.Date((short) 2014, (byte) 1, (byte) 9),
					new org.allseen.timeservice.Time((byte) 12, (byte) 0, (byte) 0, (short) 0), (short) 120));

			logger.debug("AlarmChekcer Start test for 12:01:00:0000 9/1/2014 (THURSDAY) ");
			startScheduler();

			currentAlarm.setEnabled(false);
			setClock(new org.allseen.timeservice.DateTime(new org.allseen.timeservice.Date((short) 2014, (byte) 1, (byte) 11), new org.allseen.timeservice.Time((byte) 12, (byte) 0, (byte) 0,
					(short) 0), (short) 120));

			logger.debug("AlarmChekcer Start test for 12:01:00:0000 11/1/2014 (SATURDAY) ");
			startScheduler();
			scheduledExecutorService.shutdown();

		}
	}






	public  class TimerChecker {

		private org.allseen.timeservice.client.Timer currentTimer;
		private short repeat;
		private org.allseen.timeservice.Period period = new Period(0, (byte) 1, (byte) 0, (short) 0);
		private String TimerCheckerTitle = "TimerChecker";
		private Exception exception = null;
		private CountDownLatch latch = new CountDownLatch(1);
		private ScheduledFuture primaryScheduledFuture;
		private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

		private String running = "Running";
		private String idle = "Idle";
		private String finished = "Finished";
		private String ErrorMessage;

		private Vector<Pair<Long, String>> messageQueue = new Vector<Pair<Long, String>>();

		public TimerChecker(org.allseen.timeservice.client.Timer timer) {
			currentTimer = timer;
		}

		public void validate() throws Exception {

			prepareForTest();
			test1();
		}

		private void prepareForTest() throws Exception {
			logger.debug("TimerChecker prepareForTest");
			if (currentTimer.retrieveIsRunning()) {
				currentTimer.pause();
				assertEquals("Current timer was not paused ",false, currentTimer.retrieveIsRunning());
			}
			currentTimer.setTitle(TimerCheckerTitle);
			currentTimer.setInterval(period);
			currentTimer.setRepeat(repeat);
			currentTimer.reset();

			assertEquals("Timer title was not setted correctly",TimerCheckerTitle, currentTimer.retrieveTitle());
			assertEquals("Repeat was not setted correctly",repeat, currentTimer.retrieveRepeat());
			assertEquals("Interval was not setted correctly",period.toString(), currentTimer.retrieveInterval().toString());
			assertEquals("Timer title was not setted correctly",period.toString(), currentTimer.retrieveTimeLeft().toString());
		}

		private void test1() throws Exception {
			logger.debug("TimerChecker test1");

			currentTimer.registerTimerHandler(new TimerHandler() {
				@Override
				public void handleTimerEvent(Timer arg0) {
					logger.debug("TimerChecker LatchDown");
					messageQueue.add(new Pair<Long, String>(System.currentTimeMillis(), finished));
					latch.countDown();
				}

				@Override
				public void handleRunStateChanged(Timer arg0, boolean arg1) {
					messageQueue.add(new Pair<Long, String>(System.currentTimeMillis(), arg1 ? running : idle));
				}

			});

			logger.debug("TimerChecker calling start");
			currentTimer.start();

			primaryScheduledFuture = scheduledExecutorService.schedule(new Callable<Boolean>() {
				public Boolean call() throws Exception {

					logger.debug("TimerChecker calling pause");
					currentTimer.pause();
					logger.debug("TimerChecker time remaining " + currentTimer.retrieveTimeLeft().getSeconds());

					if (Math.abs(30 - currentTimer.retrieveTimeLeft().getSeconds()) > 5) {
						throw new Exception("Timer TimeLeft in not according to test");
					}
					Thread.sleep(500);
					logger.debug("TimerChecker calling start");
					currentTimer.start();
					latch.await();
					return true;
				}
			}, 30, TimeUnit.SECONDS);
			try {
				primaryScheduledFuture.get(70, TimeUnit.SECONDS);
			} catch (TimeoutException e) {
				throw new Exception("Timer didn't finish on time");
			}

			logger.debug(currentTimer.retrieveTimeLeft().toString());

			if (messageQueue.size() != 5) {
				throw new TimeoutException("Timer didn't act according to test");
			}

			// should be Running,Idle,Running,Idle,Finished.
			for (int i = 0; i < messageQueue.size(); i++) {
				if ((i == 0 || i == 2) && !messageQueue.get(i).second.equals(running)) {
					throw new TimeoutException("Timer didn't act according to test");
				}
				if ((i == 1 || i == 3) && !messageQueue.get(i).second.equals(idle)) {
					throw new TimeoutException("Timer didn't act according to test");
				}
				if (i == 4 && !messageQueue.get(i).second.equals(finished)) {
					throw new TimeoutException("Timer didn't act according to test");
				}
			}

			scheduledExecutorService.shutdown();
		}
	}







	private  Calendar convertDateTimeToCalendar(DateTime dt) {
		Calendar newCalendar = Calendar.getInstance();
		newCalendar.set(Calendar.HOUR_OF_DAY, dt.getTime().getHour());
		newCalendar.set(Calendar.MINUTE, dt.getTime().getMinute());
		newCalendar.set(Calendar.SECOND, dt.getTime().getSeconds());
		newCalendar.set(Calendar.MILLISECOND, dt.getTime().getMilliseconds());
		newCalendar.set(Calendar.MONTH, dt.getDate().getMonth() - 1);
		newCalendar.set(Calendar.YEAR, dt.getDate().getYear());
		newCalendar.set(Calendar.DAY_OF_MONTH, dt.getDate().getDay());
		return newCalendar;
	}

	private  DateTime convertCalendarToDateTime(Calendar currentCalendar) {

		Time ts = new org.allseen.timeservice.Time((byte) currentCalendar.get(Calendar.HOUR_OF_DAY), (byte) currentCalendar.get(Calendar.MINUTE), (byte) currentCalendar.get(Calendar.SECOND),
				(short) currentCalendar.get(Calendar.MILLISECOND));

		Date ds = new Date((short) currentCalendar.get(Calendar.YEAR), (byte) (currentCalendar.get(Calendar.MONTH) + 1), (byte) currentCalendar.get(Calendar.DAY_OF_MONTH));
		return new DateTime(ds, ts, (short) 0);
	}





	private  void populateBusIntrospectPathInterfaceSet(BusIntrospector busIntrospector, Set<String> busIntrospectPathInterfaceSet, String path) throws Exception {
		NodeDetail nodeDetail = null;
		try {
			nodeDetail = busIntrospector.introspect(path);
		} catch (BusException e) {
			handleIntrospectionBusException(path, e);
		} catch (Exception ex) {
			logger.error("Encountered exception while trying to parse the introspection xml", ex);
			throw new Exception("Encountered exception while trying to parse the introspection xml", ex);
		}

		IntrospectionNode introspectionNode = nodeDetail.getIntrospectionNode();

		for (IntrospectionInterface introspectionInterface : introspectionNode.getInterfaces()) {
			String ifacename = introspectionInterface.getName();
			String key = new StringBuilder(path).append(":").append(ifacename).toString();
			busIntrospectPathInterfaceSet.add(key);
			String message = new StringBuilder("Bus Introspection contains interface ").append(ifacename).append(" at path ").append(path).toString();
			logger.debug(message);
		}
	}

	private  void handleIntrospectionBusException(String path, BusException e) throws Exception {
		String msg = ERROR_MSG_BUS_INTROSPECTION;
		if (e instanceof ErrorReplyBusException && DBUS_ERROR_SERVICE_UNKNOWN.equals(((ErrorReplyBusException) e).getErrorName())) {
			msg = new StringBuilder("AboutAnnouncement has the path ").append(path).append(", but it is not found on the Bus Intropsection.").toString();
		}
		logger.error(msg, e);
		throw new Exception(msg, e);
	}

	private  void populateAnnouncementPathInterfaceSet(Set<String> announcementPathInterfaceSet, BusObjectDescription busObjectDescription, String path) {
		for (String ifacename : busObjectDescription.interfaces) {
			String key = new StringBuilder(path).append(":").append(ifacename).toString();
			announcementPathInterfaceSet.add(key);
			String message = new StringBuilder("AboutAnnouncement contains interface ").append(ifacename).append(" at path ").append(path).toString();
			logger.debug(message);
		}
	}


	protected  BusIntrospector getIntrospector() {
		return new XmlBasedBusIntrospector(serviceHelper.getBusAttachment(), timeServiceClient.getServerBusName(), timeServiceClient.getSessionId());

	}

	protected  InterfaceValidator getInterfaceValidator() throws IOException, ParserConfigurationException, SAXException {
		return new InterfaceValidator();
	}



	protected  ServiceHelper getServiceHelper() {
		return new ServiceHelper();
	}







	protected  void tearDown() throws Exception {

		logger.debug("test tearDown started");
		releaseResources();
		logger.debug("test tearDown done");
	}


	private  void releaseResources() {
		if (timeServiceClient != null) {
			timeServiceClient.release();
			timeServiceClient = null;
		}
		if (serviceHelper != null) {
			serviceHelper.release();
			serviceHelper = null;
		}
	}


	private  void fail(String msg) {


		logger.error(msg);
		pass=false;

	}


	private  void assertEquals(String errorMsg,
			String Version, short version) {


		if(Short.parseShort(Version)!=version){
			fail(errorMsg);



		}

	}


	private  void assertEquals(Status status, Status second) {

		if(!status.equals(second)){
			fail("Status is not"+status);
		}
	}




	private  void assertEquals(String errorMsg, int i, int j) {
		if(i!=j){
			fail(errorMsg);



		}

	}

	private  void assertEquals(String errorMsg,
			String string1, String string2) {


		if(!string1.equals(string2)){
			fail(errorMsg);


		}

	}


	private  void assertEquals(String errorMsg, byte byte1,
			byte byte2) {
		if(!(byte1==byte2)){
			fail(errorMsg);


		}

	}



	private  void assertEquals(String errorMsg, boolean bool,
			boolean booleanValue) {
		if(bool!=booleanValue){
			fail(errorMsg);
		}

	}





	private  void assertTrue(String errorMsg,
			boolean bool) {

		if(!bool){
			fail(errorMsg);

		}

	}


	private  void assertFalse(String errorMsg,
			boolean bool) {

		if(bool){
			fail(errorMsg);

		}

	}


	private  void assertNotNull(String msgError, Object notNull) {

		if(notNull==null){
			fail(msgError);
		}

	}


	private  void assertNull(String msgError,
			Object Null) {
		if(Null!=null){
			fail(msgError);
		}

	}

	public String getVerdict() {

		String verdict=null;
		if(pass){
			verdict="PASS";
		}else if(!pass){
			verdict="FAIL";
		}


		return verdict;
	}





}

