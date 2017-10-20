LOCK TABLES `payment_type` WRITE;
/*!40000 ALTER TABLE `payment_type` DISABLE KEYS */;
INSERT INTO `payment_type` VALUES ('00001','测试还款类型');
/*!40000 ALTER TABLE `payment_type` ENABLE KEYS */;
UNLOCK TABLES;