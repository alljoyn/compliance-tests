/*******************************************************************************
 *  Copyright AllSeen Alliance. All rights reserved.
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
package org.alljoyn.validation.testing.suites;

import java.util.Map;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusSignal;

public class LampStateBusObject implements BusObject, LampStateBusInterface
{
	@Override
	@BusProperty(signature = "u")
	public int getVersion() throws BusException
	{
		return 0;
	}

	@Override
	@BusMethod(signature = "ta{sv}u", replySignature = "u")
	public int TransitionLampState(long Timestamp, Map<String, Variant> NewState, int TransitionPeriod) throws BusException
	{
		return 0;
	}

	@Override
	@BusMethod(signature="a{sv}a{sv}uuut", replySignature="u")
	public int ApplyPulseEffect(Map<String, Variant> FromState, Map<String, Variant> ToState, int period, int duration, int numPulses,
			long startTimeStamp) throws BusException
	{
		return 0;
	}

	@Override
	@BusSignal(signature = "s")
	public void LampStateChanged(String LampID) throws BusException
	{
		// Intentionally left blank
	}

	@Override
	@BusProperty
	public void setOnOff(boolean onOff) throws BusException
	{
		// Intentionally left blank
	}

	@Override
	@BusProperty
	public boolean getOnOff() throws BusException
	{
		return false;
	}

	@Override
	@BusProperty(signature = "u")
	public void setHue(int hue) throws BusException
	{
		// Intentionally left blank
	}

	@Override
	@BusProperty(signature = "u")
	public int getHue() throws BusException
	{
		return 0;
	}

	@Override
	@BusProperty(signature = "u")
	public void setSaturation(int saturation) throws BusException
	{
		// Intentionally left blank
	}

	@Override
	@BusProperty(signature = "u")
	public int getSaturation() throws BusException
	{
		return 0;
	}

	@Override
	@BusProperty(signature = "u")
	public void setColorTemp(int colorTemp) throws BusException
	{
		// Intentionally left blank
	}

	@Override
	@BusProperty(signature = "u")
	public int getColorTemp() throws BusException
	{
		return 0;
	}

	@Override
	@BusProperty(signature = "u")
	public void setBrightness(int brightness) throws BusException
	{
		// Intentionally left blank
	}

	@Override
	@BusProperty(signature = "u")
	public int getBrightness() throws BusException
	{
		return 0;
	}
}
