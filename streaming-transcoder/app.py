from flask import Flask, jsonify
from threading import Thread
from worker import worker_loop

app = Flask(__name__)

@app.route('/health', methods=["GET"])
def health():
    return jsonify(status='ok'), 200

if __name__ == '__main__':
    # 백그라운드에서 worker_loop 실행
    t = Thread(target=worker_loop, daemon=True)
    t.start()

    app.run()