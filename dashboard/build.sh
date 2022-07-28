mvn clean compile -Dmaven.test.skip=true package
docker build . -t registry.cn-hangzhou.aliyuncs.com/zhqn/dashboard:1.0.0
docker push registry.cn-hangzhou.aliyuncs.com/zhqn/dashboard:1.0.0