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
#include "ControllerServiceSceneBusObject.h"

#define CHECK_BREAK(x) if ((status = x) != ER_OK) { break; }
#define CHECK_RETURN(x) if ((status = x) != ER_OK) { return status; }

static const char* CONTROLLERSERVICE_OBJECT_PATH = "/org/allseen/LSF/ControllerService";
static const char* SCENE_INTERFACE_NAME = "org.allseen.LSF.ControllerService.Scene";

ControllerServiceSceneBusObject::ControllerServiceSceneBusObject(ajn::BusAttachment& t_BusAttachment, const std::string& t_BusName, const ajn::SessionId t_SessionId) :
m_BusAttachment(&t_BusAttachment), m_BusName(t_BusName), m_SessionId(t_SessionId),
m_ScenesNameChangedSignalReceived(false),
m_ScenesCreatedSignalReceived(false), m_ScenesUpdatedSignalReceived(false),
m_ScenesDeletedSignalReceived(false), m_ScenesAppliedSignalReceived(false)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* getIface = NULL;
	getIface = m_BusAttachment->GetInterface(SCENE_INTERFACE_NAME);
	if (!getIface) {
		ajn::InterfaceDescription* createIface = NULL;
		status = m_BusAttachment->CreateInterface(SCENE_INTERFACE_NAME, createIface, ajn::AJ_IFC_SECURITY_OFF);
		if (createIface && status == ER_OK) {
			do {
				CHECK_BREAK(createIface->AddMethod("GetAllSceneIDs", NULL, "uas", "responseCode,sceneIDs"))
				CHECK_BREAK(createIface->AddMethod("GetSceneName", "ss", "usss", "sceneID,language,responseCode,sceneID,language,sceneName"))
				CHECK_BREAK(createIface->AddMethod("SetSceneName", "sss", "uss", "sceneID,sceneName,language,responseCode,sceneID,language"))
				CHECK_BREAK(createIface->AddMethod("CreateScene", "a(asasa{sv}u)a(asassu)a(asasa{sv}a{sv}uuu)a(asasssuuu)ss", "us", "transitionlampsLampGroupsToState,transitionlampsLampGroupsToPreset,pulselampsLampGroupsWithState,pulselampsLampGroupsWithPreset,sceneName,language,responseCode,sceneID"))
				CHECK_BREAK(createIface->AddMethod("UpdateScene", "sa(asasa{sv}u)a(asassu)a(asasa{sv}a{sv}uuu)a(asasssuuu)", "us", "sceneID,transitionlampsLampGroupsToState,transitionlampsLampGroupsToPreset,pulselampsLampGroupsWithState,pulselampsGroupsWithPreset,lampState,responseCode,sceneID"))
				CHECK_BREAK(createIface->AddMethod("DeleteScene", "s", "us", "sceneID,responseCode,sceneID"))
				CHECK_BREAK(createIface->AddMethod("GetScene", "s", "usa(asasa{sv}u)a(asassu)a(asasa{sv}a{sv}uuu)a(asasssuuu)", "sceneID,responseCode,sceneID,lampState,transitionlampsLampGroupsToState,transitionlampsLampGroupsToPreset,pulselampsLampGroupsWithState,pulselampsGroupsWithPreset"))
				CHECK_BREAK(createIface->AddMethod("ApplyScene", "s", "us", "sceneID,responseCode,sceneID"))

				CHECK_BREAK(createIface->AddProperty("Version", "u", ajn::PROP_ACCESS_READ))

				CHECK_BREAK(createIface->AddSignal("ScenesNameChanged", "as", "sceneIDs", 0))
				CHECK_BREAK(createIface->AddSignal("ScenesCreated", "as", "sceneIDs", 0))
				CHECK_BREAK(createIface->AddSignal("ScenesUpdated", "as", "sceneIDs", 0))
				CHECK_BREAK(createIface->AddSignal("ScenesDeleted", "as", "sceneIDs", 0))
				CHECK_BREAK(createIface->AddSignal("ScenesApplied", "as", "sceneIDs", 0))
				createIface->Activate();

				const ajn::InterfaceDescription* uniqueId = m_BusAttachment->GetInterface(SCENE_INTERFACE_NAME);
				const ajn::InterfaceDescription::Member* sig = uniqueId->GetMember("ScenesNameChanged");
				m_BusAttachment->RegisterSignalHandler(this, static_cast<ajn::MessageReceiver::SignalHandler>(&ControllerServiceSceneBusObject::ScenesNameChangedSignalHandler), sig, CONTROLLERSERVICE_OBJECT_PATH);

				sig = uniqueId->GetMember("ScenesCreated");
				m_BusAttachment->RegisterSignalHandler(this, static_cast<ajn::MessageReceiver::SignalHandler>(&ControllerServiceSceneBusObject::ScenesCreatedSignalHandler), sig, CONTROLLERSERVICE_OBJECT_PATH);

				sig = uniqueId->GetMember("ScenesUpdated");
				m_BusAttachment->RegisterSignalHandler(this, static_cast<ajn::MessageReceiver::SignalHandler>(&ControllerServiceSceneBusObject::ScenesUpdatedSignalHandler), sig, CONTROLLERSERVICE_OBJECT_PATH);

				sig = uniqueId->GetMember("ScenesDeleted");
				m_BusAttachment->RegisterSignalHandler(this, static_cast<ajn::MessageReceiver::SignalHandler>(&ControllerServiceSceneBusObject::ScenesDeletedSignalHandler), sig, CONTROLLERSERVICE_OBJECT_PATH);

				sig = uniqueId->GetMember("ScenesApplied");
				m_BusAttachment->RegisterSignalHandler(this, static_cast<ajn::MessageReceiver::SignalHandler>(&ControllerServiceSceneBusObject::ScenesAppliedSignalHandler), sig, CONTROLLERSERVICE_OBJECT_PATH);

				return;
			} while (0);
		} //if (createIface)
	} //if (!getIface)
}

