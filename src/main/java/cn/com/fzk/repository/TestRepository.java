package cn.com.fzk.repository;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.com.fzk.schema.generated.Tables;
import cn.com.fzk.schema.generated.tables.daos.UserDao;
import cn.com.fzk.schema.generated.tables.pojos.User;
import cn.com.fzk.schema.generated.tables.records.UserRecord;


@Repository
public class TestRepository {
  @Autowired
  DSLContext dslContext;

  @Autowired
  Configuration configuration;

  public int create(User user) {
    UserRecord userRecord = dslContext.newRecord(Tables.USER, user);
    userRecord.store();

    return userRecord.getId();
  }

  public void update(User user) {
    new UserDao(configuration).update(user);
  }

  public User findOneById(Integer userId) {
    return new UserDao(configuration).findById(userId);
  }

  public Integer countUserByUserId(Integer userId) {
    return dslContext.selectCount().from(Tables.USER).where(Tables.USER.ID.eq(userId)).fetchOne(0,
        Integer.class);
  }
}
