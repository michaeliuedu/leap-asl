package gg.bear.helper;

public class Logger {
    private static String PREFIX = "[INTERPRETER]: ";

    public static void WARN(String... args){
        LOG(String.format("[WARN]: %s", BuildEndlineFormatting(args)));
    }

    public static void ERROR(String... args){
        LOG(String.format("[ERR]: %s", BuildEndlineFormatting(args)));
    }

    public static void LOG(String... args){
        System.out.println(BuildEndlineFormatting(args));
    }

    private static String BuildEndlineFormatting(String... args){
        StringBuilder ret = new StringBuilder(PREFIX);
        for(String s : args)
            ret.append(String.format(args.length > 1 ? "%s\n" : "%s", s));
        return String.valueOf(ret);
    }
}
