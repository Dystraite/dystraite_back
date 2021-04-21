package com.ynov.dystraite.services.maximots;

import com.ynov.dystraite.entities.Users;
import com.ynov.dystraite.entities.maximots.Grid;
import com.ynov.dystraite.entities.maximots.UserGrid;
import com.ynov.dystraite.enums.maximots.Direction;
import com.ynov.dystraite.enums.maximots.Sens;
import com.ynov.dystraite.models.maximots.EntreeVerifyResponse;
import com.ynov.dystraite.models.maximots.SortieGetGrid;
import com.ynov.dystraite.models.maximots.SortieVerifyResponse;
import com.ynov.dystraite.repositories.maximots.GridRepository;
import com.ynov.dystraite.repositories.maximots.UserGridRepository;
import com.ynov.dystraite.services.UsersService;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GridService {

    @Autowired
    GridRepository gridRepository;

    @Autowired
    UserGridRepository userGridRepository;

    @Autowired
    UsersService usersService;

    public void populate(Grid grid){
        if (grid.getLabel() != null && grid.getLabel().trim().length() > 0) {
            gridRepository.save(grid);
        }
    }

    private final Random rand = new Random();
    char[][] boardArray;

    public SortieGetGrid createBoard(Authentication authentication) throws NoSuchAlgorithmException {

        Users user = usersService.getById(authentication.getName());

        //LocalDate ageLocalDate = LocalDate.now().minus( user.getBirthdate());
        //int difficulty = ageLocalDate.getYear();

        //récupération des grilles déjà réalisées et en cours de réalisation
        Set<UserGrid> gridInProgressOrDoneList = userGridRepository.findByUserId(user.getId());

        List<UserGrid> userGridList = gridInProgressOrDoneList.stream().filter(grid1 -> !grid1.isFinish()).collect(Collectors.toList());
        if (userGridList.size() > 0){ //S'il y a des grilles en cours de réaliséation

            UserGrid userGrid = userGridList.get(0); //on récupère la première

            Optional<Grid> gridOptional = gridRepository.findById(userGrid.getGrid().getId());
            if (gridOptional.isPresent()){

                List<String> wordsFound = Arrays.asList(userGrid.getFoundWords().split(","));
                List<String> wordsToInsert = this.getWordsToInsert(gridOptional.get(), wordsFound);

                return generateBoard(wordsToInsert, gridOptional.get());
            }
        }else{
            //récupération des liste déjà réalisées
            List<Long> gridIdDone = gridInProgressOrDoneList.stream()
                    .filter(UserGrid::isFinish)
                    .map(userGrid -> userGrid.getGrid().getId())
                    .collect(Collectors.toList());

            Grid grid;
            if (gridIdDone.size() > 0) {
                grid = gridRepository.findFirstByIdNotInOrderByDifficultyAsc(gridIdDone);
            }else{
                grid = gridRepository.findFirstByOrderByDifficultyAsc();
            }

            if (grid != null) {
                return generateBoard(grid.getWords(), grid);
            }
        }

        return null;
    }

    private List<String> getWordsToInsert(Grid grid, List<String> wordsFound){
        //passage des mot en majuscule pour match avec les mots déjà trouvés
        ListIterator<String> iterator = grid.getWords().listIterator();
        while (iterator.hasNext())
        {
            iterator.set(iterator.next().toUpperCase());
        }

        //on ajoute tout les mots de la grille
        // new ArrayList permet de cloner l'objet pour ne pas l'éditer lors du removeAll
        List<String> wordsToInsert = new ArrayList<>(grid.getWords());

        //on retire les mots déjà trouvés
        wordsToInsert.removeAll(wordsFound);

        return wordsToInsert;
    }

    private SortieGetGrid generateBoard(List<String> wordList, Grid grid) throws NoSuchAlgorithmException {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");

        boardArray = new char[grid.getDifficulty()][grid.getDifficulty()];

        List<String> wordsInsertedHash = new ArrayList<>();

        for (String word : wordList) {

            if (word.length() <= boardArray.length) {
                Direction direction = Direction.Normal; //Direction.getRandom(); //normal - reverse

                String mot = word;
                /*if (direction.equals(Direction.Inverse)) {
                    mot = new StringBuilder(mot).reverse().toString();
                }*/
                boolean isWordInsertedSuccessfully = insertWord(mot.toUpperCase());

                if (isWordInsertedSuccessfully) {
                    final byte[] hashbytes = digest.digest(word.toUpperCase().getBytes(StandardCharsets.UTF_8));
                    wordsInsertedHash.add(bytesToHex(hashbytes));
                }
            }
        }

        //affichage du plateau
        System.out.println();
        for (char[] chars : boardArray) {
            for (char aChar : chars) {
                if (aChar != '\0') {
                    System.out.print(aChar + " ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }

        ArrayList<Character> board = new ArrayList<>();
        for (char[] chars : boardArray) {
            for (char aChar : chars) {
                if (aChar == '\0') {
                    board.add((char) (rand.nextInt(26) + 'a'));
                } else {
                    board.add(aChar);
                }
            }
        }
        return new SortieGetGrid(board, wordsInsertedHash, grid.getId(), grid.getLabel(), grid.getDifficulty());
    }

    private boolean insertWord(String word){

        int x = -1;
        int y = -1;
        boolean canInsertInBord = true;
        int nbFetch = 0;

        boolean isInsertWordSuccessfully = this.tryCrossWords(word);

        System.out.println(word + "tryCrossWords : " + isInsertWordSuccessfully);
        if (isInsertWordSuccessfully){
            return true;
        }else {
            Sens sens;
            do{
                sens = Sens.getRandom();
                //System.out.println(sens.toString());
                if (sens.equals(Sens.Horizontal)) {
                    x = boardArray.length - word.length();
                    if (x != 0 ) x = rand.nextInt(y);
                    y = rand.nextInt(boardArray.length);
                    int nextX = x;
                    int nextY = y;
                    System.out.println(nextX + " : " + nextY);
                    for (char letter : word.toCharArray()) {
                        if (nextX >= boardArray.length || nextX < 0 || (boardArray[nextX][nextY] != '\0' && boardArray[nextX][nextY] != letter)) {
                            canInsertInBord = false;
                            break;
                        }
                        nextX++;
                    }
                }else if (sens.equals(Sens.Vertical)) {
                    x = rand.nextInt(boardArray.length);
                    y = boardArray.length - word.length();
                    if (y != 0 ) y = rand.nextInt(y);
                    int nextX = x;
                    int nextY = y;
                    //System.out.println(nextX + " : " + nextY);
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

            //System.out.println(canInsertInBord + " " + nbFetch);

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

    public SortieVerifyResponse verifyResponse(EntreeVerifyResponse entree, Authentication authentication) throws NoSuchAlgorithmException {
        boolean isFinish = false;
        UserGrid userGrid = null;
        SortieGetGrid sortieGetGrid = null;

        Users user = usersService.getById(authentication.getName());
        //récupération de l'avancement du joueur et de la grille
        Optional<UserGrid> userGridOpt = userGridRepository.findByUserIdAndGridId(user.getId(), entree.gridId);
        Optional<Grid> grid = gridRepository.findById(entree.gridId);

        if (grid.isPresent()){

            //si mots déjà trouvé : récupération, si non : création
            userGrid = userGridOpt.orElseGet(() -> new UserGrid(user, grid.get()));

            List<String> wordsFound = entree.words;
            if (userGrid.getFoundWords() != null){
                wordsFound.addAll(new ArrayList<>(Arrays.asList(userGrid.getFoundWords().split(","))));
            }
            List<String> wordsToInsert = getWordsToInsert(grid.get(), wordsFound);
            if (wordsToInsert.size() == 0) { //win
                isFinish = true;
            } else {
                //try create new board
                sortieGetGrid = generateBoard(wordsToInsert, grid.get());
                if (sortieGetGrid.getWordsHash().size() < 2) { //si moins de 1 mots dans la grille : win
                    isFinish = true;
                } else {
                    userGrid.setFoundWords(String.join(",", wordsFound));
                    isFinish = false;
                }
            }
        }

        if (userGrid != null) {
            if (isFinish) {
                userGrid.setFinish(true);
                userGridRepository.save(userGrid);
                return new SortieVerifyResponse(null, true);
            } else {
                userGrid.setFinish(false);
                userGridRepository.save(userGrid);
            }
        }
        return new SortieVerifyResponse(sortieGetGrid, false);
    }
}
