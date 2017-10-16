package com.rickxpc.ripay.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.List;

@Configuration
public class RestTemplateConfig {
    @Value("${proxy.host}")
    private String host;
    @Value("${proxy.port}")
    private String port;
    @Value("${proxy.domain}")
    private String domain;
    @Value("${proxy.username}")
    private String username;
    @Value("${proxy.password}")
    private String password;

    @Bean
    public RestTemplate getProxiedRestTemplate(){
        AuthScope authScope = new AuthScope(host, Integer.parseInt(port));
        Credentials credentials = new NTCredentials(username, password, null, domain);
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(authScope, credentials);
        HttpHost proxy = new HttpHost(host, Integer.parseInt(port));
        org.apache.http.client.HttpClient httpClient = HttpClientBuilder.create().setProxy(proxy).setDefaultCredentialsProvider(credentialsProvider).build();
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        StringHttpMessageConverter stringHttpMessageConverter = messageConverters.stream().filter(mc -> mc.getClass() == StringHttpMessageConverter.class).findFirst().map(mc -> (StringHttpMessageConverter) mc).get();
        stringHttpMessageConverter.setDefaultCharset(Charset.forName("UTF-8"));
        return restTemplate;
    }
}
