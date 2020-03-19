package com.poppy.controller;

import com.poppy.dto.AccessTokenDTO;
import com.poppy.dto.GithubUser;
import com.poppy.mapper.UserMapper;
import com.poppy.model.User;
import com.poppy.provider.GitHubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.UUID;

@Controller
public class AuthorizeController {

    @Autowired
    private GitHubProvider gitHubProvider;
    @Autowired
    private UserMapper userMapper;
    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String secret;
    @Value("${github.redirect.uri}")
    private String uri;
    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code")String code,
                           @RequestParam("state")String state,
                           HttpServletRequest request){
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(uri);
        accessTokenDTO.setState(state);
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(secret);
        String accessToken = gitHubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser = gitHubProvider.getUser(accessToken);
        if(githubUser != null){
//            登录成功写cookie和session
            User user = new User();
            user.setToken(UUID.randomUUID().toString());
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
            request.getSession().setAttribute("user",githubUser);
            return "redirect:/";
        }else{
//            登录失败 重新登录
            return "redirect:/";
        }
    }
}
