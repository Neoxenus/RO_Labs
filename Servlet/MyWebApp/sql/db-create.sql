-- DROP database IF EXISTS testdb;

-- CREATE database testdb;

--USE testdb;

DROP TABLE workers;
DROP TABLE departments;

CREATE TABLE departments (
     id SERIAL PRIMARY KEY,
     name VARCHAR(20)
);
CREATE TABLE workers (
	id SERIAL PRIMARY KEY,
	name VARCHAR(20) UNIQUE,
	department_id INT REFERENCES departments on delete cascade
);



INSERT INTO departments VALUES (DEFAULT, 'teamA');
INSERT INTO departments VALUES (DEFAULT, 'teamB');
INSERT INTO departments VALUES (DEFAULT, 'teamC');
INSERT INTO workers VALUES (DEFAULT, 'ivanov', 1);
INSERT INTO workers VALUES (DEFAULT, 'petrov', 2);



SELECT * FROM workers;
SELECT * FROM departments;