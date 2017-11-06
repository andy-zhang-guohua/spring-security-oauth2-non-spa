package andy.backyard.admin.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 用于取代缺省的 Form Login 页面 ： org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter
 * <p>
 * Created by ZhangGuohua on 2017/10/25.
 */
@Controller
public class LoginController {

    @Value("${server.contextPath}")
    String contextPath;

    @RequestMapping(method = RequestMethod.GET, value = "/login")
    public String login(HttpServletRequest request, Map<String, Object> model) {
        String normalizedContextPath = StringUtils.defaultString(contextPath);
        if (!normalizedContextPath.endsWith("/"))
            normalizedContextPath += "/";

        if (!normalizedContextPath.equals("/")) {
            model.put("contextPath", normalizedContextPath);
        }

        // 查看是否有错误
        HttpSession session = request.getSession(false);
        if (session != null) {
            AuthenticationException ex = (AuthenticationException) session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            if (ex != null) {
                String errorMessage = ex.getMessage();
                model.put("errorMessage", errorMessage);
            }
        }
        return "login";
    }
}
