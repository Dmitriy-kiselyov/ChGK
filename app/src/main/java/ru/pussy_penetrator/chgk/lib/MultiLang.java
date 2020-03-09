package ru.pussy_penetrator.chgk.lib;

public class MultiLang {
    private MultiLangObject lang;

    public MultiLang(String none, String one, String some, String many) {
        lang = new MultiLangObject(none, one, some, many);
    }

    public String translate(int number) {
        if (number == 0) {
            return lang.none;
        }
        if (number % 100 >= 5 && number % 100 <= 20) {
            return lang.many;
        }
        if (number % 10 == 1) {
            return lang.one;
        }
        if (number % 10 >= 2 && number % 10 <= 4) {
            return lang.some;
        }

        return lang.many;
    }

    static private class MultiLangObject {
        String none;
        String one;
        String some;
        String many;

        MultiLangObject(String none, String one, String some, String many) {
            this.none = none;
            this.one = one;
            this.some = some;
            this.many = many;
        }
    }
}
