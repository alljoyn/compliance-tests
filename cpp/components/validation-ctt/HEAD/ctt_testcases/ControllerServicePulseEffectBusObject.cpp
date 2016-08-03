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
#include "ControllerServicePulseEffectBusObject.h"

#define CHECK_BREAK(x) if ((status = x) != ER_OK) { break; }
#define CHECK_RETURN(x) if ((status = x) != ER_OK) { return status; }

static const char* CONTROLLERSERVICE_OBJECT_PATH = "/org/allseen/LSF/ControllerService";
static const char* PULSEEFFECT_INTERFACE_NAME = "org.allseen.LSF.ControllerService.PulseEffect";

ControllerServicePulseEffectBusObject::ControllerServicePulseEffectBusObject(ajn::BusAttachment& t_BusAttachment, const std::string& t_BusName, const ajn::SessionId t_SessionId) :
m_BusAttachment(&t_BusAttachment), m_BusName(t_BusName), m_SessionId(t_SessionId),
m_PulseEffectsCreatedSignalReceived(false),
m_PulseEffectsUpdatedSignalReceived(false), m_PulseEffectsDeletedSignalReceived(false),
m_PulseEffectsNameChangedSignalReceived(false)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* getIface = NULL;
	getIface = m_BusAttachment->GetInterface(PULSEEFFECT_INTERFACE_NAME);
	if (!getIface) {
		ajn::InterfaceDescription* createIface = NULL;
		status = m_BusAttachment->CreateInterface(PULSEEFFECT_INTERFACE_NAME, createIface, ajn::AJ_IFC_SECURITY_OFF);
		if (createIface && status == ER_OK) {
			do {
				CHECK_BREAK(createIface->AddMethod("ApplyPulseEffectOnLampGroups", "sas", "usas", "pulseEffectID,lampGroupIDs,responseCode,pulseEffectID,lampGroupIDs"))
				CHECK_BREAK(createIface->AddMethod("ApplyPulseEffectOnLamps", "sas", "usas", "pulseEffectID,lampIDs,responseCode,pulseEffectID,lampIDs"))
				CHECK_BREAK(createIface->AddMethod("CreatePulseEffect", "a{sv}uuua{sv}ssss", "us", "toLampState,pulsePeriod,pulseDuration,numPulses,fromLampState,toPresetID,fromPresetID,pulseEffectName,language,responseCode,pulseEffectID"))
				CHECK_BREAK(createIface->AddMethod("DeletePulseEffect", "s", "us", "pulseEffectID,responseCode,pulseEffectID"))
				CHECK_BREAK(createIface->AddMethod("GetAllPulseEffectIDs", NULL, "uas", "responseCode,pulseEffectIDs"))
				CHECK_BREAK(createIface->AddMethod("GetPulseEffect", "s", "usa{sv}uuua{sv}ss", "pulseEffectID,responseCode,pulseEffectID,toLampState,pulsePeriod,pulseDuration,numPulses,fromLampState,toPresetID,fromPresetID"))
				CHECK_BREAK(createIface->AddMethod("GetPulseEffectName", "ss", "usss", "pulseEffectID,language,responseCode,pulseEffectID,language,pulseEffectName"))
				CHECK_BREAK(createIface->AddMethod("SetPulseEffectName", "sss", "uss", "pulseEffectID,pulseEffectName,language,responseCode,pulseEffectID,language"))
				CHECK_BREAK(createIface->AddMethod("UpdatePulseEffect", "sa{sv}uuua{sv}ss", "us", "pulseEffectID,toLampState,pulsePeriod,pulseDuration,numPulses,fromLampState,toPresetID,fromPresetID,responseCode,pulseEffectID"))

				CHECK_BREAK(createIface->AddProperty("Version", "u", ajn::PROP_ACCESS_READ))

				CHECK_BREAK(createIface->AddSignal("PulseEffectsNameChanged", "as", "PulseEffectIDs", 0))
				CHECK_BREAK(createIface->AddSignal("PulseEffectsCreated", "as", "PulseEffectIDs", 0))
				CHECK_BREAK(createIface->AddSignal("PulseEffectsUpdated", "as", "PulseEffectIDs", 0))
				CHECK_BREAK(createIface->AddSignal("PulseEffectsDeleted", "as", "PulseEffectIDs", 0))
				createIface->Activate();

				const ajn::InterfaceDescription* uniqueId = m_BusAttachment->GetInterface(PULSEEFFECT_INTERFACE_NAME);
				const ajn::InterfaceDescription::Member* sig = uniqueId->GetMember("PulseEffectsCreated");
				m_BusAttachment->RegisterSignalHandler(this, static_cast<ajn::MessageReceiver::SignalHandler>(&ControllerServicePulseEffectBusObject::PulseEffectsCreatedSignalHandler), sig, CONTROLLERSERVICE_OBJECT_PATH);

				sig = uniqueId->GetMember("PulseEffectsUpdated");
				m_BusAttachment->RegisterSignalHandler(this, static_cast<ajn::MessageReceiver::SignalHandler>(&ControllerServicePulseEffectBusObject::PulseEffectsUpdatedSignalHandler), sig, CONTROLLERSERVICE_OBJECT_PATH);

				sig = uniqueId->GetMember("PulseEffectsDeleted");
				m_BusAttachment->RegisterSignalHandler(this, static_cast<ajn::MessageReceiver::SignalHandler>(&ControllerServicePulseEffectBusObject::PulseEffectsDeletedSignalHandler), sig, CONTROLLERSERVICE_OBJECT_PATH);

				sig = uniqueId->GetMember("PulseEffectsNameChanged");
				m_BusAttachment->RegisterSignalHandler(this, static_cast<ajn::MessageReceiver::SignalHandler>(&ControllerServicePulseEffectBusObject::PulseEffectsNameChangedSignalHandler), sig, CONTROLLERSERVICE_OBJECT_PATH);

				return;
			} while (0);
		} //if (createIface)
	} //if (!getIface)
}

