package com.oceancode.cloud.test.mock;

import com.oceancode.cloud.api.TypeEnum;
import com.oceancode.cloud.common.util.SessionUtil;

import java.math.BigDecimal;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public class MockData {
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    public static Integer mockNormalInteger(int min, int max) {
        return RANDOM.nextInt(max - min) + min;
    }

    public static Integer mockInteger(int min, int max) {
        int number = RANDOM.nextInt() % 6;
        if (number == 0) {
            return min > Integer.MIN_VALUE ? min - 1 : Integer.MIN_VALUE;
        } else if (number == 1) {
            return min;
        } else if (number == 2) {
            return max;
        } else if (number == 3) {
            return max < Integer.MAX_VALUE ? max + 1 : Integer.MAX_VALUE;
        } else if (number == 5) {
            return null;
        }
        if (number == 4) {
            if (min < 0 && max > 0) {
                return 0;
            }
        }
        return RANDOM.nextInt(max - min) + min;
    }

    public static Integer mockInteger() {
        int number = RANDOM.nextInt() % 7;
        if (number == 0) {
            Integer value = mockInteger(1000, Integer.MAX_VALUE);
            return value == null ? number : -value;
        } else if (number == 1) {
            return mockInteger(1000, Integer.MAX_VALUE);
        } else if (number == 2) {
            return 0;
        } else if (number == 3 || number == 4 || number == 5 || number == 6) {
            return mockInteger(-1000, 1000);
        }
        return null;
    }

    public static Long mockLong(long min, long max) {
        int number = RANDOM.nextInt() % 6;
        if (number == 0) {
            return min > Long.MIN_VALUE ? min - 1 : Long.MIN_VALUE;
        } else if (number == 1) {
            return min;
        } else if (number == 2) {
            return max;
        } else if (number == 3) {
            return max < Long.MAX_VALUE ? max + 1 : Long.MAX_VALUE;
        } else if (number == 5) {
            return null;
        }
        if (number == 4) {
            if (min < 0 && max > 0) {
                return 0L;
            }
        }
        return RANDOM.nextLong(max - min) + min;
    }

    public static Long mockLong() {
        int number = RANDOM.nextInt() % 7;
        if (number == 0) {
            Long value = mockLong(1000, Long.MAX_VALUE);
            return value == null ? number : -value;
        } else if (number == 1) {
            return mockLong(1000, Long.MAX_VALUE);
        } else if (number == 2) {
            return 0L;
        } else if (number == 3 || number == 4 || number == 5 || number == 6) {
            return mockLong(-1000, 1000);
        }
        return null;
    }


    public static String mockSimpleString() {
        List<String> STR_POOLS = Arrays.asList(
                "  ", "",
                "0123456789",
                "abcdefghijklmnopqrstuvwxyz", "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
                "!@#$%^&*()_+-=\\|[]{}:;\"'；：‘“、？。》，《<,>.?/",
                "🙂",
                null
        );
        int index = mockNormalInteger(0, STR_POOLS.size());
        String str = STR_POOLS.get(index);
        if (str == null || str.length() == 0) {
            return str;
        }
        int index1 = mockNormalInteger(0, str.length());
        int index2 = mockNormalInteger(0, str.length());
        str = str.substring(Math.min(index1, index2), Math.max(index1, index2));
        return str;
    }

    public static String mockString(int min, int max) {
        int number = mockNormalInteger(0, 1000);
        int num = number % 3;
        if (num == 1) {
            return null;
        } else if (num == 2) {
            return mockSimpleString();
        }
        StringBuffer buffer = new StringBuffer();
        int len = mockNormalInteger(min, max);
        for (int i = 0; i < len; i++) {
            buffer.append(mockSimpleString());
        }
        return buffer.toString();
    }

    public static String mockString() {
        int number = mockNormalInteger(0, 1000);
        int num = number % 3;
        if (num == 1) {
            return null;
        } else if (num == 2) {
            return mockSimpleString();
        }
        StringBuffer buffer = new StringBuffer();
        int len = mockNormalInteger(5, 1000);
        for (int i = 0; i < len; i++) {
            buffer.append(mockSimpleString());
        }
        return buffer.toString();
    }

    public static <T extends TypeEnum> T mockTypeEnum(Class<T> typeClass) {
        TypeEnum[] values = typeClass.getEnumConstants();
        if (values == null || values.length == 0) {
            return null;
        }
        int num = mockNormalInteger(0, 1000) % 2;
        if (num == 0) {
            return null;
        }
        return (T) values[mockNormalInteger(0, values.length)];
    }

    public static Boolean mockBoolean() {
        if (RANDOM.nextBoolean()) {
            return null;
        }
        return RANDOM.nextBoolean();
    }

    public static Boolean mockNormalBoolean() {
        return RANDOM.nextBoolean();
    }

    public static List<String> mockStringList() {
        int length = mockNormalInteger(0, 1000);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            list.add(mockSimpleString());
        }
        return list;
    }

    public static <T> List<T> mockNormalList(int length, Supplier<T> supplier) {
        List<T> list = new ArrayList<>();
        int len = mockNormalInteger(0, length);
        for (int i = 0; i < len; i++) {
            list.add(supplier.get());
        }
        return list;
    }

    public static void mockSession() {
        int num = mockNormalInteger(0, 10000) % 3;
        if (num == 0) {
            SessionUtil.remove();
            return;
        }
        Long userId = mockLong();
        Long projectId = mockLong();
        Long tenantId = mockLong();
        if (Objects.nonNull(userId)) {
            SessionUtil.setUserId(userId);
        }
        if (Objects.nonNull(projectId)) {
            SessionUtil.setProjectId(projectId);
        }
        if (Objects.nonNull(tenantId)) {
            SessionUtil.setTenantId(tenantId);
        }
    }

    public static String mockText() {
        return mockString();
    }

    public static Timestamp mockTimestamp() {
        try {
            return new Timestamp(mockLong());
        } catch (Exception e) {
            return null;
        }
    }

    public static Date mockDate() {
        try {
            return new Date(mockLong());
        } catch (Exception e) {
            return null;
        }
    }

    public static Map<String, Object> mockMap() {
        Map<String, Object> map = new HashMap<>();
        int num = mockNormalInteger(0, 1000) % 5;
        if (num == 0) {
            return null;
        }
        int length = 10;
        if (num == 1 || num == 2 || num == 3) {
            length = 20;
        } else {
            length = num;
        }
        for (int i = 0; i < length; i++) {
            Object value = mockString();

            if (num == 1) {
                value = mockInteger();
            } else if (num == 2) {
                value = mockLong();
            } else if (num == 3) {
                value = mockTimestamp();
            } else if (num == 4) {
                value = mockBoolean();
            } else if (num == 5) {
                value = null;
            }

            map.put(mockString(), value);
        }
        return map;
    }

    public static BigDecimal mockBigDecimal() {
        int num = mockNormalInteger(0, 1000) % 5;
        if (num == 0) {
            return null;
        }

        try {
            return new BigDecimal(mockLong() + "." + mockLong());
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> Set<T> mockSet(Class<T> typeClass) {
        int num = mockNormalInteger(0, 1000) % 5;
        if (num == 0) {
            return null;
        }
        int size = mockNormalInteger(0, 20);
        Set<T> list = new HashSet<>(20);
        if (size == 0 || num == 1) {
            return list;
        }

        for (int i = 0; i < size; i++) {
            try {
                T t = typeClass.newInstance();
                list.add(mockBean(t));
            } catch (Exception e) {
                continue;
            }
        }
        return list;
    }

    public static <T> List<T> mockList(Class<T> typeClass) {
        int num = mockNormalInteger(0, 1000) % 5;
        if (num == 0) {
            return null;
        }
        int size = mockNormalInteger(0, 20);
        List<T> list = new ArrayList<>(20);
        if (size == 0 || num == 1) {
            return list;
        }

        for (int i = 0; i < size; i++) {
            try {
                T t = typeClass.newInstance();
                list.add(mockBean(t));
            } catch (Exception e) {
                continue;
            }
        }
        return list;
    }

    public static <T> T mockBean(Class<T> typeClass) {
        try {
            return mockBean(typeClass.newInstance());
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T mockBean(T instance) {
        Class<T> typeClass = (Class<T>) instance.getClass();
        Field[] fields = typeClass.getDeclaredFields();
        for (Field field : fields) {
            String setterName = "set" + field.getName().substring(0, 1).toUpperCase(Locale.ROOT) + (field.getName().length() > 1 ? field.getName().substring(1) : "");
            try {
                Method method = typeClass.getDeclaredMethod(setterName, new Class[]{field.getType()});
                Object value = null;
                if (String.class.equals(field.getType())) {
                    value = mockString();
                } else if (Long.class.equals(field.getType())) {
                    value = mockLong();
                } else if (Integer.class.equals(field.getType())) {
                    value = mockInteger();
                } else if (BigDecimal.class.equals(field.getType())) {
                    value = mockBigDecimal();
                } else if (Boolean.class.equals(field.getType())) {
                    value = mockBoolean();
                }

                method.invoke(instance, value);
            } catch (Exception e) {

            }
        }

        return instance;
    }

    public static Object mockObject() {
        int num = mockNormalInteger(0, 1000) % 11;
        if (num == 0) {
            return mockString();
        } else if (num == 1) {
            return mockLong();
        } else if (num == 2) {
            return mockInteger();
        } else if (num == 3) {
            return mockBoolean();
        } else if (num == 4) {
            return mockInteger();
        } else if (num == 5) {
            return mockBigDecimal();
        } else if (num == 6) {
            return mockStringList();
        } else if (num == 7) {
            return mockMap();
        } else if (num == 8) {
            return mockDate();
        } else if (num == 9) {
            return mockTimestamp();
        } else if (num == 10) {
            return mockBean(MockUser.class);
        }

        return mockText();
    }

    public static String mockNormalString(int min, int max) {
        int num = mockNormalInteger(min, max);
        return "a" + num;
    }

    public static Long mockNormalLong(long min, long max) {
        return mockLong(min, max);
    }

    public static <T extends TypeEnum<?>> T mockNormalTypeEnum(Class<T> typeClass) {
        TypeEnum[] values = typeClass.getEnumConstants();
        if (values == null || values.length == 0) {
            return null;
        }
        return (T) values[mockNormalInteger(0, values.length)];
    }

    public static List<String> mockNormalStringList(int size) {
        List<String> list = new ArrayList<>();
        if (size == 0) {
            return list;
        }
        list.add("a");
        return list;
    }

    public static Map<String, Object> mockNormalMap(int size) {
        Map<String, Object> map = new HashMap<>();
        if (size == 0) {
            return map;
        }
        map.put("a", "a");
        return map;
    }

    public static <T> T mockNormalBean(Class<T> typeClass) {
        return mockBean(typeClass);
    }

    public static Timestamp mockNormalTimestamp() {
        return mockTimestamp();
    }

    public static Date mockNormalDate() {
        return mockDate();
    }

    public static BigDecimal mockNormalBigDecimal() {
        return mockBigDecimal();
    }


    public class MockUser {
        private Long id;

        private String username;

        private String password;

        private String avatar;

        private String email;

        private Integer age;

        private Integer sex;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public Integer getSex() {
            return sex;
        }

        public void setSex(Integer sex) {
            this.sex = sex;
        }
    }

    public static Class mockClass() {
        List<Class> classList = Arrays.asList(String.class, HashMap.class, List.class, Map.class, MockUser.class, ArrayList.class, Void.class,
                Number.class, BigDecimal.class, Long.class, Integer.class, Date.class, Timestamp.class);
        int num = mockNormalInteger(0, 1000);
        if (num == 0) {
            return null;
        }

        return classList.get(num % classList.size());
    }
}
