/******************************************************************************
 * Copyright AllSeen Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for any
 *    purpose with or without fee is hereby granted, provided that the above
 *    copyright notice and this permission notice appear in all copies.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 ******************************************************************************/

package org.allseen.timeservice.server;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.ErrorReplyBusException;
import org.alljoyn.bus.Status;
import org.alljoyn.bus.Translator;
import org.allseen.timeservice.TimeServiceConst;
import org.allseen.timeservice.TimeServiceException;
import org.allseen.timeservice.ajinterfaces.TimerFactory;

import com.at4wireless.alljoyn.core.commons.log.WindowsLoggerImpl;



// TODO: Auto-generated Javadoc
/**
 * This class implements {@link TimerFactory} interface and realizes AllJoyn
 * communication with this TimerFactory
 */
class TimerFactoryBusObj implements TimerFactory {
    
    /** The Constant TAG. */
    private static final String TAG = "ajts" + TimerFactoryBusObj.class.getSimpleName();
	
	/** The Constant Log. */
	private static final WindowsLoggerImpl Log =  new WindowsLoggerImpl(TAG);

    /**
     * Prefix of the TimerFactory object path
     */
    private static final String OBJ_PATH_PREFIX = "/TimerFactory";

    /**
     * Timer factory to be notified with
     * {@link org.allseen.timeservice.server.TimerFactory} related messages
     */
    private org.allseen.timeservice.server.TimerFactory timerFactory;

    /**
     * Object path of this {@link BusObject}
     */
    private final String objectPath;

    /**
     * Events & Actions object description
     */
    private final String description;

    /**
     * Events & Actions description language
     */
    private final String language;

    /**
     * Events & Actions translator
     */
    private final Translator translator;

    /**
     * Constructor
     * 
     * @param timerFactory
     *            {@link org.allseen.timeservice.server.TimerFactory} handler
     * @throws TimeServiceException
     *             Is thrown if failed to create {@link TimerFactoryBusObj}
     */
    TimerFactoryBusObj(org.allseen.timeservice.server.TimerFactory timerFactory) throws TimeServiceException {

        this(timerFactory, null, null, null);
    }

    /**
     * Constructor
     * 
     * @param timerFactory
     * @param description
     *            Events&Actions description
     * @param language
     *            Events&Actions description language
     * @param translator
     *            Events&Actions {@link Translator}
     * @throws TimeServiceException
     *             Is thrown if failed to create {@link TimerFactoryBusObj}
     */
    TimerFactoryBusObj(org.allseen.timeservice.server.TimerFactory timerFactory, String description, String language, Translator translator) throws TimeServiceException {

        if (timerFactory == null) {

            throw new TimeServiceException("Undefined TimerFactory");
        }

        this.timerFactory = timerFactory;
        objectPath = GlobalStringSequencer.append(OBJ_PATH_PREFIX);

        this.description = description;
        this.language = language;
        this.translator = translator;

        Status status = getBus().registerBusObject(this, objectPath);
        if (status != Status.OK) {

            throw new TimeServiceException("Failed to register BusObject, objPath: '" + objectPath + "', Status: '" + status + "'");
        }

        Log.info("TimerFactory BusObject, objectPath: '" + objectPath + "' registered successfully");
    }

    /**
     * @see org.allseen.timeservice.ajinterfaces.TimerFactory#getVersion()
     */
    @Override
    public short getVersion() throws BusException {

        return VERSION;
    }

    /**
     * @see org.allseen.timeservice.ajinterfaces.TimerFactory#newTimer()
     */
    @Override
    public String newTimer() throws BusException {

        Timer timer;

        try {

            getBus().enableConcurrentCallbacks();

            Log.debug("NewTimer is called, objPath: '" + objectPath + "', handling");

            timer = timerFactory.newTimer();
            if (timer == null) {

                Log.error("Undefined timer, throwing exception, objPath: '" + objectPath + "'");
                throw new ErrorReplyBusException(TimeServiceConst.GENERIC_ERROR, "Uninitialized Timer");
            }

            BaseTimerBusObj timerBusObj = new TimerBusObj();
            timerBusObj.init(timer, objectPath, false, null, description, language, translator);
            timer.setTimerBusObj(timerBusObj);
        } catch (ErrorReplyBusException erbe) {

            Log.error("Failed to execute 'NewTimer', objPath: '" + objectPath + "'", erbe);
            throw erbe;
        } catch (Exception e) {

            Log.error("Failed to execute 'NewTimer', objPath: '" + objectPath + "'", e);
            throw new ErrorReplyBusException(TimeServiceConst.GENERIC_ERROR, e.getMessage());
        }

        return timer.getObjectPath();
    }

    /**
     * @see org.allseen.timeservice.ajinterfaces.TimerFactory#deleteTimer(java.lang.String)
     */
    @Override
    public void deleteTimer(String objectPath) throws BusException {

        try {

            getBus().enableConcurrentCallbacks();

            Log.debug("DeleteTimer is called, objPath: '" + objectPath + "', handling");

            timerFactory.deleteTimer(objectPath);
        } catch (ErrorReplyBusException erbe) {

            Log.error("Failed to execute 'DeleteTimer', objPath: '" + objectPath + "'", erbe);
            throw erbe;
        } catch (Exception e) {

            Log.error("Failed to execute 'DeleteTimer', objPath: '" + objectPath + "'", e);
            throw new ErrorReplyBusException(TimeServiceConst.GENERIC_ERROR, e.getMessage());
        }
    }

    /**
     * {@link org.allseen.timeservice.server.TimerFactory} object path
     * 
     * @return object path
     */
    String getObjectPath() {

        return objectPath;
    }

    /**
     * Releases object resources
     */
    void release() {

        Log.debug("Releasing Server Timer Factory object, objPath: '" + objectPath + "'");
        timerFactory = null;

        try {

            getBus().unregisterBusObject(this);
        } catch (TimeServiceException tse) {

            Log.error("Failed to unregister BusObject, objPath: '" + objectPath + "'", tse);
        }

    }

    /**
     * Access {@link TimeServiceServer} to get the {@link BusAttachment}. If
     * {@link BusAttachment} is undefined, {@link TimeServiceException} is
     * thrown.
     * 
     * @return {@link BusAttachment}
     * @throws TimeServiceException
     */
    BusAttachment getBus() throws TimeServiceException {

        BusAttachment bus = TimeServiceServer.getInstance().getBusAttachment();

        if (bus == null) {

            throw new TimeServiceException("TimeServiceServer is not initialized");
        }

        return bus;
    }
}
