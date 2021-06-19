-- Host: localhost
-- Generation Time: Jun 19, 2021 at 11:35 PM


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `sa`
--
-- --------------------------------------------------------
--
-- Table structure for table `customer`
--
CREATE TABLE IF NOT EXISTS `customer` (
  `id` nchar(10) unsigned NOT NULL,
  `room_id` nchar(10) NOT NULL,
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
-- Table structure for table `HistoryCustomer`
--
CREATE TABLE IF NOT EXISTS `HistoryCustomer` (
  `user_name` nchar(10) NOT NULL,
  `room_id` nchar(10) NOT NULL,
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
-- Table structure for table `reportor`
--
CREATE TABLE IF NOT EXISTS `reportor` (
  `start_time` datetime NOT NULL,
  `stop_time` datetime NOT NULL,
  `start_temp` float NOT NULL,
  `stop_temp` float NOT NULL,
  `wind_power` int ,
  `electricity` float ,
  `cost` float,
  `room_id` nchar(10) NOT NULL,
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
-- Table structure for table `room`
--
CREATE TABLE IF NOT EXISTS `room` (
  `room_id` nchar(10) NOT NULL,
  `is_booked` int NOT NULL,
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
-- Table structure for table `RoomState`
--
CREATE TABLE IF NOT EXISTS `RoomState` (
  `room_id` nchar(10) NOT NULL,
  `isUp` int NOT NULL,
  `upTime` datetime NOT NULL,
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
--insert some room in table
--
insert room(room_id, is_booked) values("001",0);

insert room(room_id, is_booked) values("002",0);

insert room(room_id, is_booked) values("003",0);

insert room(room_id, is_booked) values("004",0);


