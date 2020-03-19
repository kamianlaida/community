package com.poppy.provider;

import com.alibaba.fastjson.JSON;
import com.poppy.dto.AccessTokenDTO;
import com.poppy.dto.GithubUser;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GitHubProvider {


    public String getAccessToken(AccessTokenDTO accessTokenDTO){
        MediaType mediaType
                = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON.toJSONString(accessTokenDTO), mediaType);
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String str = response.body().string();
            System.out.println(str);
            String token = str.split("&")[0].split("=")[1];
            return token;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public GithubUser getUser(String accessToken){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.github.com/user?access_token="+accessToken)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String str = response.body().string();
            GithubUser githubUser = JSON.parseObject(str, GithubUser.class);
            System.out.println(githubUser);
            return githubUser;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
