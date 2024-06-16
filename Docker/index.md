### Docker
* 快速构建、运行、管理应用的工具

### 镜像和容器
* 当我们利用 Docker 安装应用时，Docker 会自动搜索并下载应用镜像（image）。
* 镜像不仅包含应用本身，还包含应用运行所需要的环境、配置、系统函数库。
* Docker 会在运行镜像时创建一个隔离环境，称为容器（container）
* 镜像仓库：存储和管理镜像的平台，Docker 官方维护了一个公共仓库

### 什么是镜像？
* 将应用所需的函数库、依赖、配置等与应用一起打包得到的就是镜像

### 什么是容器？
* 为每个镜像的应用进程创建的隔离运行环境就是容器

### docker run
* docker run：创建并运行一个容器， -d 是让容器在后台运行
* --name mysql：给容器起个名字，必须唯一
* -p 3306:3306：设置端口映射，宿主机端口映射到容器内端口
* -e KEY=VALUE：设置环境变量
* mysql：指定运行的镜像的名字
```dockerfile
docker run -d --name mysql -p 3306:3306 -e TZ=Asia/Shanghai -e MYSQL_ROOT_PASSWORD=123 mysql:5.7
```

### 镜像命名规范
* 镜像名称一般分两部分组成：【repository】：【tag】  mysql:5.7
* repository：是镜像名
* tag：是镜像的版本
* 在没有指定 tag 时，默认是 latest，代表最新版本的镜像

### docker 常见命令
* docker pull 镜像名：拉取镜像
* docker push：发布
* docker images：查看镜像
* docker rmi 镜像名:版本(latest)：删除镜像
* docker run：创建容器
* docker ps：正在运行的容器
* docker stop 容器名：停掉容器
* docker start 容器名：启动容器
* docker logs 容器名：查看日志
* docker ps -a：所有的容器
* docker rm 容器名：删除容器
* docker rm 容器名 -f：强制删除容器
* docker exec -it 容器名 bash：进入容器内部，使用命令行和容器交互

### 数据卷
* 数据卷（volume）是一个虚拟目录，它将宿主机目录映射到容器内目录，方便我们操作容器内文件，或者方便迁移容器产生的数据。是容器内目录与宿主机目录之间映射的桥梁
* 卷已创建宿主机目录会自动创建

### 如何挂载数据卷？(数据卷挂载 )
* 在执行 docker run 命令时，使用 -v 数据卷:容器内目录，可以完成数据卷挂载
* docker run -d --name nginx -p 80:80 -v html:/usr/share/nginx/html  nginx
* 当创建容器时，如果挂载了数据卷且数据卷不存在，会自动创建数据卷
* 宿主机目录：/var/lib/docker/volumes/html/_data

### 数据卷命令
* docker volume create：创建数据卷
* docker volume ls：查看所有数据卷
* docker volume rm：删除指定数据卷
* docker volume inspect：查看某个数据卷的详情
* docker volume prune：清除数据卷
  
### 本地目录挂载
* docker run -d --name mysql -p 3307:3306 -e TZ=Asia/Shanghai -e MYSQL_ROOT_PASSWORD=123 -v /Users/dengwenjie/Desktop/data:/var/lib/mysql -v /Users/dengwenjie/Desktop/init:/docker-entrypoint-initdb.d -v /Users/dengwenjie/Desktop/conf:/ect/mysql/conf.d mysql
* 在执行 docker run 命令时，使用 -v 本地目录:容器内目录 可以完成本地目录挂载
* 本地目录必须以 "/" 或 "./" 开头，如果直接以名称开头，会被识别数据卷而非本地目录
* -v mysql:/var/lib/mysql 会被识别为一个数据卷叫 mysql
* -v ./mysql:/var/lib/mysql 会被识别为当前目录下的 mysql 目录

### 自定义镜像
* 镜像就是包含了应用程序、程序运行的系统函数库、运行配置等文件的文件包。构建镜像的过程其实就是把上述文件打包的过程

