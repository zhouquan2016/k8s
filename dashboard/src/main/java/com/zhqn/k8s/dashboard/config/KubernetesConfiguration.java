package com.zhqn.k8s.dashboard.config;

import com.zhqn.k8s.dashboard.properties.KubernetesProperties;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1NodeList;
import io.kubernetes.client.util.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * Date:     2022/7/26 19:23
 * Description:
 * @author zhouquan3
 */
@Configuration
@EnableConfigurationProperties(KubernetesProperties.class)
@Slf4j
public class KubernetesConfiguration implements InitializingBean {

    @Resource
    KubernetesProperties kubernetesProperties;

    @Override
    public void afterPropertiesSet() {
        ApiClient apiClient = Config.fromUrl(kubernetesProperties.getUrl(), false);
        if (StringUtils.isNotBlank(kubernetesProperties.getToken())) {
            apiClient.addDefaultHeader("Authorization", "Bearer " + kubernetesProperties.getToken());
        }
        io.kubernetes.client.openapi.Configuration.setDefaultApiClient(apiClient);
        CoreV1Api coreV1Api = new CoreV1Api();
        V1NodeList nodeList = null;
        try {
            nodeList = coreV1Api.listNode(null, null, null, null, null, null, null, null, null, null);
        } catch (ApiException e) {
            log.error("连接k8s失败:{}", e.getResponseBody());
            throw new RuntimeException(e.getResponseBody());
        }
        for (V1Node node : nodeList.getItems()) {
            System.out.println(node.getMetadata().getName());
        }
    }
}
