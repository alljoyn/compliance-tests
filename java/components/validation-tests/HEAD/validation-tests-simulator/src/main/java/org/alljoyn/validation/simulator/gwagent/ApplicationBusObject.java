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
package org.alljoyn.validation.simulator.gwagent;

import java.util.Map;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.gatewaycontroller.sdk.managerinterfaces.AclInfoAJ;
import org.alljoyn.gatewaycontroller.sdk.managerinterfaces.AclManagement;
import org.alljoyn.gatewaycontroller.sdk.managerinterfaces.Application;
import org.alljoyn.gatewaycontroller.sdk.managerinterfaces.ApplicationStatusAJ;
import org.alljoyn.gatewaycontroller.sdk.managerinterfaces.CreateAclStatusAJ;
import org.alljoyn.gatewaycontroller.sdk.managerinterfaces.ManifestObjectDescriptionAJ;
import org.alljoyn.gatewaycontroller.sdk.managerinterfaces.ManifestRulesAJ;
import org.alljoyn.gatewaycontroller.sdk.managerinterfaces.RemotedAppAJ;

public class ApplicationBusObject implements Application, AclManagement, BusObject
{

    @Override
    public ApplicationStatusAJ getApplicationStatus() throws BusException
    {
        return null;
    }

    @Override
    public ManifestRulesAJ getManifestInterfaces() throws BusException
    {
        return null;
    }

    @Override
    public short restartApp() throws BusException
    {
        return 0;
    }

    @Override
    public String getManifestFile() throws BusException
    {
        return null;
    }

    @Override
    public void applicationStatusChanged(short installStatus, String installDescription, short connectionStatus, short operationalStatus) throws BusException
    {
    }

    @Override
    public CreateAclStatusAJ createAcl(String aclName, ManifestObjectDescriptionAJ[] exposedServices, RemotedAppAJ[] remotedApps, Map<String, String> internalMetadata,
            Map<String, String> customMetadata) throws BusException
    {
        return null;
    }

    @Override
    public short deleteAcl(String aclId) throws BusException
    {
        return 0;
    }

    @Override
    public AclInfoAJ[] listAcls() throws BusException
    {
        return null;
    }

    @Override
    public short getVersion() throws BusException
    {
        return 1;
    }

}
