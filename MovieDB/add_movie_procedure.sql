-- --------------------------------------------------------------------------------
-- Routine DDL
-- Note: comments before and after the routine body will not be stored by the server
-- --------------------------------------------------------------------------------
DELIMITER $$

CREATE PROCEDURE `moviedb`.`add_movie` (IN new_title varchar(100), IN new_year int(11), IN new_director varchar(100), IN new_genre_name varchar(32), IN new_stars_first_name varchar(50), IN new_stars_last_name varchar(50))
BEGIN
Declare start_transaction_status varchar(30);
Declare movie_status varchar(30);
Declare genre_status varchar(30);
Declare star_status varchar(30);
Declare genre_in_movie_status varchar(30);
Declare star_in_movie_status varchar(30);
Declare end_transaction_status varchar(30);
start transaction;
set start_transaction_status = 'Starting Transaction.';
if not exists(select id from movies where title = new_title and year = new_year and director like new_director) then insert into movies (title, year, director) values (new_title, new_year, new_director); set movie_status = 'Movie Added.'; else set movie_status = 'Movie Already Exists.'; end if;
if not exists(select id from genres where name = new_genre_name) then insert into genres (name) values (new_genre_name); set genre_status = 'Genre Added.'; else set genre_status = 'Genre Already Exists.'; end if;
if not exists(select id from stars where first_name = new_stars_first_name and last_name = new_stars_last_name) then insert into stars (first_name, last_name) values (new_stars_first_name, new_stars_last_name); set star_status = 'Star Added.'; else set star_status = 'Star Already Exists.'; end if;
if not exists(select * from stars_in_movies where star_id in (select id from stars where first_name = new_stars_first_name and last_name = new_stars_last_name) and movie_id in (select id from movies where title = new_title and year = new_year and director like new_director)) then insert into stars_in_movies values ((select id from stars where first_name = new_stars_first_name and last_name = new_stars_last_name), (select id from movies where title = new_title and year = new_year and director like new_director)); set star_in_movie_status = 'Stars_In_Movies Added.'; else set star_in_movie_status = 'Stars_In_Movies Already Exists.'; end if;
if not exists(select * from genres_in_movies where genre_id in (select id from genres where name = new_genre_name) and movie_id in (select id from movies where title = new_title and year = new_year and director like new_director)) then insert into genres_in_movies values ((select id from genres where name = new_genre_name), (select id from movies where title = new_title and year = new_year and director like new_director)); set genre_in_movie_status = 'Genres_In_Movies Added.'; else set genre_in_movie_status = 'Genres_In_Movies Already Exists.'; end if;
set end_transaction_status = 'Transaction Finish.';
commit;
select start_transaction_status, movie_status, genre_status, star_status, genre_in_movie_status, star_in_movie_status, end_transaction_status;
END$$

DELIMITER ;