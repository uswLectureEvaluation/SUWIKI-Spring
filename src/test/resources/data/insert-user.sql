INSERT INTO user(
                 login_id, password, email,
                 restricted_count, restricted, role,
                 written_evaluation, written_exam, view_exam_count, point,
                 last_login, requested_quit_date, created_at, updated_at)
VALUES
    ('user1', 'qwer1234!', 'user1@example.com',
     0, 0, 'ADMIN',
     0, 0, 0, 100,
     now(), null, now(), now());

INSERT INTO user(
    login_id, password, email,
    restricted_count, restricted, role,
    written_evaluation, written_exam, view_exam_count, point,
    last_login, requested_quit_date, created_at, updated_at)
VALUES
    ('user2', 'qwer1234!', 'user2@example.com',
     0, 0, 'USER',
     0, 0, 0, 100,
     now(), null, now(), now());








