### Docker
* 快速构建、运行、管理应用的工具

### 镜像和容器
* 当我们利用 Docker 安装应用时，Docker 会自动搜索并下载应用镜像（image）。
* 镜像不仅包含应用本身，还包含应用运行所需要的环境、配置、系统函数库。
* Docker 会在运行镜像时创建一个隔离环境，称为容器（container）
* 镜像仓库：存储和管理镜像的平台，Docker 官方维护了一个公共仓库

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