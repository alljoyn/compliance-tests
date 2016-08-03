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
#include "ControllerServiceMasterSceneBusObject.h"

#define CHECK_BREAK(x) if ((status = x) != ER_OK) { break; }
#define CHECK_RETURN(x) if ((status = x) != ER_OK) { return status; }

static const char* CONTROLLERSERVICE_OBJECT_PATH = "/org/allseen/LSF/ControllerService";
static const char* MASTERSCENE_INTERFACE_NAME = "org.allseen.LSF.ControllerService.MasterScene";

ControllerServiceMasterSceneBusObject::ControllerServiceMasterSceneBusObject(ajn::BusAttachment& t_BusAttachment, const std::string& t_BusName, const ajn::SessionId t_SessionId) :
m_BusAttachment(&t_BusAttachment), m_BusName(t_BusName), m_SessionId(t_SessionId),
m_MasterScenesNameChangedSignalReceived(false),
m_MasterScenesCreatedSignalReceived(false), m_MasterScenesUpdatedSignalReceived(false),
m_MasterScenesDeletedSignalReceived(false), m_MasterScenesAppliedSignalReceived(false)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* getIface = NULL;
	getIface = m_BusAttachment->GetInterface(MASTERSCENE_INTERFACE_NAME);
	if (!getIface) {
		ajn::InterfaceDescription* createIface = NULL;
		status = m_BusAttachment->CreateInterface(MASTERSCENE_INTERFACE_NAME, createIface, ajn::AJ_IFC_SECURITY_OFF);
		if (createIface && status == ER_OK) {
			do {
				CHECK_BREAK(createIface->AddMethod("GetAllMasterSceneIDs", NULL, "uas", "responseCode,masterSceneIDs"))
					CHECK_BREAK(createIface->AddMethod("GetMasterSceneName", "ss", "usss", "masterSceneID,language,responseCode,masterSceneID,language,masterSceneName"))
					CHECK_BREAK(createIface->AddMethod("SetMasterSceneName", "sss", "uss", "masterSceneID,masterSceneName,language,responseCode,masterSceneID,language"))
					CHECK_BREAK(createIface->AddMethod("CreateMasterScene", "asss", "us", "scenes,masterSceneName,language,responseCode,sceneID"))
					CHECK_BREAK(createIface->AddMethod("UpdateMasterScene", "sas", "us", "masterSceneID,scenes,responseCode,masterSceneID"))
					CHECK_BREAK(createIface->AddMethod("DeleteMasterScene", "s", "us", "masterSceneID,responseCode,masterSceneID"))
					CHECK_BREAK(createIface->AddMethod("GetMasterScene", "s", "usas", "sceneID,responseCode,masterSceneID,scenes"))
					CHECK_BREAK(createIface->AddMethod("ApplyMasterScene", "s", "us", "sceneID,responseCode,masterSceneID"))

					CHECK_BREAK(createIface->AddProperty("Version", "u", ajn::PROP_ACCESS_READ))

					CHECK_BREAK(createIface->AddSignal("MasterScenesNameChanged", "as", "masterSceneIDs", 0))
					CHECK_BREAK(createIface->AddSignal("MasterScenesCreated", "as", "masterSceneIDs", 0))
					CHECK_BREAK(createIface->AddSignal("MasterScenesUpdated", "as", "masterSceneIDs", 0))
					CHECK_BREAK(createIface->AddSignal("MasterScenesDeleted", "as", "masterSceneIDs", 0))
					CHECK_BREAK(createIface->AddSignal("MasterScenesApplied", "as", "masterSceneIDs", 0))
					createIface->Activate();

				const ajn::InterfaceDescription* uniqueId = m_BusAttachment->GetInterface(MASTERSCENE_INTERFACE_NAME);
				const ajn::InterfaceDescription::Member* sig = uniqueId->GetMember("MasterScenesNameChanged");
				m_BusAttachment->RegisterSignalHandler(this, static_cast<ajn::MessageReceiver::SignalHandler>(&ControllerServiceMasterSceneBusObject::MasterScenesNameChangedSignalHandler), sig, CONTROLLERSERVICE_OBJECT_PATH);

				sig = uniqueId->GetMember("MasterScenesCreated");
				m_BusAttachment->RegisterSignalHandler(this, static_cast<ajn::MessageReceiver::SignalHandler>(&ControllerServiceMasterSceneBusObject::MasterScenesCreatedSignalHandler), sig, CONTROLLERSERVICE_OBJECT_PATH);

				sig = uniqueId->GetMember("MasterScenesUpdated");
				m_BusAttachment->RegisterSignalHandler(this, static_cast<ajn::MessageReceiver::SignalHandler>(&ControllerServiceMasterSceneBusObject::MasterScenesUpdatedSignalHandler), sig, CONTROLLERSERVICE_OBJECT_PATH);

				sig = uniqueId->GetMember("MasterScenesDeleted");
				m_BusAttachment->RegisterSignalHandler(this, static_cast<ajn::MessageReceiver::SignalHandler>(&ControllerServiceMasterSceneBusObject::MasterScenesDeletedSignalHandler), sig, CONTROLLERSERVICE_OBJECT_PATH);

				sig = uniqueId->GetMember("MasterScenesApplied");
				m_BusAttachment->RegisterSignalHandler(this, static_cast<ajn::MessageReceiver::SignalHandler>(&ControllerServiceMasterSceneBusObject::MasterScenesAppliedSignalHandler), sig, CONTROLLERSERVICE_OBJECT_PATH);

				return;
			} while (0);
		} //if (createIface)
	} //if (!getIface)
}

