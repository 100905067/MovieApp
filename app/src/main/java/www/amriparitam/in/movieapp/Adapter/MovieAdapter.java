package www.amriparitam.in.movieapp.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import www.amriparitam.in.movieapp.Model.Movie;
import www.amriparitam.in.movieapp.R;

/**
 * Created by Amrita Pritam on 4/14/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> implements Filterable {

    private Context context;
    private List<Movie> movieList;
    private List<Movie> filteredMovieList;
    private MovieItemListener movieItemListener;
    private int layout;
    private View selectedView;

    private String base_url = "https://image.tmdb.org/t/p/w500";


    public MovieAdapter(Context context, List<Movie> movieList, MovieItemListener movieItemListener, int layout) {
        this.context = context;
        this.movieList = movieList;
        this.filteredMovieList = movieList;
        this.movieItemListener = movieItemListener;
        this.layout = layout;
    }
    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View movieItemView = inflater.inflate(layout, parent, false);
        return new MovieViewHolder(movieItemView);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind(filteredMovieList.get(position), position);
    }

    @Override
    public int getItemCount() {
        if(filteredMovieList != null) {
            return filteredMovieList.size();
        }
        return 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    filteredMovieList = movieList;
                } else {

                    ArrayList<Movie> filteredList = new ArrayList<>();

                    for (Movie movie : movieList) {

                        if (movie.getTitle().toLowerCase().contains(charString) || movie.getOriginalTitle().toLowerCase().contains(charString)) {

                            filteredList.add(movie);
                        }
                    }

                    filteredMovieList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredMovieList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredMovieList = (ArrayList<Movie>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface MovieItemListener {
        void onItemSelected(Movie movie, int position, ImageView imageView);
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView rating;
        ImageView poster_image;

        public MovieViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            rating = (TextView) itemView.findViewById(R.id.rating);
            poster_image = (ImageView) itemView.findViewById(R.id.posterImage);
        }

        public void bind(Movie movie, int position) {
            itemView.setSelected(true);
            if(itemView.isSelected()) {
                selectedView = itemView;
            }

            Picasso.with(context).load(base_url+movie.getBackdropPath())
                    .into(poster_image);
            itemView.setTag(position);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    movieItemListener.onItemSelected(filteredMovieList.get((int) v.getTag()), (int) v.getTag(), poster_image);
                    if(selectedView !=null ) {
                        selectedView.setSelected(false);
                    }
                    selectedView = v;
                    selectedView.setSelected(true);
                }
            });

            title.setText(movie.getTitle());
            rating.setText(Float.toString(movie.getVoteAverage()));
        }
    }
}
