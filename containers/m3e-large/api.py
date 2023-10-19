from sentence_transformers import SentenceTransformer
from flask import Flask, request, jsonify
import json
import os

app = Flask(__name__)
# Load model from start
model_path = os.getenv("MODEL_PATH", default='/opt/models/m3e-large')
print("loading model from: " + model_path)
model = SentenceTransformer(model_path)
print("loading model success")

"""
Calculate the sentences embedding.
param: {"sentences":["我是中国人"]}
return: {"embeddings":[
            0.23825165629386902,
            ...,
            -1.2863532304763794
        ]}
"""


@app.route('/m3e/embedding', methods=["POST"])
def embedding():
    body = request.get_data()
    data = json.loads(body)
    sentences = data['sentences']
    embeddings = model.encode(sentences,normalize_embeddings=True).tolist()
    return jsonify({
        "embeddings": embeddings
    })


"""
return api status for health check
return: {"status": "ok"}
"""


@app.route('/m3e/actuator/health', methods=["GET"])
def health():
    return jsonify({
        "status": "ok"
    })


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8080