void ControllerServiceMasterSceneBusObject::MasterScenesNameChangedSignalHandler(const ajn::InterfaceDescription::Member* member, const char* sourcePath, ajn::Message& msg)
{
	LOG(INFO) << "MasterScenesNameChanged signal received";
	m_MasterScenesNameChangedSignalReceived = true;
}

bool ControllerServiceMasterSceneBusObject::DidMasterScenesNameChanged()
{
	return m_MasterScenesNameChangedSignalReceived;
}

void ControllerServiceMasterSceneBusObject::MasterScenesCreatedSignalHandler(const ajn::InterfaceDescription::Member* member, const char* sourcePath, ajn::Message& msg)
{
	LOG(INFO) << "MasterScenesCreated signal received";
	m_MasterScenesCreatedSignalReceived = true;
}

bool ControllerServiceMasterSceneBusObject::DidMasterScenesCreated()
{
	return m_MasterScenesCreatedSignalReceived;
}

void ControllerServiceMasterSceneBusObject::MasterScenesUpdatedSignalHandler(const ajn::InterfaceDescription::Member* member, const char* sourcePath, ajn::Message& msg)
{
	LOG(INFO) << "MasterScenesUpdated signal received";
	m_MasterScenesUpdatedSignalReceived = true;
}

bool ControllerServiceMasterSceneBusObject::DidMasterScenesUpdated()
{
	return m_MasterScenesUpdatedSignalReceived;
}

void ControllerServiceMasterSceneBusObject::MasterScenesDeletedSignalHandler(const ajn::InterfaceDescription::Member* member, const char* sourcePath, ajn::Message& msg)
{
	LOG(INFO) << "MasterScenesDeleted signal received";
	m_MasterScenesDeletedSignalReceived = true;
}

bool ControllerServiceMasterSceneBusObject::DidMasterScenesDeleted()
{
	return m_MasterScenesDeletedSignalReceived;
}

void ControllerServiceMasterSceneBusObject::MasterScenesAppliedSignalHandler(const ajn::InterfaceDescription::Member* member, const char* sourcePath, ajn::Message& msg)
{
	LOG(INFO) << "MasterScenesApplied signal received";
	m_MasterScenesAppliedSignalReceived = true;
}

bool ControllerServiceMasterSceneBusObject::DidMasterScenesApplied()
{
	return m_MasterScenesAppliedSignalReceived;
}

