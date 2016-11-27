package j.trt.s.hi.st.ecities.data;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import j.trt.s.hi.st.ecities.activities.MainActivity;

public class AuthTask extends AsyncTask<String, Void, Boolean> {
    private AuthResponse delegate = null;
    String AUTH_URL = "http://ecity.org.ua:8080/user/hello";
    String UTF_8 = "UTF-8";
    public AuthTask(AuthResponse listener){
        delegate = listener;
    }
    @Override
    protected Boolean doInBackground(String... params) {
        String okResponse = "[{\"id\":277,\"name\":\"Одесса\",\"regionId\":1,\"longitude\":0,\"latitude\":0,\"population\":100,\"establishment\":-30262723200000,\"url\":\"https://ru.wikipedia.org/wiki/Одесса\",\"lastChar\":\"А\"}]";
        StringBuffer buffer = new StringBuffer();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet;
        try {
            httpGet = new HttpGet(AUTH_URL);
            String auth =new String(Base64.encode(( params[0] + ":" + params[1]).getBytes(),Base64.URL_SAFE|Base64.NO_WRAP));
            httpGet.addHeader("Authorization", "Basic " + auth);
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                if(buffer.toString().equals(okResponse)) {
                    Log.e(MainActivity.TAG, "Auth good!");
                    return true;
                }else {
                    Log.e(MainActivity.TAG, "Auth bad");
                    return false;
                }
            } else {
                    Log.e(MainActivity.TAG, "Authentication is failed");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    @Override
    protected void onPostExecute(Boolean aBoolean) {
       if(delegate != null)
        delegate.authIsDone(aBoolean);
    }
}
