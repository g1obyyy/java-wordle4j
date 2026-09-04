package ru.yandex.practicum;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/*
в главном классе нам нужно:
    создать лог-файл (он должен передаваться во все классы)
    создать загрузчик словарей WordleDictionaryLoader
    загрузить словарь WordleDictionary с помощью класса WordleDictionaryLoader
    затем создать игру WordleGame и передать ей словарь
    вызвать игровой метод в котором в цикле опрашивать пользователя и передавать информацию в игру
    вывести состояние игры и конечный результат
 */
public class Wordle {

    public static void main(String[] args) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("wordly.log", true))) {
            WordleDictionaryLoader loader = WordleDictionaryLoader.of("words_ru.txt");
            WordleDictionary dictionary = loader.load();


        } catch (IOException e) {

        }
    }

}
