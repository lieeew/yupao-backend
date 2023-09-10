# 查询到每一个组的 创建者的用户信息
select u.*
from `team` t
         left join `user` u on t.userId = u.id;


# 查询到每一个组的成员的 id 的用户信息
select u.*
from yupi.`team` t
         left join yupi.`user_team` ut on t.id = ut.teamId
         left join yupi.`user` u on u.id = ut.userId
where t.id = 1;


SELECT id,
       name,
       description,
       maxNum,
       expireTime,
       userId,
       status,
       password,
       createTime,
       updateTime,
       isDelete
FROM team
WHERE isDelete = 0
  AND ((name LIKE '%学习%' OR description LIKE '%学习%'));

select * from team where expireTime is null or expireTime > now();
