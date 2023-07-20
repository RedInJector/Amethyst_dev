SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- База даних: `project_amethyst_dev`
--

-- --------------------------------------------------------

--
-- Структура таблиці `access_token`
--

CREATE TABLE `access_token` (
                                `id` bigint(20) NOT NULL,
                                `created_at` datetime(6) NOT NULL,
                                `expires_on` datetime(6) DEFAULT NULL,
                                `token` varchar(255) DEFAULT NULL,
                                `user_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Структура таблиці `discord_user`
--

CREATE TABLE `discord_user` (
                                `id` bigint(20) NOT NULL,
                                `avatar_url` varchar(255) DEFAULT NULL,
                                `discord_id` varchar(255) DEFAULT NULL,
                                `discord_verified` bit(1) DEFAULT NULL,
                                `discriminator` varchar(255) DEFAULT NULL,
                                `email` varchar(255) DEFAULT NULL,
                                `public_username` varchar(255) DEFAULT NULL,
                                `tag` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Структура таблиці `minecraft_player`
--

CREATE TABLE `minecraft_player` (
                                    `id` bigint(20) NOT NULL,
                                    `player_name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Структура таблиці `oauth`
--

CREATE TABLE `oauth` (
                         `id` bigint(20) NOT NULL,
                         `access_token` varchar(255) DEFAULT NULL,
                         `created_at` datetime(6) NOT NULL,
                         `expires_on` datetime(6) DEFAULT NULL,
                         `refresh_token` varchar(255) DEFAULT NULL,
                         `updated_at` datetime(6) NOT NULL,
                         `user_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Структура таблиці `user`
--

CREATE TABLE `user` (
                        `id` bigint(20) NOT NULL,
                        `admin` bit(1) NOT NULL,
                        `banned` bit(1) DEFAULT NULL,
                        `has_payed` bit(1) DEFAULT NULL,
                        `discord_user_id` bigint(20) DEFAULT NULL,
                        `minecraft_player_id` bigint(20) DEFAULT NULL,
                        `oauth_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Індекси збережених таблиць
--

--
-- Індекси таблиці `access_token`
--
ALTER TABLE `access_token`
    ADD PRIMARY KEY (`id`),
    ADD KEY `FKt6hfg4j66k3lwb5idt1tuaek6` (`user_id`);

--
-- Індекси таблиці `discord_user`
--
ALTER TABLE `discord_user`
    ADD PRIMARY KEY (`id`);

--
-- Індекси таблиці `minecraft_player`
--
ALTER TABLE `minecraft_player`
    ADD PRIMARY KEY (`id`);

--
-- Індекси таблиці `oauth`
--
ALTER TABLE `oauth`
    ADD PRIMARY KEY (`id`),
    ADD KEY `FK7a23c0j3h2lvkrqjy7hkpx4hi` (`user_id`);

--
-- Індекси таблиці `user`
--
ALTER TABLE `user`
    ADD PRIMARY KEY (`id`),
    ADD KEY `FK9h78b4cxtfpkbcticux9lp8u2` (`discord_user_id`),
    ADD KEY `FKlrdxy3uj2pd6qoaswqk1nqhai` (`minecraft_player_id`),
    ADD KEY `FKt78th9rp1tsjuai4e7hdmd0de` (`oauth_id`);

--
-- AUTO_INCREMENT для збережених таблиць
--

--
-- AUTO_INCREMENT для таблиці `access_token`
--
ALTER TABLE `access_token`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT для таблиці `discord_user`
--
ALTER TABLE `discord_user`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT для таблиці `minecraft_player`
--
ALTER TABLE `minecraft_player`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT для таблиці `oauth`
--
ALTER TABLE `oauth`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT для таблиці `user`
--
ALTER TABLE `user`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- Обмеження зовнішнього ключа збережених таблиць
--

--
-- Обмеження зовнішнього ключа таблиці `access_token`
--
ALTER TABLE `access_token`
    ADD CONSTRAINT `FKt6hfg4j66k3lwb5idt1tuaek6` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

--
-- Обмеження зовнішнього ключа таблиці `oauth`
--
ALTER TABLE `oauth`
    ADD CONSTRAINT `FK7a23c0j3h2lvkrqjy7hkpx4hi` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

--
-- Обмеження зовнішнього ключа таблиці `user`
--
ALTER TABLE `user`
    ADD CONSTRAINT `FK9h78b4cxtfpkbcticux9lp8u2` FOREIGN KEY (`discord_user_id`) REFERENCES `discord_user` (`id`),
    ADD CONSTRAINT `FKlrdxy3uj2pd6qoaswqk1nqhai` FOREIGN KEY (`minecraft_player_id`) REFERENCES `minecraft_player` (`id`),
    ADD CONSTRAINT `FKt78th9rp1tsjuai4e7hdmd0de` FOREIGN KEY (`oauth_id`) REFERENCES `oauth` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;




