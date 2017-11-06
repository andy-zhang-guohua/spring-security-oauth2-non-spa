package andy.backyard.admin.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 自定义的 PermissionEvaluator, EL hasPermission 的具体实现
 * <p>
 * Created by ZhangGuohua on 2017/10/20.
 */
@Component
public class HasPermissionEvaluator implements PermissionEvaluator {
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        String username = authentication.getName();
        return !StringUtils.isBlank(username);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        String username = authentication.getName();
        return !StringUtils.isBlank(username);
    }
}