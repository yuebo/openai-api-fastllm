OPEN AI API For LLM Chat API
----------------------------------------

这是一个基于fastllm webui实现的Openai API。主要的API列表如下

* /fastllm-api/v1/models
* /fastllm-api/v1/models/{model}
* /fastllm-api/v1/chat/completions
* /fastllm-api/v1/completions
* /fastllm-api/v1/embeddings

也可以兼容openai api格式转发，用于给不支持stream格式的私有模型转发stream格式内容。

* /openai-api/v1/models
* /openai-api/v1/models/{model}
* /openai-api/v1/chat/completions

### 其他资源

* 可以使用One-Api整合多个私有模型的为OpenAI的API格式，请参考[One-Api](https://github.com/songquanpeng/one-api)
* 可以兼容FastGPT格式, 请参考[FastGPT](https://github.com/labring/FastGPT)。
* FastLLM是一个私有化的模型加速框架，可以使用改框架部署百川、ChatGLM2和Qwen-7B等模型。

### FastLLM容器化部署
需要安装官方手册编译生成build后的二进制文件。

#### 通过`Dockerfile`编译fastllm容器。
```bash
cd containers/fastllm
# make sure the build dir is under here
docker build . -t fastllm
```
#### 使用工具转换模型
请参考官方文档进行镜像转换。

#### 运行
```bash
docker run --name fastllm -d -p 8080 -e NVIDIA_VISIBLE_DEVICES=0 -e MODEL_PATH=chatglm2-6b-int4.flm -v /opt/models:/opt/models fastllm
```

### m3e large
m3e用于私有化部署Embedding API。

#### 编译镜像

```bash
cd containers/qwen-vl
docker build . -t qwen-vl
```

#### 下载模型
从仓库下载模型
```bash
git clone https://www.modelscope.cn/qwen/Qwen-VL-Chat-Int4.git
```

#### 运行
```bash
docker run --name qwen -d -p 8080 -e NVIDIA_VISIBLE_DEVICES=0 -v /opt/models:/opt/models qwen-vl
```
