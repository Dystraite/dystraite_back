package com.ynov.dystraite.services.maximots;

import com.ynov.dystraite.entities.Users;
import com.ynov.dystraite.entities.maximots.Grid;
import com.ynov.dystraite.enums.maximots.Direction;
import com.ynov.dystraite.enums.maximots.Sens;
import com.ynov.dystraite.models.maximots.SortieGetGrid;
import com.ynov.dystraite.repositories.maximots.GridRepository;
import com.ynov.dystraite.services.UsersService;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
public class GridService {

    @Autowired
    GridRepository gridRepository;

    @Autowired
    UsersService usersService;

    public void populate(Grid grid){
        if (grid.getLibelle() != null && grid.getLibelle().trim().length() > 0) {
            gridRepository.save(grid);
        }
    }

    private final Random rand = new Random();
    int boardSize = 10;
    char[][] boardArray = new char[boardSize][boardSize];

    public List<SortieGetGrid> createBoard(int difficulty, Authentication authentication) throws NoSuchAlgorithmException {

        Users user = usersService.getById(authentication.getName());

        List<SortieGetGrid> sortieGetGridList = new ArrayList<>();

        final MessageDigest digest = MessageDigest.getInstance("SHA-256");

        Set<Grid> gridList = gridRepository.getByDifficulty(difficulty);
        for (Grid grid : gridList) {

            boardArray = new char[boardSize][boardSize];
            List<String> wordsInsertedHash = new ArrayList<>();
            List<String> wordList = grid.getWords();

            for (String word : wordList) {

                if (word.length() < boardSize) {
                    Direction direction = Direction.getRandom(); //normal - reverse
                    Sens sens = Sens.getRandom(); //horiz - vert - diag_b_d - diag_h_d

                    String mot = word.toUpperCase();
                    if (direction.equals(Direction.Inverse)) {
                        mot = new StringBuilder(mot).reverse().toString();
                    }
                    boolean isWordInsertedSuccessfully = insertWord(sens, mot);

                    if (isWordInsertedSuccessfully) {
                        final byte[] hashbytes = digest.digest(word.toUpperCase().getBytes(StandardCharsets.UTF_8));
                        wordsInsertedHash.add(bytesToHex(hashbytes));
                    }
                }
            }

            //affichage du plateau
            System.out.println();
            for (int i = 0; i < boardArray.length; i++) {
                for (int j = 0; j < boardArray[i].length; j++) {
                    if (boardArray[i][j] != '\0') {
                        System.out.print(boardArray[i][j] + " ");
                    } else {
                        System.out.print(". ");
                    }
                }
                System.out.println();
            }

            ArrayList<Character> board = new ArrayList<>();
            for (int i = 0; i < boardArray.length; i++) {
                for (int j = 0; j < boardArray[i].length; j++) {
                    if (boardArray[i][j] == '\0') {
                        board.add((char) (rand.nextInt(26) + 'a'));
                    } else {
                        board.add(boardArray[i][j]);
                    }
                }
            }

            sortieGetGridList.add(new SortieGetGrid(board, wordsInsertedHash));
        }

        return sortieGetGridList;
    }

