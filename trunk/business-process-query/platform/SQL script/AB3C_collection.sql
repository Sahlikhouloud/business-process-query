-- phpMyAdmin SQL Dump
-- version 3.4.5deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Mar 09, 2012 at 01:39 PM
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
-- Table structure for table `AB3C_collection`
--

CREATE TABLE IF NOT EXISTS `AB3C_collection` (
  `iddefault` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id` text NOT NULL,
  `type` text,
  `name` text,
  `sourceref` text,
  `targetref` text,
  `processid` text,
  PRIMARY KEY (`iddefault`,`id`(22))
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=3235 ;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
