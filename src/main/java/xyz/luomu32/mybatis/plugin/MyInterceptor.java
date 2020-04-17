package xyz.luomu32.mybatis.plugin;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class MyInterceptor implements Interceptor {
    /**
     * 乐观锁常量
     */
    @Deprecated
    public static final String MP_OPTLOCK_VERSION_ORIGINAL = "MP_OPTLOCK_VERSION_ORIGINAL";
    /**
     * 乐观锁常量
     */
    @Deprecated
    public static final String MP_OPTLOCK_VERSION_COLUMN = "MP_OPTLOCK_VERSION_COLUMN";
    /**
     * 乐观锁常量
     */
    @Deprecated
    public static final String MP_OPTLOCK_ET_ORIGINAL = "MP_OPTLOCK_ET_ORIGINAL";

    private static final String NAME_ENTITY = "et";
    private static final String NAME_ENTITY_WRAPPER = "ew";
    private static final String PARAM_UPDATE_METHOD_NAME = "update";

    private static final String DOT = ".";

    private final Map<Class<?>, EntityField> versionFieldCache = new ConcurrentHashMap<>();
    private final Map<Class<?>, List<EntityField>> entityFieldsCache = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        if (SqlCommandType.UPDATE != ms.getSqlCommandType()) {
            return invocation.proceed();
        }
        Object param = args[1];
        if (param instanceof Map) { //TODO，为什么是个Map，而不是特定的类？哪里做了处理？
            Map map = (Map) param;
            //updateById(et), update(et, wrapper);
            Object et = map.get(NAME_ENTITY);
            if (et != null) {
                // entity
                String methodId = ms.getId();
                String methodName = methodId.substring(methodId.lastIndexOf(DOT) + 1);
                Class<?> entityClass = et.getClass();
//                TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
                EntityField versionField = this.getVersionField(entityClass);
                if (versionField == null) {
                    return invocation.proceed();
                }
                Field field = versionField.getField();
                Object originalVersionVal = versionField.getField().get(et);
                if (originalVersionVal == null) {
                    return invocation.proceed();
                }
                Object updatedVersionVal = getUpdatedVersionVal(originalVersionVal);
                if (PARAM_UPDATE_METHOD_NAME.equals(methodName)) {
                    // update(entity, wrapper)
                    // mapper.update(updEntity, QueryWrapper<>(whereEntity);
//                    AbstractWrapper<?, ?, ?> ew = (AbstractWrapper<?, ?, ?>) map.get(NAME_ENTITY_WRAPPER);
//                    if (ew == null) {
//                        UpdateWrapper<?> uw = new UpdateWrapper<>();
//                        uw.eq(versionField.getColumnName(), originalVersionVal);
//                        map.put(NAME_ENTITY_WRAPPER, uw);
//                        field.set(et, updatedVersionVal);
//                    } else {
//                        ew.apply(versionField.getColumnName() + " = {0}", originalVersionVal);
//                        field.set(et, updatedVersionVal);
//                        //TODO: should remove version=oldval condition from aw; 0827 by k神
//                    }
                    return invocation.proceed();
                } else {
//                    List<EntityField> fields = entityFieldsCache.computeIfAbsent(entityClass, this::getFieldsFromClazz);
                    List<EntityField> fields = getFieldsFromClazz(entityClass);
                    Map<String, Object> entityMap = new HashMap<>(fields.size());
                    for (EntityField ef : fields) {
                        Field fd = ef.getField();
                        entityMap.put(fd.getName(), fd.get(et));
                    }
                    String versionColumnName = versionField.getColumnName();
                    //update to cache
                    versionField.setColumnName(versionColumnName);
                    entityMap.put(field.getName(), updatedVersionVal);
                    entityMap.put(MP_OPTLOCK_VERSION_ORIGINAL, originalVersionVal);
                    entityMap.put(MP_OPTLOCK_VERSION_COLUMN, versionColumnName);
                    entityMap.put(MP_OPTLOCK_ET_ORIGINAL, et);
                    map.put(NAME_ENTITY, entityMap);
                    Object resultObj = invocation.proceed();
                    if (resultObj instanceof Integer) {
                        Integer effRow = (Integer) resultObj;
                        if (updatedVersionVal != null && effRow != 0) {
                            //updated version value set to entity.
                            field.set(et, updatedVersionVal);
                        }
                    }
                    return resultObj;
                }
            }
        }
        return invocation.proceed();
    }

    /**
     * This method provides the control for version value.<BR>
     * Returned value type must be the same as original one.
     *
     * @param originalVersionVal ignore
     * @return updated version val
     */
    protected Object getUpdatedVersionVal(Object originalVersionVal) {
        Class<?> versionValClass = originalVersionVal.getClass();
        if (long.class.equals(versionValClass) || Long.class.equals(versionValClass)) {
            return ((long) originalVersionVal) + 1;
        } else if (int.class.equals(versionValClass) || Integer.class.equals(versionValClass)) {
            return ((int) originalVersionVal) + 1;
        } else if (Date.class.equals(versionValClass)) {
            return new Date();
        } else if (Timestamp.class.equals(versionValClass)) {
            return new Timestamp(System.currentTimeMillis());
        }
        //not supported type, return original val.
        return originalVersionVal;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {
        // to do nothing
    }

    private EntityField getVersionField(Class<?> parameterClass) {

        if (Object.class.equals(parameterClass))
            return null;

        for (Field field : parameterClass.getFields()) {
            if (field.isAnnotationPresent(Version.class)) {
                field.setAccessible(true);
                EntityField entityField = new EntityField(field, true, "version");
                return entityField;
            }
        }

        return this.getVersionField(parameterClass.getSuperclass());
//        return versionFieldCache.computeIfAbsent(parameterClass, mapping -> getVersionFieldRegular(parameterClass, tableInfo));
    }

    /**
     * 反射检查参数类是否启动乐观锁
     *
     * @param parameterClass 实体类
     * @param tableInfo      实体数据库反射信息
     * @return ignore
     */
//    private EntityField getVersionFieldRegular(Class<?> parameterClass, TableInfo tableInfo) {
//        return Object.class.equals(parameterClass) ? null : ReflectionKit.getFieldList(parameterClass).stream().filter(e -> e.isAnnotationPresent(Version.class)).map(field -> {
//            field.setAccessible(true);
//            return new EntityField(field, true, tableInfo.getFieldList().stream().filter(e -> field.getName().equals(e.getProperty())).map(TableFieldInfo::getColumn).findFirst().orElse(null));
//        }).findFirst().orElseGet(() -> this.getVersionFieldRegular(parameterClass.getSuperclass(), tableInfo));
//    }
    private List<EntityField> getFieldsFromClazz(Class<?> parameterClass) {
        List<EntityField> entityFields = new ArrayList<>();
        for (Field field : parameterClass.getFields()) {
            EntityField entityField = new EntityField(field, field.isAnnotationPresent(Version.class));
            entityFields.add(entityField);
        }
        return entityFields;
    }

    private class EntityField {

        private Field field;
        private boolean version;
        private String columnName;

        EntityField(Field field, boolean version) {
            this.field = field;
            this.version = version;
        }

        public EntityField(Field field, boolean version, String columnName) {
            this.field = field;
            this.version = version;
            this.columnName = columnName;
        }

        public Field getField() {
            return field;
        }

        public void setField(Field field) {
            this.field = field;
        }

        public boolean isVersion() {
            return version;
        }

        public void setVersion(boolean version) {
            this.version = version;
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }
    }
}
