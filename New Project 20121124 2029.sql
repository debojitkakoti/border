-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version	5.5.21


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


--
-- Create schema deboj11
--

CREATE DATABASE IF NOT EXISTS deboj11;
USE deboj11;

--
-- Definition of table `t_new`
--

DROP TABLE IF EXISTS `t_new`;
CREATE TABLE `t_new` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `A` int(10) unsigned NOT NULL,
  `B` int(10) unsigned NOT NULL,
  `C` int(10) unsigned NOT NULL,
  `D` int(10) unsigned NOT NULL,
  `E` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `t_new`
--

/*!40000 ALTER TABLE `t_new` DISABLE KEYS */;
INSERT INTO `t_new` (`id`,`A`,`B`,`C`,`D`,`E`) VALUES 
 (1,0,0,1,1,1),
 (2,0,1,0,1,1),
 (3,1,1,0,0,1),
 (4,0,1,0,0,1),
 (5,0,1,0,1,1);
/*!40000 ALTER TABLE `t_new` ENABLE KEYS */;


--
-- Definition of table `t_old`
--

DROP TABLE IF EXISTS `t_old`;
CREATE TABLE `t_old` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `A` int(10) unsigned NOT NULL,
  `B` int(10) unsigned NOT NULL,
  `C` int(10) unsigned NOT NULL,
  `D` int(10) unsigned NOT NULL,
  `E` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `t_old`
--

/*!40000 ALTER TABLE `t_old` DISABLE KEYS */;
INSERT INTO `t_old` (`id`,`A`,`B`,`C`,`D`,`E`) VALUES 
 (1,1,0,1,0,1),
 (2,1,0,0,0,1),
 (3,1,0,1,0,1),
 (4,0,1,0,0,0),
 (5,0,1,0,1,0);
/*!40000 ALTER TABLE `t_old` ENABLE KEYS */;




/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
