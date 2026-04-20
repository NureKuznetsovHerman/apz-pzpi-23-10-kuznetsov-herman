#В.1 Реалізація механізму розбиття файлів на блоки (Chunking)
#Запит до ШІ: Напиши на мові Java приклад сервісу для Google Drive, який приймає вхідний потік даних (InputStream) великого файлу та розбиває його на блоки фіксованого розміру (наприклад, 8 МБ). Для кожного блоку потрібно розрахувати контрольну суму (SHA-256) для перевірки цілісності на стороні сервера. 

import java.io.*;
import java.security.MessageDigest;
import java.util.Base64;

public class FileChunkingProvider {
    private static final int CHUNK_SIZE = 8 * 1024 * 1024; #Порція 8МБ

    public void uploadByChunks(File file) throws Exception {
        byte[] buffer = new byte[CHUNK_SIZE];
        try (InputStream is = new FileInputStream(file)) {
            int bytesRead;
            int sequenceNumber = 0;
            
            while ((bytesRead = is.read(buffer)) != -1) {
                # Створення копії блоку, якщо він менший за CHUNK_SIZE (останній блок)
                byte[] chunk = (bytesRead == CHUNK_SIZE) ? buffer : 
                               java.util.Arrays.copyOf(buffer, bytesRead);
                
                String checksum = calculateSHA256(chunk);
               #Виклик методу відправки блоку на Block Server
                sendToBlockServer(sequenceNumber++, chunk, checksum);
            }
        }
    }

    private String calculateSHA256(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data);
        return Base64.getEncoder().encodeToString(hash);
    }
    
    private void sendToBlockServer(int seq, byte[] data, String hash) {
       # Логіка передачі даних через gRPC або HTTP до сховища Colossus
        System.out.println("Uploading chunk #" + seq + " with hash: " + hash);
    }
}

#В.2 Оптимізація доступу до метаданих через кешування (Redis)
#Запит до ШІ: Надай приклад коду на Python для мікросервісу метаданих. Використовуй Redis для кешування інформації про об'єкти файлової системи. Реалізуй стратегію 'Cache-Aside': спочатку перевіряємо кеш, при відсутності — йдемо в базу даних і оновлюємо кеш. 


import redis
import time
from my_database_driver import SpannerDB

# Ініціалізація підключення до Redis кластера
cache = redis.StrictRedis(host='cache-cluster.internal', port=6379, db=0)
db = SpannerDB()

def get_file_metadata(file_id):
    cache_key = f"file_meta:{file_id}"
    
    # 1. Спроба отримати дані з кешу
    start_time = time.time()
    cached_meta = cache.get(cache_key)
    
    if cached_meta:
        print(f"Cache HIT. Latency: {time.time() - start_time:.4f}s")
        return decode_json(cached_meta)
    
    # 2. Якщо в кеші порожньо - запит до основної БД
    print("Cache MISS. Fetching from Spanner...")
    metadata = db.execute_query(f"SELECT * FROM Files WHERE ID = '{file_id}'")
    
    # 3. Запис у кеш з терміном життя (TTL) 600 секунд
    if metadata:
        cache.setex(cache_key, 600, encode_json(metadata))
        
    return metadata


#В.3 Подієво-орієнтована синхронізація через WebSockets
#Запит до ШІ: Розроби архітектурний приклад на Node.js для Notification Service. Використовуй бібліотеку 'ws' для підтримки активних з'єднань з клієнтами. Реалізуй обробник подій, який отримує повідомлення з черги (Message Queue) про оновлення файлу та розсилає пуш-повідомлення відповідним користувачам.

    const WebSocket = require('ws');
const wss = new WebSocket.Server({ port: 8080 });

#Словник активних підключень: userId -> Set of WebSockets
const userSessions = new Map();

wss.on('connection', (ws, req) => {
    const userId = extractUserId(req); 
    if (!userSessions.has(userId)) userSessions.set(userId, new Set());
    userSessions.get(userId).add(ws);

    ws.on('close', () => {
        const sockets = userSessions.get(userId);
        if (sockets) {
            sockets.delete(ws);
            if (sockets.size === 0) userSessions.delete(userId);
        }
    });
});

# Функція, що викликається при надходженні події з Google Pub/Sub
function onFileChangedEvent(eventData) {
    const { userId, fileId, action } = eventData;
    
    const activeSockets = userSessions.get(userId);
    if (activeSockets) {
        const payload = JSON.stringify({ type: 'SYNC_REQUIRED', fileId, action });
        activeSockets.forEach(client => {
            if (client.readyState === WebSocket.OPEN) {
                client.send(payload);
            }
        });
    }
}