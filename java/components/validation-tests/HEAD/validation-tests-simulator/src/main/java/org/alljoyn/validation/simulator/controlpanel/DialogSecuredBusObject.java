/*******************************************************************************
 *     Copyright (c) Open Connectivity Foundation (OCF), AllJoyn Open Source
 *     Project (AJOSP) Contributors and others.
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
package org.alljoyn.validation.simulator.controlpanel;

import java.util.HashMap;
import java.util.Map;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.ErrorReplyBusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.AlertDialogSecured;
import org.alljoyn.ioe.controlpanelservice.ui.AlertDialogHintsType;
import org.alljoyn.ioe.controlpanelservice.ui.AlertDialogWidgetEnum;
import org.alljoyn.validation.simulator.DUTSimulatorErrorCodes;

public class DialogSecuredBusObject implements AlertDialogSecured, BusObject
{
    private static final int NUMBER_OF_ACTIONS = 2;
    private static final int STATE = 1;
    private static final String MESSAGE = "secured dialog message";
    private static final int BACKGROUND_COLOR = 13434879;
    private static final String LABEL = "secured dialog label";
    private static final String ACTION1_LABEL = "action1 label";
    private static final String ACTION2_LABEL = "action2 label";

    @Override
    public void Action1() throws BusException
    {
    }

    @Override
    public void Action2() throws BusException
    {
    }

    @Override
    public void Action3() throws BusException
    {
        throw new ErrorReplyBusException(DUTSimulatorErrorCodes.ErrorName.METHOD_NOT_ALLOWED.getErrorName(),
                DUTSimulatorErrorCodes.ErrorMessage.METHOD_NOT_ALLOWED.getErrorMessage());
    }

    @Override
    public void MetadataChanged() throws BusException
    {
    }

    @Override
    public String getMessage() throws BusException
    {
        return MESSAGE;
    }

    @Override
    public short getNumActions() throws BusException
    {
        return NUMBER_OF_ACTIONS;
    }

    @Override
    public Map<Short, Variant> getOptParams() throws BusException
    {
        Map<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put(AlertDialogWidgetEnum.LABEL.ID, new Variant(LABEL));
        parameters.put(AlertDialogWidgetEnum.BG_COLOR.ID, new Variant(BACKGROUND_COLOR, "u"));
        short[] shortArray = new short[]
        { AlertDialogHintsType.ALERT_DIALOG.ID };
        parameters.put(AlertDialogWidgetEnum.HINTS.ID, new Variant(shortArray, "aq"));
        parameters.put(AlertDialogWidgetEnum.LABEL_ACTION1.ID, new Variant(ACTION1_LABEL));
        parameters.put(AlertDialogWidgetEnum.LABEL_ACTION2.ID, new Variant(ACTION2_LABEL));

        return parameters;
    }

    @Override
    public int getStates() throws BusException
    {
        return STATE;
    }

    @Override
    public short getVersion() throws BusException
    {
        return VERSION;
    }
}