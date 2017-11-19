CREATE DATABASE diffbot;

USE diffbot;

CREATE TABLE diff_result_t (
  id                       BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  serviced_date            DATETIME        NOT NULL,
  PRIMARY KEY (id)
);
CREATE UNIQUE INDEX target_id_idx ON diff_result_t (target_postable_id);


CREATE TABLE diffbot_url_t (
  id                 BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  diff_result_id    BIGINT UNSIGNED NOT NULL,
  diffed_url        TEXT,
  original_url       TEXT            NOT NULL,
  last_diffed       DATETIME,
  PRIMARY KEY (id),
  FOREIGN KEY result_id_fk (diff_result_id) REFERENCES diff_result_t (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);
CREATE INDEX rslt_id_idx ON diffbot_url_t (diff_result_id);


CREATE TABLE blacklisted_user_t (
  id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  username     VARCHAR(128)    NOT NULL,
  date_created DATETIME        NOT NULL,
  blacklistReason       TEXT            NOT NULL,
  PRIMARY KEY (id)
);
CREATE INDEX username ON blacklisted_user_t (username);


CREATE TABLE blacklisted_subreddit_t (
  id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  name         VARCHAR(128)    NOT NULL,
  date_created DATETIME        NOT NULL,
  note         TEXT            NOT NULL,
  PRIMARY KEY (id)
);
CREATE INDEX sub_name_idx ON blacklisted_subreddit_t (name);


CREATE TABLE reddit_polling_time_t (
  id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  date         DATETIME        NOT NULL,
  PRIMARY KEY (id)
);


CREATE TABLE auth_polling_time_t (
  id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  date         DATETIME        NOT NULL,
  success      BOOLEAN         NOT NULL,
  PRIMARY KEY (id)
);


CREATE TABLE scraped_url_t (
  id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  date         DATETIME        NOT NULL,
  url          TEXT            NOT NULL,
  PRIMARY KEY (id)
);


CREATE USER diffbot@localhost
IDENTIFIED BY 'password';

GRANT SELECT, INSERT, UPDATE, DELETE, DROP, ALTER, CREATE TEMPORARY TABLES ON diffbot.* TO diffbot@localhost;

-- Newer installs of mysql will use unix auth, which we don't want
USE mysql;
UPDATE user SET plugin ='mysql_native_password' WHERE User = 'diffbot';

