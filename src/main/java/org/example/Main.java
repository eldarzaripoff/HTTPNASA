package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class Main {
    private static final String REMOTE_IRL = "https://api.nasa.gov/planetary/apod?api_key=dKw27xZOJmwt1pQARbzCs0eK0ddmltrXn16NUX0B";
    public static ObjectMapper mapper = new ObjectMapper();
    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build();
        HttpGet request = new HttpGet(REMOTE_IRL);
        request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

        CloseableHttpResponse response = httpClient.execute(request);
        Arrays.stream(response.getAllHeaders()).forEach(System.out::println);

        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        List<Image> imageList = mapper.readValue(
                response.getEntity().getContent(),
                new TypeReference<>() {
                }
        );

        String image = imageList.get(0).getHdurl();
        String[] parts = image.split("/");
        String image_name = parts[parts.length - 1];
        System.out.println(image_name);
        HttpGet request2 = new HttpGet(image);
        request2.setHeader(HttpHeaders.ACCEPT,ContentType.IMAGE_PNG.getMimeType());

        CloseableHttpResponse response2 = httpClient.execute(request2);
        Arrays.stream(response2.getAllHeaders()).forEach(System.out::println);

        InputStream inputStream = response2.getEntity().getContent();
        byte[] buffer = new byte[1024];
        int count = 0;
        FileOutputStream fos = new FileOutputStream("C:\\Users\\zarip\\IdeaProjects\\HttpNASA\\target\\generated-sources\\" + image_name);
        while ((count = inputStream.read(buffer)) != -1) {
            fos.write(buffer, 0, count);
        }
        fos.close();
    }
}