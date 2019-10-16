CREATE TABLE RESULTS
(
    ID      BIGINT PRIMARY KEY  NOT NULL,
    DATE    TIMESTAMP           NOT NULL,
    LEVEL   INT                 NOT NULL,
    NAME    VARCHAR(255)        NOT NULL,
    SCORE   BIGINT              NOT NULL
);
CREATE SEQUENCE RESULT_ID_SEQ
  AS BIGINT MINVALUE 1 NO MAXVALUE START WITH 1 INCREMENT BY 1 NO CYCLE;