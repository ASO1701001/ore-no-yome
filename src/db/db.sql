SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

CREATE DATABASE IF NOT EXISTS `vxx0_ore-no-yome` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `vxx0_ore-no-yome`;

CREATE TABLE `comment_anime` (
  `comment_id` int(10) NOT NULL,
  `anime_id` int(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

CREATE TABLE `comment_chara` (
  `comment_id` int(10) NOT NULL,
  `chara_id` int(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

CREATE TABLE `comment_data` (
  `id` int(10) NOT NULL,
  `user_id` int(10) NOT NULL,
  `post_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `content` varchar(200) NOT NULL,
  `timestamp` varchar(10) NOT NULL,
  `delete_flg` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

CREATE TABLE `favorite_chara` (
  `user_id` int(10) NOT NULL,
  `chara_id` int(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `favorite_post` (
  `id` int(10) NOT NULL,
  `user_id` int(10) NOT NULL,
  `post_id` int(10) NOT NULL,
  `timestamp` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `master_anime` (
  `id` int(10) NOT NULL,
  `anime_name` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `master_anime` (`id`, `anime_name`) VALUES
(2, 'かぐや様は告らせたい〜天才たちの恋愛頭脳戦〜'),
(10, 'からかい上手の高木さん'),
(27, 'がっこうぐらし！'),
(16, 'けいおん！！'),
(30, 'ご注文はうさぎですか??'),
(25, 'ひぐらしのなく頃に'),
(14, 'まよチキ！'),
(29, 'やはり俺の青春ラブコメはまちがっている。'),
(15, 'ゆるゆり'),
(31, 'アイドルマスターシンデレラガールズ'),
(6, 'エロマンガ先生'),
(24, 'ガウリールドロップアウト'),
(23, 'デート・ア・ライブ'),
(28, 'ハイスクール・フリート'),
(26, 'ブレンド・S'),
(22, 'ローゼンメイデン'),
(8, '五等分の花嫁'),
(5, '俺の妹がこんなに可愛いわけがない'),
(12, '俺の彼女と幼なじみが修羅場すぎる'),
(9, '僕は友だちが少ない'),
(3, '冴えない彼女の育てかた'),
(11, '化物語'),
(13, '妖狐×僕SS'),
(18, '寄宿学校のジュリエット'),
(17, '灼眼のシャナ'),
(21, '私に天使が舞い降りた！'),
(1, '進撃の巨人'),
(7, '電波女と青春男'),
(4, '青春ブタ野郎はバニーガール先輩の夢を見ない');

CREATE TABLE `master_chara` (
  `id` int(10) NOT NULL,
  `anime_id` int(10) NOT NULL,
  `chara_name` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `master_chara` (`id`, `anime_id`, `chara_name`) VALUES
(1, 1, 'ミカサ・アッカーマン'),
(2, 1, 'サシャ・ブラウス'),
(3, 1, 'クリスタ・レンズ'),
(4, 2, '四宮 かぐや'),
(5, 2, '藤原 千花'),
(6, 2, '伊井野 ミコ'),
(7, 2, '早坂 愛'),
(8, 2, '白銀 圭'),
(9, 2, '藤原 豊実'),
(10, 2, '藤原 萌葉'),
(11, 2, '柏木 渚'),
(12, 2, '四条 眞妃'),
(13, 3, '加藤 恵'),
(14, 3, '澤村・スペンサー・英梨々（柏木 エリ）'),
(15, 3, '霞ヶ丘 詩羽（霞 詩子）'),
(16, 3, '波島 出海'),
(17, 3, '氷堂 美智留'),
(18, 4, '桜島 麻衣'),
(19, 4, '梓川 かえで（梓川 花楓）'),
(20, 4, '双葉 理央'),
(21, 4, '古賀 朋絵'),
(22, 4, '牧之原 翔子'),
(23, 4, '豊浜 のどか'),
(24, 5, '高坂 桐乃'),
(25, 5, '田村 麻奈実'),
(26, 5, '沙織・バジーナ（槇島沙織）'),
(27, 5, '黒猫（五更瑠璃）'),
(28, 5, '新垣 あやせ'),
(29, 5, '来栖 加奈子'),
(30, 6, '和泉 紗霧（エロマンガ先生）'),
(31, 6, '山田 エルフ（エミリー・グレンジャー）'),
(32, 6, '千寿 ムラマサ（梅園花）'),
(33, 6, '神野 めぐみ'),
(34, 6, '高砂 智恵');

CREATE TABLE `notice_post` (
  `id` int(10) NOT NULL,
  `user_id` int(10) NOT NULL,
  `favorite_post_id` int(10) NOT NULL,
  `read_flg` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

CREATE TABLE `post_data` (
  `id` int(10) NOT NULL,
  `user_id` int(10) NOT NULL,
  `post_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `content` varchar(300) NOT NULL,
  `image_flg` tinyint(1) NOT NULL DEFAULT 0,
  `timestamp` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `anime_id` int(10) NOT NULL,
  `chara_id` int(10) NOT NULL,
  `delete_flg` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

CREATE TABLE `post_image` (
  `id` int(10) NOT NULL,
  `post_id` int(10) NOT NULL,
  `order_number` int(10) NOT NULL DEFAULT 1,
  `file_name` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user` (
  `id` int(10) NOT NULL,
  `user_id` varchar(30) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `user_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `icon` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `profile` varchar(300) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `email` varchar(250) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `password` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `status` int(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user_token` (
  `id` int(10) NOT NULL,
  `user_id` int(10) NOT NULL,
  `token` varchar(30) NOT NULL,
  `timestamp` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


ALTER TABLE `comment_anime`
  ADD KEY `comment_anime_comment_data_id_fk` (`comment_id`),
  ADD KEY `comment_anime_master_anime_id_fk` (`anime_id`);

ALTER TABLE `comment_chara`
  ADD KEY `comment_chara_comment_data_id_fk` (`comment_id`),
  ADD KEY `comment_chara_master_chara_id_fk` (`chara_id`);

ALTER TABLE `comment_data`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `post_id` (`post_id`);

ALTER TABLE `favorite_chara`
  ADD PRIMARY KEY (`user_id`,`chara_id`),
  ADD KEY `favorite_chara_master_chara_id_fk` (`chara_id`);

ALTER TABLE `favorite_post`
  ADD PRIMARY KEY (`id`),
  ADD KEY `favorite_post_post_data_id_fk` (`post_id`),
  ADD KEY `favorite_post_user_id_fk` (`user_id`);

ALTER TABLE `master_anime`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `anime_name` (`anime_name`);

ALTER TABLE `master_chara`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `chara_name` (`chara_name`),
  ADD KEY `master_chara_master_anime_id_fk` (`anime_id`);

ALTER TABLE `notice_post`
  ADD PRIMARY KEY (`id`),
  ADD KEY `notice_user_id_fk` (`user_id`),
  ADD KEY `notice_post_id_fk` (`favorite_post_id`) USING BTREE;

ALTER TABLE `post_data`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `post_id` (`post_id`),
  ADD KEY `post_data_master_anime_id_fk` (`anime_id`),
  ADD KEY `post_data_master_chara_id_fk` (`chara_id`);

ALTER TABLE `post_image`
  ADD PRIMARY KEY (`id`),
  ADD KEY `post_image_post_data_id_fk` (`post_id`);

ALTER TABLE `user`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `user_id` (`user_id`),
  ADD UNIQUE KEY `email` (`email`);

ALTER TABLE `user_token`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `user_token_token_uindex` (`token`),
  ADD KEY `user_token_user_id_fk` (`user_id`);


ALTER TABLE `comment_data`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT;

ALTER TABLE `favorite_post`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT;

ALTER TABLE `master_anime`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;

ALTER TABLE `master_chara`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=35;

ALTER TABLE `notice_post`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT;

ALTER TABLE `post_data`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT;

ALTER TABLE `post_image`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT;

ALTER TABLE `user`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT;

ALTER TABLE `user_token`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT;


ALTER TABLE `comment_anime`
  ADD CONSTRAINT `comment_anime_comment_data_id_fk` FOREIGN KEY (`comment_id`) REFERENCES `comment_data` (`id`),
  ADD CONSTRAINT `comment_anime_master_anime_id_fk` FOREIGN KEY (`anime_id`) REFERENCES `master_anime` (`id`);

ALTER TABLE `comment_chara`
  ADD CONSTRAINT `comment_chara_comment_data_id_fk` FOREIGN KEY (`comment_id`) REFERENCES `comment_data` (`id`),
  ADD CONSTRAINT `comment_chara_master_chara_id_fk` FOREIGN KEY (`chara_id`) REFERENCES `master_chara` (`id`);

ALTER TABLE `favorite_chara`
  ADD CONSTRAINT `favorite_chara_master_chara_id_fk` FOREIGN KEY (`chara_id`) REFERENCES `master_chara` (`id`),
  ADD CONSTRAINT `favorite_chara_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

ALTER TABLE `favorite_post`
  ADD CONSTRAINT `favorite_post_post_data_id_fk` FOREIGN KEY (`post_id`) REFERENCES `post_data` (`id`),
  ADD CONSTRAINT `favorite_post_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

ALTER TABLE `master_chara`
  ADD CONSTRAINT `master_chara_master_anime_id_fk` FOREIGN KEY (`anime_id`) REFERENCES `master_anime` (`id`);

ALTER TABLE `notice_post`
  ADD CONSTRAINT `notice_post_post_data_id_fk` FOREIGN KEY (`favorite_post_id`) REFERENCES `post_data` (`id`),
  ADD CONSTRAINT `notice_post_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

ALTER TABLE `post_data`
  ADD CONSTRAINT `post_data_master_anime_id_fk` FOREIGN KEY (`anime_id`) REFERENCES `master_anime` (`id`),
  ADD CONSTRAINT `post_data_master_chara_id_fk` FOREIGN KEY (`chara_id`) REFERENCES `master_chara` (`id`);

ALTER TABLE `post_image`
  ADD CONSTRAINT `post_image_post_data_id_fk` FOREIGN KEY (`post_id`) REFERENCES `post_data` (`id`);

ALTER TABLE `user_token`
  ADD CONSTRAINT `user_token_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
