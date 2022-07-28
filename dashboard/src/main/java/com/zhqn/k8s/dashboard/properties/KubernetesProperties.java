package com.zhqn.k8s.dashboard.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Date:     2022/7/26 19:42
 * Description:
 * @author zhouquan3
 */
@ConfigurationProperties(prefix = "k8s")
@Component
@Data
public class KubernetesProperties {
    String url;
    String token;
}
