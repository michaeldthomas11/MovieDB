import java.util.ArrayList;


public class Movie
{
	public static final int ID = 1;
	public static final int TITLE = 2;
	public static final int YEAR = 3;
	public static final int DIRECTOR = 4;
	public static final int BANNER_URL = 5;
	public static final int STARS = 6;
	public static final int GENRES = 7;
	private String id;
	private String title;
	private String year;
	private String director;
	private String banner_url;
	private ArrayList<Star> stars;
	private ArrayList<String> genres;

	public Movie(String id, String title, String year, String director, String banner_url)
	{
		 this.id = id;
		 this.title = title;
		 this.year = year;
		 this.director = director;
		 this.banner_url = banner_url;
		 this.stars = new ArrayList<Star>();
		 this.genres = new ArrayList<String>();
	}
	
	/**
	 *  @return size of the array.
	 */
	public int getGenreSize()
	{
		return genres.size();
	}
	
	/**
	 *  @return size of the array.
	 */
	public int getStarSize()
	{
		return stars.size();
	}
	
	/**
	 *  @param a int.
	 *  @return a star at the position of the array.
	 */
	public String getStars(int i)
	{
		return stars.get(i).getName();
	}
	
	public Star getStar(int i)
	{
		return stars.get(i);
	}
	
	/**
	 *  @param a genre.
	 */
	public void addGenres(String genre)
	{
		genres.add(genre);
	}
	
	/**
	 *  @param a star.
	 */
	public void addStars(Star star)
	{
		stars.add(star);
	}
	
	/**
	 * 	@param a int.
	 *  @return a genre at the position of the array.
	 */
	public String getGenres(int i)
	{
		return genres.get(i);
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the year
	 */
	public String getYear() {
		return year;
	}

	/**
	 * @param year the year to set
	 */
	public void setYear(String year) {
		this.year = year;
	}

	/**
	 * @return the director
	 */
	public String getDirector() {
		return director;
	}

	/**
	 * @param director the director to set
	 */
	public void setDirector(String director) {
		this.director = director;
	}

	/**
	 * @return the banner_url
	 */
	public String getBanner_url() {
		return banner_url;
	}

	/**
	 * @param banner_url the banner_url to set
	 */
	public void setBanner_url(String banner_url) {
		this.banner_url = banner_url;
	}
}
