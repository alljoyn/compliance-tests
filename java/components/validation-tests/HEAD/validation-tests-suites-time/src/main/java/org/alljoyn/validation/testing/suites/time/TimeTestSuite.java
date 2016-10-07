/*******************************************************************************
 *   *  Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
 *     Source Project (AJOSP) Contributors and others.
 *
 *     SPDX-License-Identifier: Apache-2.0
 *
 *     All rights reserved. This program and the accompanying materials are
 *     made available under the terms of the Apache License, Version 2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
 *     Alliance. All rights reserved.
 *
 *     Permission to use, copy, modify, and/or distribute this software for
 *     any purpose with or without fee is hereby granted, provided that the
 *     above copyright notice and this permission notice appear in all
 *     copies.
 *
 *      THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *      WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *      WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *      AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *      DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *      PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *      TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *      PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package org.alljoyn.validation.testing.suites.time;

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

import org.alljoyn.services.android.utils.AndroidLogger;

import org.alljoyn.services.common.BusObjectDescription;

import org.alljoyn.validation.framework.AppUnderTestDetails;
import org.alljoyn.validation.framework.ValidationBaseTestCase;
import org.alljoyn.validation.framework.annotation.ValidationSuite;
import org.alljoyn.validation.framework.annotation.ValidationTest;
import org.alljoyn.validation.framework.utils.introspection.XmlBasedBusIntrospector;
import org.alljoyn.validation.framework.utils.introspection.BusIntrospector;
import org.alljoyn.validation.framework.utils.introspection.bean.InterfaceDetail;
import org.alljoyn.validation.framework.utils.introspection.bean.IntrospectionInterface;
import org.alljoyn.validation.framework.utils.introspection.bean.IntrospectionNode;
import org.alljoyn.validation.framework.utils.introspection.bean.NodeDetail;

import org.alljoyn.validation.testing.utils.InterfaceValidator;
import org.alljoyn.validation.testing.utils.ValidationResult;
import org.alljoyn.validation.testing.utils.about.AboutAnnouncementDetails;
import org.alljoyn.validation.testing.utils.log.Logger;
import org.alljoyn.validation.testing.utils.log.LoggerFactory;
import org.alljoyn.validation.testing.utils.services.ServiceHelper;
import org.allseen.timeservice.Date;
import org.allseen.timeservice.DateTime;
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

import android.content.Context;
import android.util.Pair;

@ValidationSuite(name = "Time-v1")
public class TimeTestSuite extends ValidationBaseTestCase {
    private static final String INTROSPECTABLE_INTERFACE_NAME = "org.allseen.Introspectable";
    protected static final String TAG = "TimeServiceTestSuite";
    private static final Logger logger = LoggerFactory.getLogger(TAG);
    private static final String BUS_APPLICATION_NAME = "TimeServiceTestSuite";
    private static final String ERROR_MSG_BUS_INTROSPECTION = "Encountered exception while trying to introspect the bus";
    private static final String DBUS_ERROR_SERVICE_UNKNOWN = "org.freedesktop.DBus.Error.ServiceUnknown";

    protected AboutAnnouncementDetails deviceAboutAnnouncement;
    private ServiceHelper serviceHelper;
    private SimpleDateFormat simpleDateFormat;
    private static final long ANNOUCEMENT_TIMEOUT_IN_SECONDS = 30;
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private TimeServiceClient timeServiceClient;

    private AppUnderTestDetails appUnderTestDetails;
    private UUID dutAppId;
    private String dutDeviceId;

    private Context context;

    private static SimpleDateFormat dateAndTimeDisplayFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");

    private BlockingQueue<Pair<TimeServiceClient, Status>> joinSessionQueue = new ArrayBlockingQueue<Pair<TimeServiceClient, Status>>(1);

    public TimeTestSuite(Context context) {
        this.context = context;
        simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        simpleDateFormat.setLenient(false);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        logger.debug("test setUp started");

        try {
            appUnderTestDetails = getValidationTestContext().getAppUnderTestDetails();
            dutDeviceId = appUnderTestDetails.getDeviceId();
            logger.debug("Running Time Service test case against Device ID: %s", dutDeviceId);
            dutAppId = appUnderTestDetails.getAppId();
            logger.debug("Running Time Service test case against App ID: %s", dutAppId);

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
                    joinSessionQueue.add(new Pair<TimeServiceClient, Status>(arg0, arg1));
                }
            });

            // not if timeout will throw Interrupt Exception.
            Pair<TimeServiceClient, Status> joinRes = joinSessionQueue.poll(ANNOUCEMENT_TIMEOUT_IN_SECONDS * 2, TimeUnit.SECONDS);

            assertEquals(Status.OK, joinRes.second);

        } catch (Exception exception) {
            try {
                releaseResources();
            } catch (Exception newException) {
                logger.debug("Exception releasing resources", newException);
            }

            throw exception;
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        logger.debug("test tearDown started");
        releaseResources();
        logger.debug("test tearDown done");
    }

    @ValidationTest(name = "Time-v1-01")
    public void testTime_v1_01_GetObjectDescription() throws Exception {

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
            String path = busObjectDescription.getPath();
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

    @ValidationTest(name = "Time-v1-02")
    public void testTime_v1_02_VerifyClocks() throws Exception {

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
                new ClockChecker(clock).vlaidate();
            }
        }

    }

    public static class ClockChecker {
        org.allseen.timeservice.client.Clock currentClock;

        ClockChecker(org.allseen.timeservice.client.Clock clock) {
            this.currentClock = clock;
        }

        public void vlaidate() throws Exception {
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
                assertEquals(true, currentClock.retrieveIsSet());
            }
            Calendar receivdTime = convertDateTimeToCalendar(currentClock.retrieveDateTime());
            logger.debug("Set Time to " + dateAndTimeDisplayFormat.format(calander.getTime()) + " received " + dateAndTimeDisplayFormat.format(receivdTime.getTime()));
            assertEquals(false, Math.abs(receivdTime.getTimeInMillis() - calander.getTimeInMillis()) > 1000 * 60);
        }

    }

    @ValidationTest(name = "Time-v1-03")
    public void testTime_v1_03_VerifyTimers() throws Exception {
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
                assertEquals(initalSize + 1, timerFactory.retrieveTimerList().size());
                timerFactory.deleteTimer(tempTimer.getObjectPath());
                assertEquals(initalSize, timerFactory.retrieveTimerList().size());
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
            timerChecker.vlaidate();
            timeServiceClient.release();
        }

    }

    public static class TimerChecker {

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

        public void vlaidate() throws Exception {

            prepareForTest();
            test1();
        }

        private void prepareForTest() throws Exception {
            logger.debug("TimerChecker prepareForTest");
            if (currentTimer.retrieveIsRunning()) {
                currentTimer.pause();
                assertEquals(false, currentTimer.retrieveIsRunning());
            }
            currentTimer.setTitle(TimerCheckerTitle);
            currentTimer.setInterval(period);
            currentTimer.setRepeat(repeat);
            currentTimer.reset();

            assertEquals(TimerCheckerTitle, currentTimer.retrieveTitle());
            assertEquals(repeat, currentTimer.retrieveRepeat());
            assertEquals(period.toString(), currentTimer.retrieveInterval().toString());
            assertEquals(period.toString(), currentTimer.retrieveTimeLeft().toString());
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

    @ValidationTest(name = "Time-v1-04")
    public void testTime_v1_04_VerifyAlarms() throws Exception {
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
                assertEquals(initalSize + 1, alarmFactory.retrieveAlarmList().size());
                alarmFactory.deleteAlarm(tempAlarm.getObjectPath());
                assertEquals(initalSize, alarmFactory.retrieveAlarmList().size());
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

    public static class AlarmChecker {
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
                assertEquals(false, currentAlarm.retrieveIsEnabled());
            }
            currentAlarm.setTitle(alarmCheckerTitle);

            Schedule schedule = new Schedule(new Time((byte) 12, (byte) 1, (byte) 0, (short) 0), new HashSet<WeekDay>());
            currentAlarm.setSchedule(schedule);
            assertEquals(alarmCheckerTitle, currentAlarm.retrieveTitle());
            assertEquals(schedule.toString(), currentAlarm.retrieveSchedule().toString());
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

    protected BusIntrospector getIntrospector() {
        return new XmlBasedBusIntrospector(serviceHelper.getBusAttachment(), timeServiceClient.getServerBusName(), timeServiceClient.getSessionId());

    }

    protected InterfaceValidator getInterfaceValidator() throws IOException, ParserConfigurationException, SAXException {
        return new InterfaceValidator(getValidationTestContext());
    }

    private void releaseResources() {
        if (timeServiceClient != null) {
            timeServiceClient.release();
            timeServiceClient = null;
        }
        if (serviceHelper != null) {
            serviceHelper.release();
            serviceHelper = null;
        }
    }

    private void populateBusIntrospectPathInterfaceSet(BusIntrospector busIntrospector, Set<String> busIntrospectPathInterfaceSet, String path) throws Exception {
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

    private void handleIntrospectionBusException(String path, BusException e) throws Exception {
        String msg = ERROR_MSG_BUS_INTROSPECTION;
        if (e instanceof ErrorReplyBusException && DBUS_ERROR_SERVICE_UNKNOWN.equals(((ErrorReplyBusException) e).getErrorName())) {
            msg = new StringBuilder("AboutAnnouncement has the path ").append(path).append(", but it is not found on the Bus Intropsection.").toString();
        }
        logger.error(msg, e);
        throw new Exception(msg, e);
    }

    private void populateAnnouncementPathInterfaceSet(Set<String> announcementPathInterfaceSet, BusObjectDescription busObjDescription, String path) {
        for (String ifacename : busObjDescription.getInterfaces()) {
            String key = new StringBuilder(path).append(":").append(ifacename).toString();
            announcementPathInterfaceSet.add(key);
            String message = new StringBuilder("AboutAnnouncement contains interface ").append(ifacename).append(" at path ").append(path).toString();
            logger.debug(message);
        }
    }

    protected ServiceHelper getServiceHelper() {
        return new ServiceHelper(new AndroidLogger());
    }

    private static Calendar convertDateTimeToCalendar(DateTime dt) {
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

    private static DateTime convertCalendarToDateTime(Calendar currentCalendar) {

        Time ts = new org.allseen.timeservice.Time((byte) currentCalendar.get(Calendar.HOUR_OF_DAY), (byte) currentCalendar.get(Calendar.MINUTE), (byte) currentCalendar.get(Calendar.SECOND),
                (short) currentCalendar.get(Calendar.MILLISECOND));

        Date ds = new Date((short) currentCalendar.get(Calendar.YEAR), (byte) (currentCalendar.get(Calendar.MONTH) + 1), (byte) currentCalendar.get(Calendar.DAY_OF_MONTH));
        return new DateTime(ds, ts, (short) 0);
    }

}