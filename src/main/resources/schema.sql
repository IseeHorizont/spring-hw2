DROP TABLE IF EXISTS PERSON;
CREATE TABLE IF NOT EXISTS PERSON
(
    ID BIGSERIAL UNIQUE PRIMARY KEY,
    FULL_NAME VARCHAR (255),
    TITLE VARCHAR (255),
    AGE INTEGER
);

DROP TABLE IF EXISTS BOOK;
CREATE TABLE IF NOT EXISTS BOOK
(
    ID BIGSERIAL UNIQUE PRIMARY KEY,
    TITLE VARCHAR (255),
    AUTHOR VARCHAR (255),
    PAGE_COUNT INTEGER,
    USER_ID BIGINT
);
