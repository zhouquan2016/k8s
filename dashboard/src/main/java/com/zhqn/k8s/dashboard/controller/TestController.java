package com.zhqn.k8s.dashboard.controller;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.bouncycastle.util.Strings;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Date:     2022/7/26 19:24
 * Description:
 * @author zhouquan3
 */
@RestController
@RequestMapping("/k8s")
@Slf4j
public class TestController {

    @GetMapping("/")
    public String test() throws ApiException {
        CoreV1Api coreV1Api = new CoreV1Api();
        V1PodList list = coreV1Api.listPodForAllNamespaces(null, null, null, null,
                null, null, null, null, null, null);
        for (V1Pod item : list.getItems()) {
            V1ObjectMeta meta = item.getMetadata();
            if (Objects.nonNull(meta)) {
                System.out.println(meta.getNamespace() + ":" + meta.getName());
            }

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

    @GetMapping("/test")
    public String test(Integer loopCount, String namespace) {
        if (loopCount == null) {
            loopCount = 100;
        }
        if (namespace == null) {
            namespace = "default";
        }
        CoreV1Api coreV1Api = new CoreV1Api();
        try {
            for (int i = 0; i < loopCount; i++) {
                V1PodList v1PodList = coreV1Api.listNamespacedPod(namespace, null, null, null, null, null, null, null, null, null, null);
                if (Objects.isNull(v1PodList)) {
                    return "fail";
                }
                List<List<String>> lines = new LinkedList<>();
                lines.add(Arrays.asList("NAME", "READY", "STATUS", "startAt", "AGE"));
                if (CollectionUtils.isNotEmpty(v1PodList.getItems())) {
                    v1PodList.getItems().forEach(pod -> {
                        int readyContainerCount = 0, totalContainerCount = 0;
                        String status = null;
                        String age = null;
                        String startAt = null;
                        if (Objects.nonNull(pod.getStatus())) {
                            if (CollectionUtils.isNotEmpty(pod.getStatus().getContainerStatuses())) {
                                for (V1ContainerStatus containerStatus : pod.getStatus().getContainerStatuses()) {
                                    if (Boolean.FALSE.equals(containerStatus.getReady())) {
                                        if (status == null) {
                                            status = getContainerState(containerStatus);
                                        }
                                    }else {
                                        readyContainerCount++;
                                    }
                                    totalContainerCount++;
                                }
                            }
                            if (status == null) {
                                status = pod.getStatus().getPhase();
                            }
                            age = Duration.ofSeconds(pod.getStatus().getStartTime().getLong(ChronoField.INSTANT_SECONDS) - OffsetDateTime.now().getLong(ChronoField.INSTANT_SECONDS)).toString();
                            age.replaceAll("PT|-", "");
                            startAt = pod.getStatus().getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-hh HH:mm:ss"));
                        }
                        if (Objects.isNull(status)) {
                            status = "";
                            age = "";
                            startAt = "";
                        }

                        List<String> line = new LinkedList<>();
                        lines.add(line);
                        line.add(Objects.nonNull(pod.getMetadata()) ? pod.getMetadata().getName() : "");
                        line.add(readyContainerCount + "/" + totalContainerCount);
                        line.add(status);
                        line.add(startAt);
                        line.add(age);
                    });
                }
                //有多少列
                int cols = lines.get(0).size();
                //每一列宽度集合
                List<Integer> colWidths = new ArrayList<>(cols);
                lines.get(0).forEach((unused) -> colWidths.add(0));
                lines.forEach(line -> {
                    for (int j = 0; j < line.size(); j++) {
                        Integer width = colWidths.get(j);
                        if (width == null) {
                            width = 0;
                        }
                        String cell = line.get(j);
                        if (cell.length() > width) {
                            width = cell.length();
                        }
                        //相邻列的间隙
                        width += 4;
                        colWidths.set(j, width);
                    }
                });
                lines.forEach(line -> {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int j = 0; j < line.size(); j++) {
                        int width = colWidths.get(j);
                        String cell = line.get(j);
                        stringBuilder.append(cell);
                        for (int k = cell.length(); k <= width; k++) {
                            stringBuilder.append(" ");
                        }
                    }

                    log.info(stringBuilder.toString());
                });
            }

        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        return "ok";
    }

    private String getContainerState(V1ContainerStatus containerStatus) {
        if (Objects.isNull(containerStatus) || Objects.isNull(containerStatus.getState())) {
            return null;
        }
        V1ContainerState state = containerStatus.getState();
        if (Objects.nonNull(state.getTerminated())) {
            return state.getTerminated().getReason();
        }else if (Objects.nonNull(state.getWaiting())) {
            return state.getWaiting().getReason();
        }
        return null;
    }
}
