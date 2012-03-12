-- phpMyAdmin SQL Dump
-- version 3.4.5deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Mar 09, 2012 at 01:47 PM
-- Server version: 5.1.58
-- PHP Version: 5.3.6-13ubuntu3.6

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `process_query`
--

-- --------------------------------------------------------

--
-- Table structure for table `service_neighbors`
--

CREATE TABLE IF NOT EXISTS `service_neighbors` (
  `iddefault` int(11) NOT NULL AUTO_INCREMENT,
  `processid` varchar(100) NOT NULL COMMENT 'processid of target service',
  `target_service_name` varchar(100) NOT NULL COMMENT 'target service that we need to find similar services',
  `zone` varchar(5) NOT NULL,
  `from_service_name` varchar(100) NOT NULL,
  `to_service_name` varchar(100) NOT NULL,
  `pattern` text NOT NULL COMMENT 'It is the direct link pattern of target service at particular zone (head is from service, tail is to service)',
  `no_of_branches` int(11) NOT NULL COMMENT 'Used in case it is a single pattern (ex. AND-split, AND-join, ...). This is used for computing new weight',
  PRIMARY KEY (`iddefault`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=5598 ;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
