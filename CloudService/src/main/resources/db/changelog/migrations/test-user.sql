INSERT INTO users (username, password)
VALUES ('user',
        '$2a$10$LltLiiyT067mrSQ/mv1V.ueEKrceOTalxA2wJVUriwraUo2CUM.qW')
ON CONFLICT (username) DO NOTHING;