void ControllerServicePulseEffectBusObject::PulseEffectsCreatedSignalHandler(const ajn::InterfaceDescription::Member* member, const char* sourcePath, ajn::Message& msg)
{
	LOG(INFO) << "PulseEffectsCreated signal received";
	m_PulseEffectsCreatedSignalReceived = true;
}

bool ControllerServicePulseEffectBusObject::DidPulseEffectsCreated()
{
	return m_PulseEffectsCreatedSignalReceived;
}

void ControllerServicePulseEffectBusObject::PulseEffectsUpdatedSignalHandler(const ajn::InterfaceDescription::Member* member, const char* sourcePath, ajn::Message& msg)
{
	LOG(INFO) << "PulseEffectsUpdated signal received";
	m_PulseEffectsUpdatedSignalReceived = true;
}

bool ControllerServicePulseEffectBusObject::DidPulseEffectsUpdated()
{
	return m_PulseEffectsUpdatedSignalReceived;
}

void ControllerServicePulseEffectBusObject::PulseEffectsDeletedSignalHandler(const ajn::InterfaceDescription::Member* member, const char* sourcePath, ajn::Message& msg)
{
	LOG(INFO) << "PulseEffectsDeleted signal received";
	m_PulseEffectsDeletedSignalReceived = true;
}

bool ControllerServicePulseEffectBusObject::DidPulseEffectsDeleted()
{
	return m_PulseEffectsDeletedSignalReceived;
}

void ControllerServicePulseEffectBusObject::PulseEffectsNameChangedSignalHandler(const ajn::InterfaceDescription::Member* member, const char* sourcePath, ajn::Message& msg)
{
	LOG(INFO) << "PulseEffectsNameChanged signal received";
	m_PulseEffectsNameChangedSignalReceived = true;
}

bool ControllerServicePulseEffectBusObject::DidPulseEffectsNameChanged()
{
	return m_PulseEffectsNameChangedSignalReceived;
}

