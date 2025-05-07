from redis import Redis
import json, requests, subprocess
from dotenv import load_dotenv
import os

load_dotenv()
r = Redis(host='localhost', port=6379, db=0)

def worker_loop():
    while True:
        _, job = r.blpop('videoQueue') # 블로킹 대기
        data = json.loads(job)
        vid, path = data['videoId'], data['path']
        # 상태 업데이트: 호출 전 spring에 PATCH /api/video/{vid}/status?status=PROCESSING
        requests.patch(f"http://localhost:8080/api/video/{vid}/status?status=PROCESSING".format(vid))
        base_dir = os.environ.get("base_dir")
        new_path = f"{base_dir}/hls_output/{vid}.m3u8"
        subprocess.run(['ffmpeg', '-i', path, '-codec', 'copy', '-start_number', '0', '-hls_time', '10', '-hls_list_size', '0', '-f', 'hls', new_path])
        thumb_path = f"{base_dir}/thumbs/{vid}.png"
        subprocess.run(['ffmpeg', '-i', new_path, '-ss', '00:00:01', '-vframes', '1', thumb_path])
        requests.patch(f"http://localhost:8080/api/video/{vid}/status?status=READY".format(vid))
