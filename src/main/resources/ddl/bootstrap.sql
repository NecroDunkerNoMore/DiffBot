CREATE DATABASE diffbot;
ALTER DATABASE diffbot
CHARACTER SET utf8
COLLATE utf8_general_ci;

USE diffbot;

-- Root diff table, has one diff_patch child
CREATE TABLE diff_result_t (
  id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  diff_url_id     BIGINT UNSIGNED,
  archived_url_id BIGINT UNSIGNED,
  date_captured   DATETIME        NOT NULL,
  PRIMARY KEY (id)
);

-- Is a child of diff_result, has set of diff_delta children
CREATE TABLE diff_patch_t (
  id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  date_captured DATETIME        NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY diff_result_id_fk (id) REFERENCES diff_result_t (id)
);

-- Is a child of diff_patch, has set of diff_line children
CREATE TABLE diff_delta_t (
  id             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  diff_patch_id  BIGINT UNSIGNED NOT NULL,
  delta_type     TEXT(16)        NOT NULL,
  date_created   DATETIME        NOT NULL,
  start_position INT             NOT NULL,
  end_position   INT             NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY diff_patch_id_fk (diff_patch_id) REFERENCES diff_patch_t (id)
);

-- Is a child of diff_delta, contains actual text line
CREATE TABLE diff_line_t (
  id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  diff_delta_id BIGINT UNSIGNED NOT NULL,
  line          TEXT            NOT NULL,
  line_type     VARCHAR(16)     NOT NULL,
  PRIMARY KEY (id, line_type),
  FOREIGN KEY diff_delta_id_fk (diff_delta_id) REFERENCES diff_delta_t (id)
);

-- Is a child of diff_result, represents a webpage, contains page's raw HTML
CREATE TABLE html_snapshot_t (
  id             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  diff_result_id BIGINT UNSIGNED,
  diff_url_id    BIGINT UNSIGNED,
  capture_type   VARCHAR(16)     NOT NULL,
  date_captured  DATETIME        NOT NULL,
  raw_html       MEDIUMTEXT,
  PRIMARY KEY (id),
  FOREIGN KEY diff_res_id_fk (diff_result_id) REFERENCES diff_result_t (id)
);

-- Is a child of diff_result and html_snapshot, represents a webpage URL
CREATE TABLE diff_url_t (
  id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  source_url   TEXT            NOT NULL,
  date_created DATETIME        NOT NULL,
  active       BOOLEAN         NOT NULL DEFAULT TRUE,
  PRIMARY KEY (id)
);


CREATE TABLE archived_url_t (
  id             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  diff_url_id    BIGINT UNSIGNED NOT NULL,
  archived_link  TEXT            NOT NULL,
  date_archived  DATETIME,
  PRIMARY KEY (id),
  FOREIGN KEY diff_url_id_fk_idx (diff_url_id) REFERENCES diff_url_t (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);


CREATE TABLE reddit_user_t (
  id               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  username         VARCHAR(128)    NOT NULL,
  date_created     DATETIME        NOT NULL,
  blacklist_reason TEXT,
  is_blacklisted   BOOLEAN         NOT NULL DEFAULT FALSE,
  is_subscribed    BOOLEAN         NOT NULL DEFAULT TRUE,
  PRIMARY KEY (id)
);
CREATE INDEX username
  ON reddit_user_t (username);


CREATE TABLE reddit_polling_time_t (
  id      BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  date    DATETIME        NOT NULL,
  success BOOLEAN         NOT NULL,
  PRIMARY KEY (id)
);


CREATE TABLE auth_polling_time_t (
  id      BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  date    DATETIME        NOT NULL,
  success BOOLEAN         NOT NULL,
  PRIMARY KEY (id)
);


CREATE TABLE url_polling_time_t (
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

