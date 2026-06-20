import time
def retry(fn, n=3):
    for i in range(n):
        try: return fn()
        except: time.sleep(1)

# 1112
