CREATE TABLE `imported_recipe` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `attributes_blob` blob,
  `ingredients_blob` blob NOT NULL,
  `ingredients_tags` varchar(1000) NOT NULL,
  `directions_blob` blob NOT NULL,
  `notes` varchar(1024) DEFAULT NULL,
  `url_domain` varchar(100) DEFAULT NULL,
  `url` varchar(500) NOT NULL,
  `created_timestamp` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_recipe_index` (`name`,`url`)
) ENGINE=InnoDB AUTO_INCREMENT=243 DEFAULT CHARSET=utf8;
