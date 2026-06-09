-- ================================================================
-- Progetto di Ingegneria del Software - Prof.ssa Anna Rita Fasolino
-- WMS PRO — Script di inizializzazione e popolamento del database
-- Università degli Studi di Napoli Federico II
-- ================================================================

CREATE DATABASE IF NOT EXISTS magazzino;
USE magazzino;

-- ── Pulizia (ordine inverso per rispettare le FK) ───────────────
DROP TABLE IF EXISTS movimento;
DROP TABLE IF EXISTS prodotto;
DROP TABLE IF EXISTS operatore;
DROP TABLE IF EXISTS responsabile;
DROP TABLE IF EXISTS utente;
DROP TABLE IF EXISTS posizione;

-- ── Tabelle ─────────────────────────────────────────────────────

CREATE TABLE utente (
                        IdUtente  VARCHAR(50)  PRIMARY KEY,
                        Nome      VARCHAR(50)  NOT NULL,
                        Cognome   VARCHAR(50)  NOT NULL,
                        Email     VARCHAR(100) NOT NULL UNIQUE,
                        Password  VARCHAR(100) NOT NULL,
                        ruolo     VARCHAR(20)  NOT NULL
);

CREATE TABLE operatore (
                           IdUtenteOperatore VARCHAR(50) PRIMARY KEY,
                           FOREIGN KEY (IdUtenteOperatore) REFERENCES utente(IdUtente)
);

CREATE TABLE responsabile (
                              IdUtenteResponsabile VARCHAR(50) PRIMARY KEY,
                              FOREIGN KEY (IdUtenteResponsabile) REFERENCES utente(IdUtente)
);

CREATE TABLE posizione (
                           IdPos    VARCHAR(50) PRIMARY KEY,
                           Area     VARCHAR(50),
                           Scaffale VARCHAR(50)
);

CREATE TABLE prodotto (
                          IdProd               VARCHAR(10)  PRIMARY KEY,
                          Nome                 VARCHAR(100) NOT NULL,
                          Categoria            VARCHAR(50)  NOT NULL,
                          Descrizione          TEXT,
                          QtaDisp              INT          NOT NULL DEFAULT 0,
                          SogliaMinima         INT          DEFAULT NULL,
                          IdPos                VARCHAR(50),
                          IdUtenteResponsabile VARCHAR(50),
                          FOREIGN KEY (IdPos)                REFERENCES posizione(IdProd),
                          FOREIGN KEY (IdUtenteResponsabile) REFERENCES responsabile(IdUtenteResponsabile)
);

CREATE TABLE movimento (
                           IdMovimento      INT          PRIMARY KEY AUTO_INCREMENT,
                           QtaProd          INT          NOT NULL,
                           Data             DATE         NOT NULL,
                           TipoMovimento    VARCHAR(10)  NOT NULL,
                           IdProd           VARCHAR(10)  NOT NULL,
                           IdUtenteOperatore VARCHAR(50) NOT NULL,
                           FOREIGN KEY (IdProd)            REFERENCES prodotto(IdProd),
                           FOREIGN KEY (IdUtenteOperatore) REFERENCES utente(IdUtente)
);

-- ── Utenti ──────────────────────────────────────────────────────

INSERT INTO utente VALUES
                       ('RESP-001', 'Mario',    'Rossi',    'mario.rossi@wms.it',    'pass123', 'Responsabile'),
                       ('RESP-002', 'Laura',    'Bianchi',  'laura.bianchi@wms.it',  'pass123', 'Responsabile'),
                       ('OPE-001',  'Giovanni', 'Verdi',    'g.verdi@wms.it',        'pass123', 'Operatore'),
                       ('OPE-002',  'Anna',     'Neri',     'a.neri@wms.it',         'pass123', 'Operatore'),
                       ('OPE-003',  'Carlo',    'Esposito', 'c.esposito@wms.it',     'pass123', 'Operatore');

INSERT INTO responsabile VALUES ('RESP-001'), ('RESP-002');
INSERT INTO operatore    VALUES ('OPE-001'),  ('OPE-002'), ('OPE-003');

-- ── Posizioni ───────────────────────────────────────────────────

INSERT INTO posizione VALUES
                          ('POS-A1', 'Area A', 'Scaffale 1'),
                          ('POS-A2', 'Area A', 'Scaffale 2'),
                          ('POS-A3', 'Area A', 'Scaffale 3'),
                          ('POS-B1', 'Area B', 'Scaffale 1'),
                          ('POS-B2', 'Area B', 'Scaffale 2'),
                          ('POS-B3', 'Area B', 'Scaffale 3'),
                          ('POS-C1', 'Area C', 'Scaffale 1'),
                          ('POS-C2', 'Area C', 'Scaffale 2'),
                          ('POS-C3', 'Area C', 'Scaffale 3'),
                          ('POS-D1', 'Area D', 'Scaffale 1');

-- ── Prodotti ────────────────────────────────────────────────────

