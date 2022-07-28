package com.zhqn.k8s.dashboard.controller;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Config;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Date:     2022/7/26 19:24
 * Description:
 * @author zhouquan3
 */
@RestController
@RequestMapping("/k8s")
public class TestController {

    @GetMapping("/")
    public String test() throws ApiException {
        ApiClient apiClient = Config.fromUrl("http://192.168.48.136:8009");
        Configuration.setDefaultApiClient(apiClient);
        CoreV1Api coreV1Api = new CoreV1Api();
        V1PodList list = coreV1Api.listPodForAllNamespaces(null, null, null, null,
                null, null, null, null, null, null);
        for (V1Pod item : list.getItems()) {

            System.out.println(item.getMetadata().getNamespace() + ":" + item.getMetadata().getName());
        }
        System.out.println();
        V1NamespaceList namespaceList = coreV1Api.listNamespace(null, null, null, null, null, null, null, null, null, null);
        for (V1Namespace namespace : namespaceList.getItems()) {
            System.out.println(namespace.getMetadata().getName());
        }

        return "ok";
    }

    @GetMapping("/createNamespace")
    public String createNamespace(String namespace) {
        CoreV1Api coreV1Api = new CoreV1Api();
        V1Namespace v1Namespace = new V1Namespace();
        V1ObjectMeta meta = new V1ObjectMeta();
        meta.setName(namespace);
        v1Namespace.setMetadata(meta);
        try {
            coreV1Api.createNamespace(v1Namespace, null, null, null, null);
        } catch (ApiException e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return "ok";
    }
}
