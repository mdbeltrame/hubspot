CREATE TABLE `hubspot`.`token` (
  `codigo` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `access_token` VARCHAR(500) NULL DEFAULT NULL,
  `refresh_token` VARCHAR(500) NULL DEFAULT NULL,
  `expires_in` BIGINT(20) NULL DEFAULT NULL,
  `token_type` VARCHAR(500) NULL DEFAULT NULL,
  `client_id` VARCHAR(500) NULL DEFAULT NULL,
  `client_secret` VARCHAR(500) NULL DEFAULT NULL,
  `data_salvamento` DATE NULL DEFAULT NULL,
  `hora_salvamento` TIME NULL DEFAULT NULL,
  PRIMARY KEY (`codigo`));

