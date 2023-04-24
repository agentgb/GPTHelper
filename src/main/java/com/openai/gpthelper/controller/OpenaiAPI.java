package com.openai.gpthelper.controller;

import com.openai.gpthelper.utils.OpenAIClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;

@RestController
@RequestMapping("/API")
public class OpenaiAPI {


    @GetMapping("/gpt/reply")
    @ResponseBody
    public String getChatGPTReplyByQuestion(String sender,String question){
        try {

            long start = System.currentTimeMillis();
            System.out.println("接口接收开始时间："+start);
            String answer = OpenAIClient.getReplyBByChatGPT(sender,question);
            long finish = System.currentTimeMillis();
            System.out.println("接口接收结束时间："+finish);
            System.out.println("接口总耗时："+(finish-start));
            return answer;
        } catch (Exception e) {
            return "请联系开发工程师，程序出错了："+e.getMessage();
        }
    }

    @GetMapping("/test")
    @ResponseBody
    public String getChatGPTReplyByQuestion11(){
        long start = System.currentTimeMillis();
        SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:dd");
        String retStrFormatNowDate = sdFormatter.format(start);
        System.out.println("接口接收开始时间："+retStrFormatNowDate);

        long finish = System.currentTimeMillis();

        System.out.println("接口接收结束时间："+finish);
        System.out.println("接口总耗时："+(finish-start));
        return "test";
    }
}



