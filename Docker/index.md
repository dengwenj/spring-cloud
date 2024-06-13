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

### 如何挂载数据卷？
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