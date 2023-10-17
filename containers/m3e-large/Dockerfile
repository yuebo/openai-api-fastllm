FROM python:3.10

RUN mkdir -p /opt/app

ADD requirements.txt /opt/app/requirements.txt

VOLUME /opt/models

WORKDIR /opt/app

RUN pip install -r requirements.txt -i https://pypi.tuna.tsinghua.edu.cn/simple --no-cache-dir

ADD api.py /opt/app/api.py

ENTRYPOINT ["python","api.py"]