package com.openai.gpthelper.utils;

import com.openai.gpthelper.conts.Conts;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OpenAIClient {
    private static final Logger logger = LoggerFactory.getLogger(OpenAIClient.class);

    private static final Map<String,JSONArray> msgMap = new HashMap<>();

    // Create the proxy object with authentication credentials
    static Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(Conts.proxyHost, Conts.proxyPort));

    static Authenticator proxyAuthenticator = new Authenticator() {
        @Override
        public Request authenticate(Route route, Response response) {
            String credential = Credentials.basic(Conts.proxyUsername, Conts.proxyPassword);
            return response.request().newBuilder()
                .header("Proxy-Authorization", credential)
                .build();
        }
    };


    // Create the HTTP client with the proxy and authentication credentials, and send the request
    static OkHttpClient client = new OkHttpClient.Builder()
        .proxy(proxy)
        .eventListenerFactory(HttpEventListener.FACTORY)
        .proxyAuthenticator(proxyAuthenticator)
        .readTimeout(60, TimeUnit.SECONDS)//设置读取超时时间
        .build();

    public static String getReplyBByChatGPT(String sender,String question) throws Exception{
        JSONObject objQuestion = new JSONObject();
        objQuestion.put("role","user");
        objQuestion.put("content",question);
        if (msgMap.containsKey(sender)){
            JSONArray historyMsg = msgMap.get(sender);
            historyMsg.put(objQuestion);
        }else {
            JSONArray array = new JSONArray();
            array.put(objQuestion);
            msgMap.put(sender,array);
        }
        // Build the request object
        RequestBody body = RequestBody.create(MediaType.parse("application/json"),
            "{\"model\":\"gpt-3.5-turbo\",\"temperature\": 0.7," +
                "\"messages\":"+getLast3Array(msgMap.get(sender))+" }");

        Request request = new Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer " + Conts.apiKey)
            .post(body)
            .build();
        Response response = client.newCall(request).execute();

        assert response.body() != null;
        String personal= response.body().string();
            // Print the response body
        logger.info("接口返回的数据是:{}",personal);

        StringBuffer sb = new StringBuffer();
        JSONObject obj = new JSONObject(personal);
        JSONArray array = obj.getJSONArray("choices");
        array.forEach(a->{
            JSONObject objA = (JSONObject)a;
            //if (!"stop".equals(objA.get("finish_reason"))){
                JSONObject message = (JSONObject)objA.get("message");
                sb.append(message.get("content"));
            //}
        });


        JSONObject objAnswer = new JSONObject();
        objAnswer.put("role","assistant");
        objAnswer.put("content",sb);
        JSONArray historyMsg = msgMap.get(sender);
        historyMsg.put(objAnswer);
        logger.info("问题是：{}，openai回答是：{}",question,sb.toString());
        return sb.toString();

    }

    public static JSONArray getLast3Array(JSONArray advice){
        int length=advice.length();
        JSONArray advice_3=new JSONArray();
        if(length <=10) return advice;
        for (int i = length - 10; i < length ; i++)
            {
                // 遍历jsonarray 数组，把每一个对象转成json对象
                JSONObject job = advice.optJSONObject(i);
                //向jsonarray 数组添加新的元素
                advice_3.put(job);
            }
        return advice_3;
    }

}

