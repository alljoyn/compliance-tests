CREATE DATABASE allseen;
USE allseen;

CREATE TABLE `users` (
  `username` varchar(45) NOT NULL,
  `password` varchar(255) NOT NULL,
  `enabled` tinyint(4) NOT NULL DEFAULT '1',
  `aes_cipher_key` BLOB NOT NULL, 
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
  `isRelease` tinyint(4) NOT NULL DEFAULT '1',
  `description` TEXT DEFAULT null, 
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

INSERT INTO certrel (id_certrel,name,enabled,isRelease,description) VALUES (1,'v14.06.00a',1, 1, "CORE v14.04.00a, BASE v14.06.00");
INSERT INTO certrel (id_certrel,name,enabled,isRelease,description) VALUES (2,'v14.12.00',1, 1, "CORE v14.12.00, BASE v14.12.00");
INSERT INTO certrel (id_certrel,name,enabled,isRelease,description) VALUES (3,'v14.12.00a',1, 1, "CORE v14.12.00a, BASE v14.12.00");
INSERT INTO certrel (id_certrel,name,enabled,isRelease,description) VALUES (4,'v14.12.00b',1, 1, "CORE v14.12.00b, BASE v14.12.00");
INSERT INTO certrel (id_certrel,name,enabled,isRelease,description) VALUES (5,'v15.04.00',1, 1, "CORE v15.04.00, BASE v15.04.00");
INSERT INTO certrel (id_certrel,name,enabled,isRelease,description) VALUES (6,'v15.04.00a',1, 1, "CORE v15.04.00a, BASE v15.04.00");
INSERT INTO certrel (id_certrel,name,enabled,isRelease,description) VALUES (7,'v15.04.00b',1, 1, "CORE v15.04.00b, BASE v15.04.00");
INSERT INTO certrel (id_certrel,name,enabled,isRelease,description) VALUES (8,'v15.09.00',1, 1, "CORE v15.09.00, BASE v15.09.00");
INSERT INTO certrel (id_certrel,name,enabled,isRelease,description) VALUES (9,'v15.09.00a', 1, 1, "CORE v15.09.00a, BASE v15.09.00");
INSERT INTO certrel (id_certrel,name,enabled,isRelease,description) VALUES (10,'v16.04.00', 1, 1, "CORE v16.04.00, BASE v16.04.00");
INSERT INTO certrel (id_certrel,name,enabled,isRelease,description) VALUES (11,'v16.04.00a', 1, 1, "CORE v16.04.00a, BASE v16.04.00");
INSERT INTO certrel (id_certrel,name,enabled,isRelease,description) VALUES (12,'v16.10.00',1,0, "CORE v16.10.00, BASE v16.04.00");


INSERT INTO services (id_service,name,enabled) VALUES (1,'Core Interface',1);
INSERT INTO services (id_service,name,enabled) VALUES (2,'Notification',1);
INSERT INTO services (id_service,name,enabled) VALUES (3,'Onboarding',1);
INSERT INTO services (id_service,name,enabled) VALUES (4,'Control Panel',1);
INSERT INTO services (id_service,name,enabled) VALUES (5,'Configuration',1);
INSERT INTO services (id_service,name,enabled) VALUES (6,'LSF Lamp Service',1);
INSERT INTO services (id_service,name,enabled) VALUES (7,'Audio',1);
INSERT INTO services (id_service,name,enabled) VALUES (8,'Gateway',1);
INSERT INTO services (id_service,name,enabled) VALUES (9,'Smart Home',1);
INSERT INTO services (id_service,name,enabled) VALUES (10,'Time',1);
INSERT INTO services (id_service,name,enabled) VALUES (11,'LSF Lamp Controller',1);
INSERT INTO services (id_service,name,enabled) VALUES (12,'CDM',1);


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
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (88,'ICSCO_SrpKeyX',false,1,'((ICSCO_SrpKeyX)|(ICSCO_SrpLogon)|(ICSCO_EcdheNull)|(ICSCO_EcdhePsk)|(ICSCO_EcdheEcdsa)|(ICSCO_EcdheSpeke)|(ICSCO_RsaKeyX))','Support of ALLJOYN_SRP_KEYX auth mechanism');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (89,'ICSCO_SrpLogon',false,1,'((ICSCO_SrpKeyX)|(ICSCO_SrpLogon)|(ICSCO_EcdheNull)|(ICSCO_EcdhePsk)|(ICSCO_EcdheEcdsa)|(ICSCO_EcdheSpeke)|(ICSCO_RsaKeyX)|(ICSCO_PinKeyX))','Support of ALLJOYN_SRP_LOGON auth mechanism');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (90,'ICSCO_EcdheNull',false,1,'(ICSCO_Security20 & ICSCO_EcdheNull)|((!(ICSCO_Security20))&((ICSCO_SrpKeyX)|(ICSCO_SrpLogon)|(ICSCO_EcdheNull)|(ICSCO_EcdhePsk)|(ICSCO_EcdheEcdsa)|(ICSCO_EcdheSpeke)|(ICSCO_RsaKeyX)|(ICSCO_PinKeyX)))','Support of ALLJOYN_ECDHE_NULL auth mechanism');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (91,'ICSCO_EcdhePsk',false,1,'((ICSCO_SrpKeyX)|(ICSCO_SrpLogon)|(ICSCO_EcdheNull)|(ICSCO_EcdhePsk)|(ICSCO_EcdheEcdsa)|(ICSCO_EcdheSpeke)|(ICSCO_RsaKeyX)|(ICSCO_PinKeyX))','Support of ALLJOYN_ECDHE_PSK auth mechanism');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (93,'ICSCO_EcdheEcdsa',false,1,'(ICSCO_Security20 & ICSCO_EcdheEcdsa)|((!(ICSCO_Security20))&((ICSCO_SrpKeyX)|(ICSCO_SrpLogon)|(ICSCO_EcdheNull)|(ICSCO_EcdhePsk)|(ICSCO_EcdheEcdsa)|(ICSCO_EcdheSpeke)|(ICSCO_RsaKeyX)|(ICSCO_PinKeyX)))','Support of ALLJOYN_ECDHE_ECDSA auth mechanism');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (94,'ICSCO_EcdheSpeke',false,1,'((ICSCO_SrpKeyX)|(ICSCO_SrpLogon)|(ICSCO_EcdheNull)|(ICSCO_EcdhePsk)|(ICSCO_EcdheEcdsa)|(ICSCO_EcdheSpeke))','Support of ALLJOYN_ECDHE_SPEKE auth mechanism');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (95,'ICSCO_RsaKeyX',false,1,'((ICSCO_SrpKeyX)|(ICSCO_SrpLogon)|(ICSCO_EcdheNull)|(ICSCO_EcdhePsk)|(ICSCO_EcdheEcdsa)|(ICSCO_RsaKeyX)|(ICSCO_PinKeyX))','Support of ALLJOYN_RSA_KEYX auth mechanism');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (96,'ICSCO_PinKeyX',false,1,'((ICSCO_SrpKeyX)|(ICSCO_SrpLogon)|(ICSCO_EcdheNull)|(ICSCO_EcdhePsk)|(ICSCO_EcdheEcdsa)|(ICSCO_RsaKeyX)|(ICSCO_PinKeyX))','Support of ALLJOYN_PIN_KEYX auth mechanism');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (97,'ICSCO_Security20',false,1,null,'Support of Security 2.0 feature');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (98,'ICSCO_ApplicationInterface',false,1,'(ICSCO_Security20 & ICSCO_ApplicationInterface)','Support of Security.Application interface');
INSERT INTO ics (id_ics,name,value,service_group,scr_expression,description) VALUES (99,'ICSCO_ManagedApplicationInterface',false,1,'(ICSCO_Security20 & ICSCO_ManagedApplicationInterface)','Support of Security.ManagedApplication interface');


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
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (79,'IXITCO_DateOfManufacture','',1,'Date of manufacture (input format depends on web browser used)');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (80,'IXITCO_SupportUrl','',1,'Support URL (populated by the manufacturer)');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (81,'IXITCO_SrpKeyXPincode','000000',1,'Pincode to use when ALLJOYN_SRP_KEYX authentication is required');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (82,'IXITCO_SrpLogonUser','',1,'User to use when ALLJOYN_SRP_LOGON authentication is required');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (83,'IXITCO_SrpLogonPass','',1,'Pass to use when ALLJOYN_SRP_LOGON authentication is required');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (84,'IXITCO_EcdhePskPassword','000000',1,'Password to use when ALLJOYN_ECDHE_PSK authentication is required');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (85,'IXITCO_EcdheEcdsaPrivateKey','',1,'Private Key to use when ALLJOYN_ECDHE_ECDSA authentication is required');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (86,'IXITCO_EcdheEcdsaCertChain','',1,'Cert chain to use when ALLJOYN_ECDHE_ECDSA authentication is required');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (87,'IXITCO_EcdheSpekePassword','000000',1,'Password to use when ALLJOYN_ECDHE_SPEKE authentication is required');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (88,'IXITCO_SrpKeyXWrongPincode','123456',1,'Wrong pincode to test ALLJOYN_SRP_KEYX authentication');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (89,'IXITCO_SrpLogonWrongPass','wrongPass',1,'Wrong password to test ALLJOYN_SRP_LOGON authentication');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (90,'IXITCO_EcdhePskWrongPassword','123456',1,'Wrong password to use with ALLJOYN_ECDHE_PSK authentication');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (91,'IXITCO_EcdheEcdsaWrongPrivateKey','',1,'Wrong private key to use with ALLJOYN_ECDHE_ECDSA authentication');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (92,'IXITCO_EcdheEcdsaWrongCertChain','',1,'Wrong cert chain to use with ALLJOYN_ECDHE_ECDSA authentication');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (93,'IXITCO_EcdheSpekeWrongPassword','123456',1,'Wrong password to use with ALLJOYN_ECDHE_SPEKE authentication');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (94,'IXITCO_RsaKeyXPrivateKey','',1,'Private Key to use when ALLJOYN_RSA_KEYX authentication is required');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (95,'IXITCO_RsaKeyXCertX509','',1,'X.509 Certificate to use when ALLJOYN_RSA_KEYX authentication is required');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (96,'IXITCO_RsaKeyXWrongPrivateKey','',1,'Wrong private key to use with ALLJOYN_RSA_KEYX authentication');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (97,'IXITCO_RsaKeyXWrongCertX509','',1,'Wrong X.509 Certificate to use with ALLJOYN_RSA_KEYX authentication');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (98,'IXITCO_PinKeyXPincode','000000',1,'Pincode to use when ALLJOYN_PIN_KEYX authentication is required');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (99,'IXITCO_PinKeyXWrongPincode','123456',1,'Wrong pincode to test ALLJOYN_PIN_KEYX authentication');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (100,'IXITCO_ApplicationVersion','1',1,'Security.Application interface version number');
INSERT INTO ixit (id_ixit,name,value,service_group,description) VALUES (101,'IXITCO_ManagedApplicationVersion','2',1,'Security.ManagedApplication interface version number');


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
INSERT INTO parameters (id_param,name,value,description) VALUES (16, 'GPON_TimeToWaitForScanResults', '2', 'Onboarding Service Frameework time to wait for scan results in seconds');


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
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (27,'Onboarding-v1-09','Conformance','(ICSON_OnboardingInterface)&((ICSCO_SrpKeyX)|(ICSCO_EcdhePsk)|(ICSCO_EcdheSpeke)|(ICSCO_PinKeyX))',3,null,null,null,'Call onboarding method without proper authentication');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (28,'Onboarding-v1-10','Conformance','(ICSON_OnboardingInterface)&(ICSCF_ConfigurationInterface)&((ICSCO_SrpKeyX)|(ICSCO_EcdhePsk)|(ICSCO_EcdheSpeke)|(ICSCO_PinKeyX))',3,null,null,null,'Call Onboarding method after changing the passcode');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (29,'Onboarding-v1-11','Conformance','(ICSON_OnboardingInterface)&(ICSCF_FactoryResetMethod)&((ICSCO_SrpKeyX)|(ICSCO_EcdhePsk)|(ICSCO_EcdheSpeke)|(ICSCO_PinKeyX))',3,null,null,null,'Factory reset');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (30,'Onboarding-v1-12','Conformance','(ICSON_OnboardingInterface)&(ICSCF_FactoryResetMethod)&((ICSCO_SrpKeyX)|(ICSCO_EcdhePsk)|(ICSCO_EcdheSpeke)|(ICSCO_PinKeyX))',3,null,null,null,'Factory reset resets passcode');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (31,'ControlPanel-v1-01','Conformance','((ICSCP_ControlPanelInterface)&((ICSCP_ContainerInterface)|(ICSCP_SecuredContainerInterface)))',4,null,null,null,'Verify all ControlPanel bus objects');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (32,'ControlPanel-v1-02','Conformance','((ICSCP_ContainerInterface)|(ICSCP_SecuredContainerInterface))',4,null,null,null,'Verify all Container bus objects');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (33,'ControlPanel-v1-03','Conformance','(ICSCP_PropertyInterface)|(ICSCP_SecuredPropertyInterface)',4,null,null,null,'Verify all Property bus objects');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (34,'ControlPanel-v1-04','Conformance','(ICSCP_LabelPropertyInterface)',4,null,null,null,'Verify all LabelProperty bus objects');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (35,'ControlPanel-v1-05','Conformance','(ICSCP_ActionInterface)|(ICSCP_SecuredActionInterface)',4,null,null,null,'Verify all Action bus objects');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (36,'ControlPanel-v1-06','Conformance','(ICSCP_DialogInterface)|(ICSCP_SecuredDialogInterface)',4,null,null,null,'Verify all Dialog bus objects');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (37,'ControlPanel-v1-07','Conformance','(ICSCP_ListPropertyInterface)|(ICSCP_SecuredListPropertyInterface)',4,null,null,null,'Verify all ListProperty bus objects');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (38,'ControlPanel-v1-08','Conformance','(ICSCP_NotificationActionInterface)&((ICSCP_ContainerInterface)|(ICSCP_SecuredContainerInterface)|(ICSCP_DialogInterface)|(ICSCP_SecuredDialogInterface))',4,null,null,null,'Verify all NotificationAction bus objects');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (39,'ControlPanel-v1-09','Conformance','(ICSCP_HTTPControlInterface)',4,null,null,null,'Verify all HTTPControl bus objects');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (40,'ControlPanel-v1-10','Conformance','((ICSCP_SecuredContainerInterface)|(ICSCP_SecuredPropertyInterface)|(ICSCP_SecuredActionInterface)|(ICSCP_SecuredDialogInterface)|(ICSCP_SecuredListPropertyInterface))',4,null,null,null,'Verify all secured ControlPanel bus objects');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (41,'Config-v1-01','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'System App AppId equals DeviceId');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (42,'Config-v1-02','Conformance','(ICSCF_ConfigurationInterface)&((ICSCO_SrpKeyX)|(ICSCO_SrpLogon)|(ICSCO_EcdhePsk)|(ICSCO_EcdheEcdsa)|(ICSCO_EcdheSpeke)|(ICSCO_PinKeyX)|(ICSCO_RsaKeyX))',5,null,null,null,'Call a Config interface method without proper authentication');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (43,'Config-v1-04','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'GetConfigurations() method with default language');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (44,'Config-v1-05','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'GetConfigurations() method with unspecified language');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (45,'Config-v1-06','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'GetConfigurations() method for each supported language');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (46,'Config-v1-07','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'GetConfigurations() method with unsupported language');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (47,'Config-v1-08','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'UpdateConfigurations() method with a new DeviceName');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (48,'Config-v1-12','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'UpdateConfigurations() method with a DeviceName containing special characters');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (49,'Config-v1-13','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'UpdadteConfigurations() method with an unsupported language');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (50,'Config-v1-14','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'UpdateConfigurations() method with a DefaultLanguage of another language');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (51,'Config-v1-15','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'UpdateConfigurations() method for DefaultLanguage with an unsupported language');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (52,'Config-v1-16','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'UpdateConfigurations() method for DefaultLanguage with an unspecified language');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (53,'Config-v1-19','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'UpdateConfigurations() method for an invalid field');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (54,'Config-v1-20','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'ResetConfigurations() method for DeviceName');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (55,'Config-v1-21','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'ResetConfigurations() method for DefaultLanguage (at least one supported language)');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (56,'Config-v1-22','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'ResetConfigurations() method for DefaultLanguage (more than one supported language)');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (57,'Config-v1-24','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'ResetConfigurations() method with an unsupported language');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (58,'Config-v1-25','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'ResetConfigurations() method for an invalid field');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (59,'Config-v1-26','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'Restart() method');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (60,'Config-v1-27','Conformance','(ICSCF_ConfigurationInterface)',5,null,null,null,'Restart() method persists configuration changes');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (61,'Config-v1-29','Conformance','(ICSCF_ConfigurationInterface)&((ICSCO_SrpKeyX)|(ICSCO_EcdhePsk)|(ICSCO_EcdheSpeke)|(ICSCO_PinKeyX))',5,null,null,null,'SetPasscode() method with a new value');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (62,'Config-v1-30','Conformance','(ICSCF_ConfigurationInterface)&((ICSCO_SrpKeyX)|(ICSCO_EcdhePsk)|(ICSCO_EcdheSpeke)|(ICSCO_PinKeyX))',5,null,null,null,'SetPasscode() method with a one-character value');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (63,'Config-v1-31','Conformance','(ICSCF_ConfigurationInterface)&((ICSCO_SrpKeyX)|(ICSCO_EcdhePsk)|(ICSCO_EcdheSpeke)|(ICSCO_PinKeyX))',5,null,null,null,'SetPasscode() method with special characters');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (64,'Config-v1-32','Conformance','(ICSCF_ConfigurationInterface)&((ICSCO_SrpKeyX)|(ICSCO_EcdhePsk)|(ICSCO_EcdheSpeke)|(ICSCO_PinKeyX))',5,null,null,null,'Restart() method persists changed passcode');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (65,'Config-v1-33','Conformance','(ICSCF_FactoryResetMethod)',5,null,null,null,'FactoryReset() method');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (66,'Config-v1-34','Conformance','(ICSCF_FactoryResetMethod)',5,null,null,null,'FactoryReset() method clears configured data');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (67,'Config-v1-35','Conformance','(ICSCF_FactoryResetMethod)',5,null,null,null,'FactoryReset() method resets the passcode');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (68,'LSF_Lamp-v1-01','Conformance','(ICSL_LampServiceInterface)',6,null,null,null,'Service Interface Version equals 1');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (69,'LSF_Lamp-v1-02','Conformance','(ICSL_LampServiceInterface)',6,null,null,null,'Lamp Service Version equals 1');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (70,'LSF_Lamp-v1-03','Conformance','(ICSL_LampServiceInterface)',6,null,null,null,'ClearLampFault() method');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (71,'LSF_Lamp-v1-04','Conformance','(ICSL_LampStateInterface)',6,null,null,null,'SetOnOff() property');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (72,'LSF_Lamp-v1-05','Conformance','(ICSL_Color)&(ICSL_LampStateInterface)',6,null,null,null,'SetHue() property');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (73,'LSF_Lamp-v1-06','Conformance','(ICSL_Color)&(ICSL_LampStateInterface)',6,null,null,null,'SetSaturation() property');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (74,'LSF_Lamp-v1-07','Conformance','(ICSL_ColorTemperature)&(ICSL_LampStateInterface)',6,null,null,null,'SetColorTemp() property');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (75,'LSF_Lamp-v1-08','Conformance','(ICSL_Dimmable)&(ICSL_LampStateInterface)',6,null,null,null,'SetBrightness() property');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (76,'LSF_Lamp-v1-09','Conformance','(ICSL_LampStateInterface)',6,null,null,null,'TransitionLampState and verify state and signal');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (77,'LSF_Lamp-v1-10','Conformance','(ICSL_LampStateInterface)',6,null,null,null,'ApplyPulseEffect');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (78,'LSF_Lamp-v1-11','Conformance','(ICSL_LampServiceInterface)',6,null,null,null,'Service interface XML matches');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (79,'LSF_Lamp-v1-12','Conformance','(ICSL_LampParametersInterface)',6,null,null,null,'Parameters interface version equals 1');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (80,'LSF_Lamp-v1-13','Conformance','(ICSL_LampParametersInterface)',6,null,null,null,'GetEnergyUsageMilliwatts');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (81,'LSF_Lamp-v1-14','Conformance','(ICSL_LampParametersInterface)',6,null,null,null,'GetBrightnessLumens');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (82,'LSF_Lamp-v1-15','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'Details interface version equals 1');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (83,'LSF_Lamp-v1-16','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'GetMake');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (84,'LSF_Lamp-v1-17','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'GetModel');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (85,'LSF_Lamp-v1-18','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'GetType');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (86,'LSF_Lamp-v1-19','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'GetLampType');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (87,'LSF_Lamp-v1-20','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'GetLampBaseType');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (88,'LSF_Lamp-v1-21','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'GetLampBeamAngle');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (89,'LSF_Lamp-v1-22','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'GetDimmable');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (90,'LSF_Lamp-v1-23','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'GetColor');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (91,'LSF_Lamp-v1-24','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'GetVariableColorTemp');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (92,'LSF_Lamp-v1-25','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'GetLampID');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (93,'LSF_Lamp-v1-26','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'GetHasEffects');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (94,'LSF_Lamp-v1-27','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'GetMinVoltage');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (95,'LSF_Lamp-v1-28','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'GetMaxVoltage');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (96,'LSF_Lamp-v1-29','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'GetWattage');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (97,'LSF_Lamp-v1-30','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'GetIncandescentEquivalent');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (98,'LSF_Lamp-v1-31','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'GetMaxLumens');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (99,'LSF_Lamp-v1-32','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'GetMinTemperature');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (100,'LSF_Lamp-v1-33','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'GetMaxTemperature');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (101,'LSF_Lamp-v1-34','Conformance','(ICSL_LampDetailsInterface)',6,null,null,null,'GetColorRenderingIndex');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (102,'Audio-v1-01','Conformance','(ICSAU_StreamPortInterface)',7,null,null,null,'Validate Stream bus objects');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (103,'Audio-v1-02','Conformance','(ICSAU_StreamInterface)',7,null,null,null,'Opening a Stream bus object');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (104,'Audio-v1-03','Conformance','(ICSAU_StreamInterface)',7,null,null,null,'Opening and closing a Stream bus object');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (105,'Audio-v1-04','Conformance','(ICSAU_StreamInterface)',7,null,null,null,'Closing an unopened Stream bus object');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (106,'Audio-v1-05','Conformance','(ICSAU_PortAudioSinkInterface)',7,null,null,null,'Verify any AudioSink capabilities');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (107,'Audio-v1-06','Conformance','(ICSAU_PortImageSinkInterface)',7,null,null,null,'Verify any ImageSink capabilities');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (108,'Audio-v1-07','Conformance','(ICSAU_PortApplicationMetadataSinkInterface)',7,null,null,null,'Verify any Application.MetadataSink capabilities');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (109,'Audio-v1-08','Conformance','(ICSAU_PortAudioSinkInterface)',7,null,null,null,'Configure any AudioSink port');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (110,'Audio-v1-09','Conformance','(ICSAU_PortAudioSinkInterface)',7,null,null,null,'Configure any AudioSink bus object with an invalid configuration');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (111,'Audio-v1-10','Conformance','(ICSAU_PortAudioSinkInterface)',7,null,null,null,'Configure any AudioSink bus object twice');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (112,'Audio-v1-11','Conformance','(ICSAU_PortAudioSinkInterface)',7,null,null,null,'Check for OwnershipLost signal');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (113,'Audio-v1-12','Conformance','(ICSAU_PortAudioSinkInterface)',7,null,null,null,'Playback on an AudioSink');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (114,'Audio-v1-13','Conformance','(ICSAU_PortAudioSinkInterface)',7,null,null,null,'Pausing playback on an AudioSink');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (115,'Audio-v1-14','Conformance','(ICSAU_PortAudioSinkInterface)',7,null,null,null,'Flushing a paused AudioSink');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (116,'Audio-v1-15','Conformance','(ICSAU_PortAudioSinkInterface)',7,null,null,null,'Flushing a playing AudioSink');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (117,'Audio-v1-16','Conformance','(ICSAU_PortAudioSinkInterface)',7,null,null,null,'Paused AudioSink remains paused after sending data');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (118,'Audio-v1-17','Conformance','(ICSAU_PortAudioSinkInterface)',7,null,null,null,'Playing an empty AudioSink remains IDLE');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (119,'Audio-v1-18','Conformance','(ICSAU_PortAudioSinkInterface)',7,null,null,null,'Flushing an idle AudioSink');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (120,'Audio-v1-19','Conformance','(ICSAU_PortImageSinkInterface)',7,null,null,null,'Sending data to an ImageSink');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (121,'Audio-v1-20','Conformance','(ICSAU_PortApplicationMetadataSinkInterface)',7,null,null,null,'Sending data to an Application.MetadataSink');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (122,'Audio-v1-21','Conformance','(ICSAU_ControlVolumeInterface)',7,null,null,null,'Setting the mute state on an AudioSink');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (123,'Audio-v1-22','Conformance','(ICSAU_ControlVolumeInterface)',7,null,null,null,'Setting the volume on an AudioSink');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (124,'Audio-v1-23','Conformance','(ICSAU_ControlVolumeInterface)',7,null,null,null,'Setting an invalid volume on an AudioSink');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (125,'Audio-v1-24','Conformance','(ICSAU_ControlVolumeInterface)',7,null,null,null,'Independence of mute and volume on an AudioSink');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (126,'Audio-v1-25','Conformance','(ICSAU_StreamClockInterface)',7,null,null,null,'Synchronize clocks on an AudioSink');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (127,'Audio-v1-26','Conformance','(ICSAU_VolumeControlEnabled)',7,null,null,null,'Set Volume using AdjustVolume');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (128,'Audio-v1-27','Conformance','(ICSAU_VolumeControlEnabled)',7,null,null,null,'Set Volume using AdjustVolumePercent');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (129,'Audio-v1-28','Conformance','(ICSAU_VolumeControlEnabled)',7,null,null,null,'Test Volume.Enabled flag');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (130,'GWAgent-v1-01','Conformance','(ICSG_ProfileManagementInterface)&(ICSG_AppAccessInterface)&(ICSG_AppManagementInterface)',8,null,null,null,'Interfaces match definition');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (131,'SmartHome-v1-01','Conformance','(ICSSH_CentralizedManagementInterface)',9,null,null,null,'Service Interface Version equals 1');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (132,'SmartHome-v1-02','Conformance','(ICSSH_CentralizedManagementInterface)',9,null,null,null,'ApplianceRegistration()');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (133,'SmartHome-v1-03','Conformance','(ICSSH_CentralizedManagementInterface)',9,null,null,null,'Execute()');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (134,'SmartHome-v1-04','Conformance','(ICSSH_CentralizedManagementInterface)',9,null,null,null,'ApplianceUnRegistration()');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (135,'SmartHome-v1-05','Conformance','(ICSSH_CentralizedManagementInterface)',9,null,null,null,'DeviceHeartBeat()');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (136,'TimeService-v1-01','Conformance','(ICST_TimeServiceFramework)',10,null,null,null,'GetObjectDescription() consistent with the TimeService announcement');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (137,'TimeService-v1-02','Conformance','(ICST_ClockInterface)',10,null,null,null,'VerifyClocks');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (138,'TimeService-v1-03','Conformance','(ICST_TimerFactoryInterface)|(ICST_TimerInterface)',10,null,null,null,'VerifyTimers');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (139,'TimeService-v1-04','Conformance','(ICST_AlarmFactoryInterface)|(ICST_AlarmInterface)',10,null,null,null,'VerifyAlarms');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (140,'LSF_Controller-v1-01','Conformance','(ICSLC_LightingControllerServiceFramework)',11,null,null,null,'Service interface XML matches');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (141,'LSF_Controller-v1-02','Conformance','(ICSLC_ControllerServiceInterface)',11,null,null,null,'Service interface versions');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (142,'LSF_Controller-v1-03','Conformance','(ICSLC_ControllerServiceInterface)',11,null,null,null,'Reset controller service');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (143,'LSF_Controller-v1-04','Conformance','(ICSLC_ControllerServiceLampInterface)',11,null,null,null,'Lamp info');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (144,'LSF_Controller-v1-05','Conformance','(ICSLC_ControllerServiceLampInterface)',11,null,null,null,'Get and set lamp name');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (145,'LSF_Controller-v1-06','Conformance','(ICSLC_ControllerServiceLampInterface)',11,null,null,null,'Lamp details');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (146,'LSF_Controller-v1-07','Conformance','(ICSLC_ControllerServiceLampInterface)',11,null,null,null,'Lamp parameters');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (147,'LSF_Controller-v1-08','Conformance','(ICSLC_ControllerServiceLampInterface)',11,null,null,null,'Lamp state');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (148,'LSF_Controller-v1-09','Conformance','(ICSLC_ControllerServiceLampInterface)',11,null,null,null,'Lamp state transition');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (149,'LSF_Controller-v1-10','Conformance','(ICSLC_ControllerServiceLampInterface)',11,null,null,null,'Lamp state pulse');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (150,'LSF_Controller-v1-11','Conformance','(ICSLC_ControllerServiceLampInterface)',11,null,null,null,'Lamp change with preset');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (151,'LSF_Controller-v1-12','Conformance','(ICSLC_ControllerServiceLampInterface)',11,null,null,null,'Reset lamp state');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (152,'LSF_Controller-v1-13','Conformance','(ICSLC_ControllerServiceLampInterface)',11,null,null,null,'Lamp faults');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (153,'LSF_Controller-v1-14','Conformance','(ICSLC_ControllerServiceLampGroupInterface)',11,null,null,null,'Lamp group CRUD');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (154,'LSF_Controller-v1-15','Conformance','(ICSLC_ControllerServiceLampGroupInterface)',11,null,null,null,'Get and set lamp group name');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (155,'LSF_Controller-v1-16','Conformance','(ICSLC_ControllerServiceLampGroupInterface)',11,null,null,null,'Transition lamp group state');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (156,'LSF_Controller-v1-17','Conformance','(ICSLC_ControllerServiceLampGroupInterface)',11,null,null,null,'Pulse lamp group state');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (157,'LSF_Controller-v1-18','Conformance','(ICSLC_ControllerServiceLampGroupInterface)',11,null,null,null,'Reset lamp group state');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (158,'LSF_Controller-v1-19','Conformance','(ICSLC_ControllerServiceLampGroupInterface)',11,null,null,null,'Change lamp group state with presets');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (159,'LSF_Controller-v1-20','Conformance','(ICSLC_ControllerServicePresetInterface)',11,null,null,null,'Default lamp state');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (160,'LSF_Controller-v1-21','Conformance','(ICSLC_ControllerServicePresetInterface)',11,null,null,null,'Preset CRUD');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (161,'LSF_Controller-v1-22','Conformance','(ICSLC_ControllerServicePresetInterface)',11,null,null,null,'Get and set preset name');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (162,'LSF_Controller-v1-23','Conformance','(ICSLC_ControllerServiceSceneInterface)',11,null,null,null,'Create scene');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (163,'LSF_Controller-v1-24','Conformance','(ICSLC_ControllerServiceSceneInterface)',11,null,null,null,'Update and delete scene');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (164,'LSF_Controller-v1-25','Conformance','(ICSLC_ControllerServiceSceneInterface)',11,null,null,null,'Apply scene');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (165,'LSF_Controller-v1-26','Conformance','(ICSLC_ControllerServiceSceneInterface)',11,null,null,null,'Get and set scene name');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (166,'LSF_Controller-v1-27','Conformance','(ICSLC_ControllerServiceMasterSceneInterface)',11,null,null,null,'Create master scene');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (167,'LSF_Controller-v1-28','Conformance','(ICSLC_ControllerServiceMasterSceneInterface)',11,null,null,null,'Update and delete master scene');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (168,'LSF_Controller-v1-29','Conformance','(ICSLC_ControllerServiceMasterSceneInterface)',11,null,null,null,'Apply master scene');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (169,'LSF_Controller-v1-30','Conformance','(ICSLC_ControllerServiceMasterSceneInterface)',11,null,null,null,'Get and set master scene name');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (170,'LSF_Controller-v1-31','Conformance','(ICSLC_LeaderElectionAndStateSyncInterface)',11,null,null,null,'Leader election get checksum and modification timestamp');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (171,'LSF_Controller-v1-32','Conformance','(ICSLC_LeaderElectionAndStateSyncInterface)',11,null,null,null,'Leader election blob changed');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (172,'LSF_Controller-v1-33','Conformance','(ICSLC_LeaderElectionAndStateSyncInterface)',11,null,null,null,'Leader election overthrow');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (173,'IOP_About-v1-01','Interoperability','',1,null,null,null,'Device detected');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (174,'IOP_About-v1-02','Interoperability','',1,null,null,null,'Reception of About Announcement');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (175,'IOP_About-v1-03','Interoperability','',1,null,null,null,'Reception of GetAboutData information');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (176,'IOP_About-v1-04','Interoperability','(ICSCO_IconInterface)',1,null,null,null,'Support of DeviceIcon Object');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (177,'IOP_Notification-v1-01','Interoperability','(ICSN_NotificationInterface)&(ICSN_NotificationProducerInterface)',2,null,null,null,'Sending notifications');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (178,'IOP_Notification-Consumer-v1-01','Interoperability','(ICSN_NotificationInterface)&(ICSN_NotificationConsumer)',2,null,null,null,'Receiving notifications inside and outside the TTL period');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (179,'IOP_Notification-Consumer-v1-02','Interoperability','(ICSN_NotificationInterface)&(ICSN_NotificationConsumer)',2,null,null,null,'Handling different types of notification messages');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (180,'IOP_Notification-Consumer-v1-03','Interoperability','(ICSN_NotificationInterface)&(ICSN_NotificationConsumer)',2,null,null,null,'Display different languages messages');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (181,'IOP_Onboarding-v1-01','Interoperability','(ICSON_OnboardingInterface)',3,null,null,null,'Onboarding Service announcement');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (182,'IOP_Onboarding-v1-02','Interoperability','(ICSON_OnboardingInterface)',3,null,null,null,'DUT Offboarding');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (183,'IOP_Onboarding-v1-03','Interoperability','(ICSON_OnboardingInterface)',3,null,null,null,'DUT Onboarding');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (184,'IOP_Onboarding-v1-04','Interoperability','(ICSON_OnboardingInterface)',3,null,null,null,'DUT Onboarding without proper authentication');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (185,'IOP_Onboarding-v1-05','Interoperability','(ICSON_OnboardingInterface)',3,null,null,null,'DUT Onboarding with incorrect WiFi configuration data');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (186,'IOP_Onboarding-v1-06','Interoperability','(ICSON_GetScanInfoMethod)',3,null,null,null,'DUT Onboarding, use of GetScanInfo method');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (187,'IOP_Onboarding-v1-07','Interoperability','(ICSON_GetScanInfoMethod)&(ICSCF_ConfigurationInterface)',3,null,null,null,'Onboarding after changing passcode');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (188,'IOP_ControlPanel-v1-01','Interoperability','(ICSCP_ControlPanelInterface)',4,null,null,null,'Control Panel interface announcement');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (189,'IOP_ControlPanel-v1-02','Interoperability','(ICSCP_ControlPanelInterface)',4,null,null,null,'Retrieving widgets parameters values');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (190,'IOP_ControlPanel-v1-03','Interoperability','(ICSCP_ControlPanelInterface)',4,null,null,null,'Control Panel interface use of widgets');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (191,'IOP_Config-v1-01','Interoperability','(ICSCF_ConfigurationInterface)',5,null,null,null,'Config interface announcement');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (192,'IOP_Config-v1-02','Interoperability','(ICSCF_ConfigurationInterface)',5,null,null,null,'Get Configuration');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (193,'IOP_Config-v1-03','Interoperability','(ICSCF_ConfigurationInterface)',5,null,null,null,'Update DUT DeviceName');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (194,'IOP_Config-v1-04','Interoperability','(ICSCF_ConfigurationInterface)',5,null,null,null,'Update DUT DefaultLanguage');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (195,'IOP_Config-v1-05','Interoperability','(ICSCF_FactoryResetMethod)',5,null,null,null,'Perform DUT FactoryReset');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (196,'IOP_Config-v1-06','Interoperability','(ICSCF_ConfigurationInterface)',5,null,null,null,'Perform DUT Restart');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (197,'IOP_Config-v1-08','Interoperability','(ICSCF_ConfigurationInterface)',5,null,null,null,'Reset Configuration');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (198,'IOP_Config-v1-09','Interoperability','(ICSCF_ConfigurationInterface)',5,null,null,null,'Modify Passcode');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (199,'IOP_LSF_Controller-v1-01','Interoperability','(ICSL_LampServiceInterface)',6,null,null,null,'Switching on/off the DUT lamp');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (200,'IOP_LSF_Lamp-v1-02','Interoperability','(ICSL_LampDetailsInterface)',6,null,null,null,'Providing lamp details');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (201,'IOP_LSF_Lamp-v1-03','Interoperability','(ICSL_Color)',6,null,null,null,'Modify lamp color');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (202,'IOP_LSF_Lamp-v1-04','Interoperability','(ICSL_Dimmable)',6,null,null,null,'Modify lamp saturation');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (203,'IOP_LSF_Lamp-v1-05','Interoperability','(ICSL_ColorTemperature)',6,null,null,null,'Modify color temperature of a lamp');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (204,'IOP_LSF_Lamp-v1-06','Interoperability','(ICSL_Dimmable)',6,null,null,null,'Modify lamp brightness');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (205,'IOP_LSF_Lamp-v1-07','Interoperability','(ICSL_Color)|(ICSL_Dimmable)|(ICSL_ColorTemperature)',6,null,null,null,'Modify lamp parameters in a multi-lamp environment, joining an existing group');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (206,'IOP_LSF_Lamp-v1-08','Interoperability','(ICSL_Color)|(ICSL_Dimmable)|(ICSL_ColorTemperature)',6,null,null,null,'Modify lamp parameters in a multi-lamp environment, other lamps joining the group');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (207,'IOP_LSF_Lamp-v1-09','Interoperability','(ICSL_LampServiceInterface)',6,null,null,null,'Behaviour after switching on and off');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (208,'IOP_LSF_Lamp-v1-10','Interoperability','(ICSL_Effects)',6,null,null,null,'Pulse effects');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (209,'IOP_LSF_Lamp-v1-11','Interoperability','(ICSL_Effects)',6,null,null,null,'Transition effects');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (210,'IOP_LSF_Lamp-v1-12','Interoperability','(ICSL_Effects)',6,null,null,null,'Simultaneous effects');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (211,'IOP_LSF_Lamp-v1-13','Interoperability','(ICSL_LampServiceInterface)',6,null,null,null,'Handling lighting scenes');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (212,'IOP_AudioSink-v1-01','Interoperability','(ICSAU_PortAudioSinkInterface)',7,null,null,null,'Audio media types');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (213,'IOP_AudioSink-v1-02','Interoperability','(ICSAU_PortAudioSinkInterface)',7,null,null,null,'Synchronized audio playback on one sink');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (214,'IOP_AudioSink-v1-03','Interoperability','(ICSAU_PortAudioSinkInterface)',7,null,null,null,'Synchronized audio playback on several sinks');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (215,'IOP_AudioSink-v1-04','Interoperability','(ICSAU_PortAudioSinkInterface)',7,null,null,null,'Pausing playback');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (216,'IOP_AudioSink-v1-05','Interoperability','(ICSAU_PortAudioSinkInterface)',7,null,null,null,'Stopping (flushing) playback');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (217,'IOP_AudioSink-v1-06','Interoperability','(ICSAU_VolumeControlEnabled)',7,null,null,null,'Setting mute state');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (218,'IOP_AudioSink-v1-07','Interoperability','(ICSAU_VolumeControlEnabled)',7,null,null,null,'Volume control');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (219,'IOP_AudioSource-v1-01','Interoperability','(ICSAU_PortAudioSourceInterface)',7,null,null,null,'Getting audio media types');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (220,'IOP_AudioSource-v1-02','Interoperability','(ICSAU_PortAudioSourceInterface)',7,null,null,null,'Command audio playback on one sink');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (221,'IOP_AudioSource-v1-03','Interoperability','(ICSAU_PortAudioSourceInterface)',7,null,null,null,'Command audio playback on several sinks');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (222,'IOP_AudioSource-v1-04','Interoperability','(ICSAU_PortAudioSourceInterface)',7,null,null,null,'Command playback pause');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (223,'IOP_AudioSource-v1-05','Interoperability','(ICSAU_PortAudioSourceInterface)',7,null,null,null,'Command playback stop');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (224,'IOP_AudioSource-v1-06','Interoperability','(ICSAU_PortAudioSourceInterface)',7,null,null,null,'Command mute state');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (225,'IOP_AudioSource-v1-07','Interoperability','(ICSAU_PortAudioSourceInterface)',7,null,null,null,'Command volume control');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (226,'Security20-v1-01','Conformance','(ICSCO_ApplicationInterface)',1,null,null,null,'Claimable DUT emits Application.State signal');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (227,'Security20-v1-02','Conformance','(ICSCO_ApplicationInterface)',1,null,null,null,'Retrieve Security.Application properties before claiming');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (228,'Security20-v1-03','Conformance','(ICSCO_ApplicationInterface)',1,null,null,null,'Verify that Security.Application properties are read only');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (229,'Security20-v1-04','Conformance','(ICSCO_ApplicationInterface)',1,null,null,null,'Verify that DUT can be claimed successfully');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (230,'Security20-v1-05','Conformance','(ICSCO_ManagedApplicationInterface)',1,null,null,null,'Retrieve Security.ManagedApplication properties on claimed device');
INSERT INTO testcases (id_test,name,type,applicability,service_group,last_id_project,last_execution,last_result,description) VALUES (231,'Security20-v1-06','Conformance','(ICSCO_ManagedApplicationInterface)',1,null,null,null,'Verify that Security.ManagedApplication properties are read only');


INSERT INTO tccl (id_tccl,name,created_date,modified_date,id_certrel,num_tc) VALUES (1,'TCCL_14.06.00a_v0.1',{ts '2015-04-17 13:39:32.'},{ts '2015-04-17 13:39:32.'},1,225);
INSERT INTO tccl (id_tccl,name,created_date,modified_date,id_certrel,num_tc) VALUES (2,'TCCL_14.12.00_v0.1',{ts '2015-04-17 13:35:47.'},{ts '2015-04-17 13:40:24.'},2,223);
INSERT INTO tccl (id_tccl,name,created_date,modified_date,id_certrel,num_tc) VALUES (3,'TCCL_14.12.00a_v0.1',{ts '2015-04-17 13:35:47.'},{ts '2015-04-17 13:40:24.'},3,223);
INSERT INTO tccl (id_tccl,name,created_date,modified_date,id_certrel,num_tc) VALUES (4,'TCCL_14.12.00b_v0.1',{ts '2015-05-15 13:35:47.'},{ts '2015-05-15 13:40:24.'},4,223);
INSERT INTO tccl (id_tccl,name,created_date,modified_date,id_certrel,num_tc) VALUES (5,'TCCL_15.04.00_v0.1',{ts '2015-05-15 13:39:32.'},{ts '2015-05-15 13:39:32.'},5,223);
INSERT INTO tccl (id_tccl,name,created_date,modified_date,id_certrel,num_tc) VALUES (6,'TCCL_15.04.00a_v0.1',{ts '2015-09-24 15:58:32.'},{ts '2015-09-24 15:58:32.'},6,223);
INSERT INTO tccl (id_tccl,name,created_date,modified_date,id_certrel,num_tc) VALUES (7,'TCCL_15.04.00b_v0.1',{ts '2015-09-24 15:58:32.'},{ts '2015-09-24 15:58:32.'},7,223);
INSERT INTO tccl (id_tccl,name,created_date,modified_date,id_certrel,num_tc) VALUES (8,'TCCL_15.09.00_v0.1',{ts '2015-09-24 15:58:32.'},{ts '2015-09-24 15:58:32.'},8,223);
INSERT INTO tccl (id_tccl,name,created_date,modified_date,id_certrel,num_tc) VALUES (9,'TCCL_15.09.00a_v0.1',{ts '2015-12-20 15:58:32.'},{ts '2015-12-20 15:58:32.'},9,223);
INSERT INTO tccl (id_tccl,name,created_date,modified_date,id_certrel,num_tc) VALUES (10,'TCCL_16.04.00_v0.1',{ts '2016-05-02 15:58:32.'},{ts '2016-05-02 15:58:32.'},10,223);
INSERT INTO tccl (id_tccl,name,created_date,modified_date,id_certrel,num_tc) VALUES (11,'TCCL_16.04.00a_v0.1',{ts '2016-07-24 15:58:32.'},{ts '2016-07-24 15:58:32.'},11,223);

DELIMITER $$

CREATE PROCEDURE insert_tc_certrel()
BEGIN
	DECLARE crs INT;
	DECLARE tcs INT;
	DECLARE i INT DEFAULT 1;
	DECLARE j INT DEFAULT 1;
	SELECT COUNT(*) FROM certrel INTO crs;
	SELECT COUNT(*) FROM testcases INTO tcs;
	SELECT crs;
	SELECT tcs;
	WHILE (i<=crs) DO
		SELECT i;
		WHILE (j<=tcs) DO
			IF (i = 1)
				OR (((i > 1) AND (i <= 7)) AND ((j != 66) AND (j != 67) AND (j <= 225)))
				OR (((i > 7) AND (i <= 9)) AND ((j != 9) AND (j != 46) AND (j != 66) AND (j != 67) AND (j <= 225)))
				OR (((i > 9) AND (i <= 11)) AND ((j != 9) AND (j != 37) AND (j != 46) AND (j != 66) AND (j != 67) AND (j <= 225)))
				OR ((i > 11) AND ((j != 9) AND (j != 37) AND (j != 46) AND (j != 66) AND (j != 67)))
				THEN INSERT INTO testcases_certrel (id_test,id_certrel) VALUES (j,i);
			END IF;
			SET j=j+1;
		END WHILE;
		SET i=i+1;
		SET j=1;
	END WHILE;
END$$

CREATE PROCEDURE insert_tccl_testcase()
BEGIN
	DECLARE tccls INT;
	DECLARE tcs INT;
	DECLARE i INT DEFAULT 1;
	DECLARE j INT DEFAULT 1;
	DECLARE tc_type VARCHAR(1) DEFAULT 'A';
	DECLARE tc_enabled TINYINT(1) DEFAULT 1;
	SELECT COUNT(*) FROM tccl INTO tccls;
	SELECT COUNT(*) FROM testcases INTO tcs;
	SELECT tccls;
	SELECT tcs;
	WHILE (i <= tccls) DO
		SELECT i;
		WHILE (j <= tcs) DO
			IF (i = 1) 
				OR (((i > 1) AND (i <= 7)) AND ((j != 66) AND (j != 67) AND (j <= 225)))
				OR (((i > 7) AND (i <= 9)) AND ((j != 9) AND (j != 46) AND (j != 66) AND (j != 67) AND (j <= 225)))
				OR (((i > 9) AND (i <= 11)) AND ((j != 9) AND (j != 37) AND (j != 46) AND (j != 66) AND (j != 67) AND (j <= 225)))
				OR ((i > 11) AND ((j != 9) AND (j != 37) AND (j != 46) AND (j != 66) AND (j != 67)))
				THEN
				IF ((j = 12) OR ((j >= 127) AND (j <= 139)) OR ((i = 1) AND (j >= 140) AND (j <= 172))) THEN
					SET tc_type='P';
					SET tc_enabled='0';
				ELSEIF (j = 22) THEN
					SET tc_type='D';
					SET tc_enabled='0';	
				END IF;
				INSERT INTO tccl_testcase (id_tccl,type,enable,id_test) VALUES (i,tc_type,tc_enabled,j);
				SET tc_type='A';
				SET tc_enabled='1';
			END IF;
			SET j=j+1;
		END WHILE;
		SET i=i+1;
		SET j=1;
	END WHILE;
END$$ 

DELIMITER ;

CALL insert_tc_certrel();
CALL insert_tccl_testcase();