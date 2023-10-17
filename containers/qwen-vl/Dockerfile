FROM python:3.10

RUN mkdir -p /opt/app

ADD requirements.txt /opt/app/requirements.txt

VOLUME /opt/models

WORKDIR /opt/app

RUN pip install -r requirements.txt -i https://pypi.tuna.tsinghua.edu.cn/simple --no-cache-dir

ADD openai_api.py /opt/app/openai_api.py

ENV MODEL_NAME="/opt/models/Qwen-VL-Chat-Int4"
EXPOSE 8080
ENTRYPOINT  python openai_api.py -c $MODEL_NAME --server-name 0.0.0.0 --server-port 8080