CACHE = {}
def cached(k, fn):
    if k not in CACHE:
        CACHE[k] = fn()
    return CACHE[k]

# 8450
