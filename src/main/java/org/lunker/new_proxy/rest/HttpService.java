package org.lunker.new_proxy.rest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.lunker.new_proxy.util.HttpEntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by dongqlee on 2018. 3. 16..
 */
public class HttpService {

    private Logger logger= LoggerFactory.getLogger(HttpService.class);
    private static PoolingHttpClientConnectionManager syncConnectionManager=null;
    private static PoolingNHttpClientConnectionManager asyncConnectionManager=null;

    static {
        /*
        SSLContext sslcontext = SSLContexts.custom().albuild();
        sslcontext.init(null, new X509TrustManager[]{new HttpsTrustManager()}, new SecureRandom());


        ConnectionSocketFactory plainSocketFactory=new PlainConnectionSocketFactory();
        ConnectionSocketFactory sslSocketFactory=new SSLConnectionSocketFactory();

        Registry<ConnectionSocketFactory> r = RegistryBuilder.<ConnectionSocketFactory>create()
            .register("http", plainsf)
            .register("https", sslsf)
            .build();
        */

        syncConnectionManager=new PoolingHttpClientConnectionManager();
        syncConnectionManager.setMaxTotal(500);
        syncConnectionManager.setDefaultMaxPerRoute(20);
    }

    public String restEndpoint="https://smartbiz.sejongtelecom.net:4435/b2b/v1.0";

    public <T extends Object> T get(String url, Class<T> type) throws IOException{
        logger.info("Generate http GET request for url : " + url);

        T response=null;
        CloseableHttpClient httpClient=HttpClients.custom().setConnectionManager(syncConnectionManager).build();
        String requestUrl=restEndpoint+url;

        HttpGet httpGet=new HttpGet(requestUrl);
        CloseableHttpResponse closeableHttpResponse=httpClient.execute(httpGet);
        HttpEntity httpEntity=closeableHttpResponse.getEntity();

        String contentType=closeableHttpResponse.getFirstHeader("Content-Type").getValue();

        if(contentType.equals("application/xml")){
            logger.info("xml!");
            response=(T) EntityUtils.toString(httpEntity);
        }
        else {
            logger.info("json!");
            response=(T) HttpEntityUtils.toJson(httpEntity);
        }
        closeableHttpResponse.close();
        EntityUtils.consume(httpEntity);

        return response;
    }

    public String post(){
//        logger.info("Generate http POST request for url : " + url);
//
//        String strResponse="";
//        CloseableHttpClient httpClient= HttpClients.createDefault();
//
//        HttpPost httpPost=new HttpPost(url);
//        StringEntity requestEntity = new StringEntity(
//                data.toString(),
//                ContentType.APPLICATION_JSON);
//        httpPost.setEntity(requestEntity);
//
//        CloseableHttpResponse response=httpClient.execute(httpPost);
//        HttpEntity httpEntity=response.getEntity();
//
//        String contentType=response.getFirstHeader("Content-Type").getValue();
//
//        if(contentType.equals("application/xml")){
//            logger.info("xml!");
//            strResponse= EntityUtils.toString(httpEntity);
//        }
//        else {
//            logger.info("json!");
//            strResponse= EntityUtils.toString(httpEntity);
//        }
//
//        EntityUtils.consume(httpEntity);
//
//        return strResponse;

        return "";
    }

    public void getAsync(String url) throws InterruptedException, ExecutionException, IOException{

        CloseableHttpAsyncClient client = HttpAsyncClients.createDefault();
        client.start();
        HttpGet request = new HttpGet("http://www.google.com");

        Future<HttpResponse> future = client.execute(request, null);
        HttpResponse response = future.get();

        client.close();
    }

}
