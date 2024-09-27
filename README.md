# r-pan

这是一个简单的网上云盘系统。

## 目录结构

当前目录是后端项目的根目录，默认目录结构如下：

### 模块说明

1. **framework**: 技术框架包
    - **core**: 核心公共模块，包含一些常量类和工具类。
    - **swagger2**: API文档相关模块。
    - **web**: 处理网络请求跨域问题以及记录日志的包，并且继承了Spring Web。

2. **server**: 后端服务模块，包含服务启动类以及主体的业务代码。

### 引用关系

- **core**: 
  - 引用了Guava、Hutool、Lombok等基本的第三方库。
  - 基础模块，不需要引用其他内部模块。

- **swagger2**:
  - 引用了core模块。
  - API文档工具模块，相对独立，仅依赖于core模块。

- **web**:
  - 引用了core和Spring Web。
  - Web后端服务的父模块，依赖于core和Spring Web。

- **server**:
  - 引用了swagger2和web。
  - 实际的Web后端服务模块，依赖于swagger2来提供API文档支持，并依赖于web作为Spring的后端服务器。

通过这些层次分明的模块化设计，可以确保系统具有良好的扩展性和可维护性。