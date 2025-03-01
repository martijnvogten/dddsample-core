package se.citerus.dddsample.infrastructure.persistence.jpa;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.sql.DataSource;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.jdbc.ReturningWork;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import nl.pojoquery.DB;
import nl.pojoquery.DbContext;
import nl.pojoquery.DbContext.QuoteStyle;
import nl.pojoquery.PojoQuery;
import nl.pojoquery.SqlExpression;
import nl.pojoquery.pipeline.CustomizableQueryBuilder;
import nl.pojoquery.pipeline.QueryBuilder;
import nl.pojoquery.pipeline.SqlQuery;

public class CargoDatabase {

  @Autowired
  DataSource dataSource;

  @Autowired
  EntityManager entityManager;

  @PostConstruct
  private void configureDbContext() {
    entityManager.unwrap(Session.class).doWork((conn) -> {
      DbContext pojoqueryConfig = DbContext.getDefault();
      boolean isMysql = isMySQL(conn);
      pojoqueryConfig.setQuoteStyle(isMysql ? QuoteStyle.MYSQL : QuoteStyle.ANSI);
      pojoqueryConfig.setQuoteObjectNames(false);
    });
  }

  public <T> T doReturningWork(Function<Connection, T> w) {
    try {
      return (T) entityManager.unwrap(Session.class).doReturningWork(new ReturningWork<T>() {
        @Override
        public T execute(Connection connection) throws SQLException {
          return w.apply(connection);
        }
      });
    } catch (HibernateException hibernateException) {
      throw new RuntimeException(hibernateException);
    }
  }

  public void doWork(Consumer<Connection> w) {
    try {
      entityManager.unwrap(Session.class).doWork(new Work() {
        @Override
        public void execute(Connection connection) throws SQLException {
          w.accept(connection);
        }
      });
    } catch (HibernateException hibernateException) {
      throw new RuntimeException(hibernateException);
    }
  }

  public List<Map<String, Object>> queryRows(SqlExpression statement) {
    return doReturningWork(conn -> {
      return DB.queryRows(conn, statement);
    });
  }

  public class CargoQuery<T> extends SqlQuery<CargoQuery<T>> {
    private CustomizableQueryBuilder<CargoQuery<T>, T> queryBuilder;
    private Class<T> entityClz;

    public CargoQuery(DbContext dbContext, Class<T> entityClz) {
      super(dbContext);
      this.entityClz = entityClz;
      this.queryBuilder = QueryBuilder.from(this, entityClz);
    }

    public List<T> execute() {
      return queryBuilder.processRows(queryRows(this.toStatement()));
    }

    public T findById(long id) {
      CargoQuery<T> query = query(entityClz);
      query.getWheres().addAll(QueryBuilder.buildIdCondition(DbContext.getDefault(), entityClz, id));
      return returnSingleRow(query.execute());
    }

    private T returnSingleRow(List<T> resultList) {
        if (resultList.size() == 1) {
          return resultList.get(0);
      }
      if (resultList.size() > 1) {
          throw new RuntimeException("More than one result found in findById");
      }
      return null;
    }
  }

  public <T> CargoQuery<T> query(Class<T> entityClz) {
    return new CargoQuery<T>(DbContext.getDefault(), entityClz);
  }

  public void insert(Object entity) {
    doWork(conn -> PojoQuery.insert(conn, entity));
  }

  public void insert(String tableName, Map<String, Object> values) {
    doWork(conn -> DB.insert(conn, tableName, values));
  }

  public void update(Object entity) {
    doWork(conn -> PojoQuery.update(conn, entity));
  }

  public void update(SqlExpression sql) {
    doWork(conn -> DB.update(conn, sql));
  }

  private static boolean isMySQL(Connection conn) {
    try {
      return conn.getMetaData().getURL().startsWith("jdbc:mysql:");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public boolean isMySQL() {
    return doReturningWork(conn -> isMySQL(conn));
  }

}
