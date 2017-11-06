package andy.backyard.admin.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * 受 oauth2 server 保护的资源，用于获取当前 token 所对应的用户信息，或者回收 token
 */
@RestController
public class UserController {
    @RequestMapping(method = RequestMethod.GET, value = "/user")
    public Principal user(Principal user) {
        return user;
    }

    @Resource(name = "tokenServices")
    ConsumerTokenServices tokenServices;

    /**
     * token回收功能，注意这里的 HTTP Method 是 DELETE
     *
     * @param request
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/user/token")
    public String revokeToken(HttpServletRequest request) {
        final String authorization = StringUtils.defaultString(request.getHeader("Authorization"));
        final String name = "Bearer";
        final int bearerIndex = StringUtils.indexOfIgnoreCase(authorization, name);
        if (bearerIndex >= 0) {
            String tokenId = authorization.substring(bearerIndex + name.length() + 1);
            tokenServices.revokeToken(tokenId);
            return "OK";
        }

        return "FAILED";
    }
}
