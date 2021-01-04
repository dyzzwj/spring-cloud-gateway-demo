package com.dyzwj.springcloudgatewaydemo.codeconfig;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

//@Configuration
public class MyHelloConfig {

    @Bean
    public RouteLocator routesRequestBody(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("rewrite_request_body", r -> r.path("/hello/**")
                        .filters(f -> f.modifyRequestBody(OtherUserReq.class, MyUser.class, MediaType.APPLICATION_JSON_VALUE,
                                (exchange, s) -> Mono.just(myUser(s))))
                        .uri("http://localhost:4444"))
                .build();
    }

    @Bean
    public RouteLocator routesResponseBody(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("rewrite_response_body", r -> r.path("/hello/**")
                        .filters(f -> f.modifyResponseBody(MyUser.class, OtherUserResp.class, MediaType.APPLICATION_JSON_VALUE,
                                (exchange, s) -> Mono.just(otherUserResp(s))))
                        .uri("http://localhost:4444"))
                .build();
    }

    private OtherUserResp otherUserResp(MyUser myUser) {
        OtherUserResp otherUserResp = new OtherUserResp();
        otherUserResp.setUserName(myUser.getUsername());
        otherUserResp.setPasswd(myUser.getPassword());
        return otherUserResp;
    }


    private MyUser myUser(OtherUserReq otherUser){

        MyUser myUser = new MyUser();
        myUser.setUsername(otherUser.getName());
        myUser.setPassword(otherUser.getPwd());
        return myUser;
    }


    static class MyUser{

        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    static class OtherUserReq {
        private String name;
        private String pwd;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPwd() {
            return pwd;
        }

        public void setPwd(String pwd) {
            this.pwd = pwd;
        }
    }


    static class OtherUserResp {
        private String userName;
        private String passwd;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPasswd() {
            return passwd;
        }

        public void setPasswd(String passwd) {
            this.passwd = passwd;
        }
    }


}
