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
        logger.println("=== Starting game ===");

        while (!game.isOver()) {
            System.out.printf("Round %d/6. Enter your word (or press Enter for hint): ", 7 - game.getAttempts());
            String guess = scanner.nextLine().trim();

            if (guess.isEmpty()) {
                guess = game.getHint();
                System.out.println("Auto-move with hint: " + guess);
                logger.println("Player wants the hint. Word picked: " + guess);
            } else {
                logger.println("Player's word: " + guess);
            }

            try {
                String feedback = game.makeMove(guess);
                System.out.println(feedback);
                logger.println("Round result: " + feedback);
            } catch (WordleGameException e) {
                System.out.println("Gaming error: " + e.getMessage());
                logger.println("Gaming error: " + e.getMessage());
            }
        }

        if (game.isWin()) {
            System.out.println("\nWell done! You guessed the word: " + game.getAnswer());
            logger.println("Game finished: Player has won.");
        } else {
            System.out.println("\nGame over! The correct word was: " + game.getAnswer());
            logger.println("Game finished: Player lost. Secret word: " + game.getAnswer());
        }
    }

    private static void printMenu() {
        System.out.println("======== Welcome to Wordle Game ========");
        System.out.println("=============== Rules ==================");
        System.out.println("+ MEANS u got the char right");
        System.out.println("^ MEANS u got the char, but not on the right place");
        System.out.println("- MEANS u missed the char at all");
        System.out.println("============== Let's start =============");
    }
}