    private boolean insertWord(Sens sens, String word){

        int x = -1;
        int y = -1;
        boolean canInsertInBord = true;
        int nbFetch = 0;

        boolean isInsertWordSuccessfully = this.tryCrossWords(word);
        if (isInsertWordSuccessfully){
            return true;
        }else {
            do{
                if (sens.equals(Sens.Horizontal)) {
                    x = rand.nextInt(boardArray.length - word.length());
                    y = rand.nextInt(boardArray.length);
                    int nextX = x;
                    int nextY = y;
                    for (char letter : word.toCharArray()) {
                        if (nextX >= boardArray.length || nextX < 0 || (boardArray[nextX][nextY] != '\0' && boardArray[nextX][nextY] != letter)) {
                            canInsertInBord = false;
                            break;
                        }
                        nextX++;
                    }
                }else if (sens.equals(Sens.Vertical)) {
                    x = rand.nextInt(boardArray.length);
                    y = rand.nextInt(boardArray.length - word.length());
                    int nextX = x;
                    int nextY = y;
                    for (char letter : word.toCharArray()) {
                        if (nextY >= boardArray.length || nextY < 0 || (boardArray[nextX][nextY] != '\0' && boardArray[nextX][nextY] != letter)) {
                            canInsertInBord = false;
                            break;
                        }
                        nextY++;
                    }
                } else if (sens.equals(Sens.Diagonale_BasGauche_HautDroite)) { //posX--; posY--;
                    int max = boardArray.length;
                    int min = word.length() - 1;
                    x = rand.nextInt(max - min) + min;
                    y = rand.nextInt(max - min + 1);
                    int nextX = x;
                    int nextY = y;
                    for (char letter : word.toCharArray()) {
                        if (nextX >= boardArray.length || nextX < 0 || nextY >= boardArray.length || nextY < 0 || (boardArray[nextX][nextY] != '\0' && boardArray[nextX][nextY] != letter)) {
                            canInsertInBord = false;
                            break;
                        }
                        nextX--;
                        nextY++;
                    }
                }else if (sens.equals(Sens.Diagonale_HautGauche_BasDroite)) {
                    int max = boardArray.length;
                    int min = word.length() - 1;
                    x = rand.nextInt(max - min) + min;
                    y = rand.nextInt(max - min + 1);
                    int nextX = x;
                    int nextY = y;
                    for (char letter : word.toCharArray()) {
                        if (nextX >= boardArray.length || nextX < 0 || nextY >= boardArray.length || nextY < 0 || (boardArray[nextX][nextY] != '\0' && boardArray[nextX][nextY] != letter)) {
                            canInsertInBord = false;
                            break;
                        }
                        nextX++;
                        nextY++;
                    }
                }
            }while (!canInsertInBord && nbFetch++ < 20);

            if (canInsertInBord && x >= 0 && y >= 0){
                if (sens.equals(Sens.Horizontal)) {
                    int nextX = x;
                    for (char letter : word.toCharArray()) {
                        boardArray[nextX][y] = letter;
                        nextX++;
                    }
                }

                if (sens.equals(Sens.Vertical)) {
                    int nextY = y;
                    for (char letter : word.toCharArray()) {
                        boardArray[x][nextY] = letter;
                        nextY++;
                    }
                }

                if (sens.equals(Sens.Diagonale_BasGauche_HautDroite)) {
                    int nextX = x;
                    int nextY = y;
                    for (char letter : word.toCharArray()) {
                        boardArray[nextX][nextY] = letter;
                        nextX--;
                        nextY++;
                    }
                }

                if (sens.equals(Sens.Diagonale_HautGauche_BasDroite)) {
                    int nextX = x;
                    int nextY = y;
                    for (char letter : word.toCharArray()) {
                        boardArray[nextX][nextY] = letter;
                        nextX++;
                        nextY++;
                    }
                }
            }

        }

        return canInsertInBord;
    }