void ControllerServiceSceneBusObject::ScenesNameChangedSignalHandler(const ajn::InterfaceDescription::Member* member, const char* sourcePath, ajn::Message& msg)
{
	LOG(INFO) << "ScenesNameChanged signal received";
	m_ScenesNameChangedSignalReceived = true;
}

bool ControllerServiceSceneBusObject::DidScenesNameChanged()
{
	return m_ScenesNameChangedSignalReceived;
}

void ControllerServiceSceneBusObject::ScenesCreatedSignalHandler(const ajn::InterfaceDescription::Member* member, const char* sourcePath, ajn::Message& msg)
{
	LOG(INFO) << "ScenesCreated signal received";
	m_ScenesCreatedSignalReceived = true;
}

bool ControllerServiceSceneBusObject::DidScenesCreated()
{
	return m_ScenesCreatedSignalReceived;
}

void ControllerServiceSceneBusObject::ScenesUpdatedSignalHandler(const ajn::InterfaceDescription::Member* member, const char* sourcePath, ajn::Message& msg)
{
	LOG(INFO) << "ScenesUpdated signal received";
	m_ScenesUpdatedSignalReceived = true;
}

bool ControllerServiceSceneBusObject::DidScenesUpdated()
{
	return m_ScenesUpdatedSignalReceived;
}

void ControllerServiceSceneBusObject::ScenesDeletedSignalHandler(const ajn::InterfaceDescription::Member* member, const char* sourcePath, ajn::Message& msg)
{
	LOG(INFO) << "ScenesDeleted signal received";
	m_ScenesDeletedSignalReceived = true;
}

bool ControllerServiceSceneBusObject::DidScenesDeleted()
{
	return m_ScenesDeletedSignalReceived;
}

void ControllerServiceSceneBusObject::ScenesAppliedSignalHandler(const ajn::InterfaceDescription::Member* member, const char* sourcePath, ajn::Message& msg)
{
	LOG(INFO) << "ScenesApplied signal received";
	m_ScenesAppliedSignalReceived = true;
}

bool ControllerServiceSceneBusObject::DidScenesApplied()
{
	return m_ScenesAppliedSignalReceived;
}

