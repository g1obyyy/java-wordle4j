package ru.yandex.practicum;

import ru.yandex.practicum.exception.WordleGameException;
import ru.yandex.practicum.exception.WordleTechnicalException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

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
        try (PrintWriter logger = new PrintWriter(new FileWriter("wordly.log", true)); Scanner scanner = new Scanner(System.in)) {
            runApplication(scanner, logger);
        } catch (IOException e) {
            System.out.println("Ошибка запуска: невозможно инициализировать системный журнал.");
        }
    }

    private static void runApplication(final Scanner scanner, final PrintWriter logger) {
        try {
            WordleDictionaryLoader loader = WordleDictionaryLoader.of("words_ru.txt", logger);
            WordleDictionary dictionary = loader.load();

            final String answer = dictionary.getRandomWord();
            WordleGame game = new WordleGame(answer, dictionary, logger);

            printMenu();
            playGameLoop(game, scanner, logger);
        } catch (WordleTechnicalException e) {
            logger.println("Техническая ошибка: " + e.getMessage());
            if (e.getCause() != null) e.getCause().printStackTrace(logger);
        } catch (Exception e) {
            logger.println("Критическая ошибка: " + e.getMessage());
            e.printStackTrace(logger);
            System.out.println("Произошла системная ошибка! Игра прервана.");
        }
    }

    private static void playGameLoop(final WordleGame game, final Scanner scanner, final PrintWriter logger) {
        logger.println("=== Начало игры ===");

        while (!game.isOver()) {
            System.out.printf("Попытка %d/6. Введите слово (или введите Enter для подсказки): ", 7 - game.getAttempts());
            String guess = scanner.nextLine().trim();

            if (guess.isEmpty()) {
                guess = game.getHint();
                System.out.println("Подсказка использована: " + guess);
                logger.println("Пользователь запросил подсказку. Слово: " + guess);
            } else {
                logger.println("Слово пользователя: " + guess);
            }

            try {
                String feedback = game.makeMove(guess);
                System.out.println(feedback);
                logger.println("Результат попытки: " + feedback);
            } catch (WordleGameException e) {
                System.out.println("Игровая ошибка: " + e.getMessage());
                logger.println("Игровая ошибка: " + e.getMessage());
            }
        }

        if (game.isWin()) {
            System.out.println("\nОтлично! Вы угадали слово: " + game.getAnswer());
            logger.println("Игра окончена: Пользователь победил.");
        } else {
            System.out.println("\nИгра окончена! Верное слово было: " + game.getAnswer());
            logger.println("Игра окончена: Игрок проиграл. Верное слово: " + game.getAnswer());
        }
    }

    private static void printMenu() {
        System.out.println("====== Добро пожаловать в Wordle =======");
        System.out.println("=============== Правила =================");
        System.out.println("+ ОЗНАЧАЕТ точно попадание");
        System.out.println("^ ОЗНАЧАЕТ в слове есть такая буква");
        System.out.println("- ОЗНАЧАЕТ в слове нет такой буквы");
        System.out.println("============== Начнем игру =============");
    }
}