    /**
     * Try to cross the words in the grid
     * @param word word to insert
     * @return
     */
    private boolean tryCrossWords(String word){
        char[][] boardTmp = SerializationUtils.clone(boardArray);
        boolean insertStringSuccessfully = false;

        ArrayList<int[]> possibleCrossStringPos = new ArrayList<>();
        for (int i = 0; i < boardArray.length; i++ ) {
            for (int j = 0; j < boardArray[i].length; j++) {
                if (word.indexOf(boardArray[i][j]) != -1){
                    possibleCrossStringPos.add(new int[]{i, j});
                }
            }
        }

        if (possibleCrossStringPos.size() > 0){

            for (int[] pos : possibleCrossStringPos) {

                int posX = pos[0];
                int posY = pos[1];
                int matchCharIndex = word.indexOf(boardArray[posX][posY]);

                //Horizontal
                try{
                    //insertion des lettres précédents le match
                    posX = pos[0];
                    posY = pos[1];
                    for (int charIndexBefore = matchCharIndex - 1; charIndexBefore >= 0; charIndexBefore--){
                        char letter = word.charAt(charIndexBefore);
                        posY--;
                        if (posY < boardArray.length && posY >= 0 && (boardArray[posX][posY] == '\0' || boardArray[posX][posY] == letter)) {
                            boardTmp[posX][posY] = letter;
                        }else{
                            throw new Exception();
                        }
                    }
                    //insertion des lettres après le match
                    posX = pos[0];
                    posY = pos[1];
                    for (int charIndexAfter = matchCharIndex + 1; charIndexAfter < word.length(); charIndexAfter++){
                        char letter = word.charAt(charIndexAfter);
                        posY++;
                        if (posY < boardArray.length && posY >= 0 && (boardArray[posX][posY] == '\0' || boardArray[posX][posY] == letter)) {
                            boardTmp[posX][posY] = letter;
                        }else{
                            throw new Exception();
                        }
                    }
                    insertStringSuccessfully = true;
                    break;
                }catch (Exception ignored){
                    //reset
                    boardTmp = SerializationUtils.clone(boardArray);
                }

                //Vertical
                try{
                    posX = pos[0];
                    posY = pos[1];
                    for (int charIndexBefore = matchCharIndex - 1; charIndexBefore >= 0; charIndexBefore--){
                        char letter = word.charAt(charIndexBefore);
                        posX--;
                        if (posY < boardArray.length && posY >= 0 && (boardArray[posX][posY] == '\0' || boardArray[posX][posY] == letter)) {
                            boardTmp[posX][posY] = letter;
                        }else{
                            throw new Exception();
                        }
                    }
                    posX = pos[0];
                    posY = pos[1];
                    for (int charIndexAfter = matchCharIndex + 1; charIndexAfter < word.length(); charIndexAfter++){
                        char letter = word.charAt(charIndexAfter);
                        posX++;
                        if (posY < boardArray.length && posY >= 0 && (boardArray[posX][posY] == '\0' || boardArray[posX][posY] == letter)) {
                            boardTmp[posX][posY] = letter;
                        }else{
                            throw new Exception();
                        }
                    }
                    insertStringSuccessfully = true;
                    break;
                }catch (Exception ignored){
                    //reset
                    boardTmp = SerializationUtils.clone(boardArray);
                }

                //Diag Bas gauche Haut droite
                try{
                    posX = pos[0];
                    posY = pos[1];
                    for (int charIndexBefore = matchCharIndex - 1; charIndexBefore >= 0; charIndexBefore--){
                        char letter = word.charAt(charIndexBefore);
                        posX--;
                        posY--;
                        if (posY < boardArray.length && posY >= 0 && (boardArray[posX][posY] == '\0' || boardArray[posX][posY] == letter)) {
                            boardTmp[posX][posY] = letter;
                        }else{
                            throw new Exception();
                        }
                    }
                    posX = pos[0];
                    posY = pos[1];
                    for (int charIndexAfter = matchCharIndex + 1; charIndexAfter < word.length(); charIndexAfter++){
                        char letter = word.charAt(charIndexAfter);
                        posX++;
                        posY++;
                        if (posY < boardArray.length && posY >= 0 && (boardArray[posX][posY] == '\0' || boardArray[posX][posY] == letter)) {
                            boardTmp[posX][posY] = letter;
                        }else{
                            throw new Exception();
                        }
                    }
                    insertStringSuccessfully = true;;
                    break;
                }catch (Exception ignored){
                    //reset
                    boardTmp = SerializationUtils.clone(boardArray);
                }


                //Diag Haut Gauche Bas Droite
                try{
                    posX = pos[0];
                    posY = pos[1];
                    for (int charIndexBefore = matchCharIndex - 1; charIndexBefore >= 0; charIndexBefore--){
                        char letter = word.charAt(charIndexBefore);
                        posX--;
                        posY++;
                        if (posY < boardArray.length && posY >= 0 && (boardArray[posX][posY] == '\0' || boardArray[posX][posY] == letter)) {
                            boardTmp[posX][posY] = letter;
                        }else{
                            throw new Exception();
                        }
                    }
                    posX = pos[0];
                    posY = pos[1];
                    for (int charIndexAfter = matchCharIndex + 1; charIndexAfter < word.length(); charIndexAfter++){
                        char letter = word.charAt(charIndexAfter);
                        posX++;
                        posY--;
                        if (posY < boardArray.length && posY >= 0 && (boardArray[posX][posY] == '\0' || boardArray[posX][posY] == letter)) {
                            boardTmp[posX][posY] = letter;
                        }else{
                            throw new Exception();
                        }
                    }
                    insertStringSuccessfully = true;
                    break;
                }catch (Exception ignored){
                    //reset
                    boardTmp = SerializationUtils.clone(boardArray);
                }
            }

            if (insertStringSuccessfully){
                boardArray = boardTmp;
            }
        }

        return insertStringSuccessfully;
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
