/*******************************************************************************
 *     Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *     PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package org.alljoyn.validation.simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.alljoyn.about.transport.AboutTransport;
import org.alljoyn.about.transport.IconTransport;
import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusListener;
import org.alljoyn.bus.Mutable;
import org.alljoyn.bus.SessionOpts;
import org.alljoyn.bus.SessionPortListener;
import org.alljoyn.bus.SignalEmitter;
import org.alljoyn.bus.Status;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusSignal;
import org.alljoyn.config.transport.ConfigTransport;
import org.alljoyn.ns.NotificationReceiver;
import org.alljoyn.ns.transport.TransportNotificationText;
import org.alljoyn.services.android.security.AuthPasswordHandler;
import org.alljoyn.services.android.security.SrpAnonymousKeyListener;
import org.alljoyn.services.android.utils.AndroidLogger;
import org.alljoyn.services.common.BusObjectDescription;
import org.alljoyn.services.common.utils.GenericLogger;
import org.alljoyn.services.common.utils.TransportUtil;
import org.alljoyn.validation.simulator.about.DUTSimulatorAboutTransport;
import org.alljoyn.validation.simulator.config.ConfigPropertyStoreImpl;
import org.alljoyn.validation.simulator.config.DUTSimulatorAuthPasswordHandler;
import org.alljoyn.validation.simulator.config.DUTSimulatorConfigTransport;
import org.alljoyn.validation.simulator.controlpanel.ControlPanelInterfaceManager;
import org.alljoyn.validation.simulator.gwagent.GWAgentInterfaceManager;
import org.alljoyn.validation.simulator.notification.NotificationSignalHandler;
import org.alljoyn.validation.simulator.notification.NotificationTransport;

import android.util.Log;

public class DUTSimulator
{
    private static final short CONTROL_PANEL_SESSION_PORT = 1000;
    public static final short CONTACT_PORT = 20;
    private static String TAG = "DUTSimulator";
    private static final String[] AUTH_MECHANISMS = new String[]
    { "ALLJOYN_SRP_KEYX", "ALLJOYN_PIN_KEYX", "ALLJOYN_ECDHE_PSK" };

    private BusAttachment busAttachment;
    private DeviceDetails deviceDetails;
    private NotificationObject notificationObjEmergency;
    private NotificationObject notificationObjInfo;
    private NotificationObject notificationObjWarning;
    private SignalEmitter emitterEmer;
    private SignalEmitter emitterInfo;
    private SignalEmitter emitterWarning;
    private NotificationTransport emergencyNotification;
    private NotificationTransport infoNotification;
    private NotificationTransport warningNotification;
    private AboutTransport announcementEmmitter;
    private NotificationReceiver notificationReceiver;

    private AuthPasswordHandler authPasswordHandler;
    private char[] securedSessionPassword;
    private String keyStorePath;
    private Map<String, Variant> variantAboutMap;
    private List<BusObjectDescription> supportedInterfaces = new ArrayList<BusObjectDescription>();
    private GenericLogger logger = new AndroidLogger();
    private boolean supportsConfig = false;
    private boolean controlPanelSupported = false;
    private boolean supportsNotificationProducer = false;
    private boolean gwAgentSupported = false;

    private boolean listenForNotifications = false;
    private AboutTransport aboutInterface = new DUTSimulatorAboutTransport(this);
    private ConfigTransport configInterface = new DUTSimulatorConfigTransport(this);
    private ControlPanelInterfaceManager controlPanelInterfaceManager;

    public SrpAnonymousKeyListener authListener;
    private NotificationSignalHandler notificationSignalHandler;
    private GWAgentInterfaceManager gwAgentInterfaceManager;

    static
    {
        System.loadLibrary("alljoyn_java");
    }

    public DUTSimulator(DeviceDetails deviceDetails)
    {
        this.deviceDetails = deviceDetails;
    }

    public String getDeviceId()
    {
        return deviceDetails.getDeviceId();
    }

    public UUID getAppId()
    {
        return deviceDetails.getAppId();
    }

    public boolean isRunning()
    {
        return (busAttachment != null);
    }

    public void setBusAttachment(BusAttachment busAttachment)
    {
        this.busAttachment = busAttachment;
    }

    public AuthPasswordHandler getAuthPasswordHandler()
    {
        return authPasswordHandler;
    }

    public void setAuthPasswordHandler(AuthPasswordHandler authPasswordHandler)
    {
        this.authPasswordHandler = authPasswordHandler;
    }

    class NotificationObject implements NotificationTransport
    {
        @Override
        public void notify(int version, int msgId, short messageType, String deviceId, String deviceName, byte[] appId, String appName, Map<Integer, Variant> attributes,
                Map<String, String> customAttributes, TransportNotificationText[] text)
        {
        }
    }

    public void start()
    {
        addBusObjectDescription(AboutTransport.OBJ_PATH, new String[]
        { AboutTransport.INTERFACE_NAME });

        busAttachment = new BusAttachment("DUTSimulator", BusAttachment.RemoteMessage.Receive);

        busAttachment.registerBusListener(busListener);

        Status status = busAttachment.registerBusObject(aboutInterfaceMapper, AboutTransport.OBJ_PATH);
        checkStatus("registerBusObject About", status, true);

        status = busAttachment.registerBusObject(aboutIconInterfaceMapper, IconTransport.OBJ_PATH);
        checkStatus("registerBusObject AboutIcon", status, true);

        if (supportsNotificationProducer)
        {
            addBusObjectDescription("/info", new String[]
            { "org.alljoyn.Notification" });
            addBusObjectDescription("/emergency", new String[]
            { "org.alljoyn.Notification" });
            addBusObjectDescription("/warning", new String[]
            { "org.alljoyn.Notification" });

            notificationObjEmergency = new NotificationObject();
            notificationObjInfo = new NotificationObject();
            notificationObjWarning = new NotificationObject();

            status = busAttachment.registerBusObject(notificationObjEmergency, "/emergency");
            checkStatus("registerBusObject /emergency", status, true);
            status = busAttachment.registerBusObject(notificationObjInfo, "/info");
            checkStatus("registerBusObject /info", status, true);
            status = busAttachment.registerBusObject(notificationObjWarning, "/warning");
            checkStatus("registerBusObject /warning", status, true);

            emitterEmer = new SignalEmitter(notificationObjEmergency, SignalEmitter.GlobalBroadcast.Off);
            emitterEmer.setSessionlessFlag(true);
            emitterEmer.setTimeToLive(43200);
            emergencyNotification = emitterEmer.getInterface(NotificationTransport.class);

            emitterInfo = new SignalEmitter(notificationObjInfo, SignalEmitter.GlobalBroadcast.Off);
            emitterInfo.setSessionlessFlag(true);
            emitterInfo.setTimeToLive(43200);
            infoNotification = emitterInfo.getInterface(NotificationTransport.class);

            emitterWarning = new SignalEmitter(notificationObjWarning, SignalEmitter.GlobalBroadcast.Off);
            emitterWarning.setSessionlessFlag(true);
            emitterWarning.setTimeToLive(43200);
            warningNotification = emitterWarning.getInterface(NotificationTransport.class);
        }

        addControlPanelInterfaces();

        addGWAgentInterfaces();

        SignalEmitter signalEmitter = new SignalEmitter(aboutInterfaceMapper, SignalEmitter.GlobalBroadcast.Off);
        signalEmitter.setSessionlessFlag(true);
        signalEmitter.setTimeToLive(0);
        announcementEmmitter = signalEmitter.getInterface(AboutTransport.class);

        variantAboutMap = TransportUtil.toVariantMap(deviceDetails.getAnnounceMap());

        if (supportsConfig)
        {
            new ConfigPropertyStoreImpl();

            addBusObjectDescription(ConfigTransport.OBJ_PATH, new String[]
            { ConfigTransport.INTERFACE_NAME });

            status = busAttachment.registerBusObject(configInterface, ConfigTransport.OBJ_PATH);
            checkStatus("registerBusObject Config", status, true);
        }

        status = busAttachment.connect();
        checkStatus("BusAttachment.connect", status, true);

        if (listenForNotifications)
        {
            notificationSignalHandler = new NotificationSignalHandler(notificationReceiver, busAttachment);
            notificationSignalHandler.initialize();
        }

        if (supportsConfig || controlPanelSupported || gwAgentSupported)
        {
            authPasswordHandler = new DUTSimulatorAuthPasswordHandler(this);
            authListener = new SrpAnonymousKeyListener(authPasswordHandler, logger, AUTH_MECHANISMS);
            Status authStatus = busAttachment.registerAuthListener(authListener.getAuthMechanismsAsString(), authListener, keyStorePath);
            logger.debug(TAG, "BusAttachment.registerAuthListener status = " + authStatus);
            if (authStatus != Status.OK)
            {
                Log.d(TAG, "Failed to register Auth listener status = " + authStatus.toString());
            }
        }

        bindSessionPort(new Mutable.ShortValue(CONTACT_PORT));
        bindSessionPort(new Mutable.ShortValue(CONTROL_PANEL_SESSION_PORT));
        status = busAttachment.advertiseName(busAttachment.getUniqueName(), SessionOpts.TRANSPORT_ANY);
        checkStatus("BusAttachment.advertiseName", status, true);

        try
        {
            announcementEmmitter.Announce((short) aboutInterface.getVersion(), CONTACT_PORT, aboutInterface.GetObjectDescription(), variantAboutMap);
        }
        catch (BusException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void addGWAgentInterfaces()
    {
        if (gwAgentSupported)
        {
            gwAgentInterfaceManager = new GWAgentInterfaceManager(busAttachment);
            supportedInterfaces.add(gwAgentInterfaceManager.getBusObjectDescriptionToBeAnnounced());
            gwAgentInterfaceManager.registerBusObjects();
        }
    }

    public void sendNotification(int version, int msgId, short messageType, String deviceId, String deviceName, byte[] appId, String appName, Map<Integer, Variant> attributes,
            Map<String, String> customAttributes, TransportNotificationText[] text)
    {
        NotificationTransport notificationInterface = null;
        switch (messageType)
        {
        case 0:
            notificationInterface = emergencyNotification;
            break;
        case 1:
            notificationInterface = warningNotification;
            break;
        case 2:
            notificationInterface = infoNotification;
            break;
        }
        notificationInterface.notify(version, msgId, messageType, deviceId, deviceName, appId, appName, attributes, customAttributes, text);
    }

    private void bindSessionPort(final Mutable.ShortValue port)
    {
        Status status = busAttachment.bindSessionPort(port, new SessionOpts(), new SessionPortListener()
        {
            @Override
            public boolean acceptSessionJoiner(short sessionPort, String joiner, SessionOpts sessionOpts)
            {
                if (sessionPort == port.value)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }

            @Override
            public void sessionJoined(short sessionPort, int sessionId, String joiner)
            {
                Log.d(TAG, String.format("sessionJoined: %d %d %s", sessionPort, sessionId, joiner));
            }

        });

        checkStatus("BusAttachment.bindSessionPort", status, true);
    }

    private void addControlPanelInterfaces()
    {
        if (controlPanelSupported)
        {
            controlPanelInterfaceManager = new ControlPanelInterfaceManager(busAttachment);
            supportedInterfaces.add(controlPanelInterfaceManager.getBusObjectDescriptionToBeAnnounced());
            controlPanelInterfaceManager.registerBusObjects();
        }
    }

    private void checkStatus(String msg, Status status, boolean throwException)
    {
        if (!status.equals(Status.OK))
        {
            Log.e(TAG, msg + " returned status: " + status);
            if (throwException)
            {
                throw new RuntimeException(status.toString());
            }
        }
    }

    private AboutTransport aboutInterfaceMapper = new AboutTransport()
    {

        @Override
        public Map<String, Variant> GetAboutData(String languageTag) throws BusException
        {
            return aboutInterface.GetAboutData(languageTag);
        }

        @Override
        public BusObjectDescription[] GetObjectDescription() throws BusException
        {
            return aboutInterface.GetObjectDescription();
        }

        @Override
        public short getVersion() throws BusException
        {
            return aboutInterface.getVersion();
        }

        @Override
        @BusSignal(signature = "qqa(oas)a{sv}")
        public void Announce(short version, short port, org.alljoyn.services.common.BusObjectDescription[] objectDescriptions, Map<String, Variant> serviceMetadata)
        {
        }
    };

    public Map<String, Variant> getAbout(String languageTag)
    {
        Map<String, Variant> aboutVariantMap = null;
        Map<String, Object> aboutMap = deviceDetails.getAbout(languageTag);
        if (aboutMap != null)
        {
            aboutVariantMap = TransportUtil.toVariantMap(aboutMap);
        }

        return aboutVariantMap;
    }

    private IconTransport aboutIconInterfaceMapper = new IconTransport()
    {

        @Override
        public short getVersion() throws BusException
        {
            return aboutIconInterface.getVersion();
        }

        @Override
        public String getMimeType() throws BusException
        {
            return aboutIconInterface.getMimeType();
        }

        @Override
        public int getSize() throws BusException
        {
            return aboutIconInterface.getSize();
        }

        @Override
        public String GetUrl() throws BusException
        {
            return aboutIconInterface.GetUrl();
        }

        @Override
        public byte[] GetContent() throws BusException
        {
            return aboutIconInterface.GetContent();
        }

    };

    private IconTransport aboutIconInterface = new IconTransport()
    {

        @Override
        public short getVersion() throws BusException
        {
            return 1;
        }

        @Override
        public String getMimeType() throws BusException
        {
            return null;
        }

        @Override
        public int getSize() throws BusException
        {
            return 0;
        }

        @Override
        public String GetUrl() throws BusException
        {
            return null;
        }

        @Override
        public byte[] GetContent() throws BusException
        {
            return new byte[0];
        }

    };

    private BusListener busListener = new BusListener()
    {
        @Override
        public void nameOwnerChanged(String arg0, String arg1, String arg2)
        {
            Log.d(TAG, String.format("nameOwnerChanged: %s, %s, %s", arg0, arg1, arg2));
        }

        @Override
        public void foundAdvertisedName(String arg0, short arg1, String arg2)
        {
            Log.d(TAG, String.format("foundAdvertisedName: %s, %d, %s", arg0, arg1, arg2));
        }

        @Override
        public void lostAdvertisedName(String arg0, short arg1, String arg2)
        {
            Log.d(TAG, String.format("lostAdvertisedName: %s, %d, %s", arg0, arg1, arg2));
        }

        @Override
        public void busDisconnected()
        {
            Log.d(TAG, "busDisconnected");
        }
    };

    public void stop()
    {
        try
        {
            supportedInterfaces = new ArrayList<BusObjectDescription>();

            if (busAttachment != null)
            {
                Log.d(TAG, "Disconnecting busAttachment!");
                unregisterControlPanelBusObjects();
                unregisterGWAgentBusObjects();
                busAttachment.unregisterBusObject(notificationObjEmergency);
                busAttachment.unregisterBusObject(notificationObjInfo);
                busAttachment.unregisterBusObject(notificationObjWarning);
                busAttachment.unregisterBusObject(aboutInterfaceMapper);
                busAttachment.unregisterBusObject(aboutIconInterfaceMapper);
                busAttachment.unregisterBusObject(configInterface);
                busAttachment.unregisterBusListener(busListener);

                if (notificationSignalHandler != null)
                {
                    notificationSignalHandler.release();
                    notificationSignalHandler = null;
                }

                busAttachment.disconnect();
                busAttachment.release();
                busAttachment = null;
                Log.d(TAG, "Disconnected busAttachment!");
            }
        }
        catch (Exception e)
        {
            Log.d(TAG, "Disconnecting busAttachment threw exception ", e);
            throw new RuntimeException(e);
        }
    }

    public AboutTransport getAboutInterface()
    {
        return aboutInterface;
    }

    public void setAboutInterface(AboutTransport aboutInterface)
    {
        this.aboutInterface = aboutInterface;
    }

    public void setAboutIconInterface(IconTransport aboutIconInterface)
    {
        this.aboutIconInterface = aboutIconInterface;

        addBusObjectDescription(IconTransport.OBJ_PATH, new String[]
        { IconTransport.INTERFACE_NAME });
    }

    public BusListener getBusListener()
    {
        return busListener;
    }

    public void setBusListener(BusListener busListener)
    {
        this.busListener = busListener;
    }

    public boolean isSupportsNotificationProducer()
    {
        return supportsNotificationProducer;
    }

    public void setSupportsNotificationProducer(boolean supportsNotificationProducer)
    {
        this.supportsNotificationProducer = supportsNotificationProducer;
    }

    public boolean isSupportsConfig()
    {
        return supportsConfig;
    }

    public void setSupportsConfig(boolean supportsConfig)
    {
        this.supportsConfig = supportsConfig;
    }

    public boolean isControlPanelSupported()
    {
        return controlPanelSupported;
    }

    public void setControlPanelSupported(boolean controlPanelSupported)
    {
        this.controlPanelSupported = controlPanelSupported;
    }

    public boolean isGWAgentSupported()
    {
        return gwAgentSupported;
    }

    public void setGWAgentSupported(boolean gwAgentSupported)
    {
        this.gwAgentSupported = gwAgentSupported;
    }

    public String getKeyStorePath()
    {
        return keyStorePath;
    }

    public void setKeyStorePath(String keyStorePath)
    {
        this.keyStorePath = keyStorePath;
    }

    public char[] getSecuredSessionPassword()
    {
        return securedSessionPassword;
    }

    public void setSecuredSessionPassword(char[] securedSessionPassword)
    {
        this.securedSessionPassword = securedSessionPassword;
    }

    public Map<String, Variant> getVariantAboutMap()
    {
        return variantAboutMap;
    }

    public void setVariantAboutMap(Map<String, Variant> variantAboutMap)
    {
        this.variantAboutMap = variantAboutMap;
    }

    public AboutTransport getAnnouncementEmmitter()
    {
        return announcementEmmitter;
    }

    public void setAnnouncementEmmitter(AboutTransport announcementEmmitter)
    {
        this.announcementEmmitter = announcementEmmitter;
    }

    public DeviceDetails getDeviceDetails()
    {
        return deviceDetails;
    }

    public List<BusObjectDescription> getSupportedInterfaces()
    {
        return supportedInterfaces;
    }

    public void setSupportedInterfaces(List<BusObjectDescription> supportedInterfaces)
    {
        this.supportedInterfaces = supportedInterfaces;
    }

    public void setDeviceDetails(DeviceDetails deviceDetails)
    {
        this.deviceDetails = deviceDetails;
    }

    private void addBusObjectDescription(String path, String[] interfaces)
    {
        BusObjectDescription busObjectDescription = new BusObjectDescription();
        busObjectDescription.setInterfaces(interfaces);
        busObjectDescription.setPath(path);
        supportedInterfaces.add(busObjectDescription);
    }

    private void unregisterControlPanelBusObjects()
    {
        if (controlPanelSupported)
        {
            controlPanelInterfaceManager.unregisterBusObjects();
        }
    }

    private void unregisterGWAgentBusObjects()
    {
        if (gwAgentSupported)
        {
            gwAgentInterfaceManager.unregisterBusObjects();
        }
    }

    public BusAttachment getBusAttachment()
    {
        return busAttachment;
    }

    public boolean isListenForNotifications()
    {
        return listenForNotifications;
    }

    public void setListenForNotifications(boolean listenForNotifications)
    {
        this.listenForNotifications = listenForNotifications;
    }

    public void setNotificationReceiver(NotificationReceiver notificationReceiver)
    {
        this.notificationReceiver = notificationReceiver;
    }
}