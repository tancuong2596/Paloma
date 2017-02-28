package cit.edu.paloma.utils;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by charlie on 2/28/17.
 */

public class ImgurUtils {
    public static Response uploadBase64Photo(byte[] base64Photo) throws IOException {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("image/*");

        RequestBody body = RequestBody.create(mediaType, base64Photo);

        Request request = new Request.Builder()
                .url("https://api.imgur.com/3/image")
                .post(body)
                .addHeader("authorization", "Client-ID 8cb591cc7f30e8b")
                .addHeader("content-type", "image/*")
                .addHeader("cache-control", "no-cache")
                .addHeader("postman-token", "b1f7c91b-5858-c6bb-9163-25f29cc72bf5")
                .build();

        return client.newCall(request).execute();
    }
}
