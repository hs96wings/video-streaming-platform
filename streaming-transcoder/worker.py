import redis
import json, requests, subprocess
from dotenv import load_dotenv
import os
import time

load_dotenv()
r = redis.Redis(host='localhost', port=6379, db=0)

def worker_loop():
    while True:
        try:
            _, job = r.blpop('videoQueue') # 블로킹 대기
            process_job(job)
        except redis.exceptions.ResponseError as e:
            print(f"[Redis ResponseError] {e}")
            time.sleep(3)
        except redis.exceptions.ConnectionError as e:
            print(f"[Redis ConnectionError] {e}")
            time.sleep(5)
        except Exception as e:
            print(f"[Unexpected Error] {e}")
            time.sleep(2)

def process_job(job):
    data = json.loads(job)
    vid, path = data['videoId'], data['path']
    # 상태 업데이트: 호출 전 spring에 PATCH /api/video/{vid}/status?status=PROCESSING
    requests.patch(f"http://localhost:8080/api/video/{vid}/status?status=PROCESSING".format(vid))
    base_dir = os.environ.get("base_dir")

    output_dir = f"{base_dir}/hls_output/{vid}"
    new_path = f"{output_dir}/index.m3u8"
    subprocess.run(['ffmpeg', '-i', path, '-codec', 'copy', '-start_number', '0', '-hls_time', '10', '-hls_list_size', '0', '-f', 'hls', new_path])
    thumb_path = f"{base_dir}/thumbs/{vid}.png"

    base_url = os.environ.get("base_url")
    save_thumb_path = f"{base_url}/thumbs/{vid}.png"
    save_video_path = f"{base_url}/hls_output/{vid}.m3u8"
    subprocess.run(['ffmpeg', '-i', new_path, '-ss', '00:00:01', '-vframes', '1', thumb_path])
    requests.patch(f"http://localhost:8080/api/video/{vid}/status?status=READY".format(vid),
                    json={"videoPath": save_video_path, "thumbnailPath": save_thumb_path})
