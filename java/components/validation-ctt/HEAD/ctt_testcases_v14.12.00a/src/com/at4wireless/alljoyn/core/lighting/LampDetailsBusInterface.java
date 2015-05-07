/*******************************************************************************
 *  Copyright (c) 2013 - 2014, AllSeen Alliance. All rights reserved.
 *
 *     Permission to use, copy, modify, and/or distribute this software for any
 *     purpose with or without fee is hereby granted, provided that the above
 *     copyright notice and this permission notice appear in all copies.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *     WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *     MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *     ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *     WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *     ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *     OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package com.at4wireless.alljoyn.core.lighting;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusProperty;

@BusInterface(name="org.allseen.LSF.LampDetails")
public interface LampDetailsBusInterface
{
	@BusProperty(signature="u")
	public int getVersion() throws BusException;

	@BusProperty(signature="u")
	public int getMake() throws BusException;

	@BusProperty(signature="u")
	public int getModel() throws BusException;

	@BusProperty(signature="u")
	public int getType() throws BusException;

	@BusProperty(signature="u")
	public int getLampType() throws BusException;

	@BusProperty(signature="u")
	public int getLampBaseType() throws BusException;

	@BusProperty(signature="u")
	public int getLampBeamAngle() throws BusException;

	@BusProperty(signature="b")
	public boolean getDimmable() throws BusException;

	@BusProperty(signature="b")
	public boolean getColor() throws BusException;

	@BusProperty(signature="b")
	public boolean getVariableColorTemp() throws BusException;

	@BusProperty(signature="b")
	public boolean getHasEffects() throws BusException;

	@BusProperty(signature="u")
	public int getMinVoltage() throws BusException;

	@BusProperty(signature="u")
	public int getMaxVoltage() throws BusException;

	@BusProperty(signature="u")
	public int getWattage() throws BusException;

	@BusProperty(signature="u")
	public int getIncandescentEquivalent() throws BusException;

	@BusProperty(signature="u")
	public int getMaxLumens() throws BusException;

	@BusProperty(signature="u")
	public int getMinTemperature() throws BusException;

	@BusProperty(signature="u")
	public int getMaxTemperature() throws BusException;

	@BusProperty(signature="u")
	public int getColorRenderingIndex() throws BusException;

	@BusProperty(signature="s")
	public String getLampID() throws BusException;
}
