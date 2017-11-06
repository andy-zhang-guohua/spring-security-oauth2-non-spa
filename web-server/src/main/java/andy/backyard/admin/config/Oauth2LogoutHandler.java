package andy.backyard.admin.config;

import andy.backyard.common.exception.ApplicationException;
import andy.backyard.common.exception.CommonErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * Created by ZhangGuohua on 2017/10/18.
 */
@Slf4j
@Component
@Qualifier("oauth2LogoutHandler")
public class Oauth2LogoutHandler implements LogoutHandler {
    @Value("${security.oauth2.resource.userInfoUri}")
    String userInfoUri;

    @Override
    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) {
        log.debug("executing Oauth2LogoutHandler.logout : {}", authentication);

        Object details = authentication.getDetails();
        boolean isOAuth2AuthenticationDetails = details.getClass().isAssignableFrom(OAuth2AuthenticationDetails.class);
        if (!isOAuth2AuthenticationDetails) {
            log.error("authentication.getDetails() 对象不是预期的类型 : 实际类型 = {},预期类型 = {}", details.getClass().getCanonicalName(), OAuth2AuthenticationDetails.class.getCanonicalName());
            throw new ApplicationException("认证详情对象类型不正确", CommonErrorCode.STATE_NOT_READY);
        }

        String accessToken = ((OAuth2AuthenticationDetails) details).getTokenValue();
        log.debug("User's access token to be invoked: {}", accessToken);

        revokeAccessToken(accessToken);
    }

    private void revokeAccessToken(String accessToken) {
        final String urlRevokeAccessToken = userInfoUri + "/token";
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "bearer " + accessToken);

        HttpEntity<String> request = new HttpEntity(params, headers);

        HttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
        HttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        restTemplate.setMessageConverters(Arrays.asList(new HttpMessageConverter[]{formHttpMessageConverter, stringHttpMessageConverter}));
        final String RESULT_OK = "OK";
        try {
            ResponseEntity<String> response = restTemplate.exchange(urlRevokeAccessToken, HttpMethod.DELETE, request, String.class);
            log.info("{}", response);
            boolean ok = RESULT_OK.equalsIgnoreCase(response.getBody());
            if (!ok)
                throw new ApplicationException("退出登录时删除访问访问令牌失败");
        } catch (HttpClientErrorException e) {
            log.error("Exception revoking access token on authorization server.server URL: {}", urlRevokeAccessToken, e);
            throw new ApplicationException(e);
        }
    }
}