INSERT INTO prodotto VALUES
                         ('P-001', 'Guanti latex',       'Sicurezza',   'Guanti monouso in lattice taglia M',     3,  10, 'POS-A1', 'RESP-001'),
                         ('P-002', 'Nastro da imballo',  'Imballaggio', 'Nastro adesivo trasparente 50mm x 66m',  1,   5, 'POS-A2', 'RESP-001'),
                         ('P-003', 'Scatole S',          'Imballaggio', 'Scatole cartone 20x20x15 cm',            4,  20, 'POS-A3', 'RESP-001'),
                         ('P-004', 'Etichette adesive',  'Cancelleria', 'Etichette 70x37mm confezione 100pz',     8,  15, 'POS-B1', 'RESP-001'),
                         ('P-005', 'Pallets 80x120',     'Logistica',   'Pallet in legno standard EUR',           2,   6, 'POS-B2', 'RESP-001'),
                         ('P-006', 'Guanti nitrile',     'Sicurezza',   'Guanti monouso in nitrile taglia L',    45,  10, 'POS-B3', 'RESP-002'),
                         ('P-007', 'Cutter 18mm',        'Utensileria', 'Taglierino professionale lama 18mm',    12,   5, 'POS-C1', 'RESP-002'),
                         ('P-008', 'Fascette cavi',      'Elettrico',   'Fascette in nylon 200x3.6mm pz 100',   180,  20, 'POS-C2', 'RESP-002'),
                         ('P-009', 'Elmetti gialli',     'Sicurezza',   'Casco protettivo EN397 colore giallo',  15,   8, 'POS-C3', 'RESP-002'),
                         ('P-010', 'Bolla di accomp.',   'Modulistica', 'Moduli bolla accompagnamento 50x3 copie', 30, 10, 'POS-D1', 'RESP-002');

-- ── Movimenti (ultimi 90 giorni) ────────────────────────────────

-- 90 giorni fa
INSERT INTO movimento (QtaProd, Data, TipoMovimento, IdProd, IdUtenteOperatore) VALUES
                                                                                    (50, DATE_SUB(CURDATE(), INTERVAL 89 DAY), 'Carico',  'P-001', 'OPE-001'),
                                                                                    (30, DATE_SUB(CURDATE(), INTERVAL 88 DAY), 'Carico',  'P-002', 'OPE-002'),
                                                                                    (80, DATE_SUB(CURDATE(), INTERVAL 87 DAY), 'Carico',  'P-003', 'OPE-001'),
                                                                                    (20, DATE_SUB(CURDATE(), INTERVAL 86 DAY), 'Scarico', 'P-001', 'OPE-003'),
                                                                                    (10, DATE_SUB(CURDATE(), INTERVAL 85 DAY), 'Scarico', 'P-002', 'OPE-002');

-- 60 giorni fa
INSERT INTO movimento (QtaProd, Data, TipoMovimento, IdProd, IdUtenteOperatore) VALUES
                                                                                    (40, DATE_SUB(CURDATE(), INTERVAL 60 DAY), 'Carico',  'P-004', 'OPE-001'),
                                                                                    (25, DATE_SUB(CURDATE(), INTERVAL 59 DAY), 'Carico',  'P-005', 'OPE-002'),
                                                                                    (15, DATE_SUB(CURDATE(), INTERVAL 58 DAY), 'Scarico', 'P-003', 'OPE-003'),
                                                                                    (10, DATE_SUB(CURDATE(), INTERVAL 57 DAY), 'Scarico', 'P-004', 'OPE-001'),
                                                                                    (20, DATE_SUB(CURDATE(), INTERVAL 56 DAY), 'Carico',  'P-006', 'OPE-002');

-- 30 giorni fa
INSERT INTO movimento (QtaProd, Data, TipoMovimento, IdProd, IdUtenteOperatore) VALUES
                                                                                    (60, DATE_SUB(CURDATE(), INTERVAL 30 DAY), 'Carico',  'P-007', 'OPE-001'),
                                                                                    (35, DATE_SUB(CURDATE(), INTERVAL 29 DAY), 'Carico',  'P-008', 'OPE-003'),
                                                                                    (12, DATE_SUB(CURDATE(), INTERVAL 28 DAY), 'Scarico', 'P-005', 'OPE-002'),
                                                                                    (18, DATE_SUB(CURDATE(), INTERVAL 27 DAY), 'Scarico', 'P-006', 'OPE-001'),
                                                                                    (45, DATE_SUB(CURDATE(), INTERVAL 26 DAY), 'Carico',  'P-009', 'OPE-003');

-- Ultima settimana
INSERT INTO movimento (QtaProd, Data, TipoMovimento, IdProd, IdUtenteOperatore) VALUES
                                                                                    (20, DATE_SUB(CURDATE(), INTERVAL 6 DAY), 'Carico',  'P-001', 'OPE-001'),
                                                                                    (8,  DATE_SUB(CURDATE(), INTERVAL 6 DAY), 'Scarico', 'P-002', 'OPE-002'),
                                                                                    (15, DATE_SUB(CURDATE(), INTERVAL 5 DAY), 'Carico',  'P-003', 'OPE-003'),
                                                                                    (5,  DATE_SUB(CURDATE(), INTERVAL 5 DAY), 'Scarico', 'P-004', 'OPE-001'),
                                                                                    (30, DATE_SUB(CURDATE(), INTERVAL 4 DAY), 'Carico',  'P-005', 'OPE-002'),
                                                                                    (10, DATE_SUB(CURDATE(), INTERVAL 4 DAY), 'Scarico', 'P-006', 'OPE-003'),
                                                                                    (25, DATE_SUB(CURDATE(), INTERVAL 3 DAY), 'Carico',  'P-007', 'OPE-001'),
                                                                                    (7,  DATE_SUB(CURDATE(), INTERVAL 3 DAY), 'Scarico', 'P-008', 'OPE-002'),
                                                                                    (12, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'Carico',  'P-009', 'OPE-003'),
                                                                                    (4,  DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'Scarico', 'P-010', 'OPE-001'),
                                                                                    (18, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'Carico',  'P-001', 'OPE-002'),
                                                                                    (6,  DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'Scarico', 'P-003', 'OPE-003'),
                                                                                    (22, CURDATE(),                            'Carico',  'P-002', 'OPE-001'),
                                                                                    (9,  CURDATE(),                            'Scarico', 'P-004', 'OPE-002');