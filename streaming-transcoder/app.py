from flask import Flask, jsonify
from flask_cors import CORS

app = Flask(__name__)
CORS(app) # 모든 도메인에서 접근 가능

@app.route('/')
def hello():
    return jsonify(message="Flask 서버가 잘 작동 중입니다!")

if __name__ == '__main__':
    app.run(debug=True)