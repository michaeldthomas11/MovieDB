import java.util.ArrayList;


public class MovieCollection
{
	private static final int ARRAY_STARTING_POINT = 0;
	private ArrayList<Movie> moviesByTitle;
	private ArrayList<Movie> moviesByDate;

	public MovieCollection(ArrayList<Movie> moviesByTitle, ArrayList<Movie> moviesByDate)
	{
		this.moviesByTitle = moviesByTitle;
		this.moviesByDate = moviesByDate;
	}
	
	public ArrayList<Movie> getForwardTraverseTitle(int beginning, int size)
	{
		ArrayList<Movie> newMovies = new ArrayList<Movie>();
		for (int i = beginning; i < (beginning + size) && i < this.moviesByTitle.size(); i++)
		{
			newMovies.add(moviesByTitle.get(i));
		}
		return newMovies;
	}
	
	public ArrayList<Movie> getReverseTraverseTitle(int beginning, int size)
	{
		ArrayList<Movie> newMovies = new ArrayList<Movie>();
		
		
		for (int i = ((this.moviesByTitle.size() - 1) - beginning); i > (((this.moviesByTitle.size() - 1) - beginning) - size) && i >= MovieCollection.ARRAY_STARTING_POINT; i--)
		{
			newMovies.add(moviesByTitle.get(i));
		}
		return newMovies;
	}
	
	public ArrayList<Movie> getForwardTraverseDate(int beginning, int size)
	{
		ArrayList<Movie> newMovies = new ArrayList<Movie>();
		for (int i = beginning; i < (beginning + size) && i < this.moviesByDate.size(); i++)
		{
			newMovies.add(moviesByDate.get(i));
		}
		return newMovies;
	}
	
	public ArrayList<Movie> getReverseTraverseDate(int beginning, int size)
	{
		ArrayList<Movie> newMovies = new ArrayList<Movie>();
		for (int i = (this.moviesByTitle.size() - 1) - beginning; i > (((this.moviesByTitle.size() - 1) - beginning) - size) && i >= MovieCollection.ARRAY_STARTING_POINT; i--)
		{
			newMovies.add(moviesByDate.get(i));
		}
		return newMovies;
	}

	public int getSize()
	{
		return moviesByTitle.size();
	}
}
