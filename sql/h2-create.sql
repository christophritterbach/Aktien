CREATE TABLE aktien (
      id                IDENTITY(1)
    , wkn               VARCHAR(6)    NOT NULL
    , isin              VARCHAR(12)   NOT NULL
    , bezeichnung       VARCHAR(100)  NOT NULL
    , UNIQUE KEY  (id)
    , PRIMARY KEY (id)
);

CREATE TABLE kauf (
      id                 IDENTITY(1)
    , aktien_id          int(10) NOT NULL
    , kauf_datum         date NOT NULL
    , anzahl             DECIMAL(12,4) NOT NULL
    , kurs               DECIMAL(8,4)  NOT NULL
    , betrag             DECIMAL(10,2) NOT NULL
    , kosten             DECIMAL(10,2) NOT NULL
    , bemerkung          VARCHAR(1000)
    , UNIQUE      (id)
    , PRIMARY KEY (id)
);

CREATE TABLE dividende (
      id                 IDENTITY(1)
    , aktien_id          int(10) NOT NULL
    , zahl_datum         date NOT NULL
    , pro_stueck         DECIMAL(5,3)  NOT NULL
    , gesamt             DECIMAL(8,3)  NOT NULL
    , quellensteuer      DECIMAL(8,3)  NOT NULL
    , devisenkurs        DECIMAL(10,5) NOT NULL
    , waehrung           VARCHAR(3)    NOT NULL
    , UNIQUE      (id)
    , PRIMARY KEY (id)
);

CREATE TABLE version (
      id                 IDENTITY(1)
    , name VARCHAR(255) NOT NULL
    , version int(10) NOT NULL
    , UNIQUE (id)
    , PRIMARY KEY (id)
);

CREATE INDEX idx_aktien_wkn ON aktien(wkn);
CREATE INDEX idx_aktien_isin ON aktien(isin);
CREATE INDEX idx_kauf_datum ON kauf(kauf_datum);
CREATE INDEX idx_dividende_datum ON dividende(zahl_datum);
CREATE INDEX idx_kauf_fk_aktien ON kauf(aktien_id);
CREATE INDEX idx_dividende_fk_aktien ON dividende(aktien_id);
ALTER TABLE kauf      ADD CONSTRAINT fk_kauf_aktien      FOREIGN KEY (aktien_id) REFERENCES aktien (id);
ALTER TABLE dividende ADD CONSTRAINT fk_dividenen_aktien FOREIGN KEY (aktien_id) REFERENCES aktien (id);
-- Bevor wir Daten speichern koennen, muessen wir ein COMMIT machen
COMMIT;

INSERT INTO version (name,version) values ('db',1);
  
COMMIT;

CREATE VIEW v_aktien AS SELECT aktien.id, aktien.wkn, aktien.isin, aktien.bezeichnung, NVL(SUM(kauf.Anzahl), 0) AS anzahl, NVL(SUM(kauf.betrag), 0) AS betrag, NVL(SUM(kauf.kosten), 0) AS kosten
 , NVL((SELECT SUM(gesamt) FROM dividende where dividende.aktien_id = aktien.id), 0) as gesamt FROM aktien LEFT OUTER JOIN kauf ON kauf.aktien_id = aktien.id GROUP BY wkn, isin, bezeichnung;

CREATE VIEW v_kauf AS SELECT kauf.id, kauf.aktien_id, kauf.kauf_datum, kauf.anzahl, kauf.kurs, kauf.betrag, kauf.kosten, kauf.bemerkung
 , aktien.wkn, aktien.isin, aktien.bezeichnung FROM kauf JOIN aktien ON aktien.id = kauf.aktien_id;

CREATE VIEW v_dividende AS SELECT dividende.id, dividende.aktien_id, dividende.zahl_datum, dividende.pro_stueck, dividende.gesamt, dividende.quellensteuer, dividende.devisenkurs, dividende.waehrung
 , aktien.wkn, aktien.isin, aktien.bezeichnung FROM dividende JOIN aktien ON aktien.id = dividende.aktien_id;
