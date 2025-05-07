from redis import Redis
import json, requests, subprocess

r = Redis(host='localhost', port=6379, db=0)

def worker_loop():
    while True:
        _, job = r.blpop('videoQueue') # 블로킹 대기
        data = json.loads(job)
        print(data)
        vid, path = data['videoId'], data['path']
        # 상태 업데이트: 호출 전 spring에 PATCH /api/video/{vid}/status?status=PROCESSING
        requests.patch(f"http://localhost:8080/api/video/{vid}/status?status=PROCESSING".format(vid))
        # 여기까지 테스트