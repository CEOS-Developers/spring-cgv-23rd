INSERT INTO cinemas (cinema_id, name, region, created_at, updated_at)
VALUES
    (1, 'CGV 홍대', 'SEOUL', NOW(), NOW());

INSERT INTO theaters (theater_id, cinema_id, name, type, max_row, max_col)
VALUES
    (1, 1, '1관', 'NORMAL', 'J', 20);

INSERT INTO movies (
    movie_id, title, running_time, sales_rate, release_date,
    movie_rating, genre, prologue, created_at, updated_at
)
VALUES
    (1, '테스트 영화', 120, 0.0, '2026-05-07', 'AGE_12', 'ACTION', '부하테스트용 영화 데이터', NOW(), NOW());

INSERT INTO screenings (
    screening_id, movie_id, theater_id, start_time, end_time,
    is_morning, created_at, updated_at
)
VALUES
    (1, 1, 1, '2026-05-08 14:00:00', '2026-05-08 16:00:00', false, NOW(), NOW());

INSERT INTO products (
    product_id, name, price, description, origin, ingredient,
    pickup_possible, category, created_at, updated_at
)
VALUES
    (1, '고소팝콘 M', 6000, '부하테스트용 팝콘', '국내산', '옥수수, 버터', true, 'POPCORN', NOW(), NOW()),
    (2, '콜라 M', 3000, '부하테스트용 음료', '국내산', '탄산음료', true, 'DRINK', NOW(), NOW()),
    (3, '팝콘 콤보', 9000, '부하테스트용 콤보', '국내산', '팝콘, 콜라', true, 'COMBO', NOW(), NOW());

INSERT INTO inventories (inventory_id, cinema_id, product_id, stock_quantity)
VALUES
    (1, 1, 1, 10000),
    (2, 1, 2, 10000),
    (3, 1, 3, 10000);