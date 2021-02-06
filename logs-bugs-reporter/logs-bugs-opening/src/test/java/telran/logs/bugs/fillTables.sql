delete from bugs;
delete from artifacts;
delete from programmer;

insert into programmer (id, name, email) values(1, 'Moshe', 'moshe@mail.com');
insert into artifacts (artifact_id, programmer_id) values('bug1', 1);
