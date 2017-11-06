package andy.backyard.admin.config;

import andy.backyard.admin.service.AdminAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AdminAuthenticationProvider adminAuthenticationProvider;//自定义验证

    @Autowired
    public void globalUserDetails(final AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(adminAuthenticationProvider);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * @return
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        // 静态资源文件对外公开，不需要访问授权
        web.ignoring().antMatchers("/static/**");
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.requestMatchers()
                .antMatchers("/login", "/oauth/authorize")
                .and()
                .formLogin().loginPage("/login").permitAll()
                .and()
                .httpBasic().disable()
                .authorizeRequests()
                .anyRequest().authenticated();
    }

}
