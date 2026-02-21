CREATE TABLE IF NOT EXISTS players (
    id VARCHAR(100) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    team VARCHAR(10) NOT NULL,
    position VARCHAR(50),
    height_inches INTEGER,
    weight_lbs INTEGER,
    age DOUBLE
);

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS authorities (
    username VARCHAR(50) NOT NULL,
    authority VARCHAR(50) NOT NULL,
    FOREIGN KEY (username) REFERENCES users(username)
);