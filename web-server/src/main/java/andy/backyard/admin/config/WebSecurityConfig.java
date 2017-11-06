package andy.backyard.admin.config;

import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

import java.util.Map;

@Configuration
/**
 * 为什么要把注解 @EnableOAuth2Sso 放到自定义类 WebSecurityConfig extends WebSecurityConfigurerAdapter  上面，
 * 参考下面的官方文档 ：
 *
 * Enable OAuth2 Single Sign On (SSO). If there is an existing WebSecurityConfigurerAdapter
 * provided by the user and annotated with @EnableOAuth2Sso, it is enhanced by adding an
 * authentication filter and an authentication entry point. If the user only has @EnableOAuth2Sso
 * but not on a WebSecurityConfigurerAdapter then one is added with all paths secured and with
 * an order that puts it ahead of the default HTTP Basic security chain in Spring Boot.
 *
 *
 * 网络资料 :
 *
 * The OAuth2 client filter is OAuth2ClientAuthenticationProcessingFilter. It is used to acquire an OAuth2 access token
 * from an authorization server and load an Authentication object into SecurityContext. However, it needs the
 * OAuth2ClientContextFilter filter to redirect a request to authorization server.The two filters use UserRedirectRequiredException
 * to pass control. Both need to be wired into the filter chain and OAuth2ClientContextFilter should execute before the main
 * Spring security filter.
 */
@EnableOAuth2Sso
@EnableWebSecurity // 用与不用有什么影响 ?
// 启用控制方法层面的Security注解，例如最常用的@PreAuthorize
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    static final String ROLE_PREFIX = "";

    /**
     * 缺省情况下， Spring Security 的 hasRole('ADMIN') 等价于 hasAuthority('ROLE_ADMIN'),
     * 定义此 Bean, 使 Java 注解 @PreAuthorize 中 hasRole 和 hasAuthority完全等价，
     * 也就是 hasRole('ADMIN') == hasAuthority('ADMIN')
     *
     * @return
     */
    @Bean
    GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults(ROLE_PREFIX); // Remove the ROLE_ prefix
    }

    /**
     * @return
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        // 静态资源文件对外公开，不需要访问授权
        //2017-10-26, 以下语句注释掉访问静态文件也不需要授权，为什么 ？
        //web.ignoring().antMatchers("/static/**");

        /**
         * 缺省情况下， Spring Security 的 hasRole('ADMIN') 等价于 hasAuthority('ROLE_ADMIN'),
         * 使用以下代码, 使 spring security JSP tag 中 hasRole 和 hasAuthority完全等价，
         * 也就是 hasRole('ADMIN') == hasAuthority('ADMIN')
         */
        if (web.getExpressionHandler() instanceof DefaultWebSecurityExpressionHandler) {
            DefaultWebSecurityExpressionHandler defaultWebSecurityExpressionHandler = (DefaultWebSecurityExpressionHandler) web.getExpressionHandler();
            defaultWebSecurityExpressionHandler.setDefaultRolePrefix(ROLE_PREFIX);
        }
    }


    /**
     * 自定义一个Principal提取器，诸如给 UserInfoTokenServices,用于从 oauth2 user endpoint 提取用户信息中的 Principal 信息对象，key 为 principal
     * 为什么要自定义 : UserInfoTokenServices 缺省使用 FixedPrincipalExtractor, 提取到的 principal 是一个字符串,对应 key 为 name
     * @return
     */
    @Bean
    public PrincipalExtractor principalExtractor() {
        return new PrincipalExtractor() {
            final String[] PRINCIPAL_KEYS = new String[]{"principal"};

            @Override
            public Object extractPrincipal(Map<String, Object> map) {
                for (String key : PRINCIPAL_KEYS) {
                    if (map.containsKey(key)) {
                        return map.get(key);
                    }
                }
                return null;
            }
        };
    }
}
