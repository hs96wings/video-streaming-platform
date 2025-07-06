import redis
import json, requests, subprocess
from dotenv import load_dotenv
import os
import time

load_dotenv()

# 개발 환경 여부 (default: False)
IS_DEV = os.getenv("IS_DEV", "false").lower() == "true"

r = redis.Redis(
    host=os.getenv("REDIS_HOST", "localhost" if IS_DEV else "redis"),
    port=int(os.getenv("REDIS_PORT", 6379)),
    db=0
)

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
    SPRING_HOST = os.getenv("SPRING_HOST", "localhost" if IS_DEV else "streaming-server")
    SPRING_PORT = os.getenv("SPRING_PORT", "8080")

    login_url = f"http://{SPRING_HOST}:{SPRING_PORT}/api/auth/login"

    login_data = {
        "userid": os.getenv("ADMIN_ID", "admin"),
        "password": os.getenv("ADMIN_PW", "1234" if IS_DEV else "admin7890")
    }

    res = requests.post(login_url, json=login_data)
    token = res.json().get("token")
    headers = { "Authorization": f"Bearer {token}" }

    data = json.loads(job)
    vid, path = data['videoId'], data['path']
    
    # 상태 변경 요청 (PROCESSING)
    requests.patch(f"http://{SPRING_HOST}:{SPRING_PORT}/api/video/{vid}/status?status=PROCESSING", headers=headers)

    base_dir = os.environ.get("BASE_DIR")
    output_dir = f"{base_dir}/hls_output/{vid}"
    os.makedirs(output_dir, exist_ok=True)

    new_path = f"{output_dir}/index.m3u8"
    subprocess.run(['ffmpeg', '-i', path, '-codec', 'copy', '-start_number', '0', '-hls_time', '10', '-hls_list_size', '0', '-f', 'hls', new_path])
    thumb_path = f"{base_dir}/thumbs/{vid}.png"
    subprocess.run(['ffmpeg', '-i', new_path, '-ss', '00:00:01', '-vframes', '1', thumb_path])

    base_url = os.environ.get("BASE_URL")
    requests.patch(f"")
    save_thumb_path = f"{base_url}/thumbs/{vid}.png"
    save_video_path = f"{base_url}/hls_output/{vid}/index.m3u8"
    
    # 상태 변경 요청 (READY)
    requests.patch(f"http://{SPRING_HOST}:{SPRING_PORT}/api/video/{vid}/status?status=READY",
                    json={"videoPath": f"{base_url}/hls_output/{vid}/index.m3u8", "thumbnailPath": f"{base_url}/thumbs/{vid}.png"},
                    headers=headers)
