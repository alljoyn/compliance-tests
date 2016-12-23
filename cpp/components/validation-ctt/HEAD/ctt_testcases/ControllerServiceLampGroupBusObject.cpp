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
#include "stdafx.h"
#include "ControllerServiceLampGroupBusObject.h"

#define CHECK_BREAK(x) if ((status = x) != ER_OK) { break; }
#define CHECK_RETURN(x) if ((status = x) != ER_OK) { return status; }

static AJ_PCSTR CONTROLLERSERVICE_OBJECT_PATH = "/org/allseen/LSF/ControllerService";
static AJ_PCSTR LAMPGROUP_INTERFACE_NAME = "org.allseen.LSF.ControllerService.LampGroup";

ControllerServiceLampGroupBusObject::ControllerServiceLampGroupBusObject(ajn::BusAttachment& t_BusAttachment, const std::string& t_BusName, const ajn::SessionId t_SessionId) :
m_BusAttachment(&t_BusAttachment), m_BusName(t_BusName), m_SessionId(t_SessionId),
m_LampGroupsNameChangedSignalReceived(false),
m_LampGroupsCreatedSignalReceived(false), m_LampGroupsUpdatedSignalReceived(false),
m_LampGroupsDeletedSignalReceived(false)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* getIface = NULL;
	getIface = m_BusAttachment->GetInterface(LAMPGROUP_INTERFACE_NAME);
	if (!getIface) {
		ajn::InterfaceDescription* createIface = NULL;
		status = m_BusAttachment->CreateInterface(LAMPGROUP_INTERFACE_NAME, createIface, ajn::AJ_IFC_SECURITY_OFF);
		if (createIface && status == ER_OK) {
			do {
				CHECK_BREAK(createIface->AddMethod("GetAllLampGroupIDs", NULL, "uas", "responseCode,lampGroupIDs"))
					CHECK_BREAK(createIface->AddMethod("GetLampGroupName", "ss", "usss", "lampGroupID,language,responseCode,lampGroupID,language,lampGroupName"))
					CHECK_BREAK(createIface->AddMethod("SetLampGroupName", "sss", "uss", "lampGroupID,lampName,language,responseCode,lampID,language"))
					CHECK_BREAK(createIface->AddMethod("CreateLampGroup", "asasss", "us", "lampIDs,lampGroupIDs,lampGroupName,language,responseCode,lampGroupID"))
					CHECK_BREAK(createIface->AddMethod("UpdateLampGroup", "sasas", "us", "lampGroupID,lampIDs,lampGroupIDs,responseCode,lampGroupID"))
					CHECK_BREAK(createIface->AddMethod("DeleteLampGroup", "s", "us", "lampGroupID,responseCode,lampGroupID"))
					CHECK_BREAK(createIface->AddMethod("GetLampGroup", "s", "usasas", "lampGroupID,responseCode,lampGroupID,lampIDs,lampGroupIDs"))
					CHECK_BREAK(createIface->AddMethod("TransitionLampGroupState", "sa{sv}u", "us", "lampGroupID,lampState,transitionPeriod,responseCode,lampGroupID"))
					CHECK_BREAK(createIface->AddMethod("PulseLampGroupWithState", "sa{sv}a{sv}uuu", "us", "lampGroupID,fromLampState,toLampState,period,duration,numPulses,responseCode,lampGroupID"))
					CHECK_BREAK(createIface->AddMethod("PulseLampGroupWithPreset", "sssuuu", "us", "lampGroupID,fromPresetID,toPresetID,period,duration,numPulses,responseCode,lampGroupID"))
					CHECK_BREAK(createIface->AddMethod("TransitionLampGroupStateToPreset", "ssu", "us", "lampGroupID,presetID,transitionPeriod,responseCode,lampGroupID"))
					CHECK_BREAK(createIface->AddMethod("TransitionLampGroupStateField", "ssvu", "uss", "lampGroupID,lampGroupStateFieldName,lampGroupStateFieldValue,transitionPeriod,responseCode,lampGroupID,lampGroupStateFieldName"))
					CHECK_BREAK(createIface->AddMethod("ResetLampGroupState", "s", "us", "lampGroupID,responseCode,lampGroupID"))
					CHECK_BREAK(createIface->AddMethod("ResetLampGroupStateField", "ss", "uss", "lampGroupID,lampGroupStateFieldName,responseCode,lampGroupID,lampGroupStateFieldName"))

					CHECK_BREAK(createIface->AddProperty("Version", "u", ajn::PROP_ACCESS_READ))

					CHECK_BREAK(createIface->AddSignal("LampGroupsNameChanged", "as", "lampGroupsIDs", 0))
					CHECK_BREAK(createIface->AddSignal("LampGroupsCreated", "as", "lampGroupsIDs", 0))
					CHECK_BREAK(createIface->AddSignal("LampGroupsUpdated", "as", "lampGroupsIDs", 0))
					CHECK_BREAK(createIface->AddSignal("LampGroupsDeleted", "as", "lampGroupsIDs", 0))
					createIface->Activate();

				const ajn::InterfaceDescription* uniqueId = m_BusAttachment->GetInterface(LAMPGROUP_INTERFACE_NAME);
				const ajn::InterfaceDescription::Member* sig = uniqueId->GetMember("LampGroupsNameChanged");
				m_BusAttachment->RegisterSignalHandler(this, static_cast<ajn::MessageReceiver::SignalHandler>(&ControllerServiceLampGroupBusObject::LampGroupsNameChangedSignalHandler), sig, CONTROLLERSERVICE_OBJECT_PATH);

				sig = uniqueId->GetMember("LampGroupsCreated");
				m_BusAttachment->RegisterSignalHandler(this, static_cast<ajn::MessageReceiver::SignalHandler>(&ControllerServiceLampGroupBusObject::LampGroupsCreatedSignalHandler), sig, CONTROLLERSERVICE_OBJECT_PATH);

				sig = uniqueId->GetMember("LampGroupsUpdated");
				m_BusAttachment->RegisterSignalHandler(this, static_cast<ajn::MessageReceiver::SignalHandler>(&ControllerServiceLampGroupBusObject::LampGroupsUpdatedSignalHandler), sig, CONTROLLERSERVICE_OBJECT_PATH);

				sig = uniqueId->GetMember("LampGroupsDeleted");
				m_BusAttachment->RegisterSignalHandler(this, static_cast<ajn::MessageReceiver::SignalHandler>(&ControllerServiceLampGroupBusObject::LampGroupsDeletedSignalHandler), sig, CONTROLLERSERVICE_OBJECT_PATH);

				return;
			} while (0);
		} //if (createIface)
	} //if (!getIface)
}

