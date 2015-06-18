/*******************************************************************************
 *  Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
package org.alljoyn.validation.testing.suites;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.alljoyn.about.AboutKeys;
import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.Mutable;
import org.alljoyn.bus.PasswordManager;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.bus.SessionListener;
import org.alljoyn.bus.SessionOpts;
import org.alljoyn.bus.Status;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.alljoyn.DaemonInit;
import org.alljoyn.services.common.BusObjectDescription;
import org.alljoyn.services.common.utils.TransportUtil;

import android.content.Context;
import android.util.Log;

public class AllJoynManager
{
	/* Load AllJoyn Library */
	static
	{
	    System.loadLibrary("alljoyn_java");
	}

	/* Private Variables */
	private BusAttachment bus;
	private LampStateSignalHandler sigHandler;
	private String daemonName;
	private boolean advertisingDaemon;
	private int sessionID;
	private ArrayList<String> peers;
	private short clientContactPort;
	private String serviceName;
	private LampStateBusObject stateBusObject;
	private LampParametersBusObject parametersBusObject;
	private LampDetailsBusObject detailsBusObject;
	private LampServiceBusObject serviceBusObject;
	private ProxyBusObject lampStateProxyBusObject;
	private ProxyBusObject lampParametersProxyBusObject;
	private ProxyBusObject lampDetailsProxyBusObject;
	private ProxyBusObject lampServiceProxyBusObject;

	public static String BUS_OBJECT_PATH = "/org/allseen/LSF/Lamp";
	public static short LAMP_SERVICE_PORT = 42;

	public AllJoynManager(Context context)
	{
		this.peers = new ArrayList<String>();
		this.sessionID = -1;
		this.advertisingDaemon = false;

		createBusAttachmentAndPrepareDaemon(context);
	}

	public void registerSignalHandler(LampStateSignalHandler sig)
	{
		//Register Signal Handler
		sigHandler = sig;
		bus.registerSignalHandlers(sigHandler);
	}

	public void initialize()
	{
		Status status;

		//Create and register bus objects
		stateBusObject = new LampStateBusObject();
		status = bus.registerBusObject(stateBusObject, BUS_OBJECT_PATH);

		//Connect bus
		status = bus.connect();

		//Advertise Daemon
		advertiseDaemonBusNodeLSF();
	}

	public void destroy()
	{
		if (advertisingDaemon)
		{
			stopAdvertisingDaemon();
		}

		if (sessionID != -1)
		{
			bus.leaveSession(sessionID);
			bus.unregisterSignalHandlers(sigHandler);
			bus.unregisterBusObject(stateBusObject);
			bus.disconnect();
			bus.release();
			bus = null;
		}

		daemonName = null;
		serviceName = null;

		if (peers != null)
		{
			peers.clear();
			peers = null;
		}

		if (stateBusObject != null)
		{
			stateBusObject = null;
		}
	}

	/*
	 * Private function implementations
	 */
	private void createBusAttachmentAndPrepareDaemon(Context context)
	{
		bus = new BusAttachment("LampServiceTest", BusAttachment.RemoteMessage.Receive);
		// defaults to org.alljoyn.BusNode and not to BusNode.LSF
		daemonName = "org.alljoyn.BusNode_" + bus.getGlobalGUIDString();

		Status pasStatus = PasswordManager.setCredentials("ALLJOYN_PIN_KEYX", "000000");
	}

	private synchronized void doJoinSession(final String name)
	{
		SessionOpts sessionOpts = new SessionOpts();
		sessionOpts.isMultipoint = true;
		Mutable.IntegerValue mutableSessionId = new Mutable.IntegerValue();

		// disconnect from prior sessions, if any
		if (sessionID != -1)
		{
			Status status = bus.leaveSession(sessionID);
			sessionID = -1;
		}

		Status status = bus.joinSession(name, clientContactPort, mutableSessionId, sessionOpts, new SessionListener()
		{
			@Override
			public void sessionMemberAdded(int sessionId, String busId)
			{
				peers.add(busId);
			}

			@Override
			public void sessionLost(int sessionId)
			{
				if (sessionId == sessionID)
				{
					sessionID = -1;

					Date date = new Date();

					lampStateProxyBusObject.release();
				}
			}
		});

		if (status == Status.OK)
		{
			sessionID = mutableSessionId.value;
			lampStateProxyBusObject = bus.getProxyBusObject(this.serviceName, BUS_OBJECT_PATH,
					sessionID, new Class[] { LampStateBusInterface.class });
		}
		else
		{
			sessionID = -1;
		}
	}

	/*
	 * Implementation of LampStateAllJoynOperations
	 */
    public void createAllJoynSession(String peerName, short port)
    {
    	if ((peerName.equals(this.serviceName)) && (this.sessionID != -1))
    	{
    		return;
    	}

    	this.serviceName = peerName;
        this.clientContactPort = port;

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                doJoinSession(serviceName);
            }
        }).start();
    }

	/*
	 * Implementation of LampSettingsAllJoynOperations
	 */
	public void advertiseDaemonBusNode()
	{
		if (!advertisingDaemon)
		{
			int flag = BusAttachment.ALLJOYN_REQUESTNAME_FLAG_DO_NOT_QUEUE;
			// uses BusNode name
			daemonName = "org.alljoyn.BusNode_" + bus.getGlobalGUIDString();
			Status status = bus.requestName(daemonName, flag);

			if (status == Status.OK)
			{
				status = bus.advertiseName("quiet@" + daemonName, SessionOpts.TRANSPORT_ANY);
				if (status != Status.OK)
				{
					bus.releaseName(daemonName);
				}
			}

			advertisingDaemon = true;
		}
	}

	public void advertiseDaemonBusNodeLSF()
	{
		if (!advertisingDaemon)
		{
			int flag = BusAttachment.ALLJOYN_REQUESTNAME_FLAG_DO_NOT_QUEUE;
			// uses BusNode.LSF name
			daemonName = "org.alljoyn.BusNode.LSF_" + bus.getGlobalGUIDString();
			Status status = bus.requestName(daemonName, flag);

			if (status == Status.OK)
			{
				status = bus.advertiseName("quiet@" + daemonName, SessionOpts.TRANSPORT_ANY);
				if (status != Status.OK)
				{
					bus.releaseName(daemonName);
				}
			}

			advertisingDaemon = true;
		}
	}

	public void stopAdvertisingDaemon()
	{
		if (advertisingDaemon)
		{
			Status status = bus.cancelAdvertiseName(daemonName, SessionOpts.TRANSPORT_ANY);

			status = bus.releaseName(daemonName);

			advertisingDaemon = false;
		}
	}
}
