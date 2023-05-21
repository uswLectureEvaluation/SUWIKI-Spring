INSERT INTO confirmation_token(id, user_idx, token, created_at, expires_at, confirmed_at)
VALUES (1, 1, "test1", now(), DATE_ADD(NOW(), INTERVAL 30 MINUTE), null);

INSERT INTO confirmation_token(id, user_idx, token, created_at, expires_at, confirmed_at)
VALUES (2, 2, "test1", now(), DATE_ADD(NOW(), INTERVAL 30 MINUTE), null);

INSERT INTO confirmation_token(id, user_idx, token, created_at, expires_at, confirmed_at)
VALUES (3, 3, "test1", now(), DATE_ADD(NOW(), INTERVAL 30 MINUTE), NOW());

INSERT INTO confirmation_token(id, user_idx, token, created_at, expires_at, confirmed_at)
VALUES (4, 4, "test1", now(), DATE_ADD(NOW(), INTERVAL 30 MINUTE), NOW());