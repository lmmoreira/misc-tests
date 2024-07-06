# misc-tests

This repository contains a collection of miscellaneous tests that I have written. They are meant to be used as a reference for future projects.

## Table of Contents

memcached: 
- Docker Compose: Distributed servers of Memcached. Ports: 11211, 11212
- Default is standalone configuration on MemcachedCacheConfig::DefaultAddressProvider("localhost:11211")
- In order to set-up distributed servers, use MemcachedCacheConfig::DefaultAddressProvider("localhost:11211, localhost:11212")
- Distribution is going to be based on the key hash value, so the same key will always be stored in the same server if ones goes down, automatically redirect for other.

caffeine: 
- inmemory cache manager, and fallback for memcached and redis if they are off

redis: 
- Docker Compose: Standalone Redis server. Port: 6379
- Default is standalone configuration on RedisCacheConfig