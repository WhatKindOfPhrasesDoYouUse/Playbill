-- SET search_path TO boot;

CREATE TABLE events (
    id SERIAL PRIMARY KEY,
    title VARCHAR (255) NOT NULL,
    date DATE NOT NULL,
    time TIME NOT NULL,
    duration INT NOT NULL,
    location VARCHAR (255) NOT NULL,
    price INT NOT NULL,
    age_limit INT NOT NULL,
    quantity INT NOT NULL
);

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR (255) NOT NULL,
    username VARCHAR (255) NOT NULL UNIQUE,
    password VARCHAR (255) NOT NULL,
    email VARCHAR (255) NOT NULL UNIQUE,
    balance INT NOT NULL,
    pushkinskaya_balance INT NOT NULL,
    age INT
);

CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE user_role (
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (role_id) REFERENCES roles (id)
);

CREATE TABLE cart (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    event_id INT NOT NULL,
    purchased BOOLEAN NOT NULL DEFAULT FALSE,
    payment_type VARCHAR(255) DEFAULT 'common',
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (event_id) REFERENCES events (id)
);

INSERT INTO events (title, date, time, duration, location, price, age_limit, quantity)
VALUES ('Уроки французского', '2023-09-20', '19:30:00', 120, 'Малый зал 1', 1500, 18, 20),
       ('Первая любовь', '2023-07-19', '20:30:00', 80, 'Большой зал 2', 500, 16, 400),
       ('Монарх', '2023-08-18', '17:00:00', 210, 'Средний зал 1', 600, 16, 110),
       ('Мама', '2023-05-29', '21:10:00', 90, 'Средний зал 2', 400, 18, 200),
       ('Белые ночи', '2023-10-10', '17:00:00', 140, 'Большой зал 1', 1500, 18, 236),
       ('Домик у моря', '2023-09-17', '19:00:00', 120, 'Средний зал 2', 600, 18, 200);


INSERT INTO roles (name)
VALUES ('ROLE_ADMIN'), ('ROLE_USER');


INSERT INTO users (name, username, password, email, balance, pushkinskaya_balance, age)
VALUES ('Dima', 'dima', '$2a$12$m0uhPXWcKpoquLqM/L6LeOUiRwCgIM3XyXZRT7NY//wIGxIh3bbx.', 'dima@gmail.com', 3000, 3000, 20),
       ('Egor', 'egor', '$2a$12$E7myihD0gIgdkpH8RM9d3etFSwreGHW/Z3Ch24y21q4eKhFjPCxe6', 'egarPeregar@gmail.com', 100, 3000, 20),
       ('Sashka', 'sashka', '$2a$12$MRRlVN7SWnateyqWb8M77euQBvc5iXQ0q1f9hwY8S4mFvS5JbFHyW', 'avgAge@gmail.com', 50000, 3000, 47),
       ('Vadik', 'vadik228', '$2a$12$HUV8sTh9JXqneGFRmcEIPeuYrSa2iixZ29M16HeCB1ekwhBT.hD3e', 'aye@gmail.com', 10, 3000, 5);

INSERT INTO user_role (user_id, role_id)
VALUES ((SELECT id FROM users WHERE username = 'dima'), (SELECT id FROM roles WHERE name = 'ROLE_ADMIN')),
       ((SELECT id FROM users WHERE username = 'egor'), (SELECT id FROM roles WHERE name = 'ROLE_USER')),
       ((SELECT id FROM users WHERE username = 'sashka'), (SELECT id FROM roles WHERE name = 'ROLE_USER')),
       ((SELECT id FROM users WHERE username = 'vadik228'), (SELECT id FROM roles WHERE name = 'ROLE_USER'));
