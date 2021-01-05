package martialcoder.surajsahani.safeouts.utils;

import android.os.AsyncTask;
import android.os.Handler;
import android.widget.ProgressBar;

import org.w3c.dom.Document;


public class ProgressCheck extends AsyncTask<String, String, String> {
    ProgressBar mProgressBar;

    @Override
    protected String doInBackground(String... params) {

        Document doc = null;
        String url = "https://www.google.com.pk/";
        try {
//            doc = Jsoup.connect(url).timeout(20 * 1000).get();
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        Runnable runnable = new Runnable() {
            public void run () {
                // Do your stuff here  -- show your progress bar
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(runnable, 10000);
    }


    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onProgressUpdate(String... text) {


    }
}