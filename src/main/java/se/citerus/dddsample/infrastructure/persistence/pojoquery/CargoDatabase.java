package se.citerus.dddsample.infrastructure.persistence.pojoquery;

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
import org.pojoquery.DB;
import org.pojoquery.DbContext;
import org.pojoquery.DbContext.Dialect;
import org.pojoquery.PojoQuery;
import org.pojoquery.SqlExpression;
import org.pojoquery.pipeline.CustomizableQueryBuilder;
import org.pojoquery.pipeline.QueryBuilder;
import org.pojoquery.pipeline.SqlQuery;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;

public class CargoDatabase {

  @Autowired
  DataSource dataSource;

  @Autowired
  EntityManager entityManager;

  private static final Map<String, DbContext.Dialect> DIALECTS = Map.of(
    "jdbc:hsqldb:", Dialect.HSQLDB,
    "jdbc:mysql:", Dialect.MYSQL,
    "jdbc:postgresql:", Dialect.POSTGRES);

  @PostConstruct
  private void configureDbContext() {
    entityManager.unwrap(Session.class).doWork((conn) -> {
      String url = conn.getMetaData().getURL();
      DIALECTS.forEach((urlPrefix, dialect) -> {
        if (url.startsWith(urlPrefix)) {
          DbContext dbContext = DbContext.forDialect(dialect);
          DbContext.setDefault(dbContext);
        }
      });
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
    Long generatedId = doReturningWork(conn -> PojoQuery.insert(conn, entity));
    // Set the generated ID back on the entity using reflection
    if (generatedId != null) {
      try {
        java.lang.reflect.Field idField = findIdField(entity.getClass());
        if (idField != null) {
          idField.setAccessible(true);
          idField.set(entity, generatedId);
        }
      } catch (Exception e) {
        throw new RuntimeException("Failed to set generated ID", e);
      }
    }
  }

  private java.lang.reflect.Field findIdField(Class<?> clazz) {
    for (java.lang.reflect.Field field : clazz.getDeclaredFields()) {
      if (field.isAnnotationPresent(jakarta.persistence.Id.class)) {
        return field;
      }
    }
    // Check superclass
    if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
      return findIdField(clazz.getSuperclass());
    }
    return null;
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

  public boolean isMySQL() {
    return DbContext.getDefault().getDialect() == Dialect.MYSQL;
  }
}
