INSERT INTO user(
                 login_id, password, email,
                 restricted_count, restricted, role,
                 written_evaluation, written_exam, view_exam_count, point,
                 last_login, requested_quit_date, created_at, updated_at)
VALUES
    ('user1', '$2a$10$zkHb4uI39RHMdn4OdGRAg.1vQwDl1K4h3DCkSsiWN.vnhBw2Ux7Ge', 'user1@suwon.ac.kr',
     0, 0, 'ADMIN',
     0, 0, 0, 100,
     now(), null, now(), now());

INSERT INTO user(
    login_id, password, email,
    restricted_count, restricted, role,
    written_evaluation, written_exam, view_exam_count, point,
    last_login, requested_quit_date, created_at, updated_at)
VALUES
    ('user2', '$2a$10$zkHb4uI39RHMdn4OdGRAg.1vQwDl1K4h3DCkSsiWN.vnhBw2Ux7Ge', 'user2@suwon.ac.kr',
     0, 0, 'USER',
     0, 0, 0, 100,
     now(), null, now(), now());

INSERT INTO user(
    login_id, password, email,
    restricted_count, restricted, role,
    written_evaluation, written_exam, view_exam_count, point,
    last_login, requested_quit_date, created_at, updated_at)
VALUES
    ('user3', '$2a$10$zkHb4uI39RHMdn4OdGRAg.1vQwDl1K4h3DCkSsiWN.vnhBw2Ux7Ge', 'user3@suwon.ac.kr',
     0, 0, 'USER',
     0, 0, 0, 100,
     now(), null, now(), now());

INSERT INTO user(
    login_id, password, email,
    restricted_count, restricted, role,
    written_evaluation, written_exam, view_exam_count, point,
    last_login, requested_quit_date, created_at, updated_at)
VALUES
    ('user4', '$2a$10$zkHb4uI39RHMdn4OdGRAg.1vQwDl1K4h3DCkSsiWN.vnhBw2Ux7Ge', 'user4@suwon.ac.kr',
     0, 0, 'USER',
     0, 0, 0, 100,
     now(), null, now(), now());