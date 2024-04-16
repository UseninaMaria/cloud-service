create table clouds(
    id SERIAL PRIMARY KEY ,
    file_name VARCHAR(100) NOT NULL ,
    file_size INTEGER NOT NULL ,
    file_content BYTEA NOT NULL ,
    user_id INTEGER REFERENCES users (id),
    date TIMESTAMP NOT NULL,
    UNIQUE (file_name, user_id)
);