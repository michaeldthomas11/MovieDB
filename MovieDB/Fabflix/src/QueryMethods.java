import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;


public class QueryMethods
{
	public static final int TITLE = 0;
	public static final int YEAR = 1;
	
	public static String buildQuery(HttpServletRequest request, int orderBy)
	{
		String querySelect = "SELECT * FROM moviedb.movies";
		//Search query
		if(request.getParameter("title") != null && (!request.getParameter("title").equals("") || !request.getParameter("year").equals("")
				|| !request.getParameter("director").equals("") || !request.getParameter("starFirstName").equals("") || !request.getParameter("starLastName").equals("")))
		{
			querySelect = querySelect.concat(" WHERE ");
			boolean andNeeded = false;
			if(!request.getParameter("title").equals(""))
			{
				querySelect = querySelect.concat("title LIKE '%" + request.getParameter("title") + "%'");
				andNeeded = true;
			}
			
			if(!request.getParameter("year").equals(""))
			{
				if(andNeeded)
					querySelect = querySelect.concat(" and ");
				querySelect = querySelect.concat("year = " + Integer.parseInt(request.getParameter("year")));
				andNeeded = true;
			}
			
			if(!request.getParameter("director").equals(""))
			{
				if(andNeeded)
					querySelect = querySelect.concat(" and ");
				querySelect = querySelect.concat("director LIKE '%" + request.getParameter("director") + "%'");
				andNeeded = true;
			}
			
			if(!request.getParameter("starFirstName").equals(""))
			{
				if(andNeeded)
					querySelect = querySelect.concat(" and ");
				querySelect = querySelect.concat("moviedb.movies.id in (select moviedb.stars_in_movies.movie_id from moviedb.stars_in_movies where moviedb.stars_in_movies.star_id in (Select moviedb.stars.id from moviedb.stars where moviedb.stars.first_name like '%"+ request.getParameter("starFirstName") + "%'))");
			}
			
			if(!request.getParameter("starLastName").equals(""))
			{
				if(andNeeded)
					querySelect = querySelect.concat(" and ");
				querySelect = querySelect.concat("moviedb.movies.id in (select moviedb.stars_in_movies.movie_id from moviedb.stars_in_movies where moviedb.stars_in_movies.star_id in (Select moviedb.stars.id from moviedb.stars where moviedb.stars.last_name like '%"+ request.getParameter("starLastName") + "%'))");
			}
		}
		else if (request.getParameter("genre") != null)
		{
			querySelect = querySelect.concat(" WHERE ");
			querySelect = querySelect.concat("moviedb.movies.id in (select moviedb.genres_in_movies.movie_id from moviedb.genres_in_movies where moviedb.genres_in_movies.genre_id in (Select moviedb.genres.id from moviedb.genres where moviedb.genres.name like '"+ request.getParameter("genre") + "'))");
		}
		else if (request.getParameter("movie") != null)
		{
			if (!request.getParameter("movie").equals(""))
			{
				querySelect = querySelect.concat(" WHERE ");
				querySelect = querySelect.concat("moviedb.movies.title like '"+ request.getParameter("movie") + "%'");
			}
			else
			{
				querySelect = querySelect.concat(" WHERE ");
				querySelect = querySelect.concat("title = ''");
			}
		}
		else
		{
			querySelect = querySelect.concat(" WHERE ");
			querySelect = querySelect.concat("title = ''");
		}
		if (orderBy == TITLE)
		{
			querySelect = querySelect.concat(" order by title asc");
		}
		else if (orderBy == YEAR)
		{querySelect = querySelect.concat(" order by year desc");
			
		}
		return querySelect;
	}
	public static ArrayList<Movie> createMovieList(Connection connection,
			String querySelect) throws SQLException
	{
		//Initial Statements for storing Movies
		Statement statement = connection.createStatement();
		PreparedStatement starStatement = connection.prepareStatement("SELECT moviedb.stars.first_name, moviedb.stars.last_name, moviedb.stars.id from moviedb.stars where moviedb.stars.id in (SELECT moviedb.stars_in_movies.star_id from moviedb.stars_in_movies where moviedb.stars_in_movies.movie_id in (SELECT moviedb.movies.id FROM moviedb.movies WHERE moviedb.movies.id = ?))");
		PreparedStatement genreStatement = connection.prepareStatement("SELECT moviedb.genres.name from moviedb.genres where moviedb.genres.id in (SELECT moviedb.genres_in_movies.genre_id from moviedb.genres_in_movies where moviedb.genres_in_movies.movie_id in (SELECT moviedb.movies.id FROM moviedb.movies WHERE moviedb.movies.id = ?))");
		ResultSet resultSet = statement.executeQuery(querySelect);
		ArrayList<Movie> movies = new ArrayList<Movie>();
		String id = null;
		String genre = null;
		while (resultSet.next())
		{
			Movie movie = new Movie(resultSet.getString(Movie.ID), resultSet.getString(Movie.TITLE),
					resultSet.getString(Movie.YEAR), resultSet.getString(Movie.DIRECTOR), resultSet.getString(Movie.BANNER_URL));
			id = resultSet.getString(Movie.ID);
			genre = resultSet.getString(Movie.ID);
			movie  = addData(movie, Movie.STARS, id, starStatement);
			movie = addData(movie, Movie.GENRES, genre, genreStatement);
			movies.add(movie);
		}
		
		// Close all statements resultsets and connections.
		resultSet.close();
		starStatement.close();
		genreStatement.close();
		statement.close();
		return movies;
	}
	
	private static Movie addData(Movie movie, int column, String data, PreparedStatement resultStatement)
			throws SQLException
	{
		resultStatement.setString(1, data);
		ResultSet genreResultSet = resultStatement.executeQuery();
		while (genreResultSet.next())
		{
			if (column == Movie.STARS)
			{
				movie.addStars(new Star(genreResultSet.getString(1) + " " + genreResultSet.getString(2), genreResultSet.getString(3)));
			}
			else
			{
				movie.addGenres(genreResultSet.getString(1));
			}
		}
		genreResultSet.close();
		return movie;
	}
}
