CREATE DATABASE  IF NOT EXISTS `magazzino` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `magazzino`;
-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: magazzino
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `movimento`
--

DROP TABLE IF EXISTS `movimento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movimento` (
  `IdMovimento` int NOT NULL AUTO_INCREMENT,
  `QtaProd` int NOT NULL,
  `Data` date NOT NULL,
  `TipoMovimento` varchar(10) NOT NULL,
  `IdProd` varchar(10) NOT NULL,
  `IdUtenteOperatore` varchar(50) NOT NULL,
  PRIMARY KEY (`IdMovimento`),
  KEY `IdProd` (`IdProd`),
  KEY `IdUtenteOperatore` (`IdUtenteOperatore`),
  CONSTRAINT `movimento_ibfk_1` FOREIGN KEY (`IdProd`) REFERENCES `prodotto` (`IdProd`),
  CONSTRAINT `movimento_ibfk_2` FOREIGN KEY (`IdUtenteOperatore`) REFERENCES `utente` (`IdUtente`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movimento`
--

LOCK TABLES `movimento` WRITE;
/*!40000 ALTER TABLE `movimento` DISABLE KEYS */;
INSERT INTO `movimento` VALUES (1,50,'2026-03-12','Carico','P-001','OPE-001'),(2,30,'2026-03-13','Carico','P-002','OPE-002'),(3,80,'2026-03-14','Carico','P-003','OPE-001'),(4,20,'2026-03-15','Scarico','P-001','OPE-003'),(5,10,'2026-03-16','Scarico','P-002','OPE-002'),(6,40,'2026-04-10','Carico','P-004','OPE-001'),(7,25,'2026-04-11','Carico','P-005','OPE-002'),(8,15,'2026-04-12','Scarico','P-003','OPE-003'),(9,10,'2026-04-13','Scarico','P-004','OPE-001'),(10,20,'2026-04-14','Carico','P-006','OPE-002'),(11,60,'2026-05-10','Carico','P-007','OPE-001'),(12,35,'2026-05-11','Carico','P-008','OPE-003'),(13,12,'2026-05-12','Scarico','P-005','OPE-002'),(14,18,'2026-05-13','Scarico','P-006','OPE-001'),(15,45,'2026-05-14','Carico','P-009','OPE-003'),(16,20,'2026-06-03','Carico','P-001','OPE-001'),(17,8,'2026-06-03','Scarico','P-002','OPE-002'),(18,15,'2026-06-04','Carico','P-003','OPE-003'),(19,5,'2026-06-04','Scarico','P-004','OPE-001'),(20,30,'2026-06-05','Carico','P-005','OPE-002'),(21,10,'2026-06-05','Scarico','P-006','OPE-003'),(22,25,'2026-06-06','Carico','P-007','OPE-001'),(23,7,'2026-06-06','Scarico','P-008','OPE-002'),(24,12,'2026-06-07','Carico','P-009','OPE-003'),(25,4,'2026-06-07','Scarico','P-010','OPE-001'),(26,18,'2026-06-08','Carico','P-001','OPE-002'),(27,6,'2026-06-08','Scarico','P-003','OPE-003'),(28,22,'2026-06-09','Carico','P-002','OPE-001'),(29,9,'2026-06-09','Scarico','P-004','OPE-002'),(30,10,'2026-06-09','Carico','P-009','OPE-004'),(31,5,'2026-06-09','Carico','P-009','OPE-004'),(32,12,'2026-06-09','Carico','P-009','OPE-004'),(33,30,'2026-06-09','Carico','P-009','OPE-004'),(34,8,'2026-06-09','Carico','P-009','OPE-004'),(35,5,'2026-06-09','Carico','P-009','OPE-004');
/*!40000 ALTER TABLE `movimento` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `operatore`
--

DROP TABLE IF EXISTS `operatore`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `operatore` (
  `IdUtenteOperatore` varchar(50) NOT NULL,
  PRIMARY KEY (`IdUtenteOperatore`),
  CONSTRAINT `operatore_ibfk_1` FOREIGN KEY (`IdUtenteOperatore`) REFERENCES `utente` (`IdUtente`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `operatore`
--

LOCK TABLES `operatore` WRITE;
/*!40000 ALTER TABLE `operatore` DISABLE KEYS */;
INSERT INTO `operatore` VALUES ('OPE-001'),('OPE-002'),('OPE-003'),('OPE-004');
/*!40000 ALTER TABLE `operatore` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `posizione`
--

DROP TABLE IF EXISTS `posizione`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `posizione` (
  `IdPos` varchar(50) NOT NULL,
  `Area` varchar(50) DEFAULT NULL,
  `Scaffale` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`IdPos`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `posizione`
--

LOCK TABLES `posizione` WRITE;
/*!40000 ALTER TABLE `posizione` DISABLE KEYS */;
INSERT INTO `posizione` VALUES ('POS-A1','Area A','Scaffale 1'),('POS-A2','Area A','Scaffale 2'),('POS-A3','Area A','Scaffale 3'),('POS-B1','Area B','Scaffale 1'),('POS-B2','Area B','Scaffale 2'),('POS-B3','Area B','Scaffale 3'),('POS-C1','Area C','Scaffale 1'),('POS-C2','Area C','Scaffale 2'),('POS-C3','Area C','Scaffale 3'),('POS-D1','Area D','Scaffale 1');
/*!40000 ALTER TABLE `posizione` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prodotto`
--

DROP TABLE IF EXISTS `prodotto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prodotto` (
  `IdProd` varchar(10) NOT NULL,
  `Nome` varchar(100) NOT NULL,
  `Categoria` varchar(50) NOT NULL,
  `Descrizione` text,
  `QtaDisp` int NOT NULL DEFAULT '0',
  `SogliaMinima` int DEFAULT NULL,
  `IdPos` varchar(50) DEFAULT NULL,
  `IdUtenteResponsabile` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`IdProd`),
  KEY `IdPos` (`IdPos`),
  KEY `IdUtenteResponsabile` (`IdUtenteResponsabile`),
  CONSTRAINT `prodotto_ibfk_1` FOREIGN KEY (`IdPos`) REFERENCES `posizione` (`IdPos`),
  CONSTRAINT `prodotto_ibfk_2` FOREIGN KEY (`IdUtenteResponsabile`) REFERENCES `responsabile` (`IdUtenteResponsabile`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `prodotto`
--

LOCK TABLES `prodotto` WRITE;
/*!40000 ALTER TABLE `prodotto` DISABLE KEYS */;
INSERT INTO `prodotto` VALUES ('P-001','Guanti latex','Sicurezza','Guanti monouso in lattice taglia M',20,10,'POS-A1','RESP-001'),('P-002','Nastro da imballo','Imballaggio','Nastro adesivo trasparente 50mm x 66m',10,5,'POS-A2','RESP-003'),('P-003','Scatole S','Imballaggio','Scatole cartone 20x20x15 cm',40,20,'POS-A3','RESP-001'),('P-004','Etichette adesive','Cancelleria','Etichette 70x37mm confezione 100pz',30,15,'POS-B1','RESP-001'),('P-005','Pallets 80x120','Logistica','Pallet in legno standard EUR',12,6,'POS-B2','RESP-001'),('P-006','Guanti nitrile','Sicurezza','Guanti monouso in nitrile taglia L',45,10,'POS-B3','RESP-002'),('P-007','Cutter 18mm','Utensileria','Taglierino professionale lama 18mm',12,5,'POS-C1','RESP-002'),('P-008','Fascette cavi','Elettrico','Fascette in nylon 200x3.6mm pz 100',180,20,'POS-C2','RESP-002'),('P-009','Elmetti gialli','Sicurezza','Casco protettivo EN397 colore giallo',85,8,'POS-C3','RESP-002'),('P-010','Bolla accomp.','Modulistica','Moduli bolla accompagnamento 50x3 copie',30,10,'POS-D1','RESP-002');
/*!40000 ALTER TABLE `prodotto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `responsabile`
--

DROP TABLE IF EXISTS `responsabile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `responsabile` (
  `IdUtenteResponsabile` varchar(50) NOT NULL,
  PRIMARY KEY (`IdUtenteResponsabile`),
  CONSTRAINT `responsabile_ibfk_1` FOREIGN KEY (`IdUtenteResponsabile`) REFERENCES `utente` (`IdUtente`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `responsabile`
--

LOCK TABLES `responsabile` WRITE;
/*!40000 ALTER TABLE `responsabile` DISABLE KEYS */;
INSERT INTO `responsabile` VALUES ('RESP-001'),('RESP-002'),('RESP-003');
/*!40000 ALTER TABLE `responsabile` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `utente`
--

DROP TABLE IF EXISTS `utente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `utente` (
  `IdUtente` varchar(50) NOT NULL,
  `Nome` varchar(50) NOT NULL,
  `Cognome` varchar(50) NOT NULL,
  `Email` varchar(100) NOT NULL,
  `Password` varchar(100) NOT NULL,
  `ruolo` varchar(20) NOT NULL,
  PRIMARY KEY (`IdUtente`),
  UNIQUE KEY `Email` (`Email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `utente`
--

LOCK TABLES `utente` WRITE;
/*!40000 ALTER TABLE `utente` DISABLE KEYS */;
INSERT INTO `utente` VALUES ('OPE-001','Giovanni','Verdi','g.verdi@wms.it','pass123','Operatore'),('OPE-002','Anna','Neri','a.neri@wms.it','pass123','Operatore'),('OPE-003','Carlo','Esposito','c.esposito@wms.it','pass123','Operatore'),('OPE-004','Francesco Pio','Capasso','francesco@mail.it','Francesco12','Operatore'),('RESP-001','Mario','Rossi','mario.rossi@wms.it','pass123','Responsabile'),('RESP-002','Laura','Bianchi','laura.bianchi@wms.it','pass123','Responsabile'),('RESP-003','Amedeo','Catanese Napolitano','amedeo@mail.it','Amedeo__99','Responsabile');
/*!40000 ALTER TABLE `utente` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-10 17:53:40
