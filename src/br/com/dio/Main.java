package br.com.dio;

import br.com.dio.model.Board;
import br.com.dio.model.Space;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;

import static br.com.dio.util.BoardTemplate.BOARD_TEMPLATE;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;

public class Main {

	private static final String[][] DEFAULT_BOARD = {
		    {"5,true", "3,true", "0,false", "0,false", "7,true", "0,false", "0,false", "0,false", "0,false"},
		    {"6,true", "0,false", "0,false", "1,true", "9,true", "5,true", "0,false", "0,false", "0,false"},
		    {"0,false", "9,true", "8,true", "0,false", "0,false", "0,false", "0,false", "6,true", "0,false"},
		    {"8,true", "0,false", "0,false", "0,false", "6,true", "0,false", "0,false", "0,false", "3,true"},
		    {"4,true", "0,false", "0,false", "8,true", "0,false", "3,true", "0,false", "0,false", "1,true"},
		    {"7,true", "0,false", "0,false", "0,false", "2,true", "0,false", "0,false", "0,false", "6,true"},
		    {"0,false", "6,true", "0,false", "0,false", "0,false", "0,false", "2,true", "8,true", "0,false"},
		    {"0,false", "0,false", "0,false", "4,true", "1,true", "9,true", "0,false", "0,false", "5,true"},
		    {"0,false", "0,false", "0,false", "0,false", "8,true", "0,false", "0,false", "7,true", "9,true"}
		};

	
    private final static Scanner scanner = new Scanner(System.in);

    private static Board board;

    private final static int BOARD_LIMIT = 9;

    public static void main(String[] args) {
        
    	final Map<String, String> positions;

    	if (args.length == 0) {
    	    System.out.println("Nenhum tabuleiro informado. Carregando Sudoku padrão...");
    	    positions = new java.util.HashMap<>();
    	    for (int row = 0; row < BOARD_LIMIT; row++) {
    	        for (int col = 0; col < BOARD_LIMIT; col++) {
    	            positions.put("%s,%s".formatted(row, col), DEFAULT_BOARD[row][col]);
    	        }
    	    }
    	} else {
    	    positions = Stream.of(args)
    	            .collect(toMap(
    	                    k -> k.split(";")[0],
    	                    v -> v.split(";")[1]
    	            ));
    	}


        var option = -1;
        while (true) {
            System.out.println("\n========== MENU ==========");
            System.out.println("1 - Iniciar um novo Jogo");
            System.out.println("2 - Colocar um novo número");
            System.out.println("3 - Remover um número");
            System.out.println("4 - Visualizar jogo atual");
            System.out.println("5 - Verificar status do jogo");
            System.out.println("6 - Limpar jogo");
            System.out.println("7 - Finalizar jogo");
            System.out.println("8 - Sair");
            System.out.println("==========================");

            option = scanner.nextInt();

            switch (option) {
                case 1 -> startGame(positions);
                case 2 -> inputNumber();
                case 3 -> removeNumber();
                case 4 -> showCurrentGame();
                case 5 -> showGameStatus();
                case 6 -> clearGame();
                case 7 -> finishGame();
                case 8 -> {
                    System.out.println("Saindo do jogo... até mais!");
                    System.exit(0);
                }
                default -> System.out.println("Opção inválida, selecione uma das opções do menu");
            }
        }
    }

    private static void startGame(final Map<String, String> positions) {
        if (nonNull(board)) {
            System.out.println("O jogo já foi iniciado!");
            return;
        }

        List<List<Space>> spaces = new ArrayList<>();
        for (int row = 0; row < BOARD_LIMIT; row++) {
            spaces.add(new ArrayList<>());
            for (int col = 0; col < BOARD_LIMIT; col++) {
                var positionConfig = positions.get("%s,%s".formatted(row, col));
                var expected = Integer.parseInt(positionConfig.split(",")[0]);
                var fixed = Boolean.parseBoolean(positionConfig.split(",")[1]);
                var currentSpace = new Space(expected, fixed);
                spaces.get(row).add(currentSpace);
            }
        }

        board = new Board(spaces);
        System.out.println("O jogo foi iniciado com sucesso!");
    }

    private static void inputNumber() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado\n");
            return;
        }

        System.out.println("Informe a LINHA que em que o número será inserido (0-8):");
        var row = runUntilGetValidNumber(0, 8);
        System.out.println("Informe a COLUNA que em que o número será inserido (0-8):");
        var col = runUntilGetValidNumber(0, 8);
        System.out.printf("Informe o número que vai entrar na posição [%s,%s]\n", row, col);
        var value = runUntilGetValidNumber(1, 9);

        if (!board.changeValue(row, col, value)) {
            System.out.printf("A posição [%s,%s] tem um valor fixo e não pode ser alterada!\n", row, col);
        } else {
            System.out.println("Número inserido com sucesso!");
        }
    }

    private static void removeNumber() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado\n");
            return;
        }

        System.out.println("Informe a LINHA da posição a ser removida (0-8):");
        var row = runUntilGetValidNumber(0, 8);
        System.out.println("Informe a COLUNA da posição a ser removida (0-8):");
        var col = runUntilGetValidNumber(0, 8);

        if (!board.clearValue(row, col)) {
            System.out.printf("A posição [%s,%s] é fixa e não pode ser removida\n", row, col);
        } else {
            System.out.println("Número removido com sucesso!");
        }
    }

    private static void showCurrentGame() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado\n");
            return;
        }

        var args = new Object[81];
        var argPos = 0;

        for (int row = 0; row < BOARD_LIMIT; row++) {
            for (int col = 0; col < BOARD_LIMIT; col++) {
                var val = board.getSpaces().get(row).get(col).getActual();
                args[argPos++] = " " + (isNull(val) ? " " : val);
            }
        }

        System.out.println("Seu jogo se encontra da seguinte forma:");
        System.out.printf((BOARD_TEMPLATE) + "\n", args);
    }

    private static void showGameStatus() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado\n");
            return;
        }

        System.out.printf("O jogo atualmente se encontra no status: %s\n", board.getStatus().getLabel());
        if (board.hasErrors()) {
            System.out.println("O jogo contém erros");
        } else {
            System.out.println("O jogo não contém erros");
        }
    }

    private static void clearGame() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado\n");
            return;
        }

        System.out.println("Tem certeza que deseja limpar seu jogo e perder todo seu progresso?");
        var confirm = scanner.next();
        while (!confirm.equalsIgnoreCase("sim") && !confirm.equalsIgnoreCase("não")) {
            System.out.println("Informe 'sim' ou 'não'");
            confirm = scanner.next();
        }

        if (confirm.equalsIgnoreCase("sim")) {
            board.reset();
            System.out.println("O jogo foi limpo!");
        } else {
            System.out.println("Ação cancelada.");
        }
    }

    private static void finishGame() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado\n");
            return;
        }

        if (board.gameIsFinished()) {
            System.out.println("Parabéns você concluiu o jogo!");
            showCurrentGame();
            board = null;
        } else if (board.hasErrors()) {
            System.out.println("Seu jogo contém erros, verifique o tabuleiro e ajuste!");
        } else {
            System.out.println("Você ainda precisa preencher algum espaço.");
        }
    }

    private static int runUntilGetValidNumber(final int min, final int max) {
        var current = scanner.nextInt();
        while (current < min || current > max) {
            System.out.printf("Informe um número entre %s e %s\n", min, max);
            current = scanner.nextInt();
        }
        return current;
    }
}
