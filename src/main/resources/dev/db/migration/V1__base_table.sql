CREATE TABLE IF NOT EXISTS `language` (
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `flag_icon_name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `flag_icon_url` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `language_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`language_id`),
  UNIQUE KEY `UKg8hr207ijpxlwu10pewyo65gv` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE IF NOT EXISTS `level` (
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `code` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `description` text COLLATE utf8mb4_general_ci NOT NULL,
  `level_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`level_id`),
  UNIQUE KEY `UK3a8oesbo90de97c9qqn70b7y7` (`code`),
  UNIQUE KEY `UKlrjnw0jty1fs19q56u0us8d0n` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE IF NOT EXISTS `course` (
  `course_order` int DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `course_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `description` text COLLATE utf8mb4_general_ci,
  `flag_icon_name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `flag_icon_url` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `title` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE IF NOT EXISTS `course_level` (
  `course_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `course_level_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `level_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`course_level_id`),
  KEY `FK1sc11vscwxtx5t8stgelfi310` (`course_id`),
  KEY `FKg9m130yls0chsa4ih9s99guc2` (`level_id`),
  CONSTRAINT `FK1sc11vscwxtx5t8stgelfi310` FOREIGN KEY (`course_id`) REFERENCES `course` (`course_id`),
  CONSTRAINT `FKg9m130yls0chsa4ih9s99guc2` FOREIGN KEY (`level_id`) REFERENCES `level` (`level_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE IF NOT EXISTS `chapter` (
  `chapter_order` int DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `chapter_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `course_id` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `level_id` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `title` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`chapter_id`),
  KEY `FKhhaina8rg7bpmg1qesiluu8vu` (`course_id`),
  KEY `FKddkwe0m7ty2hqa2rc65pjgsw8` (`level_id`),
  CONSTRAINT `FKddkwe0m7ty2hqa2rc65pjgsw8` FOREIGN KEY (`level_id`) REFERENCES `level` (`level_id`),
  CONSTRAINT `FKhhaina8rg7bpmg1qesiluu8vu` FOREIGN KEY (`course_id`) REFERENCES `course` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE IF NOT EXISTS `lesson` (
  `lesson_order` int DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `chapter_id` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `description` text COLLATE utf8mb4_general_ci NOT NULL,
  `flag_icon_name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `flag_icon_url` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `lesson_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `title` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`lesson_id`),
  KEY `FKyd2sg2b1awfx3br81o66mrwl` (`chapter_id`),
  CONSTRAINT `FKyd2sg2b1awfx3br81o66mrwl` FOREIGN KEY (`chapter_id`) REFERENCES `chapter` (`chapter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE IF NOT EXISTS `grammar` (
  `grammar_order` int DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `description` text COLLATE utf8mb4_general_ci,
  `flag_icon_name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `flag_icon_url` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `grammar_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `language_id` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `title` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`grammar_id`),
  KEY `FKe5v8aq08v63tgl5mmua2i2ege` (`language_id`),
  CONSTRAINT `FKe5v8aq08v63tgl5mmua2i2ege` FOREIGN KEY (`language_id`) REFERENCES `language` (`language_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE IF NOT EXISTS `grammar_section` (
  `grammar_section_order` int DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `content` text COLLATE utf8mb4_general_ci NOT NULL,
  `description` text COLLATE utf8mb4_general_ci,
  `grammar_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `grammar_section_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `lesson_id` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `level_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `title` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`grammar_section_id`),
  KEY `FKk7klcftj0d2e4c9t4b8v35hvs` (`grammar_id`),
  KEY `FK6hmafm0qpvmht0ps4q5do7h57` (`lesson_id`),
  KEY `FKc2cw0993ykosbclydu3aldsdt` (`level_id`),
  CONSTRAINT `FK6hmafm0qpvmht0ps4q5do7h57` FOREIGN KEY (`lesson_id`) REFERENCES `lesson` (`lesson_id`),
  CONSTRAINT `FKc2cw0993ykosbclydu3aldsdt` FOREIGN KEY (`level_id`) REFERENCES `level` (`level_id`),
  CONSTRAINT `FKk7klcftj0d2e4c9t4b8v35hvs` FOREIGN KEY (`grammar_id`) REFERENCES `grammar` (`grammar_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE IF NOT EXISTS `role` (
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `role_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE IF NOT EXISTS `user` (
  `date_of_birth` date DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `last_login` date DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `active_code` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `avatar` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `avatar_name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `email` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `facebook_account_id` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `first_name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `full_name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `google_account_id` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `last_name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `otp` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `password` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `phone_number` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `user_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE IF NOT EXISTS `user_role` (
  `role_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `user_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  KEY `FKa68196081fvovjhkek5m97n3y` (`role_id`),
  KEY `FK859n2jvi8ivhui0rl0esws6o` (`user_id`),
  CONSTRAINT `FK859n2jvi8ivhui0rl0esws6o` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FKa68196081fvovjhkek5m97n3y` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE IF NOT EXISTS `user_language` (
  `date_completed` date DEFAULT NULL,
  `date_started` date DEFAULT NULL,
  `language_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `user_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `user_language_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `learning_status` enum('COMPLETED','IN_PROGRESS','NOTE_STARTED') COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`user_language_id`),
  KEY `FK5dhe6kqh7ol7f48ker70e368r` (`language_id`),
  KEY `FK5tj1u2cplnxdoiouqvmvy3ksp` (`user_id`),
  CONSTRAINT `FK5dhe6kqh7ol7f48ker70e368r` FOREIGN KEY (`language_id`) REFERENCES `language` (`language_id`),
  CONSTRAINT `FK5tj1u2cplnxdoiouqvmvy3ksp` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE IF NOT EXISTS `token` (
  `expired` bit(1) DEFAULT NULL,
  `is_mobile` bit(1) DEFAULT NULL,
  `revoked` bit(1) DEFAULT NULL,
  `expiration_date` datetime(6) DEFAULT NULL,
  `refresh_expiration_date` datetime(6) DEFAULT NULL,
  `refresh_token` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `token` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `token_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `token_type` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `user_id` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`token_id`),
  KEY `FKe32ek7ixanakfqsdaokm4q9y2` (`user_id`),
  CONSTRAINT `FKe32ek7ixanakfqsdaokm4q9y2` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE IF NOT EXISTS `question` (
  `mark` int DEFAULT NULL,
  `question_order` int DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `audio_name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `audio_url` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `explanation` text COLLATE utf8mb4_general_ci,
  `grammar_section_id` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `image_name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `image_url` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `lesson_id` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `question_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `question_text` text COLLATE utf8mb4_general_ci,
  `request` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `script_audio` text COLLATE utf8mb4_general_ci,
  `video_name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `video_url` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `question_type` enum('FILL_BLANK','KNOWLEDGE','MATCHING','MULTIPLE_CHOICE','ORDERING','TRUE_FALSE') COLLATE utf8mb4_general_ci NOT NULL,
  `show_type` enum('NORMAL','TIP','VOCAB') COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`question_id`),
  KEY `FK75ogklqhi3k3k105sxqbjnq38` (`grammar_section_id`),
  KEY `FK1sbknhfhhug49n0elkvgk38vs` (`lesson_id`),
  CONSTRAINT `FK1sbknhfhhug49n0elkvgk38vs` FOREIGN KEY (`lesson_id`) REFERENCES `lesson` (`lesson_id`),
  CONSTRAINT `FK75ogklqhi3k3k105sxqbjnq38` FOREIGN KEY (`grammar_section_id`) REFERENCES `grammar_section` (`grammar_section_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE IF NOT EXISTS `question_fill_blank` (
  `question_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `correct_answer` json DEFAULT NULL,
  PRIMARY KEY (`question_id`),
  CONSTRAINT `FKh89hnp8cbuvg89c9ed5m089pe` FOREIGN KEY (`question_id`) REFERENCES `question` (`question_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE IF NOT EXISTS `question_matching` (
  `question_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`question_id`),
  CONSTRAINT `FK4376y2mw11s2let70ykk04j2r` FOREIGN KEY (`question_id`) REFERENCES `question` (`question_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE IF NOT EXISTS `matching_pair` (
  `pair_order` int DEFAULT NULL,
  `matching_pair_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `pair_key` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `pair_text` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `question_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`matching_pair_id`),
  KEY `FKne5b22t3nxgl048pp2vj26biy` (`question_id`),
  CONSTRAINT `FKne5b22t3nxgl048pp2vj26biy` FOREIGN KEY (`question_id`) REFERENCES `question_matching` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE IF NOT EXISTS `question_multiple_choice` (
  `question_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`question_id`),
  CONSTRAINT `FK25c7j2wvgc6o2n3oqef20gs85` FOREIGN KEY (`question_id`) REFERENCES `question` (`question_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE IF NOT EXISTS `multiple_choice_option` (
  `is_correct` bit(1) DEFAULT NULL,
  `option_order` int DEFAULT NULL,
  `multiple_choice_option_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `option_text` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `question_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`multiple_choice_option_id`),
  KEY `FKfg6jtuykgpog1l1idc9x469r4` (`question_id`),
  CONSTRAINT `FKfg6jtuykgpog1l1idc9x469r4` FOREIGN KEY (`question_id`) REFERENCES `question_multiple_choice` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE IF NOT EXISTS `question_ordering` (
  `correct_answer` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `question_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`question_id`),
  CONSTRAINT `FKqc7gnp6yd9j7ac53450rw4jws` FOREIGN KEY (`question_id`) REFERENCES `question` (`question_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE IF NOT EXISTS `ordering_part` (
  `part_order` int NOT NULL,
  `ordering_part_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `question_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `sentence_part` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`ordering_part_id`),
  KEY `FKc6knekjrdjogo35mva2itc97u` (`question_id`),
  CONSTRAINT `FKc6knekjrdjogo35mva2itc97u` FOREIGN KEY (`question_id`) REFERENCES `question_ordering` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE IF NOT EXISTS `question_true_false` (
  `correct_answer` bit(1) DEFAULT NULL,
  `question_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`question_id`),
  CONSTRAINT `FKtfw8wladoeatnqbmuce72r7s4` FOREIGN KEY (`question_id`) REFERENCES `question` (`question_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE IF NOT EXISTS `level_progress` (
  `is_completed` bit(1) DEFAULT NULL,
  `progress` double DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `level_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `user_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKfq7ku82yogg15th4tieyk544y` (`level_id`),
  KEY `FKnndj1h1wlgujbgoa0hbmymqah` (`user_id`),
  CONSTRAINT `FKfq7ku82yogg15th4tieyk544y` FOREIGN KEY (`level_id`) REFERENCES `level` (`level_id`),
  CONSTRAINT `FKnndj1h1wlgujbgoa0hbmymqah` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE IF NOT EXISTS `course_progress` (
  `is_completed` bit(1) DEFAULT NULL,
  `progress` double DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `course_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `user_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrc6owopnnsws5xddrk2bsjpee` (`course_id`),
  KEY `FKbadjigldffbqgm3xjbxy4m46b` (`user_id`),
  CONSTRAINT `FKbadjigldffbqgm3xjbxy4m46b` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FKrc6owopnnsws5xddrk2bsjpee` FOREIGN KEY (`course_id`) REFERENCES `course` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE IF NOT EXISTS `chapter_progress` (
  `is_completed` bit(1) DEFAULT NULL,
  `progress` double DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `chapter_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `user_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKlk6rkos5dm8jxdpms4xo8ksov` (`chapter_id`),
  KEY `FKpgrnr93j96hdv8thl37yoynoe` (`user_id`),
  CONSTRAINT `FKlk6rkos5dm8jxdpms4xo8ksov` FOREIGN KEY (`chapter_id`) REFERENCES `chapter` (`chapter_id`),
  CONSTRAINT `FKpgrnr93j96hdv8thl37yoynoe` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE IF NOT EXISTS `lesson_progress` (
  `is_completed` bit(1) DEFAULT NULL,
  `progress` double DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `lesson_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `user_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqff2stq7jrqvtih96pxu72xcv` (`lesson_id`),
  KEY `FKprdhqbwedhmcqhw7xtf4gkp2l` (`user_id`),
  CONSTRAINT `FKprdhqbwedhmcqhw7xtf4gkp2l` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FKqff2stq7jrqvtih96pxu72xcv` FOREIGN KEY (`lesson_id`) REFERENCES `lesson` (`lesson_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE IF NOT EXISTS `grammar_progress` (
  `is_completed` bit(1) DEFAULT NULL,
  `progress` double DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `grammar_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `user_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKburgp1at38encfesw4qwcgrbp` (`grammar_id`),
  KEY `FK4g5mrltxkt3q6agmqmkaen63v` (`user_id`),
  CONSTRAINT `FK4g5mrltxkt3q6agmqmkaen63v` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FKburgp1at38encfesw4qwcgrbp` FOREIGN KEY (`grammar_id`) REFERENCES `grammar` (`grammar_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE IF NOT EXISTS `grammar_section_progress` (
  `is_completed` bit(1) DEFAULT NULL,
  `progress` double DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `grammar_section_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `user_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKknh62yfngw5w52muv3dfatb6u` (`grammar_section_id`),
  KEY `FK4q35c3mcrqdp9nc4df3yq73jf` (`user_id`),
  CONSTRAINT `FK4q35c3mcrqdp9nc4df3yq73jf` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FKknh62yfngw5w52muv3dfatb6u` FOREIGN KEY (`grammar_section_id`) REFERENCES `grammar_section` (`grammar_section_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;