-- phpMyAdmin SQL Dump
-- version 4.2.7.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Jun 06, 2015 at 01:44 AM
-- Server version: 5.6.20
-- PHP Version: 5.5.15

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `happyappy`
--
CREATE DATABASE IF NOT EXISTS `happyappy` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `happyappy`;

-- --------------------------------------------------------

--
-- Table structure for table `images`
--

DROP TABLE IF EXISTS `images`;
CREATE TABLE IF NOT EXISTS `images` (
`picId` int(11) NOT NULL,
  `picUrl` varchar(500) NOT NULL
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=32 ;

--
-- Dumping data for table `images`
--

INSERT INTO `images` (`picId`, `picUrl`) VALUES
(5, 'http://www.zooborns.com/.a/6a010535647bf3970b017c34c27806970b-500wi'),
(6, 'http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=67320552'),
(7, 'http://media-cache-ak0.pinimg.com/736x/64/05/ec/6405ecc66f04a9d013f402d7d4e88570.jpg'),
(8, 'http://theatremusicdirectors.org/wp-content/uploads/2013/01/jukebox.jpg'),
(9, 'http://www.sport81.eu/static/media/cache/bb/2a/bb2ac4f5357df74a951b7144cd1dc6eb.jpg'),
(10, 'http://www.cheershiring.co.za/products/bar.jpg'),
(11, 'http://armstrongeconomics.com/wp-content/uploads/2013/11/shadows.jpg'),
(12, 'http://www.drinkstuff.com/productimg/44987_large.jpg'),
(13, 'http://cdn.beerstore.com.au/sites/beerstore.com.au/files/imagecache/product_full/prod-img/miller-genuine-draught-beer-online-1370232158.jpg'),
(14, 'http://www.comercial55.com.br/loja/product_images/l/budweiser_7__99926_zoom.jpg'),
(15, 'http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=67320552'),
(16, 'http://media-cache-ak0.pinimg.com/736x/64/05/ec/6405ecc66f04a9d013f402d7d4e88570.jpg'),
(17, 'http://theatremusicdirectors.org/wp-content/uploads/2013/01/jukebox.jpg'),
(18, 'http://www.sport81.eu/static/media/cache/bb/2a/bb2ac4f5357df74a951b7144cd1dc6eb.jpg'),
(19, 'http://www.cheershiring.co.za/products/bar.jpg'),
(20, 'http://armstrongeconomics.com/wp-content/uploads/2013/11/shadows.jpg'),
(21, 'http://www.drinkstuff.com/productimg/44987_large.jpg'),
(22, 'http://cdn.beerstore.com.au/sites/beerstore.com.au/files/imagecache/product_full/prod-img/miller-genuine-draught-beer-online-1370232158.jpg'),
(23, 'http://www.comercial55.com.br/loja/product_images/l/budweiser_7__99926_zoom.jpg'),
(24, 'http://www.polyvore.com/cgi/img-thing?.out=jpg&size=l&tid=67320552'),
(25, 'http://media-cache-ak0.pinimg.com/736x/64/05/ec/6405ecc66f04a9d013f402d7d4e88570.jpg'),
(26, 'http://theatremusicdirectors.org/wp-content/uploads/2013/01/jukebox.jpg'),
(27, 'http://www.sport81.eu/static/media/cache/bb/2a/bb2ac4f5357df74a951b7144cd1dc6eb.jpg'),
(28, 'http://www.cheershiring.co.za/products/bar.jpg'),
(29, 'http://armstrongeconomics.com/wp-content/uploads/2013/11/shadows.jpg'),
(30, 'http://www.drinkstuff.com/productimg/44987_large.jpg'),
(31, 'http://cdn.beerstore.com.au/sites/beerstore.com.au/files/imagecache/product_full/prod-img/miller-genuine-draught-beer-online-1370232158.jpg');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `images`
--
ALTER TABLE `images`
 ADD PRIMARY KEY (`picId`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `images`
--
ALTER TABLE `images`
MODIFY `picId` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=32;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