QStatus ControllerServicePulseEffectBusObject::GetVersion(uint32_t& version)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(PULSEEFFECT_INTERFACE_NAME);
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
		CHECK_BREAK(proxyBusObj->GetProperty(PULSEEFFECT_INTERFACE_NAME, "Version", arg))
		version = arg.v_variant.val->v_uint32;
	} while (0);
	delete proxyBusObj;
	return status;
}

QStatus ControllerServicePulseEffectBusObject::ApplyPulseEffectOnLampGroups(const char* t_PulseEffectID, const std::vector<const char*>& t_LampGroupIDs, uint32_t& t_ResponseCode, qcc::String& t_RetrievedPulseEffectID, std::vector<qcc::String>& t_RetrievedLampGroupIDs)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(PULSEEFFECT_INTERFACE_NAME);
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
		msgArg[0].Set("s", t_PulseEffectID);
		msgArg[1].Set("as", t_LampGroupIDs.size(), t_LampGroupIDs.data());
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(PULSEEFFECT_INTERFACE_NAME, "ApplyPulseEffectOnLampGroups", msgArg, 2, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedPulseEffectID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedPulseEffectID))
		t_RetrievedPulseEffectID = qcc::String(tempRetrievedPulseEffectID);

		size_t tempLampGroupIDsSize;
		ajn::MsgArg* tempLampGroupIDs;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("as", &tempLampGroupIDsSize, &tempLampGroupIDs))

		t_RetrievedLampGroupIDs.clear();
		for (size_t i = 0; i < tempLampGroupIDsSize; ++i)
		{
			t_RetrievedLampGroupIDs.push_back(tempLampGroupIDs[i].v_string.str);
		}
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServicePulseEffectBusObject::ApplyPulseEffectOnLamps(const char* t_PulseEffectID, const std::vector<const char*>& t_LampIDs, uint32_t& t_ResponseCode, qcc::String& t_RetrievedPulseEffectID, std::vector<qcc::String>& t_RetrievedLampIDs)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(PULSEEFFECT_INTERFACE_NAME);
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
		msgArg[0].Set("s", t_PulseEffectID);
		msgArg[1].Set("as", t_LampIDs.size(), t_LampIDs.data());
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(PULSEEFFECT_INTERFACE_NAME, "ApplyPulseEffectOnLamps", msgArg, 2, responseMessage))
			CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

			char* tempRetrievedPulseEffectID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedPulseEffectID))
			t_RetrievedPulseEffectID = qcc::String(tempRetrievedPulseEffectID);

		size_t tempLampIDsSize;
		char** tempLampIDs;
		CHECK_RETURN(responseMessage->GetArg(2)->Get("as", &tempLampIDsSize, &tempLampIDs))

			t_RetrievedLampIDs.clear();
		for (size_t i = 0; i < tempLampIDsSize; ++i)
		{
			t_RetrievedLampIDs.push_back(tempLampIDs[i]);
		}
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServicePulseEffectBusObject::CreatePulseEffect(ajn::MsgArg* t_toLampState, const uint32_t t_PulsePeriod, const uint32_t t_PulseDuration, const uint32_t t_NumPulses, ajn::MsgArg* t_FromLampState, const char* t_ToPresetID, const char* t_FromPresetID, const char* t_PulseEffectName, const char* t_Language, uint32_t& t_ResponseCode, qcc::String& t_RetrievedPulseEffectID)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(PULSEEFFECT_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), CONTROLLERSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	status = proxyBusObj->AddInterface(*p_InterfaceDescription);
	if (status == ER_OK) {
		ajn::MsgArg* msgArg = new ajn::MsgArg[9];
		msgArg[0].Set("a{sv}", 5, t_toLampState);
		msgArg[1].Set("u", t_PulsePeriod);
		msgArg[2].Set("u", t_PulseDuration);
		msgArg[3].Set("u", t_NumPulses);
		msgArg[4].Set("a{sv}", 5, t_FromLampState);
		msgArg[5].Set("s", t_ToPresetID);
		msgArg[6].Set("s", t_FromPresetID);
		msgArg[7].Set("s", t_PulseEffectName);
		msgArg[8].Set("s", t_Language);

		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(PULSEEFFECT_INTERFACE_NAME, "CreatePulseEffect", msgArg, 9, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedPulseEffectID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedPulseEffectID))
		t_RetrievedPulseEffectID = qcc::String(tempRetrievedPulseEffectID);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServicePulseEffectBusObject::DeletePulseEffect(const char* t_PulseEffectID, uint32_t& t_ResponseCode, qcc::String& t_RetrievedPulseEffectID)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(PULSEEFFECT_INTERFACE_NAME);
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
		msgArg[0].Set("s", t_PulseEffectID);

		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(PULSEEFFECT_INTERFACE_NAME, "DeletePulseEffect", msgArg, 1, responseMessage))
			CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

			char* tempRetrievedPulseEffectID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedPulseEffectID))
			t_RetrievedPulseEffectID = qcc::String(tempRetrievedPulseEffectID);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServicePulseEffectBusObject::GetAllPulseEffectIDs(uint32_t& t_ResponseCode, std::vector<qcc::String>& t_PulseEffectIDs)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(PULSEEFFECT_INTERFACE_NAME);
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
		CHECK_RETURN(proxyBusObj->MethodCall(PULSEEFFECT_INTERFACE_NAME, "GetAllPulseEffectIDs", NULL, 0, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		size_t tempPulseEffectIDsSize;
		ajn::MsgArg* tempPulseEffectIDs;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("as", &tempPulseEffectIDsSize, &tempPulseEffectIDs))

			t_PulseEffectIDs.clear();
		for (size_t i = 0; i < tempPulseEffectIDsSize; ++i)
		{
			t_PulseEffectIDs.push_back(tempPulseEffectIDs[i].v_string.str);
		}
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServicePulseEffectBusObject::GetPulseEffect(const char* t_PulseEffectID, uint32_t& t_ResponseCode, qcc::String& t_RetrievedPulseEffectID, std::vector<ajn::MsgArg>& t_ToLampState, uint32_t& t_PulsePeriod, uint32_t& t_PulseDuration, uint32_t& t_NumPulses, std::vector<ajn::MsgArg>& t_FromLampState, qcc::String& t_ToPresetID, qcc::String& t_FromPresetID)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(PULSEEFFECT_INTERFACE_NAME);
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
		msgArg[0].Set("s", t_PulseEffectID);

		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(PULSEEFFECT_INTERFACE_NAME, "GetPulseEffect", msgArg, 1, responseMessage))
			CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

			char* tempRetrievedPulseEffectID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedPulseEffectID))
			t_RetrievedPulseEffectID = qcc::String(tempRetrievedPulseEffectID);

		size_t tempToLampStateSize;
		ajn::MsgArg* tempToLampState;
		CHECK_RETURN(responseMessage->GetArg(2)->Get("a{sv}", &tempToLampStateSize, &tempToLampState))

		t_ToLampState.clear();
		for (size_t i = 0; i < tempToLampStateSize; ++i)
		{
			t_ToLampState.push_back(tempToLampState[i]);
		}
		
		CHECK_RETURN(responseMessage->GetArg(3)->Get("u", &t_PulsePeriod))
		CHECK_RETURN(responseMessage->GetArg(4)->Get("u", &t_PulseDuration))
		CHECK_RETURN(responseMessage->GetArg(5)->Get("u", &t_NumPulses))

		size_t tempFromLampStateSize;
		ajn::MsgArg* tempFromLampState;
		CHECK_RETURN(responseMessage->GetArg(6)->Get("a{sv}", &tempFromLampStateSize, &tempFromLampState))

		t_FromLampState.clear();
		for (size_t i = 0; i < tempFromLampStateSize; ++i)
		{
			t_FromLampState.push_back(tempFromLampState[i]);
		}

		char* tempToPresetID;
		CHECK_RETURN(responseMessage->GetArg(7)->Get("s", &tempToPresetID))
		t_ToPresetID = qcc::String(tempToPresetID);

		char* tempFromPresetID;
		CHECK_RETURN(responseMessage->GetArg(8)->Get("s", &tempFromPresetID))
		t_FromPresetID = qcc::String(tempFromPresetID);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServicePulseEffectBusObject::GetPulseEffectName(const char* t_PulseEffectID, const char* t_Language, uint32_t& t_ResponseCode, qcc::String& t_RetrievedPulseEffectID, qcc::String& t_RetrievedLanguage, qcc::String& t_PulseEffectName)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(PULSEEFFECT_INTERFACE_NAME);
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
		msgArg[0].Set("s", t_PulseEffectID);
		msgArg[1].Set("s", t_Language);

		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(PULSEEFFECT_INTERFACE_NAME, "GetPulseEffectName", msgArg, 2, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedPulseEffectID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedPulseEffectID))
		t_RetrievedPulseEffectID = qcc::String(tempRetrievedPulseEffectID);

		char* tempRetrievedLanguage;
		CHECK_RETURN(responseMessage->GetArg(2)->Get("s", &tempRetrievedLanguage))
		t_RetrievedLanguage = qcc::String(tempRetrievedLanguage);

		char* tempPulseEffectName;
		CHECK_RETURN(responseMessage->GetArg(3)->Get("s", &tempPulseEffectName))
		t_PulseEffectName = qcc::String(tempPulseEffectName);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServicePulseEffectBusObject::SetPulseEffectName(const char* t_PulseEffectID, const char* t_PulseEffectName, const char* t_Language, uint32_t& t_ResponseCode, qcc::String& t_RetrievedPulseEffectID, qcc::String& t_RetrievedLanguage)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(PULSEEFFECT_INTERFACE_NAME);
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
		msgArg[0].Set("s", t_PulseEffectID);
		msgArg[1].Set("s", t_PulseEffectName);
		msgArg[2].Set("s", t_Language);

		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(PULSEEFFECT_INTERFACE_NAME, "SetPulseEffectName", msgArg, 3, responseMessage))
			CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

			char* tempRetrievedPulseEffectID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedPulseEffectID))
			t_RetrievedPulseEffectID = qcc::String(tempRetrievedPulseEffectID);

		char* tempRetrievedLanguage;
		CHECK_RETURN(responseMessage->GetArg(2)->Get("s", &tempRetrievedLanguage))
			t_RetrievedLanguage = qcc::String(tempRetrievedLanguage);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServicePulseEffectBusObject::UpdatePulseEffect(const char* t_PulseEffectID, ajn::MsgArg* t_ToLampState, const uint32_t t_PulsePeriod, const uint32_t t_PulseDuration, const uint32_t t_NumPulses, ajn::MsgArg* t_FromLampState, const char* t_ToPresetID, const char* t_FromPresetID, uint32_t& t_ResponseCode, qcc::String& t_RetrievedPulseEffectID)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(PULSEEFFECT_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), CONTROLLERSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	status = proxyBusObj->AddInterface(*p_InterfaceDescription);
	if (status == ER_OK) {
		ajn::MsgArg* msgArg = new ajn::MsgArg[8];
		msgArg[0].Set("s", t_PulseEffectID);
		msgArg[1].Set("a{sv}", 5, t_ToLampState);
		msgArg[2].Set("u", t_PulsePeriod);
		msgArg[3].Set("u", t_PulseDuration);
		msgArg[4].Set("u", t_NumPulses);
		msgArg[5].Set("a{sv}", 5, t_FromLampState);
		msgArg[6].Set("s", t_ToPresetID);
		msgArg[7].Set("s", t_FromPresetID);

		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(PULSEEFFECT_INTERFACE_NAME, "UpdatePulseEffect", msgArg, 8, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedPulseEffectID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedPulseEffectID))
		t_RetrievedPulseEffectID = qcc::String(tempRetrievedPulseEffectID);
	}
	delete proxyBusObj;
	return status;
}