### 镜像结构
* 入口（Entrypoint） ：镜像运行入口，一般是程序启动的脚本和参数
* 层（Layer）：添加安装包、依赖、配置等，每次操作都形成新的一层
* 基础镜像（BaseImage）：应用依赖的系统函数库、环境、配置、文件等

### dockerfile
* 告诉 docker 镜像结构
* Dockerfile 就是一个文本文件，其中包含一个个的指令（Instruction），用指令来说明要执行什么操作来构建镜像。将来 Docker 可以根据 Dockerfile 帮我们构镜像
* 指令               说明                     示例
* FROM            指定基础镜像              FROM centos:6
* ENV         设置环境变量，可在后面指令使用   ENV key value
* COPY        拷贝本地文件到镜像的指定目录     COPY ./jre11.tar.gz /tmp
* RUN         执行Linux的shell命令，一般是安装过程的命令   
* EXPOSE      指定容器运行时监听的端口，是给镜像使用者看的
* ENTRYPOINT  镜像中应用的启动命令，容器运行时调用

### Dockerfile 是做什么的？
* Dockerfile 就是利用固定的指令来描述镜像的结构和构建过程，这样 Docker 才可以依次来构建镜像
```dockerfile
# 基础镜像
FROM openjdk:11.0-jre-buster
# 设定时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
# 拷贝jar包
COPY docker-demo.jar /app.jar
# 入口
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### 构建镜像的命令是什么？
* docker build -t 镜像名 Dockerfile目录

### 网络
* 加入自定义网络的容器才可以通过容器名相互访问，Docker 的网络操作命令如下：
* docker network create 网络名：创建一个网络
* docker network ls：查看所有网络
* docker network rm 网络名：删除指定网络
* docker network prune 网络名：清除未使用的网络
* docker network connect 网络名 容器名：使指定容器连接加入某网络
* docker network disconnect 网络名 容器名：使指定容器连接离开某网络
* docker network inspect 网络名：查看网络详情信息
* 可以用 容器名去访问，一但两个容器加入了同一个网络他们可以用容器名互相访问，前提是自定义网络

### DockerCompose
* Docker Compose 通过一个单独的 docker-compose.yml 模版文件（yaml格式）来定义一组相关联的应用容器，帮助我们实现多个相互关联的 Docker 容器的快速部署
* docker compose 的命令格式如下：
* docker compose 【options】【command】
* Options：-f指定 compose 文件的路径和名称，-p创建并启动所有service容器
* Commands：up创建并启动所有service容器，down停止并移除所有容器、网络
* ps，logs，stop，start，restart，top，exec
```yaml
version: "3.8"

services:
  mysql:
    image: mysql
    container_name: mysql
    ports:
      - "3307:3306"
    environment:
      TZ: Asia/Shanghai
      MYSQL_ROOT_PASSWORD: 123
    volumes:
      - "/Users/dengwenjie/dockermountdata/conf:/etc/mysql/conf.d"
      - "/Users/dengwenjie/dockermountdata/data:/var/lib/mysql"
      - "/Users/dengwenjie/dockermountdata/init:/docker-entrypoint-initdb.d"
    networks:
      - hm-net
  hmall:
    build: 
      context: .
      dockerfile: Dockerfile
    container_name: hmall
    ports:
      - "8080:8080"
    networks:
      - hm-net
    depends_on:
      - mysql
  nginx:
    image: nginx
    container_name: nginx
    ports:
      - "18080:18080"
      - "18081:18081"
    volumes:
      - "/Users/dengwenjie/Desktop/nginx/nginx.conf:/etc/nginx/nginx.conf"
      - "/Users/dengwenjie/Desktop/nginx/html:/usr/share/nginx/html"
    depends_on:
      - hmall
    networks:
      - hm-net
networks:
  hm-net:
    name: hmall
```
docker run -d --name mysql -p 3307:3306 -e TZ=Asia/Shanghai -e MYSQL_ROOT_PASSWORD=123 -v /Users/dengwenjie/dockermountdata/data:/var/lib/mysql -v /Users/dengwenjie/dockermountdata/init:/docker-entrypoint-initdb.d -v /Users/dengwenjie/dockermountdata/conf:/ect/mysql/conf.d mysql
