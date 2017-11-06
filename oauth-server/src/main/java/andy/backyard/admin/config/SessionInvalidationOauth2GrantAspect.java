package andy.backyard.admin.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * 解决方案和目的 : 在用户授权完成后立刻失效授权过程中所使用的 session，用来避免浏览器中的javascript作为oauth 客户端, 用户主动logout后无需用户名密码自动登录成功的问题.
 * 所解决的问题 : 如果不失效授权过程中的 session，那么该 session 会存在于用户的浏览器中，当用户主动 logout 离开自己的服务系统后，很可能服务系统的
 * 浏览器端代码会自动跳转到一个受保护的页面从而引起新的一个授权申请，而这个已经存在的 session 会导致直接授权成功，而不会要求用户重新输入用户名密码。
 *
 * 参考资料 :
 * 1).https://stackoverflow.com/questions/33006234/spring-security-oauth2-invalidate-session-after-authentication
 * 2).https://github.com/spring-projects/spring-security-oauth/issues/140
 *
 *
 * Created by ZhangGuohua on 2017/10/18.
 */
@Slf4j
@Service
@Aspect
public class SessionInvalidationOauth2GrantAspect {

    private static final String FORWARD_OAUTH_CONFIRM_ACCESS = "forward:/oauth/confirm_access";


    @AfterReturning(value = "within(org.springframework.security.oauth2.provider.endpoint..*) && @annotation(org.springframework.web.bind.annotation.RequestMapping)", returning = "result")
    public void authorizationAdvice(JoinPoint joinpoint, ModelAndView result) throws Throwable {

        // If we're not going to the confirm_access page, it means approval has been skipped due to existing access
        // token or something else and they'll be being sent back to app. Time to end session.
        if (!FORWARD_OAUTH_CONFIRM_ACCESS.equals(result.getViewName())) {
            invalidateSession();
        }
    }

    @AfterReturning(value = "within(org.springframework.security.oauth2.provider.endpoint..*) && @annotation(org.springframework.web.bind.annotation.RequestMapping)", returning = "result")
    public void authorizationAdvice(JoinPoint joinpoint, View result) throws Throwable {
        // Anything returning a view and not a ModelView is going to be redirecting outside of the app (I think).
        // This happens after the authorize approve / deny page with the POST to /oauth/authorize. This is the time
        // to kill the session since they'll be being sent back to the requesting app.
        invalidateSession();
    }

    @AfterThrowing(value = "within(org.springframework.security.oauth2.provider.endpoint..*) &&  @annotation(org.springframework.web.bind.annotation.RequestMapping)", throwing = "error")
    public void authorizationErrorAdvice(JoinPoint joinpoint) throws Throwable {
        invalidateSession();
    }

    private void invalidateSession() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        HttpSession session = request.getSession(false);
        if (session != null) {
            log.warn(String.format("As part of OAuth application grant processing, invalidating session for request %s", request.getRequestURI()));

            session.invalidate();
            SecurityContextHolder.clearContext();
        }
    }

}