QStatus ControllerServiceMasterSceneBusObject::GetVersion(uint32_t& version)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(MASTERSCENE_INTERFACE_NAME);
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
		CHECK_BREAK(proxyBusObj->GetProperty(MASTERSCENE_INTERFACE_NAME, "Version", arg))
			version = arg.v_variant.val->v_uint32;
	} while (0);
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceMasterSceneBusObject::GetAllMasterSceneIDs(uint32_t& t_ResponseCode,
	std::vector<qcc::String>& t_MasterSceneIDs)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(MASTERSCENE_INTERFACE_NAME);
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
		CHECK_RETURN(proxyBusObj->MethodCall(MASTERSCENE_INTERFACE_NAME, "GetAllMasterSceneIDs", NULL, 0, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		uint32_t* tempIDsArraySize = new uint32_t();
		char** tempIDsArray;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("as", tempIDsArraySize, &tempIDsArray))

		t_MasterSceneIDs.clear();
		for (size_t i = 0; i < *tempIDsArraySize; ++i)
		{
			t_MasterSceneIDs.push_back(tempIDsArray[i]);
		}
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceMasterSceneBusObject::GetMasterSceneName(const char* t_MasterSceneID, const char* t_Language, uint32_t& t_ResponseCode, qcc::String& t_RetrievedMasterSceneID, qcc::String& t_RetrievedLanguage, qcc::String& t_MasterSceneName)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(MASTERSCENE_INTERFACE_NAME);
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
		msgArg[0].Set("s", t_MasterSceneID);
		msgArg[1].Set("s", t_Language);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(MASTERSCENE_INTERFACE_NAME, "GetMasterSceneName", msgArg, 2, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedSceneID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedSceneID))
		t_RetrievedMasterSceneID = qcc::String(tempRetrievedSceneID);

		char* tempRetrievedLanguage;
		CHECK_RETURN(responseMessage->GetArg(2)->Get("s", &tempRetrievedLanguage))
		t_RetrievedLanguage = qcc::String(tempRetrievedLanguage);

		char* tempSceneName;
		CHECK_RETURN(responseMessage->GetArg(3)->Get("s", &tempSceneName))
		t_MasterSceneName = qcc::String(tempSceneName);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceMasterSceneBusObject::SetMasterSceneName(const char* t_MasterSceneID, const char* t_MasterSceneName, const char* t_Language, uint32_t& t_ResponseCode, qcc::String& t_RetrievedMasterSceneID, qcc::String& t_RetrievedLanguage)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(MASTERSCENE_INTERFACE_NAME);
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
		msgArg[0].Set("s", t_MasterSceneID);
		msgArg[1].Set("s", t_MasterSceneName);
		msgArg[2].Set("s", t_Language);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(MASTERSCENE_INTERFACE_NAME, "SetMasterSceneName", msgArg, 3, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedSceneID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedSceneID))
		t_RetrievedMasterSceneID = qcc::String(tempRetrievedSceneID);

		char* tempRetrievedLanguage;
		CHECK_RETURN(responseMessage->GetArg(2)->Get("s", &tempRetrievedLanguage))
		t_RetrievedLanguage = qcc::String(tempRetrievedLanguage);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceMasterSceneBusObject::CreateMasterScene(const std::vector<const char*>& t_Scenes, const char* t_MasterSceneName, const char* t_Language, uint32_t& t_ResponseCode, qcc::String& t_RetrievedMasterSceneID)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(MASTERSCENE_INTERFACE_NAME);
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
		msgArg[0].Set("as", t_Scenes.size(), t_Scenes.data());
		msgArg[1].Set("s", t_MasterSceneName);
		msgArg[2].Set("s", t_Language);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(MASTERSCENE_INTERFACE_NAME, "CreateMasterScene", msgArg, 3, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedSceneID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedSceneID))
		t_RetrievedMasterSceneID = qcc::String(tempRetrievedSceneID);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceMasterSceneBusObject::UpdateMasterScene(const char* t_MasterSceneID, const std::vector<const char*>& t_Scenes, uint32_t& t_ResponseCode, qcc::String& t_RetrievedMasterSceneID)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(MASTERSCENE_INTERFACE_NAME);
	if (!p_InterfaceDescription) {
		return ER_FAIL;
	}

	ajn::ProxyBusObject* proxyBusObj = new ajn::ProxyBusObject(*m_BusAttachment, m_BusName.c_str(), CONTROLLERSERVICE_OBJECT_PATH, m_SessionId);
	if (!proxyBusObj) {
		return ER_FAIL;
	}
	status = proxyBusObj->AddInterface(*p_InterfaceDescription);
	if (status == ER_OK) {
		ajn::MsgArg* msgArg = new ajn::MsgArg[5];
		msgArg[0].Set("s", t_MasterSceneID);
		msgArg[1].Set("as", t_Scenes.size(), t_Scenes.data());
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(MASTERSCENE_INTERFACE_NAME, "UpdateMasterScene", msgArg, 2, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedSceneID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedSceneID))
		t_RetrievedMasterSceneID = qcc::String(tempRetrievedSceneID);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceMasterSceneBusObject::DeleteMasterScene(const char* t_MasterSceneID, uint32_t& t_ResponseCode, qcc::String& t_RetrievedMasterSceneID)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(MASTERSCENE_INTERFACE_NAME);
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
		msgArg[0].Set("s", t_MasterSceneID);

		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(MASTERSCENE_INTERFACE_NAME, "DeleteMasterScene", msgArg, 1, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedSceneID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedSceneID))
		t_RetrievedMasterSceneID = qcc::String(tempRetrievedSceneID);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceMasterSceneBusObject::GetMasterScene(const char* t_MasterSceneID, uint32_t& t_ResponseCode, qcc::String& t_RetrievedMasterSceneID, std::vector<qcc::String>& t_Scenes)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(MASTERSCENE_INTERFACE_NAME);
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
		msgArg[0].Set("s", t_MasterSceneID);

		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(MASTERSCENE_INTERFACE_NAME, "GetScene", msgArg, 1, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedSceneID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedSceneID))
		t_RetrievedMasterSceneID = qcc::String(tempRetrievedSceneID);

		size_t tempLampStateSize;
		ajn::MsgArg* tempLampState;
		CHECK_RETURN(responseMessage->GetArg(2)->Get("as", &tempLampStateSize, &tempLampState))

		t_Scenes.clear();
		for (size_t i = 0; i < tempLampStateSize; ++i)
		{
			t_Scenes.push_back(qcc::String(tempLampState[i].v_string.str));
		}
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceMasterSceneBusObject::ApplyMasterScene(const char* t_MasterSceneID, uint32_t& t_ResponseCode, qcc::String& t_RetrievedMasterSceneID)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(MASTERSCENE_INTERFACE_NAME);
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
		msgArg[0].Set("s", t_MasterSceneID);

		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(MASTERSCENE_INTERFACE_NAME, "ApplyMasterScene", msgArg, 1, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedSceneID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedSceneID))
		t_RetrievedMasterSceneID = qcc::String(tempRetrievedSceneID);
	}
	delete proxyBusObj;
	return status;
}