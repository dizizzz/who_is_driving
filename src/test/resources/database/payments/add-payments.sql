INSERT INTO payments (id, status, type, rental_id, session_url, session_id, amount_to_pay)
VALUES
(1, 'PENDING', 'PAYMENT', 1, 'http://example.com/1', 'session1', 310.00),
(2, 'PAID', 'PAYMENT', 3, 'http://example.com/2', 'session2', 160.00),
(3, 'CANCELED', 'PAYMENT', 2, 'http://example.com/3', 'session3', 200.00);