package www.amriparitam.in.movieapp.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Amrita Pritam on 6/6/2017.
 */

public class VideoResponse {
    @SerializedName("id")
    private int id;
    @SerializedName("results")
    private List<Video> results;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Video> getResults() {
        return results;
    }

    public void setResults(List<Video> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "VideoResponse : [id: "+ id
                + ", results :"+ results;
    }
}