void ControllerServiceLampGroupBusObject::LampGroupsNameChangedSignalHandler(const ajn::InterfaceDescription::Member* member, AJ_PCSTR sourcePath, ajn::Message& msg)
{
	LOG(INFO) << "LampGroupsNameChanged signal received";
	m_LampGroupsNameChangedSignalReceived = true;
}

bool ControllerServiceLampGroupBusObject::DidLampGroupsNameChanged()
{
	return m_LampGroupsNameChangedSignalReceived;
}

void ControllerServiceLampGroupBusObject::LampGroupsCreatedSignalHandler(const ajn::InterfaceDescription::Member* member, AJ_PCSTR sourcePath, ajn::Message& msg)
{
	LOG(INFO) << "LampGroupsCreated signal received";
	m_LampGroupsCreatedSignalReceived = true;
}

bool ControllerServiceLampGroupBusObject::DidLampGroupsCreated()
{
	return m_LampGroupsCreatedSignalReceived;
}

void ControllerServiceLampGroupBusObject::LampGroupsUpdatedSignalHandler(const ajn::InterfaceDescription::Member* member, AJ_PCSTR sourcePath, ajn::Message& msg)
{
	LOG(INFO) << "LampGroupsUpdated signal received";
	m_LampGroupsUpdatedSignalReceived = true;
}

bool ControllerServiceLampGroupBusObject::DidLampGroupsUpdated()
{
	return m_LampGroupsUpdatedSignalReceived;
}

void ControllerServiceLampGroupBusObject::LampGroupsDeletedSignalHandler(const ajn::InterfaceDescription::Member* member, AJ_PCSTR sourcePath, ajn::Message& msg)
{
	LOG(INFO) << "LampGroupsDeleted signal received";
	m_LampGroupsDeletedSignalReceived = true;
}

