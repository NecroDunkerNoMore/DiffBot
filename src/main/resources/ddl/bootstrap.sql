CREATE DATABASE diffbot;

USE diffbot;

CREATE TABLE diff_result_t (
  id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  serviced_date DATETIME        NOT NULL,
  PRIMARY KEY (id)
);


CREATE TABLE diff_url_t (
  id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  source_url TEXT,
  PRIMARY KEY (id)
);


CREATE TABLE reddit_user_t (
  id               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  username         VARCHAR(128)    NOT NULL,
  date_created     DATETIME        NOT NULL,
  blacklist_reason TEXT,
  is_blacklisted   BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY (id)
);
CREATE INDEX username
  ON reddit_user_t (username);


CREATE TABLE reddit_polling_time_t (
  id   BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  date DATETIME        NOT NULL,
  PRIMARY KEY (id)
);


CREATE TABLE auth_polling_time_t (
  id      BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  date    DATETIME        NOT NULL,
  success BOOLEAN         NOT NULL,
  PRIMARY KEY (id)
);


CREATE USER diffbot@localhost
  IDENTIFIED BY 'password';

GRANT SELECT, INSERT, UPDATE, DELETE, DROP, ALTER, CREATE TEMPORARY TABLES ON diffbot.* TO diffbot@localhost;

-- Newer installs of mysql will use unix auth, which we don't want
USE mysql;
UPDATE user
SET plugin = 'mysql_native_password'
WHERE User = 'diffbot';

