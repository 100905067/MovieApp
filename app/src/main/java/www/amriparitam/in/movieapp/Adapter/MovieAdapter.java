package www.amriparitam.in.movieapp.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import amagi82.flexibleratingbar.FlexibleRatingBar;
import www.amriparitam.in.movieapp.Model.Movie;
import www.amriparitam.in.movieapp.R;

/**
 * Created by Amrita Pritam on 4/14/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private Context context;
    private List<Movie> movieList;
    private MovieItemListener movieItemListener;
    private int layout;
    private View selectedView;

    private String base_url = "https://image.tmdb.org/t/p/w500";


    public MovieAdapter(Context context, List<Movie> movieList, MovieItemListener movieItemListener, int layout) {
        this.context = context;
        this.movieList = movieList;
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
        holder.bind(movieList.get(position), position);
    }

    @Override
    public int getItemCount() {
        if(movieList != null) {
            return movieList.size();
        }
        return 0;
    }

    public interface MovieItemListener {
        void onItemSelected(Movie movie, int position);
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView releaseDate;
        TextView title;
        TextView rating;
        ImageView poster_image;

        public MovieViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            releaseDate = (TextView) itemView.findViewById(R.id.releaseDate);
            rating = (TextView) itemView.findViewById(R.id.rating);
            poster_image = (ImageView) itemView.findViewById(R.id.posterImage);
        }

        public void bind(Movie movie, int position) {
            itemView.setSelected(true);
            if(itemView.isSelected()) {
                selectedView = itemView;
            }

            Glide.with(context).load(base_url+movie.getBackdropPath()).into(poster_image);
            itemView.setTag(position);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    movieItemListener.onItemSelected(movieList.get((int) v.getTag()), (int) v.getTag());
                    if(selectedView !=null ) {
                        selectedView.setSelected(false);
                    }
                    selectedView = v;
                    selectedView.setSelected(true);
                }
            });

            title.setText(movie.getTitle());
            releaseDate.setText(movie.getReleaseDate());
            rating.setText(Float.toString(movie.getVoteAverage()));
        }
    }
}
