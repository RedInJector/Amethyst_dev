CREATE TABLE `payment_history` (
                                `id` bigint(20),
                                `date_time` datetime(6) NOT NULL,
                                `goal` varchar(255) DEFAULT NULL,
                                `amount` DECIMAL(10,2) NOT NULL,
                                `message` varchar(255) DEFAULT NULL,
                                `client_name` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

ALTER TABLE `payment_history`
    ADD PRIMARY KEY (`id`);

ALTER TABLE `payment_history`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;


