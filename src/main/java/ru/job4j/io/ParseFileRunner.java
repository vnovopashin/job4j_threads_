package ru.job4j.io;

import java.io.File;

public class ParseFileRunner {
    public static void main(String[] args) {
        ParseFile parseFile = new ParseFile(new File("test.txt"));
        String str = parseFile.getContent(f -> true);
        String strWithPredicate = parseFile.getContent(f -> f < 0x80);
        System.out.println(str);
        System.out.println(strWithPredicate);

        SaveFile saveFile = new SaveFile(new File("write.txt"));
        saveFile.saveContent(str);
        saveFile.saveContent(strWithPredicate);
    }
}
