SELECT
    s.name        AS student_name,
    s.age         AS student_age,
    f.name        AS faculty_name
FROM Student AS s
INNER JOIN Faculty AS f
    ON s.faculty_id = f.id;

SELECT
    s.name        AS student_name,
    s.age         AS student_age
FROM Student AS s
INNER JOIN Avatar AS a
    ON s.id = a.student_id;