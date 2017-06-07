package www.amriparitam.in.movieapp.Core;

import java.io.IOException;

/**
 * Created by Amrita Pritam on 6/6/2017.
 */

public class NoConnectivityException extends IOException {
    @Override
    public String getMessage() {
        return "No connectivity exception";
    }
}
