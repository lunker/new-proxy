package org.lunker.new_proxy.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by dongqlee on 2018. 3. 19..
 *
 *
 * Helper Class for handling Http Request & Response
 */
public class HttpEntityUtils {

    private static JsonParser jsonParser=null;

    static {
        jsonParser=new JsonParser();
    }


    /**
     * Consume HttpEntity & Generate String Response
     * @param entity
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public static String toString(final HttpEntity entity) throws IOException, ParseException {
        return org.apache.http.util.EntityUtils.toString(entity);
    }

    /**
     * Consume HttpEntity & Generate Json Format Response
     * @param entity
     * @return
     * @throws IOException
     * @throws ParseException
     * @throws JsonSyntaxException
     */
    public static JsonObject toJson(final HttpEntity entity) throws IOException, ParseException, JsonSyntaxException {
        String str= EntityUtils.toString(entity);
        return jsonParser.parse(str).getAsJsonObject();
    }
}
