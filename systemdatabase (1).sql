-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Aug 30, 2025 at 12:25 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `systemdatabase`
--

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

CREATE TABLE `categories` (
  `id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`id`, `name`) VALUES
(2, 'Beans'),
(1, 'Coffee'),
(4, 'Drinks'),
(36, 'hehe'),
(213, 'huhu'),
(575, 'JAVA'),
(5, 'Others'),
(3, 'Pastries');

-- --------------------------------------------------------

--
-- Table structure for table `menuitems`
--

CREATE TABLE `menuitems` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `price` double NOT NULL,
  `imagepath` varchar(255) NOT NULL,
  `category_id` int(11) DEFAULT NULL,
  `availability` enum('Available','Unavailable') DEFAULT 'Available'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `menuitems`
--

INSERT INTO `menuitems` (`id`, `name`, `price`, `imagepath`, `category_id`, `availability`) VALUES
(29, '1', 11, 'images\\1756547187151_FB_IMG_1756546853871.jpg', 1, 'Available'),
(30, '2', 22, 'images\\1756547215029_FB_IMG_1756546866701.jpg', 1, 'Available'),
(31, 'cd', 1, 'images\\1756547241438_FB_IMG_1756546870254.jpg', 1, 'Available'),
(32, '4', 22, 'images\\1756547268158_FB_IMG_1756546873089.jpg', 1, 'Available'),
(33, '5', 12, 'images\\1756547305439_FB_IMG_1756546877835.jpg', 1, 'Available'),
(34, '6', 12, 'images\\1756547335390_FB_IMG_1756546883271.jpg', 1, 'Available'),
(35, '7', 99, 'images\\1756547395798_FB_IMG_1756546887447.jpg', 1, 'Available'),
(36, '8', 8, 'images\\1756547437550_FB_IMG_1756546891622.jpg', 4, 'Available'),
(37, '9', 9, 'images\\1756547544151_FB_IMG_1756546898775.jpg', 1, 'Available'),
(38, '10', 10, 'images\\1756547662414_FB_IMG_1756546902242.jpg', 1, 'Available'),
(39, '11', 11, 'images\\1756547716798_FB_IMG_1756546911313.jpg', 1, 'Available'),
(40, '12', 12, 'images\\1756547747518_FB_IMG_1756546915158.jpg', 1, 'Available'),
(41, '13', 13, 'images\\1756547786973_FB_IMG_1756546922041.jpg', 1, 'Available'),
(42, '14', 14, 'images\\1756547833710_FB_IMG_1756546929103.jpg', 1, 'Available'),
(43, '15', 15, 'images\\1756547885157_FB_IMG_1756546933098.jpg', 1, 'Available');

-- --------------------------------------------------------

--
-- Table structure for table `newuser`
--

CREATE TABLE `newuser` (
  `id` int(11) NOT NULL,
  `newusername` varchar(50) NOT NULL,
  `newuserpassword` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `newuser`
--

INSERT INTO `newuser` (`id`, `newusername`, `newuserpassword`) VALUES
(1, 'Mark12', 'mark');

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

CREATE TABLE `orders` (
  `id` int(11) NOT NULL,
  `menu_item_id` int(11) NOT NULL,
  `price` double NOT NULL,
  `quantity` int(11) NOT NULL DEFAULT 1,
  `order_date` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'active'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `status`) VALUES
(1, 'mark', 'mark', 'active'),
(2, 'Mark12', 'mark', 'active');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`);

--
-- Indexes for table `menuitems`
--
ALTER TABLE `menuitems`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `newuser`
--
ALTER TABLE `newuser`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `newusername` (`newusername`);

--
-- Indexes for table `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`id`),
  ADD KEY `menu_item_id` (`menu_item_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `categories`
--
ALTER TABLE `categories`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=632;

--
-- AUTO_INCREMENT for table `menuitems`
--
ALTER TABLE `menuitems`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=44;

--
-- AUTO_INCREMENT for table `newuser`
--
ALTER TABLE `newuser`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `orders`
--
ALTER TABLE `orders`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=30;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `orders`
--
ALTER TABLE `orders`
  ADD CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`menu_item_id`) REFERENCES `menuitems` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
