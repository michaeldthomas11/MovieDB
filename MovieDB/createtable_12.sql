CREATE table movies(
	id int NOT NULL AUTO_INCREMENT,
	title varchar(100) NOT NULL,
	year int NOT NULL,
	director varchar(100) NOT NULL,
	banner_url varchar(200),
	trailer_url varchar(200),
	PRIMARY KEY(id)
);

CREATE table stars(
   id int NOT NULL AUTO_INCREMENT,
   first_name varchar(50) NOT NULL,
   last_name varchar(50) NOT NULL,
   dob date,
   photo_url varchar(200),
   PRIMARY KEY(id)
);

CREATE table stars_in_movies(
	star_id int NOT NULL,
	movie_id int NOT NULL,
	FOREIGN KEY(star_id) REFERENCES stars(id),
	FOREIGN KEY(movie_id) REFERENCES movies(id)
);

CREATE table genres(
	id int NOT NULL AUTO_INCREMENT,
	name varchar(32) NOT NULL,
	PRIMARY KEY(id)
);

CREATE table genres_in_movies(
	genre_id int NOT NULL,
	movie_id int NOT NULL,
	FOREIGN KEY(genre_id) REFERENCES genres(id),
	FOREIGN KEY(movie_id) REFERENCES movies(id)
);

CREATE table creditcards(
	id varchar(20) NOT NULL,
	first_name varchar(50) NOT NULL,
	last_name varchar(50) NOT NULL,
	expiration date NOT NULL,
	PRIMARY KEY(id)
);

CREATE table customers(
	id int NOT NULL AUTO_INCREMENT,
	first_name varchar(50) NOT NULL,
	last_name varchar(50) NOT NULL,
	cc_id varchar(20) NOT NULL,
	address varchar(200) NOT NULL,
	email varchar(50) NOT NULL,
	password varchar(20) NOT NULL,
	FOREIGN KEY(cc_id) REFERENCES creditcards(id),
	PRIMARY KEY(id)
);

CREATE table sales(
	id int NOT NULL AUTO_INCREMENT,
	customer_id int NOT NULL,
	movie_id int NOT NULL,
	sale_date date NOT NULL,
	FOREIGN KEY(customer_id) REFERENCES customers(id),
	FOREIGN KEY(movie_id) REFERENCES movies(id),
	PRIMARY KEY(id)
);