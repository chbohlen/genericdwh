CREATE DATABASE  IF NOT EXISTS "genericdwh" /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `genericdwh`;
-- MySQL dump 10.13  Distrib 5.6.17, for Win32 (x86)
--
-- Host: localhost    Database: genericdwh
-- ------------------------------------------------------
-- Server version	5.6.19-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `dimension_categories`
--

DROP TABLE IF EXISTS `dimension_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dimension_categories` (
  `category_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `category_id` (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dimension_categories`
--

LOCK TABLES `dimension_categories` WRITE;
/*!40000 ALTER TABLE `dimension_categories` DISABLE KEYS */;
INSERT INTO `dimension_categories` VALUES (1,'Zeit'),(2,'Ort'),(3,'Mitarbeiter');
/*!40000 ALTER TABLE `dimension_categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dimension_combinations`
--

DROP TABLE IF EXISTS `dimension_combinations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dimension_combinations` (
  `aggregate_id` bigint(20) NOT NULL,
  `component_id` bigint(20) NOT NULL,
  PRIMARY KEY (`aggregate_id`,`component_id`),
  KEY `component_id` (`component_id`),
  CONSTRAINT `dimension_combinations_ibfk_1` FOREIGN KEY (`aggregate_id`) REFERENCES `dimensions` (`dimension_id`),
  CONSTRAINT `dimension_combinations_ibfk_2` FOREIGN KEY (`component_id`) REFERENCES `dimensions` (`dimension_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dimension_combinations`
--

LOCK TABLES `dimension_combinations` WRITE;
/*!40000 ALTER TABLE `dimension_combinations` DISABLE KEYS */;
INSERT INTO `dimension_combinations` VALUES (7,1),(8,2),(7,5),(8,5),(7,6),(8,6);
/*!40000 ALTER TABLE `dimension_combinations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dimension_hierarchies`
--

DROP TABLE IF EXISTS `dimension_hierarchies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dimension_hierarchies` (
  `parent_id` bigint(20) NOT NULL,
  `child_id` bigint(20) NOT NULL,
  PRIMARY KEY (`parent_id`,`child_id`),
  KEY `child_id` (`child_id`),
  CONSTRAINT `dimension_hierarchies_ibfk_1` FOREIGN KEY (`parent_id`) REFERENCES `dimensions` (`dimension_id`),
  CONSTRAINT `dimension_hierarchies_ibfk_2` FOREIGN KEY (`child_id`) REFERENCES `dimensions` (`dimension_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dimension_hierarchies`
--

LOCK TABLES `dimension_hierarchies` WRITE;
/*!40000 ALTER TABLE `dimension_hierarchies` DISABLE KEYS */;
INSERT INTO `dimension_hierarchies` VALUES (1,2),(2,3),(4,5);
/*!40000 ALTER TABLE `dimension_hierarchies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dimensions`
--

DROP TABLE IF EXISTS `dimensions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dimensions` (
  `dimension_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `category_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`dimension_id`),
  UNIQUE KEY `dimension_id` (`dimension_id`),
  KEY `category_id` (`category_id`),
  CONSTRAINT `dimensions_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `dimension_categories` (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dimensions`
--

LOCK TABLES `dimensions` WRITE;
/*!40000 ALTER TABLE `dimensions` DISABLE KEYS */;
INSERT INTO `dimensions` VALUES (1,'Jahre',1),(2,'Monate',1),(3,'Tage',1),(4,'Kontinente',2),(5,'Länder',2),(6,'Angestellte',3),(7,'Angestellte.Länder.Jahre',NULL),(8,'Angestelle.Länder.Monate',NULL);
/*!40000 ALTER TABLE `dimensions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fact_units`
--

DROP TABLE IF EXISTS `fact_units`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fact_units` (
  `unit_id` bigint(20) NOT NULL,
  `unit_name` varchar(255) NOT NULL,
  `unit_symbol` varchar(25) NOT NULL,
  PRIMARY KEY (`unit_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fact_units`
--

LOCK TABLES `fact_units` WRITE;
/*!40000 ALTER TABLE `fact_units` DISABLE KEYS */;
INSERT INTO `fact_units` VALUES (1,'Euro','€'),(2,'Stück','St.');
/*!40000 ALTER TABLE `fact_units` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `facts`
--

DROP TABLE IF EXISTS `facts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `facts` (
  `ratio_id` bigint(20) NOT NULL,
  `reference_object_id` bigint(20) NOT NULL,
  `value` double NOT NULL,
  `unit_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ratio_id`,`reference_object_id`),
  KEY `reference_object_id` (`reference_object_id`),
  KEY `unit_id_idx` (`unit_id`),
  CONSTRAINT `facts_ibfk_1` FOREIGN KEY (`ratio_id`) REFERENCES `ratios` (`ratio_id`),
  CONSTRAINT `facts_ibfk_2` FOREIGN KEY (`reference_object_id`) REFERENCES `reference_objects` (`reference_object_id`),
  CONSTRAINT `unit_id` FOREIGN KEY (`unit_id`) REFERENCES `fact_units` (`unit_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `facts`
--

LOCK TABLES `facts` WRITE;
/*!40000 ALTER TABLE `facts` DISABLE KEYS */;
INSERT INTO `facts` VALUES (2,67,3200,1),(2,68,1200,1),(2,69,5090,1),(2,70,3900,1),(2,71,1700,1),(2,72,3600,1),(2,73,2220,1),(2,74,1700,1),(2,75,1800,1),(2,76,6000,1),(2,77,4590,1),(2,78,4250,1),(2,79,5780,1),(2,80,5100,1),(2,81,3500,1),(2,82,100,1),(2,83,230,1),(2,84,220,1),(2,85,225,1),(2,86,190,1),(2,87,350,1),(2,88,345,1),(2,89,380,1),(2,90,380,1),(2,91,310,1),(2,92,270,1),(2,93,200,1);
/*!40000 ALTER TABLE `facts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ratio_categories`
--

DROP TABLE IF EXISTS `ratio_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ratio_categories` (
  `category_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `category_id` (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ratio_categories`
--

LOCK TABLES `ratio_categories` WRITE;
/*!40000 ALTER TABLE `ratio_categories` DISABLE KEYS */;
INSERT INTO `ratio_categories` VALUES (1,'Erfolgskennzahlen');
/*!40000 ALTER TABLE `ratio_categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ratio_relations`
--

DROP TABLE IF EXISTS `ratio_relations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ratio_relations` (
  `dependent_id` bigint(20) NOT NULL,
  `dependency_id` bigint(20) NOT NULL,
  PRIMARY KEY (`dependent_id`,`dependency_id`),
  KEY `dependency_id` (`dependency_id`),
  CONSTRAINT `ratio_relations_ibfk_1` FOREIGN KEY (`dependent_id`) REFERENCES `ratios` (`ratio_id`),
  CONSTRAINT `ratio_relations_ibfk_2` FOREIGN KEY (`dependency_id`) REFERENCES `ratios` (`ratio_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ratio_relations`
--

LOCK TABLES `ratio_relations` WRITE;
/*!40000 ALTER TABLE `ratio_relations` DISABLE KEYS */;
INSERT INTO `ratio_relations` VALUES (1,2),(1,3);
/*!40000 ALTER TABLE `ratio_relations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ratios`
--

DROP TABLE IF EXISTS `ratios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ratios` (
  `ratio_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `category_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ratio_id`),
  KEY `category_id` (`category_id`),
  CONSTRAINT `ratios_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `ratio_categories` (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ratios`
--

LOCK TABLES `ratios` WRITE;
/*!40000 ALTER TABLE `ratios` DISABLE KEYS */;
INSERT INTO `ratios` VALUES (1,'Gewinn',1),(2,'Umsatz',1),(3,'Kosten',1);
/*!40000 ALTER TABLE `ratios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reference_object_combinations`
--

DROP TABLE IF EXISTS `reference_object_combinations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reference_object_combinations` (
  `aggregate_id` bigint(20) NOT NULL,
  `component_id` bigint(20) NOT NULL,
  PRIMARY KEY (`aggregate_id`,`component_id`),
  KEY `component_id` (`component_id`),
  CONSTRAINT `reference_object_combinations_ibfk_1` FOREIGN KEY (`aggregate_id`) REFERENCES `reference_objects` (`reference_object_id`),
  CONSTRAINT `reference_object_combinations_ibfk_2` FOREIGN KEY (`component_id`) REFERENCES `reference_objects` (`reference_object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reference_object_combinations`
--

LOCK TABLES `reference_object_combinations` WRITE;
/*!40000 ALTER TABLE `reference_object_combinations` DISABLE KEYS */;
INSERT INTO `reference_object_combinations` VALUES (67,12),(70,12),(73,12),(76,12),(79,12),(68,13),(71,13),(74,13),(77,13),(80,13),(69,14),(72,14),(75,14),(78,14),(81,14),(82,15),(83,16),(84,17),(85,18),(86,19),(87,20),(88,21),(89,22),(90,23),(91,24),(92,25),(93,26),(67,59),(68,59),(69,59),(70,59),(71,59),(72,59),(82,59),(83,59),(84,59),(85,59),(86,59),(87,59),(88,59),(89,59),(90,59),(91,59),(92,59),(93,59),(73,60),(74,60),(75,60),(76,61),(77,61),(78,61),(79,61),(80,61),(81,61),(67,62),(68,62),(69,62),(82,62),(83,62),(84,62),(85,62),(86,62),(87,62),(88,62),(89,62),(90,62),(91,62),(92,62),(93,62),(70,63),(71,63),(72,63),(73,64),(74,64),(75,64),(76,65),(77,65),(78,65),(79,66),(80,66),(81,66);
/*!40000 ALTER TABLE `reference_object_combinations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reference_object_hierarchies`
--

DROP TABLE IF EXISTS `reference_object_hierarchies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reference_object_hierarchies` (
  `parent_id` bigint(20) NOT NULL,
  `child_id` bigint(20) NOT NULL,
  PRIMARY KEY (`parent_id`,`child_id`),
  KEY `child_id` (`child_id`),
  CONSTRAINT `reference_object_hierarchies_ibfk_1` FOREIGN KEY (`parent_id`) REFERENCES `reference_objects` (`reference_object_id`),
  CONSTRAINT `reference_object_hierarchies_ibfk_2` FOREIGN KEY (`child_id`) REFERENCES `reference_objects` (`reference_object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reference_object_hierarchies`
--

LOCK TABLES `reference_object_hierarchies` WRITE;
/*!40000 ALTER TABLE `reference_object_hierarchies` DISABLE KEYS */;
INSERT INTO `reference_object_hierarchies` VALUES (12,16),(12,17),(12,18),(12,19),(12,20),(12,21),(12,22),(12,23),(12,24),(12,25),(12,26),(15,27),(15,28),(15,29),(15,30),(15,31),(15,32),(15,33),(15,34),(15,35),(15,36),(15,37),(15,38),(15,39),(15,40),(15,41),(15,42),(15,43),(15,44),(15,45),(15,46),(15,47),(15,48),(15,49),(15,50),(15,51),(15,52),(15,53),(15,54),(15,55),(15,56),(15,57),(58,59),(58,60),(58,61);
/*!40000 ALTER TABLE `reference_object_hierarchies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reference_objects`
--

DROP TABLE IF EXISTS `reference_objects`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reference_objects` (
  `reference_object_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dimension_id` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`reference_object_id`),
  UNIQUE KEY `reference_object_id` (`reference_object_id`),
  KEY `dimension_id` (`dimension_id`),
  CONSTRAINT `reference_objects_ibfk_1` FOREIGN KEY (`dimension_id`) REFERENCES `dimensions` (`dimension_id`)
) ENGINE=InnoDB AUTO_INCREMENT=95 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reference_objects`
--

LOCK TABLES `reference_objects` WRITE;
/*!40000 ALTER TABLE `reference_objects` DISABLE KEYS */;
INSERT INTO `reference_objects` VALUES (12,1,'2014'),(13,1,'2013'),(14,1,'2012'),(15,2,'Januar 2014'),(16,2,'Februar 2014'),(17,2,'März 2014'),(18,2,'April 2014'),(19,2,'Mai 2014'),(20,2,'Juni 2014'),(21,2,'Juli 2014'),(22,2,'August 2014'),(23,2,'September 2014'),(24,2,'Oktober 2014'),(25,2,'November 2014'),(26,2,'Dezember 2014'),(27,3,'1. Januar 2014'),(28,3,'2. Januar 2014'),(29,3,'3. Januar 2014'),(30,3,'4. Januar 2014'),(31,3,'5. Januar 2014'),(32,3,'6. Januar 2014'),(33,3,'7. Januar 2014'),(34,3,'8. Januar 2014'),(35,3,'9. Januar 2014'),(36,3,'10. Januar 2014'),(37,3,'11. Januar 2014'),(38,3,'12. Januar 2014'),(39,3,'13. Januar 2014'),(40,3,'14. Januar 2014'),(41,3,'15. Januar 2014'),(42,3,'16. Januar 2014'),(43,3,'17. Januar 2014'),(44,3,'18. Januar 2014'),(45,3,'19. Januar 2014'),(46,3,'20. Januar 2014'),(47,3,'21. Januar 2014'),(48,3,'22. Januar 2014'),(49,3,'23. Januar 2014'),(50,3,'24. Januar 2014'),(51,3,'25. Januar 2014'),(52,3,'26. Januar 2014'),(53,3,'27. Januar 2014'),(54,3,'28. Januar 2014'),(55,3,'29. Januar 2014'),(56,3,'30. Januar 2014'),(57,3,'31. Januar 2014'),(58,4,'Europa'),(59,5,'Deutschland'),(60,5,'Österreich'),(61,5,'Schweiz'),(62,6,'Schmidt'),(63,6,'Becker'),(64,6,'Bauer'),(65,6,'Müller'),(66,6,'Mayer'),(67,7,'Schmidt.Deutschland.2014'),(68,7,'Schmidt.Deutschland.2013'),(69,7,'Schmidt.Deutschland.2012'),(70,7,'Becker.Deutschland.2014'),(71,7,'Becker.Deutschland.2013'),(72,7,'Becker.Deutschland.2012'),(73,7,'Bauer.Österreich.2014'),(74,7,'Bauer.Österreich.2013'),(75,7,'Bauer.Österreich.2012'),(76,7,'Müller.Schweiz.2014'),(77,7,'Müller.Schweiz.2013'),(78,7,'Müller.Schweiz.2012'),(79,7,'Mayer.Schweiz.2014'),(80,7,'Mayer.Schweiz.2013'),(81,7,'Mayer.Schweiz.2012'),(82,8,'Schmidt.Deutschland.Januar 2014'),(83,8,'Schmidt.Deutschland.Februar 2014'),(84,8,'Schmidt.Deutschland.März 2014'),(85,8,'Schmidt.Deutschland.April 2014'),(86,8,'Schmidt.Deutschland.Mai 2014'),(87,8,'Schmidt.Deutschland.Juni 2014'),(88,8,'Schmidt.Deutschland.Juli 2014'),(89,8,'Schmidt.Deutschland.August 2014'),(90,8,'Schmidt.Deutschland.September 2014'),(91,8,'Schmidt.Deutschland.Oktober 2014'),(92,8,'Schmidt.Deutschland.November 2014'),(93,8,'Schmidt.Deutschland.Dezember 2014');
/*!40000 ALTER TABLE `reference_objects` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-09-01  5:03:37