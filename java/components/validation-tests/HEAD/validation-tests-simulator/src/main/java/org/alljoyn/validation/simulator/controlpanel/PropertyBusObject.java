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
import org.alljoyn.bus.Variant;
import org.alljoyn.ioe.controlpanelservice.communication.interfaces.PropertyControl;
import org.alljoyn.ioe.controlpanelservice.ui.PropertyWidgetEnum;
import org.alljoyn.ioe.controlpanelservice.ui.PropertyWidgetHintsType;
import org.alljoyn.ioe.controlpanelservice.ui.ajstruct.PropertyWidgetConstrainToValuesAJ;

public class PropertyBusObject implements PropertyControl, BusObject
{
    private static final int STATE = 1;
    private static final String PROPERTY_VALUE = "good";
    private static final String UNIT_OF_MEASURE = "property unit of measure";
    private static final int BACKGROUND_COLOR = 13434879;
    private static final String LABEL = "property label";

    @Override
    public void MetadataChanged() throws BusException
    {
    }

    @Override
    public void ValueChanged(Variant arg0) throws BusException
    {
    }

    @Override
    public Map<Short, Variant> getOptParams() throws BusException
    {
        Map<Short, Variant> parameters = new HashMap<Short, Variant>();
        parameters.put(PropertyWidgetEnum.LABEL.ID, new Variant(LABEL));
        parameters.put(PropertyWidgetEnum.BG_COLOR.ID, new Variant(BACKGROUND_COLOR, "u"));
        short[] shortArray = new short[]
        { PropertyWidgetHintsType.CHECKBOX.ID };
        parameters.put(PropertyWidgetEnum.HINTS.ID, new Variant(shortArray, "aq"));
        parameters.put(PropertyWidgetEnum.UNIT_OF_MEASURE.ID, new Variant(UNIT_OF_MEASURE));
        parameters.put(PropertyWidgetEnum.CONSTRAINT_TO_VALUES.ID, new Variant(getPropertyWidgetConstraintToValues()));

        return parameters;
    }

    @Override
    public int getStates() throws BusException
    {
        return STATE;
    }

    @Override
    public Variant getValue() throws BusException
    {
        return new Variant(PROPERTY_VALUE);
    }

    @Override
    public short getVersion() throws BusException
    {
        return VERSION;
    }

    @Override
    public void setValue(Variant arg0) throws BusException
    {
    }

    private PropertyWidgetConstrainToValuesAJ[] getPropertyWidgetConstraintToValues()
    {
        PropertyWidgetConstrainToValuesAJ[] propertyWidgetConstrainToValuesAJArray = new PropertyWidgetConstrainToValuesAJ[2];
        propertyWidgetConstrainToValuesAJArray[0] = getPropertyWidgetConstraintToValue("property first widget", "good");
        propertyWidgetConstrainToValuesAJArray[1] = getPropertyWidgetConstraintToValue("property second widget", "bad");

        return propertyWidgetConstrainToValuesAJArray;
    }

    private PropertyWidgetConstrainToValuesAJ getPropertyWidgetConstraintToValue(String label, String value)
    {
        PropertyWidgetConstrainToValuesAJ firstPropertyWidgetConstrainToValuesAJ = new PropertyWidgetConstrainToValuesAJ();
        firstPropertyWidgetConstrainToValuesAJ.label = label;
        firstPropertyWidgetConstrainToValuesAJ.value = new Variant(value);
        return firstPropertyWidgetConstrainToValuesAJ;
    }
}