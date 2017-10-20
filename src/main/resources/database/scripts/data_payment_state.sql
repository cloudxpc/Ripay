LOCK TABLES `payment_state` WRITE;
/*!40000 ALTER TABLE `payment_state` DISABLE KEYS */;
INSERT INTO `payment_state` VALUES ('C','CLOSED(已关闭)'),('E','PAYERROR(支付失败)'),('N','NOTPAY(订单未支付)'),('P','USERPAYING(用户支付中)'),('R','REFUND(转入退款)'),('S','SUCCESS(支付成功)');
/*!40000 ALTER TABLE `payment_state` ENABLE KEYS */;
UNLOCK TABLES;