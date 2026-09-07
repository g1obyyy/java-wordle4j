package ru.yandex.practicum;

import java.util.*;

/*
в этом классе хранится словарь и состояние игры
    текущий шаг
    всё что пользователь вводил
    правильный ответ

в этом классе нужны методы, которые
    проанализируют совпадение слова с ответом
    предложат слово-подсказку с учётом всего, что вводил пользователь ранее

не забудьте про специальные типы исключений для игровых и неигровых ошибок
 */
public class WordleGame {

    private final String answer;
    private final WordleDictionary dictionary;

    private final List<String> remainingCandidates;
    private final LinkedHashMap<String, String> history = new LinkedHashMap<>();

    private int attempts = 6;
    private boolean isWin = false;

    public WordleGame(final String answer, final WordleDictionary dictionary) {
        this.answer = answer;
        this.dictionary = dictionary;
        this.remainingCandidates = new ArrayList<>(dictionary.getWords());
    }

    public void makeMove(final String guess) {
        Objects.requireNonNull(guess);

        --attempts;
        String feedback = checkWord(guess);
        history.put(guess, feedback);

        if ("+++++".equals(feedback)) {
            isWin = true;
        } else {

        }
        return feedback;
    }

    public final String getHint() {

    }

    private String checkWord(final String guess) {
        Objects.requireNonNull(guess);

        char[] feedback = new char[5];
        Map<Character, Integer> targetCounts = new HashMap<>();

        for(int i = 0; i < 5; ++i) {
            char g = guess.charAt(i);
            char c = answer.charAt(i);
            if (g == c) {
                feedback[i] = '+';
            } else {
                targetCounts.put(c, targetCounts.getOrDefault(c, 0) + 1);
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
                targetCounts.put(g, --count);
            } else {
                feedback[i] = '-';
            }
        }
        return new String(feedback);
    }
}
