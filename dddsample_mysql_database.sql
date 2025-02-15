-- MySQL dump 10.13  Distrib 8.0.40, for Linux (aarch64)
--
-- Host: localhost    Database: dddsample
-- ------------------------------------------------------
-- Server version	8.0.40-0ubuntu0.24.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cargo`
--

DROP TABLE IF EXISTS `cargo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cargo` (
  `misdirected` bit(1) DEFAULT NULL,
  `unloaded_at_dest` bit(1) DEFAULT NULL,
  `calculated_at` datetime(6) DEFAULT NULL,
  `current_voyage_id` bigint DEFAULT NULL,
  `eta` datetime(6) DEFAULT NULL,
  `id` bigint NOT NULL,
  `last_event_id` bigint DEFAULT NULL,
  `last_known_location_id` bigint DEFAULT NULL,
  `next_expected_location_id` bigint DEFAULT NULL,
  `next_expected_voyage_id` bigint DEFAULT NULL,
  `origin_id` bigint DEFAULT NULL,
  `spec_arrival_deadline` datetime(6) NOT NULL,
  `spec_destination_id` bigint DEFAULT NULL,
  `spec_origin_id` bigint DEFAULT NULL,
  `tracking_id` varchar(255) DEFAULT NULL,
  `next_expected_handling_event_type` enum('CLAIM','CUSTOMS','LOAD','RECEIVE','UNLOAD') DEFAULT NULL,
  `routing_status` enum('MISROUTED','NOT_ROUTED','ROUTED') DEFAULT NULL,
  `transport_status` enum('CLAIMED','IN_PORT','NOT_RECEIVED','ONBOARD_CARRIER','UNKNOWN') DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKy6hi5k1y54ggnvfwyqe2p8d8` (`tracking_id`),
  KEY `FKjg7tqvng4lrroi3x7k2pgcg8s` (`current_voyage_id`),
  KEY `FK9lgjt11lhhs90je4ts8vdps3h` (`last_event_id`),
  KEY `FKlevr3jshtdpkr0b0cs3x2xai8` (`last_known_location_id`),
  KEY `FK8j6s79x483hpgpow41m2jr8al` (`next_expected_location_id`),
  KEY `FKfllsyoa5okq5wj3xso3fl5bds` (`next_expected_voyage_id`),
  KEY `FKsh6x4pwr7fh8essy98rdttn3m` (`origin_id`),
  KEY `FKt96v2sqou50rrb83xb94rraih` (`spec_destination_id`),
  KEY `FKkefgnvxcwmyu13fii34934qoa` (`spec_origin_id`),
  CONSTRAINT `FK8j6s79x483hpgpow41m2jr8al` FOREIGN KEY (`next_expected_location_id`) REFERENCES `location` (`id`),
  CONSTRAINT `FK9lgjt11lhhs90je4ts8vdps3h` FOREIGN KEY (`last_event_id`) REFERENCES `handling_event` (`id`),
  CONSTRAINT `FKfllsyoa5okq5wj3xso3fl5bds` FOREIGN KEY (`next_expected_voyage_id`) REFERENCES `voyage` (`id`),
  CONSTRAINT `FKjg7tqvng4lrroi3x7k2pgcg8s` FOREIGN KEY (`current_voyage_id`) REFERENCES `voyage` (`id`),
  CONSTRAINT `FKkefgnvxcwmyu13fii34934qoa` FOREIGN KEY (`spec_origin_id`) REFERENCES `location` (`id`),
  CONSTRAINT `FKlevr3jshtdpkr0b0cs3x2xai8` FOREIGN KEY (`last_known_location_id`) REFERENCES `location` (`id`),
  CONSTRAINT `FKsh6x4pwr7fh8essy98rdttn3m` FOREIGN KEY (`origin_id`) REFERENCES `location` (`id`),
  CONSTRAINT `FKt96v2sqou50rrb83xb94rraih` FOREIGN KEY (`spec_destination_id`) REFERENCES `location` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cargo`
--

LOCK TABLES `cargo` WRITE;
/*!40000 ALTER TABLE `cargo` DISABLE KEYS */;
/*!40000 ALTER TABLE `cargo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cargo_seq`
--

DROP TABLE IF EXISTS `cargo_seq`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cargo_seq` (
  `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cargo_seq`
--

LOCK TABLES `cargo_seq` WRITE;
/*!40000 ALTER TABLE `cargo_seq` DISABLE KEYS */;
INSERT INTO `cargo_seq` VALUES (1);
/*!40000 ALTER TABLE `cargo_seq` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `carrier_movement`
--

DROP TABLE IF EXISTS `carrier_movement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `carrier_movement` (
  `arrival_location_id` bigint NOT NULL,
  `arrival_time` datetime(6) NOT NULL,
  `departure_location_id` bigint NOT NULL,
  `departure_time` datetime(6) NOT NULL,
  `id` bigint NOT NULL,
  `voyage_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKprwsa5jli519iuk0riayqriv2` (`arrival_location_id`),
  KEY `FKq0fuy9xktyi60qbt7om3ggvj6` (`departure_location_id`),
  KEY `FK19nu70gglmxo40637vykhb34j` (`voyage_id`),
  CONSTRAINT `FK19nu70gglmxo40637vykhb34j` FOREIGN KEY (`voyage_id`) REFERENCES `voyage` (`id`),
  CONSTRAINT `FKprwsa5jli519iuk0riayqriv2` FOREIGN KEY (`arrival_location_id`) REFERENCES `location` (`id`),
  CONSTRAINT `FKq0fuy9xktyi60qbt7om3ggvj6` FOREIGN KEY (`departure_location_id`) REFERENCES `location` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `carrier_movement`
--

LOCK TABLES `carrier_movement` WRITE;
/*!40000 ALTER TABLE `carrier_movement` DISABLE KEYS */;
/*!40000 ALTER TABLE `carrier_movement` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `carrier_movement_seq`
--

DROP TABLE IF EXISTS `carrier_movement_seq`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `carrier_movement_seq` (
  `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `carrier_movement_seq`
--

LOCK TABLES `carrier_movement_seq` WRITE;
/*!40000 ALTER TABLE `carrier_movement_seq` DISABLE KEYS */;
INSERT INTO `carrier_movement_seq` VALUES (1);
/*!40000 ALTER TABLE `carrier_movement_seq` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `handling_event`
--

DROP TABLE IF EXISTS `handling_event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `handling_event` (
  `cargo_id` bigint DEFAULT NULL,
  `completion_time` datetime(6) DEFAULT NULL,
  `id` bigint NOT NULL,
  `location_id` bigint DEFAULT NULL,
  `registration_time` datetime(6) DEFAULT NULL,
  `voyage_id` bigint DEFAULT NULL,
  `type` enum('CLAIM','CUSTOMS','LOAD','RECEIVE','UNLOAD') DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKt8gxdebmo4brebryyt5rwjbhs` (`cargo_id`),
  KEY `FKfou7tlvonxq8mw2l3hijids55` (`location_id`),
  KEY `FKjg6vht4yhuerxyilfag4npjpi` (`voyage_id`),
  CONSTRAINT `FKfou7tlvonxq8mw2l3hijids55` FOREIGN KEY (`location_id`) REFERENCES `location` (`id`),
  CONSTRAINT `FKjg6vht4yhuerxyilfag4npjpi` FOREIGN KEY (`voyage_id`) REFERENCES `voyage` (`id`),
  CONSTRAINT `FKt8gxdebmo4brebryyt5rwjbhs` FOREIGN KEY (`cargo_id`) REFERENCES `cargo` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `handling_event`
--

LOCK TABLES `handling_event` WRITE;
/*!40000 ALTER TABLE `handling_event` DISABLE KEYS */;
/*!40000 ALTER TABLE `handling_event` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `handling_event_seq`
--

DROP TABLE IF EXISTS `handling_event_seq`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `handling_event_seq` (
  `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `handling_event_seq`
--

LOCK TABLES `handling_event_seq` WRITE;
/*!40000 ALTER TABLE `handling_event_seq` DISABLE KEYS */;
INSERT INTO `handling_event_seq` VALUES (1);
/*!40000 ALTER TABLE `handling_event_seq` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `leg`
--

DROP TABLE IF EXISTS `leg`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `leg` (
  `cargo_id` bigint DEFAULT NULL,
  `id` bigint NOT NULL,
  `load_location_id` bigint DEFAULT NULL,
  `load_time` datetime(6) DEFAULT NULL,
  `unload_location_id` bigint DEFAULT NULL,
  `unload_time` datetime(6) DEFAULT NULL,
  `voyage_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1u5uafbfq1msjbkqxlp0afgl1` (`load_location_id`),
  KEY `FK6qhii1yfvw5kdbi6g5bb4034w` (`unload_location_id`),
  KEY `FKkfw0ujiodsdy5opctigsvvl35` (`voyage_id`),
  KEY `FKfyrfbxc9avv0joklx7jcu6c9x` (`cargo_id`),
  CONSTRAINT `FK1u5uafbfq1msjbkqxlp0afgl1` FOREIGN KEY (`load_location_id`) REFERENCES `location` (`id`),
  CONSTRAINT `FK6qhii1yfvw5kdbi6g5bb4034w` FOREIGN KEY (`unload_location_id`) REFERENCES `location` (`id`),
  CONSTRAINT `FKfyrfbxc9avv0joklx7jcu6c9x` FOREIGN KEY (`cargo_id`) REFERENCES `cargo` (`id`),
  CONSTRAINT `FKkfw0ujiodsdy5opctigsvvl35` FOREIGN KEY (`voyage_id`) REFERENCES `voyage` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `leg`
--

LOCK TABLES `leg` WRITE;
/*!40000 ALTER TABLE `leg` DISABLE KEYS */;
/*!40000 ALTER TABLE `leg` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `leg_seq`
--

DROP TABLE IF EXISTS `leg_seq`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `leg_seq` (
  `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `leg_seq`
--

LOCK TABLES `leg_seq` WRITE;
/*!40000 ALTER TABLE `leg_seq` DISABLE KEYS */;
INSERT INTO `leg_seq` VALUES (1);
/*!40000 ALTER TABLE `leg_seq` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `location`
--

DROP TABLE IF EXISTS `location`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `location` (
  `id` bigint NOT NULL,
  `name` varchar(255) NOT NULL,
  `unlocode` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKgxqy9h48v8rwidgeyn92tsnpj` (`unlocode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `location`
--

LOCK TABLES `location` WRITE;
/*!40000 ALTER TABLE `location` DISABLE KEYS */;
/*!40000 ALTER TABLE `location` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `location_seq`
--

DROP TABLE IF EXISTS `location_seq`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `location_seq` (
  `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `location_seq`
--

LOCK TABLES `location_seq` WRITE;
/*!40000 ALTER TABLE `location_seq` DISABLE KEYS */;
INSERT INTO `location_seq` VALUES (1);
/*!40000 ALTER TABLE `location_seq` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `voyage`
--

DROP TABLE IF EXISTS `voyage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `voyage` (
  `id` bigint NOT NULL,
  `voyage_number` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKiqp1nl026xcci3a9d8sc6t0o9` (`voyage_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `voyage`
--

LOCK TABLES `voyage` WRITE;
/*!40000 ALTER TABLE `voyage` DISABLE KEYS */;
/*!40000 ALTER TABLE `voyage` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `voyage_seq`
--

DROP TABLE IF EXISTS `voyage_seq`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `voyage_seq` (
  `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `voyage_seq`
--

LOCK TABLES `voyage_seq` WRITE;
/*!40000 ALTER TABLE `voyage_seq` DISABLE KEYS */;
INSERT INTO `voyage_seq` VALUES (1);
/*!40000 ALTER TABLE `voyage_seq` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-02-15 12:54:42
