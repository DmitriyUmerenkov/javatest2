SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

USE `admtestdb` ;

DROP TABLE IF EXISTS `admtestdb`.`traffic` ;

CREATE TABLE IF NOT EXISTS `admtestdb`.`traffic` (
  `date` DATETIME NOT NULL,
  `customer_id` BIGINT(8) NOT NULL,
  `uplink` INT NULL,
  `downlink` INT NULL,
  PRIMARY KEY (`date`, `customer_id`))

