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
-- Table structure for table `query_details`
--

CREATE TABLE IF NOT EXISTS `query_details` (
  `iddefault` int(11) NOT NULL AUTO_INCREMENT,
  `processid` varchar(150) NOT NULL,
  `query_no` int(11) NOT NULL,
  `target_process` varchar(150) NOT NULL,
  `target_task` varchar(100) NOT NULL,
  `zone` int(11) NOT NULL,
  `description` text NOT NULL,
  `is_initiated` tinyint(1) NOT NULL,
  PRIMARY KEY (`iddefault`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=62 ;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