QStatus ControllerServiceSceneBusObject::GetVersion(uint32_t& version)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(SCENE_INTERFACE_NAME);
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
		CHECK_BREAK(proxyBusObj->GetProperty(SCENE_INTERFACE_NAME, "Version", arg))
		version = arg.v_variant.val->v_uint32;
	} while (0);
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceSceneBusObject::GetAllSceneIDs(uint32_t& t_ResponseCode,
	std::vector<qcc::String>& t_SceneIDs)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(SCENE_INTERFACE_NAME);
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
		CHECK_RETURN(proxyBusObj->MethodCall(SCENE_INTERFACE_NAME, "GetAllSceneIDs", NULL, 0, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		uint32_t* tempIDsArraySize = new uint32_t();
		ajn::MsgArg* tempIDsArray;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("as", tempIDsArraySize, &tempIDsArray))

		t_SceneIDs.clear();
		for (size_t i = 0; i < *tempIDsArraySize; ++i)
		{
			t_SceneIDs.push_back(tempIDsArray[i].v_string.str);
		}
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceSceneBusObject::GetSceneName(const char* t_SceneID, const char* t_Language, uint32_t& t_ResponseCode, qcc::String& t_RetrievedSceneID, qcc::String& t_RetrievedLanguage, qcc::String& t_SceneName)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(SCENE_INTERFACE_NAME);
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
		msgArg[0].Set("s", t_SceneID);
		msgArg[1].Set("s", t_Language);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(SCENE_INTERFACE_NAME, "GetSceneName", msgArg, 2, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedSceneID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedSceneID))
		t_RetrievedSceneID = qcc::String(tempRetrievedSceneID);

		char* tempRetrievedLanguage;
		CHECK_RETURN(responseMessage->GetArg(2)->Get("s", &tempRetrievedLanguage))
		t_RetrievedLanguage = qcc::String(tempRetrievedLanguage);

		char* tempSceneName;
		CHECK_RETURN(responseMessage->GetArg(3)->Get("s", &tempSceneName))
		t_SceneName = qcc::String(tempSceneName);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceSceneBusObject::SetSceneName(const char* t_SceneID, const char* t_SceneName, const char* t_Language, uint32_t& t_ResponseCode, qcc::String& t_RetrievedSceneID, qcc::String& t_RetrievedLanguage)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(SCENE_INTERFACE_NAME);
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
		msgArg[0].Set("s", t_SceneID);
		msgArg[1].Set("s", t_SceneName);
		msgArg[2].Set("s", t_Language);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(SCENE_INTERFACE_NAME, "SetSceneName", msgArg, 3, responseMessage))
			CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

			char* tempRetrievedSceneID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedSceneID))
			t_RetrievedSceneID = qcc::String(tempRetrievedSceneID);

		char* tempRetrievedLanguage;
		CHECK_RETURN(responseMessage->GetArg(2)->Get("s", &tempRetrievedLanguage))
			t_RetrievedLanguage = qcc::String(tempRetrievedLanguage);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceSceneBusObject::CreateScene(const std::vector<ajn::MsgArg>& t_TransitionlampsLampGroupsToState, const std::vector<ajn::MsgArg>& t_TransitionlampsLampGroupsToPreset, const std::vector<ajn::MsgArg>& t_PulselampsLampGroupsWithState, const std::vector<ajn::MsgArg>& t_PulselampsLampGroupsWithPreset, const char* t_SceneName, const char* t_Language, uint32_t& t_ResponseCode, qcc::String& t_RetrievedSceneID)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(SCENE_INTERFACE_NAME);
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
		msgArg[0].Set("a(asasa{sv}u)", t_TransitionlampsLampGroupsToState.size(), t_TransitionlampsLampGroupsToState.data());
		msgArg[1].Set("a(asassu)", t_TransitionlampsLampGroupsToPreset.size(), t_TransitionlampsLampGroupsToPreset.data());
		msgArg[2].Set("a(asasa{sv}a{sv}uuu)", t_PulselampsLampGroupsWithState.size(), t_PulselampsLampGroupsWithState.data());
		msgArg[3].Set("a(asasssuuu)", t_PulselampsLampGroupsWithPreset.size(), t_PulselampsLampGroupsWithPreset.data());
		msgArg[4].Set("s", t_SceneName);
		msgArg[5].Set("s", t_Language);
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(SCENE_INTERFACE_NAME, "CreateScene", msgArg, 6, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedSceneID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedSceneID))
		t_RetrievedSceneID = qcc::String(tempRetrievedSceneID);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceSceneBusObject::UpdateScene(const char* t_SceneID, const std::vector<ajn::MsgArg>& t_TransitionlampsLampGroupsToState, const std::vector<ajn::MsgArg>& t_TransitionlampsLampGroupsToPreset, const std::vector<ajn::MsgArg>& t_PulselampsLampGroupsWithState, const std::vector<ajn::MsgArg>& t_PulselampsLampGroupsWithPreset, uint32_t& t_ResponseCode, qcc::String& t_RetrievedSceneID)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(SCENE_INTERFACE_NAME);
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
		msgArg[0].Set("s", t_SceneID);
		msgArg[1].Set("a(asasa{sv}u)", t_TransitionlampsLampGroupsToState.size(), t_TransitionlampsLampGroupsToState.data());
		msgArg[2].Set("a(asassu)", t_TransitionlampsLampGroupsToPreset.size(), t_TransitionlampsLampGroupsToPreset.data());
		msgArg[3].Set("a(asasa{sv}a{sv}uuu)", t_PulselampsLampGroupsWithState.size(), t_PulselampsLampGroupsWithState.data());
		msgArg[4].Set("a(asasssuuu)", t_PulselampsLampGroupsWithPreset.size(), t_PulselampsLampGroupsWithPreset.data());
		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(SCENE_INTERFACE_NAME, "UpdateScene", msgArg, 5, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedSceneID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedSceneID))
		t_RetrievedSceneID = qcc::String(tempRetrievedSceneID);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceSceneBusObject::DeleteScene(const char* t_SceneID, uint32_t& t_ResponseCode, qcc::String& t_RetrievedSceneID)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(SCENE_INTERFACE_NAME);
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
		msgArg[0].Set("s", t_SceneID);

		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(SCENE_INTERFACE_NAME, "DeleteScene", msgArg, 1, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedSceneID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedSceneID))
		t_RetrievedSceneID = qcc::String(tempRetrievedSceneID);
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceSceneBusObject::GetScene(const char* t_SceneID, uint32_t& t_ResponseCode, qcc::String& t_RetrievedSceneID, std::vector<ajn::MsgArg>& t_TransitionlampsLampGroupsToState, std::vector<ajn::MsgArg>& t_TransitionlampsLampGroupsToPreset, std::vector<ajn::MsgArg>& t_PulselampsLampGroupsWithState, std::vector<ajn::MsgArg>& t_PulselampsLampGroupsWithPreset)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(SCENE_INTERFACE_NAME);
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
		msgArg[0].Set("s", t_SceneID);

		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(SCENE_INTERFACE_NAME, "GetScene", msgArg, 1, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedSceneID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedSceneID))
		t_RetrievedSceneID = qcc::String(tempRetrievedSceneID);

		size_t tempTransitionToStateSize;
		ajn::MsgArg* tempTransitionToState;
		CHECK_RETURN(responseMessage->GetArg(2)->Get("a(asasa{sv}u)", &tempTransitionToStateSize, &tempTransitionToState))

		t_TransitionlampsLampGroupsToState.clear();
		for (size_t i = 0; i < tempTransitionToStateSize; ++i)
		{
			t_TransitionlampsLampGroupsToState.push_back(tempTransitionToState[i]);
		}

		size_t tempTransitionToPresetSize;
		ajn::MsgArg* tempTransitionToPreset;
		CHECK_RETURN(responseMessage->GetArg(3)->Get("a(asassu)", &tempTransitionToPresetSize, &tempTransitionToPreset))

		t_TransitionlampsLampGroupsToPreset.clear();
		for (size_t i = 0; i < tempTransitionToPresetSize; ++i)
		{
			t_TransitionlampsLampGroupsToPreset.push_back(tempTransitionToPreset[i]);
		}

		size_t tempPulseWithStateSize;
		ajn::MsgArg* tempPulseWithState;
		CHECK_RETURN(responseMessage->GetArg(4)->Get("a(asasa{sv}a{sv}uuu)", &tempPulseWithStateSize, &tempPulseWithState))

		t_PulselampsLampGroupsWithState.clear();
		for (size_t i = 0; i < tempPulseWithStateSize; ++i)
		{
			t_PulselampsLampGroupsWithState.push_back(tempPulseWithState[i]);
		}

		size_t tempPulseWithPresetSize;
		ajn::MsgArg* tempPulseWithPreset;
		CHECK_RETURN(responseMessage->GetArg(5)->Get("a(asasssuuu)", &tempPulseWithPresetSize, &tempPulseWithPreset))

		t_PulselampsLampGroupsWithPreset.clear();
		for (size_t i = 0; i < tempPulseWithPresetSize; ++i)
		{
			t_PulselampsLampGroupsWithPreset.push_back(tempPulseWithPreset[i]);
		}
	}
	delete proxyBusObj;
	return status;
}

QStatus ControllerServiceSceneBusObject::ApplyScene(const char* t_SceneID, uint32_t& t_ResponseCode, qcc::String& t_RetrievedSceneID)
{
	QStatus status = ER_OK;

	const ajn::InterfaceDescription* p_InterfaceDescription = m_BusAttachment->GetInterface(SCENE_INTERFACE_NAME);
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
		msgArg[0].Set("s", t_SceneID);

		ajn::Message responseMessage(*m_BusAttachment);
		CHECK_RETURN(proxyBusObj->MethodCall(SCENE_INTERFACE_NAME, "ApplyScene", msgArg, 1, responseMessage))
		CHECK_RETURN(responseMessage->GetArg(0)->Get("u", &t_ResponseCode))

		char* tempRetrievedSceneID;
		CHECK_RETURN(responseMessage->GetArg(1)->Get("s", &tempRetrievedSceneID))
		t_RetrievedSceneID = qcc::String(tempRetrievedSceneID);
	}
	delete proxyBusObj;
	return status;
}