bool ControllerServiceLampGroupBusObject::DidLampGroupsDeleted()
{
	return m_LampGroupsDeletedSignalReceived;
}

QStatus ControllerServiceLampGroupBusObject::GetVersion(uint32_t& version)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMPGROUP_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), CONTROLLERSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	do {
		ajn::MsgArg arg;
		CHECK_BREAK(proxyBusObj->AddInterface(*p_InterfaceDescription))
		CHECK_BREAK(proxyBusObj->GetProperty(LAMPGROUP_INTERFACE_NAME, "Version", arg))
		version = arg.v_variant.val->v_uint32;
	} while (0);
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceLampGroupBusObject::GetAllLampGroupIDs(uint32_t& t_ResponseCode,
	std::vector<qcc::String>& t_LampGroupIDs)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMPGROUP_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), CONTROLLERSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	status = proxyBusObj->AddInterface(*p_InterfaceDescription);
	if (status == ER_OK) {
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(LAMPGROUP_INTERFACE_NAME, "GetAllLampGroupIDs", NULL, 0, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		uint32_t* tempIDsArraySize = new uint32_t();
		ajn::MsgArg* tempIDsArray;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("as", tempIDsArraySize, &tempIDsArray))

		t_LampGroupIDs.clear();
		for (size_t i = 0; i < *tempIDsArraySize; ++i)
		{
			t_LampGroupIDs.push_back(tempIDsArray[i].v_string.str);
		}
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceLampGroupBusObject::GetLampGroupName(AJ_PCSTR t_LampGroupID, AJ_PCSTR t_Language, uint32_t& t_ResponseCode, qcc::String& t_RetrievedLampGroupID, qcc::String& t_RetrievedLanguage, qcc::String& t_LampGroupName)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMPGROUP_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), CONTROLLERSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	status = proxyBusObj->AddInterface(*p_InterfaceDescription);
	if (status == ER_OK) {
		ajn::MsgArg* msgArg = new ajn::MsgArg[2];
		msgArg[0].Set("s", t_LampGroupID);
		msgArg[1].Set("s", t_Language);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(LAMPGROUP_INTERFACE_NAME, "GetLampGroupName", msgArg, 2, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

        AJ_PSTR tempRetrievedLampGroupID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedLampGroupID))
		t_RetrievedLampGroupID = qcc::String(tempRetrievedLampGroupID);

        AJ_PSTR tempRetrievedLanguage;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedLanguage))
		t_RetrievedLanguage = qcc::String(tempRetrievedLanguage);

        AJ_PSTR tempLampGroupName;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempLampGroupName))
		t_LampGroupName = qcc::String(tempLampGroupName);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceLampGroupBusObject::SetLampGroupName(AJ_PCSTR t_LampGroupID, AJ_PCSTR t_LampGroupName,
    AJ_PCSTR t_Language, uint32_t& t_ResponseCode, qcc::String& t_RetrievedLampGroupID, qcc::String& t_RetrievedLanguage)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMPGROUP_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), CONTROLLERSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	status = proxyBusObj->AddInterface(*p_InterfaceDescription);
	if (status == ER_OK) {
		ajn::MsgArg* msgArg = new ajn::MsgArg[2];
		msgArg[0].Set("s", t_LampGroupID);
		msgArg[1].Set("s", t_LampGroupName);
		msgArg[2].Set("s", t_Language);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(LAMPGROUP_INTERFACE_NAME, "SetLampGroupName", msgArg, 3, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

        AJ_PSTR tempRetrievedLampGroupID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedLampGroupID))
		t_RetrievedLampGroupID = qcc::String(tempRetrievedLampGroupID);

        AJ_PSTR tempRetrievedLanguage;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedLanguage))
		t_RetrievedLanguage = qcc::String(tempRetrievedLanguage);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceLampGroupBusObject::CreateLampGroup(const std::vector<AJ_PCSTR>& t_LampIDs,
    const std::vector<AJ_PCSTR>& t_LampGroupIDs, AJ_PCSTR t_LampGroupName, AJ_PCSTR t_Language,
    uint32_t& t_ResponseCode, qcc::String& t_RetrievedLampGroupID)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMPGROUP_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), CONTROLLERSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	status = proxyBusObj->AddInterface(*p_InterfaceDescription);
	if (status == ER_OK) {
		ajn::MsgArg* msgArg = new ajn::MsgArg[4];
		msgArg[0].Set("as", t_LampIDs.size(), t_LampIDs.data());
		msgArg[1].Set("as", t_LampGroupIDs.size(), t_LampGroupIDs.data());
		msgArg[2].Set("s", t_LampGroupName);
		msgArg[3].Set("s", t_Language);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(LAMPGROUP_INTERFACE_NAME, "CreateLampGroup", msgArg, 4, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

        AJ_PSTR tempRetrievedLampGroupID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedLampGroupID))
		t_RetrievedLampGroupID = qcc::String(tempRetrievedLampGroupID);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceLampGroupBusObject::UpdateLampGroup(AJ_PCSTR t_LampGroupID, const std::vector<AJ_PCSTR>& t_LampIDs,
    const std::vector<AJ_PCSTR>& t_LampGroupIDs, uint32_t& t_ResponseCode, qcc::String& t_RetrievedLampGroupID)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMPGROUP_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), CONTROLLERSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	status = proxyBusObj->AddInterface(*p_InterfaceDescription);
	if (status == ER_OK) {
		ajn::MsgArg* msgArg = new ajn::MsgArg[3];
		msgArg[0].Set("s", t_LampGroupID);
		msgArg[1].Set("as", t_LampIDs.size(), t_LampIDs.data());
		msgArg[2].Set("as", t_LampGroupIDs.size(), t_LampGroupIDs.data());
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(LAMPGROUP_INTERFACE_NAME, "UpdateLampGroup", msgArg, 3, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

        AJ_PSTR tempRetrievedLampGroupID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedLampGroupID))
		t_RetrievedLampGroupID = qcc::String(tempRetrievedLampGroupID);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceLampGroupBusObject::DeleteLampGroup(AJ_PCSTR t_LampGroupID, uint32_t& t_ResponseCode, qcc::String& t_RetrievedLampGroupID)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMPGROUP_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), CONTROLLERSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	status = proxyBusObj->AddInterface(*p_InterfaceDescription);
	if (status == ER_OK) {
		ajn::MsgArg* msgArg = new ajn::MsgArg[1];
		msgArg[0].Set("s", t_LampGroupID);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(LAMPGROUP_INTERFACE_NAME, "DeleteLampGroup", msgArg, 1, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

        AJ_PSTR tempRetrievedLampGroupID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedLampGroupID))
		t_RetrievedLampGroupID = qcc::String(tempRetrievedLampGroupID);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceLampGroupBusObject::GetLampGroup(AJ_PCSTR t_LampGroupID, uint32_t& t_ResponseCode,
    qcc::String& t_RetrievedLampGroupID, std::vector<qcc::String>& t_LampIDs, std::vector<qcc::String>& t_LampGroupIDs)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMPGROUP_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), CONTROLLERSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	status = proxyBusObj->AddInterface(*p_InterfaceDescription);
	if (status == ER_OK) {
		ajn::MsgArg* msgArg = new ajn::MsgArg[1];
		msgArg[0].Set("s", t_LampGroupID);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(LAMPGROUP_INTERFACE_NAME, "GetLampGroup", msgArg, 1, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

        AJ_PSTR tempRetrievedLampGroupID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedLampGroupID))
		t_RetrievedLampGroupID = qcc::String(tempRetrievedLampGroupID);

		size_t tempLampIDsSize;
		ajn::MsgArg* tempLampIDs;
		CHECK_RETURN(responseMessage->GetArg(2)->Get("as", &tempLampIDsSize, &tempLampIDs))

		t_LampIDs.clear();
		for (size_t i = 0; i < tempLampIDsSize; ++i)
		{
			t_LampIDs.push_back(tempLampIDs[i].v_string.str);
		}

		size_t tempLampGroupIDsSize;
		ajn::MsgArg* tempLampGroupIDs;
		CHECK_RETURN(responseMessage->GetArg(3)->Get("as", &tempLampGroupIDsSize, &tempLampGroupIDs))

		t_LampGroupIDs.clear();
		for (size_t i = 0; i < tempLampGroupIDsSize; ++i)
		{
			t_LampGroupIDs.push_back(tempLampGroupIDs[i].v_string.str);
		}
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceLampGroupBusObject::TransitionLampGroupState(AJ_PCSTR t_LampGroupID, ajn::MsgArg* t_LampState,
    uint32_t t_TransitionPeriod, uint32_t& t_ResponseCode, qcc::String& t_RetrievedLampGroupID)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMPGROUP_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), CONTROLLERSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	status = proxyBusObj->AddInterface(*p_InterfaceDescription);
	if (status == ER_OK) {
		ajn::MsgArg* msgArg = new ajn::MsgArg[3];
		msgArg[0].Set("s", t_LampGroupID);
		msgArg[1].Set("a{sv}", 5, t_LampState);
		msgArg[2].Set("u", t_TransitionPeriod);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(LAMPGROUP_INTERFACE_NAME, "TransitionLampGroupState", msgArg, 3, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

        AJ_PSTR tempRetrievedLampGroupID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedLampGroupID))
		t_RetrievedLampGroupID = qcc::String(tempRetrievedLampGroupID);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceLampGroupBusObject::PulseLampGroupWithState(AJ_PCSTR t_LampGroupID,
    ajn::MsgArg* t_FromLampState, ajn::MsgArg* t_ToLampState, uint32_t t_Period, uint32_t t_Duration,
    uint32_t t_NumPulses, uint32_t& t_ResponseCode, qcc::String& t_RetrievedLampGroupID)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMPGROUP_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), CONTROLLERSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	status = proxyBusObj->AddInterface(*p_InterfaceDescription);
	if (status == ER_OK) {
		ajn::MsgArg* msgArg = new ajn::MsgArg[6];
		msgArg[0].Set("s", t_LampGroupID);
		msgArg[1].Set("a{sv}", 5, t_FromLampState);
		msgArg[2].Set("a{sv}", 5, t_ToLampState);
		msgArg[3].Set("u", t_Period);
		msgArg[4].Set("u", t_Duration);
		msgArg[5].Set("u", t_NumPulses);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(LAMPGROUP_INTERFACE_NAME, "PulseLampGroupWithState", msgArg, 6, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

        AJ_PSTR tempRetrievedLampGroupID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedLampGroupID))
		t_RetrievedLampGroupID = qcc::String(tempRetrievedLampGroupID);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceLampGroupBusObject::PulseLampGroupWithPreset(AJ_PCSTR t_LampGroupID,
    AJ_PCSTR t_FromPresetID, AJ_PCSTR t_ToPresetID, uint32_t t_Period, uint32_t t_Duration,
    uint32_t t_NumPulses, uint32_t& t_ResponseCode, qcc::String& t_RetrievedLampGroupID)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMPGROUP_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), CONTROLLERSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	status = proxyBusObj->AddInterface(*p_InterfaceDescription);
	if (status == ER_OK) {
		ajn::MsgArg* msgArg = new ajn::MsgArg[6];
		msgArg[0].Set("s", t_LampGroupID);
		msgArg[1].Set("s", t_FromPresetID);
		msgArg[2].Set("s", t_ToPresetID);
		msgArg[3].Set("u", t_Period);
		msgArg[4].Set("u", t_Duration);
		msgArg[5].Set("u", t_NumPulses);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(LAMPGROUP_INTERFACE_NAME, "PulseLampGroupWithPreset", msgArg, 6, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

        AJ_PSTR tempRetrievedLampGroupID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedLampGroupID))
		t_RetrievedLampGroupID = qcc::String(tempRetrievedLampGroupID);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceLampGroupBusObject::TransitionLampGroupStateToPreset(AJ_PCSTR t_LampGroupID,
    AJ_PCSTR t_PresetID, uint32_t t_TransitionPeriod, uint32_t& t_ResponseCode, qcc::String& t_RetrievedLampGroupID)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMPGROUP_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), CONTROLLERSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	status = proxyBusObj->AddInterface(*p_InterfaceDescription);
	if (status == ER_OK) {
		ajn::MsgArg* msgArg = new ajn::MsgArg[3];
		msgArg[0].Set("s", t_LampGroupID);
		msgArg[1].Set("s", t_PresetID);
		msgArg[2].Set("u", t_TransitionPeriod);

		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(LAMPGROUP_INTERFACE_NAME, "TransitionLampGroupStateToPreset", msgArg, 3, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

        AJ_PSTR tempRetrievedLampGroupID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedLampGroupID))
		t_RetrievedLampGroupID = qcc::String(tempRetrievedLampGroupID);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceLampGroupBusObject::TransitionLampGroupStateField(AJ_PCSTR t_LampGroupID,
    AJ_PCSTR t_LampGroupStateFieldName, ajn::MsgArg t_LampGroupStateFieldValue, uint32_t t_TransitionPeriod,
    uint32_t& t_ResponseCode, qcc::String& t_RetrievedLampGroupID, qcc::String& t_RetrievedLampGroupStateFieldName)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMPGROUP_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), CONTROLLERSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	status = proxyBusObj->AddInterface(*p_InterfaceDescription);
	if (status == ER_OK) {
		ajn::MsgArg* msgArg = new ajn::MsgArg[4];
		msgArg[0].Set("s", t_LampGroupID);
		msgArg[1].Set("s", t_LampGroupStateFieldName);
		msgArg[2].Set("v", &t_LampGroupStateFieldValue);
		msgArg[3].Set("u", t_TransitionPeriod);

		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(LAMPGROUP_INTERFACE_NAME, "TransitionLampGroupStateField", msgArg, 4, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

        AJ_PSTR tempRetrievedLampGroupID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedLampGroupID))
		t_RetrievedLampGroupID = qcc::String(tempRetrievedLampGroupID);

        AJ_PSTR tempRetrievedLampGroupStateFieldName;
		CHECK_RETURN(responseMessage->GetArg(2)->Get("s", &tempRetrievedLampGroupStateFieldName))
		t_RetrievedLampGroupStateFieldName = qcc::String(tempRetrievedLampGroupStateFieldName);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceLampGroupBusObject::ResetLampGroupState(AJ_PCSTR t_LampGroupID, uint32_t& t_ResponseCode, qcc::String& t_RetrievedLampGroupID)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMPGROUP_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), CONTROLLERSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	status = proxyBusObj->AddInterface(*p_InterfaceDescription);
	if (status == ER_OK) {
		ajn::MsgArg* msgArg = new ajn::MsgArg[1];
		msgArg[0].Set("s", t_LampGroupID);

		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(LAMPGROUP_INTERFACE_NAME, "ResetLampGroupState", msgArg, 1, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

        AJ_PSTR tempRetrievedLampGroupID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedLampGroupID))
		t_RetrievedLampGroupID = qcc::String(tempRetrievedLampGroupID);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceLampGroupBusObject::ResetLampGroupStateField(AJ_PCSTR t_LampGroupID,
    AJ_PCSTR t_LampGroupStateFieldName, uint32_t& t_ResponseCode, qcc::String& t_RetrievedLampGroupID,
    qcc::String& t_RetrievedLampGroupStateFieldName)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(LAMPGROUP_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), CONTROLLERSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	status = proxyBusObj->AddInterface(*p_InterfaceDescription);
	if (status == ER_OK) {
		ajn::MsgArg* msgArg = new ajn::MsgArg[2];
		msgArg[0].Set("s", t_LampGroupID);
		msgArg[1].Set("s", t_LampGroupStateFieldName);

		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(LAMPGROUP_INTERFACE_NAME, "ResetLampGroupStateField", msgArg, 2, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

        AJ_PSTR tempRetrievedLampGroupID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedLampGroupID))
		t_RetrievedLampGroupID = qcc::String(tempRetrievedLampGroupID);

        AJ_PSTR tempRetrievedLampGroupStateFieldName;
		CHECK_RETURN(responseMessage->GetArg(2)->Get("s", &tempRetrievedLampGroupStateFieldName))
		t_RetrievedLampGroupStateFieldName = qcc::String(tempRetrievedLampGroupStateFieldName);
	}
	delete proxyBusObj;
	return status;
}