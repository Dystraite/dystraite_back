package com.ynov.dystraite.services.maximots;

import com.ynov.dystraite.entities.Users;
import com.ynov.dystraite.entities.maximots.Category;
import com.ynov.dystraite.entities.maximots.Theme;
import com.ynov.dystraite.entities.maximots.Word;
import com.ynov.dystraite.enums.maximots.Direction;
import com.ynov.dystraite.enums.maximots.Sens;
import com.ynov.dystraite.models.maximots.SortieGameplay;
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

@Service
public class GameplayService {

    @Autowired
    ThemeService themeService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    UsersService usersService;

    private final Random rand = new Random();

    char[][] board;

    public SortieGameplay createBoard(List<Long> categoriesId, int boardSize, Authentication authentication) throws NoSuchAlgorithmException {

        Users user = usersService.getById(authentication.getName());

        List<String> wordsInsertedHash = new ArrayList<>();
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");

        List<Category> categories = categoryService.getByIdIn(categoriesId);
        List<Theme> themeList = themeService.getByCategories(categories);
        //choisi un theme dans la liste de themes envoyés
        Theme theme = themeList.get(rand.nextInt(themeList.size()));
        List<Word> wordList = theme.getWords();

        this.board = new char[boardSize][boardSize];
        
        for (Word word : wordList) {

            if (word.getWord().length() < boardSize)
            {
                Direction direction = Direction.getRandom(); //normal - reverse
                Sens sens = Sens.getRandom(); //horiz - vert - diag_b_d - diag_h_d

                String mot = word.getWord().toLowerCase();
                if (direction.equals(Direction.Inverse)){
                    mot = new StringBuilder(mot).reverse().toString();
                }

                boolean isInserted = insertWord(sens, mot);
                if (isInserted){
                    final byte[] hashbytes = digest.digest(mot.getBytes(StandardCharsets.UTF_8));
                    wordsInsertedHash.add(bytesToHex(hashbytes));
                }
            }
        }

        ArrayList<Character> finalBoard = new ArrayList<>();
        for (int i = 0; i < this.board.length; i++)
        {
            for (int j = 0; j < this.board[i].length; j++)
            {
                if (this.board[i][j] == '\0') {
                    finalBoard.add( (char) (rand.nextInt(26) + 'a') );
                }else {
                    finalBoard.add(this.board[i][j]);
                }
            }
        }

        //affichage du plateau
        System.out.println();
        for (char[] chars : this.board)
        {
            for (char c : chars)
            {
                if (c != '\0') {
                    System.out.print(c + " ");
                }else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
        System.out.println();

        return new SortieGameplay(finalBoard, wordsInsertedHash);
    }

    private boolean insertWord(Sens sens, String word){

        int x = -1;
        int y = -1;
        boolean canInsertInBord = true;
        int nbFetch = 0;

        if (this.tryCrossWords(word)){
            return true;
        }else {
            do{
                if (sens.equals(Sens.Horizontal)) {
                    x = rand.nextInt(this.board.length - word.length());
                    y = rand.nextInt(this.board.length);
                    int nextX = x;
                    int nextY = y;
                    for (char letter : word.toCharArray()) {
                        if (nextX >= this.board.length || nextX < 0 || (this.board[nextX][nextY] != '\0' && this.board[nextX][nextY] != letter)) {
                            canInsertInBord = false;
                            break;
                        }
                        nextX++;
                    }
                }else if (sens.equals(Sens.Vertical)) {
                    x = rand.nextInt(this.board.length);
                    y = rand.nextInt(this.board.length - word.length());
                    int nextX = x;
                    int nextY = y;
                    for (char letter : word.toCharArray()) {
                        if (nextY >= this.board.length || nextY < 0 || (this.board[nextX][nextY] != '\0' && this.board[nextX][nextY] != letter)) {
                            canInsertInBord = false;
                            break;
                        }
                        nextY++;
                    }
                } else if (sens.equals(Sens.Diagonale_BasGauche_HautDroite)) { //posX--; posY--;
                    int max = this.board.length;
                    int min = word.length() - 1;
                    x = rand.nextInt(max - min) + min;
                    y = rand.nextInt(max - min + 1);
                    int nextX = x;
                    int nextY = y;
                    for (char letter : word.toCharArray()) {
                        if (nextX >= this.board.length || nextX < 0 || nextY >= this.board.length || nextY < 0 || (this.board[nextX][nextY] != '\0' && this.board[nextX][nextY] != letter)) {
                            canInsertInBord = false;
                            break;
                        }
                        nextX--;
                        nextY++;
                    }
                }else if (sens.equals(Sens.Diagonale_HautGauche_BasDroite)) {
                    int max = this.board.length;
                    int min = word.length() - 1;
                    x = rand.nextInt(max - min) + min;
                    y = rand.nextInt(max - min + 1);
                    int nextX = x;
                    int nextY = y;
                    for (char letter : word.toCharArray()) {
                        if (nextX >= this.board.length || nextX < 0 || nextY >= this.board.length || nextY < 0 || (this.board[nextX][nextY] != '\0' && this.board[nextX][nextY] != letter)) {
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
                        this.board[nextX][y] = letter;
                        nextX++;
                    }
                }

                if (sens.equals(Sens.Vertical)) {
                    int nextY = y;
                    for (char letter : word.toCharArray()) {
                        this.board[x][nextY] = letter;
                        nextY++;
                    }
                }

                if (sens.equals(Sens.Diagonale_BasGauche_HautDroite)) {
                    int nextX = x;
                    int nextY = y;
                    for (char letter : word.toCharArray()) {
                        this.board[nextX][nextY] = letter;
                        nextX--;
                        nextY++;
                    }
                }

                if (sens.equals(Sens.Diagonale_HautGauche_BasDroite)) {
                    int nextX = x;
                    int nextY = y;
                    for (char letter : word.toCharArray()) {
                        this.board[nextX][nextY] = letter;
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
     */
    private boolean tryCrossWords(String word){
        char[][] boardTmp = SerializationUtils.clone(this.board);
        boolean insertWordSuccessfully = false;

        ArrayList<int[]> possibleCrossWordPos = new ArrayList<>();
        for (int i = 0; i < this.board.length; i++ ) {
            for (int j = 0; j < this.board[i].length; j++) {
                if (word.indexOf(this.board[i][j]) != -1){
                    possibleCrossWordPos.add(new int[]{i, j});
                }
            }
        }

        if (possibleCrossWordPos.size() > 0){

            for (int[] pos : possibleCrossWordPos) {

                int posX = pos[0];
                int posY = pos[1];
                int matchCharIndex = word.indexOf(this.board[posX][posY]);

                //Horizontal
                try{
                    posX = pos[0];
                    posY = pos[1];
                    for (int charIndexBefore = matchCharIndex - 1; charIndexBefore >= 0; charIndexBefore--){
                        char letter = word.charAt(charIndexBefore);
                        posY--;
                        if (posY < this.board.length && posY >= 0 && (this.board[posX][posY] == '\0' || this.board[posX][posY] == letter)) {
                            boardTmp[posX][posY] = letter;
                        }else{
                            throw new Exception();
                        }
                    }
                    posX = pos[0];
                    posY = pos[1];
                    for (int charIndexAfter = matchCharIndex + 1; charIndexAfter < word.length(); charIndexAfter++){
                        char letter = word.charAt(charIndexAfter);
                        posY++;
                        if (posY < this.board.length && posY >= 0 && (this.board[posX][posY] == '\0' || this.board[posX][posY] == letter)) {
                            boardTmp[posX][posY] = letter;
                        }else{
                            throw new Exception();
                        }
                    }
                    insertWordSuccessfully = true;
                    break;
                }catch (Exception ignored){
                    //reset
                    boardTmp = SerializationUtils.clone(this.board);
                }

                //Vertical
                try{
                    posX = pos[0];
                    posY = pos[1];
                    for (int charIndexBefore = matchCharIndex - 1; charIndexBefore >= 0; charIndexBefore--){
                        char letter = word.charAt(charIndexBefore);
                        posX--;
                        if (posY < this.board.length && posY >= 0 && (this.board[posX][posY] == '\0' || this.board[posX][posY] == letter)) {
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
                        if (posY < this.board.length && posY >= 0 && (this.board[posX][posY] == '\0' || this.board[posX][posY] == letter)) {
                            boardTmp[posX][posY] = letter;
                        }else{
                            throw new Exception();
                        }
                    }
                    insertWordSuccessfully = true;
                    break;
                }catch (Exception ignored){
                    //reset
                    boardTmp = SerializationUtils.clone(this.board);
                }

                //Diag Bas gauche Haut droite
                try{
                    posX = pos[0];
                    posY = pos[1];
                    for (int charIndexBefore = matchCharIndex - 1; charIndexBefore >= 0; charIndexBefore--){
                        char letter = word.charAt(charIndexBefore);
                        posX--;
                        posY--;
                        if (posY < this.board.length && posY >= 0 && (this.board[posX][posY] == '\0' || this.board[posX][posY] == letter)) {
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
                        if (posY < this.board.length && posY >= 0 && (this.board[posX][posY] == '\0' || this.board[posX][posY] == letter)) {
                            boardTmp[posX][posY] = letter;
                        }else{
                            throw new Exception();
                        }
                    }
                    insertWordSuccessfully = true;;
                    break;
                }catch (Exception ignored){
                    //reset
                    boardTmp = SerializationUtils.clone(this.board);
                }


                //Diag Haut Gauche Bas Droite
                try{
                    posX = pos[0];
                    posY = pos[1];
                    for (int charIndexBefore = matchCharIndex - 1; charIndexBefore >= 0; charIndexBefore--){
                        char letter = word.charAt(charIndexBefore);
                        posX--;
                        posY++;
                        if (posY < this.board.length && posY >= 0 && (this.board[posX][posY] == '\0' || this.board[posX][posY] == letter)) {
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
                        if (posY < this.board.length && posY >= 0 && (this.board[posX][posY] == '\0' || this.board[posX][posY] == letter)) {
                            boardTmp[posX][posY] = letter;
                        }else{
                            throw new Exception();
                        }
                    }
                    insertWordSuccessfully = true;;
                    break;
                }catch (Exception ignored){
                    //reset
                    boardTmp = SerializationUtils.clone(this.board);
                }
            }

            if (insertWordSuccessfully){
                this.board = boardTmp;
            }
        }

        return insertWordSuccessfully;
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
