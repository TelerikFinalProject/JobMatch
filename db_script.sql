-- --------------------------------------------------------
-- Хост:                         127.0.0.1
-- Версия на сървъра:            11.4.3-MariaDB - mariadb.org binary distribution
-- ОС на сървъра:                Win64
-- HeidiSQL Версия:              12.6.0.6765
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Дъмп на структурата на БД job_match
CREATE DATABASE IF NOT EXISTS `job_match` /*!40100 DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci */;
USE `job_match`;

-- Дъмп структура за таблица job_match.employers
CREATE TABLE IF NOT EXISTS `employers` (
  `id` int(11) NOT NULL,
  `company_name` varchar(50) NOT NULL,
  `description` varchar(200) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `employers_user_id_fk` FOREIGN KEY (`id`) REFERENCES `users_details` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Дъмп данни за таблица job_match.employers: ~15 rows (приблизителен брой)
INSERT IGNORE INTO `employers` (`id`, `company_name`, `description`) VALUES
	(8, 'testCompany', 'A startup focusing on innovative AR/VR technologies for education.'),
	(32, 'Tech Solutions', 'A leading provider of IT solutions for businesses.'),
	(33, 'Green Energy', 'Specializing in sustainable and renewable energy solutions.'),
	(34, 'FinTech Hub', 'Innovative financial technology services for the modern world.'),
	(35, 'HealthTech', 'Revolutionizing healthcare with cutting-edge technology.'),
	(36, 'EduTech', 'Enhancing education through innovative technology solutions.'),
	(37, 'Cyber Secure', 'Providing advanced cybersecurity solutions to protect businesses.'),
	(38, 'Food Innovations', 'Creating innovative solutions for the food industry.'),
	(39, 'Media Hub', 'Delivering high-quality media and entertainment content.'),
	(40, 'RetailTech', 'Innovative technology solutions for the retail industry.'),
	(41, 'Smart Transport', 'Transforming transportation with smart technology solutions.'),
	(42, 'Innovative Tech', 'Developing cutting-edge technology solutions for businesses.'),
	(43, 'Solar Energy', 'Providing solar energy solutions for a sustainable future.'),
	(44, 'BioTech Lab', 'Leading innovations in biotechnology and healthcare.'),
	(45, 'AgriTech', 'Advancing agriculture with innovative technology solutions.'),
	(48, 'Robotics Lab', 'Pioneering the future of robotics and automation.');

-- Дъмп структура за таблица job_match.job_ads
CREATE TABLE IF NOT EXISTS `job_ads` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `position_title` varchar(200) NOT NULL,
  `min_salary` decimal(10,0) NOT NULL,
  `max_salary` decimal(10,0) NOT NULL,
  `job_description` varchar(500) NOT NULL,
  `location_id` int(11) NOT NULL,
  `status_id` int(11) NOT NULL,
  `employer_id` int(11) NOT NULL,
  `is_hybrid` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `job_ads_locations_city_id_fk` (`location_id`),
  KEY `job_ads_statuses_id_fk` (`status_id`),
  KEY `job_ads_employers_id_fk` (`employer_id`),
  CONSTRAINT `job_ads_employers_id_fk` FOREIGN KEY (`employer_id`) REFERENCES `employers` (`id`),
  CONSTRAINT `job_ads_locations_city_id_fk` FOREIGN KEY (`location_id`) REFERENCES `locations` (`id`),
  CONSTRAINT `job_ads_statuses_id_fk` FOREIGN KEY (`status_id`) REFERENCES `statuses` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Дъмп данни за таблица job_match.job_ads: ~21 rows (приблизителен брой)
INSERT IGNORE INTO `job_ads` (`id`, `position_title`, `min_salary`, `max_salary`, `job_description`, `location_id`, `status_id`, `employer_id`, `is_hybrid`) VALUES
	(1, 'Java Developer', 2000, 2500, 'Join an innovative AI startup focused on revolutionizing the healthcare industry.', 1, 3, 8, 0),
	(2, 'Software Engineer', 2000, 3000, 'Develop and maintain software applications.', 9735, 1, 32, 1),
	(3, 'Data Analyst', 1800, 2500, 'Analyze and interpret complex data sets.', 9292, 1, 33, 0),
	(4, 'Marketing Specialist', 1500, 2200, 'Develop marketing strategies and campaigns.', 9656, 1, 34, 1),
	(5, 'Project Manager', 2500, 3500, 'Oversee project execution and ensure timely delivery.', 9701, 1, 35, 0),
	(6, 'System Administrator', 2000, 2800, 'Manage and maintain IT systems.', 1, 1, 36, 1),
	(7, 'Graphic Designer', 1200, 2000, 'Create visual designs for marketing.', 9735, 1, 37, 0),
	(8, 'HR Manager', 1800, 2500, 'Manage human resources operations.', 9292, 1, 38, 1),
	(9, 'Accountant', 1500, 2200, 'Handle financial records and tax reports.', 9656, 1, 39, 0),
	(10, 'Software Tester', 1500, 2000, 'Test software for bugs and quality assurance.', 9701, 1, 40, 1),
	(11, 'Content Writer', 1200, 1800, 'Write and edit content for blogs and websites.', 1, 1, 41, 0),
	(12, 'Web Developer', 2000, 3000, 'Develop and maintain websites.', 9735, 1, 42, 1),
	(13, 'Mobile App Developer', 2200, 3200, 'Design and develop mobile applications.', 9292, 1, 43, 0),
	(14, 'DevOps Engineer', 2500, 3500, 'Implement DevOps practices for CI/CD.', 9656, 1, 44, 1),
	(15, 'Network Engineer', 2000, 2800, 'Design and manage network infrastructure.', 9701, 1, 45, 0),
	(16, 'Sales Executive', 1200, 2000, 'Drive sales and build client relationships.', 1, 1, 32, 1),
	(17, 'UI/UX Designer', 1800, 2500, 'Design intuitive user interfaces and experiences.', 9735, 1, 33, 0),
	(18, 'Technical Writer', 1500, 2200, 'Write technical documentation and manuals.', 9292, 1, 34, 1),
	(19, 'Support Engineer', 1200, 2000, 'Provide technical support to customers.', 9656, 1, 35, 0),
	(20, 'Operations Manager', 2200, 3000, 'Oversee daily business operations.', 9701, 1, 36, 1),
	(21, 'Business Analyst', 2000, 2800, 'Analyze business processes and recommend solutions.', 1, 1, 37, 0);

-- Дъмп структура за таблица job_match.job_ads_match_requests
CREATE TABLE IF NOT EXISTS `job_ads_match_requests` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `job_ad_id` int(11) NOT NULL,
  `job_application_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `job_ads_match_requests_job_ads_id_fk` (`job_ad_id`),
  KEY `job_ads_match_requests_job_applications_id_fk` (`job_application_id`),
  CONSTRAINT `job_ads_match_requests_job_ads_id_fk` FOREIGN KEY (`job_ad_id`) REFERENCES `job_ads` (`id`),
  CONSTRAINT `job_ads_match_requests_job_applications_id_fk` FOREIGN KEY (`job_application_id`) REFERENCES `job_applications` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Дъмп данни за таблица job_match.job_ads_match_requests: ~0 rows (приблизителен брой)

-- Дъмп структура за таблица job_match.job_applications
CREATE TABLE IF NOT EXISTS `job_applications` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `application_description` varchar(200) NOT NULL,
  `min_salary` decimal(10,0) NOT NULL,
  `max_salary` decimal(10,0) NOT NULL,
  `location_id` int(11) NOT NULL,
  `status_id` int(11) NOT NULL,
  `professional_id` int(11) NOT NULL,
  `is_hybrid` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `job_applications_locations_location_id_fk` (`location_id`),
  KEY `job_applications_statuses_id_fk` (`status_id`),
  KEY `job_applications_professionals_id_fk` (`professional_id`),
  CONSTRAINT `job_applications_locations_location_id_fk` FOREIGN KEY (`location_id`) REFERENCES `locations` (`id`),
  CONSTRAINT `job_applications_professionals_id_fk` FOREIGN KEY (`professional_id`) REFERENCES `professionals` (`id`),
  CONSTRAINT `job_applications_statuses_id_fk` FOREIGN KEY (`status_id`) REFERENCES `statuses` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=80 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Дъмп данни за таблица job_match.job_applications: ~60 rows (приблизителен брой)
INSERT IGNORE INTO `job_applications` (`id`, `application_description`, `min_salary`, `max_salary`, `location_id`, `status_id`, `professional_id`, `is_hybrid`) VALUES
	(20, 'Looking for a challenging role in software development.', 1200, 1800, 9735, 1, 12, 1),
	(21, 'Seeking a position as a data analyst to leverage analytical skills.', 1300, 1900, 9292, 1, 13, 0),
	(22, 'Passionate about creative marketing strategies and campaigns.', 1000, 1500, 9656, 1, 14, 1),
	(23, 'Experienced in managing projects efficiently and effectively.', 1500, 2200, 9701, 1, 15, 0),
	(24, 'System administration professional with extensive IT experience.', 1400, 2000, 1, 1, 16, 1),
	(25, 'Creative graphic designer with a knack for visual storytelling.', 1000, 1400, 9735, 1, 17, 0),
	(26, 'Human resource manager focused on strategic HR solutions.', 1400, 2100, 9292, 1, 18, 1),
	(27, 'Accountant specializing in financial reporting and audits.', 1200, 1700, 9656, 1, 19, 0),
	(28, 'Software tester committed to quality assurance and bug tracking.', 1200, 1600, 9701, 1, 20, 1),
	(29, 'Content writer skilled in crafting compelling digital content.', 1000, 1500, 1, 1, 21, 0),
	(30, 'Web developer enthusiastic about creating user-friendly websites.', 1200, 1800, 9735, 1, 22, 1),
	(31, 'Mobile app developer passionate about innovative applications.', 1400, 2000, 9292, 1, 23, 0),
	(32, 'DevOps engineer skilled in CI/CD pipelines and automation tools.', 1600, 2400, 9656, 1, 24, 1),
	(33, 'Network engineer experienced in network architecture and security.', 1500, 2100, 9701, 1, 25, 0),
	(34, 'Sales executive experienced in relationship building and sales.', 1000, 1400, 1, 1, 26, 1),
	(35, 'UI/UX designer focused on user-friendly and engaging designs.', 1200, 1700, 9735, 1, 27, 0),
	(36, 'Technical writer skilled in creating comprehensive documentation.', 1200, 1600, 9292, 1, 28, 1),
	(37, 'Support engineer committed to resolving technical issues promptly.', 1200, 1700, 9656, 1, 29, 0),
	(38, 'Operations manager experienced in streamlining business processes.', 1600, 2400, 9701, 1, 30, 1),
	(39, 'Business analyst skilled in data-driven decision-making.', 1500, 2100, 1, 1, 31, 0),
	(40, 'Eager junior software developer looking for opportunities in Java development.', 2500, 3500, 9735, 1, 12, 1),
	(41, 'Experienced backend developer with expertise in Spring and Hibernate.', 3000, 4500, 9292, 1, 13, 1),
	(42, 'SQL and database specialist seeking database administration roles.', 2000, 3000, 9656, 1, 14, 0),
	(43, 'Frontend developer skilled in React and Angular, aiming to build great UI experiences.', 2500, 4000, 9701, 1, 15, 1),
	(44, 'Full-stack developer with experience in Docker and Kubernetes.', 3200, 5000, 1, 1, 16, 0),
	(45, 'Cloud engineer with expertise in AWS and Azure platforms.', 3000, 4500, 9735, 1, 17, 1),
	(46, 'Java developer passionate about building RESTful APIs.', 2500, 3500, 9292, 1, 18, 0),
	(47, 'DevOps engineer skilled in CI/CD pipelines and Jenkins automation.', 3000, 4500, 9656, 1, 19, 1),
	(48, 'Frontend specialist with a keen eye for design and CSS expertise.', 2000, 3000, 9701, 1, 20, 0),
	(49, 'Software engineer with hands-on experience in Python and machine learning.', 3500, 5000, 1, 1, 21, 1),
	(50, 'Mobile app developer skilled in React Native and TypeScript.', 2500, 4000, 9735, 1, 22, 0),
	(51, 'C++ developer with a focus on performance and system-level programming.', 3000, 5000, 9292, 1, 23, 1),
	(52, 'Network administrator with Linux expertise and shell scripting skills.', 2500, 4000, 9656, 1, 24, 0),
	(53, 'Data analyst proficient in data visualization and NoSQL databases.', 2500, 3500, 9701, 1, 25, 1),
	(54, 'Backend developer focused on Microservices and GraphQL integrations.', 3000, 4500, 1, 1, 26, 1),
	(55, 'Test engineer experienced in TDD, JUnit, and Mockito.', 2000, 3000, 9735, 1, 27, 0),
	(56, 'Agile practitioner with extensive experience using JIRA.', 2500, 4000, 9292, 1, 28, 1),
	(57, 'HTML/CSS developer eager to join creative projects.', 2000, 3000, 9656, 1, 29, 0),
	(58, 'Security-focused software engineer knowledgeable in Ansible and Terraform.', 3000, 4500, 9701, 1, 30, 1),
	(59, 'Experienced software architect with a passion for mentoring teams.', 4500, 6000, 1, 1, 31, 1),
	(60, 'Junior software developer with strong knowledge in Java and looking to grow in backend development.', 2200, 3000, 9735, 1, 12, 1),
	(61, 'Backend developer with experience in Spring and Hibernate, seeking to work on enterprise applications.', 2500, 3200, 9292, 1, 13, 1),
	(62, 'Database administrator with solid experience in SQL and PostgreSQL for large-scale applications.', 2300, 3100, 9656, 1, 14, 0),
	(63, 'Frontend developer with expertise in React and Angular, passionate about building user-friendly interfaces.', 2400, 3000, 9701, 1, 15, 1),
	(64, 'Full-stack developer experienced in both backend and frontend technologies such as Java, Spring, React, and Docker.', 2700, 3500, 1, 4, 16, 0),
	(65, 'Cloud engineer with expertise in AWS and Azure platforms, focusing on building scalable cloud infrastructures.', 2500, 3300, 9735, 1, 17, 1),
	(66, 'Java developer experienced in building REST APIs and eager to contribute to a fast-paced team.', 2300, 3000, 9292, 1, 18, 0),
	(67, 'DevOps engineer skilled in Jenkins and automation, aiming to improve development pipelines for rapid delivery.', 2600, 3300, 9656, 1, 19, 1),
	(68, 'Frontend developer specializing in CSS, HTML, and modern design practices for responsive websites.', 2200, 2900, 9701, 1, 20, 0),
	(69, 'Software engineer with experience in Python and looking to work on machine learning and data science projects.', 2800, 3500, 1, 1, 21, 1),
	(70, 'Mobile app developer skilled in React Native and TypeScript, focusing on building cross-platform applications.', 2400, 3000, 9735, 1, 22, 0),
	(71, 'C++ developer with a focus on performance optimization and system-level programming for embedded applications.', 2500, 3200, 9292, 1, 23, 1),
	(72, 'Network administrator with expertise in Linux systems, shell scripting, and network security.', 2200, 2900, 9656, 1, 24, 0),
	(73, 'Data analyst with a background in data visualization, reporting, and working with NoSQL databases.', 2300, 3000, 9701, 1, 25, 1),
	(74, 'Backend developer with experience in Microservices and working with GraphQL for API integrations.', 2600, 3300, 1, 1, 26, 1),
	(75, 'Test engineer with expertise in TDD, JUnit, and Mockito, looking to improve software quality through automated testing.', 2200, 2800, 9735, 1, 27, 0),
	(76, 'Agile practitioner with strong experience in JIRA and helping teams adopt Agile methodologies.', 2400, 3100, 9292, 1, 28, 1),
	(77, 'HTML/CSS developer with a focus on clean and responsive web design, looking to join creative teams.', 2200, 2800, 9656, 1, 29, 0),
	(78, 'Security-focused software engineer with experience in Ansible and Terraform for infrastructure automation.', 2600, 3300, 9701, 1, 30, 1),
	(79, 'Experienced software architect with strong background in mentoring teams and leading software projects.', 2800, 3500, 1, 1, 31, 1);

-- Дъмп структура за таблица job_match.job_applications_match_requests
CREATE TABLE IF NOT EXISTS `job_applications_match_requests` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `job_application_id` int(100) NOT NULL,
  `job_ad_id` int(100) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `matches_job_ads_id_fk` (`job_ad_id`),
  KEY `matches_job_applications_id_fk` (`job_application_id`),
  CONSTRAINT `matches_job_ads_id_fk` FOREIGN KEY (`job_ad_id`) REFERENCES `job_ads` (`id`),
  CONSTRAINT `matches_job_applications_id_fk` FOREIGN KEY (`job_application_id`) REFERENCES `job_applications` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Дъмп данни за таблица job_match.job_applications_match_requests: ~0 rows (приблизителен брой)

-- Дъмп структура за таблица job_match.locations
CREATE TABLE IF NOT EXISTS `locations` (
  `id` int(11) NOT NULL,
  `city_name` varchar(40) NOT NULL,
  `country_iso` varchar(5) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `locations_pk_2` (`city_name`),
  UNIQUE KEY `locations_pk` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Дъмп данни за таблица job_match.locations: ~7 rows (приблизителен брой)
INSERT IGNORE INTO `locations` (`id`, `city_name`, `country_iso`) VALUES
	(1, 'Home', 'Hm'),
	(9292, 'Burgas', 'BG'),
	(9656, 'Plovdiv', 'BG'),
	(9701, 'Sofia', 'BG'),
	(9735, 'Varna', 'BG'),
	(50388, 'London', 'GB'),
	(50481, 'Manchester', 'GB'),
	(97170, 'Belgrade', 'RS');

-- Дъмп структура за таблица job_match.professionals
CREATE TABLE IF NOT EXISTS `professionals` (
  `id` int(11) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `summary` varchar(200) NOT NULL,
  `status_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `professionals_user_id_fk` FOREIGN KEY (`id`) REFERENCES `users_details` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Дъмп данни за таблица job_match.professionals: ~21 rows (приблизителен брой)
INSERT IGNORE INTO `professionals` (`id`, `first_name`, `last_name`, `summary`, `status_id`) VALUES
	(10, 'Petar', 'Petrov', 'Looking for my first job in the IT field', 1),
	(12, 'Emily', 'Johnson', 'Expert in creating visually stunning designs and user experiences.', 1),
	(13, 'Michael', 'Smith', 'A leader in innovative marketing strategies.', 1),
	(14, 'Jessica', 'Davis', 'Specializing in full-stack web development.', 1),
	(15, 'David', 'Martinez', 'Experts in big data analytics and insights.', 1),
	(16, 'Sophia', 'Brown', 'Master in project management and execution.', 2),
	(17, 'James', 'Wilson', 'Specializing in financial analysis and investment strategies.', 1),
	(18, 'Olivia', 'Garcia', 'Expert in human resources strategies and people management.', 1),
	(19, 'Ethan', 'Miller', 'Top-tier sales professional driving results.', 1),
	(20, 'Emma', 'Rodriguez', 'Expert in safeguarding digital environments.', 1),
	(21, 'Liam', 'Anderson', 'Leading the way in technological innovations.', 1),
	(22, 'Alex', 'Robinson', 'Passionate writer with a flair for storytelling.', 1),
	(23, 'Jessica', 'Harris', 'Experienced legal advisor specializing in corporate law.', 1),
	(24, 'Ryan', 'Walker', 'UX designer with a focus on user-centered design.', 1),
	(25, 'Emily', 'Clark', 'Business analyst with expertise in data-driven decision making.', 1),
	(26, 'David', 'Lewis', 'IT consultant specializing in infrastructure and security.', 1),
	(27, 'Sarah', 'Young', 'Public relations specialist with a talent for media outreach.', 1),
	(28, 'Daniel', 'King', 'SEO expert helping businesses rank higher in search engines.', 1),
	(29, 'Laura', 'Scott', 'Content creator with a knack for engaging digital content.', 1),
	(30, 'Matthew', 'Martinez', 'Event planner specializing in corporate events and conferences.', 1),
	(31, 'Emma', 'Taylor', 'Research scientist with a focus on innovative technologies.', 1),
	(47, 'Cody', 'Watson', 'In search of my dream job as a Phyton Developer', 1);

-- Дъмп структура за таблица job_match.qualifications
CREATE TABLE IF NOT EXISTS `qualifications` (
  `job_application_id` int(11) NOT NULL,
  `skill_id` int(11) NOT NULL,
  KEY `qualifications_job_applications_id_fk` (`job_application_id`),
  KEY `qualifications_skills_id_fk` (`skill_id`),
  CONSTRAINT `qualifications_job_applications_id_fk` FOREIGN KEY (`job_application_id`) REFERENCES `job_applications` (`id`),
  CONSTRAINT `qualifications_skills_id_fk` FOREIGN KEY (`skill_id`) REFERENCES `skills` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Дъмп данни за таблица job_match.qualifications: ~201 rows (приблизителен брой)
INSERT IGNORE INTO `qualifications` (`job_application_id`, `skill_id`) VALUES
	(20, 1),
	(20, 2),
	(20, 3),
	(20, 4),
	(20, 5),
	(21, 23),
	(21, 31),
	(21, 32),
	(21, 33),
	(21, 34),
	(22, 19),
	(22, 20),
	(22, 21),
	(22, 32),
	(22, 40),
	(23, 10),
	(23, 11),
	(23, 12),
	(23, 13),
	(23, 14),
	(24, 6),
	(24, 26),
	(24, 27),
	(24, 28),
	(25, 17),
	(25, 18),
	(25, 20),
	(25, 21),
	(26, 40),
	(26, 37),
	(26, 38),
	(27, 4),
	(27, 5),
	(27, 31),
	(28, 37),
	(28, 38),
	(28, 36),
	(28, 1),
	(28, 3),
	(29, 19),
	(29, 20),
	(29, 21),
	(29, 40),
	(30, 1),
	(30, 17),
	(30, 18),
	(30, 22),
	(31, 1),
	(31, 18),
	(31, 22),
	(31, 23),
	(32, 9),
	(32, 10),
	(32, 15),
	(32, 28),
	(33, 26),
	(33, 9),
	(33, 29),
	(33, 13),
	(34, 19),
	(34, 20),
	(34, 40),
	(35, 18),
	(35, 19),
	(35, 20),
	(35, 21),
	(36, 19),
	(36, 4),
	(36, 40),
	(37, 13),
	(37, 5),
	(37, 26),
	(38, 15),
	(38, 40),
	(38, 13),
	(39, 4),
	(39, 31),
	(39, 32),
	(20, 1),
	(20, 7),
	(20, 3),
	(21, 1),
	(21, 2),
	(21, 3),
	(21, 4),
	(22, 4),
	(22, 5),
	(22, 6),
	(23, 18),
	(23, 17),
	(23, 20),
	(23, 21),
	(24, 1),
	(24, 9),
	(24, 10),
	(24, 7),
	(25, 11),
	(25, 12),
	(25, 9),
	(26, 1),
	(26, 7),
	(26, 3),
	(27, 15),
	(27, 16),
	(27, 14),
	(27, 28),
	(28, 21),
	(28, 20),
	(28, 19),
	(29, 23),
	(29, 30),
	(29, 31),
	(30, 18),
	(30, 22),
	(30, 19),
	(31, 25),
	(31, 24),
	(31, 26),
	(32, 26),
	(32, 27),
	(32, 9),
	(33, 31),
	(33, 32),
	(33, 33),
	(33, 34),
	(34, 8),
	(34, 36),
	(34, 4),
	(35, 37),
	(35, 38),
	(35, 39),
	(36, 40),
	(36, 15),
	(36, 16),
	(37, 20),
	(37, 21),
	(37, 19),
	(38, 29),
	(38, 28),
	(38, 9),
	(39, 1),
	(39, 8),
	(39, 11),
	(39, 15),
	(59, 1),
	(59, 13),
	(59, 14),
	(60, 1),
	(61, 1),
	(61, 2),
	(61, 3),
	(62, 4),
	(62, 5),
	(62, 6),
	(63, 17),
	(63, 18),
	(63, 20),
	(63, 21),
	(64, 1),
	(64, 2),
	(64, 3),
	(64, 8),
	(64, 9),
	(65, 11),
	(65, 12),
	(65, 10),
	(66, 1),
	(66, 7),
	(66, 13),
	(67, 15),
	(67, 16),
	(68, 19),
	(68, 20),
	(68, 21),
	(69, 23),
	(69, 30),
	(69, 31),
	(70, 18),
	(70, 22),
	(71, 25),
	(72, 26),
	(72, 27),
	(73, 31),
	(73, 32),
	(73, 33),
	(74, 8),
	(74, 36),
	(75, 37),
	(75, 38),
	(75, 39),
	(76, 40),
	(77, 20),
	(77, 21),
	(78, 28),
	(78, 29),
	(79, 1),
	(79, 13),
	(79, 14),
	(59, 3),
	(59, 2),
	(59, 4);

-- Дъмп структура за таблица job_match.refresh_tokens
CREATE TABLE IF NOT EXISTS `refresh_tokens` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `refresh_token` varchar(10000) NOT NULL,
  `revoked` tinyint(1) NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `refresh_tokens_users_details_id_fk` (`user_id`),
  CONSTRAINT `refresh_tokens_users_details_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users_details` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=117 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Дъмп структура за таблица job_match.roles
CREATE TABLE IF NOT EXISTS `roles` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role` varchar(40) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `roles_pk_2` (`role`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Дъмп данни за таблица job_match.roles: ~3 rows (приблизителен брой)
INSERT IGNORE INTO `roles` (`id`, `role`) VALUES
	(3, 'ROLE_ADMIN'),
	(1, 'ROLE_EMPLOYER'),
	(2, 'ROLE_PROFESSIONAL');

-- Дъмп структура за таблица job_match.skills
CREATE TABLE IF NOT EXISTS `skills` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `skill` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Дъмп данни за таблица job_match.skills: ~40 rows (приблизителен брой)
INSERT IGNORE INTO `skills` (`id`, `skill`) VALUES
	(1, 'Java'),
	(2, 'Spring Framework'),
	(3, 'Hibernate'),
	(4, 'SQL'),
	(5, 'PostgreSQL'),
	(6, 'MySQL'),
	(7, 'REST APIs'),
	(8, 'Microservices'),
	(9, 'Docker'),
	(10, 'Kubernetes'),
	(11, 'AWS'),
	(12, 'Azure'),
	(13, 'Git'),
	(14, 'Maven'),
	(15, 'Jenkins'),
	(16, 'CI/CD'),
	(17, 'Angular'),
	(18, 'React'),
	(19, 'JavaScript'),
	(20, 'HTML'),
	(21, 'CSS'),
	(22, 'TypeScript'),
	(23, 'Python'),
	(24, 'C#'),
	(25, 'C++'),
	(26, 'Linux'),
	(27, 'Shell Scripting'),
	(28, 'Terraform'),
	(29, 'Ansible'),
	(30, 'Machine Learning'),
	(31, 'Data Analysis'),
	(32, 'Data Visualization'),
	(33, 'NoSQL Databases'),
	(34, 'MongoDB'),
	(35, 'Redis'),
	(36, 'GraphQL'),
	(37, 'TDD (Test-Driven Development)'),
	(38, 'JUnit'),
	(39, 'Mockito'),
	(40, 'JIRA');

-- Дъмп структура за таблица job_match.skills_required
CREATE TABLE IF NOT EXISTS `skills_required` (
  `job_ad_id` int(11) NOT NULL,
  `skill_id` int(11) NOT NULL,
  KEY `skills_required_job_ads_id_fk` (`job_ad_id`),
  KEY `skills_required_skills_id_fk` (`skill_id`),
  CONSTRAINT `skills_required_job_ads_id_fk` FOREIGN KEY (`job_ad_id`) REFERENCES `job_ads` (`id`),
  CONSTRAINT `skills_required_skills_id_fk` FOREIGN KEY (`skill_id`) REFERENCES `skills` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Дъмп данни за таблица job_match.skills_required: ~82 rows (приблизителен брой)
INSERT IGNORE INTO `skills_required` (`job_ad_id`, `skill_id`) VALUES
	(1, 1),
	(1, 2),
	(1, 3),
	(1, 13),
	(1, 16),
	(2, 4),
	(2, 5),
	(2, 31),
	(2, 32),
	(2, 33),
	(3, 19),
	(3, 20),
	(3, 21),
	(3, 32),
	(3, 40),
	(4, 10),
	(4, 11),
	(4, 12),
	(4, 15),
	(4, 16),
	(5, 9),
	(5, 26),
	(5, 27),
	(5, 13),
	(5, 29),
	(6, 18),
	(6, 19),
	(6, 20),
	(6, 21),
	(6, 32),
	(7, 40),
	(7, 16),
	(7, 15),
	(8, 31),
	(8, 4),
	(8, 5),
	(9, 37),
	(9, 38),
	(9, 39),
	(9, 1),
	(9, 3),
	(10, 19),
	(10, 20),
	(10, 21),
	(10, 40),
	(11, 1),
	(11, 17),
	(11, 18),
	(11, 19),
	(11, 22),
	(12, 1),
	(12, 18),
	(12, 22),
	(12, 23),
	(13, 9),
	(13, 10),
	(13, 15),
	(13, 27),
	(13, 28),
	(14, 26),
	(14, 9),
	(14, 29),
	(14, 13),
	(15, 19),
	(15, 20),
	(15, 40),
	(16, 18),
	(16, 19),
	(16, 20),
	(16, 21),
	(17, 19),
	(17, 4),
	(17, 40),
	(18, 13),
	(18, 5),
	(18, 26),
	(19, 15),
	(19, 40),
	(19, 13),
	(20, 4),
	(20, 31),
	(20, 32);

-- Дъмп структура за таблица job_match.statuses
CREATE TABLE IF NOT EXISTS `statuses` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `status` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `statuses_pk_2` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Дъмп данни за таблица job_match.statuses: ~5 rows (приблизителен брой)
INSERT IGNORE INTO `statuses` (`id`, `status`) VALUES
	(1, 'Active'),
	(3, 'Archived'),
	(2, 'Busy'),
	(5, 'Hidden'),
	(4, 'Matched');

-- Дъмп структура за таблица job_match.successful_matches
CREATE TABLE IF NOT EXISTS `successful_matches` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `professional_id` int(11) NOT NULL,
  `employer_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `successful_matches_employers_id_fk` (`employer_id`),
  KEY `successful_matches_professionals_id_fk` (`professional_id`),
  CONSTRAINT `successful_matches_employers_id_fk` FOREIGN KEY (`employer_id`) REFERENCES `employers` (`id`),
  CONSTRAINT `successful_matches_professionals_id_fk` FOREIGN KEY (`professional_id`) REFERENCES `professionals` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Дъмп данни за таблица job_match.successful_matches: ~1 rows (приблизителен брой)
INSERT IGNORE INTO `successful_matches` (`id`, `professional_id`, `employer_id`) VALUES
	(6, 16, 8);

-- Дъмп структура за таблица job_match.users_details
CREATE TABLE IF NOT EXISTS `users_details` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(10000) NOT NULL,
  `roles` varchar(30) NOT NULL,
  `location_id` int(11) NOT NULL,
  `email` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `users_details_uk` (`email`),
  UNIQUE KEY `users_details_uk_2` (`id`),
  UNIQUE KEY `users_details_uk_3` (`username`),
  KEY `users_details_locations_city_id_fk` (`location_id`),
  CONSTRAINT `users_details_locations_city_id_fk` FOREIGN KEY (`location_id`) REFERENCES `locations` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Дъмп данни за таблица job_match.users_details: ~34 rows (приблизителен брой)
INSERT IGNORE INTO `users_details` (`id`, `username`, `password`, `roles`, `location_id`, `email`) VALUES
	(8, 'testCompany', '$2a$10$etceO88oZOnCcujqiClaPuai7gC4uwaB5CxwxrBAmseCp/vRGRBdu', 'EMPLOYER', 97170, 'ltestCompany@innovation.com'),
	(10, 'petarpetrov', '$2a$10$14dav.Np1I2Z7AXSWu4giOfXq32k9dd3QA8oISf1drb5tprcsFdjS', 'PROFESSIONAL', 50481, 'petarpetrov@test.com'),
	(12, 'designwizard', '$2a$10$H5ShpyzIVNvdOTpjg8xTgOY6uNYByGW93tdI1j9LZb/uWWvq1zFxm', 'PROFESSIONAL', 9701, 'wizard@design.com'),
	(13, 'marketingguru', '$2a$10$9Mn0CzsiNsQLsgZsecWDyuoBcoaE33kSGwAn1ladMzjKVu.fysZ7q', 'PROFESSIONAL', 9292, 'guru@marketing.com'),
	(14, 'devexpert', '$2a$10$IU/QbWOJsbLkzzudGtKDzuB2Hz6tH6OBgNRkDZVbQEl.onnhlCFpu', 'PROFESSIONAL', 9292, 'expert@development.com'),
	(15, 'datawhiz', '$2a$10$U3IfAI4A2tZKge5L8kbpwOh9yuJojJVJTPieAUQYCUppmSRn946Ra', 'PROFESSIONAL', 9292, 'whiz@data.com'),
	(16, 'projectmaster', '$2a$10$eV.rHDCUNGfvbWOaEaNQPu1pThsS1vaypEFbX/rD7xwjKbuid1u82', 'PROFESSIONAL', 9292, 'master@project.com'),
	(17, 'financepro', '$2a$10$KmUbShHdP0Cfyijm5gA41eMpvCbjelwuc5d2IKOeNUuryB4ypuc6.', 'PROFESSIONAL', 9292, 'pro@finance.com'),
	(18, 'hrstrategist', '$2a$10$67SMefqiyN7yFp2Jtr2VzOxWGzIMzqO4MTpp5ryiucUSTk07PCeV6', 'PROFESSIONAL', 9292, 'strategist@hr.com'),
	(19, 'saleschamp', '$2a$10$L5C/qtOvLA2gU6g1C.U9nOO7bzz/azDb9CZ8ohJq8fsN..Sb9kJMy', 'PROFESSIONAL', 9292, 'champ@sales.com'),
	(20, 'cybersecurityguru', '$2a$10$6JHUP9BbWHTzb3k9hepoAehvqmCFrTZOetcoZcMemzYbvXnCYwv3K', 'PROFESSIONAL', 9292, 'guru@cybersecurity.com'),
	(21, 'innovatorleader', '$2a$10$WFk0N6CoHPRU9mKACH3FP.vYA7ACpW4GA9WHkoWCY9clEukjPcKkq', 'PROFESSIONAL', 9292, 'leader@innovate.com'),
	(22, 'creativewriter', '$2a$10$d/VQ4hqq6UDdGw3e28ET0.BQgrcqxf9FEFQ0tSQnQktoTK.ig9n/y', 'PROFESSIONAL', 9701, 'writer@creative.com'),
	(23, 'legaladvisor', '$2a$10$YDVxty8zyt6ptbRLiN/cD.xM/bSMSqCx/SC79.6DH4veo1cZzWWN.', 'PROFESSIONAL', 9701, 'advisor@legal.com'),
	(24, 'uxdesigner', '$2a$10$gE3ZrayjyTltoczKditvSOYRY06WVOnV5fqPZbciYSJ6fsu9/u.C.', 'PROFESSIONAL', 9701, 'designer@ux.com'),
	(25, 'businessanalyst', '$2a$10$2frpemIR9qOVJmoYMDp01uKsRSFDHVhLABjytEt3CipUFpAmDwRJW', 'PROFESSIONAL', 9701, 'analyst@business.com'),
	(26, 'itconsultant', '$2a$10$Szjs4O2D7qZxJEYkGVL.O.DCsPTQ5k/L4nJL2HOKFv2K47xDrd.X2', 'PROFESSIONAL', 9701, 'consultant@it.com'),
	(27, 'prspecialist', '$2a$10$AXoDkUpatqlKbtdsRtCoE.IKfpjYeSe221K3ykCiJ9VSMmPfD9CIO', 'PROFESSIONAL', 9701, 'specialist@pr.com'),
	(28, 'seoexpert', '$2a$10$5guErpoDHQNGA6V/H5KnNu0GeemEnldd7zW3kVXxsnYLoNJbO0Gfm', 'PROFESSIONAL', 9701, 'expert@seo.com'),
	(29, 'contentcreator', '$2a$10$AuFinnTtlKzXF5WQhwb4MeZ7P7h1SboB.dCzfX2jtITm1zJGwqli2', 'PROFESSIONAL', 9701, 'creator@content.com'),
	(30, 'eventplanner', '$2a$10$yRfbPYsLg3A6BYXccBF10uQ4y/d9y88SfEVxfzVrt.9Eg35FOTJg2', 'PROFESSIONAL', 9701, 'planner@event.com'),
	(31, 'researchscientist', '$2a$10$1pY1QEAQQn8FSK7ULkxdweZ6.8Dnw8dRXAlQvW.gi1JuRLi2DTu6W', 'PROFESSIONAL', 9701, 'scientist@research.com'),
	(32, 'techsolutions', '$2a$10$gvXt6ba0SdFbcIeHKt9hIuUXmsMVOfbd7rd5Fdt.svB08yj1WJvMi', 'EMPLOYER', 9701, 'contact@techsolutions.com'),
	(33, 'greenenergy', '$2a$10$1L0w7e1jWjoV133zIkrcXeSzHqwD8KKLu/VgUNhuxF4/ZH1/W5Upa', 'EMPLOYER', 9701, 'info@greenenergy.com'),
	(34, 'fintechhub', '$2a$10$E9M82TMzADS2isksdKXJs.z/nn66NE9J4Huzte3I/uceUSdnjrvCK', 'EMPLOYER', 9701, 'support@fintechhub.com'),
	(35, 'healthtech', '$2a$10$3VRLIaoFlitban2Xwle4pe2gVbCvMtccxhJUMKIFFfuXJhYorz9Ke', 'EMPLOYER', 9701, 'contact@healthtech.com'),
	(36, 'edutech', '$2a$10$OLqQKQs7saO3ZR8cSJXPoOkI/DZS812/F24KIan8YZgkR5Uu.nNUu', 'EMPLOYER', 9701, 'info@edutech.com'),
	(37, 'cybersecure', '$2a$10$8yTacu/HaccRZ5YsMh.NP.lAdzJK2yzMmOe.dMLS.dSfE29CcWDVa', 'EMPLOYER', 9701, 'support@cybersecure.com'),
	(38, 'foodinnovations', '$2a$10$BrRON6ZoO2YiGfkQMH5ATeF3wjQHlzvf8aEzzVhh9jO8lnX7qvg9G', 'EMPLOYER', 9701, 'contact@foodinnovations.com'),
	(39, 'mediahub', '$2a$10$1wC4qBcYLVoMSNs3Zp1uTut69gOgsqWF8Fys1W1FMxSC6GbCjwba6', 'EMPLOYER', 9701, 'info@mediahub.com'),
	(40, 'retailtech', '$2a$10$qzK3A1scoAbbUj90Hro9n.8wszvzSleo9XDKFUBfFe8EARZOTbwVe', 'EMPLOYER', 9701, 'support@retailtech.com'),
	(41, 'smarttransport', '$2a$10$JJ26.jgYLQ6IHTuPTsHQJ.ydS6C6ywIKlnDy3gRiMExHsSI2JyCXm', 'EMPLOYER', 9701, 'contact@smarttransport.com'),
	(42, 'innovativetech', '$2a$10$anJKaL.wKCV1kH14vGhqeO8FzU0zKqO.C2eJL/b6.gPTAFYbWeAd.', 'EMPLOYER', 9656, 'info@innovativetech.com'),
	(43, 'solarenergy', '$2a$10$fcayetAwRqF8uM6p56ZulO/x4.zhVh5WDZPJQnCp637mrunCcyzOu', 'EMPLOYER', 9656, 'contact@solarenergy.com'),
	(44, 'biotechlab', '$2a$10$sQHpf9/5b.f6OuZ8EeJof.OjOZqNIsRsMh5Mv2Y0..4KBDzIrfC32', 'EMPLOYER', 9656, 'support@biotechlab.com'),
	(45, 'agritech', '$2a$10$NxQEpz0BV374e4sccYR/C.wuHHngNSu9riBOlSDk3jP32WSnsf9Y6', 'EMPLOYER', 9656, 'info@agritech.com'),
	(46, 'sankov', '$2a$10$4rZnV0E6ejrwFxwN.ydLAORJ40EAOrD29MQnI1aH5iPD6i8dHD4se', 'ADMIN', 1, 'sankov@example.com'),
	(47, 'codydiesel', '$2a$10$4rZnV0E6ejrwFxwN.ydLAORJ40EAOrD29MQnI1aH5iPD6i8dHD4se', 'PROFESSIONAL', 9701, 'cody@research.com'),
	(48, 'roboticslab', '$2a$10$Vw6MO9J7HPYNw4TEfruKaesd3LTXRKe2mIGZC7zoiyDWgqtGNLjY.', 'EMPLOYER', 9656, 'contact@roboticslab.com');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
