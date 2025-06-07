select * from student where student.age > 10 and student.age < 20;
select name from student;
select * from student as s where s.name LIKE '%o%';
select * from student as s where s.age < s.id;
select * from student as s order by age;