CREATE DATABASE allseen;
USE allseen;

CREATE TABLE `users` (
  `username` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `enabled` tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;

CREATE TABLE `user_roles` (
  `user_role_id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `ROLE` varchar(45) NOT NULL,
  PRIMARY KEY (`user_role_id`),
  UNIQUE KEY `uni_username_role` (`ROLE`,`username`),
  KEY `fk_username_idx` (`username`),
  CONSTRAINT `fk_username` FOREIGN KEY (`username`) REFERENCES `users` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;

CREATE TABLE `services` (
  `id_service` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id_service`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;

CREATE TABLE `category` (
  `id_category` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `enabled` tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id_category`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;

CREATE TABLE `certrel` (
  `id_certrel` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(60) NOT NULL,
  `enabled` tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id_certrel`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;

CREATE TABLE `dut` (
  `id_dut` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `user` varchar(45) NOT NULL,
  `created_date` datetime NOT NULL,
  `modified_date` datetime NOT NULL,
  `manufacturer` varchar(60) NOT NULL,
  `model` varchar(60) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_dut`),
  KEY `tested_by` (`user`),
  CONSTRAINT `tested_by` FOREIGN KEY (`user`) REFERENCES `users` (`username`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;

CREATE TABLE `golden` (
  `id_golden` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `user` varchar(45) NOT NULL,
  `created_date` datetime NOT NULL,
  `modified_date` datetime NOT NULL,
  `id_category` bigint(20) NOT NULL,
  `manufacturer` varchar(60) NOT NULL,
  `model` varchar(60) NOT NULL,
  `sw_ver` varchar(60) NOT NULL,
  `hw_ver` varchar(60) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_golden`),
  KEY `gu_tested_by` (`user`),
  KEY `gu_belongs_to` (`id_category`),
  CONSTRAINT `gu_belongs_to` FOREIGN KEY (`id_category`) REFERENCES `category` (`id_category`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `gu_tested_by` FOREIGN KEY (`user`) REFERENCES `users` (`username`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;

CREATE TABLE `ics` (
  `id_ics` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `value` tinyint(1) NOT NULL DEFAULT '0',
  `service_group` bigint(20) NOT NULL,
  `scr_expression` varchar(2014) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_ics`),
  KEY `ics_included_in` (`service_group`),
  CONSTRAINT `ics_included_in` FOREIGN KEY (`service_group`) REFERENCES `services` (`id_service`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;

CREATE TABLE `ixit` (
  `id_ixit` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `value` varchar(255) NOT NULL,
  `service_group` bigint(20) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_ixit`),
  KEY `ixit_included_in` (`service_group`),
  CONSTRAINT `ixit_included_in` FOREIGN KEY (`service_group`) REFERENCES `services` (`id_service`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;

CREATE TABLE `parameters` (
  `id_param` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `value` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_param`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;

CREATE TABLE `project` (
  `id_project` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `user` varchar(45) NOT NULL,
  `created_date` datetime NOT NULL,
  `modified_date` datetime NOT NULL,
  `type` varchar(60) NOT NULL,
  `id_certrel` bigint(20) NOT NULL,
  `id_tccl` bigint(20) DEFAULT NULL,
  `car_id` varchar(60) DEFAULT NULL,
  `is_configured` tinyint(1) NOT NULL DEFAULT '0',
  `configuration` text,
  `id_dut` bigint(20) DEFAULT NULL,
  `has_results` tinyint(1) NOT NULL DEFAULT '0',
  `results` text,
  `has_testreport` bit(1) NOT NULL DEFAULT b'0',
  `test_report` text,
  PRIMARY KEY (`id_project`),
  KEY `created_by` (`user`),
  KEY `tests` (`id_dut`),
  KEY `configured_with` (`id_certrel`),
  CONSTRAINT `configured_with` FOREIGN KEY (`id_certrel`) REFERENCES `certrel` (`id_certrel`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `created_by` FOREIGN KEY (`user`) REFERENCES `users` (`username`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;

CREATE TABLE `sample` (
  `id_sample` bigint(20) NOT NULL AUTO_INCREMENT,
  `device_id` varchar(60) NOT NULL,
  `app_id` varchar(60) NOT NULL,
  `sw_ver` varchar(60) NOT NULL,
  `hw_ver` varchar(60) NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `description` text,
  `associated_dut` bigint(20) NOT NULL,
  PRIMARY KEY (`id_sample`),
  KEY `sample_of` (`associated_dut`),
  CONSTRAINT `sample_of` FOREIGN KEY (`associated_dut`) REFERENCES `dut` (`id_dut`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;

CREATE TABLE `tccl` (
  `id_tccl` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(60) NOT NULL,
  `created_date` datetime NOT NULL,
  `modified_date` datetime NOT NULL,
  `id_certrel` bigint(20) NOT NULL,
  `num_tc` bigint(20) NOT NULL,
  PRIMARY KEY (`id_tccl`),
  KEY `conditions` (`id_certrel`),
  CONSTRAINT `conditions` FOREIGN KEY (`id_certrel`) REFERENCES `certrel` (`id_certrel`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;

CREATE TABLE `testbed` (
  `id_testbed` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `user` varchar(45) NOT NULL,
  `created_date` datetime NOT NULL,
  `modified_date` datetime NOT NULL,
  `list_golden` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_testbed`),
  KEY `tb_tested_by` (`user`),
  CONSTRAINT `tb_tested_by` FOREIGN KEY (`user`) REFERENCES `users` (`username`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;

CREATE TABLE `testcases` (
  `id_test` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `type` varchar(60) NOT NULL,
  `applicability` varchar(1024) NOT NULL,
  `service_group` bigint(20) NOT NULL,
  `last_id_project` bigint(20) DEFAULT NULL,
  `last_execution` datetime DEFAULT NULL,
  `last_result` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_test`),
  KEY `included_in` (`service_group`),
  CONSTRAINT `included_in` FOREIGN KEY (`service_group`) REFERENCES `services` (`id_service`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;

CREATE TABLE `project_golden` (
  `id_project` bigint(20) NOT NULL,
  `id_golden` bigint(20) NOT NULL,
  PRIMARY KEY (`id_project`,`id_golden`),
  KEY `fk_id_goldenp` (`id_golden`),
  CONSTRAINT `fk_id_goldenp` FOREIGN KEY (`id_golden`) REFERENCES `golden` (`id_golden`),
  CONSTRAINT `fk_id_projectg` FOREIGN KEY (`id_project`) REFERENCES `project` (`id_project`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;

CREATE TABLE `project_services` (
  `id_project` bigint(20) NOT NULL,
  `id_service` bigint(20) NOT NULL,
  PRIMARY KEY (`id_project`,`id_service`),
  KEY `fk_id_service` (`id_service`),
  CONSTRAINT `fk_id_project` FOREIGN KEY (`id_project`) REFERENCES `project` (`id_project`),
  CONSTRAINT `fk_id_service` FOREIGN KEY (`id_service`) REFERENCES `services` (`id_service`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;

CREATE TABLE `tccl_testcase` (
  `id_tccl` bigint(20) NOT NULL,
  `type` varchar(1) NOT NULL,
  `enable` tinyint(1) NOT NULL,
  `id_test` bigint(20) NOT NULL,
  PRIMARY KEY (`id_tccl`,`id_test`),
  KEY `fk_id_test` (`id_test`),
  CONSTRAINT `fk_id_tccl` FOREIGN KEY (`id_tccl`) REFERENCES `tccl` (`id_tccl`),
  CONSTRAINT `fk_id_test` FOREIGN KEY (`id_test`) REFERENCES `testcases` (`id_test`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;

CREATE TABLE `testcases_certrel` (
  `id_test` bigint(20) NOT NULL,
  `id_certrel` bigint(20) NOT NULL,
  PRIMARY KEY (`id_test`,`id_certrel`),
  KEY `fk_id_certrel` (`id_certrel`),
  CONSTRAINT `fk_id_certrel` FOREIGN KEY (`id_certrel`) REFERENCES `certrel` (`id_certrel`),
  CONSTRAINT `fk_id_testcr` FOREIGN KEY (`id_test`) REFERENCES `testcases` (`id_test`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;

INSERT INTO category (id_category,name,enabled) VALUES (1,'Category 1 AllJoyn Device (About)',1);
INSERT INTO category (id_category,name,enabled) VALUES (2,'Category 2 AllJoyn Device (Configuration)',1);
INSERT INTO category (id_category,name,enabled) VALUES (3,'Category 3 AllJoyn Device (Onboarding)',1);
INSERT INTO category (id_category,name,enabled) VALUES (4,'Category 4.1 AllJoyn Device (Control Panel Controller)',1);
INSERT INTO category (id_category,name,enabled) VALUES (5,'Category 4.2 AllJoyn Device (Control Panel)',1);
INSERT INTO category (id_category,name,enabled) VALUES (6,'Category 5.1 AllJoyn Device (Notification Consumer)',1);
INSERT INTO category (id_category,name,enabled) VALUES (7,'Category 5.2 AllJoyn Device (Notification Producer)',1);
INSERT INTO category (id_category,name,enabled) VALUES (8,'Category 6.1 AllJoyn Device (Audio Source)',1);
INSERT INTO category (id_category,name,enabled) VALUES (9,'Category 6.2 AllJoyn Device (Audio Sink)',1);
INSERT INTO category (id_category,name,enabled) VALUES (10,'Category 7.1 AllJoyn Device (Lighting Controller)',1);
INSERT INTO category (id_category,name,enabled) VALUES (11,'Category 7.2 AllJoyn Device (Lamp Service)',1);

INSERT INTO certrel (id_certrel,name,enabled) VALUES (1,'v14.06.00a',1);
INSERT INTO certrel (id_certrel,name,enabled) VALUES (2,'v14.12.00',1);

INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (1,'ICSCO_DateOfManufacture',0,1,null,'Support of About Interface GetAboutData method DateOfManufacture data field');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (2,'ICSCO_HardwareVersion',0,1,null,'Support of About Interface GetAboutData method HardwareVersion data field');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (3,'ICSCO_SupportUrl',0,1,null,'Support of About Interface GetAboutData method SupportUrl data field');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (4,'ICSCO_IconInterface',0,1,null,'Support of Icon Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (5,'ICSN_NotificationServiceFramework',0,2,null,'Support of Notification Service Framework');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (6,'ICSN_NotificationInterface',0,2,'((ICSN_NotificationServiceFramework)&(ICSN_NotificationInterface))|((!(ICSN_NotificationServiceFramework))&(!(ICSN_NotificationInterface)))','Support of Notification Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (7,'ICSN_RichIconUrl',0,2,'(!(ICSN_RichIconUrl))|(ICSN_NotificationInterface)','Support of Notification Interface Notify signal richNotificationIconUrl attribute');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (8,'ICSN_RichAudioUrl',0,2,'(!(ICSN_RichAudioUrl))|(ICSN_NotificationInterface)','Support of Notification Interface Notify signal richNotificationAudioUrl attribute');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (9,'ICSN_RespObjectPath',0,2,'(!(ICSN_RespObjectPath))|(ICSN_NotificationInterface)','Support of Notification Interface Notify signal responseObjectPath attribute');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (10,'ICSN_NotificationProducerInterface',0,2,'((!(ICSN_NotificationProducerInterface))&(!(ICSN_NotificationServiceFramework)))|(ICSN_NotificationServiceFramework)','Support of Notification Producer Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (11,'ICSN_DismisserInterface',0,2,'((ICSN_NotificationServiceFramework)&(ICSN_DismisserInterface))|((!(ICSN_NotificationServiceFramework))&(!(ICSN_DismisserInterface)))','Support of Dismisser Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (12,'ICSON_OnboardingServiceFramework',0,3,null,'Support of Onboarding Service Framework');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (13,'ICSON_OnboardingInterface',0,3,'((ICSON_OnboardingServiceFramework)&(ICSON_OnboardingInterface))|((!(ICSON_OnboardingServiceFramework))&(!(ICSON_OnboardingInterface)))','Support of Onboarding Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (14,'ICSON_ChannelSwitching',0,3,'((!(ICSON_ChannelSwitching))&(!(ICSON_OnboardingInterface)))|(ICSON_OnboardingInterface)','Support of Concurrency feature (Channel Switching)');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (15,'ICSON_GetScanInfoMethod',0,3,'((!(ICSON_GetScanInfoMethod))&(!(ICSON_OnboardingInterface)))|(ICSON_OnboardingInterface)','Support of Onboarding Interface GetScanInfo method');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (16,'ICSCP_ControlPanelServiceFramework',0,4,null,'Support of Control Panel Service Framework');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (17,'ICSCP_ControlPanelInterface',0,4,'((ICSCP_ControlPanelServiceFramework)&(ICSCP_ControlPanelInterface))|((!(ICSCP_ControlPanelServiceFramework))&(!(ICSCP_ControlPanelInterface)))','Support of Control Panel Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (18,'ICSCP_ContainerInterface',0,4,'((ICSCP_ControlPanelInterface)&((ICSCP_ContainerInterface)|(ICSCP_SecuredContainerInterface)))|((!(ICSCP_ControlPanelInterface))&(!(ICSCP_ContainerInterface)))','Support of Container Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (19,'ICSCP_SecuredContainerInterface',0,4,'((ICSCP_ControlPanelInterface)&((ICSCP_ContainerInterface)|(ICSCP_SecuredContainerInterface)))|((!(ICSCP_ControlPanelInterface))&(!(ICSCP_SecuredContainerInterface)))','Support of SecuredContainer Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (20,'ICSCP_PropertyInterface',0,4,'(!(ICSCP_PropertyInterface))|((ICSCP_ContainerInterface)|(ICSCP_SecuredContainerInterface))','Support of Property Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (21,'ICSCP_SecuredPropertyInterface',0,4,'(!(ICSCP_SecuredPropertyInterface))|((ICSCP_ContainerInterface)|(ICSCP_SecuredContainerInterface))','Support of SecuredProperty Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (22,'ICSCP_LabelPropertyInterface',0,4,'(!(ICSCP_LabelPropertyInterface))|((ICSCP_ContainerInterface)|(ICSCP_SecuredContainerInterface))','Support of LabelProperty Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (23,'ICSCP_ActionInterface',0,4,'(!(ICSCP_ActionInterface))|((ICSCP_ContainerInterface)|(ICSCP_SecuredContainerInterface))','Support of Action Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (24,'ICSCP_SecuredActionInterface',0,4,'(!(ICSCP_SecuredActionInterface))|((ICSCP_ContainerInterface)|(ICSCP_SecuredContainerInterface))','Support of SecuredAction Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (25,'ICSCP_NotificationActionInterface',0,4,'(!(ICSCP_NotificationActionInterface))|(ICSCP_ControlPanelInterface)','Support of NotificationAction Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (26,'ICSCP_DialogInterface',0,4,'(!(ICSCP_DialogInterface))|((ICSCP_ActionInterface)|(ICSCP_SecuredActionInterface)|(ICSCP_NotificationActionInterface))','Support of Dialog Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (27,'ICSCP_DI_Action2',0,4,'(!(ICSCP_DI_Action2))|(ICSCP_DialogInterface)','Support of Dialog interface Action2 method');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (28,'ICSCP_DI_Action3',0,4,'(!(ICSCP_DI_Action3))|((ICSCP_DialogInterface)&(ICSCP_DI_Action2))','Support of Dialog interface Action3 method');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (29,'ICSCP_SecuredDialogInterface',0,4,'(!(ICSCP_SecuredDialogInterface))|((ICSCP_ActionInterface)|(ICSCP_SecuredActionInterface)|(ICSCP_NotificationActionInterface))','Support of SecuredDialog interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (30,'ICSCP_SDI_Action2',0,4,'(!(ICSCP_SDI_Action2))|(ICSCP_SecuredDialogInterface)','Support of SecuredDialog interface Action2 method');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (31,'ICSCP_SDI_Action3',0,4,'(!(ICSCP_SDI_Action3))|((ICSCP_SecuredDialogInterface)&(ICSCP_SDI_Action2))','Support of SecuredDialog interface Action3 method');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (32,'ICSCP_ListPropertyInterface',0,4,'(!(ICSCP_ListPropertyInterface))|((ICSCP_ContainerInterface)|(ICSCP_SecuredContainerInterface))','Support of ListProperty interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (33,'ICSCP_SecuredListPropertyInterface',0,4,'(!(ICSCP_SecuredListPropertyInterface))|((ICSCP_ContainerInterface)|(ICSCP_SecuredContainerInterface))','Support of SecuredListProperty interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (34,'ICSCP_HTTPControlInterface',0,4,'(!(ICSCP_HTTPControlInterface))|(ICSCP_ControlPanelInterface)','Support of HTTPControl interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (35,'ICSCF_ConfigurationServiceFramework',0,5,null,'Support of Configuration Service Framework');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (36,'ICSCF_ConfigurationInterface',0,5,'((ICSCF_ConfigurationServiceFramework)&(ICSCF_ConfigurationInterface))|((!(ICSCF_ConfigurationServiceFramework))&(!(ICSCF_ConfigurationInterface)))','Support of Configuration Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (37,'ICSCF_FactoryResetMethod',0,5,'(!(ICSCF_FactoryResetMethod))|(ICSCF_ConfigurationInterface)','Support of Configuration Interface FactoryReset method');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (38,'ICSL_LightingServiceFramework',0,6,null,'Support of Lighting Service Framework');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (39,'ICSL_LampServiceInterface',0,6,'((ICSL_LightingServiceFramework)&(ICSL_LampServiceInterface))|((!(ICSL_LightingServiceFramework))&(!(ICSL_LampServiceInterface)))','Support of LampService Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (40,'ICSL_LampParametersInterface',0,6,'((ICSL_LightingServiceFramework)&(ICSL_LampParametersInterface))|((!(ICSL_LightingServiceFramework))&(!(ICSL_LampParametersInterface)))','Support of LamParameters Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (41,'ICSL_LampDetailsInterface',0,6,'((ICSL_LightingServiceFramework)&(ICSL_LampDetailsInterface))|((!(ICSL_LightingServiceFramework))&(!(ICSL_LampDetailsInterface)))','Support of LampDetails Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (42,'ICSL_Dimmable',0,6,'(!(ICSL_Dimmable))|(ICSL_LampDetailsInterface)','Support of light dimming');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (43,'ICSL_Color',0,6,'(!(ICSL_Color))|(ICSL_LampDetailsInterface)','Support of variable color');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (44,'ICSL_ColorTemperature',0,6,'(!(ICSL_ColorTemperature))|(ICSL_LampDetailsInterface)','Support of variable color temperature');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (45,'ICSL_Effects',0,6,'(!(ICSL_Effects))|(ICSL_LampDetailsInterface)','Support of effects');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (46,'ICSL_LampStateInterface',0,6,'((ICSL_LightingServiceFramework)&(ICSL_LampStateInterface))|((!(ICSL_LightingServiceFramework))&(!(ICSL_LampStateInterface)))','Support of LampState Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (47,'ICSAU_AudioServiceFramework',0,7,null,'Support of Audio Service Framework');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (48,'ICSAU_StreamInterface',0,7,'((ICSAU_AudioServiceFramework)&(ICSAU_StreamInterface))|((!(ICSAU_AudioServiceFramework))&(!(ICSAU_StreamInterface)))','Support of Stream Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (49,'ICSAU_StreamPortInterface',0,7,'((ICSAU_StreamInterface)&(ICSAU_StreamPortInterface))|((!(ICSAU_StreamInterface))&(!(ICSAU_StreamPortInterface)))','Support of Stream.Port Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (50,'ICSAU_PortAudioSinkInterface',0,7,'((ICSAU_StreamPortInterface)&(!(ICSAU_PortAudioSourceInterface))&(ICSAU_PortAudioSinkInterface))|((ICSAU_StreamPortInterface)&(ICSAU_PortAudioSourceInterface)&(!(ICSAU_PortAudioSinkInterface)))|((!(ICSAU_StreamPortInterface))&(!(ICSAU_PortAudioSinkInterface)))','Support of Stream.Por.AudioSink Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (51,'ICSAU_PortAudioSourceInterface',0,7,'((ICSAU_StreamPortInterface)&(!(ICSAU_PortAudioSinkInterface))&(ICSAU_PortAudioSourceInterface))|((ICSAU_StreamPortInterface)&(ICSAU_PortAudioSinkInterface)&(!(ICSAU_PortAudioSourceInterface)))|((!(ICSAU_StreamPortInterface))&(!(ICSAU_PortAudioSourceInterface)))','Support of Stream.Port.AudioSource Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (52,'ICSAU_PortImageSinkInterface',0,7,'(!(ICSAU_PortImageSinkInterface))|(ICSAU_PortAudioSinkInterface)','Support of Stream.Port.ImageSink Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (53,'ICSAU_PortImageSourceInterface',0,7,'(!(ICSAU_PortImageSourceInterface))|(ICSAU_PortAudioSourceInterface)','Support of Stream.Port.ImageSource Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (54,'ICSAU_PortApplicationMetadataSinkInterface',0,7,'(!(ICSAU_PortApplicationMetadataSinkInterface))|(ICSAU_PortAudioSinkInterface)','Support of Stream.Port.Application.MetadataSink Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (55,'ICSAU_PortApplicationMetadataSourceInterface',0,7,'(!(ICSAU_PortApplicationMetadataSourceInterface))|(ICSAU_PortAudioSourceInterface)','Support of Stream.Port.Application.MetadataSource Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (56,'ICSAU_ControlVolumeInterface',0,7,'(!(ICSAU_ControlVolumeInterface))|(ICSAU_PortAudioSinkInterface)','Support of Control.Volume Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (57,'ICSAU_StreamClockInterface',0,7,'(!(ICSAU_StreamClockInterface))|(ICSAU_PortAudioSinkInterface)','Support of Stream.Clock Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (58,'ICSAU_AudioXalac',0,7,'(!(ICSAU_AudioXalac))|((ICSAU_PortAudioSinkInterface)|(ICSAU_PortAudioSourceInterface))','Support of audio/x-alac media type');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (59,'ICSAU_ImageJpeg',0,7,'(!(ICSAU_ImageJpeg))|((ICSAU_PortImageSinkInterface)|(ICSAU_PortImageSourceInterface))','Support of image/jpeg media type');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (60,'ICSAU_ApplicationXmetadata',0,7,'(!(ICSAU_ApplicationXmetadata))|((ICSAU_PortApplicationMetadataSinkInterface)|(ICSAU_PortApplicationMetadataSourceInterface))','Support of application/x-metadata media type');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (61,'ICSAU_VolumeControlEnabled',0,7,'(!(ICSAU_VolumeControlEnabled))|(ICSAU_ControlVolumeInterface)','Support of Volume Control');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (62,'ICSG_GatewayServiceFramework',0,8,null,'Support of Gateway Service Framework');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (63,'ICSG_ProfileManagementInterface',0,8,'((ICSG_GatewayServiceFramework)&(ICSG_ProfileManagementInterface))|((!(ICSG_GatewayServiceFramework))&(!(ICSG_ProfileManagementInterface)))','Support of Profile Management Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (64,'ICSG_AppAccessInterface',0,8,'((ICSG_GatewayServiceFramework)&(ICSG_AppAccessInterface))|((!(ICSG_GatewayServiceFramework))&(!(ICSG_AppAccessInterface)))','Support of App Access Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (65,'ICSG_AppManagementInterface',0,8,'((ICSG_GatewayServiceFramework)&(ICSG_AppManagementInterface))|((!(ICSG_GatewayServiceFramework))&(!(ICSG_AppManagementInterface)))','Support of App Management Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (66,'ICSSH_SmartHomeServiceFramework',0,9,null,'Support of Smart Home Service Framework');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (67,'ICSSH_CentralizedManagementInterface',0,9,'((ICSSH_SmartHomeServiceFramework)&(ICSSH_CentralizedManagementInterface))|((!(ICSSH_SmartHomeServiceFramework))&(!(ICSSH_CentralizedManagementInterface)))','Support of Centralized Management Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (68,'ICST_TimeServiceFramework',0,10,null,'Support of Time Service Framework');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (69,'ICST_ClockInterface',0,10,'(!(ICST_ClockInterface))|(ICST_TimeServiceFramework)','Support of Clock Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (70,'ICST_Date',0,10,'(!(ICST_Date))|(ICST_ClockInterface)','Support of Clock Interface DateTime property date fields');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (71,'ICST_Milliseconds',0,10,'(!(ICST_Milliseconds))|(ICST_ClockInterface)','Support of Clock Interface DateTime property milliseconds field');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (72,'ICST_TimeAuthorityInterface',0,10,'(!(ICST_TimeAuthorityInterface))|(ICST_ClockInterface)','Support of TimeAuthority Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (73,'ICST_AlarmFactoryInterface',0,10,'(!(ICST_AlarmFactoryInterface))|(ICST_TimeServiceFramework)','Support of AlarmFactory Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (74,'ICST_AlarmInterface',0,10,'((ICST_AlarmFactoryInterface)&(ICST_AlarmInterface))|(!(ICST_AlarmFactoryInterface))','Support of Alarm Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (75,'ICST_TimerFactoryInterface',0,10,'(!(ICST_TimerFactoryInterface))|(ICST_TimeServiceFramework)','Support of TimerFactory Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (76,'ICST_TimerInterface',0,10,'((ICST_TimerFactoryInterface)&(ICST_TimerInterface))|(!(ICST_TimerFactoryInterface))','Support of Timer Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (77,'ICSN_NotificationConsumer',0,2,'((ICSN_NotificationServiceFramework)&(ICSN_NotificationProducerInterface))|((ICSN_NotificationServiceFramework)&(!(ICSN_NotificationProducerInterface))&(ICSN_NotificationConsumer))|((!(ICSN_NotificationServiceFramework))&(!(ICSN_NotificationConsumer)))','Support of Notification Consumer side');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (79,'ICSLC_LightingControllerServiceFramework',0,11,null,'Support of Lighting Controller Service Framework');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (80,'ICSLC_ControllerServiceInterface',0,11,'((ICSLC_LightingControllerServiceFramework)&(ICSLC_ControllerServiceInterface))|((!(ICSLC_LightingControllerServiceFramework))&(!(ICSLC_ControllerServiceInterface)))','Support of ControllerService Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (81,'ICSLC_ControllerServiceLampInterface',0,11,'((ICSLC_LightingControllerServiceFramework)&(ICSLC_ControllerServiceLampInterface))|((!(ICSLC_LightingControllerServiceFramework))&(!(ICSLC_ControllerServiceLampInterface)))','Support of ControllerService.Lamp Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (82,'ICSLC_ControllerServiceLampGroupInterface',0,11,'((ICSLC_LightingControllerServiceFramework)&(ICSLC_ControllerServiceLampGroupInterface))|((!(ICSLC_LightingControllerServiceFramework))&(!(ICSLC_ControllerServiceLampGroupInterface)))','Support of ControllerService.LampGroup Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (83,'ICSLC_ControllerServicePresetInterface',0,11,'((ICSLC_LightingControllerServiceFramework)&(ICSLC_ControllerServicePresetInterface))|((!(ICSLC_LightingControllerServiceFramework))&(!(ICSLC_ControllerServicePresetInterface)))','Support of ControllerService.Preset Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (84,'ICSLC_ControllerServiceSceneInterface',0,11,'((ICSLC_LightingControllerServiceFramework)&(ICSLC_ControllerServiceSceneInterface))|((!(ICSLC_LightingControllerServiceFramework))&(!(ICSLC_ControllerServiceSceneInterface)))','Support of ControllerService.Scene Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (85,'ICSLC_ControllerServiceMasterSceneInterface',0,11,'((ICSLC_LightingControllerServiceFramework)&(ICSLC_ControllerServiceMasterSceneInterface))|((!(ICSLC_LightingControllerServiceFramework))&(!(ICSLC_ControllerServiceMasterSceneInterface)))','Support of ControllerService.MasterScene Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (86,'ICSLC_LeaderElectionAndStateSyncInterface',0,11,'((ICSLC_LightingControllerServiceFramework)&(ICSLC_LeaderElectionAndStateSyncInterface))|((!(ICSLC_LightingControllerServiceFramework))&(!(ICSLC_LeaderElectionAndStateSyncInterface)))','Support of LeaderElectionAndStateSync Interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (87,'ICSCO_DeviceName',0,1,null,'Support of About Interface GetAboutData method DeviceName data field');

INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (1,'IXITCO_AboutVersion','1',1,'About Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (2,'IXITCO_AppId','',1,'The globally unique identifier for the application');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (3,'IXITCO_DefaultLanguage','',1,'The default language supported by the device. Specified as a IETF language tag listed in RFC 5646.');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (4,'IXITCO_DeviceName','',1,'Name of the device set by platform-specific means (such as Linux and Android).');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (5,'IXITCO_DeviceId','',1,'Device identifier set by platform-specific means.');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (6,'IXITCO_AppName','',1,'Application name assigned by the app manufacturer (developer or the OEM)');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (7,'IXITCO_Manufacturer','',1,'The manufacturer''s name of the app.');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (8,'IXITCO_ModelNumber','',1,'The app model number.');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (9,'IXITCO_SoftwareVersion','',1,'Software version of the app.');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (10,'IXITCO_AJSoftwareVersion','',1,'Current version of the AllJoyn SDK used by the application.');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (11,'IXITCO_HardwareVersion','',1,'Hardware version of the device on which the app is running.');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (12,'IXITCO_IntrospectableVersion','1',1,'Introspectable Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (13,'IXITN_NotificationVersion','1',2,'Notification Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (14,'IXITN_TTL','2',2,'Validity period (in minutes) of the notification message.');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (15,'IXITN_NotificationProducerVersion','1',2,'Notification Producer Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (16,'IXITN_NotificationDismisserVersion','1',2,'Notification Dismisser Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (17,'IXITON_OnboardingVersion','1',3,'Onboarding Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (18,'IXITON_SoftAP','',3,'SSID of the Soft AP broadcast by the DUT');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (19,'IXITON_SoftAPAuthType','',3,'Authentication required to connect the Soft AP.');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (20,'IXITON_SoftAPpassphrase','',3,'Passphrase (if required) to authenticate to the Soft AP');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (21,'IXITON_PersonalAP','',3,'SSID of the Personal AP network.');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (22,'IXITON_PersonalAPAuthType','',3,'Authentication required to connect to the Personal AP.');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (23,'IXITON_PersonalAPpassphrase','',3,'Passphrase (if required) to authenticate to the Personal AP.');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (24,'IXITCP_ControlPanelVersion','1',4,'ControlPanel interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (25,'IXITCP_ContainerVersion','1',4,'Container Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (26,'IXITCP_PropertyVersion','1',4,'Property Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (27,'IXITCP_LabelPropertyVersion','1',4,'LabelProperty Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (28,'IXITCP_ActionVersion','1',4,'Action Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (29,'IXITCP_NotificationActionVersion','1',4,'NotificationAction Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (30,'IXITCP_DialogVersion','1',4,'Dialog Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (31,'IXITCP_ListPropertyVersion','1',4,'ListProperty Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (32,'IXITCP_HTTPControlVersion','1',4,'HTTPControl Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (33,'IXITCF_ConfigVersion','1',5,'Config Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (34,'IXITCF_Passcode','',5,'Passphrase that will be utilized for the secure Config interface.');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (35,'IXITL_LampServiceVersion','1',6,'LampService Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (36,'IXITL_LampParametersVersion','1',6,'LampParameters Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (37,'IXITL_LampDetailsVersion','1',6,'LampDetails Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (38,'IXITL_LampStateVersion','1',6,'LampState Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (39,'IXITAU_StreamVersion','1',7,'Stream Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (40,'IXITAU_TestObjectPath','1',7,'The path of the Stream bus object being tested');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (41,'IXITAU_PortVersion','1',7,'Stream.Port Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (42,'IXITAU_AudioSinkVersion','1',7,'Stream.Port.AudioSink Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (43,'IXITAU_timeNanos','',7,'Timestramp in nanoseconds since the UNIX epoch.');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (44,'IXITAU_AudioSourceVersion','1',7,'Stream.Port.AudioSource Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (45,'IXITAU_ImageSinkVersion','1',7,'Stream.Port.ImageSink Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (46,'IXITAU_ImageSourceVersion','1',7,'Stream.Port.ImageSource Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (47,'IXITAU_MetadataSinkVersion','1',7,'Stream.Port.Application.MetadataSink Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (48,'IXITAU_MetadataSourceVersion','1',7,'Stream.Port.Application.MetadataSource Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (49,'IXITAU_ControlVolumeVersion','1',7,'Control.Volume Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (50,'IXITAU_delta','',7,'The amount by which to increase or decrease the volume.');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (51,'IXITAU_change','',7,'- For values greater than 0 and smaller than 1, the percentage by which to raise the volume. - For values smaller than 0 and bigger than -1, the percentage by which to reduce the volume. - For values greater than or equal to 1, increase volume to maximum.');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (52,'IXITAU_ClockVersion','1',7,'Stream.Clock Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (53,'IXITAU_adjustNanos','',7,'The amount, positive or negative, to adjust the time.');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (54,'IXITG_AppMgmtVersion','1',8,'App Management Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (55,'IXITG_CtrlAppVersion','1',8,'Controller App interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (56,'IXITG_CtrlAccessVersion','1',8,'Controller Access Control List Management Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (57,'IXITG_CtrlAclVersion','1',8,'Controller ACL Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (58,'IXITG_ConnAppVersion','1',8,'Connector App Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (59,'IXITSH_CentralizedManagementVersion','1',9,'Centralized Management Interface version');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (60,'IXITSH_WellKnownName','',9,'Well-known name provided by the appliance to be controlled');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (61,'IXITSH_UniqueName','',9,'Unique name of the appliance to be controlled');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (62,'IXITSH_DeviceId','',9,'The identification of the appliance to be controlled.');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (63,'IXITSH_HeartBeatInterval','',9,'Regular interval (in seconds) where DeviceHeartBeat method needs to be called to keep the device present and alive.');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (64,'IXITT_ClockVersion','1',10,'Clock Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (65,'IXITT_TimeAuthorityVersion','1',10,'TimeAuthority Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (66,'IXITT_AlarmFactoryVersion','1',10,'AlarmFactory Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (67,'IXITT_AlarmVersion','1',10,'Alarm Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (68,'IXITT_TimerFactoryVersion','1',10,'TimerFactory Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (69,'IXITT_TimerVersion','1',10,'Timer Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (70,'IXITCO_SupportedLanguages','en',1,'List of supported languages');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (71,'IXITLC_ControllerServiceVersion','1',11,'ControllerService Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (72,'IXITLC_ControllerServiceLampVersion','1',11,'ControllerService.Lamp Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (73,'IXITLC_ControllerServiceLampGroupVersion','1',11,'ControllerService.LampGroup Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (74,'IXITLC_ControllerServicePresetVersion','1',11,'ControllerService.Preset Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (75,'IXITLC_ControllerServiceSceneVersion','1',11,'ControllerService.Scene Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (76,'IXITLC_ControllerServiceMasterSceneVersion','1',11,'ControllerService.MasterScene Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (77,'IXITLC_LeaderElectionAndStateSyncVersion','1',11,'LeaderElectionAndStateSync Interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (78,'IXITCO_Description','',1,'Detailed description expressed in language tags as in RFC 5646');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (79,'IXITCO_DateOfManufacture','',1,'Date of manufacture using format YYYY-MM-DD (known as XML DateTime format)');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (80,'IXITCO_SupportUrl','',1,'Support URL (populated by the manufacturer)');

INSERT INTO parameters (id_param,name,value,description) VALUES (1,'GPCO_AnnouncementTimeout','30','About announcement timeout in seconds');
INSERT INTO parameters (id_param,name,value,description) VALUES (2,'GPON_WaitSoftAP','12000','Onboarding Service Framework time to wait for Soft AP in ms');
INSERT INTO parameters (id_param,name,value,description) VALUES (3,'GPON_ConnectSoftAP','60000','Onboarding Service Framework time to wait to connect to Soft AP in ms');
INSERT INTO parameters (id_param,name,value,description) VALUES (4,'GPON_WaitSoftAPAfterOffboard','15000','Onboarding Service Framework time to wait for Soft AP after offboard in ms');
INSERT INTO parameters (id_param,name,value,description) VALUES (5,'GPON_ConnectPersonalAP','60000','Onboarding Service Framework time to wait to connect to Personal AP in ms');
INSERT INTO parameters (id_param,name,value,description) VALUES (6,'GPON_Disconnect','30000','Onboarding Service Framework time to wait for disconnect in ms');
INSERT INTO parameters (id_param,name,value,description) VALUES (7,'GPON_NextAnnouncement','180000','Onboarding Service Framework time to wait for next device announcement in ms');
INSERT INTO parameters (id_param,name,value,description) VALUES (8,'GPCF_SessionLost','30','Configuration Service Framework session lost timeout in seconds');
INSERT INTO parameters (id_param,name,value,description) VALUES (9,'GPCF_SessionClose','5','Configuration Service Framework session close timeout in seconds');
INSERT INTO parameters (id_param,name,value,description) VALUES (10,'GPL_SessionClose','30','Lighting Service Framework session close timeout in seconds');
INSERT INTO parameters (id_param,name,value,description) VALUES (11,'GPAU_Signal','30','Audio Service Framework signal timeout in seconds');
INSERT INTO parameters (id_param,name,value,description) VALUES (12,'GPAU_Link','120','Audio Service Framework link timeout in seconds');
INSERT INTO parameters (id_param,name,value,description) VALUES (13,'GPG_SessionClose','30','Gateway Service Framework session close timeout in seconds');
INSERT INTO parameters (id_param,name,value,description) VALUES (14,'GPSH_Signal','30','Smart Home Service Framework signal timeout in seconds');
INSERT INTO parameters (id_param,name,value,description) VALUES (15,'GPLC_SessionClose','30','Lighting Controller Service Framework session close timeout in seconds');

INSERT INTO services (id_service,name,enabled) VALUES (1,'Core Interface',1);
INSERT INTO services (id_service,name,enabled) VALUES (2,'Notification',1);
INSERT INTO services (id_service,name,enabled) VALUES (3,'Onboarding',1);
INSERT INTO services (id_service,name,enabled) VALUES (4,'Control Panel',1);
INSERT INTO services (id_service,name,enabled) VALUES (5,'Configuration',1);
INSERT INTO services (id_service,name,enabled) VALUES (6,'Lighting',1);
INSERT INTO services (id_service,name,enabled) VALUES (7,'Audio',1);
INSERT INTO services (id_service,name,enabled) VALUES (8,'Gateway',1);
INSERT INTO services (id_service,name,enabled) VALUES (9,'Smart Home',1);
INSERT INTO services (id_service,name,enabled) VALUES (10,'Time',1);
INSERT INTO services (id_service,name,enabled) VALUES (11,'Lighting Controller',1);



INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (1,'About-v1-01','Conformance','',1,null,null,null,'About announcement received');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (2,'About-v1-02','Conformance','',1,null,null,null,'Version property consistent with the About announcement');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (3,'About-v1-03','Conformance','',1,null,null,null,'GetObjectDescription() consistent with the About announcement');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (4,'About-v1-04','Conformance','',1,null,null,null,'Bus objects consistent with the About announcement');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (5,'About-v1-05','Conformance','',1,null,null,null,'Standardized interfaces match definitions');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (6,'About-v1-06','Conformance','',1,null,null,null,'GetAboutData() with default language');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (7,'About-v1-07','Conformance','',1,null,null,null,'GetAboutData() with each supported language');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (8,'About-v1-08','Conformance','',1,null,null,null,'GetAboutData() without a specified language');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (9,'About-v1-09','Conformance','',1,null,null,null,'GetAboutData() for an unsupported language');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (10,'About-v1-10','Conformance','(ICSCO_IconInterface)',1,null,null,null,'GetContent() on the About DeviceIcon');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (11,'About-v1-11','Conformance','(ICSCO_IconInterface)',1,null,null,null,'GetUrl() on the About DeviceIcon');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (12,'EventsActions-v1-01','Conformance','',1,null,null,null,'Description tag existence in introspection XML and identical XML across different description languages');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (13,'Notification-Consumer-v1-01','Conformance','(ICSN_NotificationInterface)&(ICSN_NotificationConsumer)',2,null,null,null,'Basic text messages');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (14,'Notification-Consumer-v1-02','Conformance','(ICSN_NotificationInterface)&(ICSN_NotificationConsumer)',2,null,null,null,'Multiple languages');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (15,'Notification-Consumer-v1-04','Conformance','(ICSN_NotificationInterface)&(ICSN_NotificationConsumer)',2,null,null,null,'Invalid language field');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (16,'Notification-Consumer-v1-05','Conformance','(ICSN_NotificationInterface)&(ICSN_NotificationConsumer)',2,null,null,null,'Message priority');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (17,'Notification-Consumer-v1-06','Conformance','(ICSN_NotificationInterface)&(ICSN_NotificationConsumer)',2,null,null,null,'Custom attributes');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (18,'Notification-v1-01','Conformance','(ICSN_NotificationInterface)&(ICSN_NotificationProducerInterface)',2,null,null,null,'Sending of notifications');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (19,'Onboarding-v1-01','Conformance','(ICSON_OnboardingInterface)',3,null,null,null,'Offboard the DUT');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (20,'Onboarding-v1-02','Conformance','(ICSON_OnboardingInterface)',3,null,null,null,'Onboard the DUT');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (21,'Onboarding-v1-03','Conformance','(ICSON_OnboardingInterface)',3,null,null,null,'Session joined on Soft AP');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (22,'Onboarding-v1-04','Conformance','(ICSON_OnboardingInterface)',3,null,null,null,'Invalid authType provided to ConfigWiFi() returns OutOfRange error');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (23,'Onboarding-v1-05','Conformance','(ICSON_OnboardingInterface)',3,null,null,null,'Nonexistent personal AP SSID provided to ConfigWiFi()');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (24,'Onboarding-v1-06','Conformance','(ICSON_OnboardingInterface)',3,null,null,null,'Invalid passphrase for personal AP provided to ConfigWiFi()');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (25,'Onboarding-v1-07','Conformance','(ICSON_OnboardingInterface)',3,null,null,null,'AuthType value of "any" provided to ConfigWiFi()');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (26,'Onboarding-v1-08','Conformance','(ICSON_OnboardingInterface)',3,null,null,null,'GetScanInfo() returns results or FeatureNotAvailable error');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (27,'Onboarding-v1-09','Conformance','(ICSON_OnboardingInterface)',3,null,null,null,'Call onboarding method without proper authentication');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (28,'Onboarding-v1-10','Conformance','(ICSON_OnboardingInterface)&(ICSCF_ConfigurationInterface)',3,null,null,null,'Call Onboarding method after changing the passcode');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (29,'Onboarding-v1-11','Conformance','(ICSON_OnboardingInterface)&(ICSCF_FactoryResetMethod)',3,null,null,null,'Factory reset');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (30,'Onboarding-v1-12','Conformance','(ICSON_OnboardingInterface)&(ICSCF_FactoryResetMethod)',3,null,null,null,'Factory reset resets passcode');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (31,'ControlPanel-v1-01','Conformance','((ICSCP_ControlPanelInterface)&((ICSCP_ContainerInterface)|(ICSCP_SecuredContainerInterface)))',4,null,null,null,'Verify all ControlPanel bus objects');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (32,'ControlPanel-v1-02','Conformance','((ICSCP_ContainerInterface)|(ICSCP_SecuredContainerInterface))',4,null,null,null,'Verify all Container bus objects');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (34,'ControlPanel-v1-03','Conformance','(ICSCP_PropertyInterface)|(ICSCP_SecuredPropertyInterface)',4,null,null,null,'Verify all Property bus objects');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (35,'ControlPanel-v1-04','Conformance','(ICSCP_LabelPropertyInterface)',4,null,null,null,'Verify all LabelProperty bus objects');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (36,'ControlPanel-v1-05','Conformance','(ICSCP_ActionInterface)|(ICSCP_SecuredActionInterface)',4,null,null,null,'Verify all Action bus objects');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (37,'ControlPanel-v1-06','Conformance','(ICSCP_DialogInterface)|(ICSCP_SecuredDialogInterface)',4,null,null,null,'Verify all Dialog bus objects');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (38,'ControlPanel-v1-07','Conformance','(ICSCP_ListPropertyInterface)|(ICSCP_SecuredListPropertyInterface)',4,null,null,null,'Verify all ListProperty bus objects');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (39,'ControlPanel-v1-08','Conformance','(ICSCP_NotificationActionInterface)&((ICSCP_ContainerInterface)|(ICSCP_SecuredContainerInterface)|(ICSCP_DialogInterface)|(ICSCP_SecuredDialogInterface))',4,null,null,null,'Verify all NotificationAction bus objects');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (40,'ControlPanel-v1-09','Conformance','(ICSCP_HTTPControlInterface)',4,null,null,null,'Verify all HTTPControl bus objects');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (41,'ControlPanel-v1-10','Conformance','((ICSCP_SecuredContainerInterface)|(ICSCP_SecuredPropertyInterface)|(ICSCP_SecuredActionInterface)|(ICSCP_SecuredDialogInterface)|(ICSCP_SecuredListPropertyInterface))',4,null,null,null,'Verify all secured ControlPanel bus objects');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (42,'Config-v1-01','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'System App AppId equals DeviceId');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (43,'Config-v1-02','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'Call a Config interface method without proper authentication');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (44,'Config-v1-04','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'GetConfigurations() method with default language');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (45,'Config-v1-05','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'GetConfigurations() method with unspecified language');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (46,'Config-v1-06','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'GetConfigurations() method for each supported language');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (47,'Config-v1-07','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'GetConfigurations() method with unsupported language');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (48,'Config-v1-08','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'UpdateConfigurations() method with a new DeviceName');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (49,'Config-v1-12','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'UpdateConfigurations() method with a DeviceName containing special characters');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (50,'Config-v1-13','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'UpdadteConfigurations() method with an unsupported language');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (51,'Config-v1-14','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'UpdateConfigurations() method with a DefaultLanguage of another language');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (52,'Config-v1-15','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'UpdateConfigurations() method for DefaultLanguage with an unsupported language');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (53,'Config-v1-16','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'UpdateConfigurations() method for DefaultLanguage with an unspecified language');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (54,'Config-v1-19','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'UpdateConfigurations() method for an invalid field');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (55,'Config-v1-20','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'ResetConfigurations() method for DeviceName');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (56,'Config-v1-21','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'ResetConfigurations() method for DefaultLanguage (at least one supported language)');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (57,'Config-v1-22','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'ResetConfigurations() method for DefaultLanguage (more than one supported language)');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (58,'Config-v1-24','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'ResetConfigurations() method with an unsupported language');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (59,'Config-v1-25','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'ResetConfigurations() method for an invalid field');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (60,'Config-v1-26','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'Restart() method');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (61,'Config-v1-27','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'Restart() method persists configuration changes');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (62,'Config-v1-29','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'SetPasscode() method with a new value');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (63,'Config-v1-30','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'SetPasscode() method with a one-character value');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (64,'Config-v1-31','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'SetPasscode() method with special characters');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (65,'Config-v1-32','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'Restart() method persists changed passcode');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (66,'Config-v1-33','Conformance','(ICSCF_FactoryResetMethod)',5,null,null,null,'FactoryReset() method');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (67,'Config-v1-34','Conformance','(ICSCF_FactoryResetMethod)',5,null,null,null,'FactoryReset() method clears configured data');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (68,'Config-v1-35','Conformance','(ICSCF_FactoryResetMethod)',5,null,null,null,'FactoryReset() method resets the passcode');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (69,'LSF_Lamp-v1-01','Conformance','(ICSL_LampServiceInterface)',6,null,null,null,'Service Interface Version equals 1');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (70,'LSF_Lamp-v1-02','Conformance','(ICSL_LampServiceInterface)',6,null,null,null,'Lamp Service Version equals 1');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (71,'LSF_Lamp-v1-03','Conformance','(ICSL_LampServiceInterface)',6,null,null,null,'ClearLampFault() method');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (72,'LSF_Lamp-v1-04','Conformance','(ICSL_LampStateInterface)',6,null,null,null,'SetOnOff() property');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (73,'LSF_Lamp-v1-05','Conformance','(ICSL_Color)&(ICSL_LampStateInterface)',6,null,null,null,'SetHue() property');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (74,'LSF_Lamp-v1-06','Conformance','(ICSL_Color)&(ICSL_LampStateInterface)',6,null,null,null,'SetSaturation() property');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (75,'LSF_Lamp-v1-07','Conformance','(ICSL_ColorTemperature)&(ICSL_LampStateInterface)',6,null,null,null,'SetColorTemp() property');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (76,'LSF_Lamp-v1-08','Conformance','(ICSL_Dimmable)&(ICSL_LampStateInterface)',6,null,null,null,'SetBrightness() property');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (77,'LSF_Lamp-v1-09','Conformance','(ICSL_LampStateInterface)',6,null,null,null,'TransitionLampState and verify state and signal');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (78,'LSF_Lamp-v1-10','Conformance','(ICSL_LampStateInterface)',6,null,null,null,'ApplyPulseEffect');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (79,'LSF_Lamp-v1-11','Conformance','(ICSL_LampServiceInterface)',6,null,null,null,'Service interface XML matches');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (80,'LSF_Lamp-v1-12','Conformance','(ICSL_LampParametersInterface)',6,null,null,null,'Parameters interface version equals 1');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (81,'LSF_Lamp-v1-13','Conformance','(ICSL_LampParametersInterface)',6,null,null,null,'GetEnergyUsageMilliwatts');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (82,'LSF_Lamp-v1-14','Conformance','(ICSL_LampParametersInterface)',6,null,null,null,'GetBrightnessLumens');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (83,'LSF_Lamp-v1-15','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'Details interface version equals 1');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (84,'LSF_Lamp-v1-16','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'GetMake');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (85,'LSF_Lamp-v1-17','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'GetModel');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (86,'LSF_Lamp-v1-18','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'GetType');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (87,'LSF_Lamp-v1-19','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'GetLampType');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (88,'LSF_Lamp-v1-20','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'GetLampBaseType');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (89,'LSF_Lamp-v1-21','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'GetLampBeamAngle');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (90,'LSF_Lamp-v1-22','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'GetDimmable');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (91,'LSF_Lamp-v1-23','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'GetColor');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (92,'LSF_Lamp-v1-24','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'GetVariableColorTemp');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (93,'LSF_Lamp-v1-25','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'GetLampID');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (94,'LSF_Lamp-v1-26','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'GetHasEffects');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (95,'LSF_Lamp-v1-27','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'GetMinVoltage');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (96,'LSF_Lamp-v1-28','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'GetMaxVoltage');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (97,'LSF_Lamp-v1-29','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'GetWattage');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (98,'LSF_Lamp-v1-30','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'GetIncandescentEquivalent');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (99,'LSF_Lamp-v1-31','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'GetMaxLumens');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (100,'LSF_Lamp-v1-32','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'GetMinTemperature');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (101,'LSF_Lamp-v1-33','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'GetMaxTemperature');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (102,'LSF_Lamp-v1-34','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'GetColorRenderingIndex');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (103,'Audio-v1-01','Conformance','(ICSAU_StreamPortInterface)',7,null,null,null,'Validate Stream bus objects');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (104,'Audio-v1-02','Conformance','(ICSAU_StreamInterface)',7,null,null,null,'Opening a Stream bus object');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (105,'Audio-v1-03','Conformance','(ICSAU_StreamInterface)',7,null,null,null,'Opening and closing a Stream bus object');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (106,'Audio-v1-04','Conformance','(ICSAU_StreamInterface)',7,null,null,null,'Closing an unopened Stream bus object');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (107,'Audio-v1-05','Conformance','(ICSAU_PortAudioSinkInterface)',7,null,null,null,'Verify any AudioSink capabilities');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (108,'Audio-v1-06','Conformance','(ICSAU_PortImageSinkInterface)',7,null,null,null,'Verify any ImageSink capabilities');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (109,'Audio-v1-07','Conformance','(ICSAU_PortApplicationMetadataSinkInterface)',7,null,null,null,'Verify any Application.MetadataSink capabilities');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (110,'Audio-v1-08','Conformance','(ICSAU_PortAudioSinkInterface)',7,null,null,null,'Configure any AudioSink port');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (111,'Audio-v1-09','Conformance','(ICSAU_PortAudioSinkInterface)',7,null,null,null,'Configure any AudioSink bus object with an invalid configuration');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (112,'Audio-v1-10','Conformance','(ICSAU_PortAudioSinkInterface)',7,null,null,null,'Configure any AudioSink bus object twice');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (113,'Audio-v1-11','Conformance','(ICSAU_PortAudioSinkInterface)',7,null,null,null,'Check for OwnershipLost signal');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (114,'Audio-v1-12','Conformance','(ICSAU_PortAudioSinkInterface)',7,null,null,null,'Playback on an AudioSink');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (115,'Audio-v1-13','Conformance','(ICSAU_PortAudioSinkInterface)',7,null,null,null,'Pausing playback on an AudioSink');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (116,'Audio-v1-14','Conformance','(ICSAU_PortAudioSinkInterface)',7,null,null,null,'Flushing a paused AudioSink');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (117,'Audio-v1-15','Conformance','(ICSAU_PortAudioSinkInterface)',7,null,null,null,'Flushing a playing AudioSink');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (118,'Audio-v1-16','Conformance','(ICSAU_PortAudioSinkInterface)',7,null,null,null,'Paused AudioSink remains paused after sending data');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (119,'Audio-v1-17','Conformance','(ICSAU_PortAudioSinkInterface)',7,null,null,null,'Playing an empty AudioSink remains IDLE');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (120,'Audio-v1-18','Conformance','(ICSAU_PortAudioSinkInterface)',7,null,null,null,'Flushing an idle AudioSink');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (121,'Audio-v1-19','Conformance','(ICSAU_PortImageSinkInterface)',7,null,null,null,'Sending data to an ImageSink');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (122,'Audio-v1-20','Conformance','(ICSAU_PortApplicationMetadataSinkInterface)',7,null,null,null,'Sending data to an Application.MetadataSink');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (123,'Audio-v1-21','Conformance','(ICSAU_ControlVolumeInterface)',7,null,null,null,'Setting the mute state on an AudioSink');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (124,'Audio-v1-22','Conformance','(ICSAU_ControlVolumeInterface)',7,null,null,null,'Setting the volume on an AudioSink');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (125,'Audio-v1-23','Conformance','(ICSAU_ControlVolumeInterface)',7,null,null,null,'Setting an invalid volume on an AudioSink');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (126,'Audio-v1-24','Conformance','(ICSAU_ControlVolumeInterface)',7,null,null,null,'Independence of mute and volume on an AudioSink');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (127,'Audio-v1-25','Conformance','(ICSAU_StreamClockInterface)',7,null,null,null,'Synchronize clocks on an AudioSink');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (128,'Audio-v1-26','Conformance','(ICSAU_VolumeControlEnabled)',7,null,null,null,'Set Volume using AdjustVolume');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (129,'Audio-v1-27','Conformance','(ICSAU_VolumeControlEnabled)',7,null,null,null,'Set Volume using AdjustVolumePercent');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (130,'Audio-v1-28','Conformance','(ICSAU_VolumeControlEnabled)',7,null,null,null,'Test Volume.Enabled flag');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (131,'GWAgent-v1-01','Conformance','(ICSG_ProfileManagementInterface)&(ICSG_AppAccessInterface)&(ICSG_AppManagementInterface)',8,null,null,null,'Interfaces match definition');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (132,'SmartHome-v1-01','Conformance','(ICSSH_CentralizedManagementInterface)',9,null,null,null,'Service Interface Version equals 1');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (133,'SmartHome-v1-02','Conformance','(ICSSH_CentralizedManagementInterface)',9,null,null,null,'ApplianceRegistration()');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (134,'SmartHome-v1-03','Conformance','(ICSSH_CentralizedManagementInterface)',9,null,null,null,'Execute()');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (135,'SmartHome-v1-04','Conformance','(ICSSH_CentralizedManagementInterface)',9,null,null,null,'ApplianceUnRegistration()');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (136,'SmartHome-v1-05','Conformance','(ICSSH_CentralizedManagementInterface)',9,null,null,null,'DeviceHeartBeat()');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (137,'TimeService-v1-01','Conformance','(ICST_TimeServiceFramework)',10,null,null,null,'GetObjectDescription() consistent with the TimeService announcement');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (138,'TimeService-v1-02','Conformance','(ICST_ClockInterface)',10,null,null,null,'VerifyClocks');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (139,'TimeService-v1-03','Conformance','(ICST_TimerFactoryInterface)|(ICST_TimerInterface)',10,null,null,null,'VerifyTimers');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (140,'TimeService-v1-04','Conformance','(ICST_AlarmFactoryInterface)|(ICST_AlarmInterface)',10,null,null,null,'VerifyAlarms');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (141,'LSF_Controller-v1-01','Conformance','(ICSLC_LightingControllerServiceFramework)',11,null,null,null,'Service interface XML matches');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (142,'LSF_Controller-v1-02','Conformance','(ICSLC_ControllerServiceInterface)',11,null,null,null,'Service interface versions');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (143,'LSF_Controller-v1-03','Conformance','(ICSLC_ControllerServiceInterface)',11,null,null,null,'Reset controller service');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (144,'LSF_Controller-v1-04','Conformance','(ICSLC_ControllerServiceLampInterface)',11,null,null,null,'Lamp info');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (145,'LSF_Controller-v1-05','Conformance','(ICSLC_ControllerServiceLampInterface)',11,null,null,null,'Get and set lamp name');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (146,'LSF_Controller-v1-06','Conformance','(ICSLC_ControllerServiceLampInterface)',11,null,null,null,'Lamp details');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (147,'LSF_Controller-v1-07','Conformance','(ICSLC_ControllerServiceLampInterface)',11,null,null,null,'Lamp parameters');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (148,'LSF_Controller-v1-08','Conformance','(ICSLC_ControllerServiceLampInterface)',11,null,null,null,'Lamp state');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (149,'LSF_Controller-v1-09','Conformance','(ICSLC_ControllerServiceLampInterface)',11,null,null,null,'Lamp state transition');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (150,'LSF_Controller-v1-10','Conformance','(ICSLC_ControllerServiceLampInterface)',11,null,null,null,'Lamp state pulse');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (151,'LSF_Controller-v1-11','Conformance','(ICSLC_ControllerServiceLampInterface)',11,null,null,null,'Lamp change with preset');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (152,'LSF_Controller-v1-12','Conformance','(ICSLC_ControllerServiceLampInterface)',11,null,null,null,'Reset lamp state');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (153,'LSF_Controller-v1-13','Conformance','(ICSLC_ControllerServiceLampInterface)',11,null,null,null,'Lamp faults');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (154,'LSF_Controller-v1-14','Conformance','(ICSLC_ControllerServiceLampGroupInterface)',11,null,null,null,'Lamp group CRUD');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (155,'LSF_Controller-v1-15','Conformance','(ICSLC_ControllerServiceLampGroupInterface)',11,null,null,null,'Get and set lamp group name');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (156,'LSF_Controller-v1-16','Conformance','(ICSLC_ControllerServiceLampGroupInterface)',11,null,null,null,'Transition lamp group state');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (157,'LSF_Controller-v1-17','Conformance','(ICSLC_ControllerServiceLampGroupInterface)',11,null,null,null,'Pulse lamp group state');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (158,'LSF_Controller-v1-18','Conformance','(ICSLC_ControllerServiceLampGroupInterface)',11,null,null,null,'Reset lamp group state');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (159,'LSF_Controller-v1-19','Conformance','(ICSLC_ControllerServiceLampGroupInterface)',11,null,null,null,'Change lamp group state with presets');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (160,'LSF_Controller-v1-20','Conformance','(ICSLC_ControllerServicePresetInterface)',11,null,null,null,'Default lamp state');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (161,'LSF_Controller-v1-21','Conformance','(ICSLC_ControllerServicePresetInterface)',11,null,null,null,'Preset CRUD');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (162,'LSF_Controller-v1-22','Conformance','(ICSLC_ControllerServicePresetInterface)',11,null,null,null,'Get and set preset name');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (163,'LSF_Controller-v1-23','Conformance','(ICSLC_ControllerServiceSceneInterface)',11,null,null,null,'Create scene');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (164,'LSF_Controller-v1-24','Conformance','(ICSLC_ControllerServiceSceneInterface)',11,null,null,null,'Update and delete scene');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (165,'LSF_Controller-v1-25','Conformance','(ICSLC_ControllerServiceSceneInterface)',11,null,null,null,'Apply scene');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (166,'LSF_Controller-v1-26','Conformance','(ICSLC_ControllerServiceSceneInterface)',11,null,null,null,'Get and set scene name');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (167,'LSF_Controller-v1-27','Conformance','(ICSLC_ControllerServiceMasterSceneInterface)',11,null,null,null,'Create master scene');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (168,'LSF_Controller-v1-28','Conformance','(ICSLC_ControllerServiceMasterSceneInterface)',11,null,null,null,'Update and delete master scene');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (169,'LSF_Controller-v1-29','Conformance','(ICSLC_ControllerServiceMasterSceneInterface)',11,null,null,null,'Apply master scene');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (170,'LSF_Controller-v1-30','Conformance','(ICSLC_ControllerServiceMasterSceneInterface)',11,null,null,null,'Get and set master scene name');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (171,'LSF_Controller-v1-31','Conformance','(ICSLC_LeaderElectionAndStateSyncInterface)',11,null,null,null,'Leader election get checksum and modification timestamp');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (172,'LSF_Controller-v1-32','Conformance','(ICSLC_LeaderElectionAndStateSyncInterface)',11,null,null,null,'Leader election blob changed');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (173,'LSF_Controller-v1-33','Conformance','(ICSLC_LeaderElectionAndStateSyncInterface)',11,null,null,null,'Leader election overthrow');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (174,'IOP_About-v1-01','Interoperability','',1,null,null,null,'Device detected');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (175,'IOP_About-v1-02','Interoperability','',1,null,null,null,'Reception of About Announcement');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (176,'IOP_About-v1-03','Interoperability','',1,null,null,null,'Reception of GetAboutData information');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (177,'IOP_About-v1-04','Interoperability','(ICSCO_IconInterface)',1,null,null,null,'Support of DeviceIcon Object');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (178,'IOP_Notification-v1-01','Interoperability','(ICSN_NotificationInterface)&(ICSN_NotificationProducerInterface)',2,null,null,null,'Sending notifications');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (179,'IOP_Notification-Consumer-v1-01','Interoperability','(ICSN_NotificationInterface)&(ICSN_NotificationConsumer)',2,null,null,null,'Receiving notifications inside and outside the TTL period');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (180,'IOP_Notification-Consumer-v1-02','Interoperability','(ICSN_NotificationInterface)&(ICSN_NotificationConsumer)',2,null,null,null,'Handling different types of notification messages');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (181,'IOP_Notification-Consumer-v1-03','Interoperability','(ICSN_NotificationInterface)&(ICSN_NotificationConsumer)',2,null,null,null,'Display different languages messages');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (182,'IOP_Onboarding-v1-01','Interoperability','(ICSON_OnboardingInterface)',3,null,null,null,'Onboarding Service announcement');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (183,'IOP_Onboarding-v1-02','Interoperability','(ICSON_OnboardingInterface)',3,null,null,null,'DUT Offboarding');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (184,'IOP_Onboarding-v1-03','Interoperability','(ICSON_OnboardingInterface)',3,null,null,null,'DUT Onboarding');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (185,'IOP_Onboarding-v1-04','Interoperability','(ICSON_OnboardingInterface)',3,null,null,null,'DUT Onboarding without proper authentication');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (186,'IOP_Onboarding-v1-05','Interoperability','(ICSON_OnboardingInterface)',3,null,null,null,'DUT Onboarding with incorrect WiFi configuration data');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (187,'IOP_Onboarding-v1-06','Interoperability','(ICSON_GetScanInfoMethod)',3,null,null,null,'DUT Onboarding, use of GetScanInfo method');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (188,'IOP_Onboarding-v1-07','Interoperability','(ICSON_GetScanInfoMethod)&(ICSCF_ConfigurationInterface)',3,null,null,null,'Onboarding after changing passcode');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (189,'IOP_ControlPanel-v1-01','Interoperability','(ICSCP_ControlPanelInterface)',4,null,null,null,'Control Panel interface announcement');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (190,'IOP_ControlPanel-v1-02','Interoperability','(ICSCP_ControlPanelInterface)',4,null,null,null,'Retrieving widgets parameters values');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (191,'IOP_ControlPanel-v1-03','Interoperability','(ICSCP_ControlPanelInterface)',4,null,null,null,'Control Panel interface use of widgets');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (192,'IOP_Config-v1-01','Interoperability','(ICSCF_ConfigurationInterface)',5,null,null,null,'Config interface announcement');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (193,'IOP_Config-v1-02','Interoperability','(ICSCF_ConfigurationInterface)',5,null,null,null,'Get Configuration');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (194,'IOP_Config-v1-03','Interoperability','(ICSCF_ConfigurationInterface)',5,null,null,null,'Update DUT DeviceName');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (195,'IOP_Config-v1-04','Interoperability','(ICSCF_ConfigurationInterface)',5,null,null,null,'Update DUT DefaultLanguage');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (196,'IOP_Config-v1-05','Interoperability','(ICSCF_FactoryResetMethod)',5,null,null,null,'Perform DUT FactoryReset');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (197,'IOP_Config-v1-06','Interoperability','(ICSCF_ConfigurationInterface)',5,null,null,null,'Perform DUT Restart');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (198,'IOP_Config-v1-08','Interoperability','(ICSCF_ConfigurationInterface)',5,null,null,null,'Reset Configuration');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (199,'IOP_Config-v1-09','Interoperability','(ICSCF_ConfigurationInterface)',5,null,null,null,'Modify Passcode');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (200,'IOP_LSF_Controller-v1-01','Interoperability','(ICSL_LampServiceInterface)',6,null,null,null,'Switching on/off the DUT lamp');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (201,'IOP_LSF_Lamp-v1-02','Interoperability','(ICSL_LampDetailsInterface)',6,null,null,null,'Providing lamp details');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (202,'IOP_LSF_Lamp-v1-03','Interoperability','(ICSL_Color)',6,null,null,null,'Modify lamp color');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (203,'IOP_LSF_Lamp-v1-04','Interoperability','(ICSL_Dimmable)',6,null,null,null,'Modify lamp saturation');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (204,'IOP_LSF_Lamp-v1-05','Interoperability','(ICSL_ColorTemperature)',6,null,null,null,'Modify color temperature of a lamp');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (205,'IOP_LSF_Lamp-v1-06','Interoperability','(ICSL_Dimmable)',6,null,null,null,'Modify lamp brightness');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (206,'IOP_LSF_Lamp-v1-07','Interoperability','(ICSL_Color)|(ICSL_Dimmable)|(ICSL_ColorTemperature)',6,null,null,null,'Modify lamp parameters in a multi-lamp environment, joining an existing group');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (207,'IOP_LSF_Lamp-v1-08','Interoperability','(ICSL_Color)|(ICSL_Dimmable)|(ICSL_ColorTemperature)',6,null,null,null,'Modify lamp parameters in a multi-lamp environment, other lamps joining the group');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (208,'IOP_LSF_Lamp-v1-09','Interoperability','(ICSL_LampServiceInterface)',6,null,null,null,'Behaviour after switching on and off');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (209,'IOP_LSF_Lamp-v1-10','Interoperability','(ICSL_Effects)',6,null,null,null,'Pulse effects');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (210,'IOP_LSF_Lamp-v1-11','Interoperability','(ICSL_Effects)',6,null,null,null,'Transition effects');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (211,'IOP_LSF_Lamp-v1-12','Interoperability','(ICSL_Effects)',6,null,null,null,'Simultaneous effects');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (212,'IOP_LSF_Lamp-v1-13','Interoperability','(ICSL_LampServiceInterface)',6,null,null,null,'Handling lighting scenes');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (213,'IOP_AudioSink-v1-01','Interoperability','(ICSAU_PortAudioSinkInterface)',7,null,null,null,'Audio media types');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (214,'IOP_AudioSink-v1-02','Interoperability','(ICSAU_PortAudioSinkInterface)',7,null,null,null,'Synchronized audio playback on one sink');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (215,'IOP_AudioSink-v1-03','Interoperability','(ICSAU_PortAudioSinkInterface)',7,null,null,null,'Synchronized audio playback on several sinks');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (216,'IOP_AudioSink-v1-04','Interoperability','(ICSAU_PortAudioSinkInterface)',7,null,null,null,'Pausing playback');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (217,'IOP_AudioSink-v1-05','Interoperability','(ICSAU_PortAudioSinkInterface)',7,null,null,null,'Stopping (flushing) playback');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (218,'IOP_AudioSink-v1-06','Interoperability','(ICSAU_VolumeControlEnabled)',7,null,null,null,'Setting mute state');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (219,'IOP_AudioSink-v1-07','Interoperability','(ICSAU_VolumeControlEnabled)',7,null,null,null,'Volume control');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (220,'IOP_AudioSource-v1-01','Interoperability','(ICSAU_PortAudioSourceInterface)',7,null,null,null,'Getting audio media types');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (221,'IOP_AudioSource-v1-02','Interoperability','(ICSAU_PortAudioSourceInterface)',7,null,null,null,'Command audio playback on one sink');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (222,'IOP_AudioSource-v1-03','Interoperability','(ICSAU_PortAudioSourceInterface)',7,null,null,null,'Command audio playback on several sinks');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (223,'IOP_AudioSource-v1-04','Interoperability','(ICSAU_PortAudioSourceInterface)',7,null,null,null,'Command playback pause');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (224,'IOP_AudioSource-v1-05','Interoperability','(ICSAU_PortAudioSourceInterface)',7,null,null,null,'Command playback stop');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (225,'IOP_AudioSource-v1-06','Interoperability','(ICSAU_PortAudioSourceInterface)',7,null,null,null,'Command mute state');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (226,'IOP_AudioSource-v1-07','Interoperability','(ICSAU_PortAudioSourceInterface)',7,null,null,null,'Command volume control');


INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (1,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (1,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (2,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (2,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (3,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (3,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (4,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (4,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (5,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (5,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (6,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (6,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (7,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (7,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (8,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (8,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (9,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (9,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (10,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (10,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (11,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (11,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (12,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (12,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (13,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (13,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (14,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (14,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (15,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (15,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (16,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (16,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (17,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (17,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (18,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (18,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (19,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (19,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (20,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (20,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (21,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (21,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (22,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (22,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (23,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (23,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (24,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (24,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (25,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (25,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (26,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (26,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (27,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (27,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (28,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (28,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (29,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (29,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (30,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (30,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (31,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (31,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (32,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (32,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (34,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (34,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (35,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (35,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (36,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (36,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (37,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (37,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (38,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (38,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (39,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (39,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (40,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (40,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (41,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (41,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (42,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (42,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (43,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (43,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (44,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (44,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (45,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (45,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (46,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (46,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (47,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (47,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (48,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (48,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (49,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (49,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (50,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (50,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (51,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (51,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (52,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (52,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (53,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (53,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (54,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (54,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (55,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (55,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (56,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (56,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (57,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (57,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (58,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (58,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (59,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (59,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (60,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (60,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (61,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (61,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (62,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (62,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (63,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (63,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (64,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (64,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (65,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (65,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (66,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (66,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (67,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (68,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (69,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (69,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (70,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (70,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (71,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (71,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (72,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (72,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (73,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (73,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (74,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (74,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (75,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (75,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (76,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (76,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (77,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (77,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (78,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (78,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (79,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (79,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (80,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (80,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (81,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (81,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (82,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (82,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (83,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (83,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (84,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (84,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (85,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (85,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (86,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (86,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (87,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (87,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (88,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (88,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (89,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (89,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (90,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (90,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (91,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (91,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (92,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (92,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (93,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (93,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (94,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (94,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (95,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (95,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (96,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (96,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (97,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (97,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (98,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (98,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (99,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (99,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (100,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (100,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (101,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (101,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (102,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (102,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (103,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (103,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (104,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (104,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (105,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (105,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (106,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (106,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (107,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (107,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (108,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (108,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (109,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (109,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (110,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (110,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (111,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (111,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (112,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (112,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (113,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (113,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (114,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (114,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (115,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (115,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (116,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (116,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (117,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (117,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (118,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (118,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (119,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (119,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (120,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (120,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (121,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (121,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (122,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (122,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (123,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (123,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (124,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (124,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (125,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (125,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (126,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (126,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (127,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (127,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (128,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (128,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (129,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (129,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (130,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (130,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (131,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (131,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (132,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (132,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (133,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (133,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (134,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (134,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (135,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (135,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (136,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (136,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (137,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (137,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (138,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (138,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (139,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (139,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (140,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (140,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (141,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (141,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (142,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (142,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (143,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (143,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (144,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (144,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (145,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (145,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (146,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (146,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (147,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (147,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (148,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (148,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (149,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (149,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (150,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (150,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (151,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (151,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (152,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (152,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (153,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (153,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (154,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (154,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (155,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (155,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (156,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (156,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (157,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (157,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (158,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (158,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (159,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (159,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (160,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (160,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (161,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (161,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (162,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (162,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (163,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (163,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (164,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (164,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (165,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (165,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (166,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (166,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (167,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (167,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (168,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (168,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (169,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (169,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (170,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (170,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (171,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (171,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (172,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (172,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (173,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (173,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (174,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (174,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (175,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (175,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (176,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (176,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (177,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (177,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (178,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (178,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (179,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (179,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (180,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (180,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (181,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (181,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (182,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (182,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (183,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (183,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (184,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (184,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (185,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (185,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (186,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (186,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (187,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (187,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (188,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (188,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (189,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (189,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (190,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (190,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (191,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (191,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (192,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (192,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (193,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (193,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (194,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (194,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (195,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (195,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (196,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (196,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (197,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (197,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (198,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (198,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (199,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (199,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (200,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (200,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (201,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (201,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (202,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (202,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (203,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (203,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (204,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (204,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (205,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (205,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (206,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (206,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (207,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (207,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (208,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (208,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (209,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (209,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (210,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (210,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (211,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (211,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (212,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (212,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (213,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (213,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (214,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (214,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (215,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (215,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (216,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (216,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (217,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (217,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (218,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (218,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (219,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (219,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (220,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (220,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (221,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (221,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (222,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (222,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (223,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (223,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (224,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (224,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (225,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (225,2);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (226,1);
INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (226,2);

INSERT INTO users (username,password,enabled) VALUES ('Administrator','allseenadmin',1);
INSERT INTO user_roles (username,ROLE) VALUES ('Administrator','ROLE_ADMIN');

INSERT INTO users (username,password,enabled) VALUES ('root','roottesting',1);
INSERT INTO user_roles (username,ROLE) VALUES ('root','ROLE_USER');

INSERT INTO tccl (id_tccl,name,created_date,modified_date,id_certrel,num_tc) VALUES (1,'TCCL_14.12.00a_v0.1',{ts '2015-04-17 13:35:47.'},{ts '2015-04-17 13:40:24.'},3,223);
INSERT INTO tccl (id_tccl,name,created_date,modified_date,id_certrel,num_tc) VALUES (2,'TCCL_14.06.00a_v0.1',{ts '2015-04-17 13:39:32.'},{ts '2015-04-17 13:39:32.'},2,225);

INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,1);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,2);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,3);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,4);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,5);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,6);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,7);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,8);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,9);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,10);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,11);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'P',0,12);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,13);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,14);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,15);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,16);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,17);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,18);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,19);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,20);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,21);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'D',0,22);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,23);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,24);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,25);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,26);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,27);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,28);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,29);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,30);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,31);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,32);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,34);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,35);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,36);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,37);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,38);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,39);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,40);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,41);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,42);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,43);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,44);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,45);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,46);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,47);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,48);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,49);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,50);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,51);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,52);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,53);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,54);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,55);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,56);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,57);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,58);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,59);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,60);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,61);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,62);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,63);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,64);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,65);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,66);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,69);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,70);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,71);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,72);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,73);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,74);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,75);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,76);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,77);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,78);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,79);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,80);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,81);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,82);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,83);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,84);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,85);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,86);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,87);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,88);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,89);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,90);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,91);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,92);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,93);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,94);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,95);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,96);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,97);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,98);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,99);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,100);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,101);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,102);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,103);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,104);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,105);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,106);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,107);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,108);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,109);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,110);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,111);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,112);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,113);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,114);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,115);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,116);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,117);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,118);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,119);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,120);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,121);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,122);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,123);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,124);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,125);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,126);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,127);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'P',0,128);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'P',0,129);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'P',0,130);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'P',0,131);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'P',0,132);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'P',0,133);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'P',0,134);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'P',0,135);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'P',0,136);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'P',0,137);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'P',0,138);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'P',0,139);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'P',0,140);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,141);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,142);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,143);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,144);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,145);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,146);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,147);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,148);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,149);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,150);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,151);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,152);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,153);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,154);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,155);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,156);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,157);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,158);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,159);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,160);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,161);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,162);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,163);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,164);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,165);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,166);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,167);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,168);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,169);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,170);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,171);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,172);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,173);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,174);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,175);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,176);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,177);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,178);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,179);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,180);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,181);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,182);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,183);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,184);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,185);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,186);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,187);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,188);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,189);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,190);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,191);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,192);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,193);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,194);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,195);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,196);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,197);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,198);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,199);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,200);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,201);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,202);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,203);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,204);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,205);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,206);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,207);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,208);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,209);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,210);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,211);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,212);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,213);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,214);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,215);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,216);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,217);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,218);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,219);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,220);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,221);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,222);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,223);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,224);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,225);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (1,'A',1,226);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,1);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,2);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,3);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,4);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,5);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,6);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,7);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,8);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,9);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,10);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,11);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,12);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,13);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,14);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,15);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,16);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,17);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,18);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,19);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,20);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,21);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'D',0,22);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,23);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,24);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,25);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,26);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,27);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,28);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,29);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,30);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,31);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,32);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,34);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,35);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,36);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,37);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,38);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,39);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,40);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,41);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,42);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,43);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,44);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,45);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,46);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,47);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,48);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,49);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,50);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,51);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,52);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,53);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,54);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,55);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,56);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,57);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,58);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,59);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,60);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,61);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,62);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,63);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,64);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,65);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,66);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,67);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,68);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,69);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,70);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,71);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,72);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,73);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,74);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,75);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,76);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,77);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,78);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,79);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,80);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,81);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,82);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,83);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,84);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,85);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,86);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,87);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,88);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,89);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,90);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,91);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,92);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,93);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,94);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,95);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,96);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,97);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,98);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,99);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,100);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,101);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,102);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,103);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,104);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,105);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,106);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,107);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,108);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,109);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,110);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,111);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,112);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,113);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,114);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,115);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,116);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,117);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,118);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,119);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,120);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,121);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,122);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,123);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,124);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,125);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,126);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,127);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,128);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,129);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,130);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,131);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,132);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,133);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,134);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,135);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,136);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,137);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,138);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,139);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,140);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,141);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,142);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,143);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,144);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,145);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,146);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,147);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,148);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,149);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,150);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,151);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,152);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,153);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,154);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,155);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,156);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,157);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,158);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,159);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,160);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,161);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,162);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,163);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,164);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,165);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,166);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,167);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,168);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,169);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,170);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,171);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,172);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'P',0,173);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,174);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,175);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,176);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,177);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,178);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,179);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,180);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,181);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,182);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,183);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,184);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,185);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,186);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,187);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,188);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,189);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,190);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,191);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,192);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,193);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,194);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,195);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,196);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,197);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,198);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,199);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,200);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,201);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,202);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,203);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,204);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,205);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,206);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,207);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,208);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,209);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,210);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,211);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,212);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,213);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,214);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,215);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,216);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,217);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,218);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,219);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,220);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,221);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,222);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,223);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,224);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,225);
INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (2,'A',1,226);
