package bot.utils;

import java.lang.reflect.Field;

public class Injector {

    public static void injectSystemEnvValue(Object instance) {
        Field[] fields = instance.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(SystemEnv.class)) {
                SystemEnv set = field.getAnnotation(SystemEnv.class);
                field.setAccessible(true); // should work on private fields
                try {
                    field.set(instance, System.getenv(set.value()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
