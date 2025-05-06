from redis import Redis
import json, requests, subprocess

r = Redis(host='localhost', port=6379, db=0)

