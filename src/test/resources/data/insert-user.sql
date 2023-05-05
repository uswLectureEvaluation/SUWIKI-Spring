INSERT INTO user(
                 login_id, password, email,
                 restricted_count, restricted, role,
                 written_evaluation, written_exam, view_exam_count, point,
                 last_login, requested_quit_date, created_at, updated_at)
VALUES
    ('testuser01', 'password1234', 'testuser01@example.com',
     0, 0, 'USER',
     0, 0, 0, 100,
     now(), null, now(), now());

INSERT INTO user(
    login_id, password, email,
    restricted_count, restricted, role,
    written_evaluation, written_exam, view_exam_count, point,
    last_login, requested_quit_date, created_at, updated_at)
VALUES
    ('testuser02', 'password1234', 'testuser02@example.com',
     0, 0, 'USER',
     0, 0, 0, 100,
     now(), null, now(), now());








