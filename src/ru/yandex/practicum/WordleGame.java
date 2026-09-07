package ru.yandex.practicum;

import ru.yandex.practicum.exception.RepeatWordException;
import ru.yandex.practicum.exception.WordNotFoundInDictionary;
import ru.yandex.practicum.exception.WordleGameException;

import java.io.PrintWriter;
import java.util.*;

public class WordleGame {

    private final String answer;
    private final WordleDictionary dictionary;
    private final PrintWriter logger;

    private final List<String> remainingCandidates;
    private final LinkedHashMap<String, String> history = new LinkedHashMap<>();

    private int attempts = 6;
    private boolean isWin = false;

    public WordleGame(final String answer, final WordleDictionary dictionary, final PrintWriter logger) {
        this.answer = answer;
        this.dictionary = dictionary;
        this.logger = logger;
        this.remainingCandidates = new ArrayList<>(dictionary.getWords());

        logger.println("Игра инициализирована. Загаданное слово: " + answer);
    }

    public final String makeMove(final String rawGuess) throws WordleGameException {
        Objects.requireNonNull(rawGuess);
        String guess = dictionary.normalizeString(rawGuess);

        if (guess.length() != 5 || !dictionary.contains(guess)) {
            throw new WordNotFoundInDictionary("Такого слова нет в словаре: " + guess);
        }
        if (history.containsKey(guess)) {
            throw new RepeatWordException("Be careful, the word has been used before: " + guess);
        }

        --attempts;
        String feedback = checkWord(guess, this.answer);
        history.put(guess, feedback);
        logger.println("Ход: " + guess + " | Результат: " + feedback + " | Попыток осталось: " + attempts);

        if ("+++++".equals(feedback)) {
            isWin = true;
        } else {
            // ИСПРАВЛЕНО: добавлен ! и правильный порядок (guess, candidate)
            remainingCandidates.removeIf(candidate -> !checkWord(guess, candidate).equals(feedback));
            logger.println("После фильтрации кандидатов осталось: " + remainingCandidates.size());
        }
        return feedback;
    }

    public final String getHint() throws WordleGameException {
        if (remainingCandidates.isEmpty()) {
            throw new WordleGameException("Не осталось слов для подсказки!");
        }
        return remainingCandidates.getFirst();
    }

    private String checkWord(final String guess, final String target) {
        char[] feedback = new char[5];
        Map<Character, Integer> targetCounts = new HashMap<>();

        for(int i = 0; i < 5; ++i) {
            char g = guess.charAt(i);
            char t = target.charAt(i);
            if (g == t) {
                feedback[i] = '+';
            } else {
                targetCounts.put(t, targetCounts.getOrDefault(t, 0) + 1);
            }
        }

        for(int i = 0; i < 5; ++i) {
            if (feedback[i] == '+') {
                continue;
            }

            char g = guess.charAt(i);
            int count = targetCounts.getOrDefault(g, 0);
            if (count > 0) {
                feedback[i] = '^';
                targetCounts.put(g, count - 1);
            } else {
                feedback[i] = '-';
            }
        }

        StringBuilder sb = new StringBuilder(5);
        sb.append(feedback);
        return sb.toString();
    }

    public boolean isWin() {
        return isWin;
    }

    public boolean isOver() {
        return attempts == 0 || isWin();
    }

    public int getAttempts() {
        return attempts;
    }

    public final String getAnswer() {
        return answer;
    }
}