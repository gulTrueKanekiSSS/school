CREATE TABLE Car (
    id SERIAL PRIMARY KEY,
    brand VARCHAR NOT NULL,
    model VARCHAR NOT NULL,
    price NUMERIC(12, 2) NOT NULL
);

CREATE TABLE Person (
    id SERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
    age INTEGER NOT NULL,
    has_license BOOLEAN NOT NULL,
    car_id INTEGER,
    CONSTRAINT fk_person_car
        FOREIGN KEY (car_id) REFERENCES Car (id)
);
