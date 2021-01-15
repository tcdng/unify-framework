A _Rack_ control is a specialized _Table_ widget that allows a user to change the position of any of its rows manually. There is a dedicated column that contains shift buttons which a user can click to effect the movement of a row in a particular direction and step. Movement can occur one step up or down to the previous or next position or in multiple steps to the topmost or bottommost position of the _Rack_.

Like a _Table_, a _Rack_ binds to a _List_ data object. The items in the binded list are reordered as the user performs shift operations in the rendered Rack on the web client.

<img src="images/webcomponents/controls/rack.png" alt="Rack" align="center">

Listing 1: Rack UPL

```upl
!ui-rack:movieRack
    caption:$s{Now Showing} binding:movieList
    components:$c{description genre showtime}
    style:$s{width:640px;height:200px;} 
    windowed:true serialNumbers:true
!ui-label:genre
    caption:$s{Genre} binding:genre
    columnStyle:$s{width:120px;}
!ui-label:description
    caption:$s{Description} binding:description
    columnStyle:$s{width:160px;}
!ui-label:showtime
    caption:$s{Showtime} binding:showtime
    columnStyle:$s{width:160px;}
```

Listing 2: Movie class

```java
public class Movie {

    private String genre;

    private String description;

    private String showtime;

    public Movie(String genre, String description, String showtime) {
        this.genre = genre;
        this.description = description;
        this.showtime = showtime;
    }

    ...
}
```

Listing 3: Movie page controller

```java
MoviePageBean pageBean = ...

List<Movie> movieList = new ArrayList<Movie>();
movieList.add(new Movie("Action, Adventure", "Avengers: Infinity War",
            "12:10PM, 3:20PM, 7:00PM"));
movieList.add(new Movie("Action, Adventure", "Deadpool 2",
            "2:30PM, 8:00PM"));
movieList.add(new Movie("Comedy", "The Ghost & The Tout",
            "1:15PM, 8:00PM"));
movieList.add(new Movie("Adventure", "Sherlock Gnomes",
            "10:40AM, 2:30PM"));
movieList.add(new Movie("Action, Drama", "Samson",
            "4:45PM"));
movieList.add(new Movie("Action, Adventure",
            "Jurassic World: Fallen Kingdom", "1:20PM, 5:00PM"));
movieList.add(new Movie("Comedy, Romance", "Rule No. 1",
            "1:15PM, 4:15PM"));
pageBean.setMovieList(movieList);
```

## Attributes

See [Table](/tcdng/unify-framework/wiki/Table#Attributes).
