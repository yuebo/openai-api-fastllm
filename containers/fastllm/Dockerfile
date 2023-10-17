FROM nvidia/cuda:12.2.0

ADD build /opt/fastllm

EXPOSE 8080
ENV MODEL_PATH=chatglm2-6b-float16.flm

VOLUME /opt/models/fastllm

CMD /opt/fastllm/webui -w /opt/fastllm/web  -p /opt/models/fastllm/$MODEL_PATH --port 8080
