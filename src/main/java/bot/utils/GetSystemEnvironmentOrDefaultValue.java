package bot.utils;

import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.Map;

public class GetSystemEnvironmentOrDefaultValue
{
    private static Map<String, String> defaultValueMap = new HashMap<>();

    static
    {
        defaultValueMap.put("BOT_USER_ID", "567437271733633024");
    }

    public static String get(String key)
    {
        try
        {
            String value = System.getenv(key);
            if (value != null) return value;
            throw new InvalidKeyException();
        }
        catch (Exception e)
        {
            return defaultValueMap.get(key);
        }
    }
}
